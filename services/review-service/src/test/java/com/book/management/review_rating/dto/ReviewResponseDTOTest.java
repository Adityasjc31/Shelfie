package com.book.management.review_rating.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.book.management.review_rating.model.Review.ReviewStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReviewResponseDTO.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ReviewResponseDTOTest {

    @Test
    @DisplayName("Builder should create DTO with all fields")
    void builder_CreatesCompleteDTO() {
        LocalDateTime now = LocalDateTime.now();
        ReviewResponseDTO dto = ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great book!")
                .status(ReviewStatus.APPROVED)
                .moderatedBy(999L)
                .rejectionReason(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, dto.getReviewId());
        assertEquals(100L, dto.getUserId());
        assertEquals(200L, dto.getBookId());
        assertEquals(5, dto.getRating());
        assertEquals("Great book!", dto.getComment());
        assertEquals(ReviewStatus.APPROVED, dto.getStatus());
        assertEquals(999L, dto.getModeratedBy());
        assertNull(dto.getRejectionReason());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        ReviewResponseDTO dto = new ReviewResponseDTO();

        assertNull(dto.getReviewId());
        assertNull(dto.getUserId());
        assertNull(dto.getBookId());
        assertNull(dto.getRating());
        assertNull(dto.getComment());
        assertNull(dto.getStatus());
        assertNull(dto.getModeratedBy());
        assertNull(dto.getRejectionReason());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("All-args constructor should create DTO with all fields")
    void allArgsConstructor_CreatesCompleteDTO() {
        LocalDateTime now = LocalDateTime.now();
        ReviewResponseDTO dto = new ReviewResponseDTO(
                1L, 100L, 200L, 5, "Great!", ReviewStatus.PENDING,
                null, null, now, now);

        assertEquals(1L, dto.getReviewId());
        assertEquals(100L, dto.getUserId());
        assertEquals(200L, dto.getBookId());
        assertEquals(5, dto.getRating());
        assertEquals("Great!", dto.getComment());
        assertEquals(ReviewStatus.PENDING, dto.getStatus());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setReviewId(5L);
        dto.setUserId(50L);
        dto.setBookId(500L);
        dto.setRating(3);
        dto.setComment("Average");
        dto.setStatus(ReviewStatus.REJECTED);
        dto.setModeratedBy(77L);
        dto.setRejectionReason("Spam");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals(5L, dto.getReviewId());
        assertEquals(50L, dto.getUserId());
        assertEquals(500L, dto.getBookId());
        assertEquals(3, dto.getRating());
        assertEquals("Average", dto.getComment());
        assertEquals(ReviewStatus.REJECTED, dto.getStatus());
        assertEquals(77L, dto.getModeratedBy());
        assertEquals("Spam", dto.getRejectionReason());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        ReviewResponseDTO dto1 = ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(100L)
                .createdAt(now)
                .build();

        ReviewResponseDTO dto2 = ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(100L)
                .createdAt(now)
                .build();

        ReviewResponseDTO dto3 = ReviewResponseDTO.builder()
                .reviewId(2L)
                .userId(100L)
                .createdAt(now)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        LocalDateTime now = LocalDateTime.now();
        ReviewResponseDTO dto1 = ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(100L)
                .createdAt(now)
                .build();

        ReviewResponseDTO dto2 = ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(100L)
                .createdAt(now)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain key fields")
    void toString_ContainsKeyFields() {
        ReviewResponseDTO dto = ReviewResponseDTO.builder()
                .reviewId(1L)
                .rating(5)
                .comment("Great!")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains("Great!"));
    }
}
