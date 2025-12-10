package com.db.ms.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating inventory quantity.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateDTO {

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
}