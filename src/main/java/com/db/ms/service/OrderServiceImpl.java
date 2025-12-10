package com.db.ms.service;

import com.db.ms.dto.responsedto.OrderResponseDTO;
import com.db.ms.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.dto.requestdto.UpdateOrderRequestDTO;
import com.db.ms.enums.OrderEnum;
import com.db.ms.entity.Order;
import com.db.ms.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create
    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        Order order=new Order();
        order.setUserId(request.getUserId());
        order.setBookId(request.getBookIds());
        order.setOrderTotalAmount(request.getOrderTotalAmount());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderEnum.PENDING);

        Order saved = orderRepository.save(order);
        return toResponseDTO(saved);
    }

    // Read
    @Override
    public List<OrderResponseDTO> getOrderAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();


    }

    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        return orderRepository.findById(orderId).map(this::toResponseDTO);
    }

    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Update
    @Override
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderRequestDTO request) {
        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        existing.setOrderStatus(request.getOrderStatus());
        Order updated = orderRepository.update(existing);
        return toResponseDTO(updated);
    }

    // Delete
    @Override
    public void cancelOrder(long orderId) {
        orderRepository.deleteById(orderId);
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
}
