package com.book.management.inventory.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
/**
 * Data Transfer Object for creating new inventory records.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCreateDTO {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;

    @Min(value = 1, message = "Low stock threshold must be at least 1")
    private Integer lowStockThreshold;
}