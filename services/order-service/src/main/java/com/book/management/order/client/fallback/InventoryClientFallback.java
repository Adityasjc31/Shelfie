
package com.book.management.order.client.fallback;

import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.exception.OrderNotPlacedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for InventoryClient.
 * This class is triggered when the Inventory Service is down or responding too
 * slowly.
 * If stock reduction cannot be confirmed, the order process must stop.
 *
 * @author Rehan Ashraf
 * @version 1.2
 */

@Component
@Slf4j
public class InventoryClientFallback implements InventoryServiceClient {

    /**
     * Fallback for stock reduction.
     * Since we cannot safely assume stock is available if the service is down,
     * we throw a specific exception to stop the order process.
     */
    @Override
    public void reduceStock(ReduceInventoryStockRequestDTO request) {
        log.error("CRITICAL: Inventory Service is unreachable. Stock reduction failed for items: {}",
                request.getBookQuantities());
        throw new OrderNotPlacedException("Inventory Service is temporarily unavailable. Please try again later.");
    }
}
