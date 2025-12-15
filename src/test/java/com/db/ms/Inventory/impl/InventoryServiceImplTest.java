<<<<<<< HEAD:src/test/java/com/db/ms/Inventory/impl/InventoryServiceImplTest.java
package com.db.ms.inventory.impl;
=======
package com.db.ms.service.impl;
>>>>>>> origin/InventoryManagementModule:src/test/java/com/db/ms/service/impl/InventoryServiceImplTest.java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

<<<<<<< HEAD:src/test/java/com/db/ms/Inventory/impl/InventoryServiceImplTest.java
import com.db.ms.Inventory.dto.*;
import com.db.ms.Inventory.exception.*;
import com.db.ms.Inventory.model.Inventory;
import com.db.ms.Inventory.repository.InventoryRepository;
import com.db.ms.Inventory.service.impl.InventoryServiceImpl;
=======
import com.db.ms.inventory.dto.*;
import com.db.ms.inventory.exception.*;
import com.db.ms.inventory.model.Inventory;
import com.db.ms.inventory.repository.InventoryRepository;
import com.db.ms.inventory.service.impl.InventoryServiceImpl;
>>>>>>> origin/InventoryManagementModule:src/test/java/com/db/ms/service/impl/InventoryServiceImplTest.java

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryServiceImpl.
 * Uses JUnit 5 and Mockito for testing.
 * Tests work with both in-memory repository and future JPA implementation.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-08
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory testInventory;
    private InventoryCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        testInventory = Inventory.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDTO = InventoryCreateDTO.builder()
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .build();
    }

    @Test
    void createInventory_Success() {
        // Arrange
        when(inventoryRepository.existsByBookId(100L)).thenReturn(false);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponseDTO result = inventoryService.createInventory(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getBookId());
        assertEquals(50, result.getQuantity());
        verify(inventoryRepository, times(1)).existsByBookId(100L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void createInventory_ThrowsExceptionWhenAlreadyExists() {
        // Arrange
        when(inventoryRepository.existsByBookId(100L)).thenReturn(true);

        // Act & Assert
        assertThrows(InventoryAlreadyExistsException.class, 
                () -> inventoryService.createInventory(createDTO));
        verify(inventoryRepository, times(1)).existsByBookId(100L);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void getInventoryById_Success() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));

        // Act
        InventoryResponseDTO result = inventoryService.getInventoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getInventoryId());
        assertEquals(100L, result.getBookId());
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    void getInventoryById_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InventoryNotFoundException.class, 
                () -> inventoryService.getInventoryById(1L));
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    void getInventoryByBookId_Success() {
        // Arrange
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(testInventory));

        // Act
        InventoryResponseDTO result = inventoryService.getInventoryByBookId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getBookId());
        verify(inventoryRepository, times(1)).findByBookId(100L);
    }

    @Test
    void getAllInventory_Success() {
        // Arrange
        List<Inventory> inventories = Arrays.asList(testInventory, testInventory);
        when(inventoryRepository.findAll()).thenReturn(inventories);

        // Act
        List<InventoryResponseDTO> result = inventoryService.getAllInventory();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void updateInventoryQuantity_Success() {
        // Arrange
        InventoryUpdateDTO updateDTO = InventoryUpdateDTO.builder()
                .quantity(75)
                .build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponseDTO result = inventoryService.updateInventoryQuantity(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void adjustInventoryQuantity_Success() {
        // Arrange
        InventoryAdjustmentDTO adjustmentDTO = InventoryAdjustmentDTO.builder()
                .adjustmentQuantity(10)
                .reason("Restock")
                .build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponseDTO result = inventoryService.adjustInventoryQuantity(1L, adjustmentDTO);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void adjustInventoryQuantity_ThrowsExceptionForNegativeResult() {
        // Arrange
        InventoryAdjustmentDTO adjustmentDTO = InventoryAdjustmentDTO.builder()
                .adjustmentQuantity(-100)
                .reason("Invalid adjustment")
                .build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));

        // Act & Assert
        assertThrows(InvalidInventoryOperationException.class, 
                () -> inventoryService.adjustInventoryQuantity(1L, adjustmentDTO));
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void reduceInventory_Success() {
        // Arrange
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponseDTO result = inventoryService.reduceInventory(100L, 20);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findByBookId(100L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void reduceInventory_ThrowsExceptionForInsufficientStock() {
        // Arrange
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(testInventory));

        // Act & Assert
        assertThrows(InsufficientStockException.class, 
                () -> inventoryService.reduceInventory(100L, 100));
        verify(inventoryRepository, times(1)).findByBookId(100L);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void restockInventory_Success() {
        // Arrange
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponseDTO result = inventoryService.restockInventory(100L, 30);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findByBookId(100L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void checkAvailability_ReturnsTrue() {
        // Arrange
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(testInventory));

        // Act
        boolean result = inventoryService.checkAvailability(100L, 30);

        // Assert
        assertTrue(result);
        verify(inventoryRepository, times(1)).findByBookId(100L);
    }

    @Test
    void checkAvailability_ReturnsFalse() {
        // Arrange
        when(inventoryRepository.findByBookId(100L)).thenReturn(Optional.of(testInventory));

        // Act
        boolean result = inventoryService.checkAvailability(100L, 100);

        // Assert
        assertFalse(result);
        verify(inventoryRepository, times(1)).findByBookId(100L);
    }

    @Test
    void getLowStockItems_Success() {
        // Arrange
        Inventory lowStockItem = Inventory.builder()
                .inventoryId(2L)
                .bookId(200L)
                .quantity(5)
                .lowStockThreshold(10)
                .build();
        when(inventoryRepository.findLowStockItems()).thenReturn(Arrays.asList(lowStockItem));

        // Act
        List<LowStockAlertDTO> result = inventoryService.getLowStockItems();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WARNING", result.get(0).getAlertLevel());
        verify(inventoryRepository, times(1)).findLowStockItems();
    }

    @Test
    void getOutOfStockItems_Success() {
        // Arrange
        Inventory outOfStockItem = Inventory.builder()
                .inventoryId(3L)
                .bookId(300L)
                .quantity(0)
                .lowStockThreshold(10)
                .build();
        when(inventoryRepository.findOutOfStockItems()).thenReturn(Arrays.asList(outOfStockItem));

        // Act
        List<InventoryResponseDTO> result = inventoryService.getOutOfStockItems();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOutOfStock());
        verify(inventoryRepository, times(1)).findOutOfStockItems();
    }

    @Test
    void updateLowStockThreshold_Success() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponseDTO result = inventoryService.updateLowStockThreshold(1L, 15);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void deleteInventory_Success() {
        // Arrange
        when(inventoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventoryRepository).deleteById(1L);

        // Act
        inventoryService.deleteInventory(1L);

        // Assert
        verify(inventoryRepository, times(1)).existsById(1L);
        verify(inventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteInventory_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(inventoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(InventoryNotFoundException.class, 
                () -> inventoryService.deleteInventory(1L));
        verify(inventoryRepository, times(1)).existsById(1L);
        verify(inventoryRepository, never()).deleteById(anyLong());
    }
}