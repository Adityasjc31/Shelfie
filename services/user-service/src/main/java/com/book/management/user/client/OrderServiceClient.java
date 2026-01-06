package com.book.management.user.client;

import com.book.management.user.client.fallback.OrderServiceClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

/**
 * Feign Client for inter-service communication with Order Service.
 * 
 * Enables the User Service to:
 * - Delete orders when a user is deleted (cascade)
 * 
 * Uses FallbackFactory for resilience and detailed error logging
 * when Order Service is unavailable.
 * 
 * Microservices Pattern: Service-to-Service Communication via FeignClient
 * - Declarative REST client
 * - Integrated with Eureka for service discovery
 * - Circuit breaker support via FallbackFactory
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-06
 */
@FeignClient(name = "order-service", fallbackFactory = OrderServiceClientFallbackFactory.class)
public interface OrderServiceClient {

    /**
     * Deletes all orders for a specific user.
     * 
     * @param userId the user ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/deleteByUser/{userId}")
    ResponseEntity<Void> deleteOrdersByUserId(@PathVariable("userId") Long userId);
}