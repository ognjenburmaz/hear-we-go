package com.streaming.gateway.config;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class TlsLoggingConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addContextValves(new TlsAuditValve());
    }

    static class TlsAuditValve extends ValveBase {
        @Override
        public void invoke(Request request, Response response) throws IOException, ServletException {
            // In Tomcat, if TLS fails, the request attribute "javax.servlet.request.ssl_session_id"
            // will be missing or an error attribute will be set.

            Object sslError = request.getAttribute("javax.servlet.error.exception");

            if (sslError != null) {
                System.err.println("TLS_PROTOCOL_ERROR!!!!!!!!!!!!!!!!!!!!!!!!");
                log.warn("TLS_PROTOCOL_ERROR: IP: {} | Reason: {}",
                        request.getRemoteAddr(), sslError.toString());
            }

            getNext().invoke(request, response);
        }
    }
}
