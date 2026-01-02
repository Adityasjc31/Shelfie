
package com.book.management.order.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for placing a new order.
 * Encapsulates the necessary information to create an order in the system.

 * Responsibilities:
 * - Represents the incoming request payload for order placement.
 * - Provides mapping between books and their requested quantities.

 * Domain Notes:
 * - {@code userId} must correspond to a valid user in the system.
 * - {@code bookOrder} must contain at least one entry; each key represents a book ID,
 *   and each value represents the quantity requested for that book.
 * - Validation should ensure quantities are positive integers.

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

    @NotNull(message = "User ID must be provided")
    private Long userId;

    @NotEmpty(message = "Order must contain at least one book")
    private Map<Long, @Positive(message = "Quantity must be at least 1") Integer> bookOrder;
}
