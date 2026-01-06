
package com.book.management.order.client;

import com.book.management.order.client.fallback.InventoryClientFallbackFactory;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.dto.responsedto.ReduceInventoryStockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign Client for Inventory Service.
 * 
 * Per LLD Section 4.4 - Inventory Management Module:
 * - Tracks available book stock and updates on purchase.
 * - Uses Inventory entity (InventoryID, BookID, Quantity).
 * 
 * Endpoints:
 * - POST /api/v1/inventory/bulk/check - Check availability without modifying stock
 * - PATCH /api/v1/inventory/bulk/reduce - Reduce stock for order placement
 *
 * @author Rehan Ashraf
 * @version 2.0
 */
@FeignClient(name = "inventory-service", path = "/api/v1/inventory", fallbackFactory = InventoryClientFallbackFactory.class)
public interface InventoryServiceClient {

        /**
         * Checks bulk availability without modifying stock.
         * Enables parallel validation with price fetch during order placement.
         * 
         * @param bookQuantities map of BookID to requested Quantity
         * @return map of BookID to availability status (true if available)
         */
        @PostMapping(value = "/bulk/check", consumes = MediaType.APPLICATION_JSON_VALUE)
        Map<Long, Boolean> checkBulkAvailability(@RequestBody Map<Long, Integer> bookQuantities);

        /**
         * Reduces stock for multiple books in a single transaction.
         * Called after availability validation during order placement.
         * 
         * @param request the reduce stock request with BookID -> Quantity mapping
         */
        @PatchMapping(value = "/bulk/reduce", consumes = MediaType.APPLICATION_JSON_VALUE)
        void reduceStock(@RequestBody ReduceInventoryStockRequestDTO request);
}
