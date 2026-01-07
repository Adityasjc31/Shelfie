package com.book.management.inventory.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DTO classes.
 * Tests Lombok-generated methods and builders.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-08
 */
class DTOTest {

    // ==================== InventoryCreateDTO Tests ====================

    @Test
    void inventoryCreateDTO_BuilderAndGetters() {
        // Act
        InventoryCreateDTO dto = InventoryCreateDTO.builder()
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .build();

        // Assert
        assertEquals(100L, dto.getBookId());
        assertEquals(50, dto.getQuantity());
        assertEquals(10, dto.getLowStockThreshold());
    }

    @Test
    void inventoryCreateDTO_SettersWork() {
        // Arrange
        InventoryCreateDTO dto = new InventoryCreateDTO();

        // Act
        dto.setBookId(100L);
        dto.setQuantity(50);
        dto.setLowStockThreshold(10);

        // Assert
        assertEquals(100L, dto.getBookId());
        assertEquals(50, dto.getQuantity());
        assertEquals(10, dto.getLowStockThreshold());
    }

    // ==================== InventoryResponseDTO Tests ====================

    @Test
    void inventoryResponseDTO_BuilderAndGetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        InventoryResponseDTO dto = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(1L, dto.getInventoryId());
        assertEquals(100L, dto.getBookId());
        assertEquals(50, dto.getQuantity());
        assertEquals(10, dto.getLowStockThreshold());
        assertFalse(dto.isLowStock());
        assertFalse(dto.isOutOfStock());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    // ==================== InventoryUpdateDTO Tests ====================

    @Test
    void inventoryUpdateDTO_BuilderAndGetters() {
        // Act
        InventoryUpdateDTO dto = InventoryUpdateDTO.builder()
                .quantity(75)
                .build();

        // Assert
        assertEquals(75, dto.getQuantity());
    }

    // ==================== InventoryAdjustmentDTO Tests ====================

    @Test
    void inventoryAdjustmentDTO_BuilderAndGetters() {
        // Act
        InventoryAdjustmentDTO dto = InventoryAdjustmentDTO.builder()
                .adjustmentQuantity(10)
                .reason("Restock")
                .build();

        // Assert
        assertEquals(10, dto.getAdjustmentQuantity());
        assertEquals("Restock", dto.getReason());
    }

    // ==================== LowStockAlertDTO Tests ====================

    @Test
    void lowStockAlertDTO_BuilderAndGetters() {
        // Act
        LowStockAlertDTO dto = LowStockAlertDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .currentQuantity(5)
                .lowStockThreshold(10)
                .quantityNeeded(5)
                .alertLevel("WARNING")
                .build();

        // Assert
        assertEquals(1L, dto.getInventoryId());
        assertEquals(100L, dto.getBookId());
        assertEquals(5, dto.getCurrentQuantity());
        assertEquals(10, dto.getLowStockThreshold());
        assertEquals(5, dto.getQuantityNeeded());
        assertEquals("WARNING", dto.getAlertLevel());
    }

    // ==================== BulkStockCheckDTO Tests ====================

    @Test
    void bulkStockCheckDTO_BuilderAndGetters() {
        // Arrange
        Map<Long, Integer> bookQuantities = new HashMap<>();
        bookQuantities.put(100L, 5);
        bookQuantities.put(101L, 3);

        // Act
        BulkStockCheckDTO dto = BulkStockCheckDTO.builder()
                .bookQuantities(bookQuantities)
                .build();

        // Assert
        assertNotNull(dto.getBookQuantities());
        assertEquals(2, dto.getBookQuantities().size());
        assertEquals(5, dto.getBookQuantities().get(100L));
    }

    // ==================== BulkStockCheckResponseDTO Tests ====================

    @Test
    void bulkStockCheckResponseDTO_BuilderAndGetters() {
        // Arrange
        Map<Long, Boolean> availabilityMap = new HashMap<>();
        availabilityMap.put(100L, true);
        availabilityMap.put(101L, false);

        // Act
        BulkStockCheckResponseDTO dto = BulkStockCheckResponseDTO.builder()
                .availabilityMap(availabilityMap)
                .allAvailable(false)
                .message("Some books unavailable")
                .build();

        // Assert
        assertNotNull(dto.getAvailabilityMap());
        assertFalse(dto.isAllAvailable());
        assertEquals("Some books unavailable", dto.getMessage());
    }

    // ==================== BulkStockReduceDTO Tests ====================

    @Test
    void bulkStockReduceDTO_SettersAndGetters() {
        // Arrange
        BulkStockReduceDTO dto = new BulkStockReduceDTO();
        Map<Long, Integer> bookQuantities = new HashMap<>();
        bookQuantities.put(100L, 2);

        // Act
        dto.setBookQuantities(bookQuantities);

        // Assert
        assertNotNull(dto.getBookQuantities());
        assertEquals(1, dto.getBookQuantities().size());
        assertEquals(2, dto.getBookQuantities().get(100L));
    }
}
