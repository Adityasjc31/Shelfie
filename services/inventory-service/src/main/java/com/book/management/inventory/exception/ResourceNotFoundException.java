package com.book.management.inventory.exception;

/**
 * Exception thrown when a resource is not found in a downstream service.
 * Used for FeignClient inter-service communication when the API returns 404.
 * 
 * Example: Book not found in book-service during inventory creation.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
public class ResourceNotFoundException extends RuntimeException {

    private final int httpStatus;

    public ResourceNotFoundException(String message) {
        super(message);
        this.httpStatus = 404;
    }

    public ResourceNotFoundException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
