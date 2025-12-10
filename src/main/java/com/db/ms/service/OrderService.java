package com.db.ms.service;


import com.db.ms.dto.responsedto.OrderResponseDTO;
import com.db.ms.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.dto.requestdto.UpdateOrderRequestDTO;
import com.db.ms.enums.OrderEnum;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // Create
    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request);

    // Read
    List<OrderResponseDTO> getOrderAll();
    Optional<OrderResponseDTO> getOrderById(long orderId);
    List<OrderResponseDTO> getOrderByStatus(OrderEnum status);

    // Update
    OrderResponseDTO changeOrderStatus(long orderId, UpdateOrderRequestDTO request);

    // Delete
    void cancelOrder(long orderId);
}

