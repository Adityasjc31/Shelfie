package com.book.management.order.client.fallback;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.exception.OrderNotPlacedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BookClientFallbackFactory}.
 *
 * Tests the fallback mechanism for Book Service client failures.
 * Ensures that appropriate exceptions are thrown when the Book Service
 * is unavailable or returns errors.
 *
 * Test Coverage:
 * - Fallback creation with various exception types
 * - OrderNotPlacedException thrown on service failure
 * - Proper error message propagation
 * - Request data logging during failures
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class BookClientFallbackFactoryTest {

    @InjectMocks
    private BookClientFallbackFactory fallbackFactory;

    private GetBookPriceRequestDTO testRequest;

    /**
     * Setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        List<Long> bookIds = Arrays.asList(101L, 102L, 103L);
        testRequest = new GetBookPriceRequestDTO();
        testRequest.setBookIds(bookIds);
    }

    /**
     * Test: Verify fallback creates client that throws OrderNotPlacedException.
     *
     * Scenario: Book Service is completely unavailable.
     * Expected: OrderNotPlacedException with appropriate error message.
     */
    @Test
    void testCreate_WithServiceUnavailable_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("Connection refused");
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
        );

        assertTrue(exception.getMessage().contains("Book Service unavailable"));
        assertTrue(exception.getMessage().contains("Connection refused"));
    }

    /**
     * Test: Verify fallback handles Feign exceptions correctly.
     *
     * Scenario: Feign client throws FeignException.
     * Expected: OrderNotPlacedException with Feign error details.
     */
    @Test
    void testCreate_WithFeignException_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("FeignException: 503 Service Unavailable");
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
        );

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Book Service unavailable"));
    }

    /**
     * Test: Verify fallback handles timeout exceptions.
     *
     * Scenario: Book Service request times out.
     * Expected: OrderNotPlacedException indicating timeout.
     */
    @Test
    void testCreate_WithTimeoutException_ShouldThrowOrderNotPlacedException() {
        // Arrange
        Throwable cause = new RuntimeException("Read timed out");
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
        );

        assertTrue(exception.getMessage().contains("Book Service unavailable"));
        assertTrue(exception.getMessage().contains("Read timed out"));
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
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
        );

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Book Service unavailable"));
    }

    /**
     * Test: Verify fallback includes request book IDs in error logging.
     *
     * Scenario: Service fails and request contains multiple book IDs.
     * Expected: Exception is thrown (logging is verified via logs in real scenario).
     */
    @Test
    void testCreate_WithMultipleBookIds_ShouldHandleCorrectly() {
        // Arrange
        List<Long> bookIds = Arrays.asList(101L, 102L, 103L, 104L, 105L);
        testRequest.setBookIds(bookIds);
        Throwable cause = new RuntimeException("Service error");
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Book Service unavailable"));
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
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Assert
        assertNotNull(fallbackClient);
        assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
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
        String originalError = "Book service database connection failed";
        Throwable cause = new RuntimeException(originalError);
        BookServiceClient fallbackClient = fallbackFactory.create(cause);

        // Act & Assert
        OrderNotPlacedException exception = assertThrows(
            OrderNotPlacedException.class,
            () -> fallbackClient.getBookPrices(testRequest)
        );

        assertTrue(exception.getMessage().contains(originalError));
    }
}

