package com.book.management.review_rating.client.fallback;

import com.book.management.review_rating.client.dto.BookRatingUpdateRequest;
import com.book.management.review_rating.client.dto.BookResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookServiceClientFallback.
 * Tests that fallback methods return expected values when Book Service is
 * unavailable.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class BookServiceClientFallbackTest {

    private BookServiceClientFallback fallback;

    @BeforeEach
    void setUp() {
        fallback = new BookServiceClientFallback();
    }

    @Test
    @DisplayName("getBookById should return null when Book Service is unavailable")
    void getBookById_ReturnsNull() {
        BookResponseDTO result = fallback.getBookById(123L);

        assertNull(result);
    }

    @Test
    @DisplayName("updateBookRating should not throw exception")
    void updateBookRating_DoesNotThrowException() {
        BookRatingUpdateRequest request = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        // Should not throw any exception
        assertDoesNotThrow(() -> fallback.updateBookRating(123L, request));
    }

    @Test
    @DisplayName("checkBookExists should return false when Book Service is unavailable")
    void checkBookExists_ReturnsFalse() {
        boolean result = fallback.checkBookExists(123L);

        assertFalse(result);
    }

    @Test
    @DisplayName("All fallback methods should handle null parameters gracefully")
    void fallbackMethods_HandleNullParameters() {
        // getBookById with null
        assertNull(fallback.getBookById(null));

        // checkBookExists with null
        assertFalse(fallback.checkBookExists(null));

        // updateBookRating should not throw with null request
        assertDoesNotThrow(() -> fallback.updateBookRating(123L, null));
    }
}
