package com.book.management.inventory.filter;

import com.book.management.inventory.config.GatewaySecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter to validate that requests originate from the API Gateway.
 * 
 * This filter checks for a secret header that only the gateway knows.
 * Requests without this header (direct access attempts) are rejected
 * with a 403 Forbidden response.
 * 
 * Public endpoints (e.g., actuator health checks) bypass this validation.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1) // Execute early in the filter chain
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private final GatewaySecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip validation if gateway security is disabled
        if (!securityProperties.isEnabled()) {
            log.debug("Gateway security disabled - allowing request to {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Skip validation for public endpoints (health checks, etc.)
        if (isPublicEndpoint(requestPath)) {
            log.debug("Public endpoint accessed: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Validate gateway secret header
        String gatewaySecret = request.getHeader(securityProperties.getHeaderName());

        if (gatewaySecret == null || gatewaySecret.isEmpty()) {
            log.warn("🚫 Direct access attempt blocked - Missing gateway secret header: {} from IP: {}",
                    requestPath, request.getRemoteAddr());
            sendForbiddenResponse(response, "Direct access not allowed. Please use the API Gateway.");
            return;
        }

        if (!gatewaySecret.equals(securityProperties.getExpectedToken())) {
            log.warn("🚫 Invalid gateway secret - Path: {}, IP: {}", requestPath, request.getRemoteAddr());
            sendForbiddenResponse(response, "Invalid gateway credentials.");
            return;
        }

        // Valid gateway request
        log.debug("✓ Gateway authentication successful for {}", requestPath);
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the request path is a public endpoint.
     * 
     * @param path request URI
     * @return true if public endpoint
     */
    private boolean isPublicEndpoint(String path) {
        return securityProperties.getPublicPaths().stream()
                .anyMatch(path::startsWith);
    }

    /**
     * Sends a 403 Forbidden response with error details.
     * 
     * @param response HttpServletResponse
     * @param message  error message
     * @throws IOException if writing response fails
     */
    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", "Direct access not permitted");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
