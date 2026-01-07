package com.book.management.order.repository;

import com.book.management.order.enums.OrderEnum;
import com.book.management.order.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link OrderRepository}.
 *
 * Tests the JPA repository for Order entity operations including
 * CRUD operations, custom queries, and soft delete behavior.
 * Uses the configured database for testing.
 *
 * Test Coverage:
 * - Save and retrieve orders
 * - Find by order status
 * - Find by user ID
 * - Soft delete functionality (@SQLDelete)
 * - Soft delete filter (@SQLRestriction)
 * - Custom query methods
 * - Null handling
 * - Empty result handling
 *
 * Note: Disabled by default. Requires database configuration in application-test.properties
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Requires database configuration. Enable when test profile is configured.")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Order testOrder1;
    private Order testOrder2;
    private Order testOrder3;
    private Map<Long, Integer> items;

    /**
     * Setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Create test items
        items = new HashMap<>();
        items.put(101L, 2);
        items.put(102L, 1);

        // Create test orders
        testOrder1 = Order.builder()
                .userId(100L)
                .items(items)
                .orderDateTime(LocalDateTime.now())
                .orderTotalAmount(1500.0)
                .orderStatus(OrderEnum.PENDING)
                .isDeleted(false)
                .build();

        testOrder2 = Order.builder()
                .userId(100L)
                .items(items)
                .orderDateTime(LocalDateTime.now())
                .orderTotalAmount(2000.0)
                .orderStatus(OrderEnum.SHIPPED)
                .isDeleted(false)
                .build();

        testOrder3 = Order.builder()
                .userId(200L)
                .items(items)
                .orderDateTime(LocalDateTime.now())
                .orderTotalAmount(1000.0)
                .orderStatus(OrderEnum.PENDING)
                .isDeleted(false)
                .build();
    }

    /**
     * Test: Verify order can be saved and retrieved.
     *
     * Expected: Order is persisted and can be found by ID.
     */
    @Test
    void testSave_ShouldPersistOrder() {
        // Act
        Order savedOrder = orderRepository.save(testOrder1);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertNotNull(savedOrder.getOrderId());
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getOrderId());
        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getOrderId(), foundOrder.get().getOrderId());
        assertEquals(100L, foundOrder.get().getUserId());
        assertEquals(1500.0, foundOrder.get().getOrderTotalAmount());
    }

    /**
     * Test: Verify findByOrderStatus returns orders with matching status.
     *
     * Expected: Only orders with PENDING status are returned.
     */
    @Test
    void testFindByOrderStatus_WithPending_ShouldReturnPendingOrders() {
        // Arrange
        orderRepository.save(testOrder1);
        orderRepository.save(testOrder2);
        orderRepository.save(testOrder3);
        entityManager.flush();

        // Act
        List<Order> pendingOrders = orderRepository.findByOrderStatus(OrderEnum.PENDING);

        // Assert
        assertEquals(2, pendingOrders.size());
        assertTrue(pendingOrders.stream().allMatch(o -> o.getOrderStatus() == OrderEnum.PENDING));
    }

    /**
     * Test: Verify findByOrderStatus returns orders with SHIPPED status.
     *
     * Expected: Only orders with SHIPPED status are returned.
     */
    @Test
    void testFindByOrderStatus_WithShipped_ShouldReturnShippedOrders() {
        // Arrange
        orderRepository.save(testOrder1);
        orderRepository.save(testOrder2);
        orderRepository.save(testOrder3);
        entityManager.flush();

        // Act
        List<Order> shippedOrders = orderRepository.findByOrderStatus(OrderEnum.SHIPPED);

        // Assert
        assertEquals(1, shippedOrders.size());
        assertEquals(OrderEnum.SHIPPED, shippedOrders.get(0).getOrderStatus());
    }

    /**
     * Test: Verify findByOrderStatus returns empty list when no matches.
     *
     * Expected: Empty list for status with no orders.
     */
    @Test
    void testFindByOrderStatus_WithNoMatches_ShouldReturnEmptyList() {
        // Arrange
        orderRepository.save(testOrder1);
        entityManager.flush();

        // Act
        List<Order> deliveredOrders = orderRepository.findByOrderStatus(OrderEnum.DELIVERED);

        // Assert
        assertTrue(deliveredOrders.isEmpty());
    }

    /**
     * Test: Verify findByUserId returns all orders for specific user.
     *
     * Expected: All orders for user 100L are returned.
     */
    @Test
    void testFindByUserId_ShouldReturnUserOrders() {
        // Arrange
        orderRepository.save(testOrder1);
        orderRepository.save(testOrder2);
        orderRepository.save(testOrder3);
        entityManager.flush();

        // Act
        List<Order> userOrders = orderRepository.findByUserId(100L);

        // Assert
        assertEquals(2, userOrders.size());
        assertTrue(userOrders.stream().allMatch(o -> o.getUserId() == 100L));
    }

    /**
     * Test: Verify findByUserId returns empty list for user with no orders.
     *
     * Expected: Empty list for non-existent user.
     */
    @Test
    void testFindByUserId_WithNoOrders_ShouldReturnEmptyList() {
        // Arrange
        orderRepository.save(testOrder1);
        entityManager.flush();

        // Act
        List<Order> userOrders = orderRepository.findByUserId(999L);

        // Assert
        assertTrue(userOrders.isEmpty());
    }

    /**
     * Test: Verify soft delete sets isDeleted flag to true.
     *
     * Expected: After delete, order is marked as deleted but still in database.
     */
    @Test
    void testDelete_ShouldSoftDeleteOrder() {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder1);
        Long orderId = savedOrder.getOrderId();
        entityManager.flush();
        entityManager.clear();

        // Act
        orderRepository.deleteById(orderId);
        entityManager.flush();
        entityManager.clear();

        // Assert - order should not be found by normal queries due to @SQLRestriction
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        assertFalse(foundOrder.isPresent());

        // Verify it's actually soft deleted by checking with native query
        Order deletedOrder = entityManager
                .createQuery("SELECT o FROM Order o WHERE o.orderId = :id AND o.isDeleted = true", Order.class)
                .setParameter("id", orderId)
                .getSingleResult();
        assertTrue(deletedOrder.isDeleted());
    }

    /**
     * Test: Verify soft deleted orders are excluded from findAll.
     *
     * Expected: Soft deleted orders are not included in findAll results.
     */
    @Test
    void testFindAll_ShouldExcludeSoftDeletedOrders() {
        // Arrange
        Order order1 = orderRepository.save(testOrder1);
        Order order2 = orderRepository.save(testOrder2);
        entityManager.flush();

        // Soft delete one order
        orderRepository.deleteById(order1.getOrderId());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Order> allOrders = orderRepository.findAll();

        // Assert
        assertEquals(1, allOrders.size());
        assertEquals(order2.getOrderId(), allOrders.get(0).getOrderId());
    }

    /**
     * Test: Verify soft deleted orders are excluded from findByOrderStatus.
     *
     * Expected: Soft deleted orders with matching status are not returned.
     */
    @Test
    void testFindByOrderStatus_ShouldExcludeSoftDeletedOrders() {
        // Arrange
        Order order1 = orderRepository.save(testOrder1);
        orderRepository.save(testOrder3);
        entityManager.flush();

        // Soft delete one PENDING order
        orderRepository.deleteById(order1.getOrderId());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Order> pendingOrders = orderRepository.findByOrderStatus(OrderEnum.PENDING);

        // Assert
        assertEquals(1, pendingOrders.size());
        assertNotEquals(order1.getOrderId(), pendingOrders.get(0).getOrderId());
    }

    /**
     * Test: Verify soft deleted orders are excluded from findByUserId.
     *
     * Expected: Soft deleted orders for the user are not returned.
     */
    @Test
    void testFindByUserId_ShouldExcludeSoftDeletedOrders() {
        // Arrange
        Order order1 = orderRepository.save(testOrder1);
        orderRepository.save(testOrder2);
        entityManager.flush();

        // Soft delete one order for user 100L
        orderRepository.deleteById(order1.getOrderId());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Order> userOrders = orderRepository.findByUserId(100L);

        // Assert
        assertEquals(1, userOrders.size());
    }

    /**
     * Test: Verify order can be updated after saving.
     *
     * Expected: Updates are persisted correctly.
     */
    @Test
    void testUpdate_ShouldPersistChanges() {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder1);
        Long orderId = savedOrder.getOrderId();
        entityManager.flush();
        entityManager.clear();

        // Act
        Order orderToUpdate = orderRepository.findById(orderId).orElseThrow();
        orderToUpdate.setOrderStatus(OrderEnum.SHIPPED);
        orderToUpdate.setOrderTotalAmount(1800.0);
        orderRepository.save(orderToUpdate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderEnum.SHIPPED, updatedOrder.getOrderStatus());
        assertEquals(1800.0, updatedOrder.getOrderTotalAmount());
    }

    /**
     * Test: Verify items map is persisted and retrieved correctly.
     *
     * Expected: Items ElementCollection is stored and loaded properly.
     */
    @Test
    void testSave_WithItems_ShouldPersistItemsMap() {
        // Arrange
        Map<Long, Integer> customItems = new HashMap<>();
        customItems.put(201L, 5);
        customItems.put(202L, 3);
        customItems.put(203L, 1);
        testOrder1.setItems(customItems);

        // Act
        Order savedOrder = orderRepository.save(testOrder1);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Order foundOrder = orderRepository.findById(savedOrder.getOrderId()).get();
        assertEquals(3, foundOrder.getItems().size());
        assertEquals(5, foundOrder.getItems().get(201L));
        assertEquals(3, foundOrder.getItems().get(202L));
        assertEquals(1, foundOrder.getItems().get(203L));
    }

    /**
     * Test: Verify multiple orders can be saved in batch.
     *
     * Expected: All orders are persisted successfully.
     */
    @Test
    void testSaveAll_ShouldPersistMultipleOrders() {
        // Act
        List<Order> savedOrders = orderRepository.saveAll(List.of(testOrder1, testOrder2, testOrder3));
        entityManager.flush();

        // Assert
        assertEquals(3, savedOrders.size());
        assertEquals(3, orderRepository.findAll().size());
    }

    /**
     * Test: Verify count returns correct number of non-deleted orders.
     *
     * Expected: Count excludes soft deleted orders.
     */
    @Test
    void testCount_ShouldReturnCountOfNonDeletedOrders() {
        // Arrange
        Order order1 = orderRepository.save(testOrder1);
        orderRepository.save(testOrder2);
        orderRepository.save(testOrder3);
        entityManager.flush();

        // Soft delete one order
        orderRepository.deleteById(order1.getOrderId());
        entityManager.flush();

        // Act
        long count = orderRepository.count();

        // Assert
        assertEquals(2, count);
    }

    /**
     * Test: Verify existsById returns false for soft deleted order.
     *
     * Expected: Soft deleted orders are not considered to exist.
     */
    @Test
    void testExistsById_WithSoftDeletedOrder_ShouldReturnFalse() {
        // Arrange
        Order savedOrder = orderRepository.save(testOrder1);
        Long orderId = savedOrder.getOrderId();
        entityManager.flush();

        // Soft delete the order
        orderRepository.deleteById(orderId);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean exists = orderRepository.existsById(orderId);

        // Assert
        assertFalse(exists);
    }

    /**
     * Test: Verify order with different statuses can be saved.
     *
     * Expected: All order statuses are persisted correctly.
     */
    @Test
    void testSave_WithDifferentStatuses_ShouldPersistAll() {
        // Arrange
        testOrder1.setOrderStatus(OrderEnum.PENDING);
        testOrder2.setOrderStatus(OrderEnum.SHIPPED);
        testOrder3.setOrderStatus(OrderEnum.CANCELLED);

        // Act
        orderRepository.saveAll(List.of(testOrder1, testOrder2, testOrder3));
        entityManager.flush();

        // Assert
        List<Order> shippedOrders = orderRepository.findByOrderStatus(OrderEnum.SHIPPED);
        List<Order> cancelledOrders = orderRepository.findByOrderStatus(OrderEnum.CANCELLED);

        assertEquals(1, shippedOrders.size());
        assertEquals(1, cancelledOrders.size());
    }
}

