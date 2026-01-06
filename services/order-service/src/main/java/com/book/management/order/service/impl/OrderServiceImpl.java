
package com.book.management.order.service.impl;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
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
     * Exception handling is driven by the FeignErrorDecoder for inter-service errors.
     * @param request The order placement request details.
     * @return OrderResponseDTO for the created order.
     * @throws OrderNotPlacedException only when error occurs in downstream
     * services.
     */
    @Override
    @Transactional
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        log.info("Initiating order placement for userId: {}", request.getUserId());
        try {
            // 1) Fetch prices and validate existence (Book-service)
            List<Long> bookIdList = new ArrayList<>(request.getBookOrder().keySet());
            GetBookPriceResponseDTO priceDTO = bookServiceClient.getBookPrices(new GetBookPriceRequestDTO(bookIdList));
            Map<Long, Double> priceMap = priceDTO.getBookPrice();

            // 2) Reduce stock (Inventory-service)
            inventoryServiceClient.reduceStock(new ReduceInventoryStockRequestDTO(request.getBookOrder()));

            // 3) Compute total amount (missing price -> 0.0, warn)
            double totalAmount = request.getBookOrder().entrySet().stream()
                    .mapToDouble(e -> {
                        Long bookId = e.getKey();
                        Integer qty = e.getValue();
                        Double unitPrice = priceMap.get(bookId);
                        if (unitPrice == null) {
                            log.warn("Price not found for bookId={}, defaulting contribution to 0.0", bookId);
                            return 0.0;
                        }
                        return unitPrice * qty;
                    })
                    .sum();

            // 4) Map and save order
            Order order = Order.builder()
                    .userId(request.getUserId())
                    .items(request.getBookOrder())
                    .orderTotalAmount(totalAmount)
                    .orderDateTime(LocalDateTime.now())
                    .orderStatus(OrderEnum.PENDING)
                    .isDeleted(false)
                    .build();

            Order savedOrder = orderRepository.save(order);
            log.info("Order placed successfully. orderId: {}, userId: {}, totalAmount: {}",
                    savedOrder.getOrderId(), savedOrder.getUserId(), savedOrder.getOrderTotalAmount());

            return toResponseDTO(savedOrder);
        } catch (OrderNotPlacedException ex) {
            // Re-throw with original message from Feign error decoder
            log.error("Downstream service error during order placement: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during order placement: {}", ex.getMessage(), ex);
            throw new OrderNotPlacedException(
                    "Internal system error: Unable to process order. Cause: " + ex.getMessage());
        }
    }

    /**
     * Updates an order's status after validating the transition logic.
     * * @param orderId ID of the order to update.
     * * @param request DTO containing the target status.
     * @return Updated OrderResponseDTO.
     * @throws OrderNotFoundException                if the order doesn't exist.
     * @throws OrderInvalidStatusTransitionException if the status change is
     * logically invalid.

     * Current state machine rules:
     * - PENDING → SHIPPED
     * - SHIPPED → DELIVERED
     * - DELIVERED → (No further transitions allowed)
     * - CANCELLED → (No further transitions allowed)

     * Note:
     * - Status transition allowed for /changeOrderStatus endpoint.
     * - Cancellation is only permitted via /cancelOrder endpoint.
     * - Any other transition is considered invalid.
     */
    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(long orderId, UpdateOrderStatusRequestDTO request) {
        log.info("Initializing order status update for orderId: {} with target: {}",
                orderId, request.getOrderStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for status update. orderId: {}", orderId);
                    return new OrderNotFoundException(NOT_FOUND_MSG + orderId);
                });

        // Business Logic: Prevent cancellation request
        if (request.getOrderStatus() == OrderEnum.CANCELLED) {
            log.warn("Cancellation not allowed on /changeOrderStatus. orderId: {}, requested: {}",
                    orderId, request.getOrderStatus());
            throw new OrderInvalidStatusTransitionException(
                    "Invalid operation: cancellation is not allowed on this endpoint. Use /cancelOrder instead."
            );
        }

        // Business Logic: Prevent updates if order is already DELIVERED
        if (order.getOrderStatus() == OrderEnum.DELIVERED) {
            log.warn("Status update rejected: order already DELIVERED. orderId: {}", orderId);
            throw new OrderInvalidStatusTransitionException(
                    "Invalid operation: status cannot be changed for already DELIVERED order."
            );
        }

        // Business Logic: Validate transition per state machine
        OrderEnum current = order.getOrderStatus();
        OrderEnum target = request.getOrderStatus();

        if (!isValidTransition(current, target)) {
            log.warn("Invalid transition detected. orderId: {}, from: {} to: {}", orderId, current, target);
            throw new OrderInvalidStatusTransitionException(
                    String.format("Invalid transition: %s → %s. Allowed transitions: PENDING → SHIPPED → DELIVERED.", current, target)
            );
        }

        order.setOrderStatus(target);
        Order saved = orderRepository.save(order);
        log.info("Order status updated successfully. orderId: {}, from: {} to: {}", orderId, current, target);
        return toResponseDTO(saved);
    }

    /**
     * Business logic for cancelling an order.
     * Rule: Can only cancel if PENDING or SHIPPED.
     * Cannot cancel if DELIVERED or already CANCELLED.
     * @param orderId ID of the order to cancel.
     * @throws OrderNotFoundException               if order does not exist.
     * @throws OrderCancellationNotAllowedException if status is final.
     */
    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(long orderId) {
        log.info("Initializing order cancellation for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for cancellation. orderId: {}", orderId);
                    return new OrderNotFoundException(NOT_FOUND_MSG + orderId);
                });

        // Logic check: Deny if Delivered or already Cancelled
        if (order.getOrderStatus() == OrderEnum.DELIVERED || order.getOrderStatus() == OrderEnum.CANCELLED) {
            log.warn("Cancellation rejected. orderId: {}, current status: {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException(
                    "Cannot cancel an order that is already " + order.getOrderStatus());
        }

        // Logic check: Allow only if Pending or Shipped
        order.setOrderStatus(OrderEnum.CANCELLED);
        Order saved=orderRepository.save(order);
        log.info("Order cancelled successfully. orderId: {}", orderId);
        return toResponseDTO(saved);
    }


    /**
     * Administrative soft delete. Marks the isDeleted flag as true in MySQL.
     * @param orderId ID of the order to delete.
     * @throws OrderNotFoundException if the ID does not exist.
     */
    @Override
    @Transactional
    public void softDeleteOrder(long orderId) {
        log.info("Initializing order soft delete for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for soft delete. orderId: {}", orderId);
                    return new OrderNotFoundException(NOT_FOUND_MSG + orderId);
                });

        if (order.isDeleted()) {
            log.warn("Soft delete skipped: order already marked as deleted. orderId: {}", orderId);
            return; // no-op if already soft-deleted
        }

        order.setDeleted(true);
        orderRepository.save(order);
        log.info("Order soft-deleted successfully. orderId: {}", orderId);
    }


    /**
     * user soft delete. Marks the isDeleted flag as true in MySQL.
     * @param userId ID of the order to delete.
     * @throws OrderNotFoundException if the ID does not exist.
     */
    @Override
    @Transactional
    public void softDeleteUserOrder(long userId) {
        log.info("Initializing soft delete for all orders of userId: {}", userId);

        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders == null || orders.isEmpty()) {
            log.warn("No orders found to soft delete for userId: {}", userId);
            throw new OrderNotFoundException(NOT_FOUND_MSG + userId);
        }

        int skipped = 0;
        int updated = 0;

        for (Order order : orders) {
            if (order.isDeleted()) {
                skipped++;
                log.warn("Skipping already soft-deleted order. orderId: {}", order.getOrderId());
                continue;
            }
            order.setDeleted(true);
            updated++;
        }

        // Persist only if there is anything to update
        if (updated > 0) {
            orderRepository.saveAll(orders);
        }

        log.info("Soft delete completed for userId: {}. updated: {}, skipped: {}", userId, updated, skipped);
    }


    /**
     * Retrieves all active orders from the database.
     *  @return List of OrderResponseDTO.
     *  @throws OrderNotFoundException if no orders exist in the system.
     */
    @Override
    public List<OrderResponseDTO> getOrderAll() {
        log.info("Initializing all orders fetch");
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            log.warn("No orders found in the system.");
            throw new OrderNotFoundException("No orders found in the system.");
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }
        log.info("All orders fetched successfully. count: {}", responseList.size());
        return responseList;
    }

    /**
     * Retrieves a specific order by its ID.
     * * @param orderId The ID of the order to find.
     * * @return Optional containing OrderResponseDTO.
     * @throws OrderNotFoundException if the ID does not exist.
     */
    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        log.info("Fetching order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found. orderId: {}", orderId);
                    return new OrderNotFoundException(NOT_FOUND_MSG + orderId);
                });

        log.info("Order fetched successfully. orderId: {}", orderId);
        return Optional.of(toResponseDTO(order));
    }

    /**
     * Retrieves all orders associated with a specific user.
     * Filters out soft-deleted orders automatically via @SQLRestriction.
     *
     * @param userId The unique identifier of the user.
     * @return List of OrderResponseDTO belonging to the user.
     * @throws OrderNotFoundException if no orders exist for the given user.
     */
    @Override
    public List<OrderResponseDTO> getOrdersByUserId(long userId) {
        log.info("Fetching orders for userId: {}", userId);

        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            log.warn("No orders found for userId: {}", userId);
            throw new OrderNotFoundException("No orders found for user with ID: " + userId);
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }

        log.info("Orders fetched successfully for userId: {}. count: {}", userId, responseList.size());
        return responseList;
    }

    /**
     * Retrieves orders filtered by their current status.
     * * @param status The OrderEnum status to filter by.
     * * @return List of OrderResponseDTO.
     * @throws OrderNotFoundException if no orders match the given status.
     */
    @Override
    public List<OrderResponseDTO> getOrdersByStatus(OrderEnum status) {
        log.info("Fetching orders by status: {}", status);
        List<Order> orders = orderRepository.findByOrderStatus(status);

        if (orders.isEmpty()) {
            log.warn("No orders found with status: {}", status);
            throw new OrderNotFoundException("No orders found with status: " + status);
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }
        log.info("Orders fetched successfully by status: {}. count: {}", status, responseList.size());
        return responseList;
    }

    /**
     * Helper method to map Order Entity to OrderResponseDTO.
     *  @param order The source entity.
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

    /**
     * State machine rule for order status updates via /changeOrderStatus.
     * Allowed transitions: PENDING → SHIPPED → DELIVERED.
     * Cancellation is only permitted via /cancelOrder.
     */
    private boolean isValidTransition(OrderEnum from, OrderEnum to) {
        switch (from) {
            case PENDING:
                return to == OrderEnum.SHIPPED;
            case SHIPPED:
                return to == OrderEnum.DELIVERED;
            case DELIVERED,CANCELLED:
            default:
                return false;
        }
    }
}

