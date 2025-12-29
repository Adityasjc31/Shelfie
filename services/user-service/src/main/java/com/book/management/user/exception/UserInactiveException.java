package com.book.management.user.exception;

/**
 * Exception thrown when attempting to authenticate an inactive user account.
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
public class UserInactiveException extends RuntimeException {
    
    public UserInactiveException() {
        super("User account is inactive. Please contact administrator.");
    }
}