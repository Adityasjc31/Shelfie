package com.book.management.api_gateway.exception;

/**
 * Custom exception for timeout.
 */
public class TimeoutException extends RuntimeException {
    public TimeoutException(String message) {
        super(message);
    }
}
