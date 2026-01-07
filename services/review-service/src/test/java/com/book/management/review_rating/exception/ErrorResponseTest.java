package com.book.management.review_rating.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ErrorResponse.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ErrorResponseTest {

    @Test
    @DisplayName("All-args constructor should create complete ErrorResponse")
    void allArgsConstructor_CreatesCompleteErrorResponse() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(404, "Not found", "/api/test", now);

        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not found", errorResponse.getMessage());
        assertEquals("/api/test", errorResponse.getPath());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("No-args constructor should create empty ErrorResponse")
    void noArgsConstructor_CreatesEmptyErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();

        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
        assertNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Setters should update fields correctly")
    void setters_UpdateFieldsCorrectly() {
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();

        errorResponse.setStatus(500);
        errorResponse.setMessage("Internal error");
        errorResponse.setPath("/api/test2");
        errorResponse.setTimestamp(now);

        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal error", errorResponse.getMessage());
        assertEquals("/api/test2", errorResponse.getPath());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Equals should work correctly")
    void equals_WorksCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse er1 = new ErrorResponse(404, "Not found", "/api/test", now);
        ErrorResponse er2 = new ErrorResponse(404, "Not found", "/api/test", now);
        ErrorResponse er3 = new ErrorResponse(500, "Not found", "/api/test", now);

        assertEquals(er1, er2);
        assertNotEquals(er1, er3);
    }

    @Test
    @DisplayName("HashCode should be consistent for equal objects")
    void hashCode_ConsistentForEqualObjects() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse er1 = new ErrorResponse(404, "Not found", "/api/test", now);
        ErrorResponse er2 = new ErrorResponse(404, "Not found", "/api/test", now);

        assertEquals(er1.hashCode(), er2.hashCode());
    }

    @Test
    @DisplayName("ToString should contain key fields")
    void toString_ContainsKeyFields() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(404, "Not found", "/api/test", now);

        String toString = errorResponse.toString();
        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not found"));
        assertTrue(toString.contains("/api/test"));
    }
}
