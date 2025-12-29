package com.book.management.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for adjusting inventory quantity.
 * Supports both increment and decrement operations.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAdjustmentDTO {

    @NotNull(message = "Adjustment quantity is required")
    private Integer adjustmentQuantity;

    private String reason;
}