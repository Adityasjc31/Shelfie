package com.db.ms.inventory.repository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.db.ms.inventory.entity.Inventory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository class for Inventory entity using in-memory data structures.
 * Uses ConcurrentHashMap for thread-safe operations.
 * Can be easily replaced with JPA repository interface later.
 *
 * @author Shelfie
 * @version 1.0
 * @since 2024-12-08
 */
@Repository
@Slf4j
public class InventoryRepository {

    // In-memory storage using ConcurrentHashMap for thread safety
    private final Map<Long, Inventory> inventoryStore = new ConcurrentHashMap<>();

    // Index for quick lookup by bookId
    private final Map<Long, Long> bookIdToInventoryIdIndex = new ConcurrentHashMap<>();

    // Auto-increment ID generator
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Initializes repository with sample data for testing.
     * This method is called after the bean is constructed.
     */
    @PostConstruct
    public void initializeSampleData() {
        log.info("Initializing repository with sample inventory data");

        // Sample inventory data
        createSampleInventory(101L, 50, 10);
        createSampleInventory(102L, 75, 15);
        createSampleInventory(103L, 5, 10);   // Low stock
        createSampleInventory(104L, 0, 10);   // Out of stock
        createSampleInventory(105L, 100, 20);
        createSampleInventory(106L, 8, 10);   // Low stock
        createSampleInventory(107L, 150, 25);
        createSampleInventory(108L, 30, 10);
        createSampleInventory(109L, 0, 15);   // Out of stock
        createSampleInventory(110L, 200, 30);

        log.info("Sample data initialized: {} inventory records", inventoryStore.size());
    }

    /**
     * Helper method to create sample inventory.
     */
    private void createSampleInventory(Long bookId, Integer quantity, Integer threshold) {
        Inventory inventory = Inventory.builder()
                .inventoryId(idGenerator.getAndIncrement())
                .bookId(bookId)
                .quantity(quantity)
                .lowStockThreshold(threshold)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        inventoryStore.put(inventory.getInventoryId(), inventory);
        bookIdToInventoryIdIndex.put(bookId, inventory.getInventoryId());
    }

    /**
     * Saves inventory record (create or update).
     *
     * @param inventory the inventory to save
     * @return the saved inventory
     */
    public Inventory save(Inventory inventory) {
        if (inventory.getInventoryId() == null) {
            // New inventory - generate ID
            inventory.setInventoryId(idGenerator.getAndIncrement());
            inventory.setCreatedAt(LocalDateTime.now());
        }
        inventory.setUpdatedAt(LocalDateTime.now());

        inventoryStore.put(inventory.getInventoryId(), inventory);
        bookIdToInventoryIdIndex.put(inventory.getBookId(), inventory.getInventoryId());

        log.debug("Saved inventory: {}", inventory);
        return inventory;
    }

    /**
     * Finds inventory by ID.
     *
     * @param inventoryId the inventory ID
     * @return Optional containing the inventory if found
     */
    public Optional<Inventory> findById(Long inventoryId) {
        return Optional.ofNullable(inventoryStore.get(inventoryId));
    }

    /**
     * Finds inventory by book ID.
     *
     * @param bookId the book ID
     * @return Optional containing the inventory if found
     */
    public Optional<Inventory> findByBookId(Long bookId) {
        Long inventoryId = bookIdToInventoryIdIndex.get(bookId);
        if (inventoryId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(inventoryStore.get(inventoryId));
    }

    /**
     * Checks if inventory exists for a book ID.
     *
     * @param bookId the book ID
     * @return true if exists, false otherwise
     */
    public boolean existsByBookId(Long bookId) {
        return bookIdToInventoryIdIndex.containsKey(bookId);
    }

    /**
     * Checks if inventory exists by ID.
     *
     * @param inventoryId the inventory ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(Long inventoryId) {
        return inventoryStore.containsKey(inventoryId);
    }

    /**
     * Finds all inventory records.
     *
     * @return List of all inventories
     */
    public List<Inventory> findAll() {
        return new ArrayList<>(inventoryStore.values());
    }

    /**
     * Finds all inventory records with low stock.
     *
     * @return List of low stock inventories
     */
    public List<Inventory> findLowStockItems() {
        return inventoryStore.values().stream()
                .filter(Inventory::isLowStock)
                .collect(Collectors.toList());
    }

    /**
     * Finds all out-of-stock inventory records.
     *
     * @return List of out-of-stock inventories
     */
    public List<Inventory> findOutOfStockItems() {
        return inventoryStore.values().stream()
                .filter(Inventory::isOutOfStock)
                .collect(Collectors.toList());
    }

    /**
     * Finds all in-stock inventory records.
     *
     * @return List of in-stock inventories
     */
    public List<Inventory> findInStockItems() {
        return inventoryStore.values().stream()
                .filter(inventory -> inventory.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Deletes inventory by ID.
     *
     * @param inventoryId the inventory ID
     */
    public void deleteById(Long inventoryId) {
        Inventory inventory = inventoryStore.get(inventoryId);
        if (inventory != null) {
            bookIdToInventoryIdIndex.remove(inventory.getBookId());
            inventoryStore.remove(inventoryId);
            log.debug("Deleted inventory with ID: {}", inventoryId);
        }
    }

    /**
     * Deletes all inventory records.
     * Useful for testing.
     */
    public void deleteAll() {
        inventoryStore.clear();
        bookIdToInventoryIdIndex.clear();
        log.debug("Deleted all inventory records");
    }

    /**
     * Counts total inventory records.
     *
     * @return count of inventories
     */
    public long count() {
        return inventoryStore.size();
    }
}