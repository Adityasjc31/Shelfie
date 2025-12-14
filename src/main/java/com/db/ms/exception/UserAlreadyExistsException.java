// ============================================================================
// FILE: src/main/java/com/bookstore/user/exception/UserAlreadyExistsException.java
// ============================================================================
package com.db.ms.exception;

/**
 * Exception thrown when attempting to register a user with an email that already exists.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }
}