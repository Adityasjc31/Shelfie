package com.book.management.api_gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.UUID;

/**
 * Custom Gateway Filter for request/response logging.
 * 
 * Logs:
 * - Request Method, Path, Headers
 * - Request ID (X-Request-ID)
 * - Routing destination
 * - Response Status
 * - Execution time
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            Instant startTime = Instant.now();
            
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString();
            
            // Log incoming request
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("→ INCOMING REQUEST");
            log.info("→ Request ID: {}", requestId);
            log.info("→ Method: {}", request.getMethod());
            log.info("→ Path: {}", request.getPath());
            log.info("→ URI: {}", request.getURI());
            log.info("→ Remote Address: {}", request.getRemoteAddress());
            
            // Log headers (excluding sensitive ones)
            HttpHeaders headers = request.getHeaders();
            log.info("→ Headers:");
            headers.forEach((name, values) -> {
                if (!name.equalsIgnoreCase("Authorization") && 
                    !name.equalsIgnoreCase("Cookie")) {
                    log.info("→   {}: {}", name, values);
                }
            });
            
            // Add request ID to headers
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Request-ID", requestId)
                    .build();
            
            // Continue filter chain and log response
            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .doOnSuccess(aVoid -> {
                        ServerHttpResponse response = exchange.getResponse();
                        long executionTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
                        
                        log.info("← OUTGOING RESPONSE");
                        log.info("← Request ID: {}", requestId);
                        log.info("← Status Code: {}", response.getStatusCode());
                        log.info("← Execution Time: {} ms", executionTime);
                        
                        // Warn for slow requests
                        if (executionTime > 3000) {
                            log.warn("⚠ SLOW REQUEST: {} took {} ms", request.getPath(), executionTime);
                        }
                        
                        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    })
                    .doOnError(error -> {
                        long executionTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
                        
                        log.error("✗ REQUEST FAILED");
                        log.error("✗ Request ID: {}", requestId);
                        log.error("✗ Path: {}", request.getPath());
                        log.error("✗ Error: {}", error.getMessage());
                        log.error("✗ Execution Time: {} ms", executionTime);
                        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    });
        };
    }

    /**
     * Configuration class for LoggingFilter.
     * Can be extended with configuration properties if needed.
     */
    @Data
    public static class Config {
        private boolean logHeaders = true;
        private boolean logRequestBody = false;
        private boolean logResponseBody = false;
    }
}
