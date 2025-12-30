package com.book.management.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Main Application class for Common API Gateway.
 * 
 * This is the single entry point for all client requests in the
 * Digital Bookstore microservices ecosystem.
 * 
 * Gateway Routes:
 * - /books/**        → book-service
 * - /inventory/**    → inventory-service
 * - /orders/**       → order-service
 * - /reviews/**      → review-service
 * - /users/**        → user-service
 * - /auth/**         → authentication-service
 * 
 * Features:
 * - Request routing with load balancing
 * - AOP-based comprehensive logging
 * - Authentication integration
 * - Rate limiting
 * - Circuit breaker patterns
 * - CORS configuration
 * - Centralized error handling
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
