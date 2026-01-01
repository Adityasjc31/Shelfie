package main.java.com.book.management.review_rating.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating book rating in Book Service.
 * 
 * Sent to Book Service after review operations (create/update/delete)
 * to keep the book's rating statistics in sync.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRatingUpdateRequest {

    /**
     * The new average rating for the book.
     * Calculated from all approved reviews.
     */
    private Double averageRating;

    /**
     * The total number of approved reviews for the book.
     */
    private Long totalReviews;
}
