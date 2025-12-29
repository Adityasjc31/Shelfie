package com.book.management.user.exception;

/**
 * Exception thrown when user provides invalid login credentials.
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}