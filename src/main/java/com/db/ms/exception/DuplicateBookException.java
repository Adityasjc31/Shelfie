package com.db.ms.exception;



public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(int id) { super("Duplicate bookId detected: " + id); }
}
