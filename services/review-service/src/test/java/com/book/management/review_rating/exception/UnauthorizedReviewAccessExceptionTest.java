package com.book.management.review_rating.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UnauthorizedReviewAccessException.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class UnauthorizedReviewAccessExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_WithMessage_SetsMessage() {
        String message = "Custom unauthorized message";
        UnauthorizedReviewAccessException exception = new UnauthorizedReviewAccessException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with userId and reviewId should format message correctly")
    void constructor_WithUserIdAndReviewId_FormatsMessage() {
        UnauthorizedReviewAccessException exception = new UnauthorizedReviewAccessException(100L, 200L);

        assertEquals("User 100 is not authorized to access review 200", exception.getMessage());
    }

    @Test
    @DisplayName("Exception should be a RuntimeException")
    void exception_IsRuntimeException() {
        UnauthorizedReviewAccessException exception = new UnauthorizedReviewAccessException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
