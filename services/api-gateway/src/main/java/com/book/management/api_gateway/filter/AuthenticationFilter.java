package com.book.management.api_gateway.filter;

import com.book.management.api_gateway.config.AuthenticationProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
 * Development Hook:
 * - Set auth.enabled=false in config to bypass authentication
 * - Uses mock user data when disabled
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;
    private final AuthenticationProperties authProperties;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/actuator",
            "/swagger-ui",
            "/api-docs");

    public AuthenticationFilter(WebClient.Builder webClientBuilder, AuthenticationProperties authProperties) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.authProperties = authProperties;
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

            // Check if authentication is disabled (development mode)
            if (!authProperties.isEnabled()) {
                log.warn("⚠ Authentication DISABLED - Using mock user for path: {}", path);
                return proceedWithMockUser(exchange, chain);
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
                            log.warn("✗ Token validation failed: {}", validationResponse.getMessage());
                            return onError(exchange,
                                    validationResponse.getMessage() != null
                                            ? validationResponse.getMessage()
                                            : "Invalid or expired token",
                                    HttpStatus.UNAUTHORIZED);
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
     * Proceeds with mock user when authentication is disabled.
     * 
     * @param exchange ServerWebExchange
     * @param chain    filter chain
     * @return Mono<Void>
     */
    private Mono<Void> proceedWithMockUser(ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        AuthenticationProperties.Mock mockConfig = authProperties.getMock();

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", mockConfig.getUserId())
                .header("X-User-Email", mockConfig.getUsername())
                .header("X-User-Roles", mockConfig.getRoles())
                .header("X-Auth-Mode", "MOCK")
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * Validates token with Authentication Service.
     * 
     * @param token JWT token
     * @return Mono of ValidationResponse
     */
    private Mono<ValidationResponse> validateToken(String token) {
        String validateUrl = authProperties.getService().getUrl()
                + authProperties.getService().getValidateEndpoint();

        log.debug("Validating token with auth service: {}", validateUrl);

        return webClientBuilder.build()
                .post()
                .uri(validateUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    log.warn("Token validation failed with status: {}", response.statusCode());
                    return Mono.empty();
                })
                .bodyToMono(ValidationResponse.class)
                .defaultIfEmpty(createInvalidResponse("Token validation failed"))
                .doOnError(error -> log.error("Error validating token: {}", error.getMessage()));
    }

    /**
     * Creates an invalid validation response.
     * 
     * @param message error message
     * @return ValidationResponse
     */
    private ValidationResponse createInvalidResponse(String message) {
        ValidationResponse response = new ValidationResponse();
        response.setValid(false);
        response.setMessage(message);
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
        // Filter configuration options can be added here
    }

    /**
     * Validation response from Authentication Service.
     * Matches TokenValidationResponse from authentication-service.
     */
    @Data
    private static class ValidationResponse {
        private boolean valid;
        private String userId;
        private String username;
        private List<String> roles;
        private String message;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime validatedAt;
    }
}
