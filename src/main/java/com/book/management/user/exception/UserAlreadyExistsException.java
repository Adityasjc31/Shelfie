package com.book.management.user.exception;

/**
 * Exception thrown when attempting to register a user with an email that already exists.
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }
}