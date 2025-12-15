<<<<<<< HEAD:src/main/java/com/db/ms/Review_Rating/exception/ReviewGlobalExceptionHandler.java
package com.db.ms.review_rating.exception;

=======
package com.db.ms.inventory.exception;
>>>>>>> origin/InventoryManagementModule:src/main/java/com/db/ms/Inventory/exception/GlobalExceptionHandler.java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Review Management module.
 * Centralizes exception handling across all controllers.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@RestControllerAdvice
@Slf4j
public class ReviewGlobalExceptionHandler {

    /**
     * Handles ReviewNotFoundException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details and HTTP 404 status
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFoundException(
            ReviewNotFoundException ex, WebRequest request) {
        log.error("ReviewNotFoundException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles DuplicateReviewException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details and HTTP 409 status
     */
    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReviewException(
            DuplicateReviewException ex, WebRequest request) {
        log.error("DuplicateReviewException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles UnauthorizedReviewAccessException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details and HTTP 403 status
     */
    @ExceptionHandler(UnauthorizedReviewAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedReviewAccessException(
            UnauthorizedReviewAccessException ex, WebRequest request) {
        log.error("UnauthorizedReviewAccessException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handles InvalidReviewOperationException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details and HTTP 400 status
     */
    @ExceptionHandler(InvalidReviewOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReviewOperationException(
            InvalidReviewOperationException ex, WebRequest request) {
        log.error("InvalidReviewOperationException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles InvalidModerationException.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details and HTTP 400 status
     */
    @ExceptionHandler(InvalidModerationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidModerationException(
            InvalidModerationException ex, WebRequest request) {
        log.error("InvalidModerationException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation errors from @Valid annotations.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with validation error details and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles all other uncaught exceptions.
     * 
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Standard error response structure.
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String message;
        private String path;
        private LocalDateTime timestamp;
    }

    /**
     * Validation error response structure with field-specific errors.
     */
    @Data
    @AllArgsConstructor
    public static class ValidationErrorResponse {
        private int status;
        private String message;
        private String path;
        private LocalDateTime timestamp;
        private Map<String, String> errors;
    }
}