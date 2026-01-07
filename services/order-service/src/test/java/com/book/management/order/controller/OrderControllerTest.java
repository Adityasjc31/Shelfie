package com.book.management.order.controller;

import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.dto.requestdto.UpdateOrderStatusRequestDTO;
import com.book.management.order.dto.responsedto.OrderResponseDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.GlobalOrderExceptionHandler;
import com.book.management.order.exception.OrderCancellationNotAllowedException;
import com.book.management.order.exception.OrderInvalidStatusTransitionException;
import com.book.management.order.exception.OrderNotFoundException;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.filter.GatewayAuthenticationFilter;
import com.book.management.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link OrderController}.
 * Tests REST endpoints for order management including placement,
 * retrieval, status updates, cancellation, and soft deletion.
 * Uses MockMvc for HTTP request simulation with mocked service layer.
 *
 * @author Rehan Ashraf
 * @version 2.0
 * @since 2024-12-07
 */
@WebMvcTest(value = OrderController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = GatewayAuthenticationFilter.class))
@Import({GlobalOrderExceptionHandler.class, OrderControllerTest.TestConfig.class})
class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private OrderService orderService;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private PlaceOrderRequestDTO validPlaceOrderRequest;
        private OrderResponseDTO successfulOrderResponse;
        private Map<Long, Integer> bookOrder;

        /**
         * Sets up test fixtures before each test method.
         * Initializes sample request and response DTOs for order operations.
         */
        @BeforeEach
        void setUp() {
                bookOrder = Map.of(101L, 2, 102L, 1);

                validPlaceOrderRequest = new PlaceOrderRequestDTO();
                validPlaceOrderRequest.setUserId(99L);
                validPlaceOrderRequest.setBookOrder(bookOrder);

                successfulOrderResponse = OrderResponseDTO.builder()
                                .orderId(1L)
                                .userId(99L)
                                .bookIds(new ArrayList<>(bookOrder.keySet()))
                                .orderDateTime(LocalDateTime.now())
                                .orderTotalAmount(1047.5)
                                .orderStatus(OrderEnum.PENDING)
                                .build();
        }

        // ==================== POST /api/v1/order/place ====================

        /**
         * Tests successful order placement.
         * Verifies HTTP 201 Created response with order details.
         */
        @Test
        void placeOrder_Success_Returns201Created() throws Exception {
                when(orderService.placeOrder(any(PlaceOrderRequestDTO.class))).thenReturn(successfulOrderResponse);

                mockMvc.perform(post("/api/v1/order/place")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validPlaceOrderRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.orderId").value(1L))
                                .andExpect(jsonPath("$.userId").value(99L))
                                .andExpect(jsonPath("$.orderStatus").value("PENDING"));

                verify(orderService, times(1)).placeOrder(any(PlaceOrderRequestDTO.class));
        }

        /**
         * Tests order placement failure due to service error.
         * Verifies HTTP 400 Bad Request with ORDER_PLACEMENT_FAILED error code.
         */
        @Test
        void placeOrder_ServiceFailure_Returns400BadRequest() throws Exception {
                when(orderService.placeOrder(any(PlaceOrderRequestDTO.class)))
                                .thenThrow(new OrderNotPlacedException("Insufficient stock available."));

                mockMvc.perform(post("/api/v1/order/place")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validPlaceOrderRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("ORDER_PLACEMENT_FAILED"));

                verify(orderService, times(1)).placeOrder(any(PlaceOrderRequestDTO.class));
        }

        // ==================== GET /api/v1/order/getAll ====================

        /**
         * Tests successful retrieval of all orders.
         * Verifies HTTP 200 OK with order list.
         */
        @Test
        void getAllOrders_Success_Returns200Ok() throws Exception {
                when(orderService.getOrderAll()).thenReturn(List.of(successfulOrderResponse));

                mockMvc.perform(get("/api/v1/order/getAll"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].orderId").value(1L));

                verify(orderService, times(1)).getOrderAll();
        }

        /**
         * Tests retrieval when no orders exist.
         * Verifies HTTP 200 OK with empty array.
         */
        @Test
        void getAllOrders_Empty_Returns200Ok() throws Exception {
                when(orderService.getOrderAll()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/order/getAll"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());

                verify(orderService, times(1)).getOrderAll();
        }

        // ==================== GET /api/v1/order/getById/{orderId} ====================

        /**
         * Tests successful order retrieval by ID.
         * Verifies HTTP 200 OK with order details.
         */
        @Test
        void getOrderById_Found_Returns200Ok() throws Exception {
                when(orderService.getOrderById(1L)).thenReturn(Optional.of(successfulOrderResponse));

                mockMvc.perform(get("/api/v1/order/getById/{orderId}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.orderId").value(1L));

                verify(orderService, times(1)).getOrderById(1L);
        }

        /**
         * Tests order retrieval when order does not exist.
         * Verifies HTTP 404 Not Found response.
         */
        @Test
        void getOrderById_NotFound_Returns404NotFound() throws Exception {
                when(orderService.getOrderById(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/order/getById/{orderId}", 99L))
                                .andExpect(status().isNotFound());

                verify(orderService, times(1)).getOrderById(99L);
        }

        // ==================== GET /api/v1/order/status/{status} ====================

        /**
         * Tests successful retrieval of orders by status.
         * Verifies HTTP 200 OK with filtered orders.
         */
        @Test
        void getOrdersByStatus_Success_Returns200Ok() throws Exception {
                when(orderService.getOrdersByStatus(OrderEnum.PENDING)).thenReturn(List.of(successfulOrderResponse));

                mockMvc.perform(get("/api/v1/order/status/{status}", "PENDING"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].orderStatus").value("PENDING"));

                verify(orderService, times(1)).getOrdersByStatus(OrderEnum.PENDING);
        }

        // ==================== PATCH /api/v1/order/update/{orderId}
        // ====================

        /**
         * Tests successful order status update.
         * Verifies HTTP 200 OK with updated order status.
         */
        @Test
        void changeOrderStatus_Success_Returns200Ok() throws Exception {
                UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
                updateRequest.setOrderStatus(OrderEnum.SHIPPED);

                OrderResponseDTO updatedResponse = OrderResponseDTO.builder()
                                .orderId(1L)
                                .userId(99L)
                                .orderStatus(OrderEnum.SHIPPED)
                                .build();

                when(orderService.updateOrderStatus(eq(1L), any(UpdateOrderStatusRequestDTO.class)))
                                .thenReturn(updatedResponse);

                mockMvc.perform(patch("/api/v1/order/update/{orderId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.orderStatus").value("SHIPPED"));

                verify(orderService, times(1)).updateOrderStatus(eq(1L), any(UpdateOrderStatusRequestDTO.class));
        }

        /**
         * Tests invalid status transition attempt.
         * Verifies HTTP 422 Unprocessable Entity with INVALID_ORDER_STATUS_TRANSITION
         * error.
         */
        @Test
        void changeOrderStatus_InvalidTransition_Returns422() throws Exception {
                UpdateOrderStatusRequestDTO updateRequest = new UpdateOrderStatusRequestDTO();
                updateRequest.setOrderStatus(OrderEnum.DELIVERED);

                when(orderService.updateOrderStatus(eq(1L), any(UpdateOrderStatusRequestDTO.class)))
                                .thenThrow(new OrderInvalidStatusTransitionException("Invalid transition"));

                mockMvc.perform(patch("/api/v1/order/update/{orderId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(jsonPath("$.error").value("INVALID_ORDER_STATUS_TRANSITION"));
        }

        // ==================== DELETE /api/v1/order/cancel/{orderId}
        // ====================

        /**
         * Tests successful order cancellation.
         * Verifies HTTP 200 OK with CANCELLED status.
         */
        @Test
        void cancelOrder_Success_Returns200Ok() throws Exception {
                OrderResponseDTO cancelledResponse = OrderResponseDTO.builder()
                                .orderId(1L)
                                .orderStatus(OrderEnum.CANCELLED)
                                .build();

                when(orderService.cancelOrder(1L)).thenReturn(cancelledResponse);

                mockMvc.perform(delete("/api/v1/order/cancel/{orderId}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));

                verify(orderService, times(1)).cancelOrder(1L);
        }

        /**
         * Tests cancellation denial for delivered orders.
         * Verifies HTTP 409 Conflict with ORDER_CANCELLATION_DENIED error.
         */
        @Test
        void cancelOrder_NotAllowed_Returns409Conflict() throws Exception {
                when(orderService.cancelOrder(1L))
                                .thenThrow(new OrderCancellationNotAllowedException("Order already delivered"));

                mockMvc.perform(delete("/api/v1/order/cancel/{orderId}", 1L))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.error").value("ORDER_CANCELLATION_DENIED"));
        }

        /**
         * Tests cancellation of non-existent order.
         * Verifies HTTP 404 Not Found response.
         */
        @Test
        void cancelOrder_NotFound_Returns404() throws Exception {
                when(orderService.cancelOrder(99L))
                                .thenThrow(new OrderNotFoundException("Order not found"));

                mockMvc.perform(delete("/api/v1/order/cancel/{orderId}", 99L))
                                .andExpect(status().isNotFound());
        }

        // ==================== DELETE /api/v1/order/delete/{orderId}
        // ====================

        /**
         * Tests successful soft deletion of an order.
         * Verifies HTTP 204 No Content response.
         */
        @Test
        void deleteOrder_Success_Returns204NoContent() throws Exception {
                doNothing().when(orderService).softDeleteOrder(1L);

                mockMvc.perform(delete("/api/v1/order/delete/{orderId}", 1L))
                                .andExpect(status().isNoContent());

                verify(orderService, times(1)).softDeleteOrder(1L);
        }

        // ==================== DELETE /api/v1/order/deleteByUser/{userId}
        // ====================

        /**
         * Tests successful soft deletion of all orders for a user.
         * Verifies HTTP 204 No Content response.
         */
        @Test
        void deleteUserOrder_Success_Returns204NoContent() throws Exception {
                doNothing().when(orderService).softDeleteUserOrder(100L);

                mockMvc.perform(delete("/api/v1/order/deleteByUser/{userId}", 100L))
                                .andExpect(status().isNoContent());

                verify(orderService, times(1)).softDeleteUserOrder(100L);
        }
}
