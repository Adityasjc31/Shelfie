package com.db.ms.exception;

/**
 * Exception thrown when a user tries to modify someone else's review.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
public class UnauthorizedReviewAccessException extends RuntimeException {
    
    public UnauthorizedReviewAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedReviewAccessException(Long userId, Long reviewId) {
        super(String.format("User %d is not authorized to access review %d", userId, reviewId));
    }
}