package com.book.management.order.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GatewaySecurityProperties}.
 *
 * Tests the configuration properties for gateway security validation.
 * Verifies proper binding of configuration values and default values.
 *
 * Test Coverage:
 * - Default property values
 * - Property getters and setters
 * - Enabled/disabled flag behavior
 * - Header name configuration
 * - Token configuration
 * - Public paths configuration
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
class GatewaySecurityPropertiesTest {

    private GatewaySecurityProperties properties;

    /**
     * Setup test instance before each test.
     */
    @BeforeEach
    void setUp() {
        properties = new GatewaySecurityProperties();
    }

    /**
     * Test: Verify default value for enabled flag is true.
     *
     * Expected: Gateway security is enabled by default.
     */
    @Test
    void testDefaultEnabled_ShouldBeTrue() {
        // Assert
        assertTrue(properties.isEnabled());
    }

    /**
     * Test: Verify default header name is "X-Gateway-Secret".
     *
     * Expected: Default header name is correctly set.
     */
    @Test
    void testDefaultHeaderName_ShouldBeXGatewaySecret() {
        // Assert
        assertEquals("X-Gateway-Secret", properties.getHeaderName());
    }

    /**
     * Test: Verify default expected token is null.
     *
     * Expected: No token is configured by default.
     */
    @Test
    void testDefaultExpectedToken_ShouldBeNull() {
        // Assert
        assertNull(properties.getExpectedToken());
    }

    /**
     * Test: Verify default public paths list is empty.
     *
     * Expected: Empty list of public paths by default.
     */
    @Test
    void testDefaultPublicPaths_ShouldBeEmptyList() {
        // Assert
        assertNotNull(properties.getPublicPaths());
        assertTrue(properties.getPublicPaths().isEmpty());
    }

    /**
     * Test: Verify enabled flag can be set to false.
     *
     * Scenario: Disable gateway security.
     * Expected: Enabled flag is updated correctly.
     */
    @Test
    void testSetEnabled_ToFalse_ShouldUpdateValue() {
        // Act
        properties.setEnabled(false);

        // Assert
        assertFalse(properties.isEnabled());
    }

    /**
     * Test: Verify enabled flag can be set to true.
     *
     * Scenario: Explicitly enable gateway security.
     * Expected: Enabled flag remains true.
     */
    @Test
    void testSetEnabled_ToTrue_ShouldUpdateValue() {
        // Act
        properties.setEnabled(true);

        // Assert
        assertTrue(properties.isEnabled());
    }

    /**
     * Test: Verify custom header name can be set.
     *
     * Scenario: Configure custom header name for gateway validation.
     * Expected: Header name is updated correctly.
     */
    @Test
    void testSetHeaderName_WithCustomValue_ShouldUpdateValue() {
        // Arrange
        String customHeaderName = "X-Custom-Gateway-Header";

        // Act
        properties.setHeaderName(customHeaderName);

        // Assert
        assertEquals(customHeaderName, properties.getHeaderName());
    }

    /**
     * Test: Verify expected token can be set.
     *
     * Scenario: Configure gateway secret token.
     * Expected: Token is updated correctly.
     */
    @Test
    void testSetExpectedToken_WithValue_ShouldUpdateValue() {
        // Arrange
        String token = "my-secure-gateway-token-12345";

        // Act
        properties.setExpectedToken(token);

        // Assert
        assertEquals(token, properties.getExpectedToken());
    }

    /**
     * Test: Verify expected token can be set to null.
     *
     * Scenario: Clear the configured token.
     * Expected: Token is set to null.
     */
    @Test
    void testSetExpectedToken_ToNull_ShouldUpdateValue() {
        // Arrange
        properties.setExpectedToken("initial-token");

        // Act
        properties.setExpectedToken(null);

        // Assert
        assertNull(properties.getExpectedToken());
    }

    /**
     * Test: Verify public paths can be configured.
     *
     * Scenario: Add public endpoints that bypass gateway validation.
     * Expected: Public paths list is updated correctly.
     */
    @Test
    void testSetPublicPaths_WithValues_ShouldUpdateList() {
        // Arrange
        List<String> publicPaths = Arrays.asList(
            "/actuator/health",
            "/actuator/info",
            "/api/v1/public"
        );

        // Act
        properties.setPublicPaths(publicPaths);

        // Assert
        assertEquals(3, properties.getPublicPaths().size());
        assertTrue(properties.getPublicPaths().contains("/actuator/health"));
        assertTrue(properties.getPublicPaths().contains("/actuator/info"));
        assertTrue(properties.getPublicPaths().contains("/api/v1/public"));
    }

