package com.book.management.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server Application - Centralized Configuration Management
 * 
 * Provides externalized configuration for all microservices in the
 * Digital Bookstore ecosystem.
 * 
 * Features:
 * - Centralized configuration storage
 * - Environment-specific configs (dev, staging, prod)
 * - Dynamic configuration refresh
 * - Git-backed or native file system storage
 * - Version control for configurations
 * 
 * Configuration Sources Priority:
 * 1. Application-specific config: {application-name}.properties
 * 2. Profile-specific config: {application-name}-{profile}.properties
 * 3. Default config: application.properties
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-30
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
