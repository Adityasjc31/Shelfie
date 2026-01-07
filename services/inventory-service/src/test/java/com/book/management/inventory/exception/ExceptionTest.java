package com.book.management.inventory.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exception classes.
 * Tests all constructors and getter methods.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-08
 */
class ExceptionTest {

    // ==================== InsufficientStockException Tests ====================

    @Test
    void insufficientStockException_MessageConstructor() {
        // Act
        InsufficientStockException ex = new InsufficientStockException("Custom message");

        // Assert
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void insufficientStockException_DetailedConstructor() {
        // Act
        InsufficientStockException ex = new InsufficientStockException(100L, 5, 10);

        // Assert
        assertTrue(ex.getMessage().contains("Insufficient stock"));
        assertTrue(ex.getMessage().contains("100"));
        assertTrue(ex.getMessage().contains("5"));
        assertTrue(ex.getMessage().contains("10"));
    }

    // ==================== InventoryNotFoundException Tests ====================

    @Test
    void inventoryNotFoundException_MessageConstructor() {
        // Act
        InventoryNotFoundException ex = new InventoryNotFoundException("Custom message");

        // Assert
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void inventoryNotFoundException_IdConstructor() {
        // Act
        InventoryNotFoundException ex = new InventoryNotFoundException(1L);

        // Assert
        assertTrue(ex.getMessage().contains("Inventory not found"));
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    void inventoryNotFoundException_FieldValueConstructor() {
        // Act
        InventoryNotFoundException ex = new InventoryNotFoundException("bookId", 100L);

        // Assert
        assertTrue(ex.getMessage().contains("bookId"));
        assertTrue(ex.getMessage().contains("100"));
    }

    // ==================== InventoryAlreadyExistsException Tests
    // ====================

    @Test
    void inventoryAlreadyExistsException_MessageConstructor() {
        // Act
        InventoryAlreadyExistsException ex = new InventoryAlreadyExistsException("Custom message");

        // Assert
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void inventoryAlreadyExistsException_BookIdConstructor() {
        // Act
        InventoryAlreadyExistsException ex = new InventoryAlreadyExistsException(100L);

        // Assert
        assertTrue(ex.getMessage().contains("already exists"));
        assertTrue(ex.getMessage().contains("100"));
    }

    // ==================== InvalidInventoryOperationException Tests
    // ====================

    @Test
    void invalidInventoryOperationException_MessageConstructor() {
        // Act
        InvalidInventoryOperationException ex = new InvalidInventoryOperationException("Invalid operation");

        // Assert
        assertEquals("Invalid operation", ex.getMessage());
    }

    // ==================== ResourceNotFoundException Tests ====================

    @Test
    void resourceNotFoundException_MessageConstructor() {
        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Assert
        assertEquals("Resource not found", ex.getMessage());
        assertEquals(404, ex.getHttpStatus());
    }

    @Test
    void resourceNotFoundException_MessageAndStatusConstructor() {
        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found", 404);

        // Assert
        assertEquals("Not found", ex.getMessage());
        assertEquals(404, ex.getHttpStatus());
    }

    // ==================== ServiceUnavailableException Tests ====================

    @Test
    void serviceUnavailableException_MessageConstructor() {
        // Act
        ServiceUnavailableException ex = new ServiceUnavailableException("Service down");

        // Assert
        assertEquals("Service down", ex.getMessage());
        assertEquals(503, ex.getHttpStatus());
    }

    @Test
    void serviceUnavailableException_MessageAndStatusConstructor() {
        // Act
        ServiceUnavailableException ex = new ServiceUnavailableException("Service error", 500);

        // Assert
        assertEquals("Service error", ex.getMessage());
        assertEquals(500, ex.getHttpStatus());
    }
}
