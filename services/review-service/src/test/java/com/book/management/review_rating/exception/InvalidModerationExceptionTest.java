package com.book.management.review_rating.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InvalidModerationException.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class InvalidModerationExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_WithMessage_SetsMessage() {
        String message = "Rejection reason is required";
        InvalidModerationException exception = new InvalidModerationException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Exception should be a RuntimeException")
    void exception_IsRuntimeException() {
        InvalidModerationException exception = new InvalidModerationException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
