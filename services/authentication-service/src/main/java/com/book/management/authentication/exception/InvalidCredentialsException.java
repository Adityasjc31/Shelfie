package com.book.management.authentication.exception;

/**
 * Invalid credentials exception.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
