package com.streaming.gateway.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // 1. OPEN ACCESS: Login, Register, Swagger UI
                        .pathMatchers("/api/users/login", "/api/users/register").permitAll()

                        // 2. PROTECTED: Everything else (Music, Ratings, etc.)
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                );
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Use the SAME secret key as User Service
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("21377d21d9ad8aec91f1f08f03abf8a37ebeced0590fd6db1072ffeb227cfc008dafc1d462c15869705c0c2c6c8372a6fdf31a5fea105120755e9a98aaee49f9"));
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}