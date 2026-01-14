package com.streaming.gateway.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // OMOGUĆEN CORS na nivou Spring Security-a
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("https://localhost", "https://localhost:4200")); // Dodaj port ako Angular trči na drugom
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeExchange(exchanges -> exchanges
                        // 1. WebSocket putanje
                        .pathMatchers("/api/ws/**", "/ws/**").permitAll()
                        .pathMatchers("/api/ws/info/**", "/ws/info/**").permitAll()
                        .pathMatchers("/api/users/login/**", "/api/users/register/**").permitAll()

                        // 2. Auth putanje
                        .pathMatchers("/api/users/login/*", "/api/users/register", "/api/users/recovery", "/api/users/pswchange").permitAll()
                        .pathMatchers("/actuator/**").permitAll()

                        // 3. RBAC
                        .pathMatchers(HttpMethod.POST, "/api/content/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/content/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/content/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/content/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/notifications/test").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("notification-websocket", r -> r.path("/ws/**")

                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri("lb://NOTIFICATION-SERVICE"))
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
                        .map(securityContext -> securityContext.getAuthentication())
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