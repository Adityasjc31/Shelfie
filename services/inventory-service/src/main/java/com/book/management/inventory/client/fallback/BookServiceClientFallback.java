package com.book.management.inventory.client.fallback;

import com.book.management.inventory.client.BookServiceClient;
import com.book.management.inventory.dto.BookDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for BookServiceClient.
 * 
 * Provides graceful degradation when the Book Service is unavailable.
 * Returns null or default values to prevent cascading failures.
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
    public BookDTO getBookById(Long bookId) {
        log.warn("FALLBACK: Book Service unavailable. Cannot fetch book with ID: {}", bookId);
        return null;
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
