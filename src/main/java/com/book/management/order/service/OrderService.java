package com.book.management.order.service;

import com.book.management.book.exception.BookNotFoundException;
import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderInvalidStatusTransitionException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
// Note: We are assuming BookNotFoundException and InsufficientStockException
// are thrown by the implementation layer, but we don't declare them here
// unless they are specific to the Order service contract.
// We declare OrderNotPlacedException as the final failure state.

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for Order Management services.
 * All order-related business logic and orchestration are handled here.
 *
 * @author Rehan Ashraf
 * @version 1.5 (Updated placeOrder signature)
 * @since 2024-12-15
 */
public interface OrderService {

    /**
     * Places a new order. The service handles fetching prices, checking stock,
     * persisting the order, and reducing inventory internally.
     *
     * @param request The DTO containing userId and bookOrder map.
     * @return The response DTO of the newly created order.
     * @throws OrderNotPlacedException if the order fails at the persistence or stock reduction stage.
     * @throws IllegalArgumentException for validation errors (like null request or empty book list).
     * @throws BookNotFoundException if prices cannot be retrieved for requested items.
     * @throws InsufficientStockException if stock checks fail.
     */
    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException, IllegalArgumentException;

    /**
     * Retrieves all existing orders.
     *
     * @return A list of all order response DTOs.
     */
    List<OrderResponseDTO> getOrderAll();

    /**
     * Retrieves an order by its unique ID.
     *
     * @param orderId The unique identifier of the order.
     * @return An Optional containing the OrderResponseDTO if found, otherwise empty.
     */
    Optional<OrderResponseDTO> getOrderById(long orderId);

    /**
     * Retrieves all orders matching a specific status.
     *
     * @param status The status enum to filter by (e.g., PENDING, PLACED).
     * @return A list of matching order response DTOs.
     */
    List<OrderResponseDTO> getOrderByStatus(OrderEnum status);

    /**
     * Changes the status of an existing order.
     *
     * @param orderId The unique identifier of the order.
     * @param request The DTO containing the new status.
     * @return The updated order response DTO.
     * @throws OrderNotFoundException if the order ID is not found.
     * @throws OrderInvalidStatusTransitionException if the status change is not allowed by business rules.
     */
    OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request)
            throws OrderNotFoundException, OrderInvalidStatusTransitionException;

    /**
     * Cancels an existing order.
     *
     * @param orderId The unique identifier of the order to cancel.
     * @throws OrderNotFoundException if the order ID is not found.
     * @throws OrderCancellationNotAllowedException if the order is in a state that cannot be cancelled (e.g., DELIVERED).
     */
    void cancelOrder(long orderId) throws OrderNotFoundException, OrderCancellationNotAllowedException;
}