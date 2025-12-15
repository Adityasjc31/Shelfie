package com.db.ms.Order.dto.responsedto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * Response DTO aggregating book price and stock availability.
 *
 * Fields:
 * - {@code bookPrice}: Map of bookId → price (double)
 * - {@code bookStock}: Map of bookId → stock availability (boolean)
 *
 * Intended use:
 * - Returned by OrderPriceStockController after orchestrating calls to Books and Inventory services.
 * - Consumed by OrderController during the /orders/place flow.
 *
 * Example:
 * <pre>
 * {
 *   "bookPrice": { 101: 399.0, 102: 249.5 },
 *   "bookStock": { 101: true, 102: false }
 * }
 * </pre>
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPriceStockResponseDTO {

    /**
     * Map of bookId to its unit price.
     * Key: {@code Long} (bookId)
     * Value: {@code Double} (unit price)
     */
    private Map<Long, Double> bookPrice;

    /**
     * Map of bookId to availability in inventory.
     * Key: {@code Long} (bookId)
     * Value: {@code Boolean} (true if sufficient stock, false otherwise)
     */
    private Map<Long, Boolean> bookStock;
}

