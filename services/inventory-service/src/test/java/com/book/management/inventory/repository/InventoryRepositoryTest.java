package com.book.management.inventory.repository;

import com.book.management.inventory.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for InventoryRepository.
 * Uses @DataJpaTest for in-memory database testing.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();

        testInventory = Inventory.builder()
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .build();
        testInventory = inventoryRepository.save(testInventory);
    }

    @Test
    void findByBookId_ReturnsInventory_WhenExists() {
        // Act
        Optional<Inventory> result = inventoryRepository.findByBookId(100L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getBookId());
        assertEquals(50, result.get().getQuantity());
    }

    @Test
    void findByBookId_ReturnsEmpty_WhenNotExists() {
        // Act
        Optional<Inventory> result = inventoryRepository.findByBookId(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void existsByBookId_ReturnsTrue_WhenExists() {
        // Act
        boolean result = inventoryRepository.existsByBookId(100L);

        // Assert
        assertTrue(result);
    }

    @Test
    void existsByBookId_ReturnsFalse_WhenNotExists() {
        // Act
        boolean result = inventoryRepository.existsByBookId(999L);

        // Assert
        assertFalse(result);
    }

    @Test
    void findLowStockItems_ReturnsItemsBelowThreshold() {
        // Arrange
        Inventory lowStockInventory = Inventory.builder()
                .bookId(101L)
                .quantity(5)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(lowStockInventory);

        // Act
        List<Inventory> result = inventoryRepository.findLowStockItems();

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(inv -> inv.getQuantity() <= inv.getLowStockThreshold()));
    }

    @Test
    void findOutOfStockItems_ReturnsZeroQuantityItems() {
        // Arrange
        Inventory outOfStockInventory = Inventory.builder()
                .bookId(102L)
                .quantity(0)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(outOfStockInventory);

        // Act
        List<Inventory> result = inventoryRepository.findOutOfStockItems();

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(inv -> inv.getQuantity() == 0));
    }

    @Test
    void findInStockItems_ReturnsPositiveQuantityItems() {
        // Act
        List<Inventory> result = inventoryRepository.findInStockItems();

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(inv -> inv.getQuantity() > 0));
    }

    @Test
    void findByBookIdIn_ReturnsMatchingInventories() {
        // Arrange
        Inventory inv2 = Inventory.builder()
                .bookId(101L)
                .quantity(30)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(inv2);

        // Act
        List<Inventory> result = inventoryRepository.findByBookIdIn(Arrays.asList(100L, 101L, 999L));

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void findByQuantityRange_ReturnsItemsInRange() {
        // Arrange
        Inventory inv2 = Inventory.builder()
                .bookId(103L)
                .quantity(25)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(inv2);

        // Act
        List<Inventory> result = inventoryRepository.findByQuantityRange(20, 60);

        // Assert
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(inv -> inv.getQuantity() >= 20 && inv.getQuantity() <= 60));
    }

    @Test
    void findAllByOrderByQuantityAsc_ReturnsOrderedList() {
        // Arrange
        Inventory inv2 = Inventory.builder()
                .bookId(104L)
                .quantity(5)
                .lowStockThreshold(10)
                .build();
        Inventory inv3 = Inventory.builder()
                .bookId(105L)
                .quantity(100)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(inv2);
        inventoryRepository.save(inv3);

        // Act
        List<Inventory> result = inventoryRepository.findAllByOrderByQuantityAsc();

        // Assert
        assertTrue(result.size() >= 3);
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i).getQuantity() >= result.get(i - 1).getQuantity());
        }
    }

    @Test
    void countLowStockItems_ReturnsCorrectCount() {
        // Arrange
        Inventory lowStockInv = Inventory.builder()
                .bookId(106L)
                .quantity(5)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(lowStockInv);

        // Act
        long count = inventoryRepository.countLowStockItems();

        // Assert
        assertTrue(count >= 1);
    }

    @Test
    void countOutOfStockItems_ReturnsCorrectCount() {
        // Arrange
        Inventory outOfStockInv = Inventory.builder()
                .bookId(107L)
                .quantity(0)
                .lowStockThreshold(10)
                .build();
        inventoryRepository.save(outOfStockInv);

        // Act
        long count = inventoryRepository.countOutOfStockItems();

        // Assert
        assertTrue(count >= 1);
    }
}
