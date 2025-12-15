
package com.book.management.order.repository;

import java.util.List;
import java.util.Optional;

import com.book.management.order.enums.OrderEnum;
import com.book.management.order.model.Order;

/**
 * Repository interface for Order entity.
 * Defines methods for CRUD operations and custom queries on orders.

 * Notes:
 * - This is an abstraction; logging should be done in the implementation class,
 *   not in the interface.
 * - For now, implementations can be in-memory. Later, this can be backed by JPA.
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
public interface OrderRepository {

    /**
     * Saves an order (create or update).
     *
     * When the order has no identifier or a new identifier is assigned,
     * this acts as a create operation. Otherwise, it persists changes
     * to an existing order.
     *
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);

    /**
     * Finds all orders.
     *
     * @return list of all orders
     */
    List<Order> findAll();

    /**
     * Finds an order by its unique identifier.
     *
     * @param orderId the order ID
     * @return Optional containing the order if found, otherwise empty
     */
    Optional<Order> findById(long orderId);

    /**
     * Finds orders by lifecycle status.
     *
     * @param status the order status filter
     * @return list of orders matching the given status
     */
    List<Order> findByStatus(OrderEnum status);

    /**
     * Updates an existing order.
     * Implementations should throw an error if the order does not exist.
     * @param order the order with updated fields
     * @return the updated order
     */
    Order update(Order order);

    /**
     * Deletes an order by its unique identifier.
     *
     * @param orderId the order ID
     */
    void deleteById(long orderId);
}
