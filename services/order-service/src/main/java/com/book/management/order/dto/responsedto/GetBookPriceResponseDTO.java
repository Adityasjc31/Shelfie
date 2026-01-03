
package com.book.management.order.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO representing price details for books.
 * Fields:
 * {@code bookPrice}: Map of bookId → unit price (double)

 * Intended use:
 *  Returned by Book Service or orchestration layer to provide pricing information for requested books.
 *  Consumed by OrderController during the /orders/place flow to calculate total order amount.

 * Example:
 * {
 *   "bookPrice": {
 *     101: 399.0,
 *     102: 249.5
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
public class GetBookPriceResponseDTO {
    /**
     * Map of bookId to its unit price.
     * <p>Key: {@code Long} (bookId)</p>
     * <p>Value: {@code Double} (unit price)</p>
     */
    private Map<Long, Double> bookPrice;
}
