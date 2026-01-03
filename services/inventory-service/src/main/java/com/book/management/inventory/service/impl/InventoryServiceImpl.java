package com.book.management.inventory.service.impl;

import com.book.management.inventory.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.inventory.exception.InvalidInventoryOperationException;
import com.book.management.inventory.exception.InventoryAlreadyExistsException;
import com.book.management.inventory.exception.InventoryNotFoundException;
import com.book.management.inventory.model.Inventory;
import com.book.management.inventory.repository.InventoryRepository;
import com.book.management.inventory.service.InventoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
// @Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponseDTO createInventory(InventoryCreateDTO createDTO) {
        Long bookId = createDTO.getBookId();
        log.info("Creating inventory for book ID: {}", bookId);

        if (inventoryRepository.existsByBookId(bookId)) {
            log.error("Inventory already exists for book ID: {}", bookId);
            throw new InventoryAlreadyExistsException(bookId);
        }

        Inventory inventory = Inventory.builder()
                .bookId(bookId)
                .quantity(createDTO.getQuantity())
                .lowStockThreshold(createDTO.getLowStockThreshold() != null ? createDTO.getLowStockThreshold() : 10)
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Successfully created inventory with ID: {} for book ID: {}",
                savedInventory.getInventoryId(), savedInventory.getBookId());

        return mapToResponseDTO(savedInventory);
    }

    @Override
    // @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryById(Long inventoryId) {
        log.info("Fetching inventory by ID: {}", inventoryId);

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> {
                    log.error("Inventory not found with ID: {}", inventoryId);
                    return new InventoryNotFoundException(inventoryId);
                });

        log.info("Successfully fetched inventory with ID: {}", inventoryId);
        return mapToResponseDTO(inventory);
    }

    @Override
    // @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryByBookId(Long bookId) {
        log.info("Fetching inventory by book ID: {}", bookId);

        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for book ID: {}", bookId);
                    return new InventoryNotFoundException("bookId", bookId);
                });

        log.info("Successfully fetched inventory for book ID: {}", bookId);
        return mapToResponseDTO(inventory);
    }

    @Override
    // @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllInventory() {
        log.info("Fetching all inventory records");

        List<Inventory> inventories = inventoryRepository.findAll();
        log.info("Successfully fetched {} inventory records", inventories.size());

        return inventories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponseDTO updateInventoryQuantity(Long inventoryId, InventoryUpdateDTO updateDTO) {
        log.info("Updating inventory quantity for ID: {} to {}", inventoryId, updateDTO.getQuantity());

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> {
                    log.error("Inventory not found with ID: {}", inventoryId);
                    return new InventoryNotFoundException(inventoryId);
                });

        inventory.setQuantity(updateDTO.getQuantity());
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Successfully updated inventory ID: {} with new quantity: {}",
                inventoryId, updateDTO.getQuantity());

        return mapToResponseDTO(updatedInventory);
    }

    @Override
    public InventoryResponseDTO adjustInventoryQuantity(Long inventoryId, InventoryAdjustmentDTO adjustmentDTO) {
        log.info("Adjusting inventory ID: {} by {} units. Reason: {}",
                inventoryId, adjustmentDTO.getAdjustmentQuantity(), adjustmentDTO.getReason());

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> {
                    log.error("Inventory not found with ID: {}", inventoryId);
                    return new InventoryNotFoundException(inventoryId);
                });

        int newQuantity = inventory.getQuantity() + adjustmentDTO.getAdjustmentQuantity();

        if (newQuantity < 0) {
            log.error("Invalid adjustment: would result in negative quantity. Current: {}, Adjustment: {}",
                    inventory.getQuantity(), adjustmentDTO.getAdjustmentQuantity());
            throw new InvalidInventoryOperationException(
                    "Adjustment would result in negative quantity");
        }

        inventory.setQuantity(newQuantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Successfully adjusted inventory ID: {}. New quantity: {}", inventoryId, newQuantity);

        return mapToResponseDTO(updatedInventory);
    }

    @Override
    public InventoryResponseDTO reduceInventory(Long bookId, Integer quantity) {
        log.info("Reducing inventory for book ID: {} by {} units", bookId, quantity);

        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for book ID: {}", bookId);
                    return new InventoryNotFoundException("bookId", bookId);
                });

        if (inventory.getQuantity() < quantity) {
            log.error("Insufficient stock for book ID: {}. Available: {}, Requested: {}",
                    bookId, inventory.getQuantity(), quantity);
            throw new InsufficientStockException(bookId, inventory.getQuantity(), quantity);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Successfully reduced inventory for book ID: {}. Remaining quantity: {}",
                bookId, updatedInventory.getQuantity());

        return mapToResponseDTO(updatedInventory);
    }

    @Override
    public InventoryResponseDTO restockInventory(Long bookId, Integer quantity) {
        log.info("Restocking inventory for book ID: {} with {} units", bookId, quantity);

        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for book ID: {}", bookId);
                    return new InventoryNotFoundException("bookId", bookId);
                });

        inventory.setQuantity(inventory.getQuantity() + quantity);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Successfully restocked inventory for book ID: {}. New quantity: {}",
                bookId, updatedInventory.getQuantity());

        return mapToResponseDTO(updatedInventory);
    }

    @Override
    // @Transactional(readOnly = true)
    public boolean checkAvailability(Long bookId, Integer quantity) {
        log.info("Checking availability for book ID: {} with quantity: {}", bookId, quantity);

        return inventoryRepository.findByBookId(bookId)
                .map(inventory -> {
                    boolean available = inventory.getQuantity() >= quantity;
                    log.info("Availability check for book ID: {} - Available: {}", bookId, available);
                    return available;
                })
                .orElse(false);
    }

    @Override
    // @Transactional(readOnly = true)
    public List<LowStockAlertDTO> getLowStockItems() {
        log.info("Fetching low stock items");

        List<Inventory> lowStockInventories = inventoryRepository.findLowStockItems();
        log.info("Found {} low stock items", lowStockInventories.size());

        return lowStockInventories.stream()
                .map(this::mapToLowStockAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
    // @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getOutOfStockItems() {
        log.info("Fetching out-of-stock items");

        List<Inventory> outOfStockInventories = inventoryRepository.findOutOfStockItems();
        log.info("Found {} out-of-stock items", outOfStockInventories.size());

        return outOfStockInventories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    // @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInStockItems() {
        log.info("Fetching in-stock items");

        List<Inventory> inStockInventories = inventoryRepository.findInStockItems();
        log.info("Found {} in-stock items", inStockInventories.size());

        return inStockInventories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponseDTO updateLowStockThreshold(Long inventoryId, Integer newThreshold) {
        log.info("Updating low stock threshold for inventory ID: {} to {}", inventoryId, newThreshold);

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> {
                    log.error("Inventory not found with ID: {}", inventoryId);
                    return new InventoryNotFoundException(inventoryId);
                });

        inventory.setLowStockThreshold(newThreshold);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Successfully updated low stock threshold for inventory ID: {}", inventoryId);

        return mapToResponseDTO(updatedInventory);
    }

    @Override
    public void deleteInventory(Long inventoryId) {
        log.info("Deleting inventory with ID: {}", inventoryId);

        if (!inventoryRepository.existsById(inventoryId)) {
            log.error("Inventory not found with ID: {}", inventoryId);
            throw new InventoryNotFoundException(inventoryId);
        }

        inventoryRepository.deleteById(inventoryId);
        log.info("Successfully deleted inventory with ID: {}", inventoryId);
    }

    @Override
    public void deleteInventoryByBookId(Long bookId) {
        log.info("Deleting inventory for book ID: {}", bookId);

        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for book ID: {}", bookId);
                    return new InventoryNotFoundException("bookId", bookId);
                });

        inventoryRepository.delete(inventory);
        log.info("Successfully deleted inventory for book ID: {}", bookId);
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
    public Map<Long, Boolean> checkBulkAvailability(Map<Long, Integer> bookQuantities) {
        log.info("Checking bulk availability for {} books", bookQuantities.size());

        Map<Long, Boolean> availabilityMap = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : bookQuantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer requiredQuantity = entry.getValue();

            boolean available = checkAvailability(bookId, requiredQuantity);
            availabilityMap.put(bookId, available);

            log.debug("Book ID: {} - Required: {} - Available: {}",
                    bookId, requiredQuantity, available);
        }

        log.info("Bulk availability check completed. Results: {}", availabilityMap);
        return availabilityMap;
    }

    @Override
    public void reduceBulkInventory(Map<Long, Integer> bookQuantities) {
        log.info("Reducing bulk inventory for {} books", bookQuantities.size());

        // First, check if all books are available
        Map<Long, Boolean> availabilityMap = checkBulkAvailability(bookQuantities);

        boolean allAvailable = availabilityMap.values().stream().allMatch(Boolean::booleanValue);

        if (!allAvailable) {
            List<Long> unavailableBooks = availabilityMap.entrySet().stream()
                    .filter(entry -> !entry.getValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            log.error("Cannot reduce inventory. Unavailable books: {}", unavailableBooks);
            throw new InsufficientStockException(
                    "Insufficient stock for books: " + unavailableBooks);
        }

        // All available - proceed with deduction
        for (Map.Entry<Long, Integer> entry : bookQuantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();
            reduceInventory(bookId, quantity);
            log.debug("Successfully reduced {} units for book ID: {}", quantity, bookId);
        }

        log.info("Bulk inventory reduction completed successfully for {} books", bookQuantities.size());
    }
}
