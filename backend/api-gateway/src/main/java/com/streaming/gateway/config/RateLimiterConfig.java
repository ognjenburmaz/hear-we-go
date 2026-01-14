package com.streaming.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .defaultIfEmpty("anonymous")
                .flatMap(name -> {
                    if ("anonymous".equals(name)) {
                        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
                        return Mono.just(remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown");
                    }
                    return Mono.just(name);
                });
    }
}