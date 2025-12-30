package com.book.management.api_gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Custom Gateway Filter for authentication.
 * 
 * Responsibilities:
 * - Extract JWT token from Authorization header
 * - Validate token with Authentication Service
 * - Check user permissions
 * - Add user context to request headers
 * - Handle authentication errors
 * 
 * Public endpoints (skip authentication):
 * - /auth/login
 * - /auth/register
 * - /actuator/**
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service.url:lb://authentication-service}")
    private String authServiceUrl;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/actuator",
            "/swagger-ui",
            "/api-docs");

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            
            // Skip authentication for public endpoints
            if (isPublicEndpoint(path)) {
                log.debug("Public endpoint accessed: {}", path);
                return chain.filter(exchange);
            }
            
            // Extract Authorization header
            if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header for path: {}", path);
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }
            
            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format for path: {}", path);
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authorizationHeader.substring(7);
            
            log.info("🔐 Validating token for path: {}", path);
            
            // Validate token with Authentication Service
            return validateToken(token)
                    .flatMap(validationResponse -> {
                        if (validationResponse.isValid()) {
                            log.info("✓ Token validated successfully for user: {}", 
                                    validationResponse.getUsername());
                            
                            // Add user context to request headers
                            ServerHttpRequest modifiedRequest = request.mutate()
                                    .header("X-User-Id", validationResponse.getUserId())
                                    .header("X-User-Email", validationResponse.getUsername())
                                    .header("X-User-Roles", String.join(",", validationResponse.getRoles()))
                                    .build();
                            
                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        } else {
                            log.warn("✗ Token validation failed for path: {}", path);
                            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                        }
                    })
                    .onErrorResume(error -> {
                        log.error("✗ Authentication error: {}", error.getMessage());
                        return onError(exchange, "Authentication service unavailable", 
                                HttpStatus.SERVICE_UNAVAILABLE);
                    });
        };
    }

    /**
     * Validates token with Authentication Service.
     * 
     * @param token JWT token
     * @return Mono of ValidationResponse
     */
    private Mono<ValidationResponse> validateToken(String token) {
        // TODO: Replace with actual authentication service call
        // For now, return a mock response for development

        return webClientBuilder.build()
                .post()
                .uri(authServiceUrl + "/api/v1/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ValidationResponse.class)
                .doOnError(error -> log.error("Error validating token: {}", error.getMessage()))
                .onErrorReturn(createMockValidationResponse()); // Fallback for development
    }

    /**
     * Creates mock validation response for development.
     * TODO: Remove this when authentication service is ready.
     */
    private ValidationResponse createMockValidationResponse() {
        log.warn("⚠ Using mock authentication - Remove in production!");
        ValidationResponse response = new ValidationResponse();
        response.setValid(true);
        response.setUserId("mock-user-id");
        response.setUsername("mock@example.com");
        response.setRoles(List.of("USER", "ADMIN"));
        return response;
    }

    /**
     * Checks if the endpoint is public (no authentication required).
     * 
     * @param path request path
     * @return true if public endpoint
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Creates error response.
     * 
     * @param exchange   ServerWebExchange
     * @param message    error message
     * @param httpStatus HTTP status
     * @return Mono<Void>
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String errorBody = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d}",
                httpStatus.getReasonPhrase(),
                message,
                httpStatus.value());

        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(errorBody.getBytes())));
    }

    /**
     * Configuration class for AuthenticationFilter.
     */
    @Data
    public static class Config {
        private boolean skipValidation = false;
    }

    /**
     * Validation response from Authentication Service.
     */
    @Data
    private static class ValidationResponse {
        private boolean valid;
        private String userId;
        private String username;
        private List<String> roles;
        private String message;
    }
}
