package com.book.management.authentication.exception;

/**
 * Base authentication exception.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
