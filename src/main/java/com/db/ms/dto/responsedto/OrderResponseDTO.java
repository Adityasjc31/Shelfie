package com.db.ms.dto.responsedto;

import com.db.ms.enums.OrderEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
