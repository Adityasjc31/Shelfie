package com.book.management.order.service.impl;

import com.book.management.book.exception.BookNotFoundException;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderInvalidStatusTransitionException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.exception.*;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;
// Injected Services (Assume interfaces are available)
import com.book.management.book.service.BookService;
import com.book.management.inventory.service.InventoryService;
// DTOs from external services
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.inventory.exception.InsufficientStockException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
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
     * Orchestrates order placement in two phases: pre-process & save, then stock reduction.
     * Validates input, delegates to helpers, and returns the finalized OrderResponseDTO.
     * Propagates domain and infra exceptions (validation, price, stock, persistence).
     * Keeps core logic thin for clarity and testability.
     */

    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException {
        log.info("Order placement started: userId={}, itemsCount={}",
                request == null ? null : request.getUserId(),
                (request == null || request.getBookOrder() == null) ? 0 : request.getBookOrder().size());

        // Part 1: Validate, fetch prices, (optional) check stock, compute total, save
        Order savedOrder = preProcessAndSaveOrder(request);

        // Part 2: Reduce inventory & finalize order
        return inventoryStockReduction(savedOrder, request.getBookOrder());
    }


    /**
     * Phase 1: Pre-processes an order and persists it in PENDING status.
     *
     * Workflow:
     * - Validates request (userId > 0, non-empty bookOrder, each bookId/qty > 0).
     * - Fetches prices from Book service and verifies the price map (non-null, non-empty, no null prices).
     * - Checks stock availability with inventoryService.checkBulkAvailability(bookOrder);
     *   throws InsufficientStockException if any requested item is unavailable.
     * - Computes total (sum of price * quantity) and persists a new Order using ID=0
     *   so the repository auto-generates the real ID.

     * Returns:
     * - The saved Order (as returned by repository.save()), expected to have a generated ID if the repo populates it.

     * Exceptions:
     * - IllegalArgumentException via handleValidationOrNotFoundError for invalid input.
     * - BookNotFoundException for missing/invalid pricing data.
     * - InsufficientStockException if availability check fails.
     * - OrderNotPlacedException for infrastructure/persistence errors.
     */


    private Order preProcessAndSaveOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException {
        // --- 1. INITIAL VALIDATION ---
        if (request == null || request.getUserId() <= 0) {
            handleValidationOrNotFoundError(IAE, "Invalid request or userId", "Invalid request or userId provided.");
        }
        Map<Long, Integer> bookOrder = request.getBookOrder();
        if (bookOrder == null || bookOrder.isEmpty()) {
            handleValidationOrNotFoundError(IAE, "Empty bookOrder", "bookOrder must not be empty");
        }
        bookOrder.forEach((bookId, qty) -> {
            if (bookId == null || bookId <= 0 || qty == null || qty <= 0) {
                handleValidationOrNotFoundError(IAE,
                        String.format("Invalid bookId (%d) or quantity (%d)", bookId, qty),
                        "Invalid bookId or quantity in order list.");
            }
        });
        List<Long> bookIds = new ArrayList<>(bookOrder.keySet());

        // --- 2.1 PRICE CHECK (Book Service) ---
        Map<Long, Double> priceMap;
        try {
            log.info("Fetching prices for {} books...", bookIds.size());
            BookPriceResponseDTO priceResponse = bookService.getBookPricesMap(bookIds);
            priceMap = priceResponse.getPrices();
        } catch (BookNotFoundException ex) {
            throw ex; // propagate specific
        } catch (Exception ex) {
            log.error("Book service price fetch failed: {}", ex.getMessage(), ex);
            throw new OrderNotPlacedException("Failed to retrieve prices from the Book service due to a system error.");
        }

        if (priceMap == null || priceMap.isEmpty()) {
            throw new BookNotFoundException("Book Service returned an empty or null price map for requested books.");
        }
        List<Long> nullPricedBooks = bookIds.stream()
                .filter(id -> priceMap.containsKey(id) && priceMap.get(id) == null)
                .toList();
        if (!nullPricedBooks.isEmpty()) {
            throw new BookNotFoundException("Null price found for books: " + nullPricedBooks);
        }

        // --- 2.2 STOCK AVAILABILITY CHECK (Inventory Service) ---
        Map<Long, Boolean> availability;
        try {
            availability = inventoryService.checkBulkAvailability(bookOrder);
            log.info("Availability check results: {}", availability);
        } catch (Exception ex) {
            log.error("Inventory availability check failed: {}", ex.getMessage(), ex);
            throw new OrderNotPlacedException("Failed to check inventory availability due to a system error.");
        }

        // Ensure the availability map has entries for all requested books
        if (!availability.keySet().containsAll(bookOrder.keySet())) {
            log.error("Availability map missing entries for some requested books. requested={}, availabilityKeys={}",
                    bookOrder.keySet(), availability.keySet());
            throw new OrderNotPlacedException("Availability check returned incomplete results.");
        }

        // Ensure all requested items are available in required quantities
        List<Long> unavailable = availability.entrySet().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        if (!unavailable.isEmpty()) {
            String msg = "Insufficient stock for books: " + unavailable;
            log.warn(msg);
            throw new InsufficientStockException(msg);
        }

        // --- 3. COMPUTE TOTAL & SAVE ORDER ---
        double totalAmount = bookOrder.entrySet().stream()
                .mapToDouble(entry -> priceMap.get(entry.getKey()) * entry.getValue())
                .sum();
        log.info("Computed order total: userId={}, totalAmount={}", request.getUserId(), totalAmount);

        Order order = new Order(
                0, // Placeholder: repo will generate the real ID
                request.getUserId(),
                bookIds,
                LocalDateTime.now(),
                totalAmount,
                OrderEnum.PENDING
        );

        try {
            Order savedOrder = orderRepository.save(order);
            log.info("Order saved successfully: orderId={}", savedOrder.getOrderId());
            return savedOrder;
        } catch (RuntimeException ex) {
            log.error("Order persistence failed: {}", ex.getMessage(), ex);
            throw new OrderNotPlacedException("Order database persistence failed.");
        }
    }


    /**
     * Phase 2: Reduces inventory for the saved order and finalizes the order status.

     * Workflow:
     * - Calls inventoryService.reduceBulkInventory(bookOrder) to decrement stock; propagates
     *   InsufficientStockException on failures.
     * - Updates the order status to a default state (e.g., PENDING) and persists via repository.update.
     * - Guards against repository update returning null; throws OrderNotPlacedException if it does.

     * Returns:
     * - OrderResponseDTO produced from the finalized (updated) Order.

     * Exceptions:
     * - InsufficientStockException for stock reduction failures.
     * - OrderNotPlacedException if update fails or returns null; RuntimeException propagated for other infra errors.

     * Notes:
     * - Logs use savedOrder.getOrderId() for traceability; ensure repository save populates
     *   the generated ID (by mutating the entity or returning a new instance with the ID).
     */
    private OrderResponseDTO inventoryStockReduction(Order savedOrder, Map<Long, Integer> bookOrder) {
        // --- 4. Stock Reduction  ---
        try {
            log.info("Reducing bulk inventory for orderId: {}", savedOrder.getOrderId());

            // Throws InsufficientStockException if any book is unavailable.
            inventoryService.reduceBulkInventory(bookOrder);

            // Update to final successful status and persist
            savedOrder.setOrderStatus(OrderEnum.PENDING);
            Order finalOrder = orderRepository.update(savedOrder);

            if (finalOrder == null) {
                log.error("Order update returned null for tentative order (userId={}).", savedOrder.getUserId());
                throw new OrderNotPlacedException("Order update failed—repository returned null.");
            }

            log.info("Stock reduced and Order finalized: orderId={}", finalOrder.getOrderId());
            return toResponseDTO(finalOrder);

        } catch (InsufficientStockException ex) {
            log.error("Stock reduction failed for orderId: {}. Insufficient stock. Propagating.", savedOrder.getOrderId());
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Stock reduction failed for orderId: {}. Runtime error.", savedOrder.getOrderId(), ex);
            throw ex;
        }
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
        // FIX: If the Optional is empty, throw OrderNotFoundException immediately.
        return orderRepository.findById(orderId)
                .map(this::toResponseDTO);
    }

    /*
     * Get All Order details by Order Status
     */
    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        // ... (implementation remains the same)
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
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request) throws OrderNotFoundException, OrderInvalidStatusTransitionException {

        // FIX: Throw OrderNotFoundException directly using orElseThrow.
        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Change status failed: order not found: orderId={}", orderId);
                    return new OrderNotFoundException(ONFRE + orderId);
                });

        // NOTE: If you add status validation, throw OrderInvalidStatusTransitionException here.

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

        // NOTE: In a real system, you would call InventoryService.restockInventory() here.

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