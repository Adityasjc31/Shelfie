package com.book.management.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for API Gateway.
 * 
 * Configures Cross-Origin Resource Sharing (CORS) settings
 * to allow frontend applications to access the API.
 * 
 * Development Settings (current):
 * - Allows all origins (*)
 * - Allows all methods
 * - Allows all headers
 * 
 * Production Settings (recommended):
 * - Restrict to specific frontend domains
 * - Limit methods to required ones
 * - Specify exact headers needed
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
public class CorsConfig {

    /**
     * Configures CORS filter with appropriate settings.
     * 
     * @return CorsWebFilter bean
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);
        
        // Allowed origins
        // Development: Allow all
        corsConfig.setAllowedOriginPatterns(List.of("*"));
        
        // Production: Specify exact origins
        // corsConfig.setAllowedOrigins(Arrays.asList(
        //     "https://digitalbookstore.com",
        //     "https://admin.digitalbookstore.com",
        //     "https://mobile.digitalbookstore.com"
        // ));
        
        // Allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Allowed headers
        corsConfig.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "X-Request-ID",
                "X-User-Id",
                "X-User-Email",
                "X-User-Roles",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        
        // Exposed headers (headers that client can read)
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Request-ID",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size",
                "Content-Disposition"
        ));
        
        // Max age for preflight requests (1 hour)
        corsConfig.setMaxAge(3600L);
        
        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
