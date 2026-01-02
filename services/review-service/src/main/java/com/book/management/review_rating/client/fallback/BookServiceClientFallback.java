package com.book.management.review_rating.client.fallback;

import com.book.management.review_rating.client.BookServiceClient;
import com.book.management.review_rating.client.dto.BookRatingUpdateRequest;
import com.book.management.review_rating.client.dto.BookResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for BookServiceClient.
 * 
 * Provides graceful degradation when the Book Service is unavailable.
 * Returns null or default values to prevent cascading failures.
 * 
 * Resilience Pattern: Circuit Breaker Fallback
 * - Prevents service failures from propagating
 * - Logs warnings for monitoring and alerting
 * - Allows core operations to continue with degraded functionality
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
@Component
@Slf4j
public class BookServiceClientFallback implements BookServiceClient {

    /**
     * Fallback method when book-service is unavailable.
     * Returns null and logs a warning.
     * 
     * @param bookId the book ID
     * @return null (fallback response)
     */
    @Override
    public BookResponseDTO getBookById(Long bookId) {
        log.warn("FALLBACK: Book Service unavailable. Cannot fetch book with ID: {}", bookId);
        return null;
    }

    /**
     * Fallback method for updating book rating.
     * Logs a warning but doesn't fail the operation.
     * 
     * @param bookId       the book ID
     * @param ratingUpdate the rating update request
     */
    @Override
    public void updateBookRating(Long bookId, BookRatingUpdateRequest ratingUpdate) {
        log.warn("FALLBACK: Book Service unavailable. Cannot update rating for book ID: {}. " +
                "Average: {}, Total Reviews: {}",
                bookId,
                ratingUpdate.getAverageRating(),
                ratingUpdate.getTotalReviews());
    }

    /**
     * Fallback for book existence check.
     * Returns false when book-service is unavailable.
     * 
     * @param bookId the book ID
     * @return false (assume book doesn't exist when service is down)
     */
    @Override
    public boolean checkBookExists(Long bookId) {
        log.warn("FALLBACK: Book Service unavailable. Cannot verify book existence for ID: {}", bookId);
        return false;
    }
}
