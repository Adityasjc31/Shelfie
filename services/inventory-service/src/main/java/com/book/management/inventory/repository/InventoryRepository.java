package com.book.management.inventory.repository;

import com.book.management.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface for Inventory entity.
 * 
 * Provides CRUD operations and custom queries for inventory management.
 * Extends JpaRepository for standard database operations.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Finds inventory by book ID.
     * 
     * @param bookId the book ID
     * @return Optional containing the inventory if found
     */
    Optional<Inventory> findByBookId(Long bookId);

    /**
     * Checks if inventory exists for a book ID.
     * 
     * @param bookId the book ID
     * @return true if exists, false otherwise
     */
    boolean existsByBookId(Long bookId);

    /**
     * Finds all inventory records with low stock.
     * Returns items where quantity is less than or equal to low stock threshold.
     * 
     * @return List of low stock inventories
     */
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold")
    List<Inventory> findLowStockItems();

    /**
     * Finds all out-of-stock inventory records.
     * Returns items where quantity is zero.
     * 
     * @return List of out-of-stock inventories
     */
    @Query("SELECT i FROM Inventory i WHERE i.quantity = 0")
    List<Inventory> findOutOfStockItems();

    /**
     * Finds all in-stock inventory records.
     * Returns items where quantity is greater than zero.
     * 
     * @return List of in-stock inventories
     */
    @Query("SELECT i FROM Inventory i WHERE i.quantity > 0")
    List<Inventory> findInStockItems();

    /**
     * Finds inventory items by quantity range.
     * 
     * @param minQuantity minimum quantity
     * @param maxQuantity maximum quantity
     * @return List of inventories within the range
     */
    @Query("SELECT i FROM Inventory i WHERE i.quantity BETWEEN :minQuantity AND :maxQuantity")
    List<Inventory> findByQuantityRange(@Param("minQuantity") Integer minQuantity, 
                                       @Param("maxQuantity") Integer maxQuantity);

    /**
     * Finds all inventory records ordered by quantity ascending.
     * Useful for identifying lowest stock items first.
     * 
     * @return List of inventories ordered by quantity
     */
    List<Inventory> findAllByOrderByQuantityAsc();

    /**
     * Counts total inventory records with low stock.
     * 
     * @return count of low stock items
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantity <= i.lowStockThreshold")
    long countLowStockItems();

    /**
     * Counts total inventory records that are out of stock.
     * 
     * @return count of out-of-stock items
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantity = 0")
    long countOutOfStockItems();

    /**
     * Finds inventory by book IDs (bulk operation).
     * 
     * @param bookIds list of book IDs
     * @return List of inventories for the specified books
     */
    @Query("SELECT i FROM Inventory i WHERE i.bookId IN :bookIds")
    List<Inventory> findByBookIdIn(@Param("bookIds") List<Long> bookIds);
}
