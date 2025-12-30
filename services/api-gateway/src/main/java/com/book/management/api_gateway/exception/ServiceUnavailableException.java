package com.book.management.api_gateway.exception;

/**
 * Custom exception for service unavailable.
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
