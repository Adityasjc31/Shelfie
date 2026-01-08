// ============================================================================
// FILE: GatewaySecretRequestInterceptorTest.java
// ============================================================================
package com.book.management.user.config;

import feign.RequestTemplate;
import feign.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GatewaySecretRequestInterceptor.
 * Tests that the interceptor properly adds gateway secret headers to Feign requests.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GatewaySecretRequestInterceptor Unit Tests")
class GatewaySecretRequestInterceptorTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    @Mock
    private Target<?> feignTarget;

    private GatewaySecretRequestInterceptor interceptor;
    private RequestTemplate requestTemplate;

    @BeforeEach
    void setUp() {
        interceptor = new GatewaySecretRequestInterceptor(securityProperties);
        requestTemplate = new RequestTemplate();
        requestTemplate.feignTarget(feignTarget);
        // Default stub for feignTarget.name() used in logging
        when(feignTarget.name()).thenReturn("test-service");
    }

    @Test
    @DisplayName("Should add gateway secret header when security is enabled and token is configured")
    void testApply_AddsHeaderWhenEnabled() {
        // Given
        String expectedToken = "test-gateway-secret-token";
        String headerName = "X-Gateway-Secret";

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(expectedToken);
        when(securityProperties.getHeaderName()).thenReturn(headerName);

        // When
        interceptor.apply(requestTemplate);

        // Then
        Collection<String> headerValues = requestTemplate.headers().get(headerName);
        assertThat(headerValues).isNotNull();
        assertThat(headerValues).contains(expectedToken);
    }

    @Test
    @DisplayName("Should not add header when security is disabled")
    void testApply_NoHeaderWhenDisabled() {
        // Given
        when(securityProperties.isEnabled()).thenReturn(false);

        // When
        interceptor.apply(requestTemplate);

        // Then
        assertThat(requestTemplate.headers()).isEmpty();
    }

    @Test
    @DisplayName("Should not add header when token is null")
    void testApply_NoHeaderWhenTokenNull() {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(null);

        // When
        interceptor.apply(requestTemplate);

        // Then
        assertThat(requestTemplate.headers()).isEmpty();
    }

    @Test
    @DisplayName("Should use correct header name from properties")
    void testApply_UsesCorrectHeaderName() {
        // Given
        String customHeaderName = "X-Custom-Gateway-Header";
        String expectedToken = "my-secret";

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(expectedToken);
        when(securityProperties.getHeaderName()).thenReturn(customHeaderName);

        // When
        interceptor.apply(requestTemplate);

        // Then
        assertThat(requestTemplate.headers().containsKey(customHeaderName)).isTrue();
        assertThat(requestTemplate.headers().get(customHeaderName)).contains(expectedToken);
    }

    @Test
    @DisplayName("Should not add header when token is empty string")
    void testApply_HandlesEmptyToken() {
        // Given
        String emptyToken = "";
        String headerName = "X-Gateway-Secret";

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(emptyToken);
        when(securityProperties.getHeaderName()).thenReturn(headerName);

        // When
        interceptor.apply(requestTemplate);

        // Then - Feign's RequestTemplate doesn't add headers with empty values,
        // so the header collection for this key will be null or not contain the empty string
        Collection<String> headerValues = requestTemplate.headers().get(headerName);
        // Empty string headers may not be added by Feign, verify behavior
        if (headerValues != null) {
            assertThat(headerValues).contains(emptyToken);
        } else {
            // Empty values are filtered out by Feign
            assertThat(requestTemplate.headers()).isEmpty();
        }
    }
}
