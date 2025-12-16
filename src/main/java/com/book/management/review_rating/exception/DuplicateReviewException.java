package com.book.management.review_rating.exception;

/**
 * Exception thrown when a user tries to review the same book twice.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-15
 */
public class DuplicateReviewException extends RuntimeException {
    
    public DuplicateReviewException(String message) {
        super(message);
    }
    
    public DuplicateReviewException(Long userId, Long bookId) {
        super(String.format("User %d has already reviewed book %d", userId, bookId));
    }
}
