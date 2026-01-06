
package com.book.management.order.client;

import com.book.management.order.client.fallback.InventoryClientFallbackFactory;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for Inventory Service.
 * Reduces stock for multiple books using a typed request DTO.
 * Endpoint (via Gateway):</b> PATCH /api/v1/inventory/bulk/reduce
 * 
 * Request Body:
 * {
 * "bookQuantities": {
 * "101": 1,
 * "105": 2
 * }
 * }
 * Response (DTO):
 * {
 * "bookStock": {
 * "101": true,
 * "105": true
 * }
 * }
 */
@FeignClient(name = "inventory-service", path = "/api/v1/inventory", fallbackFactory = InventoryClientFallbackFactory.class)
public interface InventoryServiceClient {

        @PatchMapping(value = "/bulk/reduce", consumes = MediaType.APPLICATION_JSON_VALUE)
        void reduceStock(@RequestBody ReduceInventoryStockRequestDTO request);
}
