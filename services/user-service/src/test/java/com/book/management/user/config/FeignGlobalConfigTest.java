// ============================================================================
// FILE: FeignGlobalConfigTest.java
// ============================================================================
package com.book.management.user.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for FeignGlobalConfig.
 * Tests that the configuration properly creates Feign beans.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FeignGlobalConfig Unit Tests")
class FeignGlobalConfigTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    private FeignGlobalConfig feignGlobalConfig;

    @BeforeEach
    void setUp() {
        feignGlobalConfig = new FeignGlobalConfig(securityProperties);
    }

    @Test
    @DisplayName("Should create ErrorDecoder bean")
    void testErrorDecoderBean() {
        // When
        ErrorDecoder errorDecoder = feignGlobalConfig.errorDecoder();

        // Then
        assertThat(errorDecoder).isNotNull();
        assertThat(errorDecoder).isInstanceOf(CustomFeignErrorDecoder.class);
    }

    @Test
    @DisplayName("Should create GatewaySecretRequestInterceptor bean")
    void testGatewaySecretRequestInterceptorBean() {
        // When
        RequestInterceptor interceptor = feignGlobalConfig.gatewaySecretRequestInterceptor();

        // Then
        assertThat(interceptor).isNotNull();
        assertThat(interceptor).isInstanceOf(GatewaySecretRequestInterceptor.class);
    }
}
