package com.book.management.order.exception;

import com.book.management.book.exception.BookNotFoundException;
import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.order.dto.responsedto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for the Order service, using @ControllerAdvice.
 * Catches custom domain exceptions and maps them to appropriate HTTP status codes.
 * Ensures a consistent error response format (ErrorResponseDTO).
 *
 * NOTE: Aligned to handle orchestration exceptions (BookNotFound, InsufficientStock).
 */
@ControllerAdvice
@Slf4j
public class GlobalOrderExceptionHandler extends ResponseEntityExceptionHandler {

    // --- 404 NOT FOUND Handlers ---

    /**
     * Handles 404 Not Found for missing orders (internal) or missing books (external service).
     * Catches OrderNotFoundException and BookNotFoundException.
     */
    @ExceptionHandler({OrderNotFoundException.class, BookNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleNotFoundExceptions(Exception ex, WebRequest request) {
        log.warn("Not Found Error: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    // --- 400 BAD REQUEST Handlers (Validation and Business Rules) ---

    /**
     * Handles 400 Bad Request for validation errors or invalid business transitions.
     * Catches OrderInvalidStatusTransitionException, OrderCancellationNotAllowedException,
     * and IllegalArgumentException (for generic validation errors).
     */
    @ExceptionHandler({OrderInvalidStatusTransitionException.class, OrderCancellationNotAllowedException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequestExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        log.warn("Bad Request (Validation/Business Rule): {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                status.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, status);
    }

    // --- 409 CONFLICT Handlers (Business Conflict/Resource State) ---

    /**
     * Handles 409 Conflict for resource state issues that prevent the order from being placed.
     * Catches InsufficientStockException (from Inventory Service).
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictExceptions(InsufficientStockException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409
        log.warn("Conflict (Insufficient Stock): {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                status.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, status);
    }

    // --- 500 INTERNAL SERVER ERROR Handlers ---

    /**
     * Handles internal service failures during order placement (e.g., database failure,
     * unexpected internal service runtime error that failed rollback).
     * Catches OrderNotPlacedException.
     */
    @ExceptionHandler(OrderNotPlacedException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotPlacedException(OrderNotPlacedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        log.error("Order Placement Internal Service Failure: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO(
                status.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, status);
    }
}