package com.book.management.order.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for reducing inventory quantities for multiple books.
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code bookQuantities}: Map of bookId → quantity to reduce.</li>
 * </ul>
 *
 * <p><b>Intended use:</b></p>
 * <ul>
 *   <li>Sent by Order Service to Inventory Service to deduct stock as part of the order placement workflow.</li>
 *   <li>Inventory Service performs validation (availability, thresholds) and throws exceptions if insufficient.</li>
 * </ul>
 *
 * <p><b>Example (JSON request body):</b></p>
 * <pre>
 * {
 *   "bookQuantities": {
 *     "101": 1,
 *     "105": 2
 *   }
 * }
 * </pre>
 *
 * <p><b>Notes:</b></p>
 * <ul>
 *   <li>Keys are book IDs (Long).</li>
 *   <li>Values are the units to reduce (Integer, must be positive).</li>
 * </ul>
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReduceInventoryStockRequestDTO {

    /**
     * Map of bookId to quantity to deduct.
     * <p>Key: {@code Long} (bookId)</p>
     * <p>Value: {@code Integer} (units to reduce, must be &gt; 0)</p>
     */
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

