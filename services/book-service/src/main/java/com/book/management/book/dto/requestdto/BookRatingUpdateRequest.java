package com.book.management.book.dto.requestdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating book rating from Review Service.
 * 
 * Per LLD Section 4.5 - Review & Rating Module:
 * - Allows customers to submit book reviews and ratings.
 * - Rating data is synced from Review Service to Book Service.
 * 
 * This breaks the circular dependency by allowing Review Service
 * to push rating updates instead of Book Service pulling review data.
 *
 * @author Shashwat Srivastava
 * @version 1.0
 * @since 2026-01-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRatingUpdateRequest {
    
    /**
     * Average rating from approved reviews (0.0 - 5.0).
     * Calculated by Review Service from Review.Rating values.
     */
    @NotNull(message = "Average rating is required")
    @Min(value = 0, message = "Average rating must be at least 0")
    @Max(value = 5, message = "Average rating must not exceed 5")
    private Double averageRating;
    
    /**
     * Total count of approved reviews for this book.
     * Used for displaying review count alongside rating.
     */
    @NotNull(message = "Total reviews count is required")
    @Min(value = 0, message = "Total reviews must be at least 0")
    private Long totalReviews;
}
