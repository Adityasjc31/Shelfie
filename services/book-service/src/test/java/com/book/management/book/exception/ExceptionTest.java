package com.book.management.book.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exception Classes Tests")
class ExceptionTest {

    @Nested
    @DisplayName("BookNotFoundException Tests")
    class BookNotFoundExceptionTests {
        
        @Test
        @DisplayName("Should create exception with int id")
        void shouldCreateExceptionWithIntId() {
            BookNotFoundException ex = new BookNotFoundException(123);
            assertEquals("Book not found with id: 123", ex.getMessage());
        }

        @Test
        @DisplayName("Should create exception with string message")
        void shouldCreateExceptionWithStringMessage() {
            BookNotFoundException ex = new BookNotFoundException("Custom message");
            assertEquals("Custom message", ex.getMessage());
        }

        @Test
        @DisplayName("Should be RuntimeException")
        void shouldBeRuntimeException() {
            BookNotFoundException ex = new BookNotFoundException(1);
            assertTrue(ex instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("DuplicateBookException Tests")
    class DuplicateBookExceptionTests {
        
        @Test
        @DisplayName("Should create exception with int id")
        void shouldCreateExceptionWithIntId() {
            DuplicateBookException ex = new DuplicateBookException(456);
            assertEquals("Duplicate bookId detected: 456", ex.getMessage());
        }

        @Test
        @DisplayName("Should be RuntimeException")
        void shouldBeRuntimeException() {
            DuplicateBookException ex = new DuplicateBookException(1);
            assertTrue(ex instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("InvalidBookDataException Tests")
    class InvalidBookDataExceptionTests {
        
        @Test
        @DisplayName("Should create exception with message")
        void shouldCreateExceptionWithMessage() {
            InvalidBookDataException ex = new InvalidBookDataException("Invalid data");
            assertEquals("Invalid data", ex.getMessage());
        }

        @Test
        @DisplayName("Should be RuntimeException")
        void shouldBeRuntimeException() {
            InvalidBookDataException ex = new InvalidBookDataException("test");
            assertTrue(ex instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("InvalidCategoryException Tests")
    class InvalidCategoryExceptionTests {
        
        @Test
        @DisplayName("Should create exception with category id")
        void shouldCreateExceptionWithCategoryId() {
            InvalidCategoryException ex = new InvalidCategoryException("INVALID-CAT");
            assertEquals("Invalid category ID: INVALID-CAT. Please provide a valid category ID.", ex.getMessage());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("root cause");
            InvalidCategoryException ex = new InvalidCategoryException("Custom message", cause);
            assertEquals("Custom message", ex.getMessage());
            assertEquals(cause, ex.getCause());
        }

        @Test
        @DisplayName("Should be RuntimeException")
        void shouldBeRuntimeException() {
            InvalidCategoryException ex = new InvalidCategoryException("test");
            assertTrue(ex instanceof RuntimeException);
        }
    }
}
