package com.book.management.authentication.exception;

/**
 * Session expired exception.
 */
public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException() {
        super("Session has expired. Please login again.");
    }
}
