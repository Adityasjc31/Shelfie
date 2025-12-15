package com.db.ms.Review_Rating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing Review.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    /**
     * Unique identifier for the review.
     */
    private Long reviewId;

    /**
     * Foreign key reference to the User entity.
     * Represents the customer who wrote the review.
     */
    private Long userId;

    /**
     * Foreign key reference to the Book entity.
     * Represents the book being reviewed.
     */
    private Long bookId;

    /**
     * Rating given to the book (1-5 scale).
     * 1 = Poor, 5 = Excellent
     */
    private Integer rating;

    /**
     * Review comment/text written by the customer.
     */
    private String comment;

    /**
     * Review status for moderation.
     * Values: PENDING, APPROVED, REJECTED
     */
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    /**
     * ID of the admin who moderated this review (if moderated).
     */
    private Long moderatedBy;

    /**
     * Reason for rejection (if status is REJECTED).
     */
    private String rejectionReason;

    /**
     * Flag to indicate if review is helpful.
     * Can be extended to count helpful votes.
     */
    @Builder.Default
    private Integer helpfulCount = 0;

    /**
     * Timestamp when the review was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the review was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Checks if the review is approved.
     * 
     * @return true if approved, false otherwise
     */
    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }

    /**
     * Checks if the review is pending moderation.
     * 
     * @return true if pending, false otherwise
     */
    public boolean isPending() {
        return status == ReviewStatus.PENDING;
    }

    /**
     * Checks if the review is rejected.
     * 
     * @return true if rejected, false otherwise
     */
    public boolean isRejected() {
        return status == ReviewStatus.REJECTED;
    }

    /**
     * Enum for review status.
     */
    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}