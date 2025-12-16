
package com.book.management.order.service.impl;

import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.service.BookService;
import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.inventory.service.InventoryService;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl.
 * Uses JUnit 5 and Mockito for testing.
 * Validates placing orders with orchestration (Book/Inventory calls), reads, status updates, cancellation,
 * and a comprehensive set of invalid input scenarios.
 *
 * Notes:
 * - External service calls and Repository interactions are mocked.
 * - Tests focus on service behavior, validation, and exception propagation/handling.
 *
 * @author Rehan Ashraf
 * @version 1.8 (Refactored placeOrder tests; fixed Mockito doNothing misuse)
 * @since 2024-12-15
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BookService bookService;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private PlaceOrderRequestDTO validRequest;
    private Order persistedOrder;
    private BookPriceResponseDTO validPriceResponse;

    // Availability Map (phase-1 check)
    private Map<Long, Boolean> availabilityAllTrue;

    // Expected total: 399.0 * 2 + 249.5 * 1 = 1047.5
    private static final double EXPECTED_TOTAL = 1047.5;

    @BeforeEach
    void setUp() {
        // Valid request: userId and book quantities
        Map<Long, Integer> bookOrder = new LinkedHashMap<>();
        bookOrder.put(101L, 2);
        bookOrder.put(102L, 1);

        validRequest = new PlaceOrderRequestDTO();
        validRequest.setUserId(101L);
        validRequest.setBookOrder(bookOrder);

        // Prices for all requested books (BookPriceResponseDTO)
        Map<Long, Double> prices = new HashMap<>();
        prices.put(101L, 399.0);
        prices.put(102L, 249.5);
        validPriceResponse = new BookPriceResponseDTO(prices);

        // Stock available for all requested books (phase-1 returns a Map)
        availabilityAllTrue = new HashMap<>();
        availabilityAllTrue.put(101L, true);
        availabilityAllTrue.put(102L, true);

        // Persisted Order returned by repository.save(...) - Using AllArgsConstructor
        persistedOrder = new Order(
                1L, // orderId (Simulated generated ID)
                validRequest.getUserId(),
                new ArrayList<>(bookOrder.keySet()),
                LocalDateTime.now(),
                EXPECTED_TOTAL,
                OrderEnum.PENDING
        );
    }

    // ------------------------------
    // placeOrder: success & failure cases for 2-phase implementation
    // ------------------------------

    /**
     * placeOrder: happy path with valid request and successful orchestration.
     * Verifies:
     * - Total = sum(quantity * unitPrice)
     * - Orchestration calls (BookService, InventoryService check/reduce)
     * - Order saved and status finalized via update.
     */
    @Test
    void placeOrder_Success_ComputesTotalAndPersists() {
        // Phase-1: price + availability (both non-void)
        when(bookService.getBookPricesMap(anyList())).thenReturn(validPriceResponse);
        when(inventoryService.checkBulkAvailability(anyMap())).thenReturn(availabilityAllTrue);

        // Phase-1: save (non-void)
        when(orderRepository.save(any(Order.class))).thenReturn(persistedOrder);

        // Phase-2: reduce (void) + update (non-void)
        doNothing().when(inventoryService).reduceBulkInventory(anyMap());
        when(orderRepository.update(any(Order.class))).thenReturn(persistedOrder);

        OrderResponseDTO result = orderService.placeOrder(validRequest);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(101L, result.getUserId());
        assertEquals(List.of(101L, 102L), result.getBookIds());
        assertEquals(EXPECTED_TOTAL, result.getOrderTotalAmount(), 0.0001);
        assertEquals(OrderEnum.PENDING, result.getOrderStatus());

        verify(bookService, times(1)).getBookPricesMap(anyList());
        verify(inventoryService, times(1)).checkBulkAvailability(anyMap());
        verify(inventoryService, times(1)).reduceBulkInventory(anyMap());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderRepository, times(1)).update(any(Order.class));
    }

    /**
     * placeOrder: request is null → IllegalArgumentException (phase-1 validation).
     */
    @Test
    void placeOrder_NullRequest_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(null));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: userId <= 0 → IllegalArgumentException (phase-1 validation).
     */
    @Test
    void placeOrder_InvalidUserId_ThrowsIllegalArgumentException() {
        validRequest.setUserId(0L);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: bookOrder is null → IllegalArgumentException (phase-1 validation).
     */
    @Test
    void placeOrder_NullBookOrder_ThrowsIllegalArgumentException() {
        validRequest.setBookOrder(null);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: bookOrder empty → IllegalArgumentException (phase-1 validation).
     */
    @Test
    void placeOrder_EmptyBookOrder_ThrowsIllegalArgumentException() {
        validRequest.setBookOrder(Collections.emptyMap());

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: invalid bookId (null or <=0) → IllegalArgumentException (phase-1 validation).
     */
    @Test
    void placeOrder_InvalidBookId_ThrowsIllegalArgumentException() {
        Map<Long, Integer> badOrder = new HashMap<>();
        badOrder.put(null, 1);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));
        verify(orderRepository, never()).save(any(Order.class));

        // Test bookId <= 0 as well
        badOrder.clear();
        badOrder.put(0L, 1);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: invalid quantity (null or <=0) → IllegalArgumentException (phase-1 validation).
     */
    @Test
    void placeOrder_InvalidQuantity_ThrowsIllegalArgumentException() {
        Map<Long, Integer> badOrder = new HashMap<>();
        badOrder.put(101L, null);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));

        badOrder.put(101L, 0);
        validRequest.setBookOrder(badOrder);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(validRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: BookService throws BookNotFoundException during fetch → Propagates BookNotFoundException (phase-1).
     */
    @Test
    void placeOrder_BookServiceThrowsBookNotFound_PropagatesException() {
        when(bookService.getBookPricesMap(anyList())).thenThrow(new BookNotFoundException("Book 999 not found"));

        assertThrows(BookNotFoundException.class,
                () -> orderService.placeOrder(validRequest));

        verify(inventoryService, never()).checkBulkAvailability(anyMap());
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: NULL price for a requested book (Service Validation) → BookNotFoundException (phase-1).
     * NOTE: New implementation checks for null prices for present keys.
     */
    @Test
    void placeOrder_NullPriceForRequestedBook_ThrowsBookNotFoundException() {
        // Mock price response with a null price for 102L
        Map<Long, Double> pricesWithNull = new HashMap<>();
        pricesWithNull.put(101L, 399.0);
        pricesWithNull.put(102L, null);
        validPriceResponse.setPrices(pricesWithNull);

        when(bookService.getBookPricesMap(anyList())).thenReturn(validPriceResponse);

        assertThrows(BookNotFoundException.class,
                () -> orderService.placeOrder(validRequest));

        verify(inventoryService, never()).checkBulkAvailability(anyMap());
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * placeOrder: stock not available in phase-1 availability check → InsufficientStockException.
     */
    @Test
    void placeOrder_InsufficientStock_ThrowsInsufficientStockException() {
        when(bookService.getBookPricesMap(anyList())).thenReturn(validPriceResponse);

        // Availability with one book unavailable
        Map<Long, Boolean> availability = new HashMap<>();
        availability.put(101L, false);
        availability.put(102L, true);

        when(inventoryService.checkBulkAvailability(anyMap())).thenReturn(availability);

        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder(validRequest));

        verify(orderRepository, never()).save(any(Order.class));
        verify(inventoryService, times(1)).checkBulkAvailability(anyMap());
        verify(inventoryService, never()).reduceBulkInventory(anyMap());
    }

    /**
     * placeOrder: InventoryService.reduceBulkInventory fails (phase-2) → propagates RuntimeException.
     */
    @Test
    void placeOrder_StockDeductionFails_PropagatesRuntimeException() {
        when(bookService.getBookPricesMap(anyList())).thenReturn(validPriceResponse);
        when(inventoryService.checkBulkAvailability(anyMap())).thenReturn(availabilityAllTrue);
        when(orderRepository.save(any(Order.class))).thenReturn(persistedOrder);

        // reduceBulkInventory is void: use doThrow
        doThrow(new RuntimeException("Inventory DB Error"))
                .when(inventoryService).reduceBulkInventory(anyMap());

        assertThrows(RuntimeException.class,
                () -> orderService.placeOrder(validRequest));

        // Save happened; update should NOT happen after reduce failure
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderRepository, never()).update(any(Order.class));
        verify(inventoryService, times(1)).reduceBulkInventory(anyMap());
    }

    /**
     * placeOrder: repository save throws RuntimeException → wrapped as OrderNotPlacedException (phase-1).
     */
    @Test
    void placeOrder_RepositoryThrows_ThrowsOrderNotPlacedException() {
        when(bookService.getBookPricesMap(anyList())).thenReturn(validPriceResponse);
        when(inventoryService.checkBulkAvailability(anyMap())).thenReturn(availabilityAllTrue);

        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("DB down"));

        assertThrows(OrderNotPlacedException.class,
                () -> orderService.placeOrder(validRequest));

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(inventoryService, never()).reduceBulkInventory(anyMap());
        verify(orderRepository, never()).update(any(Order.class));
    }

    // ------------------------------
    // Read operations (left unchanged)
    // ------------------------------

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

    @Test
    void getOrderById_Found_ReturnsDTO() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(persistedOrder));

        Optional<OrderResponseDTO> opt = orderService.getOrderById(1L);

        assertTrue(opt.isPresent());
        assertEquals(1L, opt.get().getOrderId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NotFound_ReturnsEmpty() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<OrderResponseDTO> opt = orderService.getOrderById(999L);

        assertTrue(opt.isEmpty());
        verify(orderRepository, times(1)).findById(999L);
    }

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

    @Test
    void changeOrderStatus_OrderNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(12345L)).thenReturn(Optional.empty());
        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO();
        request.setOrderId(12345L);
        request.setOrderStatus(OrderEnum.SHIPPED);

        assertThrows(OrderNotFoundException.class,
                () -> orderService.changeOrderStatus(12345L, request));
        verify(orderRepository, times(1)).findById(12345L);
        verify(orderRepository, never()).update(any(Order.class));
    }

    @Test
    void cancelOrder_Success_DeletesById() {
        Order existing = new Order(77L, 777L, List.of(1L), LocalDateTime.now(), 100.0, OrderEnum.PENDING);
        when(orderRepository.findById(77L)).thenReturn(Optional.of(existing));
        doNothing().when(orderRepository).deleteById(77L);

        orderService.cancelOrder(77L);

        verify(orderRepository, times(1)).findById(77L);
        verify(orderRepository, times(1)).deleteById(77L);
    }

    @Test
    void cancelOrder_OrderNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.cancelOrder(88L));
        verify(orderRepository, times(1)).findById(88L);
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void cancelOrder_ShippedStatus_ThrowsOrderCancellationNotAllowedException() {
        Order existing = new Order(77L, 777L, List.of(1L), LocalDateTime.now(), 100.0, OrderEnum.SHIPPED);
        when(orderRepository.findById(77L)).thenReturn(Optional.of(existing));

        assertThrows(OrderCancellationNotAllowedException.class,
                () -> orderService.cancelOrder(77L));

        verify(orderRepository, times(1)).findById(77L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
}