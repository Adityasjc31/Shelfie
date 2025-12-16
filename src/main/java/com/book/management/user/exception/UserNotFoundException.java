package com.book.management.user.exception;

/**
 * Exception thrown when a requested user is not found in the system.
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }
}