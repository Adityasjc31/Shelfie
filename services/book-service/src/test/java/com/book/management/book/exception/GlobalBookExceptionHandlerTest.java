package com.book.management.book.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalBookExceptionHandler Tests")
class GlobalBookExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    private GlobalBookExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalBookExceptionHandler();
        lenient().when(webRequest.getDescription(false)).thenReturn("uri=/api/books/1");
    }

    @Nested
    @DisplayName("HandleBookNotFoundException Tests")
    class HandleBookNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 status for BookNotFoundException")
        void shouldReturn404StatusForBookNotFoundException() {
            BookNotFoundException ex = new BookNotFoundException("Book not found with ID: 1");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBookNotFoundException(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return error body with correct message")
        void shouldReturnErrorBodyWithCorrectMessage() {
            String errorMessage = "Book with ID 123 not found";
            BookNotFoundException ex = new BookNotFoundException(errorMessage);

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBookNotFoundException(ex);

            assertNotNull(response.getBody());
            assertEquals(errorMessage, response.getBody().get("message"));
        }

        @Test
        @DisplayName("Should return error body with status 404")
        void shouldReturnErrorBodyWithStatus404() {
            BookNotFoundException ex = new BookNotFoundException("Not found");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBookNotFoundException(ex);

            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().get("status"));
        }

        @Test
        @DisplayName("Should return error body with Not Found error")
        void shouldReturnErrorBodyWithNotFoundError() {
            BookNotFoundException ex = new BookNotFoundException("Not found");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBookNotFoundException(ex);

            assertNotNull(response.getBody());
            assertEquals("Not Found", response.getBody().get("error"));
        }

        @Test
        @DisplayName("Should include timestamp in response")
        void shouldIncludeTimestampInResponse() {
            BookNotFoundException ex = new BookNotFoundException("Not found");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBookNotFoundException(ex);

            assertNotNull(response.getBody());
            assertNotNull(response.getBody().get("timestamp"));
        }
    }

    @Nested
    @DisplayName("HandleDuplicateBookException Tests")
    class HandleDuplicateBookExceptionTests {

        @Test
        @DisplayName("Should return 409 status for DuplicateBookException")
        void shouldReturn409StatusForDuplicateBookException() {
            DuplicateBookException ex = new DuplicateBookException(1);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateBookException(ex, webRequest);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return ErrorResponse with correct message")
        void shouldReturnErrorResponseWithCorrectMessage() {
            DuplicateBookException ex = new DuplicateBookException(123);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateBookException(ex, webRequest);

            assertNotNull(response.getBody());
            assertTrue(response.getBody().getMessage().contains("123"));
        }

        @Test
        @DisplayName("Should return ErrorResponse with status 409")
        void shouldReturnErrorResponseWithStatus409() {
            DuplicateBookException ex = new DuplicateBookException(1);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateBookException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals(409, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should include path in response")
        void shouldIncludePathInResponse() {
            DuplicateBookException ex = new DuplicateBookException(1);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateBookException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals("uri=/api/books/1", response.getBody().getPath());
        }

        @Test
        @DisplayName("Should include timestamp in response")
        void shouldIncludeTimestampInResponse() {
            DuplicateBookException ex = new DuplicateBookException(1);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateBookException(ex, webRequest);

            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
        }
    }

    @Nested
    @DisplayName("HandleInvalidBookDataException Tests")
    class HandleInvalidBookDataExceptionTests {

        @Test
        @DisplayName("Should return 400 status for InvalidBookDataException")
        void shouldReturn400StatusForInvalidBookDataException() {
            InvalidBookDataException ex = new InvalidBookDataException("Invalid book data");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidBookDataException(ex, webRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return ErrorResponse with correct message")
        void shouldReturnErrorResponseWithCorrectMessage() {
            String errorMessage = "Book title cannot be empty";
            InvalidBookDataException ex = new InvalidBookDataException(errorMessage);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidBookDataException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals(errorMessage, response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should return ErrorResponse with status 400")
        void shouldReturnErrorResponseWithStatus400() {
            InvalidBookDataException ex = new InvalidBookDataException("Invalid data");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidBookDataException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should include path in response")
        void shouldIncludePathInResponse() {
            InvalidBookDataException ex = new InvalidBookDataException("Invalid");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidBookDataException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals("uri=/api/books/1", response.getBody().getPath());
        }
    }

    @Nested
    @DisplayName("HandleInvalidCategoryException Tests")
    class HandleInvalidCategoryExceptionTests {

        @Test
        @DisplayName("Should return 400 status for InvalidCategoryException")
        void shouldReturn400StatusForInvalidCategoryException() {
            InvalidCategoryException ex = new InvalidCategoryException("INVALID-CAT");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryException(ex, webRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return ErrorResponse with correct message")
        void shouldReturnErrorResponseWithCorrectMessage() {
            InvalidCategoryException ex = new InvalidCategoryException("WRONG-CAT");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryException(ex, webRequest);

            assertNotNull(response.getBody());
            assertTrue(response.getBody().getMessage().contains("WRONG-CAT"));
        }

        @Test
        @DisplayName("Should return ErrorResponse with status 400")
        void shouldReturnErrorResponseWithStatus400() {
            InvalidCategoryException ex = new InvalidCategoryException("BAD-CAT");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should include path in response")
        void shouldIncludePathInResponse() {
            InvalidCategoryException ex = new InvalidCategoryException("TEST-CAT");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals("uri=/api/books/1", response.getBody().getPath());
        }

        @Test
        @DisplayName("Should include timestamp in response")
        void shouldIncludeTimestampInResponse() {
            InvalidCategoryException ex = new InvalidCategoryException("CAT-INVALID");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryException(ex, webRequest);

            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
        }
    }

    @Nested
    @DisplayName("HandleGlobalException Tests")
    class HandleGlobalExceptionTests {

        @Test
        @DisplayName("Should return 500 status for generic Exception")
        void shouldReturn500StatusForGenericException() {
            Exception ex = new Exception("Unexpected error");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return generic error message")
        void shouldReturnGenericErrorMessage() {
            Exception ex = new RuntimeException("Database connection failed");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertNotNull(response.getBody());
            assertTrue(response.getBody().getMessage().contains("unexpected error"));
        }

        @Test
        @DisplayName("Should return ErrorResponse with status 500")
        void shouldReturnErrorResponseWithStatus500() {
            Exception ex = new NullPointerException("null");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should include path in response")
        void shouldIncludePathInResponse() {
            Exception ex = new IllegalStateException("Bad state");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertNotNull(response.getBody());
            assertEquals("uri=/api/books/1", response.getBody().getPath());
        }

        @Test
        @DisplayName("Should include timestamp in response")
        void shouldIncludeTimestampInResponse() {
            Exception ex = new Exception("Error");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        @DisplayName("Should not expose internal error details")
        void shouldNotExposeInternalErrorDetails() {
            Exception ex = new RuntimeException("Database password: secret123");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertNotNull(response.getBody());
            assertFalse(response.getBody().getMessage().contains("secret123"));
            assertFalse(response.getBody().getMessage().contains("password"));
        }
    }
}
