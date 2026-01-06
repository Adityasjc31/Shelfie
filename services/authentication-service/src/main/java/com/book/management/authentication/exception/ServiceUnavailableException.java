package com.book.management.authentication.exception;

/**
 * Exception thrown when a dependent service is unavailable.
 * Maps to HTTP 503 Service Unavailable.
 *
 * @author Aditya Srivastava
 * @version 1.0
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
