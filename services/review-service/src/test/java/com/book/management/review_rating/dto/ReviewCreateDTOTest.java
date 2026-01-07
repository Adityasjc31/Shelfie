package com.book.management.review_rating.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReviewCreateDTO.
 * Tests builder, getters, setters, and equals/hashCode.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ReviewCreateDTOTest {

    @Test
    @DisplayName("Builder should create DTO with all fields")
    void builder_CreatesCompleteDTO() {
        ReviewCreateDTO dto = ReviewCreateDTO.builder()
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Excellent book!")
                .build();

        assertEquals(100L, dto.getUserId());
        assertEquals(200L, dto.getBookId());
        assertEquals(5, dto.getRating());
        assertEquals("Excellent book!", dto.getComment());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        ReviewCreateDTO dto = new ReviewCreateDTO();

        assertNull(dto.getUserId());
        assertNull(dto.getBookId());
        assertNull(dto.getRating());
        assertNull(dto.getComment());
    }

    @Test
    @DisplayName("All-args constructor should create DTO with all fields")
    void allArgsConstructor_CreatesCompleteDTO() {
        ReviewCreateDTO dto = new ReviewCreateDTO(100L, 200L, 4, "Good book!");

        assertEquals(100L, dto.getUserId());
        assertEquals(200L, dto.getBookId());
        assertEquals(4, dto.getRating());
        assertEquals("Good book!", dto.getComment());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        ReviewCreateDTO dto = new ReviewCreateDTO();

        dto.setUserId(50L);
        dto.setBookId(150L);
        dto.setRating(3);
        dto.setComment("Average book");

        assertEquals(50L, dto.getUserId());
        assertEquals(150L, dto.getBookId());
        assertEquals(3, dto.getRating());
        assertEquals("Average book", dto.getComment());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        ReviewCreateDTO dto1 = ReviewCreateDTO.builder()
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great!")
                .build();

        ReviewCreateDTO dto2 = ReviewCreateDTO.builder()
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great!")
                .build();

        ReviewCreateDTO dto3 = ReviewCreateDTO.builder()
                .userId(101L)
                .bookId(200L)
                .rating(5)
                .comment("Great!")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        ReviewCreateDTO dto1 = ReviewCreateDTO.builder()
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great!")
                .build();

        ReviewCreateDTO dto2 = ReviewCreateDTO.builder()
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great!")
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain all fields")
    void toString_ContainsAllFields() {
        ReviewCreateDTO dto = ReviewCreateDTO.builder()
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great!")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("100"));
        assertTrue(toString.contains("200"));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains("Great!"));
    }
}
