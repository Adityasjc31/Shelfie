package com.db.ms.Inventory.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Inventory responses.
 * Used to send inventory data to clients.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponseDTO {

    private Long inventoryId;
    private Long bookId;
    private Integer quantity;
    private Integer lowStockThreshold;
    private boolean isLowStock;
    private boolean isOutOfStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}