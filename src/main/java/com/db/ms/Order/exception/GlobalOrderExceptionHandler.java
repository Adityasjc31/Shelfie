package com.db.ms.Order.exception;

import com.db.ms.Order.dto.responsedto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for the Order service, using @ControllerAdvice.
 * Catches custom domain exceptions and maps them to appropriate HTTP status codes.
 * Ensures a consistent error response format (ErrorResponseDTO).
 */
@ControllerAdvice
@Slf4j
public class GlobalOrderExceptionHandler extends ResponseEntityExceptionHandler {

    // You might need a simple DTO for error responses:
    // This is a placeholder; you should define a proper ErrorResponseDTO class

    /**
     * Handles 404 Not Found exceptions.
     * Catches the custom checked OrderNotFoundException.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request) {
        log.warn("Order Not Found: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    /**
     * Handles 400 Bad Request for invalid transitions or cancellation policies.
     * Catches OrderInvalidStatusTransitionException (for status changes)
     * and OrderCancellationNotAllowedException (for cancel policy violations).
     */
    @ExceptionHandler({OrderInvalidStatusTransitionException.class, OrderCancellationNotAllowedException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequestExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        log.warn("Bad Request (Validation/Business Rule): {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(status.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, status);
    }

    /**
     * Handles service failures during order placement.
     * Catches OrderNotPlacedException.
     */
    @ExceptionHandler(OrderNotPlacedException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotPlacedException(OrderNotPlacedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        log.error("Order Placement Service Failure: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO(status.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, status);
    }

    /**
     * Handles exceptions thrown by the OrderPriceStockController (like 400 or 502)
     * which uses Spring's built-in ResponseStatusException.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        log.warn("External Service/Validation Error ({}): {}", status, ex.getReason());
        ErrorResponseDTO error = new ErrorResponseDTO(status.value(), ex.getReason(), request.getDescription(false));
        return new ResponseEntity<>(error, status);
    }
}