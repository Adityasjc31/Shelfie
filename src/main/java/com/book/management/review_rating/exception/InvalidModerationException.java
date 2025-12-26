package com.book.management.review_rating.exception;

/**
 * Exception thrown when moderation action is invalid.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-15
 */
public class InvalidModerationException extends RuntimeException {
    
    public InvalidModerationException(String message) {
        super(message);
    }
}