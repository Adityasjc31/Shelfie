package com.book.management.inventory.service;

import java.util.List;
import java.util.Map;

import com.book.management.inventory.dto.*;
import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.inventory.exception.InvalidInventoryOperationException;
import com.book.management.inventory.exception.InventoryAlreadyExistsException;
import com.book.management.inventory.exception.InventoryNotFoundException;

/**
 * Service interface defining business logic operations for Inventory
 * Management.
 * Follows the Interface Segregation Principle (ISP) from SOLID principles.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-078
 */
public interface InventoryService {

    /**
     * Creates a new inventory record for a book.
     *
     * @param createDTO the inventory creation data
     * @return the created inventory response
     * @throws InventoryAlreadyExistsException if inventory already exists for the
     *                                         book
     */
    InventoryResponseDTO createInventory(InventoryCreateDTO createDTO);

    /**
     * Retrieves an inventory record by its ID.
     *
     * @param inventoryId the inventory ID
     * @return the inventory response
     * @throws InventoryNotFoundException if inventory not found
     */
    InventoryResponseDTO getInventoryById(Long inventoryId);

    /**
     * Retrieves an inventory record by book ID.
     *
     * @param bookId the book ID
     * @return the inventory response
     * @throws InventoryNotFoundException if inventory not found
     */
    InventoryResponseDTO getInventoryByBookId(Long bookId);

    /**
     * Retrieves all inventory records.
     *
     * @return list of all inventory records
     */
    List<InventoryResponseDTO> getAllInventory();

    /**
     * Updates the quantity of an inventory record.
     *
     * @param inventoryId the inventory ID
     * @param updateDTO   the update data
     * @return the updated inventory response
     * @throws InventoryNotFoundException if inventory not found
     */
    InventoryResponseDTO updateInventoryQuantity(Long inventoryId, InventoryUpdateDTO updateDTO);

    /**
     * Adjusts inventory quantity by a specified amount (increment or decrement).
     *
     * @param inventoryId   the inventory ID
     * @param adjustmentDTO the adjustment data
     * @return the updated inventory response
     * @throws InventoryNotFoundException         if inventory not found
     * @throws InvalidInventoryOperationException if adjustment would result in
     *                                            negative quantity
     */
    InventoryResponseDTO adjustInventoryQuantity(Long inventoryId, InventoryAdjustmentDTO adjustmentDTO);

    /**
     * Reduces inventory quantity for a book purchase.
     *
     * @param bookId   the book ID
     * @param quantity the quantity to reduce
     * @return the updated inventory response
     * @throws InventoryNotFoundException if inventory not found
     * @throws InsufficientStockException if insufficient stock available
     */
    InventoryResponseDTO reduceInventory(Long bookId, Integer quantity);

    /**
     * Increases inventory quantity for a book restock.
     *
     * @param bookId   the book ID
     * @param quantity the quantity to add
     * @return the updated inventory response
     * @throws InventoryNotFoundException if inventory not found
     */
    InventoryResponseDTO restockInventory(Long bookId, Integer quantity);

    /**
     * Checks if a book is available in sufficient quantity.
     *
     * @param bookId   the book ID
     * @param quantity the required quantity
     * @return true if available, false otherwise
     */
    boolean checkAvailability(Long bookId, Integer quantity);

    /**
     * Retrieves all inventory records with low stock levels.
     *
     * @return list of low stock inventory records
     */
    List<LowStockAlertDTO> getLowStockItems();

    /**
     * Retrieves all out-of-stock inventory records.
     *
     * @return list of out-of-stock inventory records
     */
    List<InventoryResponseDTO> getOutOfStockItems();

    /**
     * Retrieves all in-stock inventory records.
     *
     * @return list of in-stock inventory records
     */
    List<InventoryResponseDTO> getInStockItems();

    /**
     * Updates the low stock threshold for an inventory record.
     *
     * @param inventoryId  the inventory ID
     * @param newThreshold the new threshold value
     * @return the updated inventory response
     * @throws InventoryNotFoundException if inventory not found
     */
    InventoryResponseDTO updateLowStockThreshold(Long inventoryId, Integer newThreshold);

    /**
     * Deletes an inventory record.
     *
     * @param inventoryId the inventory ID
     * @throws InventoryNotFoundException if inventory not found
     */
    void deleteInventory(Long inventoryId);

    /**
     * Deletes an inventory record by book ID.
     *
     * @param bookId the book ID
     * @throws InventoryNotFoundException if inventory not found for the book
     */
    void deleteInventoryByBookId(Long bookId);

    /**
     * Checks stock availability for multiple books.
     *
     * @param bookQuantities map of bookId to required quantity
     * @return map of bookId to availability status
     */
    Map<Long, Boolean> checkBulkAvailability(Map<Long, Integer> bookQuantities);

    /**
     * Reduces inventory for multiple books (bulk deduction).
     * Only deducts if ALL books are available in required quantities.
     * Throws exception if any book has insufficient stock.
     *
     * @param bookQuantities map of bookId to quantity to deduct
     * @throws InsufficientStockException if any book has insufficient stock
     */
    void reduceBulkInventory(Map<Long, Integer> bookQuantities);
}