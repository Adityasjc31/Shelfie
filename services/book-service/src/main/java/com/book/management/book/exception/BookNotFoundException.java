package com.book.management.book.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(int id) { super("Book not found with id: " + id); }
    public BookNotFoundException(String message) { super(message); }
}
