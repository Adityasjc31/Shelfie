package com.db.ms.controller;


import com.db.ms.dto.responsedto.OrderResponseDTO;
import com.db.ms.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.dto.requestdto.UpdateOrderRequestDTO;
import com.db.ms.enums.OrderEnum;
import com.db.ms.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController

@RequestMapping("/orders")
public class OrderController {


    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    // Create: placeOrder
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        OrderResponseDTO created = orderService.placeOrder(request);
        return ResponseEntity.ok(created);
    }

    // Read: getOrderAll
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderResponseDTO>> getOrderAll() {
        return ResponseEntity.ok(orderService.getOrderAll());
    }

    // Read: getOrderById
    @GetMapping("/getById/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Read: getOrderByStatus
    @GetMapping("/getByStatus")
    public ResponseEntity<List<OrderResponseDTO>> getOrderByStatus(@RequestParam OrderEnum status) {
        return ResponseEntity.ok(orderService.getOrderByStatus(status));
    }

    // Update: changeOrderStatus
    @PatchMapping("/changeStatus/{orderId}")
    public ResponseEntity<OrderResponseDTO> changeOrderStatus(@PathVariable long orderId,
                                                              @RequestBody UpdateOrderRequestDTO request) {
        OrderResponseDTO updated = orderService.changeOrderStatus(orderId, request);
        return ResponseEntity.ok(updated);
    }

    // Delete: cancelOrder
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
