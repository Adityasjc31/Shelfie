package com.book.management.order.controller;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order Management.
 *
 * @author Rehan Ashraf
 * @version 1.5 (Cleaned for direct service invocation)
 * @since 2024-12-15
 */
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;


    /**
     * Places a new order. The service layer handles all external calls.
     */
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        log.info("Received place order request: userId={}, items={}",
                request.getUserId(),
                request.getBookOrder() == null ? 0 : request.getBookOrder().size());

        // Call the service directly. Exceptions are handled globally.
        OrderResponseDTO order = orderService.placeOrder(request);

        log.info("Order placed successfully: orderId={}, userId={}", order.getOrderId(), order.getUserId());

        // Returns status CREATED (201)
        return new ResponseEntity<>(order, HttpStatus.CREATED);
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
        // Relies on OrderServiceImpl to return Optional. If empty, returns 404.
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
     */
    @PatchMapping("/change/status")
    public ResponseEntity<OrderResponseDTO> changeOrderStatus(@RequestBody UpdateOrderStatusRequestDTO request) {
        OrderResponseDTO updated = orderService.changeOrderStatus(request.getOrderId(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Cancel an order.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}