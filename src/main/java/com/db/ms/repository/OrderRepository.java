package com.db.ms.repository;

import com.db.ms.enums.OrderEnum;
import com.db.ms.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    // Create
    Order save(Order order);

    // Read
    List<Order> findAll();
    Optional<Order> findById(long orderId);
    List<Order> findByStatus(OrderEnum status);

    // Update
    Order update(Order order);

    // Delete
    void deleteById(long orderId);
}

