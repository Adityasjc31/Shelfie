package com.book.management.order.service;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.client.InventoryServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Async wrapper service for Feign client calls.
 * Enables parallel execution of inter-service communication to reduce latency.
 * 
 * Per LLD Section 4.3 - Order Management Module:
 * - Optimizes order placement by running Book Service (price fetch) and 
 *   Inventory Service (availability check) calls in parallel.
 * 
 * Per LLD Section 8.1 - Performance:
 * - Helps handle 500 concurrent users by reducing per-request latency.
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncFeignService {

    private final BookServiceClient bookServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    /**
     * Asynchronously fetches book prices from Book Service.
     * 
     * Per LLD Section 4.1 - Book Catalog Management Module:
     * - Retrieves Price from Book entity for order total calculation.
     *
     * @param request the book price request containing BookIDs
     * @return CompletableFuture containing price response
     */
    @Async("feignExecutor")
    public CompletableFuture<GetBookPriceResponseDTO> getBookPricesAsync(GetBookPriceRequestDTO request) {
        if (log.isDebugEnabled()) {
            log.debug("Async call: Fetching book prices for {} books", request.getBookIds().size());
        }
        long start = System.currentTimeMillis();
        
        try {
            GetBookPriceResponseDTO response = bookServiceClient.getBookPrices(request);
            
            if (log.isDebugEnabled()) {
                log.debug("Async call: Book prices fetched in {} ms", System.currentTimeMillis() - start);
            }
            return CompletableFuture.completedFuture(response);
        } catch (Exception ex) {
            log.error("Async call failed: Error fetching book prices - {}", ex.getMessage());
            return CompletableFuture.failedFuture(ex);
        }
    }

    /**
     * Asynchronously validates inventory availability.
     * 
     * Per LLD Section 4.4 - Inventory Management Module:
     * - Tracks stock levels and prevents out-of-stock purchases.
     * - Uses Inventory entity (InventoryID, BookID, Quantity).
     *
     * @param bookQuantities map of BookID to requested Quantity
     * @return CompletableFuture containing availability map (BookID -> available)
     */
    @Async("feignExecutor")
    public CompletableFuture<Map<Long, Boolean>> checkInventoryAsync(Map<Long, Integer> bookQuantities) {
        if (log.isDebugEnabled()) {
            log.debug("Async call: Checking inventory for {} items", bookQuantities.size());
        }
        long start = System.currentTimeMillis();
        
        try {
            Map<Long, Boolean> availability = inventoryServiceClient.checkBulkAvailability(bookQuantities);
            
            if (log.isDebugEnabled()) {
                log.debug("Async call: Inventory checked in {} ms", System.currentTimeMillis() - start);
            }
            return CompletableFuture.completedFuture(availability);
        } catch (Exception ex) {
            log.error("Async call failed: Error checking inventory - {}", ex.getMessage());
            return CompletableFuture.failedFuture(ex);
        }
    }
}
