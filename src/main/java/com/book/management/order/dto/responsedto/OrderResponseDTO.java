package com.book.management.order.dto.responsedto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.book.management.order.enums.OrderEnum;

@Data
@Builder
public class OrderResponseDTO {
    private long orderId;
    private long userId;
    private List<Long> bookIds;
    private LocalDateTime orderDateTime;
    private double orderTotalAmount;
    private OrderEnum orderStatus;
}
