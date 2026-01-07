package com.book.management.api_gateway.config;

import com.book.management.api_gateway.filter.AuthenticationFilter;
import com.book.management.api_gateway.filter.LoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

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
        private final GatewaySecretProperties gatewaySecretProperties;

        /**
         * Adds gateway secret header to filters if enabled.
         * 
         * @param filters current gateway filter spec
         * @return updated gateway filter spec with secret header if enabled
         */
        private GatewayFilterSpec addGatewaySecretHeader(
                        GatewayFilterSpec filters) {
                if (gatewaySecretProperties.isEnabled() && gatewaySecretProperties.getToken() != null) {
                        return filters.addRequestHeader(
                                        gatewaySecretProperties.getHeaderName(),
                                        gatewaySecretProperties.getToken());
                }
                return filters;
        }

        /**
         * Applies standard filters for authenticated service routes.
         * 
         * @param filters            current gateway filter spec
         * @param circuitBreakerName name of the circuit breaker
         * @param fallbackPath       fallback path for circuit breaker
         * @return configured gateway filter spec
         */
        private GatewayFilterSpec applyStandardFilters(
                        GatewayFilterSpec filters,
                        String circuitBreakerName,
                        String fallbackPath) {
                return addGatewaySecretHeader(filters)
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                                .setName(circuitBreakerName)
                                                .setFallbackUri(fallbackPath))
                                .retry(retryConfig -> retryConfig.setRetries(3));
        }

        /**
         * Applies filters for authentication service route (no auth filter needed).
         * 
         * @param filters current gateway filter spec
         * @return configured gateway filter spec
         */
        private GatewayFilterSpec applyAuthServiceFilters(
                        GatewayFilterSpec filters) {
                return filters
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .circuitBreaker(c -> c
                                                .setName("authCircuitBreaker")
                                                .setFallbackUri("forward:/fallback/auth"))
                                .retry(retryConfig -> retryConfig
                                                .setRetries(3)
                                                .setBackoff(Duration.ofMillis(50), Duration.ofSeconds(5), 2, true));
        }

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
                                                .path("/api/v1/auth/**")
                                                .filters(this::applyAuthServiceFilters)
                                                .uri("lb://authentication-service"))

                                // ==========================================
                                // BOOK SERVICE ROUTES
                                // ==========================================
                                .route("book-service", r -> r
                                                .path("/api/v1/book/**")
                                                .filters(f -> applyStandardFilters(f, "bookCircuitBreaker",
                                                                "forward:/fallback/books"))
                                                .uri("lb://book-service"))

                                // ==========================================
                                // INVENTORY SERVICE ROUTES
                                // ==========================================
                                .route("inventory-service", r -> r
                                                .path("/api/v1/inventory/**")
                                                .filters(f -> applyStandardFilters(f, "inventoryCircuitBreaker",
                                                                "forward:/fallback/inventory"))
                                                .uri("lb://inventory-service"))

                                // ==========================================
                                // ORDER SERVICE ROUTES
                                // ==========================================
                                .route("order-service", r -> r
                                                .path("/api/v1/order/**")
                                                .filters(f -> applyStandardFilters(f, "orderCircuitBreaker",
                                                                "forward:/fallback/orders"))
                                                .uri("lb://order-service"))

                                // ==========================================
                                // REVIEW SERVICE ROUTES
                                // ==========================================
                                .route("review-service", r -> r
                                                .path("/api/v1/review/**")
                                                .filters(f -> applyStandardFilters(f, "reviewCircuitBreaker",
                                                                "forward:/fallback/reviews"))
                                                .uri("lb://review-service"))

                                // ==========================================
                                // USER SERVICE ROUTES
                                // ==========================================
                                .route("user-service", r -> r
                                                .path("/api/v1/users/**")
                                                .filters(f -> applyStandardFilters(f, "userCircuitBreaker",
                                                                "forward:/fallback/users"))
                                                .uri("lb://user-service"))

                                .build();
        }
}
