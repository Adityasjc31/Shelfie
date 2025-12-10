<<<<<<< HEAD:src/main/java/com/db/ms/exception/InsufficientStockException.java
package com.db.ms.exception;
=======
package com.db.ms.inventory.exceptions;
>>>>>>> 6979c75f791df3a9533e62ae5df45fc130808a3a:src/main/java/com/db/ms/Inventory/exceptions/InsufficientStockException.java

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