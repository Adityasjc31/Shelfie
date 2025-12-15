
package com.db.ms.order.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderPriceStockResponseDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.impl.OrderServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl.
 * Uses JUnit 5 and Mockito for testing.
 * Validates placing orders with price/stock aggregation, reads, status updates, cancellation,
 * and a comprehensive set of invalid input scenarios.
 *
 * Notes:
 * - Repository interactions are mocked; tests focus on service behavior and validation.
 * - Ensures iteration logic for total amount calculation and defensive checks.
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-14
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private PlaceOrderRequestDTO validRequest;
    private OrderPriceStockResponseDTO validPriceStock;

    private Order persistedOrder;

    @BeforeEach
    void setUp() {
        // Valid request: userId and book quantities
        Map<Long, Integer> bookOrder = new LinkedHashMap<>();
        bookOrder.put(101L, 2);
        bookOrder.put(102L, 1);

        validRequest = new PlaceOrderRequestDTO();
        validRequest.setUserId(101L);
        validRequest.setBookOrder(bookOrder);

        // Prices and stock available for all requested books
        Map<Long, Double> prices = new HashMap<>();
        prices.put(101L, 399.0);
        prices.put(102L, 249.5);

        Map<Long, Boolean> stock = new HashMap<>();
        stock.put(101L, true);
        stock.put(102L, true);

        validPriceStock = new OrderPriceStockResponseDTO(prices, stock);

        // Persisted Order returned by repository.save(...)
        persistedOrder = new Order();
        persistedOrder.setOrderId(1L);
        persistedOrder.setUserId(validRequest.getUserId());
        persistedOrder.setBookId(new ArrayList<>(bookOrder.keySet()));
        // totalAmount = 399.0 * 2 + 249.5 * 1 = 1047.5
        persistedOrder.setOrderTotalAmount(1047.5);
        persistedOrder.setOrderDateTime(LocalDateTime.now());
        persistedOrder.setOrderStatus(OrderEnum.PENDING);
    }

    /**
     * placeOrder: happy path with valid request and price/stock maps.
     * Verifies:
     * - Total = sum(quantity * unitPrice)
     * - Book IDs list derives from request keys
     * - save called once
     * - DTO maps persisted fields
     */
    @Test
    void placeOrder_Success_ComputesTotalAndPersists() {
        when(orderRepository.save(any(Order.class))).thenReturn(persistedOrder);

        OrderResponseDTO result = orderService.placeOrder(validRequest, validPriceStock);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(101L, result.getUserId());
        assertEquals(List.of(101L, 102L), result.getBookIds());
        assertEquals(1047.5, result.getOrderTotalAmount(), 0.0001);
        assertEquals(OrderEnum.PENDING, result.getOrderStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // ------------------------------
    // Invalid input scenarios (placeOrder)
    // ------------------------------

    /**
     * placeOrder: request is null → IllegalArgumentException.
     */
    @Test
    void placeOrder_NullRequest_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(null, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: userId <= 0 → IllegalArgumentException.
     */
    @Test
    void placeOrder_InvalidUserId_ThrowsIllegalArgumentException() {
        validRequest.setUserId(0L);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: bookOrder is null → IllegalArgumentException.
     */
    @Test
    void placeOrder_NullBookOrder_ThrowsIllegalArgumentException() {
        validRequest.setBookOrder(null);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: bookOrder empty → IllegalArgumentException.
     */
    @Test
    void placeOrder_EmptyBookOrder_ThrowsIllegalArgumentException() {
        validRequest.setBookOrder(Collections.emptyMap());

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: invalid bookId (null or <=0) → IllegalArgumentException.
     */
    @Test
    void placeOrder_InvalidBookId_ThrowsIllegalArgumentException() {
        Map<Long, Integer> badOrder = new HashMap<>();
        badOrder.put(null, 1);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));

        // Test bookId <= 0 as well
        badOrder.clear();
        badOrder.put(0L, 1);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: invalid quantity (null or <=0) → IllegalArgumentException.
     */
    @Test
    void placeOrder_InvalidQuantity_ThrowsIllegalArgumentException() {
        Map<Long, Integer> badOrder = new HashMap<>();
        badOrder.put(101L, null);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));

        badOrder.put(101L, 0);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: priceStock is null → IllegalArgumentException.
     */
    @Test
    void placeOrder_NullPriceStock_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, null));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: priceMap is null or empty → IllegalArgumentException.
     */
    @Test
    void placeOrder_EmptyOrNullPriceMap_ThrowsIllegalArgumentException() {
        validPriceStock.setBookPrice(null);
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));

        validPriceStock.setBookPrice(Collections.emptyMap());
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: stockMap is null or empty → IllegalArgumentException.
     */
    @Test
    void placeOrder_EmptyOrNullStockMap_ThrowsIllegalArgumentException() {
        validPriceStock.setBookStock(null);
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));

        validPriceStock.setBookStock(Collections.emptyMap());
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: missing price for a requested book → IllegalArgumentException.
     */
    @Test
    void placeOrder_MissingPriceForRequestedBook_ThrowsIllegalArgumentException() {
        validPriceStock.getBookPrice().remove(102L);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: stock not available for a requested book → IllegalArgumentException.
     */
    @Test
    void placeOrder_InsufficientStock_ThrowsIllegalArgumentException() {
        validPriceStock.getBookStock().put(101L, false);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: repository save throws RuntimeException → propagate.
     */
    @Test
    void placeOrder_RepositoryThrows_PropagatesRuntimeException() {
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class,
                () -> orderService.placeOrder(validRequest, validPriceStock));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // ------------------------------
    // Read operations
    // ------------------------------

    /**
     * getOrderAll: returns mapped list from repository.
     */
    @Test
    void getOrderAll_ReturnsMappedList() {
        Order o1 = new Order(1L, 201L, List.of(11L, 12L), LocalDateTime.now(), 500.0, OrderEnum.PENDING);
        Order o2 = new Order(2L, 202L, List.of(21L), LocalDateTime.now(), 250.0, OrderEnum.SHIPPED);
        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));

        List<OrderResponseDTO> result = orderService.getOrderAll();

        assertEquals(2, result.size());
        assertEquals(201L, result.get(0).getUserId());
        assertEquals(202L, result.get(1).getUserId());
        verify(orderRepository, times(1)).findAll();
    }

    /**
     * getOrderById: present order returns mapped DTO.
     */
    @Test
    void getOrderById_Found_ReturnsDTO() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(persistedOrder));

        Optional<OrderResponseDTO> opt = orderService.getOrderById(1L);

        assertTrue(opt.isPresent());
        assertEquals(1L, opt.get().getOrderId());
        verify(orderRepository, times(1)).findById(1L);
    }

    /**
     * getOrderById: absent order returns Optional.empty.
     */
    @Test
    void getOrderById_NotFound_ReturnsEmpty() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<OrderResponseDTO> opt = orderService.getOrderById(999L);

        assertTrue(opt.isEmpty());
        verify(orderRepository, times(1)).findById(999L);
    }

    /**
     * getOrderByStatus: filters by enum status and maps to DTO list.
     */
    @Test
    void getOrderByStatus_ReturnsFilteredList() {
        Order o1 = new Order(10L, 300L, List.of(100L), LocalDateTime.now(), 300.0, OrderEnum.PENDING);
        Order o2 = new Order(11L, 301L, List.of(101L), LocalDateTime.now(), 350.0, OrderEnum.PENDING);
        when(orderRepository.findByStatus(OrderEnum.PENDING)).thenReturn(List.of(o1, o2));

        List<OrderResponseDTO> result = orderService.getOrderByStatus(OrderEnum.PENDING);

        assertEquals(2, result.size());
        assertEquals(OrderEnum.PENDING, result.get(0).getOrderStatus());
        verify(orderRepository, times(1)).findByStatus(OrderEnum.PENDING);
    }

    // ------------------------------
    // Update and Delete
    // ------------------------------

    /**
     * changeOrderStatus: happy path updates status and returns DTO.
     */
    @Test
    void changeOrderStatus_Success_UpdatesStatus() {
        Order existing = new Order(55L, 777L, List.of(1L, 2L), LocalDateTime.now(), 120.0, OrderEnum.PENDING);
        Order updated = new Order(55L, 777L, List.of(1L, 2L), existing.getOrderDateTime(), 120.0, OrderEnum.DELIVERED);

        when(orderRepository.findById(55L)).thenReturn(Optional.of(existing));
        when(orderRepository.update(any(Order.class))).thenReturn(updated);

        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO();
        request.setOrderId(55L);
        request.setOrderStatus(OrderEnum.DELIVERED);

        OrderResponseDTO result = orderService.changeOrderStatus(55L, request);

        assertEquals(OrderEnum.DELIVERED, result.getOrderStatus());
        verify(orderRepository, times(1)).findById(55L);
        verify(orderRepository, times(1)).update(any(Order.class));
    }

    /**
     * changeOrderStatus: not found → NoSuchElementException.
     */
    @Test
    void changeOrderStatus_OrderNotFound_ThrowsNoSuchElementException() {
        when(orderRepository.findById(12345L)).thenReturn(Optional.empty());
        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO();
        request.setOrderId(12345L);
        request.setOrderStatus(OrderEnum.SHIPPED);

        assertThrows(NoSuchElementException.class,
                () -> orderService.changeOrderStatus(12345L, request));
        verify(orderRepository, times(1)).findById(12345L);
        verify(orderRepository, never()).update(any(Order.class));
    }

    /**
     * cancelOrder: delegates to repository.deleteById successfully.
     */
    @Test
    void cancelOrder_DeletesById() {
        doNothing().when(orderRepository).deleteById(77L);

        orderService.cancelOrder(77L);

        verify(orderRepository, times(1)).deleteById(77L);
    }

    /**
     * cancelOrder: repository throws NoSuchElementException → propagate.
     */
    @Test
    void cancelOrder_OrderNotFound_PropagatesNoSuchElementException() {
        doThrow(new NoSuchElementException("Missing")).when(orderRepository).deleteById(88L);

        assertThrows(NoSuchElementException.class,
                () -> orderService.cancelOrder(88L));
        verify(orderRepository, times(1)).deleteById(88L);
    }
}
