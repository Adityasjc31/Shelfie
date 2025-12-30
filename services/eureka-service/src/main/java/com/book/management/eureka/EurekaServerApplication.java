package com.bookstore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server Application.
 * 
 * This is the Service Registry for the Digital Bookstore microservices ecosystem.
 * All microservices (including API Gateway) register with this Eureka Server.
 * 
 * Features:
 * - Service registration and discovery
 * - Health monitoring of registered services
 * - Load balancing support
 * - Self-preservation mode
 * - Web dashboard at http://localhost:8761
 * 
 * Registered Services:
 * - api-gateway
 * - authentication-service
 * - book-service
 * - inventory-service
 * - order-service
 * - review-service
 * - user-service
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
