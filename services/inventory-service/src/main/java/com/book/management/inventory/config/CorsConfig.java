package com.book.management.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 * 
 * Configures allowed origins, methods, and headers for cross-origin requests.
 * In production, this should be configured at the API Gateway level.
 * This configuration is primarily for development/testing direct access.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
public class CorsConfig {

    /**
     * Configures CORS filter with appropriate settings.
     * 
     * Development Settings (current):
     * - Allows all origins
     * - Allows all methods
     * - Allows all headers
     * 
     * Production Settings (recommended):
     * - Restrict to specific frontend domains
     * - Limit methods to required ones only
     * - Specify exact headers needed
     * 
     * @return CorsFilter bean
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Allowed origins (configure per environment)
        // Development: Allow all
        config.setAllowedOriginPatterns(List.of("*"));
        
        // Production: Specify exact origins
        // config.setAllowedOrigins(Arrays.asList(
        //     "https://digitalbookstore.com",
        //     "https://admin.digitalbookstore.com"
        // ));
        
        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Allowed headers
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "X-Request-ID"
        ));
        
        // Exposed headers (headers that client can read)
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Request-ID",
                "X-Total-Count"
        ));
        
        // Max age for preflight requests (1 hour)
        config.setMaxAge(3600L);
        
        // Apply CORS configuration to all endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
