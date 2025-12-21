package com.streaming.users.controller;

import com.streaming.users.dto.AuthRequest;
import com.streaming.users.dto.UserRegistrationRequest;
import com.streaming.users.dto.UserRegistrationResponse;
import com.streaming.users.model.User;
import com.streaming.users.security.TokenUtils;
import com.streaming.users.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        return ResponseEntity.ok(userServiceImpl.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenUtils.JwtDTO> login(@RequestBody AuthRequest authRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        User user = userServiceImpl.getUserEntity(authRequest.getUsername());

        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(new TokenUtils.JwtDTO(jwt, tokenUtils.getExpiredIn()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userServiceImpl.findAll());
    }

}