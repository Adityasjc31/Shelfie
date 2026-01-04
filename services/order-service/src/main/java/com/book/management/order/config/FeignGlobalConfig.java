package com.book.management.order.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignGlobalConfig {

    private final GatewaySecurityProperties securityProperties;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    @Bean
    public RequestInterceptor gatewaySecretRequestInterceptor() {
        return new GatewaySecretRequestInterceptor(securityProperties);
    }
}
