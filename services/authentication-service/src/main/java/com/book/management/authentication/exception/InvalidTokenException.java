package com.book.management.authentication.exception;

/**
 * Invalid token exception.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
