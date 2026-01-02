package com.book.management.review_rating.client;

import com.book.management.review_rating.client.dto.BookRatingUpdateRequest;
import com.book.management.review_rating.client.dto.BookResponseDTO;
import com.book.management.review_rating.client.fallback.BookServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for inter-service communication with Book Service.
 * 
 * Enables the Review Service to:
 * - Validate book existence before creating reviews
 * - Fetch book details for enriched responses
 * - Update book rating statistics after review changes
 * 
 * Uses fallback for resilience when Book Service is unavailable.
 * 
 * Microservices Pattern: Service-to-Service Communication via FeignClient
 * - Declarative REST client
 * - Integrated with Eureka for service discovery
 * - Circuit breaker support via fallback
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
@FeignClient(name = "book-service", fallback = BookServiceClientFallback.class)
public interface BookServiceClient {

    /**
     * Fetches book details by book ID.
     * 
     * @param bookId the book ID
     * @return BookResponseDTO containing book details, or null if not found
     */
    @GetMapping("/api/v1/book/getById/{bookId}")
    BookResponseDTO getBookById(@PathVariable("bookId") Long bookId);

    /**
     * Updates book rating statistics in Book Service.
     * Called after a review is approved/deleted to keep ratings in sync.
     * 
     * @param bookId       the book ID
     * @param ratingUpdate the rating update request containing average rating and
     *                     total reviews
     */
    @PutMapping("/api/v1/book/{bookId}/rating")
    void updateBookRating(@PathVariable("bookId") Long bookId,
            @RequestBody BookRatingUpdateRequest ratingUpdate);

    /**
     * Checks if a book exists in the book service.
     * Default method that wraps getBookById for convenience.
     * 
     * @param bookId the book ID
     * @return true if book exists, false otherwise
     */
    default boolean checkBookExists(Long bookId) {
        try {
            BookResponseDTO book = getBookById(bookId);
            return book != null && book.getBookId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
