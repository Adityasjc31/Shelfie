package com.db.ms.inventory.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.db.ms.inventory.dto.*;
import com.db.ms.inventory.entity.Inventory;
import com.db.ms.inventory.exceptions.InsufficientStockException;
import com.db.ms.inventory.exceptions.InvalidInventoryOperationException;
import com.db.ms.inventory.exceptions.InventoryAlreadyExistsException;
import com.db.ms.inventory.exceptions.InventoryNotFoundException;
import com.db.ms.inventory.repository.InventoryRepository;
import com.db.ms.inventory.service.InventoryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of InventoryService interface.
 * Handles all business logic for inventory management operations.
 * Follows Single Responsibility Principle (SRP) and Dependency Inversion Principle (DIP).
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
@Service
@RequiredArgsConstructor
@Slf4j
//@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponseDTO createInventory(InventoryCreateDTO createDTO) {
        log.info("Creating inventory for book ID: {}", createDTO.getBookId());

        if (inventoryRepository.existsByBookId(createDTO.getBookId())) {
            log.error("Inventory already exists for book ID: {}", createDTO.getBookId());
            throw new InventoryAlreadyExistsException(createDTO.getBookId());
        }

        Inventory inventory = Inventory.builder()
                .bookId(createDTO.getBookId())
                .quantity(createDTO.getQuantity())
                .lowStockThreshold(createDTO.getLowStockThreshold() != null ?
                        createDTO.getLowStockThreshold() : 10)
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Successfully created inventory with ID: {} for book ID: {}",
                savedInventory.getInventoryId(), savedInventory.getBookId());

        return mapToResponseDTO(savedInventory);
    }

    @Override
//    @Transactional(readOnly = true)
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
//    @Transactional(readOnly = true)
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
//    @Transactional(readOnly = true)
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
//    @Transactional(readOnly = true)
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
//    @Transactional(readOnly = true)
    public List<LowStockAlertDTO> getLowStockItems() {
        log.info("Fetching low stock items");

        List<Inventory> lowStockInventories = inventoryRepository.findLowStockItems();
        log.info("Found {} low stock items", lowStockInventories.size());

        return lowStockInventories.stream()
                .map(this::mapToLowStockAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
//    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getOutOfStockItems() {
        log.info("Fetching out-of-stock items");

        List<Inventory> outOfStockInventories = inventoryRepository.findOutOfStockItems();
        log.info("Found {} out-of-stock items", outOfStockInventories.size());

        return outOfStockInventories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
//    @Transactional(readOnly = true)
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
}
