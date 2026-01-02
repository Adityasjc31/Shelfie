package com.book.management.inventory.exception;

/**
 * Exception thrown when a downstream service is unavailable (5xx responses).
 * Used for FeignClient inter-service communication errors.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
public class ServiceUnavailableException extends RuntimeException {

    private final int httpStatus;

    public ServiceUnavailableException(String message) {
        super(message);
        this.httpStatus = 503;
    }

    public ServiceUnavailableException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
