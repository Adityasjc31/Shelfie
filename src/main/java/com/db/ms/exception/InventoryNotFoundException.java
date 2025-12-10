<<<<<<< HEAD:src/main/java/com/db/ms/exception/InventoryNotFoundException.java
package com.db.ms.exception;
=======
package com.db.ms.inventory.exceptions;
>>>>>>> 6979c75f791df3a9533e62ae5df45fc130808a3a:src/main/java/com/db/ms/Inventory/exceptions/InventoryNotFoundException.java

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