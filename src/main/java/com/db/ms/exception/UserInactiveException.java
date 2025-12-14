// ============================================================================
// FILE: src/main/java/com/bookstore/user/exception/UserInactiveException.java
// ============================================================================
package com.db.ms.exception;

/**
 * Exception thrown when attempting to authenticate an inactive user account.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
public class UserInactiveException extends RuntimeException {
    
    public UserInactiveException() {
        super("User account is inactive. Please contact administrator.");
    }
}