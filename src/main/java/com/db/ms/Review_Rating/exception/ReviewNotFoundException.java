package com.db.ms.review_rating.exception;

/**
 * Exception thrown when a review is not found.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
public class ReviewNotFoundException extends RuntimeException {
    
    public ReviewNotFoundException(String message) {
        super(message);
    }
    
    public ReviewNotFoundException(Long reviewId) {
        super("Review not found with ID: " + reviewId);
    }
}