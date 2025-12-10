package com.db.ms.dto.requestdto;

import com.db.ms.enums.OrderEnum;
import lombok.Data;


@Data
public class UpdateOrderRequestDTO {
    private OrderEnum orderStatus; // only status update

}
