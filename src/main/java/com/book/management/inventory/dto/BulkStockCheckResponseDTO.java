package com.book.management.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map; /**
 * Data Transfer Object for bulk stock check response.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkStockCheckResponseDTO {

    private Map<Long, Boolean> availabilityMap; // bookId -> available (true/false)
    private boolean allAvailable;
    private String message;
}
