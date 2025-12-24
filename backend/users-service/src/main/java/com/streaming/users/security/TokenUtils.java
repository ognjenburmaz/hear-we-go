package com.streaming.users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("3600000")
    private Long expiration;

    public String getEmailFromToken(String token) {
        String email;
        try {
            Claims claims = this.getClaimsFromToken(token);
            email = claims.getSubject();
        } catch (Exception e) {
            email = null;
        }
        return email;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(this.secretKey)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expirationDate;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            expirationDate = claims.getExpiration();
        } catch (Exception e) {
            expirationDate = null;
        }
        return expirationDate;
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate.before(new Date(System.currentTimeMillis()));
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("role", role);
        claims.put("created", new Date(System.currentTimeMillis()));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public int getExpiredIn() {
        return expiration.intValue();
    }

    @AllArgsConstructor
    public static class JwtDTO {
        public String jwt;
        public int expiresIn;
    }
}
