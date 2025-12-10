package com.db.ms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db.ms.dto.*;
import com.db.ms.service.InventoryService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Inventory Management operations.
 * Exposes endpoints for managing book inventory, stock levels, and alerts.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-07
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Creates a new inventory record.
     *
     * @param createDTO the inventory creation data
     * @return ResponseEntity with created inventory and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @Valid @RequestBody InventoryCreateDTO createDTO) {
        log.info("POST /api/v1/inventory - Creating inventory for book ID: {}", createDTO.getBookId());

        InventoryResponseDTO response = inventoryService.createInventory(createDTO);

        log.info("Successfully created inventory with ID: {}", response.getInventoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
        log.info("GET /api/v1/inventory/{} - Fetching inventory", inventoryId);

        InventoryResponseDTO response = inventoryService.getInventoryById(inventoryId);

        log.info("Successfully retrieved inventory with ID: {}", inventoryId);
        return ResponseEntity.ok(response);
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
        log.info("GET /api/v1/inventory/book/{} - Fetching inventory by book ID", bookId);

        InventoryResponseDTO response = inventoryService.getInventoryByBookId(bookId);

        log.info("Successfully retrieved inventory for book ID: {}", bookId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all inventory records.
     *
     * @return ResponseEntity with list of all inventory records and HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {
        log.info("GET /api/v1/inventory - Fetching all inventory records");

        List<InventoryResponseDTO> response = inventoryService.getAllInventory();

        log.info("Successfully retrieved {} inventory records", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Updates inventory quantity.
     *
     * @param inventoryId the inventory ID
     * @param updateDTO the update data
     * @return ResponseEntity with updated inventory and HTTP 200 status
     */
    @PutMapping("/{inventoryId}/quantity")
    public ResponseEntity<InventoryResponseDTO> updateInventoryQuantity(
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryUpdateDTO updateDTO) {
        log.info("PUT /api/v1/inventory/{}/quantity - Updating quantity to {}",
                inventoryId, updateDTO.getQuantity());

        InventoryResponseDTO response = inventoryService.updateInventoryQuantity(inventoryId, updateDTO);

        log.info("Successfully updated inventory ID: {} quantity", inventoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Adjusts inventory quantity by a specified amount.
     *
     * @param inventoryId the inventory ID
     * @param adjustmentDTO the adjustment data
     * @return ResponseEntity with updated inventory and HTTP 200 status
     */
    @PatchMapping("/{inventoryId}/adjust")
    public ResponseEntity<InventoryResponseDTO> adjustInventoryQuantity(
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryAdjustmentDTO adjustmentDTO) {
        log.info("PATCH /api/v1/inventory/{}/adjust - Adjusting by {} units",
                inventoryId, adjustmentDTO.getAdjustmentQuantity());

        InventoryResponseDTO response = inventoryService.adjustInventoryQuantity(inventoryId, adjustmentDTO);

        log.info("Successfully adjusted inventory ID: {}", inventoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reduces inventory for a book purchase.
     *
     * @param bookId the book ID
     * @param quantity the quantity to reduce
     * @return ResponseEntity with updated inventory and HTTP 200 status
     */
    @PatchMapping("/book/{bookId}/reduce")
    public ResponseEntity<InventoryResponseDTO> reduceInventory(
            @PathVariable Long bookId,
            @RequestParam Integer quantity) {
        log.info("PATCH /api/v1/inventory/book/{}/reduce?quantity={} - Reducing inventory",
                bookId, quantity);

        InventoryResponseDTO response = inventoryService.reduceInventory(bookId, quantity);

        log.info("Successfully reduced inventory for book ID: {}", bookId);
        return ResponseEntity.ok(response);
    }

    /**
     * Restocks inventory for a book.
     *
     * @param bookId the book ID
     * @param quantity the quantity to add
     * @return ResponseEntity with updated inventory and HTTP 200 status
     */
    @PatchMapping("/book/{bookId}/restock")
    public ResponseEntity<InventoryResponseDTO> restockInventory(
            @PathVariable Long bookId,
            @RequestParam Integer quantity) {
        log.info("PATCH /api/v1/inventory/book/{}/restock?quantity={} - Restocking inventory",
                bookId, quantity);

        InventoryResponseDTO response = inventoryService.restockInventory(bookId, quantity);

        log.info("Successfully restocked inventory for book ID: {}", bookId);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if a book is available in sufficient quantity.
     *
     * @param bookId the book ID
     * @param quantity the required quantity
     * @return ResponseEntity with availability status and HTTP 200 status
     */
    @GetMapping("/book/{bookId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long bookId,
            @RequestParam Integer quantity) {
        log.info("GET /api/v1/inventory/book/{}/availability?quantity={} - Checking availability",
                bookId, quantity);

        boolean available = inventoryService.checkAvailability(bookId, quantity);

        log.info("Availability check for book ID: {} - Available: {}", bookId, available);
        return ResponseEntity.ok(available);
    }

    /**
     * Retrieves all low stock items.
     *
     * @return ResponseEntity with list of low stock alerts and HTTP 200 status
     */
    @GetMapping("/alerts/low-stock")
    public ResponseEntity<List<LowStockAlertDTO>> getLowStockItems() {
        log.info("GET /api/v1/inventory/alerts/low-stock - Fetching low stock items");

        List<LowStockAlertDTO> response = inventoryService.getLowStockItems();

        log.info("Successfully retrieved {} low stock alerts", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all out-of-stock items.
     *
     * @return ResponseEntity with list of out-of-stock items and HTTP 200 status
     */
    @GetMapping("/status/out-of-stock")
    public ResponseEntity<List<InventoryResponseDTO>> getOutOfStockItems() {
        log.info("GET /api/v1/inventory/status/out-of-stock - Fetching out-of-stock items");

        List<InventoryResponseDTO> response = inventoryService.getOutOfStockItems();

        log.info("Successfully retrieved {} out-of-stock items", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all in-stock items.
     *
     * @return ResponseEntity with list of in-stock items and HTTP 200 status
     */
    @GetMapping("/status/in-stock")
    public ResponseEntity<List<InventoryResponseDTO>> getInStockItems() {
        log.info("GET /api/v1/inventory/status/in-stock - Fetching in-stock items");

        List<InventoryResponseDTO> response = inventoryService.getInStockItems();

        log.info("Successfully retrieved {} in-stock items", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the low stock threshold for an inventory record.
     *
     * @param inventoryId the inventory ID
     * @param threshold the new threshold value
     * @return ResponseEntity with updated inventory and HTTP 200 status
     */
    @PatchMapping("/{inventoryId}/threshold")
    public ResponseEntity<InventoryResponseDTO> updateLowStockThreshold(
            @PathVariable Long inventoryId,
            @RequestParam Integer threshold) {
        log.info("PATCH /api/v1/inventory/{}/threshold?threshold={} - Updating threshold",
                inventoryId, threshold);

        InventoryResponseDTO response = inventoryService.updateLowStockThreshold(inventoryId, threshold);

        log.info("Successfully updated threshold for inventory ID: {}", inventoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an inventory record.
     *
     * @param inventoryId the inventory ID
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long inventoryId) {
        log.info("DELETE /api/v1/inventory/{} - Deleting inventory", inventoryId);

        inventoryService.deleteInventory(inventoryId);

        log.info("Successfully deleted inventory with ID: {}", inventoryId);
        return ResponseEntity.noContent().build();
    }
}