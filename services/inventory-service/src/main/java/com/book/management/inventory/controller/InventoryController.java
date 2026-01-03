package com.book.management.inventory.controller;

import com.book.management.inventory.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.book.management.inventory.service.InventoryService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Inventory Management operations.
 * Exposes endpoints for managing book inventory, stock levels, and alerts.
 * Logging is handled automatically by LoggingAspect.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-07
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

        private final InventoryService inventoryService;

        /**
         * Creates a new inventory record.
         *
         * @param createDTO the inventory creation data
         * @return ResponseEntity with created inventory and HTTP 201 status
         */
        @PostMapping("/create")
        public ResponseEntity<InventoryResponseDTO> createInventory(
                        @Valid @RequestBody InventoryCreateDTO createDTO) {
                return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(createDTO));
        }

        /**
         * Retrieves an inventory record by its ID.
         *
         * @param inventoryId the inventory ID
         * @return ResponseEntity with inventory data and HTTP 200 status
         */
        @GetMapping("/{inventoryId}")
        public ResponseEntity<InventoryResponseDTO> getInventoryById(
                        @PathVariable Long inventoryId) {
                return ResponseEntity.ok(inventoryService.getInventoryById(inventoryId));
        }

        /**
         * Retrieves an inventory record by book ID.
         *
         * @param bookId the book ID
         * @return ResponseEntity with inventory data and HTTP 200 status
         */
        @GetMapping("/book/{bookId}")
        public ResponseEntity<InventoryResponseDTO> getInventoryByBookId(
                        @PathVariable Long bookId) {
                return ResponseEntity.ok(inventoryService.getInventoryByBookId(bookId));
        }

        /**
         * Retrieves all inventory records.
         *
         * @return ResponseEntity with list of all inventory records and HTTP 200 status
         */
        @GetMapping
        public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
                return ResponseEntity.ok(inventoryService.getAllInventory());
        }

        /**
         * Reduces inventory for a book purchase.
         *
         * @param bookId   the book ID
         * @param quantity the quantity to reduce
         * @return ResponseEntity with updated inventory and HTTP 200 status
         */
        @PatchMapping("/book/{bookId}/reduce")
        public ResponseEntity<InventoryResponseDTO> reduceInventory(
                        @PathVariable Long bookId,
                        @RequestParam Integer quantity) {
                return ResponseEntity.ok(inventoryService.reduceInventory(bookId, quantity));
        }

        /**
         * Restocks inventory for a book.
         *
         * @param bookId   the book ID
         * @param quantity the quantity to add
         * @return ResponseEntity with updated inventory and HTTP 200 status
         */
        @PatchMapping("/book/{bookId}/restock")
        public ResponseEntity<InventoryResponseDTO> restockInventory(
                        @PathVariable Long bookId,
                        @RequestParam Integer quantity) {
                return ResponseEntity.ok(inventoryService.restockInventory(bookId, quantity));
        }

        /**
         * Checks if a book is available in sufficient quantity.
         *
         * @param bookId   the book ID
         * @param quantity the required quantity
         * @return ResponseEntity with availability status and HTTP 200 status
         */
        @GetMapping("/book/{bookId}/availability")
        public ResponseEntity<Boolean> checkAvailability(
                        @PathVariable Long bookId,
                        @RequestParam Integer quantity) {
                return ResponseEntity.ok(inventoryService.checkAvailability(bookId, quantity));
        }

        /**
         * Retrieves all low stock items.
         *
         * @return ResponseEntity with list of low stock alerts and HTTP 200 status
         */
        @GetMapping("/alerts/low-stock")
        public ResponseEntity<List<LowStockAlertDTO>> getLowStockItems() {
                return ResponseEntity.ok(inventoryService.getLowStockItems());
        }

        /**
         * Retrieves all out-of-stock items.
         *
         * @return ResponseEntity with list of out-of-stock items and HTTP 200 status
         */
        @GetMapping("/status/out-of-stock")
        public ResponseEntity<List<InventoryResponseDTO>> getOutOfStockItems() {
                return ResponseEntity.ok(inventoryService.getOutOfStockItems());
        }

        /**
         * Retrieves all in-stock items.
         *
         * @return ResponseEntity with list of in-stock items and HTTP 200 status
         */
        @GetMapping("/status/in-stock")
        public ResponseEntity<List<InventoryResponseDTO>> getInStockItems() {
                return ResponseEntity.ok(inventoryService.getInStockItems());
        }

        /**
         * Updates the low stock threshold for an inventory record.
         *
         * @param inventoryId the inventory ID
         * @param threshold   the new threshold value
         * @return ResponseEntity with updated inventory and HTTP 200 status
         */
        @PatchMapping("/{inventoryId}/threshold")
        public ResponseEntity<InventoryResponseDTO> updateLowStockThreshold(
                        @PathVariable Long inventoryId,
                        @RequestParam Integer threshold) {
                return ResponseEntity.ok(inventoryService.updateLowStockThreshold(inventoryId, threshold));
        }

        /**
         * Deletes an inventory record.
         *
         * @param inventoryId the inventory ID
         * @return ResponseEntity with HTTP 204 No Content status
         */
        @DeleteMapping("/{inventoryId}")
        public ResponseEntity<Void> deleteInventory(@PathVariable Long inventoryId) {
                inventoryService.deleteInventory(inventoryId);
                return ResponseEntity.noContent().build();
        }

        /**
         * Deletes an inventory record by book ID.
         *
         * @param bookId the book ID
         * @return ResponseEntity with HTTP 204 No Content status
         */
        @DeleteMapping("/book/{bookId}")
        public ResponseEntity<Void> deleteInventoryByBookId(@PathVariable Long bookId) {
                inventoryService.deleteInventoryByBookId(bookId);
                return ResponseEntity.noContent().build();
        }

        /**
         * Checks stock availability for multiple books.
         *
         * @param checkDTO the bulk stock check data
         * @return ResponseEntity with availability map and HTTP 200 status
         */
        @PostMapping("/bulk/check-availability")
        public ResponseEntity<BulkStockCheckResponseDTO> checkBulkAvailability(
                        @Valid @RequestBody BulkStockCheckDTO checkDTO) {
                Map<Long, Boolean> availabilityMap = inventoryService.checkBulkAvailability(
                                checkDTO.getBookQuantities());

                boolean allAvailable = availabilityMap.values().stream()
                                .allMatch(Boolean::booleanValue);

                String message = allAvailable ? "All books are available in required quantities"
                                : "Some books are not available in required quantities";

                return ResponseEntity.ok(BulkStockCheckResponseDTO.builder()
                                .availabilityMap(availabilityMap)
                                .allAvailable(allAvailable)
                                .message(message)
                                .build());
        }

    /**
     * Reduces inventory for multiple books (bulk deduction).
     * Handles all validation and throws exceptions if any book has insufficient stock.
     *
     * @param request DTO containing map of bookId to quantity to reduce
     */
    @PatchMapping("/bulk/reduce")
    public ResponseEntity<Void> reduceBulkInventory(
            @RequestBody BulkStockReduceDTO request) {
        // Pass the internal map from the DTO to the service
        inventoryService.reduceBulkInventory(request.getBookQuantities());
        return ResponseEntity.ok().build();
    }

}