package com.streaming.users.controller;

import com.streaming.users.dto.AuthRequest;
import com.streaming.users.dto.UserRegistrationRequest;
import com.streaming.users.dto.UserRegistrationResponse;
import com.streaming.users.model.User;
import com.streaming.users.security.OtpAuthenticationToken;
import com.streaming.users.security.TokenUtils;
import com.streaming.users.service.impl.OtpService;
import com.streaming.users.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

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


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        User user = userServiceImpl.getUserEntity(authRequest.getUsername());
        String otp = otpService.generateOtp(authRequest.getUsername());

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Vas jednokratni kod");
        msg.setText("Vas kod je: " + otp + " \n Kod istice za 5 minuta.");

        try {
            mailSender.send(msg);
            System.out.println("Poslat MEJL!");
        } catch (Exception ex) {
            System.err.println("Greška pri slanju mejla: " + ex.getMessage());
        }

        return ResponseEntity.ok(null);
    }

    @PostMapping("/login/otp")
    public ResponseEntity<TokenUtils.JwtDTO> login(@RequestBody AuthRequest authRequest) {

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

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userServiceImpl.findAll());
    }

}