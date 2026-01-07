package com.book.management.order.model;

import com.book.management.order.enums.OrderEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Order} entity.
 *
 * Tests the Order entity model including all fields, constructors,
 * and builder pattern functionality.
 *
 * Test Coverage:
 * - Builder pattern functionality
 * - Getter and setter methods
 * - NoArgsConstructor and AllArgsConstructor
 * - Field validation
 * - Soft delete flag behavior
 * - Items map handling
 * - Equality and hash code (Lombok-generated)
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
class OrderTest {

    private Order order;
    private Map<Long, Integer> items;
    private LocalDateTime testDateTime;

    /**
     * Setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        items = new HashMap<>();
        items.put(101L, 2);
        items.put(102L, 1);
        items.put(103L, 3);

        testDateTime = LocalDateTime.of(2026, 1, 7, 10, 30, 0);

        order = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderDateTime(testDateTime)
                .orderTotalAmount(1500.0)
                .orderStatus(OrderEnum.PENDING)
                .isDeleted(false)
                .build();
    }

    /**
     * Test: Verify builder creates Order with all fields correctly set.
     *
     * Expected: All fields match the builder values.
     */
    @Test
    void testBuilder_WithAllFields_ShouldCreateOrderCorrectly() {
        // Assert
        assertEquals(1L, order.getOrderId());
        assertEquals(100L, order.getUserId());
        assertEquals(items, order.getItems());
        assertEquals(testDateTime, order.getOrderDateTime());
        assertEquals(1500.0, order.getOrderTotalAmount());
        assertEquals(OrderEnum.PENDING, order.getOrderStatus());
        assertFalse(order.isDeleted());
    }

    /**
     * Test: Verify no-args constructor creates Order with default values.
     *
     * Expected: Order is created with null/default values.
     */
    @Test
    void testNoArgsConstructor_ShouldCreateEmptyOrder() {
        // Act
        Order emptyOrder = new Order();

        // Assert
        assertNotNull(emptyOrder);
        assertNull(emptyOrder.getOrderId());
        assertNull(emptyOrder.getUserId());
        assertNull(emptyOrder.getItems());
        assertNull(emptyOrder.getOrderDateTime());
        assertEquals(0.0, emptyOrder.getOrderTotalAmount());
        assertNull(emptyOrder.getOrderStatus());
        assertFalse(emptyOrder.isDeleted());
    }

    /**
     * Test: Verify all-args constructor creates Order with provided values.
     *
     * Expected: Order is created with all provided values.
     */
    @Test
    void testAllArgsConstructor_ShouldCreateOrderWithAllFields() {
        // Act
        Order newOrder = new Order(2L, 200L, items, testDateTime, 2000.0, OrderEnum.SHIPPED, false);

        // Assert
        assertEquals(2L, newOrder.getOrderId());
        assertEquals(200L, newOrder.getUserId());
        assertEquals(items, newOrder.getItems());
        assertEquals(testDateTime, newOrder.getOrderDateTime());
        assertEquals(2000.0, newOrder.getOrderTotalAmount());
        assertEquals(OrderEnum.SHIPPED, newOrder.getOrderStatus());
        assertFalse(newOrder.isDeleted());
    }

    /**
     * Test: Verify setters update Order fields correctly.
     *
     * Expected: All fields can be updated via setters.
     */
    @Test
    void testSetters_ShouldUpdateFieldsCorrectly() {
        // Arrange
        LocalDateTime newDateTime = LocalDateTime.now();
        Map<Long, Integer> newItems = new HashMap<>();
        newItems.put(201L, 5);

        // Act
        order.setOrderId(10L);
        order.setUserId(500L);
        order.setItems(newItems);
        order.setOrderDateTime(newDateTime);
        order.setOrderTotalAmount(3000.0);
        order.setOrderStatus(OrderEnum.DELIVERED);
        order.setDeleted(true);

        // Assert
        assertEquals(10L, order.getOrderId());
        assertEquals(500L, order.getUserId());
        assertEquals(newItems, order.getItems());
        assertEquals(newDateTime, order.getOrderDateTime());
        assertEquals(3000.0, order.getOrderTotalAmount());
        assertEquals(OrderEnum.DELIVERED, order.getOrderStatus());
        assertTrue(order.isDeleted());
    }

    /**
     * Test: Verify items map can be modified after creation.
     *
     * Expected: Items map is mutable and changes are reflected.
     */
    @Test
    void testItems_ShouldBeMutableMap() {
        // Act
        order.getItems().put(104L, 4);
        order.getItems().remove(101L);

        // Assert
        assertTrue(order.getItems().containsKey(104L));
        assertFalse(order.getItems().containsKey(101L));
        assertEquals(4, order.getItems().get(104L));
    }

    /**
     * Test: Verify order status can transition through different states.
     *
     * Expected: Order status can be updated to any enum value.
     */
    @Test
    void testOrderStatus_CanTransitionThroughStates() {
        // Act & Assert
        order.setOrderStatus(OrderEnum.PENDING);
        assertEquals(OrderEnum.PENDING, order.getOrderStatus());

        order.setOrderStatus(OrderEnum.SHIPPED);
        assertEquals(OrderEnum.SHIPPED, order.getOrderStatus());

        order.setOrderStatus(OrderEnum.DELIVERED);
        assertEquals(OrderEnum.DELIVERED, order.getOrderStatus());

        order.setOrderStatus(OrderEnum.DELIVERED);
        assertEquals(OrderEnum.DELIVERED, order.getOrderStatus());
    }

