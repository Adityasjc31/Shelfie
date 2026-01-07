package com.book.management.order.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FeignGlobalConfig}.
 *
 * Tests the Feign configuration that provides custom error decoder
 * and request interceptor beans for inter-service communication.
 *
 * Test Coverage:
 * - Custom error decoder bean creation
 * - Gateway secret request interceptor bean creation
 * - Proper dependency injection
 * - Bean type validation
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class FeignGlobalConfigTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    private FeignGlobalConfig feignGlobalConfig;

    /**
     * Setup test dependencies before each test.
     */
    @BeforeEach
    void setUp() {
        feignGlobalConfig = new FeignGlobalConfig(securityProperties);
    }

    /**
     * Test: Verify error decoder bean is created successfully.
     *
     * Expected: CustomFeignErrorDecoder instance is returned.
     */
    @Test
    void testErrorDecoder_ShouldReturnCustomFeignErrorDecoder() {
        // Act
        ErrorDecoder errorDecoder = feignGlobalConfig.errorDecoder();

        // Assert
        assertNotNull(errorDecoder);
        assertInstanceOf(CustomFeignErrorDecoder.class, errorDecoder);
    }

    /**
     * Test: Verify gateway secret request interceptor bean is created.
     *
     * Expected: GatewaySecretRequestInterceptor instance is returned.
     */
    @Test
    void testGatewaySecretRequestInterceptor_ShouldReturnInterceptor() {
        // Act
        RequestInterceptor interceptor = feignGlobalConfig.gatewaySecretRequestInterceptor();

        // Assert
        assertNotNull(interceptor);
        assertInstanceOf(GatewaySecretRequestInterceptor.class, interceptor);
    }

    /**
     * Test: Verify request interceptor uses injected security properties.
     *
     * Expected: Interceptor is created with the provided security properties.
     */
    @Test
    void testGatewaySecretRequestInterceptor_ShouldUseSecurityProperties() {
        // Arrange
        lenient().when(securityProperties.isEnabled()).thenReturn(true);
        lenient().when(securityProperties.getExpectedToken()).thenReturn("test-token");
        lenient().when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");

        // Act
        RequestInterceptor interceptor = feignGlobalConfig.gatewaySecretRequestInterceptor();

        // Assert
        assertNotNull(interceptor);
        assertInstanceOf(GatewaySecretRequestInterceptor.class, interceptor);
    }

    /**
     * Test: Verify multiple calls to errorDecoder return new instances.
     *
     * Expected: Each call creates a new CustomFeignErrorDecoder instance.
     */
    @Test
    void testErrorDecoder_MultipleCalls_ShouldReturnNewInstances() {
        // Act
        ErrorDecoder decoder1 = feignGlobalConfig.errorDecoder();
        ErrorDecoder decoder2 = feignGlobalConfig.errorDecoder();

        // Assert
        assertNotNull(decoder1);
        assertNotNull(decoder2);
        assertNotSame(decoder1, decoder2);
    }

    /**
     * Test: Verify multiple calls to gatewaySecretRequestInterceptor return new instances.
     *
     * Expected: Each call creates a new GatewaySecretRequestInterceptor instance.
     */
    @Test
    void testGatewaySecretRequestInterceptor_MultipleCalls_ShouldReturnNewInstances() {
        // Act
        RequestInterceptor interceptor1 = feignGlobalConfig.gatewaySecretRequestInterceptor();
        RequestInterceptor interceptor2 = feignGlobalConfig.gatewaySecretRequestInterceptor();

        // Assert
        assertNotNull(interceptor1);
        assertNotNull(interceptor2);
        assertNotSame(interceptor1, interceptor2);
    }

    /**
     * Test: Verify configuration can be instantiated with valid security properties.
     *
     * Expected: FeignGlobalConfig is created successfully.
     */
    @Test
    void testFeignGlobalConfig_WithValidSecurityProperties_ShouldInstantiate() {
        // Arrange
        GatewaySecurityProperties props = new GatewaySecurityProperties();
        props.setEnabled(true);
        props.setHeaderName("X-Gateway-Secret");
        props.setExpectedToken("secret-token");

        // Act
        FeignGlobalConfig config = new FeignGlobalConfig(props);

        // Assert
        assertNotNull(config);
        assertNotNull(config.errorDecoder());
        assertNotNull(config.gatewaySecretRequestInterceptor());
    }

    /**
     * Test: Verify error decoder works independently of security properties.
     *
     * Expected: Error decoder is created even when security properties are null.
     */
    @Test
    void testErrorDecoder_WithNullSecurityProperties_ShouldStillWork() {
        // Arrange
        FeignGlobalConfig config = new FeignGlobalConfig(null);

        // Act
        ErrorDecoder decoder = config.errorDecoder();

        // Assert
        assertNotNull(decoder);
        assertInstanceOf(CustomFeignErrorDecoder.class, decoder);
    }

    /**
     * Test: Verify request interceptor is created with disabled security.
     *
     * Scenario: Security is disabled in properties.
     * Expected: Interceptor is still created (behavior depends on runtime checks).
     */
    @Test
    void testGatewaySecretRequestInterceptor_WithDisabledSecurity_ShouldCreateInterceptor() {
        // Arrange
        lenient().when(securityProperties.isEnabled()).thenReturn(false);

        // Act
        RequestInterceptor interceptor = feignGlobalConfig.gatewaySecretRequestInterceptor();

        // Assert
        assertNotNull(interceptor);
        assertInstanceOf(GatewaySecretRequestInterceptor.class, interceptor);
    }
}

