package com.db.ms.dto;
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