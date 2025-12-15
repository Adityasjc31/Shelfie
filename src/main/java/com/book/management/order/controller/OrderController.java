package com.book.management.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderPriceStockResponseDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.service.OrderService;

import java.util.List;
// import java.util.NoSuchElementException; // No longer needed and removed

/**
 * REST Controller for Order Management.
 *
 * NOTE: Exception handling is now centralized in GlobalOrderExceptionHandler.
 * Controller methods are clean and focus only on the successful flow.
 *
 * @author Rehan Ashraf
 * @version 1.4 (Cleaned up exception handling for ControllerAdvice)
 * @since 2024-12-08
 */
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderPriceStockController orderPriceStockController;

    /**
     * Places a new order.
     * Exceptions (400, 500, 502) are handled by GlobalOrderExceptionHandler.
     */
    @PostMapping("/place")
    public ResponseEntity<OrderPriceStockResponseDTO> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        log.info("Received place order request: userId={}, items={}",
                request.getUserId(),
                request.getBookOrder() == null ? 0 : request.getBookOrder().size());

        // Exceptions from computePriceAndStock (e.g., 400, 502) are handled globally via ResponseStatusException.
        OrderPriceStockResponseDTO priceStock = orderPriceStockController.computePriceAndStock(request);

        // Exceptions from placeOrder (e.g., 400 IllegalArgumentException, 500 OrderNotPlacedException) are handled globally.
        OrderResponseDTO order = orderService.placeOrder(request,priceStock);
        log.info("Order placed successfully: orderId={}, userId={}", order.getOrderId(), order.getUserId());

        return new ResponseEntity<>(priceStock, HttpStatus.CREATED);
    }

    /**
     * Fetch all orders.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> all = orderService.getOrderAll();
        return ResponseEntity.ok(all);
    }

    /**
     * Fetch an order by ID.
     */
    @GetMapping("/getById/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable long orderId) {
        // Uses Optional handling to return 404 if not found (no exception needed here).
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Fetch orders by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderEnum status) {
        List<OrderResponseDTO> byStatus = orderService.getOrderByStatus(status);
        return ResponseEntity.ok(byStatus);
    }

    /**
     * Change status of an existing order.
     * Exceptions (404 OrderNotFoundException, 400 OrderInvalidStatusTransitionException)
     * are thrown by the service and handled by GlobalOrderExceptionHandler.
     */
    @PatchMapping("/change/status")
    public ResponseEntity<OrderResponseDTO> changeOrderStatus(@RequestBody UpdateOrderStatusRequestDTO request) {
        // Clean implementation: relies on the Service to throw exceptions on failure.
        OrderResponseDTO updated = orderService.changeOrderStatus(request.getOrderId(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Cancel an order.
     * Exceptions (404 OrderNotFoundException, 400 OrderCancellationNotAllowedException)
     * are thrown by the service and handled by GlobalOrderExceptionHandler.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable long orderId) {
        // Clean implementation: relies on the Service to throw exceptions on failure.
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}