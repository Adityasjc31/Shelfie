package com.db.ms.review_rating.dto;

import java.time.LocalDateTime;

import com.db.ms.review_rating.model.Review.ReviewStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Review responses.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    
    private Long reviewId;
    private Long userId;
    private Long bookId;
    private Integer rating;
    private String comment;
    private ReviewStatus status;
    private Long moderatedBy;
    private String rejectionReason;
    private Integer helpfulCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}