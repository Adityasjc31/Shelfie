<<<<<<< HEAD:src/main/java/com/db/ms/dto/InventoryAdjustmentDTO.java
package com.db.ms.dto;
=======
package com.db.ms.inventory.dto;
>>>>>>> 6979c75f791df3a9533e62ae5df45fc130808a3a:src/main/java/com/db/ms/Inventory/dto/InventoryAdjustmentDTO.java

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for adjusting inventory quantity.
 * Supports both increment and decrement operations.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAdjustmentDTO {

    @NotNull(message = "Adjustment quantity is required")
    private Integer adjustmentQuantity;

    private String reason;
}