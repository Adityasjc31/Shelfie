package com.book.management.book.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response structure for Book Management Module.
 * 
 * This class provides a consistent error response format across all
 * book-related API endpoints. It follows REST API best practices
 * for error handling.
 * 
 * Error Response Structure:
 * ------------------------
 * {
 *   "status": 404,
 *   "message": "Book not found with id: 123",
 *   "path": "uri=/api/v1/book/getById/123",
 *   "timestamp": "2024-12-16T10:30:00"
 * }
 * 
 * Design Benefits:
 * ---------------
 * 1. Consistency: All errors follow the same structure
 * 2. Debuggability: Includes timestamp and path for troubleshooting
 * 3. Client-Friendly: Provides clear error messages
 * 4. HTTP Compliance: Maps to standard HTTP status codes
 * 
 * Usage in Exception Handlers:
 * ---------------------------
 * This class is used by GlobalBookExceptionHandler to format
 * all exception responses consistently.
 * 
 * @author Shashwat Srivastava + Aditya Srivastava
 * @version 2.0 - Enhanced documentation
 * @since 2024-12-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * HTTP status code (e.g., 404, 400, 500).
     * Corresponds to the HTTP status returned in the response.
     */
    private int status;
    
    /**
     * Human-readable error message.
     * Should be clear and actionable for clients.
     */
    private String message;
    
    /**
     * Request path where the error occurred.
     * Useful for debugging and logging.
     */
    private String path;
    
    /**
     * Timestamp when the error occurred.
     * Helps with debugging and correlating logs.
     */
    private LocalDateTime timestamp;
}