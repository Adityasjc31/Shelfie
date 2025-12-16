package com.book.management.book.exception;



public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(int id) { super("Duplicate bookId detected: " + id); }
}
