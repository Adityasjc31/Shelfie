<<<<<<< HEAD:src/main/java/com/db/ms/dto/LowStockAlertDTO.java
package com.db.ms.dto;
=======
package com.db.ms.inventory.dto;
>>>>>>> 6979c75f791df3a9533e62ae5df45fc130808a3a:src/main/java/com/db/ms/Inventory/dto/LowStockAlertDTO.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for low stock alert information.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockAlertDTO {

    private Long inventoryId;
    private Long bookId;
    private Integer currentQuantity;
    private Integer lowStockThreshold;
    private Integer quantityNeeded;
    private String alertLevel;
}