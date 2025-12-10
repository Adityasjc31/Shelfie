package com.db.ms.dto.requestdto;


import lombok.Data;
import java.util.List;


@Data
public class PlaceOrderRequestDTO {
    private long userId;
    private List<Long> bookIds;
    private double orderTotalAmount;
    // orderDateTime and status are set server-side
}
