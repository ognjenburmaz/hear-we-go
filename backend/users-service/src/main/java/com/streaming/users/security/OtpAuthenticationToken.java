package com.streaming.users.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class OtpAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String otp;

    public OtpAuthenticationToken(Object principal, String otp) {
        super(null);
        this.principal = principal;
        this.otp = otp;
        setAuthenticated(false);
    }

    public OtpAuthenticationToken(Object principal,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.otp = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return otp;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

