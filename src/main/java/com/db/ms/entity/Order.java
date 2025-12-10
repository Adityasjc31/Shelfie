package com.db.ms.entity;

import com.db.ms.enums.OrderEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {

      private  long orderId;
      private long userId;
      private List<Long> bookId;
      private LocalDateTime orderDateTime;
      private double orderTotalAmount;
      private OrderEnum orderStatus;


}
