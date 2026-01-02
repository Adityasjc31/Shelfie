package com.book.management.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Application class for Inventory Management Service.
 * 
 * This microservice handles all inventory-related operations including:
 * - Stock management
 * - Low stock alerts
 * - Inventory adjustments
 * - Bulk operations
 * 
 * Integrates with:
 * - Config Server for centralized configuration
 * - Eureka Server for service discovery
 * - API Gateway for routing and authentication
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}