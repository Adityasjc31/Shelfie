package com.book.management.order.controller;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order Management.
 * Provides endpoints for order lifecycle management, including placement,
 * retrieval, status updates, and cancellation.
 *
 * @author Rehan Ashraf
 * @version 2.1
 */
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Places a new order by orchestrating calls to Book and Inventory services.
     * * @param request Validated DTO containing user ID and items.
     * @return ResponseEntity containing the created OrderResponseDTO.
     */
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody PlaceOrderRequestDTO request) {
        log.info("Received place order request for userId={}", request.getUserId());
        OrderResponseDTO response = orderService.placeOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all active (non-deleted) orders.
     * * @return List of all orders.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getOrderAll());
    }

    /**
     * Retrieves a specific order by its unique identifier.
     * * @param orderId Primary key of the order.
     * @return Order details or 404 if not found.
     */
    @GetMapping("/getById/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Filters orders by their current status.
     * * @param status The OrderEnum value to filter by.
     * @return List of matching orders.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderEnum status) {
        return ResponseEntity.ok(orderService.getOrderByStatus(status));
    }

    /**
     * Updates the status of an existing order.
     * * @param orderId ID of the order to update.
     * @param request DTO containing the new status.
     * @return Updated order details.
     */
    @PatchMapping("/update/{orderId}")
    public ResponseEntity<OrderResponseDTO> changeOrderStatus(@PathVariable long orderId, @Valid @RequestBody UpdateOrderStatusRequestDTO request) {
        return ResponseEntity.ok(orderService.changeOrderStatus(orderId, request));
    }

    /**
     * Triggers the business logic for order cancellation.
     * Only allowed for PENDING and SHIPPED orders.
     * * @param orderId ID of the order to cancel.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Performs a soft delete on an order (marking isDeleted as true).
     * * @param orderId ID of the order to soft delete.
     * @return 204 No Content.
     */
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long orderId) {
        orderService.softDeleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}