package com.book.management.order.service.impl;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import com.book.management.order.dto.responsedto.ReduceInventoryStockResponseDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.*;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;
import com.book.management.order.service.OrderService;
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
 * Implementation of OrderService.
 * Manages the business logic for order processing and persistence.
 * Orchestrates cross-service calls via Feign and manages MySQL persistence.
 *
 * @author Rehan Ashraf
 * @version 2.1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookServiceClient bookServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    private static final String NOT_FOUND_MSG = "Order not found with ID: ";

    /**
     * Orchestrates order placement.
     * Exception handling is driven by the FeignErrorDecoder for inter-service
     * errors.
     * * @param request The order placement request details.
     * 
     * @return OrderResponseDTO for the created order.
     * @throws OrderNotPlacedException only when error occurs in downstream
     *                                 services.
     */
    @Override
    @Transactional
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        try {
            // Collect all bookIds from incoming order
            List<Long> bookIdList = new ArrayList<>(request.getBookOrder().keySet());

            // fetch book prices via Book-service
            GetBookPriceRequestDTO priceReq = new GetBookPriceRequestDTO(bookIdList);
            GetBookPriceResponseDTO priceDTO = bookServiceClient.getBookPrices(priceReq);
            Map<Long, Double> priceMap = priceDTO.getBookPrice();

            // compute order totalAmount
            double totalAmount = 0.0;
            for (Map.Entry<Long, Integer> entry : request.getBookOrder().entrySet()) {
                Long bookId = entry.getKey();
                Integer quantity = entry.getValue();

                if (priceMap.containsKey(bookId)) {
                    double price = priceMap.get(bookId) * quantity;
                    totalAmount += price;
                } else {
                    log.warn("Price not found for bookId={}, defaulting contribution to 0.0", bookId);
                }
            }

            // Reduce stock via Inventory-service
            // Inventory-service handles all validation and throws exceptions if
            // insufficient stock
            ReduceInventoryStockRequestDTO reduceReq = new ReduceInventoryStockRequestDTO(request.getBookOrder());

            ReduceInventoryStockResponseDTO stockDTO = inventoryServiceClient.reduceStock(reduceReq);
            Map<Long, Boolean> stockMap = stockDTO.getBookStock();

            // Optional: validate stockMap (ensure all requested items are true)
            boolean allAvailable = request.getBookOrder().keySet().stream()
                    .allMatch(id -> Boolean.TRUE.equals(stockMap.get(id)));
            if (!allAvailable) {
                log.error("Insufficient stock for one or more items: {}", stockMap);
                throw new OrderNotPlacedException("Inventory check failed: insufficient stock.");
            }

            // map and save order
            Order order = Order.builder()
                    .userId(request.getUserId())
                    .items(request.getBookOrder())
                    .orderTotalAmount(totalAmount)
                    .orderDateTime(LocalDateTime.now())
                    .orderStatus(OrderEnum.PENDING)
                    .isDeleted(false)
                    .build();

            Order savedOrder = orderRepository.save(order);
            log.info("Order placed successfully. ID: {}, UserId: {}, Total: {}",
                    savedOrder.getOrderId(), savedOrder.getUserId(), savedOrder.getOrderTotalAmount());

            return toResponseDTO(savedOrder);

        } catch (OrderNotPlacedException ex) {
            // Rethrowing mapped exception caught by CustomFeignErrorDecoder
            throw ex;
        } catch (Exception ex) {
            log.error("Internal service error during order placement: {}", ex.getMessage(), ex);
            throw new OrderNotPlacedException("Internal system error: Unable to process order.");
        }
    }

    /**
     * Business logic for cancelling an order.
     * Rule: Can only cancel if PENDING or SHIPPED.
     * Cannot cancel if DELIVERED or already CANCELLED.
     * * @param orderId ID of the order to cancel.
     * 
     * @throws OrderNotFoundException               if order does not exist.
     * @throws OrderCancellationNotAllowedException if status is final.
     */
    @Override
    @Transactional
    public void cancelOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        // Logic check: Deny if Delivered or already Cancelled
        if (order.getOrderStatus() == OrderEnum.DELIVERED || order.getOrderStatus() == OrderEnum.CANCELLED) {
            log.warn("Cancellation rejected. Order {} status is {}", orderId, order.getOrderStatus());
            throw new OrderCancellationNotAllowedException(
                    "Cannot cancel an order that is already " + order.getOrderStatus());
        }

        // Logic check: Allow only if Pending or Shipped
        order.setOrderStatus(OrderEnum.CANCELLED);
        orderRepository.save(order);

        // Also perform soft delete (sets isDeleted=true via @SQLDelete)
        orderRepository.delete(order);
        log.info("Order {} cancelled and soft-deleted", orderId);
    }

    /**
     * Administrative soft delete. Marks the isDeleted flag as true in MySQL.
     * * @param orderId ID of the order to delete.
     * 
     * @throws OrderNotFoundException if order does not exist.
     */
    @Override
    @Transactional
    public void softDeleteOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        // The @SQLDelete annotation in the Order entity handles the UPDATE logic
        orderRepository.delete(order);
    }

    /**
     * Retrieves all active orders from the database.
     * * @return List of OrderResponseDTO.
     * 
     * @throws OrderNotFoundException if no orders exist in the system.
     */
    @Override
    public List<OrderResponseDTO> getOrderAll() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found in the system.");
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }
        return responseList;
    }

    /**
     * Retrieves a specific order by its ID.
     * * @param orderId The ID of the order to find.
     * 
     * @return Optional containing OrderResponseDTO.
     * @throws OrderNotFoundException if the ID does not exist.
     */
    @Override
    public Optional<OrderResponseDTO> getOrderById(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        return Optional.of(toResponseDTO(order));
    }

    /**
     * Retrieves orders filtered by their current status.
     * * @param status The OrderEnum status to filter by.
     * 
     * @return List of OrderResponseDTO.
     * @throws OrderNotFoundException if no orders match the given status.
     */
    @Override
    public List<OrderResponseDTO> getOrderByStatus(OrderEnum status) {
        List<Order> orders = orderRepository.findByOrderStatus(status);

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found with status: " + status);
        }

        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(toResponseDTO(order));
        }
        return responseList;
    }

    /**
     * Updates an order's status after validating the transition logic.
     * * @param orderId ID of the order to update.
     * 
     * @param request DTO containing the target status.
     * @return Updated OrderResponseDTO.
     * @throws OrderNotFoundException                if the order doesn't exist.
     * @throws OrderInvalidStatusTransitionException if the status change is
     *                                               logically invalid.
     */
    @Override
    @Transactional
    public OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(NOT_FOUND_MSG + orderId));

        // Business Logic: Prevent updates if order is already CANCELLED or DELIVERED
        if (order.getOrderStatus() == OrderEnum.CANCELLED || order.getOrderStatus() == OrderEnum.DELIVERED) {
            throw new OrderInvalidStatusTransitionException(
                    "Cannot change status. Order is already in a final state: " + order.getOrderStatus());
        }

        order.setOrderStatus(request.getOrderStatus());
        return toResponseDTO(orderRepository.save(order));
    }

    /**
     * Helper method to map Order Entity to OrderResponseDTO.
     * * @param order The source entity.
     * 
     * @return Mapped Response DTO.
     */
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