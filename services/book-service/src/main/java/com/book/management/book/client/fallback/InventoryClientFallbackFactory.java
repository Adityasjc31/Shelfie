package com.book.management.book.client.fallback;

import com.book.management.book.client.InventoryClient;
import com.book.management.book.dto.requestdto.InventoryCreateDTO;
import com.book.management.book.dto.responsedto.InventoryResponseDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {
            @Override
            public InventoryResponseDTO createInventory(InventoryCreateDTO request) {
                logError("create", cause);

                return InventoryResponseDTO.builder()
                        .bookId(request.getBookId())
                        .quantity(0)
                        .lowStockThreshold(request.getLowStockThreshold())
                        .isLowStock(true)
                        .isOutOfStock(true)
                        .createdAt(LocalDateTime.now())
                        .build();
            }

            @Override
            public InventoryResponseDTO getInventoryByBookId(Long bookId) {
                logError("get", cause);

                return InventoryResponseDTO.builder()
                        .bookId(bookId)
                        .quantity(0)
                        .lowStockThreshold(5)
                        .isLowStock(true)
                        .isOutOfStock(true)
                        .updatedAt(LocalDateTime.now())
                        .build();
            }

            @Override
            public void deleteInventoryByBookId(Long bookId) {
                logError("delete", cause);
                // No return needed for void method
            }
        };
    }

    // Helper to keep logs clean and identify Security vs Connection issues
    private void logError(String operation, Throwable cause) {
        if (cause.getMessage() != null && cause.getMessage().contains("401")) {
            log.error("Fallback triggered for {}: Authentication failure (401 Unauthorized).", operation);
        } else if (cause.getMessage() != null && cause.getMessage().contains("403")) {
            log.error("Fallback triggered for {}: Permission denied (403 Forbidden).", operation);
        } else {
            log.error("Fallback triggered for {}: Inventory Service is unreachable. Error: {}",
                    operation, cause.getMessage());
        }
    }
}