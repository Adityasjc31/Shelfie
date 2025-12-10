package com.db.ms.exception;
/**
 * Exception thrown when attempting to create duplicate inventory record.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
public class InventoryAlreadyExistsException extends RuntimeException {

    public InventoryAlreadyExistsException(String message) {
        super(message);
    }

    public InventoryAlreadyExistsException(Long bookId) {
        super("Inventory already exists for book ID: " + bookId);
    }
}