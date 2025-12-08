package com.db.ms.Inventory.exceptions;

/**
 * Exception thrown when insufficient stock is available for an operation.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(Long bookId, Integer available, Integer requested) {
        super(String.format("Insufficient stock for book ID: %d. Available: %d, Requested: %d",
                bookId, available, requested));
    }
}