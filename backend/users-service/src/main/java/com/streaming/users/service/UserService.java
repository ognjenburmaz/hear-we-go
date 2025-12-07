package com.streaming.users.service;

import com.streaming.common.dto.UserRegistrationRequest;
import com.streaming.users.model.User;
import com.streaming.users.repository.UserRepository;
import com.streaming.users.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public User registerUser(UserRegistrationRequest request) {
        // TODO: Check if user exists...

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // HASH THE PASSWORD!
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setRole("RK");
        user.setActive(true); // Temporary true for testing
        user.setLastPasswordReset(LocalDateTime.now());

        return userRepository.save(user);
    }

    public String login(String username, String password) {
        // This attempts to authenticate against the DB using BCrypt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // If we get here, password is correct. Fetch role and generate token.
        User user = userRepository.findByUsername(username).orElseThrow();
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}