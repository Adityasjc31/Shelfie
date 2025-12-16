package com.db.ms.order.controller;

import com.db.ms.book.exception.BookNotFoundException;
import com.db.ms.order.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.order.dto.responsedto.OrderResponseDTO;
import com.db.ms.order.enums.OrderEnum;
import com.db.ms.order.exception.OrderCancellationNotAllowedException;
import com.db.ms.order.exception.OrderNotFoundException;
import com.db.ms.order.exception.OrderNotPlacedException;
import com.db.ms.order.service.OrderService;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the refactored OrderController.
 * This test suite focuses on validating the Controller's interaction with the
 * OrderService and correct HTTP status code mapping for success and failure cases.
 *
 * It uses the clean architecture: Controller -> OrderService(Orchestration).
 *
 * @author Rehan Ashraf
 * @version 2.0 (Aligned with refactored Controller/Service)
 * @since 2024-12-15
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Only mock the direct dependency: OrderService
    @MockitoBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PlaceOrderRequestDTO validPlaceOrderRequest;
    private OrderResponseDTO successfulOrderResponse;

    @BeforeEach
    void setUp() {
        Map<Long, Integer> bookOrder = Map.of(101L, 2, 102L, 1);

        validPlaceOrderRequest = new PlaceOrderRequestDTO();
        validPlaceOrderRequest.setUserId(99L);
        validPlaceOrderRequest.setBookOrder(bookOrder);

        successfulOrderResponse = OrderResponseDTO.builder()
                .orderId(1L)
                .userId(99L)
                .bookIds(List.of(101L, 102L))
                .orderDateTime(LocalDateTime.now())
                .orderTotalAmount(1047.5)
                .orderStatus(OrderEnum.PENDING)
                .build();
    }

    // --------------------------------------------------
    // POST /api/v1/order/place (Place Order)
    // --------------------------------------------------

    /**
     * Success path: Order placed successfully via service. Returns 201 CREATED.
     */
    @Test
    void placeOrder_Success_Returns201CreatedAndOrderDTO() throws Exception {
        when(orderService.placeOrder(any(PlaceOrderRequestDTO.class))).thenReturn(successfulOrderResponse);

        mockMvc.perform(post("/api/v1/order/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPlaceOrderRequest)))
                .andExpect(status().isCreated()) // Expect 201 CREATED
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(99L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));

        verify(orderService, times(1)).placeOrder(any(PlaceOrderRequestDTO.class));
    }

    /**
     * Failure path: Invalid input (e.g., userId=0) causes IllegalArgumentException. Returns 400 BAD REQUEST.
     */
    @Test
    void placeOrder_InvalidInput_Returns400BadRequest() throws Exception {
        validPlaceOrderRequest.setUserId(0L); // Bad input for the service validation

        when(orderService.placeOrder(any(PlaceOrderRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid request or userId provided."));

        mockMvc.perform(post("/api/v1/order/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPlaceOrderRequest)))
                .andExpect(status().isBadRequest()); // Expect 400 BAD REQUEST

        verify(orderService, times(1)).placeOrder(any(PlaceOrderRequestDTO.class));
    }

    /**
     * Failure path: External service failure (BookNotFoundException). Returns 404 NOT FOUND.
     * Note: This mapping depends on a global exception handler, but we verify service invocation.
     */
    @Test
    void placeOrder_BookNotFound_Returns404NotFound() throws Exception {
        when(orderService.placeOrder(any(PlaceOrderRequestDTO.class)))
                .thenThrow(new BookNotFoundException("Book 101 not found."));

        mockMvc.perform(post("/api/v1/order/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPlaceOrderRequest)))
                .andExpect(status().isNotFound()); // Expect 404 NOT FOUND (Common mapping for resource not found)
    }

    /**
     * Failure path: Stock/Persistence failure (OrderNotPlacedException). Returns 409 CONFLICT or 400 BAD REQUEST.
     * We map OrderNotPlacedException to 409 CONFLICT (Resource state mismatch/failure to process).
     */
    @Test
    void placeOrder_StockFailure_Returns409Conflict() throws Exception {
        when(orderService.placeOrder(any(PlaceOrderRequestDTO.class)))
                .thenThrow(new OrderNotPlacedException("Insufficient stock available."));

        mockMvc.perform(post("/api/v1/order/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPlaceOrderRequest)))
                .andExpect(status().isConflict()); // Expect 409 CONFLICT (Global handler mapping)
    }

    // --------------------------------------------------
    // GET /api/v1/order/getById/{orderId}
    // --------------------------------------------------

    @Test
    void getOrderById_Found_Returns200Ok() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(successfulOrderResponse));

        mockMvc.perform(get("/api/v1/order/getById/{orderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_NotFound_Returns404NotFound() throws Exception {
        when(orderService.getOrderById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/order/getById/{orderId}", 99L))
                .andExpect(status().isNotFound()); // Controller logic handles Optional.empty() to 404

        verify(orderService, times(1)).getOrderById(99L);
    }

    // --------------------------------------------------
    // DELETE /api/v1/order/{orderId} (Cancel Order)
    // --------------------------------------------------

    @Test
    void cancelOrder_Success_Returns204NoContent() throws Exception {
        doNothing().when(orderService).cancelOrder(1L);

        mockMvc.perform(delete("/api/v1/order/{orderId}", 1L))
                .andExpect(status().isNoContent()); // Expect 204 NO CONTENT

        verify(orderService, times(1)).cancelOrder(1L);
    }

    @Test
    void cancelOrder_OrderNotFound_Returns404NotFound() throws Exception {
        doThrow(new OrderNotFoundException("Order 99 not found.")).when(orderService).cancelOrder(99L);

        mockMvc.perform(delete("/api/v1/order/{orderId}", 99L))
                .andExpect(status().isNotFound()); // Expect 404 (Global handler mapping)

        verify(orderService, times(1)).cancelOrder(99L);
    }

    @Test
    void cancelOrder_NotAllowedStatus_Returns409Conflict() throws Exception {
        doThrow(new OrderCancellationNotAllowedException("Order is already shipped."))
                .when(orderService).cancelOrder(1L);

        mockMvc.perform(delete("/api/v1/order/{orderId}", 1L))
                .andExpect(status().isConflict()); // Expect 409 (Global handler mapping for business conflicts)

        verify(orderService, times(1)).cancelOrder(1L);
    }
}