package com.book.management.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MicroserviceConfig.
 * Tests configuration beans.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
class MicroserviceConfigTest {

    @Test
    void restTemplate_CreatesBean() {
        // Arrange
        MicroserviceConfig config = new MicroserviceConfig();

        // Act
        RestTemplate restTemplate = config.restTemplate();

        // Assert
        assertNotNull(restTemplate);
    }

    @Test
    void customOpenAPI_CreatesBean() {
        // Arrange
        MicroserviceConfig config = new MicroserviceConfig();

        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Inventory Service API", openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
    }
}
