package com.db.ms.review_rating_temp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import com.db.ms.review_rating_temp.model.Review.ReviewStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for moderating reviews.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public
class ReviewModerationDTO {
    
    @NotNull(message = "Status is required")
    private ReviewStatus status;
    
    @NotNull(message = "Moderator ID is required")
    private Long moderatedBy;
    
    private String rejectionReason;
}