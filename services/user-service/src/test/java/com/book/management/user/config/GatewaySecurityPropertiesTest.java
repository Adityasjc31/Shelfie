// ============================================================================
// FILE: GatewaySecurityPropertiesTest.java
// ============================================================================
package com.book.management.user.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GatewaySecurityProperties.
 * Tests configuration properties initialization and default values.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@DisplayName("GatewaySecurityProperties Unit Tests")
class GatewaySecurityPropertiesTest {

    private GatewaySecurityProperties properties;

    @BeforeEach
    void setUp() {
        properties = new GatewaySecurityProperties();
    }

    @Test
    @DisplayName("Should have security enabled by default")
    void testDefaultEnabled() {
        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should have default header name")
    void testDefaultHeaderName() {
        assertThat(properties.getHeaderName()).isEqualTo("X-Gateway-Secret");
    }

    @Test
    @DisplayName("Should have null expected token by default")
    void testDefaultExpectedToken() {
        assertThat(properties.getExpectedToken()).isNull();
    }

    @Test
    @DisplayName("Should have empty public paths by default")
    void testDefaultPublicPaths() {
        assertThat(properties.getPublicPaths()).isNotNull();
        assertThat(properties.getPublicPaths()).isEmpty();
    }

    @Test
    @DisplayName("Should allow setting enabled flag")
    void testSetEnabled() {
        properties.setEnabled(false);
        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should allow setting header name")
    void testSetHeaderName() {
        properties.setHeaderName("X-Custom-Header");
        assertThat(properties.getHeaderName()).isEqualTo("X-Custom-Header");
    }

    @Test
    @DisplayName("Should allow setting expected token")
    void testSetExpectedToken() {
        properties.setExpectedToken("my-secret-token");
        assertThat(properties.getExpectedToken()).isEqualTo("my-secret-token");
    }

    @Test
    @DisplayName("Should allow setting public paths")
    void testSetPublicPaths() {
        properties.setPublicPaths(Arrays.asList("/actuator", "/health", "/public"));
        assertThat(properties.getPublicPaths()).hasSize(3);
        assertThat(properties.getPublicPaths()).contains("/actuator", "/health", "/public");
    }
}
