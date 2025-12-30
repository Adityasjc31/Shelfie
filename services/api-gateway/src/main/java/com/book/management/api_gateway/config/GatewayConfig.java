package com.book.management.api_gateway.config;

import com.book.management.book.gateway.filter.AuthenticationFilter;
import com.book.management.book.gateway.filter.LoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration.
 * 
 * Defines all routes to downstream microservices with appropriate filters.
 * Uses Eureka for service discovery and load balancing.
 * 
 * Route Pattern: /service-path/** → lb://service-name
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;
    private final LoggingFilter loggingFilter;

    /**
     * Configures all routes for the API Gateway.
     * Each route includes:
     * - Path predicate for matching requests
     * - URI with load balancer (lb://) for service discovery
     * - StripPrefix filter to remove gateway path prefix
     * - Custom filters for logging and authentication
     * 
     * @param builder RouteLocatorBuilder
     * @return RouteLocator with all configured routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                
                // ==========================================
                // AUTHENTICATION SERVICE ROUTES
                // ==========================================
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("authCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(null, null, null, true)))
                        .uri("lb://authentication-service"))
                
                // ==========================================
                // BOOK SERVICE ROUTES
                // ==========================================
                .route("book-service", r -> r
                        .path("/books/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("bookCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/books"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://book-service"))
                
                // ==========================================
                // INVENTORY SERVICE ROUTES
                // ==========================================
                .route("inventory-service", r -> r
                        .path("/inventory/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("inventoryCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/inventory"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://inventory-service"))
                
                // ==========================================
                // ORDER SERVICE ROUTES
                // ==========================================
                .route("order-service", r -> r
                        .path("/orders/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("orderCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/orders"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://order-service"))
                
                // ==========================================
                // REVIEW SERVICE ROUTES
                // ==========================================
                .route("review-service", r -> r
                        .path("/reviews/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("reviewCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/reviews"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://review-service"))
                
                // ==========================================
                // USER SERVICE ROUTES
                // ==========================================
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("userCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/users"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://user-service"))
                
                .build();
    }
}
