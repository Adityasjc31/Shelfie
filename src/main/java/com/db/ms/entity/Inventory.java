package com.db.ms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing Inventory.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    /**
     * Unique identifier for the inventory record.
     */
    private Long inventoryId;

    /**
     * Foreign key reference to the Book entity.
     * Represents which book this inventory record tracks.
     */
    private Long bookId;

    /**
     * Current quantity of books available in stock.
     * Must be non-negative.
     */
    private Integer quantity;

    /**
     * Minimum threshold quantity for low stock alerts.
     * When quantity falls below this value, alerts are triggered.
     */
    @Builder.Default
    private Integer lowStockThreshold = 10;

    /**
     * Timestamp when the inventory record was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the inventory record was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Checks if the current stock is below the low stock threshold.
     *
     * @return true if stock is low, false otherwise
     */
    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    /**
     * Checks if the item is out of stock.
     *
     * @return true if quantity is zero, false otherwise
     */
    public boolean isOutOfStock() {
        return quantity == 0;
    }
}