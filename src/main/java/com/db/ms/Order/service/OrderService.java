package com.db.ms.Order.service;

import com.db.ms.Order.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.Order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.db.ms.Order.dto.responsedto.OrderPriceStockResponseDTO;
import com.db.ms.Order.dto.responsedto.OrderResponseDTO;
import com.db.ms.Order.enums.OrderEnum;
import com.db.ms.Order.exception.OrderCancellationNotAllowedException; // Added for completeness
import com.db.ms.Order.exception.OrderNotPlacedException; // Added for completeness
import com.db.ms.Order.exception.OrderNotFoundException;
import com.db.ms.Order.exception.OrderInvalidStatusTransitionException;
import java.util.List;
import java.util.Optional;

/**
 * Service interface defining business logic operations for Order Management.
 * Follows SOLID principles—particularly Interface Segregation and Single Responsibility.
 *
 * FIX: The signatures below have been updated to include all CHECKED exceptions
 * thrown by the OrderServiceImpl (OrderNotPlacedException, OrderCancellationNotAllowedException).
 *
 * @author Rehan Ashraf
 * @version 1.0 (Fixed for compilation with checked exceptions)
 * @since 2024-12-08
 */
public interface OrderService {

    /**
     * Places a new order using the request payload and the pre-fetched price/stock maps.
     *
     * @param request    PlaceOrderRequestDTO containing userId and bookOrder map
     * @param priceStock OrderPriceStockResponseDTO containing bookPrice and bookStock maps
     * @return the created order response
     * @throws IllegalArgumentException if payload or price/stock data is invalid (Unchecked, but often declared for clarity)
     * @throws OrderNotPlacedException if persistence fails unexpectedly (CHECKED)
     */
    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request, OrderPriceStockResponseDTO priceStock)
            throws IllegalArgumentException, OrderNotPlacedException;


    /**
     * Retrieves all orders.
     *
     * @return list of all orders
     */
    List<OrderResponseDTO> getOrderAll();

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the order ID
     * @return optional order response (empty if not found)
     */
    Optional<OrderResponseDTO> getOrderById(long orderId);

    /**
     * Retrieves orders filtered by their status.
     *
     * @param status the order status to filter by
     * @return list of orders matching the status
     */
    List<OrderResponseDTO> getOrderByStatus(OrderEnum status);

    /**
     * Changes the status of an existing order.
     *
     * @param orderId the order ID
     * @param request the status change request (newStatus, optional reason/meta)
     * @return the updated order response
     * @throws OrderNotFoundException                if the order does not exist (CHECKED)
     * @throws OrderInvalidStatusTransitionException if the requested transition is invalid (CHECKED)
     */
    OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request)
            throws OrderNotFoundException, OrderInvalidStatusTransitionException;

    /**
     * Cancels an order.
     *
     * @param orderId the order ID
     * @throws OrderNotFoundException                if the order does not exist (CHECKED)
     * @throws OrderInvalidStatusTransitionException if order cannot be cancelled from its current status (CHECKED)
     * @throws OrderCancellationNotAllowedException  if cancellation is forbidden by domain rules (CHECKED)
     */
    void cancelOrder(long orderId)
            throws OrderNotFoundException, OrderInvalidStatusTransitionException, OrderCancellationNotAllowedException;

}