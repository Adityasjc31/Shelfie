package com.book.management.review_rating.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Application Configuration for Review Service.
 * 
 * Microservices Configuration:
 * 1. FeignClient: Declarative REST clients for inter-service communication
 * 2. AOP: Enable aspect-oriented programming for logging
 * 3. Circuit Breaker: Handle service unavailability gracefully
 * 
 * Note: RestTemplate has been removed in favor of FeignClient.
 * FeignClient provides:
 * - Declarative interface-based REST calls
 * - Integration with Eureka for service discovery
 * - Built-in circuit breaker support
 * - Automatic load balancing
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    // RestTemplate bean has been removed.
    // Inter-service communication now uses FeignClient (BookServiceClient)
    // which is auto-configured by @EnableFeignClients in the main application.
}
