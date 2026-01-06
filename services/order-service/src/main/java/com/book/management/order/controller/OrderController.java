
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
 * Logging is handled here for key events and outcomes.
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
     *
     * @param request Validated DTO containing user ID and items.
     * @return ResponseEntity containing the created OrderResponseDTO.
     */
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody PlaceOrderRequestDTO request) {
        log.info("POST : /api/v1/order/place | Initiating order placement | userId: {} | itemsCount: {}",
                request.getUserId(), request.getBookOrder() != null ? request.getBookOrder().size() : 0);

        OrderResponseDTO response = orderService.placeOrder(request);

        log.info("POST : /api/v1/order/place | Order created | orderId: {} | userId: {} | status: {}",
                response.getOrderId(), response.getUserId(), response.getOrderStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all active (non-deleted) orders.
     *
     * @return List of all orders.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.info("GET : /api/v1/order/getAll | Fetching all orders");
        List<OrderResponseDTO> orders = orderService.getOrderAll();
        log.info("GET : /api/v1/order/getAll | Fetched orders | count: {}", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves a specific order by its unique identifier.
     *
     * @param orderId Primary key of the order.
     * @return Order details or 404 if not found.
     */
    @GetMapping("/getById/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable long orderId) {
        log.info("GET : /api/v1/order/getById/{} | Fetching order by ID", orderId);

        return orderService.getOrderById(orderId)
                .map(order -> {
                    log.info("GET : /api/v1/order/getById/{} | Order found | status: {}", orderId, order.getOrderStatus());
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("GET : /api/v1/order/getById/{} | Order not found", orderId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId The unique identifier of the user.
     * @return List of orders belonging to the user or 404 if none found.
     */
    @GetMapping("/getByUser/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable long userId) {
        log.info("GET : /api/v1/order/getByUser/{} | Fetching orders for user", userId);

        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId);

        log.info("GET : /api/v1/order/getByUser/{} | Orders fetched | count: {}", userId, orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Filters orders by their current status.
     *
     * @param status The OrderEnum value to filter by.
     * @return List of matching orders.
     */
    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderEnum status) {
        log.info("GET : /api/v1/order/getByStatus/{} | Fetching orders by status", status);
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);
        log.info("GET : /api/v1/order/getByStatus/{} | Fetched orders | count: {}", status, orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Changes the status of an existing order.
     * @param orderId ID of the order to update.
     * @param request DTO containing the new status.
     * @return Updated order details.
     */
    @PatchMapping("/update/{orderId}")
    public ResponseEntity<OrderResponseDTO> changeOrderStatus(
            @PathVariable long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request) {

        log.info("PATCH : /api/v1/order/update/{} | Requested status update | target: {}", orderId, request.getOrderStatus());
        OrderResponseDTO updated = orderService.updateOrderStatus(orderId, request);
        log.info("PATCH : /api/v1/order/update/{} | Status updated | newStatus: {}", orderId, updated.getOrderStatus());
        return ResponseEntity.ok(updated);
    }

    /*
     * Triggers the business logic for order cancellation.
     * Only allowed for PENDING and SHIPPED orders.
     * @param orderId ID of the order to cancel.
     * @return 202 on success.
     */
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable long orderId) {
        log.info("DELETE : /api/v1/order/cancel/{} | Cancelling order", orderId);

        // NOTE: If your service returns void, change to ResponseEntity<Void> with 202/204
        OrderResponseDTO response = orderService.cancelOrder(orderId);

        log.info("DELETE : /api/v1/order/cancel/{} | Order cancelled | finalStatus: {}", orderId, response.getOrderStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * Performs a soft delete on an order (marking isDeleted as true).
     * @param orderId ID of the order to soft-delete.
     * @return 204 No Content.
     */
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long orderId) {
        log.info("DELETE : /api/v1/order/delete/{} | Soft deleting order", orderId);
        orderService.softDeleteOrder(orderId);
        log.info("DELETE : /api/v1/order/delete/{} | Soft delete complete", orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Performs a soft delete on orders for a user (marking isDeleted as true).
     * @param userId of the order to soft-delete.
     * @return 204 No Content.
     */
    @DeleteMapping("/deleteByUser/{userId}")
    public ResponseEntity<Void> deleteUserOrder(@PathVariable long userId) {
        log.info("DELETE : /api/v1/order/deleteByUser/{} | Soft deleting user orders", userId);
        orderService.softDeleteUserOrder(userId);
        log.info("DELETE : /api/v1/order/deleteByUser/{} | Soft delete complete", userId);
        return ResponseEntity.noContent().build();
    }
}
