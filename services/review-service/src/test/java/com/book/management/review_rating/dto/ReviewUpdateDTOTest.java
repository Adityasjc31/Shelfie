package com.book.management.review_rating.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReviewUpdateDTO.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ReviewUpdateDTOTest {

    @Test
    @DisplayName("Builder should create DTO with all fields")
    void builder_CreatesCompleteDTO() {
        ReviewUpdateDTO dto = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Updated comment!")
                .build();

        assertEquals(4, dto.getRating());
        assertEquals("Updated comment!", dto.getComment());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        ReviewUpdateDTO dto = new ReviewUpdateDTO();

        assertNull(dto.getRating());
        assertNull(dto.getComment());
    }

    @Test
    @DisplayName("All-args constructor should create DTO with all fields")
    void allArgsConstructor_CreatesCompleteDTO() {
        ReviewUpdateDTO dto = new ReviewUpdateDTO(3, "Average book");

        assertEquals(3, dto.getRating());
        assertEquals("Average book", dto.getComment());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        ReviewUpdateDTO dto = new ReviewUpdateDTO();

        dto.setRating(5);
        dto.setComment("Excellent!");

        assertEquals(5, dto.getRating());
        assertEquals("Excellent!", dto.getComment());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        ReviewUpdateDTO dto1 = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Good!")
                .build();

        ReviewUpdateDTO dto2 = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Good!")
                .build();

        ReviewUpdateDTO dto3 = ReviewUpdateDTO.builder()
                .rating(5)
                .comment("Good!")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        ReviewUpdateDTO dto1 = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Good!")
                .build();

        ReviewUpdateDTO dto2 = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Good!")
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain all fields")
    void toString_ContainsAllFields() {
        ReviewUpdateDTO dto = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Good book!")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("4"));
        assertTrue(toString.contains("Good book!"));
    }
}
