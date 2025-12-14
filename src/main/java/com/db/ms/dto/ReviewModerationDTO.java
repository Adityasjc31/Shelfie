package com.db.ms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import com.db.ms.model.Review.ReviewStatus;

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