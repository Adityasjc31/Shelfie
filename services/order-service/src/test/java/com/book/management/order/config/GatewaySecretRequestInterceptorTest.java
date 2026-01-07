package com.book.management.order.config;

import feign.RequestTemplate;
import feign.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GatewaySecretRequestInterceptor}.
 *
 * Tests the Feign request interceptor that injects the Gateway Secret header
 * into outgoing inter-service communication requests.
 *
 * Test Coverage:
 * - Header injection when security is enabled
 * - Header not injected when security is disabled
 * - Handling of null/empty tokens
 * - Proper header name usage
 * - Multiple request template scenarios
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class GatewaySecretRequestInterceptorTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    @Mock
    private RequestTemplate requestTemplate;

    @Mock
    private Target<?> target;

    private GatewaySecretRequestInterceptor interceptor;

    private static final String HEADER_NAME = "X-Gateway-Secret";
    private static final String VALID_TOKEN = "test-gateway-secret-token-12345";

    /**
     * Setup test dependencies before each test.
     */
    @BeforeEach
    void setUp() {
        interceptor = new GatewaySecretRequestInterceptor(securityProperties);
        lenient().when(requestTemplate.feignTarget()).thenReturn((Target) target);
        lenient().when(target.name()).thenReturn("book-service");
    }

    /**
     * Test: Verify header is injected when security is enabled with valid token.
     *
     * Scenario: Security enabled, valid token configured.
     * Expected: Gateway secret header is added to request.
     */
    @Test
    void testApply_WithSecurityEnabled_ShouldInjectHeader() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(VALID_TOKEN);
        when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, times(1)).header(HEADER_NAME, VALID_TOKEN);
        verify(securityProperties, times(1)).isEnabled();
        verify(securityProperties, times(1)).getExpectedToken();
        verify(securityProperties, times(1)).getHeaderName();
    }

    /**
     * Test: Verify header is NOT injected when security is disabled.
     *
     * Scenario: Security is disabled in configuration.
     * Expected: No header is added to request.
     */
    @Test
    void testApply_WithSecurityDisabled_ShouldNotInjectHeader() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(false);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, never()).header(anyString(), anyString());
        verify(securityProperties, times(1)).isEnabled();
        verify(securityProperties, never()).getExpectedToken();
    }

    /**
     * Test: Verify header is NOT injected when token is null.
     *
     * Scenario: Security enabled but token is null.
     * Expected: No header is added to request.
     */
    @Test
    void testApply_WithNullToken_ShouldNotInjectHeader() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(null);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, never()).header(anyString(), anyString());
    }

    /**
     * Test: Verify interceptor handles empty token gracefully.
     *
     * Scenario: Security enabled but token is empty string.
     * Expected: Header is still added with empty token.
     */
    @Test
    void testApply_WithEmptyToken_ShouldInjectEmptyHeader() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn("");
        when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, times(1)).header(HEADER_NAME, "");
    }

    /**
     * Test: Verify custom header name is used when configured.
     *
     * Scenario: Non-default header name configured.
     * Expected: Custom header name is used for injection.
     */
    @Test
    void testApply_WithCustomHeaderName_ShouldUseCustomName() {
        // Arrange
        String customHeaderName = "X-Custom-Gateway-Header";
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(VALID_TOKEN);
        when(securityProperties.getHeaderName()).thenReturn(customHeaderName);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, times(1)).header(customHeaderName, VALID_TOKEN);
    }

    /**
     * Test: Verify interceptor works with different service targets.
     *
     * Scenario: Multiple inter-service calls to different services.
     * Expected: Header is injected for all services.
     */
    @Test
    void testApply_WithDifferentServiceTargets_ShouldInjectHeaderForAll() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(VALID_TOKEN);
        when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);

        // Test with book-service
        when(target.name()).thenReturn("book-service");
        interceptor.apply(requestTemplate);
        verify(requestTemplate, times(1)).header(HEADER_NAME, VALID_TOKEN);

        // Test with inventory-service
        reset(requestTemplate);
        lenient().when(requestTemplate.feignTarget()).thenReturn((Target) target);
        lenient().when(target.name()).thenReturn("inventory-service");
        interceptor.apply(requestTemplate);
        verify(requestTemplate, times(1)).header(HEADER_NAME, VALID_TOKEN);
    }

    /**
     * Test: Verify interceptor handles very long tokens.
     *
     * Scenario: Token is extremely long (for logging masking test).
     * Expected: Header is injected successfully.
     */
    @Test
    void testApply_WithLongToken_ShouldInjectHeader() {
        // Arrange
        String longToken = "a".repeat(100);
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(longToken);
        when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, times(1)).header(HEADER_NAME, longToken);
    }

    /**
     * Test: Verify interceptor handles short tokens.
     *
     * Scenario: Token is very short (less than masking threshold).
     * Expected: Header is injected successfully.
     */
    @Test
    void testApply_WithShortToken_ShouldInjectHeader() {
        // Arrange
        String shortToken = "abc";
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(shortToken);
        when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, times(1)).header(HEADER_NAME, shortToken);
    }

    /**
     * Test: Verify interceptor can be created with valid security properties.
     *
     * Expected: Interceptor is instantiated successfully.
     */
    @Test
    void testConstructor_WithValidSecurityProperties_ShouldInstantiate() {
        // Arrange & Act
        GatewaySecretRequestInterceptor newInterceptor =
            new GatewaySecretRequestInterceptor(securityProperties);

        // Assert
        assertNotNull(newInterceptor);
    }

    /**
     * Test: Verify interceptor handles multiple apply calls.
     *
     * Scenario: Interceptor is reused for multiple requests.
     * Expected: Each request gets the header injected.
     */
    @Test
    void testApply_MultipleInvocations_ShouldInjectHeaderEachTime() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(VALID_TOKEN);
        when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);

        // Act
        interceptor.apply(requestTemplate);
        interceptor.apply(requestTemplate);
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate, times(3)).header(HEADER_NAME, VALID_TOKEN);
    }

    /**
     * Test: Verify warning is logged when security is disabled.
     *
     * Scenario: Security disabled - should log warning.
     * Expected: No exception is thrown, processing continues.
     */
    @Test
    void testApply_WithDisabledSecurity_ShouldContinueWithoutError() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> interceptor.apply(requestTemplate));
    }

    /**
     * Test: Verify warning is logged when no token configured.
     *
     * Scenario: Security enabled but no token configured.
     * Expected: No exception is thrown, processing continues.
     */
    @Test
    void testApply_WithNoTokenConfigured_ShouldContinueWithoutError() {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getExpectedToken()).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> interceptor.apply(requestTemplate));
    }
}

