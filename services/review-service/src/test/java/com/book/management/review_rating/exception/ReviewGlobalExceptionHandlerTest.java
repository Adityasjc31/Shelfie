package com.book.management.review_rating.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Unit tests for ReviewGlobalExceptionHandler.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@ExtendWith(MockitoExtension.class)
class ReviewGlobalExceptionHandlerTest {

    private ReviewGlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ReviewGlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/reviews/test");
    }

    @Test
    @DisplayName("Should handle ReviewNotFoundException with 404 status")
    void handleReviewNotFoundException_Returns404() {
        ReviewNotFoundException exception = new ReviewNotFoundException(123L);

        ResponseEntity<ReviewGlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleReviewNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Review not found with ID: 123", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle DuplicateReviewException with 409 status")
    void handleDuplicateReviewException_Returns409() {
        DuplicateReviewException exception = new DuplicateReviewException(100L, 200L);

        ResponseEntity<ReviewGlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleDuplicateReviewException(exception, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("already reviewed"));
    }

    @Test
    @DisplayName("Should handle UnauthorizedReviewAccessException with 403 status")
    void handleUnauthorizedReviewAccessException_Returns403() {
        UnauthorizedReviewAccessException exception = new UnauthorizedReviewAccessException(100L, 200L);

        ResponseEntity<ReviewGlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleUnauthorizedReviewAccessException(exception, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("not authorized"));
    }

    @Test
    @DisplayName("Should handle InvalidReviewOperationException with 400 status")
    void handleInvalidReviewOperationException_Returns400() {
        InvalidReviewOperationException exception = new InvalidReviewOperationException("Invalid operation");

        ResponseEntity<ReviewGlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInvalidReviewOperationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid operation", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidModerationException with 400 status")
    void handleInvalidModerationException_Returns400() {
        InvalidModerationException exception = new InvalidModerationException("Rejection reason required");

        ResponseEntity<ReviewGlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleInvalidModerationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Rejection reason required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400 status")
    void handleValidationExceptions_Returns400WithErrors() {
        FieldError fieldError = new FieldError("reviewCreateDTO", "rating", "Rating is required");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ReviewGlobalExceptionHandler.ValidationErrorResponse> response = exceptionHandler
                .handleValidationExceptions(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().containsKey("rating"));
        assertEquals("Rating is required", response.getBody().getErrors().get("rating"));
    }

    @Test
    @DisplayName("Should handle general Exception with 500 status")
    void handleGlobalException_Returns500() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ReviewGlobalExceptionHandler.ErrorResponse> response = exceptionHandler
                .handleGlobalException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    @DisplayName("ValidationErrorResponse should have correct structure")
    void validationErrorResponse_HasCorrectStructure() {
        FieldError fieldError1 = new FieldError("dto", "field1", "Error 1");
        FieldError fieldError2 = new FieldError("dto", "field2", "Error 2");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ReviewGlobalExceptionHandler.ValidationErrorResponse> response = exceptionHandler
                .handleValidationExceptions(exception, webRequest);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("Error 1", response.getBody().getErrors().get("field1"));
        assertEquals("Error 2", response.getBody().getErrors().get("field2"));
    }
}
