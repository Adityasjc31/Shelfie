package com.book.management.inventory.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validating inventory operations with book service.
 * Uses direct service-to-service communication.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryBookValidator {

    private final BookServiceHelper bookServiceHelper;

    /**
     * Validates if a book exists before creating inventory.
     *
     * @param bookId the book ID to validate
     * @throws IllegalArgumentException if book doesn't exist
     */
    public void validateBookExistsForInventoryCreation(Long bookId) {
        log.debug("Validating book exists for inventory creation: {}", bookId);

        if (!bookServiceHelper.bookExists(bookId)) {
            log.error("Validation failed: Book {} does not exist", bookId);
            throw new IllegalArgumentException(
                    "Cannot create inventory for non-existent book ID: " + bookId);
        }

        log.debug("Validation passed: Book {} exists", bookId);
    }

    /**
     * Checks if a book exists without throwing exception.
     *
     * @param bookId the book ID
     * @return true if book exists, false otherwise
     */
    public boolean isValidBook(Long bookId) {
        return bookServiceHelper.bookExists(bookId);
    }

    /**
     * Gets validation message for a book ID.
     *
     * @param bookId the book ID
     * @return validation message (null if valid)
     */
    public String getValidationMessage(Long bookId) {
        return bookServiceHelper.validateBookExists(bookId);
    }

    /**
     * Validates multiple book IDs at once.
     *
     * @param bookIds array of book IDs to validate
     * @return comma-separated list of invalid book IDs, or null if all valid
     */
    public String validateMultipleBooks(Long... bookIds) {
        if (bookIds == null || bookIds.length == 0) {
            return null;
        }

        List<Long> invalidBooks = new ArrayList<>();

        for (Long bookId : bookIds) {
            if (!bookServiceHelper.bookExists(bookId)) {
                invalidBooks.add(bookId);
            }
        }

        return invalidBooks.isEmpty() ?
                null : "Invalid book IDs: " + invalidBooks.toString();
    }

    /**
     * Validates bulk stock check map - ensures all books exist.
     *
     * @param bookQuantities map of bookId to quantity
     * @return error message if any book doesn't exist, null if all valid
     */
    public String validateBulkStockCheck(Map<Long, Integer> bookQuantities) {
        if (bookQuantities == null || bookQuantities.isEmpty()) {
            return null;
        }

        List<Long> invalidBooks = new ArrayList<>();

        for (Long bookId : bookQuantities.keySet()) {
            if (!bookServiceHelper.bookExists(bookId)) {
                invalidBooks.add(bookId);
            }
        }

        if (!invalidBooks.isEmpty()) {
            return "Cannot check stock for non-existent books: " + invalidBooks;
        }

        return null;
    }

    /**
     * Logs book information when creating inventory.
     *
     * @param bookId the book ID
     */
    public void logBookInfo(Long bookId) {
        bookServiceHelper.getBookDetails(bookId).ifPresent(book -> {
            log.info("Creating inventory for book: {} (ID: {}, Price: {}, Author: {})",
                    book.getBookTitle(),
                    book.getBookId(),
                    book.getBookPrice(),
                    book.getBookAuthorId());
        });
    }

    /**
     * Logs detailed book information.
     *
     * @param bookId the book ID
     */
    public void logDetailedBookInfo(Long bookId) {
        bookServiceHelper.getBookDetails(bookId).ifPresent(book -> {
            log.info("Book Details - ID: {}, Title: {}, Author: {}, Category: {}, Price: {}",
                    book.getBookId(),
                    book.getBookTitle(),
                    book.getBookAuthorId(),
                    book.getBookCategoryId(),
                    book.getBookPrice());
        });
    }
}