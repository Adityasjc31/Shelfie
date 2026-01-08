package com.book.management.user.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global Feign configuration for User Service.
 * 
 * Configures:
 * - Custom error decoder for downstream service errors
 * - Request interceptor for inter-service authentication (Gateway Secret)
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@Configuration
@RequiredArgsConstructor
public class FeignGlobalConfig {

    private final GatewaySecurityProperties securityProperties;

    /**
     * Custom error decoder to parse downstream service errors.
     * 
     * @return CustomFeignErrorDecoder instance
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    /**
     * Request interceptor that injects the Gateway Secret header
     * into all outgoing Feign requests for inter-service communication.
     * 
     * @return GatewaySecretRequestInterceptor instance
     */
    @Bean
    public RequestInterceptor gatewaySecretRequestInterceptor() {
        return new GatewaySecretRequestInterceptor(securityProperties);
    }
}
