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
                log.error("Inventory Service is down or forbidden. Fallback triggered for create. Error: {}", cause.getMessage());

                // Using Builder as your DTO has @Builder
                return InventoryResponseDTO.builder()
                        .bookId(request.getBookId())
                        .quantity(0) // Default to 0 stock
                        .lowStockThreshold(request.getLowStockThreshold())
                        .isLowStock(true)
                        .isOutOfStock(true)
                        .createdAt(LocalDateTime.now())
                        .build();
            }

            @Override
            public InventoryResponseDTO getInventoryByBookId(Long bookId) {
                log.error("Inventory Service is down or forbidden. Fallback triggered for get. Error: {}", cause.getMessage());

                return InventoryResponseDTO.builder()
                        .bookId(bookId)
                        .quantity(0)
                        .lowStockThreshold(5)
                        .isLowStock(true)
                        .isOutOfStock(true)
                        .updatedAt(LocalDateTime.now())
                        .build();
            }
        };
    }
}