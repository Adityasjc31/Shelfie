package com.db.ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for book rating statistics.
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
class BookRatingStatsDTO {    
    private Long bookId;
    private Double averageRating;
    private Long totalReviews;
    private Long approvedReviews;
    private Long pendingReviews;
    private Long rejectedReviews;
    private RatingDistribution ratingDistribution;
}