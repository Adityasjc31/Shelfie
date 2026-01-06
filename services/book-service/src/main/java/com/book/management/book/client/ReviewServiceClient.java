package com.book.management.book.client;

import com.book.management.book.client.dto.BookRatingStatsDTO;
import com.book.management.book.client.dto.ReviewResponseDTO;
import com.book.management.book.client.fallback.ReviewServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for communicating with Review Service.
 * 
 * @author Shashwat Srivastava
 * @version 1.0
 * @since 2024-12-15
 */
@FeignClient(
        name = "review-service",
        path = "/api/v1/reviews",
        fallbackFactory = ReviewServiceClientFallback.class
)
public interface ReviewServiceClient {

    /**
     * Retrieves all reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return list of reviews for the book
     */
    @GetMapping("/book/{bookId}")
    List<ReviewResponseDTO> getReviewsByBookId(@PathVariable("bookId") Long bookId);

    /**
     * Retrieves all approved reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return list of approved reviews for the book
     */
    @GetMapping("/book/{bookId}/approved")
    List<ReviewResponseDTO> getApprovedReviewsByBookId(@PathVariable("bookId") Long bookId);

    /**
     * Gets rating statistics for a book.
     * 
     * @param bookId the book ID
     * @return rating statistics for the book
     */
    @GetMapping("/book/{bookId}/stats")
    BookRatingStatsDTO getBookRatingStats(@PathVariable("bookId") Long bookId);

    /**
     * Calculates average rating for a book.
     * 
     * @param bookId the book ID
     * @return average rating for the book
     */
    @GetMapping("/book/{bookId}/average-rating")
    Double calculateAverageRating(@PathVariable("bookId") Long bookId);
}
