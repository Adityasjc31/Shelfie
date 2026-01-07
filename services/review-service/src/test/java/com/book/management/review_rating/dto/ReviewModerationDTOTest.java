package com.book.management.review_rating.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.book.management.review_rating.model.Review.ReviewStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReviewModerationDTO.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ReviewModerationDTOTest {

    @Test
    @DisplayName("Builder should create DTO with all fields")
    void builder_CreatesCompleteDTO() {
        ReviewModerationDTO dto = ReviewModerationDTO.builder()
                .status(ReviewStatus.APPROVED)
                .moderatedBy(999L)
                .rejectionReason(null)
                .build();

        assertEquals(ReviewStatus.APPROVED, dto.getStatus());
        assertEquals(999L, dto.getModeratedBy());
        assertNull(dto.getRejectionReason());
    }

    @Test
    @DisplayName("Builder should create rejected DTO with reason")
    void builder_CreatesRejectedDTOWithReason() {
        ReviewModerationDTO dto = ReviewModerationDTO.builder()
                .status(ReviewStatus.REJECTED)
                .moderatedBy(888L)
                .rejectionReason("Inappropriate content")
                .build();

        assertEquals(ReviewStatus.REJECTED, dto.getStatus());
        assertEquals(888L, dto.getModeratedBy());
        assertEquals("Inappropriate content", dto.getRejectionReason());
    }

    @Test
    @DisplayName("No-args constructor should create empty DTO")
    void noArgsConstructor_CreatesEmptyDTO() {
        ReviewModerationDTO dto = new ReviewModerationDTO();

        assertNull(dto.getStatus());
        assertNull(dto.getModeratedBy());
        assertNull(dto.getRejectionReason());
    }

    @Test
    @DisplayName("All-args constructor should create DTO with all fields")
    void allArgsConstructor_CreatesCompleteDTO() {
        ReviewModerationDTO dto = new ReviewModerationDTO(
                ReviewStatus.APPROVED, 999L, null);

        assertEquals(ReviewStatus.APPROVED, dto.getStatus());
        assertEquals(999L, dto.getModeratedBy());
        assertNull(dto.getRejectionReason());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        ReviewModerationDTO dto = new ReviewModerationDTO();

        dto.setStatus(ReviewStatus.REJECTED);
        dto.setModeratedBy(777L);
        dto.setRejectionReason("Spam content");

        assertEquals(ReviewStatus.REJECTED, dto.getStatus());
        assertEquals(777L, dto.getModeratedBy());
        assertEquals("Spam content", dto.getRejectionReason());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        ReviewModerationDTO dto1 = ReviewModerationDTO.builder()
                .status(ReviewStatus.APPROVED)
                .moderatedBy(999L)
                .build();

        ReviewModerationDTO dto2 = ReviewModerationDTO.builder()
                .status(ReviewStatus.APPROVED)
                .moderatedBy(999L)
                .build();

        ReviewModerationDTO dto3 = ReviewModerationDTO.builder()
                .status(ReviewStatus.REJECTED)
                .moderatedBy(999L)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        ReviewModerationDTO dto1 = ReviewModerationDTO.builder()
                .status(ReviewStatus.APPROVED)
                .moderatedBy(999L)
                .build();

        ReviewModerationDTO dto2 = ReviewModerationDTO.builder()
                .status(ReviewStatus.APPROVED)
                .moderatedBy(999L)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain key fields")
    void toString_ContainsKeyFields() {
        ReviewModerationDTO dto = ReviewModerationDTO.builder()
                .status(ReviewStatus.REJECTED)
                .moderatedBy(999L)
                .rejectionReason("Spam")
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("REJECTED"));
        assertTrue(toString.contains("999"));
        assertTrue(toString.contains("Spam"));
    }
}
