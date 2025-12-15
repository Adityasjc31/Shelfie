
package com.db.ms.order.model;

import com.db.ms.order.enums.OrderEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing an Order in the Digital Book Store.
 * Captures who placed the order, which books are included, when it was placed,
 * the total amount charged, and the current lifecycle status of the order.

 * Notes:
 * Status transitions should follow business policies (e.g., PENDING → SHIPPED → DELIVERED).
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {

    /**
     * Unique identifier for the order.
     */
    private long orderId;

    /**
     * Identifier for the user who placed the order.
     * Used to validate user existence and ownership checks.
     */
    private long userId;

    /**
     * List of book identifiers included in the order.
     */
    private List<Long> bookId;

    /**
     * Timestamp when the order was created/placed.
     * Typically set by the server at order placement time.
     */
    private LocalDateTime orderDateTime;

    /**
     * Total amount charged for the order.
     */
    private double orderTotalAmount;

    /**
     * Current status of the order in its lifecycle.
     * Possible values: {@link OrderEnum#PENDING}, {@link OrderEnum#SHIPPED}, {@link OrderEnum#DELIVERED}.
     */
    private OrderEnum orderStatus;

}

