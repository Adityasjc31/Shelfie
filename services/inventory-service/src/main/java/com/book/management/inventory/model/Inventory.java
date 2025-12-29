package com.book.management.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA Entity class representing Inventory in the database.
 * 
 * This entity tracks book inventory levels, including:
 * - Current stock quantity
 * - Low stock thresholds
 * - Timestamps for auditing
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@Entity
@Table(name = "inventory", 
       indexes = {
           @Index(name = "idx_book_id", columnList = "book_id"),
           @Index(name = "idx_quantity", columnList = "quantity")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    /**
     * Unique identifier for the inventory record.
     * Auto-generated using IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    /**
     * Foreign key reference to the Book entity.
     * Represents which book this inventory record tracks.
     * Must be unique (one inventory record per book).
     */
    @Column(name = "book_id", nullable = false, unique = true)
    private Long bookId;

    /**
     * Current quantity of books available in stock.
     * Must be non-negative.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Minimum threshold quantity for low stock alerts.
     * When quantity falls below this value, alerts are triggered.
     * Default value is 10.
     */
    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;

    /**
     * Timestamp when the inventory record was created.
     * Automatically populated by JPA auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the inventory record was last updated.
     * Automatically updated by JPA auditing on every modification.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Version field for optimistic locking.
     * Prevents concurrent modification issues.
     */
    @Version
    @Column(name = "version")
    private Long version;

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

    /**
     * Pre-persist hook to ensure default values.
     */
    @PrePersist
    protected void onCreate() {
        if (lowStockThreshold == null) {
            lowStockThreshold = 10;
        }
    }
}
