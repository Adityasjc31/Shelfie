// ============================================================================
// FILE: src/main/java/com/bookstore/user/exception/InvalidCredentialsException.java
// ============================================================================
package com.db.ms.exception;

/**
 * Exception thrown when user provides invalid login credentials.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}