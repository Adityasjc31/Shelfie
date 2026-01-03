package com.book.management.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkStockReduceDTO {
    @NotEmpty(message = "bookQuantities must not be empty")
    private Map<
            @NotNull(message = "bookId must not be null")
            @Positive(message = "bookId must be positive")
                    Long,
            @NotNull(message = "quantity must not be null")
            @Positive(message = "quantity must be positive")
                    Integer
            > bookQuantities;
}