package com.book.management.review_rating.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Unit tests for GatewaySecretRequestInterceptor.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@ExtendWith(MockitoExtension.class)
class GatewaySecretRequestInterceptorTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    private GatewaySecretRequestInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new GatewaySecretRequestInterceptor(securityProperties);
    }

    @Test
    @DisplayName("Should add header when security is enabled and token exists")
    void apply_WhenEnabledAndTokenExists_AddsHeader() {
        RequestTemplate requestTemplate = new RequestTemplate();

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn("my-secret-token");
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");

        interceptor.apply(requestTemplate);

        // Verify header was added
        assertTrue(requestTemplate.headers().containsKey("X-Gateway-Secret"));
    }

    @Test
    @DisplayName("Should not add header when security is disabled")
    void apply_WhenDisabled_DoesNotAddHeader() {
        RequestTemplate requestTemplate = new RequestTemplate();

        when(securityProperties.isEnabled()).thenReturn(false);

        interceptor.apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey("X-Gateway-Secret"));
    }

    @Test
    @DisplayName("Should not add header when token is null")
    void apply_WhenTokenIsNull_DoesNotAddHeader() {
        RequestTemplate requestTemplate = new RequestTemplate();

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(null);

        interceptor.apply(requestTemplate);

        // Should not add any headers when token is null
        assertTrue(requestTemplate.headers().isEmpty() ||
                !requestTemplate.headers().containsKey("X-Gateway-Secret"));
    }

    @Test
    @DisplayName("Should use configured header name")
    void apply_UsesConfiguredHeaderName() {
        RequestTemplate requestTemplate = new RequestTemplate();

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn("token123");
        when(securityProperties.getHeaderName()).thenReturn("X-Custom-Header");

        interceptor.apply(requestTemplate);

        assertTrue(requestTemplate.headers().containsKey("X-Custom-Header"));
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }

    private void assertFalse(boolean condition) {
        org.junit.jupiter.api.Assertions.assertFalse(condition);
    }
}
