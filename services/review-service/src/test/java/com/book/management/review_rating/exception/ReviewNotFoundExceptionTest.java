package com.book.management.review_rating.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReviewNotFoundException.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ReviewNotFoundExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_WithMessage_SetsMessage() {
        String message = "Custom not found message";
        ReviewNotFoundException exception = new ReviewNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with reviewId should format message correctly")
    void constructor_WithReviewId_FormatsMessage() {
        ReviewNotFoundException exception = new ReviewNotFoundException(123L);

        assertEquals("Review not found with ID: 123", exception.getMessage());
    }

    @Test
    @DisplayName("Exception should be a RuntimeException")
    void exception_IsRuntimeException() {
        ReviewNotFoundException exception = new ReviewNotFoundException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
