package com.book.management.order.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderPriceStockResponseDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderInvalidStatusTransitionException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for Order operations.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    // Exception type constants for centralized handling
    private static final String IAE = "IllegalArgumentException";
    private static final String NFE = "OrderNotFoundException";
    private static final String ONFRE = "Order not found with ID:";
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request, OrderPriceStockResponseDTO priceStock) throws OrderNotPlacedException {
        // ... (Place order implementation remains the same, throwing IAE and OrderNotPlacedException)
        log.info("Order placement started: userId={}, itemsCount={}",
                request == null ? null : request.getUserId(),
                (request == null || request.getBookOrder() == null) ? 0 : request.getBookOrder().size());

        // --- VALIDATION (Keep IAE checks) ---
        if (request == null) {
            handleValidationOrNotFoundError(IAE, "Place order failed: request is null", "Request cannot be null");
        }
        if (request.getUserId() <= 0) {
            handleValidationOrNotFoundError(IAE, "Place order failed: invalid userId=" + request.getUserId(), "Invalid userId: " + request.getUserId());
        }
        Map<Long, Integer> bookOrder = request.getBookOrder();
        if (bookOrder == null || bookOrder.isEmpty()) {
            handleValidationOrNotFoundError(IAE, "Place order failed: empty bookOrder for userId=" + request.getUserId(), "bookOrder must not be empty");
        }
        for (Map.Entry<Long, Integer> e : bookOrder.entrySet()) {
            Long bookId = e.getKey();
            Integer qty = e.getValue();
            if (bookId == null || bookId <= 0) {
                handleValidationOrNotFoundError(IAE, "Invalid bookId found in bookOrder: " + bookId, "Invalid bookId in bookOrder: " + bookId);
            }
            if (qty == null || qty <= 0) {
                handleValidationOrNotFoundError(IAE, "Invalid quantity found for bookId=" + bookId + ": qty=" + qty, "Quantity must be > 0 for bookId: " + bookId);
            }
        }

        if (priceStock == null) {
            handleValidationOrNotFoundError(IAE, "Place order failed: priceStock is null", "priceStock cannot be null. Provide OrderPriceStockResponseDTO.");
        }
        Map<Long, Double> priceMap = priceStock.getBookPrice();
        Map<Long, Boolean> stockMap = priceStock.getBookStock();
        if (priceMap == null || priceMap.isEmpty()) {
            handleValidationOrNotFoundError(IAE, "Place order failed: priceMap is empty/null", "Price map is empty or null");
        }
        if (stockMap == null || stockMap.isEmpty()) {
            handleValidationOrNotFoundError(IAE, "Place order failed: stockMap is empty/null", "Stock map is empty or null");
        }

        // Defensive check for missing prices or insufficient stock
        for (Long bookId : bookOrder.keySet()) {
            if (!priceMap.containsKey(bookId)) {
                handleValidationOrNotFoundError(IAE, "Missing price for bookId=" + bookId, "Missing price for bookId: " + bookId);
            }
            Boolean available = stockMap.get(bookId);
            if (available == null || !available) {
                handleValidationOrNotFoundError(IAE, "Insufficient stock for bookId=" + bookId, "Insufficient stock for bookId: " + bookId);
            }
        }
        // --- END VALIDATION ---

        // Compute total amount
        double totalAmount = 0.0;
        for (Map.Entry<Long, Integer> e : bookOrder.entrySet()) {
            Long bookId = e.getKey();
            Integer qty = e.getValue();
            Double unitPrice = priceMap.get(bookId);
            totalAmount += (unitPrice * qty);
        }
        log.info("Computed order total: userId={}, totalAmount={}", request.getUserId(), totalAmount);

        // Build list of bookIds for the Order model
        List<Long> bookIds = new ArrayList<>(bookOrder.keySet());

        // Build and persist Order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setBookId(bookIds);
        order.setOrderTotalAmount(totalAmount);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderEnum.PENDING);

        try {
            Order saved = orderRepository.save(order);
            log.info("Order placed successfully: orderId={}, userId={}, itemsCount={}",
                    saved.getOrderId(), saved.getUserId(), bookIds.size());
            return toResponseDTO(saved);
        } catch (RuntimeException ex) {
            // Throw custom OrderNotPlacedException for persistence failure
            log.error("Order persistence failed: userId={}, error={}", request.getUserId(), ex.getMessage(), ex);
            throw new OrderNotPlacedException("Order could not be placed due to an internal system error.");
        }
    }

    @Override
    public List<OrderResponseDTO> getOrderAll() {
        // ... (getAll implementation remains the same)
        List<OrderResponseDTO> result = orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Fetched all orders: count={}", result.size());
        return result;
    }

    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        // FIX: If the Optional is empty, throw OrderNotFoundException immediately.
        log.info("Fetching order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", orderId);
                    return new OrderNotFoundException(ONFRE + orderId);
                });

        log.info("Order found: orderId={}", orderId);
        return Optional.of(toResponseDTO(order));
    }

    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        // ... (getOrderByStatus implementation remains the same)
        List<OrderResponseDTO> result = orderRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Fetched orders by status: status={}, count={}", status, result.size());
        return result;
    }

    /**
     * Changes the status of an existing order.
     */
    @Override
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request) throws OrderNotFoundException, OrderInvalidStatusTransitionException {

        // FIX: Throw OrderNotFoundException directly using orElseThrow.
        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Change status failed: order not found: orderId={}", orderId);
                    return new OrderNotFoundException(ONFRE + orderId);
                });

        // NOTE: If you add status validation, throw OrderInvalidStatusTransitionException here.
        // if (transition_is_invalid) {
        //    throw new OrderInvalidStatusTransitionException(...);
        // }

        existing.setOrderStatus(request.getOrderStatus());
        Order updated = orderRepository.update(existing);
        log.info("Order status updated: orderId={}, newStatus={}", orderId, updated.getOrderStatus());
        return toResponseDTO(updated);
    }

    /**
     * Cancels an order.
     */
    @Override
    public void cancelOrder(long orderId) throws OrderNotFoundException, OrderInvalidStatusTransitionException, OrderCancellationNotAllowedException {

        // FIX: Throw OrderNotFoundException directly using orElseThrow.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Cancel failed: order not found: orderId={}", orderId);
                    return new OrderNotFoundException(ONFRE + orderId);
                });

        // Example of using OrderCancellationNotAllowedException
        if (order.getOrderStatus() == OrderEnum.SHIPPED || order.getOrderStatus() == OrderEnum.DELIVERED) {
            log.warn("Cancel failed: orderId={} already in status {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException("Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        // NOTE: If you add status validation, throw OrderInvalidStatusTransitionException here.

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
            // Throw unchecked IllegalArgumentException immediately
            throw new IllegalArgumentException(errorMessage);
        } else if (NFE.equals(exceptionType)) {
            // NFE is now RuntimeException, throwing it directly.
            throw new OrderNotFoundException(errorMessage);
        } else {
            // Default to a generic runtime exception if an unknown type is passed
            throw new IllegalArgumentException("Unhandled exception type: " + exceptionType + ". Details: " + errorMessage);
        }
    }
}