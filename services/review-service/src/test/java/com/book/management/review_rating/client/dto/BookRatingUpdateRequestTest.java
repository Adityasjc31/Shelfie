package com.book.management.review_rating.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookRatingUpdateRequest.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class BookRatingUpdateRequestTest {

    @Test
    @DisplayName("Builder should create DTO with all fields")
    void builder_CreatesCompleteDTO() {
        BookRatingUpdateRequest dto = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        assertEquals(4.5, dto.getAverageRating());
        assertEquals(100L, dto.getTotalReviews());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        BookRatingUpdateRequest dto = new BookRatingUpdateRequest();

        assertNull(dto.getAverageRating());
        assertNull(dto.getTotalReviews());
    }

    @Test
    @DisplayName("All-args constructor should create DTO with all fields")
    void allArgsConstructor_CreatesCompleteDTO() {
        BookRatingUpdateRequest dto = new BookRatingUpdateRequest(3.8, 50L);

        assertEquals(3.8, dto.getAverageRating());
        assertEquals(50L, dto.getTotalReviews());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        BookRatingUpdateRequest dto = new BookRatingUpdateRequest();

        dto.setAverageRating(4.2);
        dto.setTotalReviews(75L);

        assertEquals(4.2, dto.getAverageRating());
        assertEquals(75L, dto.getTotalReviews());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        BookRatingUpdateRequest dto1 = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        BookRatingUpdateRequest dto2 = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        BookRatingUpdateRequest dto3 = BookRatingUpdateRequest.builder()
                .averageRating(3.5)
                .totalReviews(100L)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        BookRatingUpdateRequest dto1 = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        BookRatingUpdateRequest dto2 = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain key fields")
    void toString_ContainsKeyFields() {
        BookRatingUpdateRequest dto = BookRatingUpdateRequest.builder()
                .averageRating(4.5)
                .totalReviews(100L)
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("4.5"));
        assertTrue(toString.contains("100"));
    }
}
