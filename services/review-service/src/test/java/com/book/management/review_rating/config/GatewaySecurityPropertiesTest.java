package com.book.management.review_rating.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GatewaySecurityProperties.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class GatewaySecurityPropertiesTest {

    private GatewaySecurityProperties properties;

    @BeforeEach
    void setUp() {
        properties = new GatewaySecurityProperties();
    }

    @Test
    @DisplayName("Default enabled should be true")
    void defaultEnabled_IsTrue() {
        assertTrue(properties.isEnabled());
    }

    @Test
    @DisplayName("Default headerName should be X-Gateway-Secret")
    void defaultHeaderName_IsXGatewaySecret() {
        assertEquals("X-Gateway-Secret", properties.getHeaderName());
    }

    @Test
    @DisplayName("Default expectedToken should be null")
    void defaultExpectedToken_IsNull() {
        assertNull(properties.getExpectedToken());
    }

    @Test
    @DisplayName("Default publicPaths should be empty list")
    void defaultPublicPaths_IsEmptyList() {
        assertNotNull(properties.getPublicPaths());
        assertTrue(properties.getPublicPaths().isEmpty());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        properties.setEnabled(false);
        properties.setHeaderName("X-Custom-Header");
        properties.setExpectedToken("my-secret-token");
        List<String> paths = Arrays.asList("/actuator", "/health");
        properties.setPublicPaths(paths);

        assertFalse(properties.isEnabled());
        assertEquals("X-Custom-Header", properties.getHeaderName());
        assertEquals("my-secret-token", properties.getExpectedToken());
        assertEquals(2, properties.getPublicPaths().size());
        assertTrue(properties.getPublicPaths().contains("/actuator"));
        assertTrue(properties.getPublicPaths().contains("/health"));
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        GatewaySecurityProperties props1 = new GatewaySecurityProperties();
        props1.setExpectedToken("token1");

        GatewaySecurityProperties props2 = new GatewaySecurityProperties();
        props2.setExpectedToken("token1");

        GatewaySecurityProperties props3 = new GatewaySecurityProperties();
        props3.setExpectedToken("token2");

        assertEquals(props1, props2);
        assertNotEquals(props1, props3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        GatewaySecurityProperties props1 = new GatewaySecurityProperties();
        props1.setExpectedToken("token1");

        GatewaySecurityProperties props2 = new GatewaySecurityProperties();
        props2.setExpectedToken("token1");

        assertEquals(props1.hashCode(), props2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain key fields")
    void toString_ContainsKeyFields() {
        properties.setEnabled(true);
        properties.setHeaderName("X-Test");

        String toString = properties.toString();
        assertTrue(toString.contains("X-Test"));
    }
}
