package com.book.management.review_rating.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        LocalDateTime now = LocalDateTime.now();
        BookResponseDTO dto = BookResponseDTO.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .bookAuthorId(100L)
                .authorName("John Author")
                .categoryId(10L)
                .categoryName("Fiction")
                .bookPrice(BigDecimal.valueOf(29.99))
                .stockQuantity(50)
                .isbn("978-1234567890")
                .description("A great book")
                .publisher("Test Publisher")
                .publicationYear(2024)
                .language("English")
                .pages(350)
                .averageRating(4.5)
                .totalReviews(100L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, dto.getBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals(100L, dto.getBookAuthorId());
        assertEquals("John Author", dto.getAuthorName());
        assertEquals(10L, dto.getCategoryId());
        assertEquals("Fiction", dto.getCategoryName());
        assertEquals(BigDecimal.valueOf(29.99), dto.getBookPrice());
        assertEquals(50, dto.getStockQuantity());
        assertEquals("978-1234567890", dto.getIsbn());
        assertEquals("A great book", dto.getDescription());
        assertEquals("Test Publisher", dto.getPublisher());
        assertEquals(2024, dto.getPublicationYear());
        assertEquals("English", dto.getLanguage());
        assertEquals(350, dto.getPages());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(100L, dto.getTotalReviews());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        BookResponseDTO dto = new BookResponseDTO();

        assertNull(dto.getBookId());
        assertNull(dto.getBookTitle());
        assertNull(dto.getBookAuthorId());
        assertNull(dto.getBookPrice());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        BookResponseDTO dto = new BookResponseDTO();

        dto.setBookId(5L);
        dto.setBookTitle("Updated Title");
        dto.setBookPrice(BigDecimal.valueOf(19.99));
        dto.setStockQuantity(25);

        assertEquals(5L, dto.getBookId());
        assertEquals("Updated Title", dto.getBookTitle());
        assertEquals(BigDecimal.valueOf(19.99), dto.getBookPrice());
        assertEquals(25, dto.getStockQuantity());
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
