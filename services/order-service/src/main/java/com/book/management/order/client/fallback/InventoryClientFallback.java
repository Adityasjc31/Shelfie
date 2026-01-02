package com.book.management.order.client.fallback;

import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.exception.OrderNotPlacedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback implementation for InventoryClient.
 * This class is triggered when the Inventory Service is down or responding too slowly.
 * * @author Rehan Ashraf
 * @version 1.0
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
    public void reduceStock(Map<Long, Integer> items) {
        log.error("CRITICAL: Inventory Service is unreachable. Stock reduction failed for items: {}", items);

        // In a real-world scenario, you don't want to place an order if you can't confirm stock reduction.
        throw new OrderNotPlacedException("Inventory Service is temporarily unavailable. Please try again later.");
    }
}
