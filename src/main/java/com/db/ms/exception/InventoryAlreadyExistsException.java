<<<<<<< HEAD:src/main/java/com/db/ms/exception/InventoryAlreadyExistsException.java
package com.db.ms.exception;
=======
package com.db.ms.inventory.exceptions;
>>>>>>> 6979c75f791df3a9533e62ae5df45fc130808a3a:src/main/java/com/db/ms/Inventory/exceptions/InventoryAlreadyExistsException.java

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