package com.db.ms.repository;
import jakarta.annotation.PostConstruct;
import com.db.ms.model.Inventory;

import java.util.*;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Inventory entity.
 * Defines methods for CRUD operations and custom queries on inventory records.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-08
 */
@Repository
public interface InventoryRepository {
    /**
     * Initializes repository with sample data for testing.
     * This method is called after the bean is constructed.
     */
    @PostConstruct
    public void initializeSampleData();
    
    /**
     * Saves inventory record (create or update).
     *
     * @param inventory the inventory to save
     * @return the saved inventory
     */
    public Inventory save(Inventory inventory);

    /**
     * Finds inventory by ID.
     *
     * @param inventoryId the inventory ID
     * @return Optional containing the inventory if found
     */
    public Optional<Inventory> findById(Long inventoryId);
    /**
     * Finds inventory by book ID.
     *
     * @param bookId the book ID
     * @return Optional containing the inventory if found
     */
    public Optional<Inventory> findByBookId(Long bookId) ;

    /**
     * Checks if inventory exists for a book ID.
     *
     * @param bookId the book ID
     * @return true if exists, false otherwise
     */
    public boolean existsByBookId(Long bookId) ;

    /**
     * Checks if inventory exists by ID.
     *
     * @param inventoryId the inventory ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(Long inventoryId);

    /**
     * Finds all inventory records.
     *
     * @return List of all inventories
     */
    public List<Inventory> findAll();

    /**
     * Finds all inventory records with low stock.
     *
     * @return List of low stock inventories
     */
    public List<Inventory> findLowStockItems();

    /**
     * Finds all out-of-stock inventory records.
     *
     * @return List of out-of-stock inventories
     */
    public List<Inventory> findOutOfStockItems();

    /**
     * Finds all in-stock inventory records.
     *
     * @return List of in-stock inventories
     */
    public List<Inventory> findInStockItems();

    /**
     * Deletes inventory by ID.
     *
     * @param inventoryId the inventory ID
     */
    public void deleteById(Long inventoryId);

    /**
     * Deletes all inventory records.
     * Useful for testing.
     */
    public void deleteAll();

    /**
     * Counts total inventory records.
     *
     * @return count of inventories
     */
    public long count();
}