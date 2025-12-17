
package com.book.management.order.service.impl;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderInvalidStatusTransitionException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;
import com.book.management.book.service.BookService;
import com.book.management.inventory.service.InventoryService;
import com.book.management.order.util.OrderOpsUtils;
import com.book.management.inventory.exception.InsufficientStockException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.Optional;


 /**
 * Service for Order Management.
 *
 * @author Rehan Ashraf
 * @version 1.5 (Cleaned for direct service invocation)
 * @since 2024-12-15
 * Service implementation for Order operations, handling orchestration of Book and Inventory services.
 * Propagation of specific exceptions (BookNotFoundException, InsufficientStockException).
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final InventoryService inventoryService;

    // Exception type constants for centralized handling
    private static final String IAE = "IllegalArgumentException";
    private static final String NFE = "OrderNotFoundException";
    private static final String ONFRE = "Order not found with ID:";

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BookService bookService, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
        this.inventoryService = inventoryService;
    }

    /**
     * Orchestrates order placement: validation, price check, stock check, stock reduction,
     * compute total & save, then returns the OrderResponseDTO.
     */
    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException {
        log.info("Order placement started: userId={}, itemsCount={}",
                (request == null ? null : request.getUserId()),
                ((request == null || request.getBookOrder() == null) ? 0 : request.getBookOrder().size()));

        Order savedOrder = preProcessAndSaveOrder(request);
        return toResponseDTO(savedOrder);
    }

    /**
     * Phase 1: Executes the five util operations in order.
     * - initialValidation (via centralized handler)
     * - priceCheck
     * - stockCheck
     * - stockReduction
     * - computeTotalAndSaveOrder
     * <p>
     * Notes:
     * - Null-safety: avoid dereferencing request before validation completes.
     * - No method/constructor references: pass the handler via a lambda.
     */
    private Order preProcessAndSaveOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException {
        // 1) Initial validation (lambda, uses IAE)
        List<Long> bookIds = OrderOpsUtils.initialValidation(
                request,
                (exceptionType, logMessage, errorMessage) -> handleValidationOrNotFoundError(exceptionType, logMessage, errorMessage),
                IAE
        );

        // Safely read bookOrder after validation to satisfy static analysis
        final Map<Long, Integer> bookOrder = (request == null) ? Map.of() : request.getBookOrder();

        // 2) Price check
        Map<Long, Double> priceMap = OrderOpsUtils.priceCheck(bookService, bookIds);

        // 3) Stock availability check
        OrderOpsUtils.stockCheck(inventoryService, bookOrder);

        // 4) Stock reduction
        try {
            OrderOpsUtils.stockReduction(inventoryService, bookOrder);
        } catch (InsufficientStockException ex) {
            log.error("Stock reduction failed. userId={}", (request == null ? null : request.getUserId()));
            throw ex;
        }

        // 5) Compute total & save
        Order savedOrder = OrderOpsUtils.computeTotalAndSaveOrder(
                orderRepository,
                (request == null ? 0L : request.getUserId()),
                bookOrder,
                priceMap,
                bookIds,
                OrderEnum.PENDING
        );

        log.info("Order saved successfully: orderId={}", savedOrder.getOrderId());
        return savedOrder;
    }

    /*
     *  Get All Order details
     */
    @Override
    public List<OrderResponseDTO> getOrderAll() {
        List<OrderResponseDTO> result = orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Fetched all orders: count={}", result.size());
        return result;
    }

    /*
     * Get Order details by OrderId
     */
    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        log.info("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .map(this::toResponseDTO);
    }

    /*
     * Get All Order details by Order Status
     */
    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        List<OrderResponseDTO> result = orderRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Fetched orders by status: status={}, count={}", status, result.size());
        return result;
    }

    /**
     * Change the status of an existing order.
     */
    @Override
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request)
            throws OrderNotFoundException, OrderInvalidStatusTransitionException {
        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Change status failed: order not found: orderId={}", orderId);
                    return new OrderNotFoundException(ONFRE + orderId);
                });

        existing.setOrderStatus(request.getOrderStatus());
        Order updated = orderRepository.update(existing);
        log.info("Order status updated: orderId={}, newStatus={}", orderId, updated.getOrderStatus());
        return toResponseDTO(updated);
    }

    /**
     * Cancels an order.
     */
    @Override
    public void cancelOrder(long orderId)
            throws OrderNotFoundException, OrderInvalidStatusTransitionException, OrderCancellationNotAllowedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Cancel failed: order not found: orderId={}", orderId);
                    return new OrderNotFoundException(ONFRE + orderId);
                });

        if (order.getOrderStatus() == OrderEnum.SHIPPED || order.getOrderStatus() == OrderEnum.DELIVERED) {
            log.warn("Cancel failed: orderId={} already in status {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException("Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        orderRepository.deleteById(orderId);
        log.info("Order cancelled successfully: orderId={}", orderId);
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .bookIds(order.getBookId())
                .orderDateTime(order.getOrderDateTime())
                .orderTotalAmount(order.getOrderTotalAmount())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    /**
     * Centralized method to handle and THROW exceptions immediately.
     * Only throws RuntimeExceptions (IAE, NFE is now a RuntimeException)
     */
    private void handleValidationOrNotFoundError(String exceptionType, String logMessage, String errorMessage) {
        log.warn(logMessage);

        if (IAE.equals(exceptionType)) {
            throw new IllegalArgumentException(errorMessage);
        } else if (NFE.equals(exceptionType)) {
            throw new OrderNotFoundException(errorMessage);
        } else {
            throw new IllegalArgumentException("Unhandled exception type: " + exceptionType + ". Details: " + errorMessage);
        }
    }
}