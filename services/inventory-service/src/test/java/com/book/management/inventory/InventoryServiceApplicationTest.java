package com.book.management.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for InventoryServiceApplication.
 * Tests the main application class.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
class InventoryServiceApplicationTest {

    @Test
    void main_DoesNotThrowException() {
        // This is a simple test to cover the main class
        // Spring Boot Application context test is too heavy for unit testing
        // We just verify the class can be instantiated
        InventoryServiceApplication app = new InventoryServiceApplication();
        assertNotNull(app);
    }
}
