package com.streaming.gateway.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("https://localhost", "https://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeExchange(exchanges -> exchanges
                        // 1. Public / WebSocket
                        .pathMatchers("/api/ws/**", "/ws/**", "/api/ws/info/**", "/ws/info/**").permitAll()
                        .pathMatchers("/api/users/login/**", "/api/users/register/**").permitAll()
                        .pathMatchers("/api/users/recovery", "/api/users/pswchange").permitAll()
                        .pathMatchers("/actuator/**").permitAll()

                        // 2. RBAC (The Gateway door)
                        .pathMatchers(HttpMethod.POST, "/api/content/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/content/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/content/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/content/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/notifications/test").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // LOGS 403: User is logged in but lacks 'ADMIN' role
                        .accessDeniedHandler((exchange, denied) -> {
                            logSecurityFailure(exchange, "FORBIDDEN", denied.getMessage());
                            return Mono.error(denied);
                        })
                        // LOGS 401: Token is missing, expired, or invalid
                        .authenticationEntryPoint((exchange, authEx) -> {
                            logSecurityFailure(exchange, "UNAUTHORIZED", authEx.getMessage());
                            return Mono.error(authEx);
                        })
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    private void logSecurityFailure(org.springframework.web.server.ServerWebExchange exchange, String type, String reason) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        String ip = "unknown";
        if (exchange.getRequest().getRemoteAddress() != null) {
            ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        log.warn("ACCESS FAILURE: [{}] {} - Type: {}, Reason: {}, IP: {}", method, path, type, reason, ip);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("notification-websocket", r -> r.path("/ws/**")

                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri("lb://NOTIFICATION-SERVICE"))
                .route("analytics-service", r -> r.path("/api/analytics/**")
                        .uri("lb://ANALYTICS-SERVICE"))
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public GlobalFilter addUserIdHeaderFilter() {
        return new GlobalFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .cast(org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken.class)
                        .map(jwtAuthToken -> {
                            Jwt jwt = jwtAuthToken.getToken();
                            String userId = jwt.getSubject();
                            return exchange.mutate()
                                    .request(exchange.getRequest().mutate()
                                            .header("X-User-Id", userId)
                                            .build())
                                    .build();
                        })
                        .defaultIfEmpty(exchange)
                        .flatMap(chain::filter);
            }
        };
    }
}