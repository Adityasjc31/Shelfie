package com.book.management.inventory.client;

import com.book.management.inventory.client.fallback.BookServiceClientFallback;
import com.book.management.inventory.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for inter-service communication with Book Service.
 * 
 * Enables the Inventory Service to:
 * - Validate book existence before creating inventory
 * - Fetch book details for enriched responses
 * 
 * Uses fallback for resilience when Book Service is unavailable.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
@FeignClient(
    name = "book-service",
    fallback = BookServiceClientFallback.class
)
public interface BookServiceClient {

    /**
     * Fetches book details by book ID.
     * 
     * @param bookId the book ID
     * @return BookDTO containing book details, or null if not found
     */
    @GetMapping("/api/v1/book/getById/{bookId}")
    BookDTO getBookById(@PathVariable("bookId") Long bookId);

    /**
     * Checks if a book exists in the book service.
     * 
     * @param bookId the book ID
     * @return true if book exists, false otherwise
     */
    default boolean checkBookExists(Long bookId) {
        try {
            BookDTO book = getBookById(bookId);
            return book != null && book.getBookId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
