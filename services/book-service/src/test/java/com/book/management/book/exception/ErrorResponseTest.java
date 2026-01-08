package com.book.management.book.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create ErrorResponse with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertNull(response.getMessage());
        assertNull(response.getPath());
        assertNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create ErrorResponse with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse(404, "Not Found", "/api/books/1", now);
        
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getMessage());
        assertEquals("/api/books/1", response.getPath());
        assertEquals(now, response.getTimestamp());
    }

    @Test
    @DisplayName("Should set and get status")
    void shouldSetAndGetStatus() {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(500);
        assertEquals(500, response.getStatus());
    }

    @Test
    @DisplayName("Should set and get message")
    void shouldSetAndGetMessage() {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Error occurred");
        assertEquals("Error occurred", response.getMessage());
    }

    @Test
    @DisplayName("Should set and get path")
    void shouldSetAndGetPath() {
        ErrorResponse response = new ErrorResponse();
        response.setPath("/api/test");
        assertEquals("/api/test", response.getPath());
    }

    @Test
    @DisplayName("Should set and get timestamp")
    void shouldSetAndGetTimestamp() {
        ErrorResponse response = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 8, 10, 30);
        response.setTimestamp(timestamp);
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    @DisplayName("Should have equals and hashCode")
    void shouldHaveEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response1 = new ErrorResponse(404, "Not Found", "/api", now);
        ErrorResponse response2 = new ErrorResponse(404, "Not Found", "/api", now);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should have toString")
    void shouldHaveToString() {
        ErrorResponse response = new ErrorResponse(400, "Bad Request", "/test", LocalDateTime.now());
        String toString = response.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("400"));
        assertTrue(toString.contains("Bad Request"));
    }
}
