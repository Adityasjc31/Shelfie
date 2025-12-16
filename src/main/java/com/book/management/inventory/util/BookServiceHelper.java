package com.book.management.inventory.util;

import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility helper to communicate with Book Service directly.
 * Uses service-to-service communication (dependency injection).
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookServiceHelper {

    private final BookService bookService;

    /**
     * Checks if a book exists by calling Book Service directly.
     *
     * @param bookId the book ID
     * @return true if book exists, false otherwise
     */
    public boolean bookExists(Long bookId) {
        log.debug("Checking if book exists: {}", bookId);

        try {
            Optional<BookResponseDTO> book = bookService.getBookById(bookId);
            boolean exists = book.isPresent();

            log.debug("Book {} exists: {}", bookId, exists);
            return exists;

        } catch (Exception e) {
            log.error("Error checking book existence for bookId: {}", bookId, e);
            return false;
        }
    }

    /**
     * Gets book details from Book Service.
     *
     * @param bookId the book ID
     * @return Optional containing book details if found
     */
    public Optional<BookResponseDTO> getBookDetails(Long bookId) {
        log.debug("Fetching book details for bookId: {}", bookId);

        try {
            Optional<BookResponseDTO> book = bookService.getBookById(bookId);

            if (book.isPresent()) {
                log.debug("Successfully fetched book details for bookId: {}", bookId);
            } else {
                log.debug("Book {} not found", bookId);
            }

            return book;

        } catch (Exception e) {
            log.error("Error fetching book details for bookId: {}", bookId, e);
            return Optional.empty();
        }
    }

    /**
     * Validates if a book exists and returns error message if not.
     *
     * @param bookId the book ID to validate
     * @return error message if book doesn't exist, null if exists
     */
    public String validateBookExists(Long bookId) {
        if (!bookExists(bookId)) {
            return "Book with ID " + bookId + " does not exist";
        }
        return null;
    }

    /**
     * Gets book title by book ID.
     *
     * @param bookId the book ID
     * @return book title or "Unknown" if not found
     */
    public String getBookTitle(Long bookId) {
        return getBookDetails(bookId)
                .map(BookResponseDTO::getBookTitle)
                .orElse("Unknown");
    }

    /**
     * Gets book price by book ID.
     *
     * @param bookId the book ID
     * @return book price or 0.0 if not found
     */
    public Double getBookPrice(Long bookId) {
        return getBookDetails(bookId)
                .map(BookResponseDTO::getBookPrice)
                .orElse(0.0);
    }

    /**
     * Gets book author ID by book ID.
     *
     * @param bookId the book ID
     * @return book author ID or "Unknown" if not found
     */
    public String getBookAuthorId(Long bookId) {
        return getBookDetails(bookId)
                .map(BookResponseDTO::getBookAuthorId)
                .orElse("Unknown");
    }

    /**
     * Gets book category ID by book ID.
     *
     * @param bookId the book ID
     * @return book category ID or "Unknown" if not found
     */
    public String getBookCategoryId(Long bookId) {
        return getBookDetails(bookId)
                .map(BookResponseDTO::getBookCategoryId)
                .orElse("Unknown");
    }
}