package com.db.ms.review_rating.exception;

/**
 * Exception thrown for invalid review operations.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
public class InvalidReviewOperationException extends RuntimeException {
    
    public InvalidReviewOperationException(String message) {
        super(message);
    }
}