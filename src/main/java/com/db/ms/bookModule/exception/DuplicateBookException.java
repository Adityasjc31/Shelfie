package com.db.ms.bookModule.exception;



public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(int id) { super("Duplicate bookId detected: " + id); }
}
