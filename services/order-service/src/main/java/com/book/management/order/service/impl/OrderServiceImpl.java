package com.book.management.order.service.impl;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.*;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of OrderService.
 * Manages the business logic for order processing and persistence.
 * Orchestrates cross-service calls via Feign and manages MySQL persistence.
 *
 * @author Rehan Ashraf
 * @version 2.1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookServiceClient bookServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    private static final String NOT_FOUND_MSG = "Order not found with ID: ";

    /**
     * Orchestrates order placement.
     * Exception handling is driven by the FeignErrorDecoder for inter-service
     * errors.
     * * @param request The order placement request details.
     * 
     * @return OrderResponseDTO for the created order.
     * @throws OrderNotPlacedException only when error occurs in downstream
     *                                 services.
     */
    @Override
    @Transactional
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        log.info("Initiating order placement for userId: {}", request.getUserId());
        try {
            // fetch book prices via Book Service
            List<Long> bookIdList = new ArrayList<>(request.getBookOrder().keySet());
            Map<Long, Double> priceMap = bookServiceClient.getBookPrices(bookIdList);

            // compute order totalAmount
            double totalAmount = 0.0;
            for (Map.Entry<Long, Integer> entry : request.getBookOrder().entrySet()) {
                Long bookId = entry.getKey();
                Integer quantity = entry.getValue();

                if (priceMap.containsKey(bookId)) {
                    double price = priceMap.get(bookId) * quantity;
                    totalAmount += price;
                }
            }

            // Reduce stock via Inventory Service
            // Inventory service handles all validation and throws exceptions if
            // insufficient stock
            inventoryServiceClient.reduceStock(request.getBookOrder());

            // map and save order
            Order order = Order.builder()
                    .userId(request.getUserId())
                    .items(request.getBookOrder())
                    .orderTotalAmount(totalAmount)
                    .orderDateTime(LocalDateTime.now())
                    .orderStatus(OrderEnum.PENDING)
                    .isDeleted(false)
                    .build();

            Order savedOrder = orderRepository.save(order);
            log.info("Order successfully placed. ID: {}", savedOrder.getOrderId());
            return toResponseDTO(savedOrder);

        } catch (OrderNotPlacedException ex) {
            // Rethrowing mapped exception caught by CustomFeignErrorDecoder
            throw ex;
        } catch (Exception ex) {
            log.error("Internal service error during order placement: {}", ex.getMessage());
            throw new OrderNotPlacedException("Internal system error: Unable to process order.");
        }
    }

    /**
     * Business logic for cancelling an order.
     * Rule: Can only cancel if PENDING or SHIPPED.
     * Cannot cancel if DELIVERED or already CANCELLED.
     * * @param orderId ID of the order to cancel.
     * 
     * @throws OrderNotFoundException               if order does not exist.
     * @throws OrderCancellationNotAllowedException if status is final.
     */
    @Override
    @Transactional
    public void cancelOrder(long orderId) {
        log.info("Processing business cancellation for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        // Logic check: Deny if Delivered or already Cancelled
        if (order.getOrderStatus() == OrderEnum.DELIVERED || order.getOrderStatus() == OrderEnum.CANCELLED) {
            log.warn("Cancellation rejected. Order {} status is {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException(
                    "Cannot cancel an order that is already " + order.getOrderStatus());
        }

        // Logic check: Allow only if Pending or Shipped
        order.setOrderStatus(OrderEnum.CANCELLED);
        orderRepository.save(order);

        // Also perform soft delete (sets isDeleted=true via @SQLDelete)
        orderRepository.delete(order);
        log.info("Order {} successfully cancelled and soft-deleted.", orderId);
    }

    /**
     * Administrative soft delete. Marks the isDeleted flag as true in MySQL.
     * * @param orderId ID of the order to delete.
     * 
     * @throws OrderNotFoundException if order does not exist.
     */
    @Override
    @Transactional
    public void softDeleteOrder(long orderId) {
        log.info("Soft-deleting orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        // The @SQLDelete annotation in the Order entity handles the UPDATE logic
        orderRepository.delete(order);
        log.info("Order {} marked as deleted.", orderId);
    }

    /**
     * Retrieves all active orders from the database.
     * * @return List of OrderResponseDTO.
     * 
     * @throws OrderNotFoundException if no orders exist in the system.
     */
    @Override
    public List<OrderResponseDTO> getOrderAll() {
        log.info("Fetching all orders.");
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found in the system.");
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }
        return responseList;
    }

    /**
     * Retrieves a specific order by its ID.
     * * @param orderId The ID of the order to find.
     * 
     * @return Optional containing OrderResponseDTO.
     * @throws OrderNotFoundException if the ID does not exist.
     */
    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        log.info("Fetching order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        return Optional.of(toResponseDTO(order));
    }

    /**
     * Retrieves orders filtered by their current status.
     * * @param status The OrderEnum status to filter by.
     * 
     * @return List of OrderResponseDTO.
     * @throws OrderNotFoundException if no orders match the given status.
     */
    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        log.info("Fetching orders with status: {}", status);
        List<Order> orders = orderRepository.findByOrderStatus(status);

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found with status: " + status);
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }
        return responseList;
    }

    /**
     * Updates an order's status after validating the transition logic.
     * * @param orderId ID of the order to update.
     * 
     * @param request DTO containing the target status.
     * @return Updated OrderResponseDTO.
     * @throws OrderNotFoundException                if the order doesn't exist.
     * @throws OrderInvalidStatusTransitionException if the status change is
     *                                               logically invalid.
     */
    @Override
    @Transactional
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request) {
        log.info("Attempting status change for order ID: {} to {}", orderId, request.getOrderStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        // Business Logic: Prevent updates if order is already CANCELLED or DELIVERED
        if (order.getOrderStatus() == OrderEnum.CANCELLED || order.getOrderStatus() == OrderEnum.DELIVERED) {
            throw new OrderInvalidStatusTransitionException(
                    "Cannot change status. Order is already in a final state: " + order.getOrderStatus());
        }

        order.setOrderStatus(request.getOrderStatus());
        Order updatedOrder = orderRepository.save(order);
        return toResponseDTO(updatedOrder);
    }

    /**
     * Helper method to map Order Entity to OrderResponseDTO.
     * * @param order The source entity.
     * 
     * @return Mapped Response DTO.
     */
    private OrderResponseDTO toResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .bookIds(new ArrayList<>(order.getItems().keySet()))
                .orderDateTime(order.getOrderDateTime())
                .orderTotalAmount(order.getOrderTotalAmount())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}