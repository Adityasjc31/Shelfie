package com.book.management.api_gateway.exception;

/**
 * Custom exception for unauthorized access.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
