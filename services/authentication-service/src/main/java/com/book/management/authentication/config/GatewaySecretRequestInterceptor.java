package com.book.management.authentication.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign Request Interceptor that injects the Gateway Secret header
 * into all outgoing Feign requests for inter-service communication.
 * 
 * This allows authentication-service to call user-service while maintaining
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
            template.header(
                    securityProperties.getHeaderName(),
                    securityProperties.getExpectedToken());
            log.debug("Injected gateway secret header for inter-service call to: {}",
                    template.feignTarget().name());
        }
    }
}
