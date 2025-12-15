package com.db.ms.Order.controller;


import com.db.ms.Order.controller.OrderController;
import com.db.ms.Order.controller.OrderPriceStockController;
import com.db.ms.Order.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.Order.dto.responsedto.OrderPriceStockResponseDTO;
import com.db.ms.Order.dto.responsedto.OrderResponseDTO;
import com.db.ms.Order.enums.OrderEnum;
import com.db.ms.Order.service.OrderService;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for /orders/place endpoint using MockMvc.
 * Validates success response and invalid input scenarios via mocked OrderPriceStockController.
 *
 * Notes:
 * - Tests return body type as OrderPriceStockResponseDTO (per controller design).
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    // We call the controller-to-controller dependency per design
    @MockitoBean
    private OrderPriceStockController orderPriceStockController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * /orders/place: success path returns 201 Created and combined price/stock in body.
     */
    @Test
    void placeOrder_Success_Returns201WithPriceStock() throws Exception {
        // Request payload
        Map<Long, Integer> bookOrder = new LinkedHashMap<>();
        bookOrder.put(101L, 2);
        bookOrder.put(102L, 1);

        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO();
        request.setUserId(99L);
        request.setBookOrder(bookOrder);

        // Mock price/stock
        OrderPriceStockResponseDTO priceStock = new OrderPriceStockResponseDTO(
                Map.of(101L, 399.0, 102L, 249.5),
                Map.of(101L, true, 102L, true)
        );
        Mockito.when(orderPriceStockController.computePriceAndStock(any(PlaceOrderRequestDTO.class)))
                .thenReturn(priceStock);

        // Mock orderService placement (controller returns priceStock DTO, not the order)
        OrderResponseDTO order = OrderResponseDTO.builder()
                .orderId(1L)
                .userId(99L)
                .bookIds(java.util.List.of(101L, 102L))
                .orderDateTime(LocalDateTime.now())
                .orderTotalAmount(1047.5)
                .orderStatus(OrderEnum.PENDING)
                .build();
        Mockito.when(orderService.placeOrder(any(PlaceOrderRequestDTO.class), any(OrderPriceStockResponseDTO.class)))
                .thenReturn(order);

        mockMvc.perform(post("/orders/place")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookPrice.101").value(399.0))
                .andExpect(jsonPath("$.bookPrice.102").value(249.5))
                .andExpect(jsonPath("$.bookStock.101").value(true))
                .andExpect(jsonPath("$.bookStock.102").value(true));
    }

    /**
     * /orders/place: invalid price/stock (missing prices or stock failure) yields 400 Bad Request.
     * Simulated by having OrderPriceStockController throw ResponseStatusException(BAD_REQUEST).
     */
    @Test
    void placeOrder_InvalidPriceStock_Returns400() throws Exception {
        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO();
        request.setUserId(99L);
        request.setBookOrder(Map.of(101L, 2));

        Mockito.when(orderPriceStockController.computePriceAndStock(any(PlaceOrderRequestDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock"));

        mockMvc.perform(post("/orders/place")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * /orders/place: invalid payload (empty bookOrder) yields 400 Bad Request.
     * Simulated by having OrderPriceStockController throw ResponseStatusException(BAD_REQUEST).
     */
    @Test
    void placeOrder_EmptyBookOrder_Returns400() throws Exception {
        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO();
        request.setUserId(99L);
        request.setBookOrder(java.util.Collections.emptyMap());

        Mockito.when(orderPriceStockController.computePriceAndStock(any(PlaceOrderRequestDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookOrder must contain at least one item"));

        mockMvc.perform(post("/orders/place")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}


