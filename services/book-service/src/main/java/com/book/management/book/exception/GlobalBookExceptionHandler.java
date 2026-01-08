package com.book.management.book.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the Book Management module.
 * Centralizes exception handling across all book-related controllers.
 * 
 * Exception Handling Strategy:
 * ---------------------------
 * This handler catches exceptions thrown by BookService and BookController,
 * transforms them into appropriate HTTP responses, and logs them for debugging.
 * 
 * Integration Consideration:
 * -------------------------
 * When Review module calls Book module methods (e.g., getBookById), if a book
 * is not found, this handler will NOT catch it because the exception occurs
 * in the Review module's context. The Review module's exception handler will
 * need to catch and handle Book module exceptions appropriately.
 * 
 * Design Principles:
 * -----------------
 * 1. Centralized Error Handling: All book exceptions handled in one place
 * 2. Consistent Response Format: Uses ErrorResponse for all errors
 * 3. Appropriate HTTP Status Codes: Maps exceptions to correct status codes
 * 4. Comprehensive Logging: All exceptions logged for troubleshooting
 * 
 * Exception Hierarchy:
 * -------------------
 * - BookNotFoundException → 404 Not Found
 * - DuplicateBookException → 409 Conflict
 * - InvalidBookDataException → 400 Bad Request
 * - Generic Exception → 500 Internal Server Error
 * 
 * @author Shashwat Srivastava + Aditya Srivastava
 * @version 2.0 - Complete implementation with integration support
 * @since 2024-12-16
 */
@RestControllerAdvice(basePackages = "com.book.management.book")
@Slf4j
public class GlobalBookExceptionHandler {

    /**
     * Catches BookNotFoundException thrown by the service layer.
     * Returns a 404 Status which the FeignErrorDecoder maps to OrderNotPlacedException.
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotFoundException(BookNotFoundException ex) {
        log.error("Mapping exception to 404 response: {}", ex.getMessage());

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", HttpStatus.NOT_FOUND.value());
        errorBody.put("error", "Not Found");
        errorBody.put("message", ex.getMessage()); // The key used by your Feign Decoder

        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateBookException.
     * 
     * Triggered when:
     * - Attempting to create a book with an ID that already exists
     * - Violating unique constraints on book data
     * 
     * HTTP Status: 409 Conflict
     * 
     * @param ex the DuplicateBookException
     * @param request the web request context
     * @return ResponseEntity with error details and 409 status
     */
    @ExceptionHandler(DuplicateBookException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateBookException(
            DuplicateBookException ex, WebRequest request) {
        
        log.error("DuplicateBookException occurred: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles InvalidBookDataException.
     * 
     * Triggered when:
     * - Required book fields are missing or blank
     * - Book data violates business rules (e.g., negative price)
     * - Invalid category or author IDs
     * 
     * HTTP Status: 400 Bad Request
     * 
     * @param ex the InvalidBookDataException
     * @param request the web request context
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(InvalidBookDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookDataException(
            InvalidBookDataException ex, WebRequest request) {
        
        log.error("InvalidBookDataException occurred: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles InvalidCategoryException.
     * 
     * Triggered when:
     * - An invalid or non-existent category ID is provided
     * - Category ID format is incorrect
     * 
     * HTTP Status: 400 Bad Request
     * 
     * @param ex the InvalidCategoryException
     * @param request the web request context
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCategoryException(
            InvalidCategoryException ex, WebRequest request) {
        
        log.error("InvalidCategoryException occurred: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles all other uncaught exceptions.
     * 
     * This is the catch-all handler for any unexpected exceptions that are
     * not specifically handled by other methods in this class.
     * 
     * HTTP Status: 500 Internal Server Error
     * 
     * Important: This handler logs the full stack trace for debugging but
     * returns a generic error message to avoid exposing internal details.
     * 
     * @param ex the generic exception
     * @param request the web request context
     * @return ResponseEntity with generic error details and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred in Book module: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred while processing the book request. Please try again later.",
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}