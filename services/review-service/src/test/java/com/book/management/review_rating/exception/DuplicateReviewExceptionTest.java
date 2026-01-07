package com.book.management.review_rating.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DuplicateReviewException.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class DuplicateReviewExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_WithMessage_SetsMessage() {
        String message = "Custom duplicate review message";
        DuplicateReviewException exception = new DuplicateReviewException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with userId and bookId should format message correctly")
    void constructor_WithUserIdAndBookId_FormatsMessage() {
        DuplicateReviewException exception = new DuplicateReviewException(100L, 200L);

        assertEquals("User 100 has already reviewed book 200", exception.getMessage());
    }

    @Test
    @DisplayName("Exception should be a RuntimeException")
    void exception_IsRuntimeException() {
        DuplicateReviewException exception = new DuplicateReviewException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