    /**
     * Test: Verify soft delete flag can be toggled.
     *
     * Expected: isDeleted flag can be set to true and false.
     */
    @Test
    void testIsDeleted_CanBeToggled() {
        // Initially false
        assertFalse(order.isDeleted());

        // Set to true
        order.setDeleted(true);
        assertTrue(order.isDeleted());

        // Set back to false
        order.setDeleted(false);
        assertFalse(order.isDeleted());
    }

    /**
     * Test: Verify order with cancelled status.
     *
     * Expected: Order can be set to CANCELLED status.
     */
    @Test
    void testOrderStatus_WithCancelled_ShouldBeSet() {
        // Act
        order.setOrderStatus(OrderEnum.CANCELLED);

        // Assert
        assertEquals(OrderEnum.CANCELLED, order.getOrderStatus());
    }

    /**
     * Test: Verify order total amount can be zero.
     *
     * Expected: Zero amount is valid.
     */
    @Test
    void testOrderTotalAmount_CanBeZero() {
        // Act
        order.setOrderTotalAmount(0.0);

        // Assert
        assertEquals(0.0, order.getOrderTotalAmount());
    }

    /**
     * Test: Verify order total amount can be very large.
     *
     * Expected: Large amounts are accepted.
     */
    @Test
    void testOrderTotalAmount_CanBeVeryLarge() {
        // Act
        double largeAmount = 999999.99;
        order.setOrderTotalAmount(largeAmount);

        // Assert
        assertEquals(largeAmount, order.getOrderTotalAmount());
    }

    /**
     * Test: Verify order with empty items map.
     *
     * Expected: Empty items map is valid.
     */
    @Test
    void testItems_CanBeEmptyMap() {
        // Act
        order.setItems(new HashMap<>());

        // Assert
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    /**
     * Test: Verify order with single item.
     *
     * Expected: Single item in map is handled correctly.
     */
    @Test
    void testItems_WithSingleItem_ShouldBeHandledCorrectly() {
        // Arrange
        Map<Long, Integer> singleItem = new HashMap<>();
        singleItem.put(999L, 1);

        // Act
        order.setItems(singleItem);

        // Assert
        assertEquals(1, order.getItems().size());
        assertEquals(1, order.getItems().get(999L));
    }

    /**
     * Test: Verify order with large quantity values in items.
     *
     * Expected: Large quantities are accepted.
     */
    @Test
    void testItems_WithLargeQuantities_ShouldBeAccepted() {
        // Arrange
        Map<Long, Integer> largeQuantities = new HashMap<>();
        largeQuantities.put(101L, 1000);
        largeQuantities.put(102L, 5000);

        // Act
        order.setItems(largeQuantities);

        // Assert
        assertEquals(1000, order.getItems().get(101L));
        assertEquals(5000, order.getItems().get(102L));
    }

    /**
     * Test: Verify order builder with partial fields.
     *
     * Expected: Builder allows setting only some fields.
     */
    @Test
    void testBuilder_WithPartialFields_ShouldCreateOrder() {
        // Act
        Order partialOrder = Order.builder()
                .userId(300L)
                .orderStatus(OrderEnum.PENDING)
                .build();

        // Assert
        assertNull(partialOrder.getOrderId());
        assertEquals(300L, partialOrder.getUserId());
        assertNull(partialOrder.getItems());
        assertNull(partialOrder.getOrderDateTime());
        assertEquals(0.0, partialOrder.getOrderTotalAmount());
        assertEquals(OrderEnum.PENDING, partialOrder.getOrderStatus());
        assertFalse(partialOrder.isDeleted());
    }

    /**
     * Test: Verify order datetime can be in the past.
     *
     * Expected: Past dates are accepted.
     */
    @Test
    void testOrderDateTime_CanBeInThePast() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        // Act
        order.setOrderDateTime(pastDate);

        // Assert
        assertEquals(pastDate, order.getOrderDateTime());
    }

    /**
     * Test: Verify order datetime can be in the future.
     *
     * Expected: Future dates are accepted.
     */
    @Test
    void testOrderDateTime_CanBeInTheFuture() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);

        // Act
        order.setOrderDateTime(futureDate);

        // Assert
        assertEquals(futureDate, order.getOrderDateTime());
    }

    /**
     * Test: Verify two orders with same values are equal (Lombok @Data generates equals).
     *
     * Expected: Orders with identical values are considered equal.
     */
    @Test
    void testEquals_WithIdenticalValues_ShouldBeEqual() {
        // Arrange
        Order order1 = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderDateTime(testDateTime)
                .orderTotalAmount(1500.0)
                .orderStatus(OrderEnum.PENDING)
                .isDeleted(false)
                .build();

        Order order2 = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderDateTime(testDateTime)
                .orderTotalAmount(1500.0)
                .orderStatus(OrderEnum.PENDING)
                .isDeleted(false)
                .build();

        // Assert
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    /**
     * Test: Verify two orders with different values are not equal.
     *
     * Expected: Orders with different values are not equal.
     */
    @Test
    void testEquals_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        Order order2 = Order.builder()
                .orderId(2L)
                .userId(200L)
                .items(items)
                .orderDateTime(testDateTime)
                .orderTotalAmount(2000.0)
                .orderStatus(OrderEnum.SHIPPED)
                .isDeleted(false)
                .build();

        // Assert
        assertNotEquals(order, order2);
    }

    /**
     * Test: Verify toString method includes key fields (Lombok @Data generates toString).
     *
     * Expected: String representation contains field values.
     */
    @Test
    void testToString_ShouldIncludeKeyFields() {
        // Act
        String orderString = order.toString();

        // Assert
        assertNotNull(orderString);
        assertTrue(orderString.contains("orderId"));
        assertTrue(orderString.contains("userId"));
        assertTrue(orderString.contains("orderStatus"));
    }
}

