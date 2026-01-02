package com.book.management.order.filter;

import com.book.management.order.config.GatewaySecurityProperties;
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
 * Blocks direct access attempts with 403 Forbidden.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private final GatewaySecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if (!securityProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String gatewaySecret = request.getHeader(securityProperties.getHeaderName());

        if (gatewaySecret == null || gatewaySecret.isEmpty()) {
            log.warn("🚫 Direct access blocked: {} from IP: {}", requestPath, request.getRemoteAddr());
            sendForbiddenResponse(response, "Direct access not allowed. Please use the API Gateway.");
            return;
        }

        if (!gatewaySecret.equals(securityProperties.getExpectedToken())) {
            log.warn("🚫 Invalid gateway secret - Path: {}", requestPath);
            sendForbiddenResponse(response, "Invalid gateway credentials.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return securityProperties.getPublicPaths().stream().anyMatch(path::startsWith);
    }

    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        errorResponse.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
