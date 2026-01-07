package com.book.management.book.exception;

/**
 * Exception thrown when an invalid category ID is provided.
 * This includes non-existent category IDs or malformed category identifiers.
 */
public class InvalidCategoryException extends RuntimeException {
    
    public InvalidCategoryException(String categoryId) {
        super("Invalid category ID: " + categoryId + ". Please provide a valid category ID.");
    }
    
    public InvalidCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
