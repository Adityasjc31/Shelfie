
package com.book.management.order.dto.requestdto;

import com.book.management.order.enums.OrderEnum;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for updating the status of an existing order.
 * Used in status transition operations within the Order Management module.

 * Responsibilities:
 * - Represents the incoming request payload for changing an order's status.

 * Domain Notes:
 * - {@code orderId} must correspond to an existing order.
 * - {@code orderStatus} must be a valid value from {@link OrderEnum}.
 * - Transition rules should be enforced in the service layer (e.g., PENDING → DELIVERED).

 * Example:
 * <pre>
 * {
 *   "orderId": 1001,
 *   "orderStatus": "SHIPPED"
 * }
 * </pre>
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Data
public class UpdateOrderStatusRequestDTO {

    /**
     * The unique identifier of the order whose status is to be updated.
     */
    private long orderId;

    /**
     * The new status to apply to the order.
     * Must be a valid enum value from {@link OrderEnum}.
     */
    private OrderEnum orderStatus;
}
