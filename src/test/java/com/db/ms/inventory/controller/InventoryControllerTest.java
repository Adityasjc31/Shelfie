package com.db.ms.inventory.controller;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.book.management.inventory.controller.InventoryController;
import com.book.management.inventory.dto.*;
import com.book.management.inventory.service.InventoryService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for InventoryController.
 * Uses MockMvc for testing REST endpoints with JUnit 5 and Mockito.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-08
 */
@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    private InventoryResponseDTO responseDTO;
    private InventoryCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        responseDTO = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
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
    void testCreateInventory() throws Exception {
        // Arrange
        when(inventoryService.createInventory(any(InventoryCreateDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inventoryId").value(1L))
                .andExpect(jsonPath("$.bookId").value(100L))
                .andExpect(jsonPath("$.quantity").value(50))
                .andExpect(jsonPath("$.lowStockThreshold").value(10));

        verify(inventoryService, times(1)).createInventory(any(InventoryCreateDTO.class));
    }

    @Test
    void testGetInventoryById() throws Exception {
        // Arrange
        when(inventoryService.getInventoryById(1L)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/{inventoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryId").value(1L))
                .andExpect(jsonPath("$.bookId").value(100L))
                .andExpect(jsonPath("$.quantity").value(50));

        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    void testGetInventoryByBookId() throws Exception {
        // Arrange
        when(inventoryService.getInventoryByBookId(100L)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/book/{bookId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(100L))
                .andExpect(jsonPath("$.quantity").value(50));

        verify(inventoryService, times(1)).getInventoryByBookId(100L);
    }

    @Test
    void testGetAllInventory() throws Exception {
        // Arrange
        InventoryResponseDTO responseDTO2 = InventoryResponseDTO.builder()
                .inventoryId(2L)
                .bookId(101L)
                .quantity(75)
                .lowStockThreshold(15)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<InventoryResponseDTO> inventories = Arrays.asList(responseDTO, responseDTO2);
        when(inventoryService.getAllInventory()).thenReturn(inventories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].inventoryId").value(1L))
                .andExpect(jsonPath("$[1].inventoryId").value(2L));

        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void testUpdateInventoryQuantity() throws Exception {
        // Arrange
        InventoryUpdateDTO updateDTO = InventoryUpdateDTO.builder()
                .quantity(75)
                .build();

        InventoryResponseDTO updatedResponse = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(75)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryService.updateInventoryQuantity(anyLong(), any(InventoryUpdateDTO.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/inventory/{inventoryId}/quantity", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(75));

        verify(inventoryService, times(1))
                .updateInventoryQuantity(anyLong(), any(InventoryUpdateDTO.class));
    }

    @Test
    void testAdjustInventoryQuantity() throws Exception {
        // Arrange
        InventoryAdjustmentDTO adjustmentDTO = InventoryAdjustmentDTO.builder()
                .adjustmentQuantity(10)
                .reason("Restock from supplier")
                .build();

        InventoryResponseDTO adjustedResponse = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(60)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryService.adjustInventoryQuantity(anyLong(), any(InventoryAdjustmentDTO.class)))
                .thenReturn(adjustedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/inventory/{inventoryId}/adjust", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adjustmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(60));

        verify(inventoryService, times(1))
                .adjustInventoryQuantity(anyLong(), any(InventoryAdjustmentDTO.class));
    }

    @Test
    void testReduceInventory() throws Exception {
        // Arrange
        InventoryResponseDTO reducedResponse = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(30)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryService.reduceInventory(100L, 20)).thenReturn(reducedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/inventory/book/{bookId}/reduce", 100L)
                        .param("quantity", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(30));

        verify(inventoryService, times(1)).reduceInventory(100L, 20);
    }

    @Test
    void testRestockInventory() throws Exception {
        // Arrange
        InventoryResponseDTO restockedResponse = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(80)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryService.restockInventory(100L, 30)).thenReturn(restockedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/inventory/book/{bookId}/restock", 100L)
                        .param("quantity", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(80));

        verify(inventoryService, times(1)).restockInventory(100L, 30);
    }

    @Test
    void testCheckAvailability() throws Exception {
        // Arrange
        when(inventoryService.checkAvailability(100L, 30)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/book/{bookId}/availability", 100L)
                        .param("quantity", "30"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inventoryService, times(1)).checkAvailability(100L, 30);
    }

    @Test
    void testGetLowStockItems() throws Exception {
        // Arrange
        LowStockAlertDTO alert1 = LowStockAlertDTO.builder()
                .inventoryId(1L)
                .bookId(103L)
                .currentQuantity(5)
                .lowStockThreshold(10)
                .quantityNeeded(5)
                .alertLevel("WARNING")
                .build();

        LowStockAlertDTO alert2 = LowStockAlertDTO.builder()
                .inventoryId(2L)
                .bookId(106L)
                .currentQuantity(8)
                .lowStockThreshold(10)
                .quantityNeeded(2)
                .alertLevel("WARNING")
                .build();

        List<LowStockAlertDTO> alerts = Arrays.asList(alert1, alert2);
        when(inventoryService.getLowStockItems()).thenReturn(alerts);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/alerts/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].alertLevel").value("WARNING"))
                .andExpect(jsonPath("$[0].currentQuantity").value(5))
                .andExpect(jsonPath("$[1].currentQuantity").value(8));

        verify(inventoryService, times(1)).getLowStockItems();
    }

    @Test
    void testGetOutOfStockItems() throws Exception {
        // Arrange
        InventoryResponseDTO outOfStock1 = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(104L)
                .quantity(0)
                .lowStockThreshold(10)
                .isLowStock(true)
                .isOutOfStock(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<InventoryResponseDTO> outOfStockItems = Arrays.asList(outOfStock1);
        when(inventoryService.getOutOfStockItems()).thenReturn(outOfStockItems);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/status/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(0))
                .andExpect(jsonPath("$[0].isOutOfStock").value(true));

        verify(inventoryService, times(1)).getOutOfStockItems();
    }

    @Test
    void testGetInStockItems() throws Exception {
        // Arrange
        InventoryResponseDTO inStock1 = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(101L)
                .quantity(50)
                .lowStockThreshold(10)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        InventoryResponseDTO inStock2 = InventoryResponseDTO.builder()
                .inventoryId(2L)
                .bookId(102L)
                .quantity(75)
                .lowStockThreshold(15)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<InventoryResponseDTO> inStockItems = Arrays.asList(inStock1, inStock2);
        when(inventoryService.getInStockItems()).thenReturn(inStockItems);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/status/in-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].quantity").value(50))
                .andExpect(jsonPath("$[1].quantity").value(75));

        verify(inventoryService, times(1)).getInStockItems();
    }

    @Test
    void testUpdateLowStockThreshold() throws Exception {
        // Arrange
        InventoryResponseDTO updatedResponse = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(100L)
                .quantity(50)
                .lowStockThreshold(15)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryService.updateLowStockThreshold(1L, 15)).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/inventory/{inventoryId}/threshold", 1L)
                        .param("threshold", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowStockThreshold").value(15));

        verify(inventoryService, times(1)).updateLowStockThreshold(1L, 15);
    }

    @Test
    void testDeleteInventory() throws Exception {
        // Arrange
        doNothing().when(inventoryService).deleteInventory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/inventory/{inventoryId}", 1L))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteInventory(1L);
    }
}