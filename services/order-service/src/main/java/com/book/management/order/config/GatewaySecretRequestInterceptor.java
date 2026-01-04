package com.book.management.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign Request Interceptor that injects the Gateway Secret header
 * into all outgoing Feign requests for inter-service communication.
 * 
 * This allows services to authenticate with each other while maintaining
 * the gateway security validation that blocks direct external access.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewaySecretRequestInterceptor implements RequestInterceptor {

    private final GatewaySecurityProperties securityProperties;

    @Override
    public void apply(RequestTemplate template) {
        if (securityProperties.isEnabled() && securityProperties.getExpectedToken() != null) {
            String token = securityProperties.getExpectedToken();
            template.header(
                    securityProperties.getHeaderName(),
                    token);
            // Debug logging - show first 8 chars of token for troubleshooting
            String maskedToken = token.length() > 8 ? token.substring(0, 8) + "..." : token;
            log.info("Inter-service call to: {} | Header: {} | Token prefix: {}",
                    template.feignTarget().name(),
                        securityProperties.getHeaderName(),
                    maskedToken);
        } else {
            log.warn(
                    "Gateway security disabled or no token configured - NOT injecting header for inter-service call to: {}",
                    template.feignTarget().name());
        }
    }
}
