package com.db.ms.repository.Impl;


import com.db.ms.enums.OrderEnum;
import com.db.ms.model.Order;
import com.db.ms.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1000L); // starting id

    @Override
    public Order save(Order order) {
        long id = order.getOrderId() > 0 ? order.getOrderId() : sequence.incrementAndGet();
        order.setOrderId(id);
        store.put(id, order);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Order> findById(long orderId) {
        return Optional.ofNullable(store.get(orderId));
    }

    @Override
    public List<Order> findByStatus(OrderEnum status) {
        return store.values().stream()
                .filter(o -> o.getOrderStatus() == status)
                .toList();
    }

    @Override
    public Order update(Order order) {
        if (!store.containsKey(order.getOrderId())) {
            throw new NoSuchElementException("Order not found: " + order.getOrderId());
        }
        store.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public void deleteById(long orderId) {
        store.remove(orderId);
    }
}

