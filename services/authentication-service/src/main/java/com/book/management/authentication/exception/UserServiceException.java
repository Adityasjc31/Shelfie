package com.book.management.authentication.exception;

/**
 * Exception thrown when communication with User Service fails.
 *
 * @author Aditya Srivastava
 * @version 1.0
 */
public class UserServiceException extends RuntimeException {

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
