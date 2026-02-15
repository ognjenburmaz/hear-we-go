package com.streaming.users.controller;

import com.streaming.users.dto.*;
import com.streaming.users.model.RegistrationStatus;
import com.streaming.users.model.User;
import com.streaming.users.security.OtpAuthenticationToken;
import com.streaming.users.security.TokenUtils;
import com.streaming.users.service.impl.OtpService;
import com.streaming.users.service.impl.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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

//    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        return ResponseEntity.ok(userServiceImpl.registerUser(request));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<User>> getPendingRegistrations() {
        List<User> pendingRegistrations = new ArrayList<>();
        for (User user : userServiceImpl.findAll()) {
            if (user.getRegistrationStatus().equals(RegistrationStatus.PENDING)) {
                pendingRegistrations.add(user);
            }
        }
        return ResponseEntity.ok(pendingRegistrations);
    }

    @PatchMapping("/requests/accept/{email}")
    public ResponseEntity<User> acceptRegistration(@PathVariable String email) {
        Optional<User> optionalUser = userServiceImpl.findByEmail(email);
        User user = optionalUser.get();
        user.setRegistrationStatus(RegistrationStatus.APPROVED);
        userServiceImpl.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Vas zahtev za registraciju je prihvacen!");
        message.setText("Vas zahtev je prihvacen, datum i vreme kreiranje naloga: " + LocalDateTime.now());

        try {
            mailSender.send(message);
            System.out.println("Poslat MEJL!");
        } catch (Exception ex) {
            System.err.println("Greška pri slanju mejla: " + ex.getMessage());
        }

        log.info("Account registration request accepted for {}", email);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/requests/reject/{email}")
    public ResponseEntity<User> denyRegistration(@PathVariable String email) {
        Optional<User> optionalUser = userServiceImpl.findByEmail(email);
        User user = optionalUser.get();
        user.setRegistrationStatus(RegistrationStatus.DENIED);
        userServiceImpl.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Vas zahtev za registraciju je odbijen!");
        message.setText("Vas zahtev za registraciju je nazalost odbijen :(");

        try {
            mailSender.send(message);
            System.out.println("Poslat MEJL!");
        } catch (Exception ex) {
            System.err.println("Greška pri slanju mejla: " + ex.getMessage());
        }

        log.info("Account registration request rejected for {}", email);
        return ResponseEntity.ok(user);
    }


    @PostMapping("/login/psw")
    public ResponseEntity<?> pswlogin(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        // TODO nek ovde vraca neki UserDTO (ili u login/otp?)

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            Optional<User> atteptedLogin = userServiceImpl.findByUsername(authRequest.getUsername());
            if (atteptedLogin.isPresent()) {
                User atteptedLoginUser = atteptedLogin.get();
                atteptedLoginUser.setFailedLoginAttempts(atteptedLoginUser.getFailedLoginAttempts() + 1);
                if (atteptedLoginUser.getFailedLoginAttempts() > 3) {
                    atteptedLoginUser.setRegistrationStatus(RegistrationStatus.LOCKED);
                }
                userServiceImpl.save(atteptedLoginUser);

            }
            log.warn("Login failed: username={}, ip={}, reason={}", authRequest.getUsername(), request.getRemoteAddr(), "WRONG_CREDENTIALS");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "code", "WRONG_CREDENTIALS",
                            "message", "Incorrect username or password!"
                    ));
        }


        User user = userServiceImpl.getUserEntity(authRequest.getUsername());


        if (user.getRegistrationStatus().equals(RegistrationStatus.LOCKED)) {
            log.warn("Login failed: username={}, ip={}, reason={}", authRequest.getUsername(), request.getRemoteAddr(), "LOCKED_ACCOUNT");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "LOCKED_ACCOUNT",
                            "message", "Your account has been locked due to too many failed login attempts!"
                    ));
        }

        if (user.getLastPasswordReset().plusDays(60).isBefore(LocalDateTime.now())) {
            log.warn("Login failed: username={}, ip={}, reason={}", authRequest.getUsername(), request.getRemoteAddr(), "PASSWORD_TOO_OLD");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "PASSWORD_TOO_OLD",
                            "message", "The password is older than 60 days"
                    ));
        }

        if (user.getRegistrationStatus().equals(RegistrationStatus.PENDING)) {
            log.warn("Login failed: username={}, ip={}, reason={}", authRequest.getUsername(), request.getRemoteAddr(), "PENDING_REGISTRATION");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "PENDING_REGISTRATION",
                            "message", "Wait for the admin to approve your registration"
                    ));
        }

        if (user.getRegistrationStatus().equals(RegistrationStatus.DENIED)) {
            log.warn("Login failed: username={}, ip={}, reason={}", authRequest.getUsername(), request.getRemoteAddr(), "DENIED_REGISTRATION");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", "DENIED_REGISTRATION",
                            "message", "Your registration has been denied!"
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

        log.info(
                "Password login successful: userId={}, ip={}",
                user.getId(),
                request.getRemoteAddr()
        );

        return ResponseEntity.ok(emailDTO);
    }

    @PostMapping("/login/otp")
    public ResponseEntity<TokenUtils.JwtDTO> login(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
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

        log.info(
                "OTP login successful: userId={}, ip={}",
                user.getId(),
                request.getRemoteAddr()
        );
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
            user.setFailedLoginAttempts(0);
            user.setRegistrationStatus(RegistrationStatus.APPROVED);
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