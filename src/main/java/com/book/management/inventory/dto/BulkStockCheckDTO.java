package com.book.management.inventory.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Data Transfer Object for bulk stock check and deduction.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkStockCheckDTO {

    @NotNull(message = "Book quantities map is required")
    private Map<Long, Integer> bookQuantities; // bookId -> quantity
}

