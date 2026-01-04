
package com.book.management.order.client.fallback;

import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.exception.OrderNotPlacedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback factory for InventoryClient.
 * This captures the actual exception that caused the fallback, enabling better
 * debugging.
 *
 * @author Rehan Ashraf
 * @version 1.3
 */
@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {

    @Override
    public InventoryServiceClient create(Throwable cause) {
        return new InventoryServiceClient() {
            @Override
            public void reduceStock(ReduceInventoryStockRequestDTO request) {
                log.error("CRITICAL: Inventory Service call failed for items: {} | Cause: {} - {}",
                        request.getBookQuantities(),
                        cause.getClass().getSimpleName(),
                        cause.getMessage());

                // Log the full stack trace at debug level
                log.debug("Full exception details:", cause);

                throw new OrderNotPlacedException(
                        "Inventory Service unavailable: " + cause.getMessage());
            }
        };
    }
}
