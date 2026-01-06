package com.book.management.order.service.impl;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.model.Order;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        placeOrderRequest.setBookOrder(items);

        order = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(80.0)
                .orderStatus(OrderEnum.PENDING)
                .orderDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void placeOrder_Success() {
        GetBookPriceResponseDTO priceResponse = new GetBookPriceResponseDTO();
        priceResponse.setBookPrice(bookPrices);

        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class))).thenReturn(priceResponse);
        doNothing().when(inventoryServiceClient).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO result = orderService.placeOrder(placeOrderRequest);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(order.getUserId(), result.getUserId());
        assertEquals(80.0, result.getOrderTotalAmount());
        assertEquals(OrderEnum.PENDING, result.getOrderStatus());

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, times(1)).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_BookServiceFails_ThrowsOrderNotPlacedException() {
        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class)))
                .thenThrow(new OrderNotPlacedException("Book service unavailable"));

        assertThrows(OrderNotPlacedException.class, () -> orderService.placeOrder(placeOrderRequest));

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, never()).reduceStock(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_InventoryServiceFails_ThrowsOrderNotPlacedException() {
        GetBookPriceResponseDTO priceResponse = new GetBookPriceResponseDTO();
        priceResponse.setBookPrice(bookPrices);

        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class))).thenReturn(priceResponse);
        doThrow(new OrderNotPlacedException("Insufficient stock"))
                .when(inventoryServiceClient).reduceStock(any(ReduceInventoryStockRequestDTO.class));

        assertThrows(OrderNotPlacedException.class, () -> orderService.placeOrder(placeOrderRequest));

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, times(1)).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<OrderResponseDTO> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(order.getOrderId(), result.get().getOrderId());
        assertEquals(order.getUserId(), result.get().getUserId());

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void updateOrderStatus_PendingToShipped_Success() {
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.SHIPPED);

        Order updatedOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(80.0)
                .orderStatus(OrderEnum.SHIPPED)
                .orderDateTime(order.getOrderDateTime())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, updateRequest);

        assertNotNull(result);
        assertEquals(OrderEnum.SHIPPED, result.getOrderStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_ShippedToDelivered_Success() {
        order.setOrderStatus(OrderEnum.SHIPPED);
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.DELIVERED);

        Order updatedOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(80.0)
                .orderStatus(OrderEnum.DELIVERED)
                .orderDateTime(order.getOrderDateTime())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderResponseDTO result = orderService.updateOrderStatus(1L, updateRequest);

        assertNotNull(result);
        assertEquals(OrderEnum.DELIVERED, result.getOrderStatus());
    }

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

    @Test
    void updateOrderStatus_OrderNotFound_ThrowsException() {
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.SHIPPED);

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderStatus(999L, updateRequest));
    }

    @Test
    void cancelOrder_PendingOrder_Success() {
        Order cancelledOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(80.0)
                .orderStatus(OrderEnum.CANCELLED)
                .orderDateTime(order.getOrderDateTime())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderEnum.CANCELLED, result.getOrderStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void cancelOrder_DeliveredOrder_ThrowsException() {
        order.setOrderStatus(OrderEnum.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationNotAllowedException.class,
                () -> orderService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_AlreadyCancelled_ThrowsException() {
        order.setOrderStatus(OrderEnum.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationNotAllowedException.class,
                () -> orderService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(999L));
    }

    // ==================== getOrderAll Tests ====================

    @Test
    void getOrderAll_Success() {
        Order order2 = Order.builder()
                .orderId(2L)
                .userId(101L)
                .items(items)
                .orderTotalAmount(100.0)
                .orderStatus(OrderEnum.SHIPPED)
                .orderDateTime(LocalDateTime.now())
                .build();

        when(orderRepository.findAll()).thenReturn(java.util.List.of(order, order2));

        java.util.List<OrderResponseDTO> result = orderService.getOrderAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(2L, result.get(1).getOrderId());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderAll_Empty_ThrowsException() {
        when(orderRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderAll());

        verify(orderRepository, times(1)).findAll();
    }

    // ==================== getOrderByStatus Tests ====================

    @Test
    void getOrderByStatus_Success() {
        Order order2 = Order.builder()
                .orderId(2L)
                .userId(101L)
                .items(items)
                .orderTotalAmount(100.0)
                .orderStatus(OrderEnum.PENDING)
                .orderDateTime(LocalDateTime.now())
                .build();

        when(orderRepository.findByOrderStatus(OrderEnum.PENDING))
                .thenReturn(java.util.List.of(order, order2));

        java.util.List<OrderResponseDTO> result = orderService.getOrdersByStatus(OrderEnum.PENDING);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(OrderEnum.PENDING, result.get(0).getOrderStatus());
        assertEquals(OrderEnum.PENDING, result.get(1).getOrderStatus());

        verify(orderRepository, times(1)).findByOrderStatus(OrderEnum.PENDING);
    }

    @Test
    void getOrderByStatus_NoOrdersFound_ThrowsException() {
        when(orderRepository.findByOrderStatus(OrderEnum.DELIVERED))
                .thenReturn(java.util.Collections.emptyList());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrdersByStatus(OrderEnum.DELIVERED));

        verify(orderRepository, times(1)).findByOrderStatus(OrderEnum.DELIVERED);
    }

    // ==================== softDeleteOrder Tests ====================

    @Test
    void softDeleteOrder_Success() {
        order.setDeleted(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.softDeleteOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void softDeleteOrder_AlreadyDeleted_NoOp() {
        order.setDeleted(true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderService.softDeleteOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void softDeleteOrder_NotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.softDeleteOrder(999L));

        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    // ==================== softDeleteUserOrder Tests ====================

    @Test
    void softDeleteUserOrder_Success() {
        Order order2 = Order.builder()
                .orderId(2L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(100.0)
                .orderStatus(OrderEnum.SHIPPED)
                .orderDateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();
        order.setDeleted(false);

        when(orderRepository.findByUserId(100L)).thenReturn(java.util.List.of(order, order2));
        when(orderRepository.saveAll(any())).thenReturn(java.util.List.of(order, order2));

        assertDoesNotThrow(() -> orderService.softDeleteUserOrder(100L));

        verify(orderRepository, times(1)).findByUserId(100L);
        verify(orderRepository, times(1)).saveAll(any());
    }

    @Test
    void softDeleteUserOrder_NoOrdersFound_ThrowsException() {
        when(orderRepository.findByUserId(999L)).thenReturn(java.util.Collections.emptyList());

        assertThrows(OrderNotFoundException.class, () -> orderService.softDeleteUserOrder(999L));

        verify(orderRepository, times(1)).findByUserId(999L);
        verify(orderRepository, never()).saveAll(any());
    }

    @Test
    void softDeleteUserOrder_AllAlreadyDeleted_NoSave() {
        order.setDeleted(true);
        Order order2 = Order.builder()
                .orderId(2L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(100.0)
                .orderStatus(OrderEnum.SHIPPED)
                .orderDateTime(LocalDateTime.now())
                .isDeleted(true)
                .build();

        when(orderRepository.findByUserId(100L)).thenReturn(java.util.List.of(order, order2));

        assertDoesNotThrow(() -> orderService.softDeleteUserOrder(100L));

        verify(orderRepository, times(1)).findByUserId(100L);
        verify(orderRepository, never()).saveAll(any());
    }

    @Test
    void softDeleteUserOrder_PartiallyDeleted_OnlyUpdatesNonDeleted() {
        order.setDeleted(false);
        Order order2 = Order.builder()
                .orderId(2L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(100.0)
                .orderStatus(OrderEnum.SHIPPED)
                .orderDateTime(LocalDateTime.now())
                .isDeleted(true)
                .build();

        when(orderRepository.findByUserId(100L)).thenReturn(java.util.List.of(order, order2));
        when(orderRepository.saveAll(any())).thenReturn(java.util.List.of(order, order2));

        assertDoesNotThrow(() -> orderService.softDeleteUserOrder(100L));

        verify(orderRepository, times(1)).findByUserId(100L);
        verify(orderRepository, times(1)).saveAll(any());
    }

    @Test
    void softDeleteUserOrder_NullList_ThrowsException() {
        when(orderRepository.findByUserId(999L)).thenReturn(null);

        assertThrows(OrderNotFoundException.class, () -> orderService.softDeleteUserOrder(999L));

        verify(orderRepository, times(1)).findByUserId(999L);
    }

    // ==================== updateOrderStatus Additional Tests ====================

    @Test
    void updateOrderStatus_CancelledViaUpdate_ThrowsException() {
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderInvalidStatusTransitionException.class,
                () -> orderService.updateOrderStatus(1L, updateRequest));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_DeliveredOrder_ThrowsException() {
        order.setOrderStatus(OrderEnum.DELIVERED);
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderInvalidStatusTransitionException.class,
                () -> orderService.updateOrderStatus(1L, updateRequest));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_CancelledOrder_ThrowsException() {
        order.setOrderStatus(OrderEnum.CANCELLED);
        UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
        updateRequest.setOrderStatus(OrderEnum.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(OrderInvalidStatusTransitionException.class,
                () -> orderService.updateOrderStatus(1L, updateRequest));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    // ==================== cancelOrder Additional Tests ====================

    @Test
    void cancelOrder_ShippedOrder_Success() {
        order.setOrderStatus(OrderEnum.SHIPPED);
        Order cancelledOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(80.0)
                .orderStatus(OrderEnum.CANCELLED)
                .orderDateTime(order.getOrderDateTime())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderEnum.CANCELLED, result.getOrderStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // ==================== placeOrder Additional Tests ====================

    @Test
    void placeOrder_MissingPriceForBook_DefaultsToZero() {
        Map<Long, Double> partialPriceMap = new HashMap<>();
        partialPriceMap.put(1L, 10.0);  // Only price for book 1, not book 2

        GetBookPriceResponseDTO priceResponse = new GetBookPriceResponseDTO();
        priceResponse.setBookPrice(partialPriceMap);

        Order savedOrder = Order.builder()
                .orderId(1L)
                .userId(100L)
                .items(items)
                .orderTotalAmount(20.0) // Only book 1's price (10.0 * 2)
                .orderStatus(OrderEnum.PENDING)
                .orderDateTime(LocalDateTime.now())
                .build();

        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class))).thenReturn(priceResponse);
        doNothing().when(inventoryServiceClient).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDTO result = orderService.placeOrder(placeOrderRequest);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, times(1)).reduceStock(any(ReduceInventoryStockRequestDTO.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_UnexpectedException_ThrowsOrderNotPlacedException() {
        when(bookServiceClient.getBookPrices(any(GetBookPriceRequestDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        OrderNotPlacedException exception = assertThrows(OrderNotPlacedException.class,
                () -> orderService.placeOrder(placeOrderRequest));

        assertTrue(exception.getMessage().contains("Internal system error"));

        verify(bookServiceClient, times(1)).getBookPrices(any(GetBookPriceRequestDTO.class));
        verify(inventoryServiceClient, never()).reduceStock(any());
        verify(orderRepository, never()).save(any());
    }
}
