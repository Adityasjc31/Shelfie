package com.book.management.order.service.impl;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.dto.responsedto.ReduceInventoryStockResponseDTO;
import com.book.management.order.entity.Order;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderInvalidStatusTransitionException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderServiceImpl}.
 * Tests order lifecycle operations including placement, retrieval,
 * status updates, and cancellation with mocked dependencies.
 *
 * @author Rehan Ashraf
 * @version 2.0
 * @since 2024-12-07
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookServiceClient bookServiceClient;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private PlaceOrderRequestDTO placeOrderRequest;
    private Order order;
    private Map<Long, Integer> items;
    private Map<Long, Double> bookPrices;

    /**
     * Sets up test fixtures before each test method.
     * Initializes sample order data, items map, and book prices.
     */
    @BeforeEach
    void setUp() {
        items = new HashMap<>();
        items.put(1L, 2);
        items.put(2L, 3);

        bookPrices = new HashMap<>();
        bookPrices.put(1L, 10.0);
        bookPrices.put(2L, 20.0);

        placeOrderRequest = new PlaceOrderRequestDTO();
        placeOrderRequest.setUserId(100L);
        placeOrderRequest.setItems(items);

        order = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .totalAmount(80.0)
                .orderStatus(OrderEnum.PENDING)
                .orderDate(LocalDateTime.now())
                .build();
    }

    // ==================== placeOrder Tests ====================

    /**
     * Tests successful order placement when all services respond correctly.
     * Verifies order is saved with correct total amount and PENDING status.
     */
    @Test
    void placeOrder_Success() {
        GetBookPriceResponseDTO priceResponse = new GetBookPriceResponseDTO();
        priceResponse.setBookPrice(bookPrices);

        ReduceInventoryStockResponseDTO stockResponse = new ReduceInventoryStockResponseDTO();

        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class))).thenReturn(priceResponse);
        when(inventoryServiceClient.reduceStock(any(ReduceInventoryStockRequestDTO.class))).thenReturn(stockResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO result = orderService.placeOrder(placeOrderRequest);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(order.getUserId(), result.getUserId());
        assertEquals(80.0, result.getTotalAmount());
        assertEquals(OrderEnum.PENDING, result.getOrderStatus());

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, times(1)).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Tests order placement failure when Book Service is unavailable.
     * Verifies OrderNotPlacedException is thrown and no inventory/save operations occur.
     */
    @Test
    void placeOrder_BookServiceFails_ThrowsOrderNotPlacedException() {
        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class)))
                .thenThrow(new OrderNotPlacedException("Book service unavailable"));

        assertThrows(OrderNotPlacedException.class, () -> orderService.placeOrder(placeOrderRequest));

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, never()).reduceStock(any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * Tests order placement failure when Inventory Service reports insufficient stock.
     * Verifies OrderNotPlacedException is thrown and order is not saved.
     */
    @Test
    void placeOrder_InventoryServiceFails_ThrowsOrderNotPlacedException() {
        GetBookPriceResponseDTO priceResponse = new GetBookPriceResponseDTO();
        priceResponse.setBookPrice(bookPrices);

        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class))).thenReturn(priceResponse);
        when(inventoryServiceClient.reduceStock(any(ReduceInventoryStockRequestDTO.class)))
                .thenThrow(new OrderNotPlacedException("Insufficient stock"));

        assertThrows(OrderNotPlacedException.class, () -> orderService.placeOrder(placeOrderRequest));

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, times(1)).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        verify(orderRepository, never()).save(any());
    }

    // ==================== getOrderById Tests ====================

    /**
     * Tests successful retrieval of an order by its ID.
     * Verifies correct order details are returned.
     */
    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(order.getUserId(), result.getUserId());

        verify(orderRepository, times(1)).findById(1L);
    }

    /**
     * Tests retrieval of a non-existent order.
     * Verifies OrderNotFoundException is thrown.
     */
    @Test
    void getOrderById_NotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));

        verify(orderRepository, times(1)).findById(999L);
    }

    // ==================== getOrdersByUserId Tests ====================

    /**
     * Tests successful retrieval of orders by user ID.
     * Verifies list contains expected order data.
     */
    @Test
    void getOrdersByUserId_Success() {
        List<Order> orders = List.of(order);
        when(orderRepository.findByUserId(100L)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByUserId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order.getOrderId(), result.get(0).getOrderId());

        verify(orderRepository, times(1)).findByUserId(100L);
    }

    /**
     * Tests retrieval of orders for a user with no orders.
     * Verifies empty list is returned without errors.
     */
    @Test
    void getOrdersByUserId_EmptyList() {
        when(orderRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> result = orderService.getOrdersByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(orderRepository, times(1)).findByUserId(999L);
    }

    // ==================== getAllOrders Tests ====================

    /**
     * Tests successful retrieval of all orders.
     * Verifies list is returned with correct size.
     */
    @Test
    void getAllOrders_Success() {
        List<Order> orders = List.of(order);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(orderRepository, times(1)).findAll();
    }

    /**
     * Tests retrieval when no orders exist in the system.
     * Verifies empty list is returned.
     */
    @Test
    void getAllOrders_EmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(orderRepository, times(1)).findAll();
    }

    // ==================== getOrdersByStatus Tests ====================

    /**
     * Tests successful retrieval of orders filtered by status.
     * Verifies returned orders have the requested status.
     */
    @Test
    void getOrdersByStatus_Success() {
        List<Order> orders = List.of(order);
        when(orderRepository.findByOrderStatus(OrderEnum.PENDING)).thenReturn(orders);

        List<OrderResponseDTO> result = orderService.getOrdersByStatus(OrderEnum.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderEnum.PENDING, result.get(0).getOrderStatus());

        verify(orderRepository, times(1)).findByOrderStatus(OrderEnum.PENDING);
    }

    // ==================== updateOrderStatus Tests ====================

    /**
     * Tests valid status transition from PENDING to SHIPPED.
     * Verifies order status is updated and saved correctly.
     */
    @Test
    void updateOrderStatus_PendingToShipped_Success() {
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.SHIPPED);

        Order updatedOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .totalAmount(80.0)
                .orderStatus(OrderEnum.SHIPPED)
                .orderDate(order.getOrderDate())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, updateRequest);

        assertNotNull(result);
        assertEquals(OrderEnum.SHIPPED, result.getOrderStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Tests valid status transition from SHIPPED to DELIVERED.
     * Verifies order status is updated correctly.
     */
    @Test
    void updateOrderStatus_ShippedToDelivered_Success() {
        order.setOrderStatus(OrderEnum.SHIPPED);
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.DELIVERED);

        Order updatedOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .totalAmount(80.0)
                .orderStatus(OrderEnum.DELIVERED)
                .orderDate(order.getOrderDate())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, updateRequest);

        assertNotNull(result);
        assertEquals(OrderEnum.DELIVERED, result.getOrderStatus());
    }

    /**
     * Tests invalid status transition (PENDING to DELIVERED directly).
     * Verifies OrderInvalidStatusTransitionException is thrown.
     */
    @Test
    void updateOrderStatus_InvalidTransition_ThrowsException() {
        order.setOrderStatus(OrderEnum.PENDING);
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderInvalidStatusTransitionException.class,
                () -> orderService.updateOrderStatus(1L, updateRequest));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    /**
     * Tests status update on non-existent order.
     * Verifies OrderNotFoundException is thrown.
     */
    @Test
    void updateOrderStatus_OrderNotFound_ThrowsException() {
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.SHIPPED);

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderStatus(999L, updateRequest));
    }

    // ==================== cancelOrder Tests ====================

    /**
     * Tests successful cancellation of a PENDING order.
     * Verifies order status changes to CANCELLED.
     */
    @Test
    void cancelOrder_PendingOrder_Success() {
        Order cancelledOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .totalAmount(80.0)
                .orderStatus(OrderEnum.CANCELLED)
                .orderDate(order.getOrderDate())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderEnum.CANCELLED, result.getOrderStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Tests cancellation attempt on a DELIVERED order.
     * Verifies OrderCancellationNotAllowedException is thrown.
     */
    @Test
    void cancelOrder_DeliveredOrder_ThrowsException() {
        order.setOrderStatus(OrderEnum.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationNotAllowedException.class,
                () -> orderService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    /**
     * Tests cancellation attempt on an already CANCELLED order.
     * Verifies OrderCancellationNotAllowedException is thrown.
     */
    @Test
    void cancelOrder_AlreadyCancelled_ThrowsException() {
        order.setOrderStatus(OrderEnum.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationNotAllowedException.class,
                () -> orderService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    /**
     * Tests cancellation of a non-existent order.
     * Verifies OrderNotFoundException is thrown.
     */
    @Test
    void cancelOrder_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(999L));
    }
}
