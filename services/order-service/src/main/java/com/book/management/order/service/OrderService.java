package com.book.management.order.service;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.*;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for Order Management services in a microservice architecture.
 * This service orchestrates the order lifecycle, including placement, status management,
 * and synchronization with external service deletion events.
 *
 * @author Rehan Ashraf
 * @version 2.0 (Microservice Migration)
 * @since 2024-12-08
 */
public interface OrderService {

    /**
     * Orchestrates the placement of a new order.
     * Communicates with Book Service for pricing and Inventory Service for stock reduction.
     *
     * @param request The DTO containing the user ID and the map of book IDs to quantities.
     * @return OrderResponseDTO containing the details of the successfully created order.
     * @throws OrderNotPlacedException if stock reduction fails or downstream services are unreachable.
     */
    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException;

    /**
     * Retrieves all active (non-deleted) orders currently stored in the system.
     *
     * @return A list of OrderResponseDTOs representing all active orders.
     */
    List<OrderResponseDTO> getOrderAll();

    /**
     * Fetches a specific order by its unique identifier.
     *
     * @param orderId The unique primary key of the order in the database.
     * @return An Optional containing the OrderResponseDTO if found, otherwise empty.
     */
    Optional<OrderResponseDTO> getOrderById(long orderId);

    /**
     * Filters and retrieves orders based on their current lifecycle status.
     *
     * @param status The OrderEnum value (e.g., PENDING, SHIPPED) to filter by.
     * @return A list of orders matching the specified status.
     */
    List<OrderResponseDTO> getOrderByStatus(OrderEnum status);

    /**
     * Updates the status of an existing order.
     * Transitions are typically validated in the implementation layer (e.g., PENDING to SHIPPED).
     *
     * @param orderId The unique identifier of the order to be updated.
     * @param request DTO containing the new target status.
     * @return The updated OrderResponseDTO.
     * @throws OrderNotFoundException if the specified orderId does not exist.
     */
    OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request)
            throws OrderNotFoundException;

    /**
     * Cancels an existing order if it has not yet been shipped or delivered.
     * Performs a soft delete by marking the order as inactive in the database.
     *
     * @param orderId The unique identifier of the order to cancel.
     * @throws OrderNotFoundException if the orderId is invalid.
     * @throws OrderCancellationNotAllowedException if the order status prohibits cancellation.
     */
    void cancelOrder(long orderId) throws OrderNotFoundException, OrderCancellationNotAllowedException;

    /**
     * Synchronizes the deletion state of an order with external service events.
     * Triggered when a User or Book associated with the order is deleted in their respective services.
     * Sets the 'isDeleted' flag to true without performing status validation.
     *
     * @param orderId The unique identifier of the order to be marked as deleted.
     * @throws OrderNotFoundException if the orderId is not found.
     */
    void softDeleteOrder(long orderId) throws OrderNotFoundException;
}