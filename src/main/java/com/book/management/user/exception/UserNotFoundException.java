// ============================================================================
// FILE: src/main/java/com/bookstore/user/exception/UserNotFoundException.java
// ============================================================================
package com.book.management.user.exception;

/**
 * Exception thrown when a requested user is not found in the system.
 * 
 * @author Digital Bookstore Team
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