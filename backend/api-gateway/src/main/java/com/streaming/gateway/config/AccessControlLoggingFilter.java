package com.streaming.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class AccessControlLoggingFilter {

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpStatus status = (HttpStatus) exchange.getResponse().getStatusCode();

                if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
                    String path = exchange.getRequest().getPath().toString();
                    String method = exchange.getRequest().getMethod().name();
                    String ip = "";
                    if (exchange.getRequest().getRemoteAddress() != null)
                        ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

                    log.warn("ACCESS FAILURE: [{}] {} returned {} for IP: {}",
                            method, path, status.value(), ip);
                }
            }));
        };
    }
}
