package com.book.management.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookApplication Tests")
class BookApplicationTest {

    @Test
    @DisplayName("Application class exists")
    void applicationClassExists() {
        // Test that the application class exists
        assertNotNull(BookApplication.class);
    }

    @Test
    @DisplayName("Main method exists")
    void mainMethodExists() throws NoSuchMethodException {
        // Verify main method exists with correct signature
        assertNotNull(BookApplication.class.getMethod("main", String[].class));
    }
}
