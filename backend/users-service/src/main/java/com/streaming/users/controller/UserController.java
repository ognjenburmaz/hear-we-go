package com.streaming.users.controller;

import com.streaming.users.dto.*;
import com.streaming.users.model.User;
import com.streaming.users.security.OtpAuthenticationToken;
import com.streaming.users.security.TokenUtils;
import com.streaming.users.service.impl.OtpService;
import com.streaming.users.service.impl.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OtpService otpService;
    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        return ResponseEntity.ok(userServiceImpl.registerUser(request));
    }

    @PostMapping("/login/psw")
    public ResponseEntity<?> pswlogin(@RequestBody AuthRequest authRequest) {
        // TODO nek ovde vraca neki UserDTO (ili u login/otp?)

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        User user = userServiceImpl.getUserEntity(authRequest.getUsername());

        if (user.getLastPasswordReset().plusDays(60).isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "PASSWORD_TOO_OLD",
                            "message", "The password is older than 60 days"
                    ));
        }

        String otp = otpService.generateOtp(authRequest.getUsername());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Vas jednokratni kod");
        message.setText("Vas kod je: " + otp + "\nKod istice za 5 minuta.");

        try {
            mailSender.send(message);
            System.out.println("Poslat MEJL!");
        } catch (Exception ex) {
            System.err.println("Greška pri slanju mejla: " + ex.getMessage());
        }

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(user.getEmail());

        return ResponseEntity.ok(emailDTO);
    }

    @PostMapping("/login/otp")
    public ResponseEntity<TokenUtils.JwtDTO> login(@RequestBody AuthRequest authRequest) {
        // TODO nek ovde vraca neki UserDTO (ili u login/psw?)

        Authentication authentication =
                authenticationManager.authenticate(
                        new OtpAuthenticationToken(
                                authRequest.getUsername(),
                                authRequest.getPassword()
                        )
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userServiceImpl.getUserEntity(authRequest.getUsername());

        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(new TokenUtils.JwtDTO(jwt, tokenUtils.getExpiredIn()));
    }

    @PostMapping("/recovery")
    public ResponseEntity<?> sendRecoveryMail(@RequestBody EmailRequest emailRequest) throws MessagingException {
        String email = emailRequest.getEmail();
        System.out.println("email --------------------------------: " + email);
        User user = null;
        if (userServiceImpl.findByEmail(email).isPresent()) {
            user = userServiceImpl.findByEmail(email).get();
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "EMAIL_NOT_FOUND",
                            "message", "User with this email does not exist"
                    ));
        }
        if (user.getLastPasswordReset().plusDays(1).isAfter(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "RESET_TOO_SOON",
                            "message", "Password reset already requested recently"
                    ));
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        user.setRecoveryHash(UUID.randomUUID());
        userServiceImpl.save(user);

        String magicLink = "https://localhost/users/changepassword?recoveryHash=" + user.getRecoveryHash();

        helper.setTo(user.getEmail());
        helper.setSubject("Resetovanje lozinke");
        helper.setText("<html>\n" +
                "    <h1>Vas kod za resetovanje lozinke je: </h1>\n" +
                "    <br>\n" +
                "    <a href=\"" + magicLink + "\">" + magicLink + "</a>\n" +
                "<br><h2>Ne delite ovaj link ni sa kim!</h2>" +
                "</html>", true);

        try {
            mailSender.send(message);
            System.out.println("Poslat HTML MEJL!");
        } catch (Exception ex) {
            System.err.println("Greška pri slanju HTML mejla: " + ex.getMessage());
        }


        return ResponseEntity.ok(null);
    }

    @PatchMapping("/pswchange")
    public ResponseEntity<?> changePassword(@RequestBody PswChangeRequest request) {
        UUID recoveryHash = request.getRecoveryHash();
        String newPassword = request.getNewPassword();

        Optional<User> userOptional = userServiceImpl.findByRecoveryHash(recoveryHash);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setRecoveryHash(null);
            user.setLastPasswordReset(LocalDateTime.now());
            userServiceImpl.save(user);
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userServiceImpl.findAll());
    }

}