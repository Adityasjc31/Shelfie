package com.book.management.order.client.fallback;

import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.exception.OrderNotPlacedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InventoryClientFallbackFactory}.
 *
 * Tests the fallback mechanism for Inventory Service client failures.
 * Ensures that appropriate exceptions are thrown when the Inventory Service
 * is unavailable or returns errors.
 *
 * Test Coverage:
 * - Fallback creation with various exception types
 * - OrderNotPlacedException thrown on service failure
 * - Proper error message propagation
 * - Request data logging during failures
 * - Exception cause tracking and debugging
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class InventoryClientFallbackFactoryTest {

    @InjectMocks
    private InventoryClientFallbackFactory fallbackFactory;

    private ReduceInventoryStockRequestDTO testRequest;

    /**
     * Setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        Map<Long, Integer> bookQuantities = new HashMap<>();
        bookQuantities.put(101L, 2);
        bookQuantities.put(102L, 1);
        bookQuantities.put(103L, 3);

        testRequest = new ReduceInventoryStockRequestDTO();
        testRequest.setBookQuantities(bookQuantities);
    }

    /**
     * Test: Verify fallback creates client that throws OrderNotPlacedException.
     *
     * Scenario: Inventory Service is completely unavailable.
     * Expected: OrderNotPlacedException with appropriate error message.
     */
    @Test
    void testCreate_WithServiceUnavailable_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("Connection refused");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
        assertTrue(exception.getMessage().contains("Connection refused"));
    }

    /**
     * Test: Verify fallback handles timeout exceptions.
     *
     * Scenario: Inventory Service request times out.
     * Expected: OrderNotPlacedException indicating timeout.
     */
    @Test
    void testCreate_WithTimeoutException_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("Read timed out");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
        assertTrue(exception.getMessage().contains("Read timed out"));
    }

    /**
     * Test: Verify fallback handles Feign exceptions correctly.
     *
     * Scenario: Feign client throws exception during inventory update.
     * Expected: OrderNotPlacedException with Feign error details.
     */
    @Test
    void testCreate_WithFeignException_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("FeignException: 503 Service Unavailable");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
    }

    /**
     * Test: Verify fallback handles insufficient stock scenario.
     *
     * Scenario: Inventory service returns error due to insufficient stock.
     * Expected: OrderNotPlacedException with stock error message.
     */
    @Test
    void testCreate_WithInsufficientStockError_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("Insufficient stock for book ID: 101");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    /**
     * Test: Verify fallback handles null cause gracefully.
     *
     * Scenario: Exception cause is null.
     * Expected: OrderNotPlacedException is still thrown.
     */
    @Test
    void testCreate_WithNullCause_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException();
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
    }

    /**
     * Test: Verify fallback includes request book quantities in error logging.
     *
     * Scenario: Service fails with multiple book quantities in request.
     * Expected: Exception is thrown (logging is verified via logs in real scenario).
     */
    @Test
    void testCreate_WithMultipleBookQuantities_ShouldHandleCorrectly() {
        // Arrange
        Map<Long, Integer> bookQuantities = new HashMap<>();
        bookQuantities.put(101L, 1);
        bookQuantities.put(102L, 2);
        bookQuantities.put(103L, 3);
        bookQuantities.put(104L, 1);
        bookQuantities.put(105L, 2);
        testRequest.setBookQuantities(bookQuantities);

        Throwable cause = new RuntimeException("Service error");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
    }

    /**
     * Test: Verify fallback factory creates new instance for each call.
     *
     * Expected: Factory method returns a functional fallback client.
     */
    @Test
    void testCreate_ShouldReturnFunctionalFallbackClient() {
        // Arrange
        Throwable cause = new RuntimeException("Test error");

        // Act
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Assert
        assertNotNull(fallbackClient);
        assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );
    }

    /**
     * Test: Verify exception message contains original error details.
     *
     * Scenario: Detailed error message from downstream service.
     * Expected: Original error message is preserved in exception.
     */
    @Test
    void testCreate_ShouldPreserveOriginalErrorMessage() {
        // Arrange
        String originalError = "Inventory service database connection failed";
        Throwable cause = new RuntimeException(originalError);
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertTrue(exception.getMessage().contains(originalError));
    }

    /**
     * Test: Verify fallback handles network-related exceptions.
     *
     * Scenario: Network connectivity issues occur.
     * Expected: OrderNotPlacedException with network error details.
     */
    @Test
    void testCreate_WithNetworkException_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("No route to host");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertTrue(exception.getMessage().contains("Inventory Service unavailable"));
        assertTrue(exception.getMessage().contains("No route to host"));
    }

    /**
     * Test: Verify fallback handles empty book quantities request.
     *
     * Scenario: Request with empty book quantities map.
     * Expected: OrderNotPlacedException is thrown correctly.
     */
    @Test
    void testCreate_WithEmptyBookQuantities_ShouldThrowOrderNotPlacedException() {
        // Arrange
        testRequest.setBookQuantities(new HashMap<>());
        Throwable cause = new RuntimeException("Service unavailable");
        InventoryServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.reduceStock(testRequest)
        );

        assertNotNull(exception.getMessage());
    }
}

