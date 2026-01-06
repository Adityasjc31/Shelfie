
package com.book.management.order.client.fallback;

import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.exception.OrderNotPlacedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback factory for InventoryClient.
 * Captures the actual exception that caused the fallback for better debugging.
 * 
 * Per LLD Section 4.4 - Inventory Management Module:
 * - Provides graceful degradation when Inventory Service is unavailable.
 *
 * @author Rehan Ashraf
 * @version 2.0
 */
@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {

    @Override
    public InventoryServiceClient create(Throwable cause) {
        return new InventoryServiceClient() {
            
            @Override
            public Map<Long, Boolean> checkBulkAvailability(Map<Long, Integer> bookQuantities) {
                log.error("CRITICAL: Inventory availability check failed for {} items | Cause: {} - {}",
                        bookQuantities.size(),
                        cause.getClass().getSimpleName(),
                        cause.getMessage());
                log.debug("Full exception details:", cause);
                
                throw new OrderNotPlacedException(
                        "Inventory Service unavailable: " + cause.getMessage());
            }
            
            @Override
            public void reduceStock(ReduceInventoryStockRequestDTO request) {
                log.error("CRITICAL: Inventory Service call failed for items: {} | Cause: {} - {}",
                        request.getBookQuantities(),
                        cause.getClass().getSimpleName(),
                        cause.getMessage());
                log.debug("Full exception details:", cause);

                throw new OrderNotPlacedException(
                        "Inventory Service unavailable: " + cause.getMessage());
            }
        };
    }
}
