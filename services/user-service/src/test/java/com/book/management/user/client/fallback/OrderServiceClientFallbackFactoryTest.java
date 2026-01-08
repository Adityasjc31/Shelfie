// ============================================================================
// FILE: OrderServiceClientFallbackFactoryTest.java
// ============================================================================
package com.book.management.user.client.fallback;

import com.book.management.user.client.OrderServiceClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderServiceClientFallbackFactory.
 * Tests fallback behavior when Order Service is unavailable.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@DisplayName("OrderServiceClientFallbackFactory Unit Tests")
class OrderServiceClientFallbackFactoryTest {

    private OrderServiceClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new OrderServiceClientFallbackFactory();
    }

    @Test
    @DisplayName("Should create fallback client instance")
    void testCreate_ReturnsFallbackClient() {
        // Given
        RuntimeException cause = new RuntimeException("Service unavailable");

        // When
        OrderServiceClient fallbackClient = fallbackFactory.create(cause);

        // Then
        assertThat(fallbackClient).isNotNull();
    }

    @Test
    @DisplayName("Should return no content for deleteOrdersByUserId fallback")
    void testDeleteOrdersByUserId_Fallback() {
        // Given
        RuntimeException cause = new RuntimeException("Connection refused");
        OrderServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteOrdersByUserId(123L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should handle FeignException in fallback")
    void testDeleteOrdersByUserId_FeignExceptionFallback() {
        // Given
        Request request = Request.create(
                Request.HttpMethod.DELETE,
                "http://order-service/api/v1/order/deleteByUser/123",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate()
        );
        FeignException feignException = new FeignException.ServiceUnavailable(
                "Service Unavailable", request, null, null);

        OrderServiceClient fallbackClient = fallbackFactory.create(feignException);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteOrdersByUserId(123L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should handle timeout exception in fallback")
    void testDeleteOrdersByUserId_TimeoutFallback() {
        // Given
        RuntimeException cause = new RuntimeException("Read timed out");
        OrderServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteOrdersByUserId(456L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should gracefully handle null user ID")
    void testDeleteOrdersByUserId_NullUserId() {
        // Given
        RuntimeException cause = new RuntimeException("Service down");
        OrderServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteOrdersByUserId(null);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
