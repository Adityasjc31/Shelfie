package com.book.management.inventory.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Inventory entity.
 * Tests business logic methods and entity behavior.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
class InventoryTest {

    @Test
    void isLowStock_ReturnsTrue_WhenQuantityBelowThreshold() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(5)
                .lowStockThreshold(10)
                .build();

        // Act & Assert
        assertTrue(inventory.isLowStock());
    }

    @Test
    void isLowStock_ReturnsTrue_WhenQuantityEqualsThreshold() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(10)
                .lowStockThreshold(10)
                .build();

        // Act & Assert
        assertTrue(inventory.isLowStock());
    }

    @Test
    void isLowStock_ReturnsFalse_WhenQuantityAboveThreshold() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .build();

        // Act & Assert
        assertFalse(inventory.isLowStock());
    }

    @Test
    void isOutOfStock_ReturnsTrue_WhenQuantityIsZero() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(0)
                .lowStockThreshold(10)
                .build();

        // Act & Assert
        assertTrue(inventory.isOutOfStock());
    }

    @Test
    void isOutOfStock_ReturnsFalse_WhenQuantityIsPositive() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(1)
                .lowStockThreshold(10)
                .build();

        // Act & Assert
        assertFalse(inventory.isOutOfStock());
    }

    @Test
    void onCreate_SetsDefaultThreshold_WhenNull() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setInventoryId(1L);
        inventory.setBookId(100L);
        inventory.setQuantity(50);
        inventory.setLowStockThreshold(null);

        // Act
        inventory.onCreate();

        // Assert
        assertEquals(10, inventory.getLowStockThreshold());
    }

    @Test
    void onCreate_KeepsExistingThreshold_WhenNotNull() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setInventoryId(1L);
        inventory.setBookId(100L);
        inventory.setQuantity(50);
        inventory.setLowStockThreshold(20);

        // Act
        inventory.onCreate();

        // Assert
        assertEquals(20, inventory.getLowStockThreshold());
    }

    @Test
    void builder_SetsDefaultThreshold() {
        // Arrange & Act
        Inventory inventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(50)
                .build();

        // Assert
        assertEquals(10, inventory.getLowStockThreshold());
    }

    @Test
    void allArgsConstructor_SetsAllFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        Inventory inventory = new Inventory(1L, 100L, 50, 15, now, now, 0L);

        // Assert
        assertEquals(1L, inventory.getInventoryId());
        assertEquals(100L, inventory.getBookId());
        assertEquals(50, inventory.getQuantity());
        assertEquals(15, inventory.getLowStockThreshold());
        assertEquals(now, inventory.getCreatedAt());
        assertEquals(now, inventory.getUpdatedAt());
        assertEquals(0L, inventory.getVersion());
    }

    @Test
    void setters_WorkCorrectly() {
        // Arrange
        Inventory inventory = new Inventory();
        LocalDateTime now = LocalDateTime.now();

        // Act
        inventory.setInventoryId(1L);
        inventory.setBookId(100L);
        inventory.setQuantity(50);
        inventory.setLowStockThreshold(15);
        inventory.setCreatedAt(now);
        inventory.setUpdatedAt(now);
        inventory.setVersion(1L);

        // Assert
        assertEquals(1L, inventory.getInventoryId());
        assertEquals(100L, inventory.getBookId());
        assertEquals(50, inventory.getQuantity());
        assertEquals(15, inventory.getLowStockThreshold());
        assertEquals(now, inventory.getCreatedAt());
        assertEquals(now, inventory.getUpdatedAt());
        assertEquals(1L, inventory.getVersion());
    }
}
