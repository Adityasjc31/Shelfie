<<<<<<< HEAD:src/main/java/com/db/ms/dto/InventoryResponseDTO.java
package com.db.ms.dto;
=======
package com.db.ms.inventory.dto;
>>>>>>> 6979c75f791df3a9533e62ae5df45fc130808a3a:src/main/java/com/db/ms/Inventory/dto/InventoryResponseDTO.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Inventory responses.
 * Used to send inventory data to clients.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponseDTO {

    private Long inventoryId;
    private Long bookId;
    private Integer quantity;
    private Integer lowStockThreshold;
    private boolean isLowStock;
    private boolean isOutOfStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}