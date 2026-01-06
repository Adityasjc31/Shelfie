package com.book.management.inventory.service.impl;

import com.book.management.inventory.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.inventory.exception.InvalidInventoryOperationException;
import com.book.management.inventory.exception.InventoryAlreadyExistsException;
import com.book.management.inventory.exception.InventoryNotFoundException;
import com.book.management.inventory.model.Inventory;
import com.book.management.inventory.repository.InventoryRepository;
import com.book.management.inventory.service.InventoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of InventoryService interface.
 * Handles all business logic for inventory management operations.
 * Follows Single Responsibility Principle (SRP) and Dependency Inversion
 * Principle (DIP).
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private static final String BOOK_ID_FIELD = "bookId";
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public InventoryResponseDTO createInventory(InventoryCreateDTO createDTO) {
        Long bookId = createDTO.getBookId();

        if (inventoryRepository.existsByBookId(bookId)) {
            log.warn("Duplicate inventory attempt for book ID: {}", bookId);
            throw new InventoryAlreadyExistsException(bookId);
        }

        Inventory inventory = Inventory.builder()
                .bookId(bookId)
                .quantity(createDTO.getQuantity())
                .lowStockThreshold(createDTO.getLowStockThreshold() != null ? createDTO.getLowStockThreshold() : 10)
                .build();

        return mapToResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryById(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        return mapToResponseDTO(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryByBookId(Long bookId) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new InventoryNotFoundException(BOOK_ID_FIELD, bookId));

        return mapToResponseDTO(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponseDTO updateInventoryQuantity(Long inventoryId, InventoryUpdateDTO updateDTO) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        inventory.setQuantity(updateDTO.getQuantity());
        return mapToResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDTO adjustInventoryQuantity(Long inventoryId, InventoryAdjustmentDTO adjustmentDTO) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        int newQuantity = inventory.getQuantity() + adjustmentDTO.getAdjustmentQuantity();

        if (newQuantity < 0) {
            log.warn(
                    "Invalid adjustment for inventory ID: {}. Current: {}, Adjustment: {} would result in negative quantity",
                    inventoryId, inventory.getQuantity(), adjustmentDTO.getAdjustmentQuantity());
            throw new InvalidInventoryOperationException(
                    "Adjustment would result in negative quantity");
        }

        inventory.setQuantity(newQuantity);
        return mapToResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDTO reduceInventory(Long bookId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new InventoryNotFoundException(BOOK_ID_FIELD, bookId));

        if (inventory.getQuantity() < quantity) {
            log.warn("Insufficient stock for book ID: {}. Available: {}, Requested: {}",
                    bookId, inventory.getQuantity(), quantity);
            throw new InsufficientStockException(bookId, inventory.getQuantity(), quantity);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        return mapToResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponseDTO restockInventory(Long bookId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new InventoryNotFoundException(BOOK_ID_FIELD, bookId));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        return mapToResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long bookId, Integer quantity) {
        return inventoryRepository.findByBookId(bookId)
                .map(inventory -> inventory.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockAlertDTO> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::mapToLowStockAlertDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInStockItems() {
        return inventoryRepository.findInStockItems().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponseDTO updateLowStockThreshold(Long inventoryId, Integer newThreshold) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));

        inventory.setLowStockThreshold(newThreshold);
        return mapToResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public void deleteInventory(Long inventoryId) {
        if (!inventoryRepository.existsById(inventoryId)) {
            throw new InventoryNotFoundException(inventoryId);
        }

        inventoryRepository.deleteById(inventoryId);
    }

    @Override
    @Transactional
    public void deleteInventoryByBookId(Long bookId) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new InventoryNotFoundException(BOOK_ID_FIELD, bookId));

        inventoryRepository.delete(inventory);
    }

    /**
     * Maps Inventory entity to InventoryResponseDTO.
     *
     * @param inventory the inventory entity
     * @return the inventory response DTO
     */
    private InventoryResponseDTO mapToResponseDTO(Inventory inventory) {
        return InventoryResponseDTO.builder()
                .inventoryId(inventory.getInventoryId())
                .bookId(inventory.getBookId())
                .quantity(inventory.getQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .isLowStock(inventory.isLowStock())
                .isOutOfStock(inventory.isOutOfStock())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    /**
     * Maps Inventory entity to LowStockAlertDTO.
     *
     * @param inventory the inventory entity
     * @return the low stock alert DTO
     */
    private LowStockAlertDTO mapToLowStockAlertDTO(Inventory inventory) {
        int quantityNeeded = inventory.getLowStockThreshold() - inventory.getQuantity();
        String alertLevel = inventory.isOutOfStock() ? "CRITICAL" : "WARNING";

        return LowStockAlertDTO.builder()
                .inventoryId(inventory.getInventoryId())
                .bookId(inventory.getBookId())
                .currentQuantity(inventory.getQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .quantityNeeded(quantityNeeded)
                .alertLevel(alertLevel)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Boolean> checkBulkAvailability(Map<Long, Integer> bookQuantities) {
        if (bookQuantities == null || bookQuantities.isEmpty()) {
            return new HashMap<>();
        }

        // Single bulk query instead of N individual queries
        List<Long> bookIds = new ArrayList<>(bookQuantities.keySet());
        List<Inventory> inventories = inventoryRepository.findByBookIdIn(bookIds);

        // Create a map for quick lookup: bookId -> Inventory
        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(java.util.stream.Collectors.toMap(Inventory::getBookId, inv -> inv));

        // Check availability in-memory
        Map<Long, Boolean> availabilityMap = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : bookQuantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            Inventory inventory = inventoryMap.get(bookId);
            boolean isAvailable = inventory != null && inventory.getQuantity() >= requestedQuantity;
            availabilityMap.put(bookId, isAvailable);
        }

        log.debug("Bulk availability check for {} books completed with single query", bookIds.size());
        return availabilityMap;
    }

    @Override
    @Transactional
    public void reduceBulkInventory(Map<Long, Integer> bookQuantities) {
        if (bookQuantities == null || bookQuantities.isEmpty()) {
            return;
        }

        // Single bulk query to fetch all inventory records
        List<Long> bookIds = new ArrayList<>(bookQuantities.keySet());
        List<Inventory> inventories = inventoryRepository.findByBookIdIn(bookIds);

        // Create a map for quick lookup: bookId -> Inventory
        Map<Long, Inventory> inventoryMap = inventories.stream()
                .collect(java.util.stream.Collectors.toMap(Inventory::getBookId, inv -> inv));

        // Check availability in-memory
        List<Long> unavailableBooks = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : bookQuantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            Inventory inventory = inventoryMap.get(bookId);
            if (inventory == null || inventory.getQuantity() < requestedQuantity) {
                unavailableBooks.add(bookId);
            }
        }

        if (!unavailableBooks.isEmpty()) {
            log.warn("Bulk inventory reduction failed. Unavailable books: {}", unavailableBooks);
            throw new InsufficientStockException(
                    "Insufficient stock for books: " + unavailableBooks);
        }

        // All available - proceed with deduction in-memory and batch save
        for (Map.Entry<Long, Integer> entry : bookQuantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();
            Inventory inventory = inventoryMap.get(bookId);
            inventory.setQuantity(inventory.getQuantity() - quantity);
        }

        // Save all updated inventories in one batch
        inventoryRepository.saveAll(inventories);
        log.debug("Bulk inventory reduction for {} books completed with optimized queries", bookIds.size());
    }
}
