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
        return buildResponse(HttpStatus.BAD_REQUEST, "PLACEMENT_FAILED", ex.getMessage(), request);
    }

    /**
     * Handles status transition errors.
     * Uses 409 CONFLICT because the update conflicts with the existing order state.
     */
    @ExceptionHandler(OrderInvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTransition(
            OrderInvalidStatusTransitionException ex, HttpServletRequest request) {

        log.error("Transition Conflict: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION", ex.getMessage(), request);
    }

    /**
     * Handles cancellation failures.
     * Uses 409 CONFLICT because you cannot cancel an order that is already in transit/delivered.
     */
    @ExceptionHandler(OrderCancellationNotAllowedException.class)
    public ResponseEntity<ErrorResponseDTO> handleCancellationDenied(
            OrderCancellationNotAllowedException ex, HttpServletRequest request) {

        log.error("Cancellation Conflict: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, "CANCELLATION_DENIED", ex.getMessage(), request);
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