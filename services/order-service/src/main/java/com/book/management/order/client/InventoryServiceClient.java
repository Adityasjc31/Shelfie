package com.book.management.order.client;


import com.book.management.order.client.fallback.InventoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign Client for Inventory Service.
 * Only calls reduction; inventory handles its own validation.
 */
@FeignClient(name = "inventory-service", path = "/api/v1/inventory", fallback = InventoryClientFallback.class)
public interface InventoryServiceClient {
    @PostMapping("/reduce")
    void reduceStock(@RequestBody Map<Long, Integer> itemsToReduce);
}
