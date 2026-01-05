package com.book.management.order.exception;

import com.book.management.order.dto.responsedto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Centralized Exception Handling for the Order Service.
 * Manages business logic errors and cross-service communication failures.
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-07
 */
@RestControllerAdvice
@Slf4j
public class GlobalOrderExceptionHandler {

    /**
     * Handles business logic failures during order placement (e.g., Inventory/Catalog issues).
     */
    @ExceptionHandler(OrderNotPlacedException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotPlaced(
            OrderNotPlacedException ex, HttpServletRequest request) {

        log.error("Order Placement Blocked: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, "ORDER_PLACEMENT_FAILED", ex.getMessage(), request);
    }


    /**
     * Handles invalid order status transitions for /changeOrderStatus.

     * Uses 422 UNPROCESSABLE_CONTENT because the request body is syntactically valid,
     * but the requested transition is semantically invalid given the current order state
     * (i.e., it violates the domain/state machine rules).

     * Example: Attempting PENDING → DELIVERED directly, or any transition not allowed
     * by the configured workflow.

     * Note: 409 CONFLICT can also be used for state conflicts; here we prefer 422 to
     * explicitly signal unprocessable content due to business rule violations.
     */
    @ExceptionHandler(OrderInvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTransition(
            OrderInvalidStatusTransitionException ex, HttpServletRequest request) {

        log.error("Transition Unprocessable: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, "INVALID_ORDER_STATUS_TRANSITION", ex.getMessage(), request);
    }



    /**
     * Handles order cancellation failures for /cancelOrder.

     * Uses 409 CONFLICT because the request is valid, but the operation cannot be
     * completed due to the current state of the resource (business rule violation).

     * Example: Attempting to cancel an order that is already delivered,
     * where cancellation is no longer allowed.

     * Note: 422 UNPROCESSABLE_CONTENT is typically used for invalid payload semantics;
     * here we use 409 to indicate a state conflict preventing the requested action.
     */
    @ExceptionHandler(OrderCancellationNotAllowedException.class)
    public ResponseEntity<ErrorResponseDTO> handleCancellationDenied(
            OrderCancellationNotAllowedException ex, HttpServletRequest request) {

        log.error("Cancellation Conflict: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, "ORDER_CANCELLATION_DENIED", ex.getMessage(), request);
    }


    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotFound(
            OrderNotFoundException ex, HttpServletRequest request) {

        log.error("Resource Missing: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage(), request);
    }

    /**
     * Standardized builder for ErrorResponseDTO.
     */
    private ResponseEntity<ErrorResponseDTO> buildResponse(
            HttpStatus status, String errorCode, String message, HttpServletRequest request) {

        ErrorResponseDTO errorBody = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorCode) // Using specific error codes instead of generic labels
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorBody, status);
    }
}