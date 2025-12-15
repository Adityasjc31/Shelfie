package com.db.ms.review_rating.exception;

/**
 * Exception thrown when moderation action is invalid.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
public class InvalidModerationException extends RuntimeException {
    
    public InvalidModerationException(String message) {
        super(message);
    }
}