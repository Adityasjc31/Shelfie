package com.book.management.api_gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Unit tests for FallbackController.
 * Tests circuit breaker fallback responses for all downstream services.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@WebFluxTest(FallbackController.class)
public class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testAuthServiceFallback() {
        webTestClient.get()
                .uri("/fallback/auth")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Authentication Service Unavailable")
                .jsonPath("$.service").isEqualTo("authentication-service")
                .jsonPath("$.status").isEqualTo(503);
    }

    @Test
    void testBookServiceFallback() {
        webTestClient.get()
                .uri("/fallback/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Book Service Unavailable")
                .jsonPath("$.service").isEqualTo("book-service")
                .jsonPath("$.status").isEqualTo(503);
    }

    @Test
    void testInventoryServiceFallback() {
        webTestClient.get()
                .uri("/fallback/inventory")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Inventory Service Unavailable")
                .jsonPath("$.service").isEqualTo("inventory-service")
                .jsonPath("$.status").isEqualTo(503);
    }

    @Test
    void testOrderServiceFallback() {
        webTestClient.get()
                .uri("/fallback/orders")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Order Service Unavailable")
                .jsonPath("$.service").isEqualTo("order-service")
                .jsonPath("$.status").isEqualTo(503);
    }

    @Test
    void testReviewServiceFallback() {
        webTestClient.get()
                .uri("/fallback/reviews")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Review Service Unavailable")
                .jsonPath("$.service").isEqualTo("review-service")
                .jsonPath("$.status").isEqualTo(503);
    }

    @Test
    void testUserServiceFallback() {
        webTestClient.get()
                .uri("/fallback/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("User Service Unavailable")
                .jsonPath("$.service").isEqualTo("user-service")
                .jsonPath("$.status").isEqualTo(503);
    }

    @Test
    void testFallbackResponseContainsTimestamp() {
        webTestClient.get()
                .uri("/fallback/auth")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.message").exists();
    }
}
