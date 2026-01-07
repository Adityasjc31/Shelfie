package com.book.management.order.exception;

import com.book.management.order.dto.responsedto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GlobalOrderExceptionHandler}.
 *
 * Tests the centralized exception handling for the Order Service.
 * Validates proper HTTP status codes, error messages, and response structure
 * for various business logic and communication failures.
 *
 * Test Coverage:
 * - OrderNotPlacedException handling (400 Bad Request)
 * - OrderInvalidStatusTransitionException handling (422 Unprocessable Content)
 * - OrderCancellationNotAllowedException handling (409 Conflict)
 * - OrderNotFoundException handling (404 Not Found)
 * - Error response DTO structure validation
 * - Request path inclusion in error responses
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class GlobalOrderExceptionHandlerTest {

    @InjectMocks
    private GlobalOrderExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    private static final String TEST_REQUEST_URI = "/api/v1/order/place";

    /**
     * Setup common mock behavior before each test.
     */
    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_REQUEST_URI);
    }

    /**
     * Test: Verify OrderNotPlacedException is handled with 400 Bad Request.
     *
     * Scenario: Order placement fails due to inventory or catalog issues.
     * Expected: 400 status code with appropriate error response.
     */
    @Test
    void testHandleOrderNotPlaced_ShouldReturn400BadRequest() {
        // Arrange
        String errorMessage = "Insufficient stock for book ID: 101";
        OrderNotPlacedException exception = new OrderNotPlacedException(errorMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleOrderNotPlaced(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("ORDER_PLACEMENT_FAILED", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_REQUEST_URI, response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    /**
     * Test: Verify OrderInvalidStatusTransitionException is handled with 422 Unprocessable Content.
     *
     * Scenario: Invalid status transition attempted (e.g., PENDING to DELIVERED).
     * Expected: 422 status code with appropriate error response.
     */
    @Test
    void testHandleInvalidTransition_ShouldReturn422UnprocessableContent() {
        // Arrange
        String errorMessage = "Cannot transition from PENDING to DELIVERED";
        OrderInvalidStatusTransitionException exception =
            new OrderInvalidStatusTransitionException(errorMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidTransition(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("INVALID_ORDER_STATUS_TRANSITION", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_REQUEST_URI, response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    /**
     * Test: Verify OrderCancellationNotAllowedException is handled with 409 Conflict.
     *
     * Scenario: Attempt to cancel order that is already delivered.
     * Expected: 409 status code with appropriate error response.
     */
    @Test
    void testHandleCancellationDenied_ShouldReturn409Conflict() {
        // Arrange
        String errorMessage = "Cannot cancel order that is already DELIVERED";
        OrderCancellationNotAllowedException exception =
            new OrderCancellationNotAllowedException(errorMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleCancellationDenied(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("ORDER_CANCELLATION_DENIED", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_REQUEST_URI, response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    /**
     * Test: Verify OrderNotFoundException is handled with 404 Not Found.
     *
     * Scenario: Order with specified ID does not exist.
     * Expected: 404 status code with appropriate error response.
     */
    @Test
    void testHandleOrderNotFound_ShouldReturn404NotFound() {
        // Arrange
        String errorMessage = "Order not found with ID: 12345";
        OrderNotFoundException exception = new OrderNotFoundException(errorMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleOrderNotFound(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("ORDER_NOT_FOUND", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(TEST_REQUEST_URI, response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    /**
     * Test: Verify error response includes correct request path for different endpoints.
     *
     * Scenario: Exception occurs on different API endpoint.
     * Expected: Correct request path is included in error response.
     */
    @Test
    void testHandleException_ShouldIncludeCorrectRequestPath() {
        // Arrange
        String differentUri = "/api/v1/order/cancel/123";
        when(request.getRequestURI()).thenReturn(differentUri);
        OrderNotFoundException exception = new OrderNotFoundException("Order not found");

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleOrderNotFound(exception, request);

        // Assert
        assertEquals(differentUri, response.getBody().getPath());
    }

    /**
     * Test: Verify OrderNotPlacedException with detailed error message.
     *
     * Scenario: Complex error message from downstream service.
     * Expected: Full error message is preserved in response.
     */
    @Test
    void testHandleOrderNotPlaced_WithDetailedMessage_ShouldPreserveMessage() {
        // Arrange
        String detailedMessage = "Book Service unavailable: Connection timeout after 5000ms";
        OrderNotPlacedException exception = new OrderNotPlacedException(detailedMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleOrderNotPlaced(exception, request);

        // Assert
        assertEquals(detailedMessage, response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test: Verify invalid transition exception with state machine violation details.
     *
     * Scenario: Detailed state transition error message.
     * Expected: Complete error details are included in response.
     */
    @Test
    void testHandleInvalidTransition_WithStateDetails_ShouldIncludeDetails() {
        // Arrange
        String stateMessage = "Invalid transition: Current=DELIVERED, Requested=SHIPPED, Allowed=[]";
        OrderInvalidStatusTransitionException exception =
            new OrderInvalidStatusTransitionException(stateMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidTransition(exception, request);

        // Assert
        assertEquals(stateMessage, response.getBody().getMessage());
        assertEquals(422, response.getBody().getStatus());
    }

    /**
     * Test: Verify cancellation denied with business rule violation details.
     *
     * Scenario: Cancellation blocked with detailed reason.
     * Expected: Business rule violation details are preserved.
     */
    @Test
    void testHandleCancellationDenied_WithBusinessRuleDetails_ShouldIncludeDetails() {
        // Arrange
        String ruleMessage = "Cancellation not allowed: Order status is DELIVERED (Final State)";
        OrderCancellationNotAllowedException exception =
            new OrderCancellationNotAllowedException(ruleMessage);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleCancellationDenied(exception, request);

        // Assert
        assertEquals(ruleMessage, response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
    }

    /**
     * Test: Verify order not found with specific order ID in message.
     *
     * Scenario: Order lookup fails for specific ID.
     * Expected: Order ID is included in error message.
     */
    @Test
    void testHandleOrderNotFound_WithOrderId_ShouldIncludeOrderId() {
        // Arrange
        Long orderId = 99999L;
        String message = "Order not found with ID: " + orderId;
        OrderNotFoundException exception = new OrderNotFoundException(message);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleOrderNotFound(exception, request);

        // Assert
        assertTrue(response.getBody().getMessage().contains(orderId.toString()));
        assertEquals(404, response.getBody().getStatus());
    }

    /**
     * Test: Verify all exception handlers set correct HTTP status in response body.
     *
     * Scenario: Verify consistency between ResponseEntity status and body status.
     * Expected: Status codes match between response entity and body.
     */
    @Test
    void testAllHandlers_ShouldHaveConsistentStatusCodes() {
        // Test OrderNotPlacedException
        ResponseEntity<ErrorResponseDTO> response1 = exceptionHandler.handleOrderNotPlaced(
            new OrderNotPlacedException("test"), request);
        assertEquals(response1.getStatusCode().value(), response1.getBody().getStatus());

        // Test OrderInvalidStatusTransitionException
        ResponseEntity<ErrorResponseDTO> response2 = exceptionHandler.handleInvalidTransition(
            new OrderInvalidStatusTransitionException("test"), request);
        assertEquals(response2.getStatusCode().value(), response2.getBody().getStatus());

        // Test OrderCancellationNotAllowedException
        ResponseEntity<ErrorResponseDTO> response3 = exceptionHandler.handleCancellationDenied(
            new OrderCancellationNotAllowedException("test"), request);
        assertEquals(response3.getStatusCode().value(), response3.getBody().getStatus());

        // Test OrderNotFoundException
        ResponseEntity<ErrorResponseDTO> response4 = exceptionHandler.handleOrderNotFound(
            new OrderNotFoundException("test"), request);
        assertEquals(response4.getStatusCode().value(), response4.getBody().getStatus());
    }

    /**
     * Test: Verify error response DTOs have all required fields populated.
     *
     * Scenario: Check that no required fields are null in error responses.
     * Expected: All fields (timestamp, status, error, message, path) are non-null.
     */
    @Test
    void testErrorResponseDTO_ShouldHaveAllRequiredFields() {
        // Arrange
        OrderNotFoundException exception = new OrderNotFoundException("Test error");

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleOrderNotFound(exception, request);
        ErrorResponseDTO body = response.getBody();

        // Assert
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertNotNull(body.getStatus());
        assertNotNull(body.getError());
        assertNotNull(body.getMessage());
        assertNotNull(body.getPath());
    }
}

