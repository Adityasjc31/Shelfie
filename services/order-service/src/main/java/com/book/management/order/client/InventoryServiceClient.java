package com.book.management.order.client;

import com.book.management.order.client.fallback.InventoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign Client for Inventory Service.
 * Only calls reduction; inventory service handles all validation and
 * exceptions.
 */
@FeignClient(name = "inventory-service", path = "/api/v1/inventory", fallback = InventoryClientFallback.class)
public interface InventoryServiceClient {
    /**
     * Reduces inventory for multiple books in an order.
     * Inventory service handles availability checking and throws exceptions if
     * insufficient stock.
     * 
     * @param bookQuantities map of bookId to quantity to reduce
     */
    @PatchMapping("/bulk/reduce")
    void reduceStock(@RequestBody Map<Long, Integer> bookQuantities);
}
