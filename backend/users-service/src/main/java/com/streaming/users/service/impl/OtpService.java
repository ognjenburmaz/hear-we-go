package com.streaming.users.service.impl;

import com.streaming.users.model.User;
import com.streaming.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
public class OtpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OtpService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateOtp(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));

        user.setOneTimePasswordHash(passwordEncoder.encode(otp));
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        return otp;
    }

    public void validateOtp(String username, String rawOtp) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (user.getOneTimePasswordHash() == null || user.getOtpExpiresAt() == null) {
            throw new BadCredentialsException("OTP not requested");
        }

        if (user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            clearOtp(user);
            throw new BadCredentialsException("OTP expired");
        }

        if (!passwordEncoder.matches(rawOtp, user.getOneTimePasswordHash())) {
            throw new BadCredentialsException("Invalid OTP");
        }

        clearOtp(user);
    }

    private void clearOtp(User user) {
        user.setOneTimePasswordHash(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
    }
}
