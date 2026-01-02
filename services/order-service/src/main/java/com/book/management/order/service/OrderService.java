package com.book.management.order.service;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining order management capabilities.
 ** @author Rehan Ashraf
 * @version 2.0
 */
public interface OrderService {

    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request);

    List<OrderResponseDTO> getOrderAll();

    Optional<OrderResponseDTO> getOrderById(long orderId);

    List<OrderResponseDTO> getOrderByStatus(OrderEnum status);

    OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderStatusRequestDTO request);

    void cancelOrder(long orderId);

    void softDeleteOrder(long orderId);
}