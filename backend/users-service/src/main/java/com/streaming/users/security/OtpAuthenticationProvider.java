package com.streaming.users.security;

import com.streaming.users.service.impl.OtpService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final OtpService otpService;
    private final UserDetailsService userDetailsService;

    public OtpAuthenticationProvider(OtpService otpService,
                                     UserDetailsService userDetailsService) {
        this.otpService = otpService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getPrincipal().toString();
        String otp = authentication.getCredentials().toString();

        otpService.validateOtp(username, otp);

        UserDetails user = userDetailsService.loadUserByUsername(username);

        return new OtpAuthenticationToken(
                user.getUsername(),
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
