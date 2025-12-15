package com.db.ms.inventory_temp.exception;
/**
 * Exception thrown for invalid inventory operations.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-08
 */
public class InvalidInventoryOperationException extends RuntimeException {

    public InvalidInventoryOperationException(String message) {
        super(message);
    }
}