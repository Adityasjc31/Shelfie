package com.book.management.api_gateway.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Fallback Controller for Circuit Breaker.
 * 
 * Provides fallback responses when downstream services are unavailable
 * or circuit breaker is open.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    /**
     * Fallback for Authentication Service.
     */
    @GetMapping("/auth")
    public ResponseEntity<FallbackResponse> authServiceFallback() {
        log.warn("⚠ Authentication service fallback triggered");
        
        FallbackResponse response = new FallbackResponse(
                "Authentication Service Unavailable",
                "The authentication service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "authentication-service",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Fallback for Book Service.
     */
    @GetMapping("/books")
    public ResponseEntity<FallbackResponse> bookServiceFallback() {
        log.warn("⚠ Book service fallback triggered");
        
        FallbackResponse response = new FallbackResponse(
                "Book Service Unavailable",
                "The book service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "book-service",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Fallback for Inventory Service.
     */
    @GetMapping("/inventory")
    public ResponseEntity<FallbackResponse> inventoryServiceFallback() {
        log.warn("⚠ Inventory service fallback triggered");
        
        FallbackResponse response = new FallbackResponse(
                "Inventory Service Unavailable",
                "The inventory service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "inventory-service",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Fallback for Order Service.
     */
    @GetMapping("/orders")
    public ResponseEntity<FallbackResponse> orderServiceFallback() {
        log.warn("⚠ Order service fallback triggered");
        
        FallbackResponse response = new FallbackResponse(
                "Order Service Unavailable",
                "The order service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "order-service",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Fallback for Review Service.
     */
    @GetMapping("/reviews")
    public ResponseEntity<FallbackResponse> reviewServiceFallback() {
        log.warn("⚠ Review service fallback triggered");
        
        FallbackResponse response = new FallbackResponse(
                "Review Service Unavailable",
                "The review service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "review-service",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Fallback for User Service.
     */
    @GetMapping("/users")
    public ResponseEntity<FallbackResponse> userServiceFallback() {
        log.warn("⚠ User service fallback triggered");
        
        FallbackResponse response = new FallbackResponse(
                "User Service Unavailable",
                "The user service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "user-service",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Fallback response structure.
     */
    @Data
    @AllArgsConstructor
    public static class FallbackResponse {
        private String error;
        private String message;
        private int status;
        private String service;
        private LocalDateTime timestamp;
    }
}
