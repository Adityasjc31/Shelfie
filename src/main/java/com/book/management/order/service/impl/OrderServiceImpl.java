package com.book.management.order.service.impl;

import com.book.management.book.exception.BookNotFoundException;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.*;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;
import com.book.management.book.service.BookService;
import com.book.management.inventory.service.InventoryService;
import com.book.management.order.util.OrderOpsUtils;
import com.book.management.inventory.exception.InsufficientStockException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for Order Management.
 *
 * @author Rehan Ashraf
 * @version 1.6 (Refactored to use simplified Utility flow)
 * @since 2024-12-15
 * Service implementation for Order operations, handling orchestration of Book and Inventory services.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final InventoryService inventoryService;

    private static final String ONFRE = "Order not found with ID: ";

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BookService bookService, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
        this.inventoryService = inventoryService;
    }

    /**
     * Orchestrates order placement: validation, price check, stock check, stock reduction,
     * compute total & save, then returns the OrderResponseDTO.
     */
    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException {
        log.info("Order placement initiated for userId: {}", (request != null ? request.getUserId() : "null"));

        Order savedOrder = preProcessAndSaveOrder(request);
        return toResponseDTO(savedOrder);
    }

    /**
     * Phase 1: Executes the five util operations in sequence.
     * Logic is simplified by allowing exceptions to propagate naturally.
     */
    private Order preProcessAndSaveOrder(PlaceOrderRequestDTO request) throws OrderNotPlacedException {

        // 1) Initial validation (Directly throws IllegalArgumentException if invalid)
        List<Long> bookIds = OrderOpsUtils.initialValidation(request);

        // Access bookOrder safely after initialValidation has passed
        final Map<Long, Integer> bookOrder = request.getBookOrder();

        // 2) Price check (Propagates BookNotFoundException or OrderNotPlacedException)
        Map<Long, Double> priceMap;
        try {
             priceMap= OrderOpsUtils.priceCheck(bookService, bookIds);
        } catch (BookNotFoundException ex ){
            log.error("Price fetch failed for userId={}: {}",request.getUserId(), ex.getMessage());
             throw ex;
        }
        // 3) Stock availability check (Propagates InsufficientStockException)
        try {
            OrderOpsUtils.stockCheck(inventoryService, bookOrder);
        }catch (InsufficientStockException ex){
            log.error("Stock check failed for userId={}: {}", request.getUserId(), ex.getMessage());
            throw ex;
        }

        // 4) Stock reduction
        try {
            OrderOpsUtils.stockReduction(inventoryService, bookOrder);
        } catch (InsufficientStockException ex) {
            log.error("Stock reduction failed for userId={}: {}", request.getUserId(), ex.getMessage());
            throw ex;
        }

        // 5) Compute total & save
        Order savedOrder;
        try{
        savedOrder = OrderOpsUtils.computeTotalAndSaveOrder(
                orderRepository,
                request.getUserId(),
                bookOrder,
                priceMap,
                bookIds,
                OrderEnum.PENDING
        );}
        catch (OrderNotPlacedException ex){
            log.error("Order placing failed for bookOrder: {}", request.getBookOrder(), ex.getMessage());
            throw ex;
        }

        log.info("Order successfully persisted: orderId={}", savedOrder.getOrderId());
        return savedOrder;
    }

    @Override
    public List<OrderResponseDTO> getOrderAll() {
        List<OrderResponseDTO> result = orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Fetched all orders: count={}", result.size());
        return result;
    }

    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        log.info("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId).map(this::toResponseDTO);
    }

    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        List<OrderResponseDTO> result = orderRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
        log.info("Fetched orders by status: status={}, count={}", status, result.size());
        return result;
    }

    @Override
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request)
            throws OrderNotFoundException {
        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ONFRE + orderId));

        existing.setOrderStatus(request.getOrderStatus());
        Order updated = orderRepository.update(existing);
        log.info("Order status updated: orderId={}, status={}", orderId, updated.getOrderStatus());
        return toResponseDTO(updated);
    }

    @Override
    public void cancelOrder(long orderId)
            throws OrderNotFoundException, OrderCancellationNotAllowedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ONFRE + orderId));

        if (order.getOrderStatus() == OrderEnum.SHIPPED || order.getOrderStatus() == OrderEnum.DELIVERED) {
            log.warn("Cancellation rejected: Order {} is already {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException("Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        orderRepository.deleteById(orderId);
        log.info("Order cancelled: orderId={}", orderId);
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