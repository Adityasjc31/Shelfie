package com.book.management.review_rating.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookResponseDTO.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class BookResponseDTOTest {

    @Test
    @DisplayName("Builder should create DTO with all fields")
    void builder_CreatesCompleteDTO() {
        BookResponseDTO dto = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .bookAuthorId("author-001")
                .bookCategoryId("CAT-FIC")
                .bookPrice(29.99)
                .bookStockQuantity(50L)
                .build();

        assertEquals(1L, dto.getBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals("author-001", dto.getBookAuthorId());
        assertEquals("CAT-FIC", dto.getBookCategoryId());
        assertEquals(29.99, dto.getBookPrice());
        assertEquals(50L, dto.getBookStockQuantity());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        BookResponseDTO dto = new BookResponseDTO();

        assertEquals(0L, dto.getBookId());
        assertNull(dto.getBookTitle());
        assertNull(dto.getBookAuthorId());
        assertEquals(0.0, dto.getBookPrice());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        BookResponseDTO dto = new BookResponseDTO();

        dto.setBookId(5L);
        dto.setBookTitle("Updated Title");
        dto.setBookPrice(19.99);
        dto.setBookStockQuantity(25L);

        assertEquals(5L, dto.getBookId());
        assertEquals("Updated Title", dto.getBookTitle());
        assertEquals(19.99, dto.getBookPrice());
        assertEquals(25L, dto.getBookStockQuantity());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        BookResponseDTO dto1 = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .build();

        BookResponseDTO dto2 = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .build();

        BookResponseDTO dto3 = BookResponseDTO.builder()
                .bookId(2L)
                .bookTitle("Test Book")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        BookResponseDTO dto1 = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .build();

        BookResponseDTO dto2 = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain key fields")
    void toString_ContainsKeyFields() {
        BookResponseDTO dto = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Book"));
    }
}
