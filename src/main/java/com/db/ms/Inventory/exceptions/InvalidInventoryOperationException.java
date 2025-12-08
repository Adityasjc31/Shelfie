package com.db.ms.Inventory.exceptions;

/**
 * Exception thrown for invalid inventory operations.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
public class InvalidInventoryOperationException extends RuntimeException {

    public InvalidInventoryOperationException(String message) {
        super(message);
    }
}