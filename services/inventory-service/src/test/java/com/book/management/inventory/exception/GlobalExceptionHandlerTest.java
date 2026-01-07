package com.book.management.inventory.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests all exception handler methods for proper HTTP responses.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-08
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/inventory");
    }

    @Test
    void handleInventoryNotFoundException_ReturnsNotFound() {
        // Arrange
        InventoryNotFoundException ex = new InventoryNotFoundException(1L);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInventoryNotFoundException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Inventory not found"));
    }

    @Test
    void handleResourceNotFoundException_ReturnsNotFound() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Book not found", 404);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleResourceNotFoundException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleServiceUnavailableException_ReturnsServiceUnavailable() {
        // Arrange
        ServiceUnavailableException ex = new ServiceUnavailableException("Service down", 503);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleServiceUnavailableException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("unavailable"));
    }

    @Test
    void handleInventoryAlreadyExistsException_ReturnsConflict() {
        // Arrange
        InventoryAlreadyExistsException ex = new InventoryAlreadyExistsException(100L);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInventoryAlreadyExistsException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("already exists"));
    }

    @Test
    void handleInsufficientStockException_ReturnsBadRequest() {
        // Arrange
        InsufficientStockException ex = new InsufficientStockException(100L, 5, 10);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInsufficientStockException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Insufficient stock"));
    }

    @Test
    void handleInvalidInventoryOperationException_ReturnsBadRequest() {
        // Arrange
        InvalidInventoryOperationException ex = new InvalidInventoryOperationException(
                "Adjustment would result in negative quantity");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInvalidInventoryOperationException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleValidationExceptions_ReturnsBadRequestWithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("inventoryCreateDTO", "quantity", "must not be null");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response = exceptionHandler
                .handleValidationExceptions(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors().containsKey("quantity"));
    }

    @Test
    void handleGlobalException_ReturnsInternalServerError() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler.handleGlobalException(ex,
                webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void handleInventoryNotFoundException_WithFieldAndValue() {
        // Arrange
        InventoryNotFoundException ex = new InventoryNotFoundException("bookId", 100L);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInventoryNotFoundException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("bookId"));
        assertTrue(response.getBody().getMessage().contains("100"));
    }

    @Test
    void handleInsufficientStockException_WithMessage() {
        // Arrange
        InsufficientStockException ex = new InsufficientStockException("Custom message");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInsufficientStockException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom message", response.getBody().getMessage());
    }
}
