package com.book.management.inventory.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CorsConfig.
 * Tests CORS filter configuration.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
class CorsConfigTest {

    @Test
    void corsFilter_CreatesFilterSuccessfully() {
        // Arrange
        CorsConfig corsConfig = new CorsConfig();

        // Act
        CorsFilter corsFilter = corsConfig.corsFilter();

        // Assert
        assertNotNull(corsFilter);
    }
}
