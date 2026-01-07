package com.book.management.inventory.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GatewaySecurityProperties.
 * Tests configuration properties bean.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-02
 */
class GatewaySecurityPropertiesTest {

    @Test
    void defaultValues_AreCorrect() {
        // Arrange
        GatewaySecurityProperties properties = new GatewaySecurityProperties();

        // Assert
        assertTrue(properties.isEnabled());
        assertEquals("X-Gateway-Secret", properties.getHeaderName());
        assertNotNull(properties.getPublicPaths());
        assertTrue(properties.getPublicPaths().isEmpty());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        GatewaySecurityProperties properties = new GatewaySecurityProperties();

        // Act
        properties.setEnabled(false);
        properties.setHeaderName("X-Custom-Header");
        properties.setExpectedToken("test-token");
        List<String> publicPaths = new ArrayList<>();
        publicPaths.add("/actuator");
        publicPaths.add("/swagger-ui");
        properties.setPublicPaths(publicPaths);

        // Assert
        assertFalse(properties.isEnabled());
        assertEquals("X-Custom-Header", properties.getHeaderName());
        assertEquals("test-token", properties.getExpectedToken());
        assertEquals(2, properties.getPublicPaths().size());
        assertTrue(properties.getPublicPaths().contains("/actuator"));
    }
}
