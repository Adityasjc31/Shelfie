
package com.book.management.order.client;

import com.book.management.order.client.fallback.InventoryClientFallback;
import com.book.management.order.dto.requestdto.ReduceInventoryStockRequestDTO;
import com.book.management.order.dto.responsedto.ReduceInventoryStockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for Inventory Service.
 * Reduces stock for multiple books using a typed request DTO.
 *
 * <p><b>Endpoint (via Gateway):</b> PATCH /api/v1/inventory/bulk/reduce</p>
 *
 * <p><b>Request Body:</b></p>
 * <pre>
 * {
 *   "bookQuantities": {
 *     "101": 1,
 *     "105": 2
 *   }
 * }
 * </pre>
 *
 * <p><b>Response (DTO):</b></p>
 * <pre>
 * {
 *   "bookStock": {
 *     "101": true,
 *     "105": true
 *   }
 * }
 * </pre>
 */
@FeignClient(
        name = "inventory-service",
        path = "/api/v1/inventory",
        fallback = InventoryClientFallback.class
)
public interface InventoryServiceClient {

    @PatchMapping(
            value = "/bulk/reduce",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ReduceInventoryStockResponseDTO reduceStock(@RequestBody ReduceInventoryStockRequestDTO request);
}
