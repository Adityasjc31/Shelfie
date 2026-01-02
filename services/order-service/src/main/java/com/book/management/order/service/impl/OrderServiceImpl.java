package com.book.management.order.service.impl;

import com.book.management.order.client.BookClient;
import com.book.management.order.client.InventoryClient;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.*;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for Order operations.
 * Orchestrates cross-service calls and manages MySQL persistence.
 *
 * @author Rehan Ashraf
 * @version 2.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final InventoryClient inventoryClient;

    private static final String NOT_FOUND_MSG = "Order not found with ID: ";

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        log.info("Initiating order placement for userId: {}", request.getUserId());

        try {
            // 1. Fetch Book Prices via Feign
            Map<Long, Double> priceMap = bookClient.getBookPrices(new ArrayList<>(request.getBookOrder().keySet()));

            // 2. Calculate Total
            double total = request.getBookOrder().entrySet().stream()
                    .mapToDouble(entry -> priceMap.get(entry.getKey()) * entry.getValue())
                    .sum();

            // 3. Inventory Reduction (Implicit Stock Check) via Feign
            inventoryClient.reduceStock(request.getBookOrder());

            // 4. Persistence
            Order order = Order.builder()
                    .userId(request.getUserId())
                    .items(request.getBookOrder())
                    .orderTotalAmount(total)
                    .orderDateTime(LocalDateTime.now())
                    .orderStatus(OrderEnum.PENDING)
                    .isDeleted(false)
                    .build();

            Order savedOrder = orderRepository.save(order);
            log.info("Order successfully placed. ID: {}", savedOrder.getOrderId());
            return toResponseDTO(savedOrder);

        } catch (FeignException.NotFound ex) {
            log.error("Downstream fail: Book/User not found. {}", ex.getMessage());
            throw new OrderNotPlacedException("One or more items in your order do not exist.");
        } catch (FeignException.Conflict ex) {
            log.error("Downstream fail: Stock conflict. {}", ex.getMessage());
            throw new OrderNotPlacedException("Insufficient stock to complete this order.");
        } catch (Exception ex) {
            log.error("Unexpected error during order placement: {}", ex.getMessage());
            throw new OrderNotPlacedException("Order could not be processed due to a system error.");
        }
    }

    @Override
    public List<OrderResponseDTO> getOrderAll() {
        log.info("Fetching all active orders from MySQL.");
        return orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        log.info("Querying order ID: {}", orderId);
        return orderRepository.findById(orderId).map(this::toResponseDTO);
    }

    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        log.info("Filtering orders by status: {}", status);
        return orderRepository.findByOrderStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request) {
        log.info("Updating status for order {}: to {}", orderId, request.getOrderStatus());

        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        existing.setOrderStatus(request.getOrderStatus());
        // Save handles update in JPA
        return toResponseDTO(orderRepository.save(existing));
    }

    @Override
    @Transactional
    public void cancelOrder(long orderId) {
        log.info("Request to cancel order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        if (order.getOrderStatus() == OrderEnum.SHIPPED || order.getOrderStatus() == OrderEnum.DELIVERED) {
            log.warn("Cancel denied: Order {} is already {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException("Cannot cancel an order that is already " + order.getOrderStatus());
        }

        // Standard JPA delete will trigger our @SQLDelete (Soft Delete)
        orderRepository.delete(order);
        log.info("Order {} marked as cancelled/deleted.", orderId);
    }

    /**
     * Logic to synchronize deletion from other services.
     * Updates isDeleted to true for the specific order.
     */
    @Override
    @Transactional
    public void softDeleteOrder(long orderId) {
        log.info("Synchronizing deletion for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        order.setDeleted(true);
        orderRepository.save(order);
        log.info("Order {} successfully synchronized as deleted.", orderId);
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .bookIds(new ArrayList<>(order.getItems().keySet()))
                .orderDateTime(order.getOrderDateTime())
                .orderTotalAmount(order.getOrderTotalAmount())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}