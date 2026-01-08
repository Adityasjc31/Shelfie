package com.book.management.book.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GatewaySecurityProperties Tests")
class GatewaySecurityPropertiesTest {

    private GatewaySecurityProperties properties;

    @BeforeEach
    void setUp() {
        properties = new GatewaySecurityProperties();
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have enabled true by default")
        void shouldHaveEnabledTrueByDefault() {
            assertTrue(properties.isEnabled());
        }

        @Test
        @DisplayName("Should have default header name")
        void shouldHaveDefaultHeaderName() {
            assertEquals("X-Gateway-Secret", properties.getHeaderName());
        }

        @Test
        @DisplayName("Should have null expected token by default")
        void shouldHaveNullExpectedTokenByDefault() {
            assertNull(properties.getExpectedToken());
        }

        @Test
        @DisplayName("Should have empty public paths by default")
        void shouldHaveEmptyPublicPathsByDefault() {
            assertNotNull(properties.getPublicPaths());
            assertTrue(properties.getPublicPaths().isEmpty());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("Should set enabled property")
        void shouldSetEnabledProperty() {
            properties.setEnabled(false);
            assertFalse(properties.isEnabled());

            properties.setEnabled(true);
            assertTrue(properties.isEnabled());
        }

        @Test
        @DisplayName("Should set header name")
        void shouldSetHeaderName() {
            properties.setHeaderName("Custom-Header");
            assertEquals("Custom-Header", properties.getHeaderName());
        }

        @Test
        @DisplayName("Should set expected token")
        void shouldSetExpectedToken() {
            properties.setExpectedToken("my-secret-token");
            assertEquals("my-secret-token", properties.getExpectedToken());
        }

        @Test
        @DisplayName("Should set public paths")
        void shouldSetPublicPaths() {
            List<String> paths = Arrays.asList("/public", "/health", "/actuator/health");
            properties.setPublicPaths(paths);
            
            assertEquals(3, properties.getPublicPaths().size());
            assertTrue(properties.getPublicPaths().contains("/public"));
            assertTrue(properties.getPublicPaths().contains("/health"));
            assertTrue(properties.getPublicPaths().contains("/actuator/health"));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return enabled state")
        void shouldReturnEnabledState() {
            properties.setEnabled(true);
            assertTrue(properties.isEnabled());
        }

        @Test
        @DisplayName("Should return header name")
        void shouldReturnHeaderName() {
            String expected = "Authorization-Gateway";
            properties.setHeaderName(expected);
            assertEquals(expected, properties.getHeaderName());
        }

        @Test
        @DisplayName("Should return expected token")
        void shouldReturnExpectedToken() {
            String token = "super-secret";
            properties.setExpectedToken(token);
            assertEquals(token, properties.getExpectedToken());
        }

        @Test
        @DisplayName("Should return public paths list")
        void shouldReturnPublicPathsList() {
            List<String> paths = Arrays.asList("/api/public", "/swagger-ui");
            properties.setPublicPaths(paths);
            
            List<String> result = properties.getPublicPaths();
            assertEquals(2, result.size());
            assertEquals("/api/public", result.get(0));
            assertEquals("/swagger-ui", result.get(1));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty header name")
        void shouldHandleEmptyHeaderName() {
            properties.setHeaderName("");
            assertEquals("", properties.getHeaderName());
        }

        @Test
        @DisplayName("Should handle null header name")
        void shouldHandleNullHeaderName() {
            properties.setHeaderName(null);
            assertNull(properties.getHeaderName());
        }

        @Test
        @DisplayName("Should handle empty expected token")
        void shouldHandleEmptyExpectedToken() {
            properties.setExpectedToken("");
            assertEquals("", properties.getExpectedToken());
        }

        @Test
        @DisplayName("Should handle single public path")
        void shouldHandleSinglePublicPath() {
            properties.setPublicPaths(List.of("/health"));
            assertEquals(1, properties.getPublicPaths().size());
        }

        @Test
        @DisplayName("Should handle many public paths")
        void shouldHandleManyPublicPaths() {
            List<String> paths = Arrays.asList(
                "/health", "/info", "/metrics", "/actuator", "/swagger", 
                "/public", "/api/public", "/auth/login", "/auth/register"
            );
            properties.setPublicPaths(paths);
            assertEquals(9, properties.getPublicPaths().size());
        }
    }
}