    /**
     * Test: Verify public paths can be set to empty list.
     *
     * Scenario: Clear all public paths.
     * Expected: Public paths list is empty.
     */
    @Test
    void testSetPublicPaths_ToEmptyList_ShouldUpdateList() {
        // Arrange
        properties.setPublicPaths(Arrays.asList("/path1", "/path2"));

        // Act
        properties.setPublicPaths(new ArrayList<>());

        // Assert
        assertTrue(properties.getPublicPaths().isEmpty());
    }

    /**
     * Test: Verify public paths can be modified after retrieval.
     *
     * Scenario: Get public paths and add new paths.
     * Expected: List is mutable and can be modified.
     */
    @Test
    void testGetPublicPaths_ShouldReturnMutableList() {
        // Act
        List<String> publicPaths = properties.getPublicPaths();
        publicPaths.add("/test/path");

        // Assert
        assertEquals(1, properties.getPublicPaths().size());
        assertTrue(properties.getPublicPaths().contains("/test/path"));
    }

    /**
     * Test: Verify multiple property updates work correctly.
     *
     * Scenario: Configure all properties with custom values.
     * Expected: All properties are updated correctly.
     */
    @Test
    void testMultiplePropertyUpdates_ShouldAllBeApplied() {
        // Arrange & Act
        properties.setEnabled(false);
        properties.setHeaderName("X-Custom-Header");
        properties.setExpectedToken("custom-token-123");
        properties.setPublicPaths(Arrays.asList("/public1", "/public2"));

        // Assert
        assertFalse(properties.isEnabled());
        assertEquals("X-Custom-Header", properties.getHeaderName());
        assertEquals("custom-token-123", properties.getExpectedToken());
        assertEquals(2, properties.getPublicPaths().size());
    }

    /**
     * Test: Verify empty string header name can be set.
     *
     * Scenario: Set header name to empty string.
     * Expected: Empty string is accepted.
     */
    @Test
    void testSetHeaderName_ToEmptyString_ShouldUpdateValue() {
        // Act
        properties.setHeaderName("");

        // Assert
        assertEquals("", properties.getHeaderName());
    }

    /**
     * Test: Verify empty string token can be set.
     *
     * Scenario: Set token to empty string.
     * Expected: Empty string is accepted.
     */
    @Test
    void testSetExpectedToken_ToEmptyString_ShouldUpdateValue() {
        // Act
        properties.setExpectedToken("");

        // Assert
        assertEquals("", properties.getExpectedToken());
    }

    /**
     * Test: Verify public paths with duplicate values.
     *
     * Scenario: Add duplicate paths to public paths list.
     * Expected: Duplicates are preserved (no automatic deduplication).
     */
    @Test
    void testSetPublicPaths_WithDuplicates_ShouldPreserveDuplicates() {
        // Arrange
        List<String> pathsWithDuplicates = Arrays.asList(
            "/path1",
            "/path2",
            "/path1"
        );

        // Act
        properties.setPublicPaths(pathsWithDuplicates);

        // Assert
        assertEquals(3, properties.getPublicPaths().size());
    }

    /**
     * Test: Verify very long token value handling.
     *
     * Scenario: Set extremely long token value.
     * Expected: Long token is accepted without truncation.
     */
    @Test
    void testSetExpectedToken_WithVeryLongValue_ShouldAccept() {
        // Arrange
        String longToken = "a".repeat(1000);

        // Act
        properties.setExpectedToken(longToken);

        // Assert
        assertEquals(1000, properties.getExpectedToken().length());
        assertEquals(longToken, properties.getExpectedToken());
    }

    /**
     * Test: Verify properties object can be created and all defaults are set.
     *
     * Expected: New instance has all default values properly initialized.
     */
    @Test
    void testNewInstance_ShouldHaveAllDefaultValues() {
        // Arrange
        GatewaySecurityProperties newProps = new GatewaySecurityProperties();

        // Assert
        assertTrue(newProps.isEnabled());
        assertEquals("X-Gateway-Secret", newProps.getHeaderName());
        assertNull(newProps.getExpectedToken());
        assertNotNull(newProps.getPublicPaths());
        assertTrue(newProps.getPublicPaths().isEmpty());
    }
}

