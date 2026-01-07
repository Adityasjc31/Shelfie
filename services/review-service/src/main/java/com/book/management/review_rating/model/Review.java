package com.book.management.review_rating.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity class representing Review.
 * Converted to use Spring Data JPA for microservices architecture.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-16
 */
@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_book_id", columnList = "book_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_rating", columnList = "rating")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", nullable = false, length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "moderated_by")
    private Long moderatedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the review is approved.
     */
    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }

    /**
     * Checks if the review is pending moderation.
     */
    public boolean isPending() {
        return status == ReviewStatus.PENDING;
    }

    /**
     * Checks if the review is rejected.
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
