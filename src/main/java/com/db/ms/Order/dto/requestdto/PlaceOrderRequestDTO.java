
package com.db.ms.Order.dto.requestdto;

import lombok.Data;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for placing a new order.
 * Encapsulates the necessary information to create an order in the system.
 *
 * Responsibilities:
 * - Represents the incoming request payload for order placement.
 * - Provides mapping between books and their requested quantities.
 *
 * Domain Notes:
 * - {@code userId} must correspond to a valid user in the system.
 * - {@code bookOrder} must contain at least one entry; each key represents a book ID,
 *   and each value represents the quantity requested for that book.
 * - Validation should ensure quantities are positive integers.
 *
 * Example:
 * JSON:
 * {
 *   "userId": 101,
 *   "bookOrder": {
 *       101: 2,
 *       102: 1
 *   }
 * }
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Data
public class PlaceOrderRequestDTO {

    /**
     * The unique identifier of the user placing the order.
     * Must correspond to an existing user in the system.
     */
    private long userId;

    /**
     * A map representing the books and their quantities in the order.
     * Key: {@code bookId} (Long) - the unique identifier of the book.
     * Value: {@code orderQuantity} (Integer) - the quantity requested for that book.
     *
     * Constraints:
     * - Must not be empty.
     * - Quantities must be greater than zero.
     */
    private Map<Long, Integer> bookOrder;
}
