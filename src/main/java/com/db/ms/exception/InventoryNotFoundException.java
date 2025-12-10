package com.db.ms.exception;
/**
 * Exception thrown when an inventory record is not found.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String message) {
        super(message);
    }

    public InventoryNotFoundException(Long inventoryId) {
        super("Inventory not found with ID: " + inventoryId);
    }

    public InventoryNotFoundException(String field, Long value) {
        super(String.format("Inventory not found with %s: %d", field, value));
    }
}