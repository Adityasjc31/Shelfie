package com.book.management.review_rating.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InvalidReviewOperationException.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class InvalidReviewOperationExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_WithMessage_SetsMessage() {
        String message = "Cannot create review: Book does not exist";
        InvalidReviewOperationException exception = new InvalidReviewOperationException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Exception should be a RuntimeException")
    void exception_IsRuntimeException() {
        InvalidReviewOperationException exception = new InvalidReviewOperationException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
