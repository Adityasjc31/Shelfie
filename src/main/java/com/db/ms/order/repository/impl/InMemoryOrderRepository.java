package com.db.ms.order.repository.impl;

import com.db.ms.order.enums.OrderEnum;
import com.db.ms.order.model.Order;
import com.db.ms.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

/**
 * In-memory repository implementation for Order entity.

 * Responsibilities:
 * - Store and retrieve orders using a thread-safe map.
 * - Generate identifiers when needed using an atomic sequence.

 * Design notes:
 * - Repository layer should not throw domain exceptions. It returns null or empty Optional,
 *   and the Service layer is responsible for translating these into module-specific exceptions
 *   such as OrderNotFoundException or OrderNotPlacedException.
 * - Logging is provided to aid observability during development and testing.
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Repository
@Slf4j
public class InMemoryOrderRepository implements OrderRepository {

    /**
     * In-memory storage for orders keyed by orderId.
     */
    private final Map<Long, Order> store = new ConcurrentHashMap<>();

    /**
     * Auto-increment sequence for generating order identifiers.
     */
    private final AtomicLong sequence = new AtomicLong(1000L);


// Add these imports at the top of InMemoryOrderRepository


    /**
     * Initializes repository with sample order data for testing.
     * This method is called after the bean is constructed.
     *
     * Notes:
     * - Uses the existing save(order) behavior (id generated from sequence if not set).
     * - Since save does not mutate orderId in the object, retrieving by id requires
     *   knowing the generated storage key. For testing list queries (findAll/findByStatus),
     *   this is sufficient.
     */
    @PostConstruct
    public void initializeSampleData() {
        log.info("Initializing repository with sample order data");

        // Sample orders: different users, multiple books, varied amounts and statuses
        createSampleOrder(
                10001L,
                List.of(101L, 102L),           // 2 books
                1499.00,
                OrderEnum.PENDING
        );

        createSampleOrder(
                10002L,
                List.of(103L),                 // single book
                499.00,
                OrderEnum.SHIPPED
        );

        createSampleOrder(
                10003L,
                Arrays.asList(104L, 105L, 106L, 107L, 108L),     // 5 books
                2299.00,
                OrderEnum.DELIVERED
        );

        createSampleOrder(
                10005L,
                List.of(109L),                 // out-of-stock example when integrated later
                399.00,
                OrderEnum.PENDING
        );

        // More samples to increase dataset variety
        createSampleOrder(10006L, List.of(110L, 111L), 999.00, OrderEnum.SHIPPED);
        createSampleOrder(10007L, List.of(112L),       299.00, OrderEnum.DELIVERED);
        createSampleOrder(10008L, List.of(113L, 114L), 1299.00, OrderEnum.PENDING);
        createSampleOrder(10009L, List.of(115L),       599.00,  OrderEnum.SHIPPED);
        createSampleOrder(10010L, List.of(116L, 117L), 1799.00, OrderEnum.DELIVERED);

        log.info("Sample data initialized: {} order records", store.size());
    }

    /**
     * Helper method to create and persist a sample order.
     *
     * @param userId            the user identifier
     * @param bookIds           list of book identifiers in the order
     * @param totalAmount       total order amount
     * @param status            order lifecycle status
     */
    private void createSampleOrder(Long userId, List<Long> bookIds, double totalAmount, OrderEnum status) {
        Order order = new Order();
        order.setUserId(userId);
        order.setBookId(bookIds);
        order.setOrderTotalAmount(totalAmount);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderStatus(status);

        // Persist using repository save (will use generated storage id if orderId <= 0)
        save(order);

        log.debug("Created sample order: userId={}, books={}, total={}, status={}",
                userId, bookIds, totalAmount, status);
    }


    /**
     * Saves an order. If the order has no id or an id less than or equal to zero,
     * a new id is computed and used as the storage key. The order instance itself
     * is not mutated to set the id, matching the original behavior.
     *
     * @param order the order to save
     * @return the saved order instance
     */
    @Override
    public Order save(Order order) {
        long id = order.getOrderId() > 0 ? order.getOrderId() : sequence.incrementAndGet();
        // order.setOrderId(id); // intentionally left commented per original code
        store.put(id, order);
        log.debug("Saved order with storage id={}, order={}", id, order);
        return order;
    }

    /**
     * Retrieves all orders.
     *
     * @return list of all orders
     */
    @Override
    public List<Order> findAll() {
        List<Order> all = new ArrayList<>(store.values());
        log.debug("Fetched all orders, count={}", all.size());
        return all;
    }

    /**
     * Retrieves an order by id.
     *
     * @param orderId the order id
     * @return Optional containing the order if found, otherwise empty
     */
    @Override
    public Optional<Order> findById(long orderId) {
        Order found = store.get(orderId);
        log.debug("Lookup order by id={}, found={}", orderId, found != null);
        return Optional.ofNullable(found);
    }

    /**
     * Retrieves orders by status.
     *
     * @param status the order status
     * @return list of orders matching the status
     */
    @Override
    public List<Order> findByStatus(OrderEnum status) {
        List<Order> result = store.values().stream()
                .filter(o -> o.getOrderStatus() == status)
                .toList();
        log.debug("Fetched orders by status={}, count={}", status, result.size());
        return result;
    }

    /**
     * Updates an existing order.
     *
     * Behavior:
     * - If the order does not exist in the store, this method returns null and logs a warning.
     * - If the order exists, it is replaced and returned.
     *
     * @param order the order with updated fields
     * @return the updated order if present, otherwise null
     */
    @Override
    public Order update(Order order) {
        if (!store.containsKey(order.getOrderId())) {
            log.warn("Update skipped: order not found in repository, orderId={}", order.getOrderId());
            return null;
        }
        store.put(order.getOrderId(), order);
        log.debug("Updated order, orderId={}, order={}", order.getOrderId(), order);
        return order;
    }

    /**
     * Deletes an order by id. If no order exists, the operation is a no-op.
     *
     * @param orderId the order id to delete
     */
    @Override
    public void deleteById(long orderId) {
        boolean existed = store.remove(orderId) != null;
        log.debug("Deleted order by id={}, existed={}", orderId, existed);
    }
}
