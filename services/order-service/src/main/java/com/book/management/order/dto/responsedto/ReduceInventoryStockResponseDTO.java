
package com.book.management.order.dto.responsedto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * Response DTO representing stock availability for books.
 * Fields:
 *{@code bookStock}: Map of bookId → stock availability (boolean)

 *
 * Intended use:
 *   Returned by Inventory Service or orchestration layer to indicate which books have sufficient stock.
 *   Consumed by OrderController during the /orders/place flow to validate availability before confirming an order.

 * Example:
 * {
 *   "bookStock": {
 *     101: true,
 *     102: false
 *   }
 * }
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReduceInventoryStockResponseDTO {
    /**
     * Map of bookId to availability in inventory.
     * <p>Key: {@code Long} (bookId)</p>
     * <p>Value: {@code Boolean} (true if sufficient stock, false otherwise)</p>
     */
    private Map<Long, Boolean> bookStock;
}
