package com.book.management.book.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Book Model Tests")
class BookTest {

    @Test
    @DisplayName("Test no-arg constructor creates empty Book")
    void testNoArgConstructor() {
        Book book = new Book();
        
        assertNotNull(book);
        assertNull(book.getBookId());
        assertNull(book.getBookTitle());
        assertNull(book.getBookAuthorId());
        assertNull(book.getBookCategoryId());
        assertEquals(0.0, book.getBookPrice());
    }

    @Test
    @DisplayName("Test all-args constructor creates Book with all fields")
    void testAllArgsConstructor() {
        Book book = new Book(1L, "Test Book", "AUTH-1", "CAT-FIC", 29.99);
        
        assertNotNull(book);
        assertEquals(1L, book.getBookId());
        assertEquals("Test Book", book.getBookTitle());
        assertEquals("AUTH-1", book.getBookAuthorId());
        assertEquals("CAT-FIC", book.getBookCategoryId());
        assertEquals(29.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test builder creates Book with all fields")
    void testBuilder() {
        Book book = Book.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .bookAuthorId("AUTH-1")
                .bookCategoryId("CAT-FIC")
                .bookPrice(29.99)
                .build();
        
        assertNotNull(book);
        assertEquals(1L, book.getBookId());
        assertEquals("Test Book", book.getBookTitle());
        assertEquals("AUTH-1", book.getBookAuthorId());
        assertEquals("CAT-FIC", book.getBookCategoryId());
        assertEquals(29.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test builder with partial fields")
    void testBuilderPartial() {
        Book book = Book.builder()
                .bookTitle("Partial Book")
                .bookPrice(19.99)
                .build();
        
        assertNotNull(book);
        assertNull(book.getBookId());
        assertEquals("Partial Book", book.getBookTitle());
        assertNull(book.getBookAuthorId());
        assertNull(book.getBookCategoryId());
        assertEquals(19.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test setters set field values correctly")
    void testSetters() {
        Book book = new Book();
        
        book.setBookId(1L);
        book.setBookTitle("Updated Title");
        book.setBookAuthorId("AUTH-2");
        book.setBookCategoryId("CAT-SCI");
        book.setBookPrice(39.99);
        
        assertEquals(1L, book.getBookId());
        assertEquals("Updated Title", book.getBookTitle());
        assertEquals("AUTH-2", book.getBookAuthorId());
        assertEquals("CAT-SCI", book.getBookCategoryId());
        assertEquals(39.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test getters return correct field values")
    void testGetters() {
        Book book = new Book(5L, "Getter Test", "AUTH-3", "CAT-HIS", 49.99);
        
        assertEquals(5L, book.getBookId());
        assertEquals("Getter Test", book.getBookTitle());
        assertEquals("AUTH-3", book.getBookAuthorId());
        assertEquals("CAT-HIS", book.getBookCategoryId());
        assertEquals(49.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test equals for same values")
    void testEquals() {
        Book book1 = new Book(1L, "Test", "AUTH-1", "CAT-FIC", 29.99);
        Book book2 = new Book(1L, "Test", "AUTH-1", "CAT-FIC", 29.99);
        
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Test equals returns false for different values")
    void testNotEquals() {
        Book book1 = new Book(1L, "Test", "AUTH-1", "CAT-FIC", 29.99);
        Book book2 = new Book(2L, "Different", "AUTH-2", "CAT-SCI", 39.99);
        
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Test hashCode for same values")
    void testHashCode() {
        Book book1 = new Book(1L, "Test", "AUTH-1", "CAT-FIC", 29.99);
        Book book2 = new Book(1L, "Test", "AUTH-1", "CAT-FIC", 29.99);
        
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    @DisplayName("Test toString contains all field values")
    void testToString() {
        Book book = new Book(1L, "Test Book", "AUTH-1", "CAT-FIC", 29.99);
        
        String toString = book.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Book"));
        assertTrue(toString.contains("AUTH-1"));
        assertTrue(toString.contains("CAT-FIC"));
        assertTrue(toString.contains("29.99"));
    }

    @Test
    @DisplayName("Test Book with null bookId")
    void testWithNullBookId() {
        Book book = new Book(null, "Test", "AUTH-1", "CAT-FIC", 29.99);
        
        assertNull(book.getBookId());
        assertNotNull(book.getBookTitle());
    }

    @Test
    @DisplayName("Test Book with zero price")
    void testWithZeroPrice() {
        Book book = new Book(1L, "Free Book", "AUTH-1", "CAT-FIC", 0.0);
        
        assertEquals(0.0, book.getBookPrice());
    }

    @Test
    @DisplayName("Test Book with negative price")
    void testWithNegativePrice() {
        Book book = new Book(1L, "Negative Price", "AUTH-1", "CAT-FIC", -10.0);
        
        assertEquals(-10.0, book.getBookPrice());
    }

    @Test
    @DisplayName("Test Book with very high price")
    void testWithHighPrice() {
        Book book = new Book(1L, "Expensive Book", "AUTH-1", "CAT-FIC", 9999.99);
        
        assertEquals(9999.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test Book with empty title")
    void testWithEmptyTitle() {
        Book book = new Book(1L, "", "AUTH-1", "CAT-FIC", 29.99);
        
        assertEquals("", book.getBookTitle());
    }

    @Test
    @DisplayName("Test Book with long title")
    void testWithLongTitle() {
        String longTitle = "A".repeat(500);
        Book book = new Book(1L, longTitle, "AUTH-1", "CAT-FIC", 29.99);
        
        assertEquals(longTitle, book.getBookTitle());
    }

    @Test
    @DisplayName("Test Book with special characters in title")
    void testWithSpecialCharactersInTitle() {
        Book book = new Book(1L, "Test @#$ Book!", "AUTH-1", "CAT-FIC", 29.99);
        
        assertEquals("Test @#$ Book!", book.getBookTitle());
    }

    @Test
    @DisplayName("Test Book field modifications")
    void testFieldModifications() {
        Book book = new Book(1L, "Original", "AUTH-1", "CAT-FIC", 29.99);
        
        book.setBookTitle("Modified");
        assertEquals("Modified", book.getBookTitle());
        
        book.setBookAuthorId("AUTH-2");
        assertEquals("AUTH-2", book.getBookAuthorId());
        
        book.setBookCategoryId("CAT-SCI");
        assertEquals("CAT-SCI", book.getBookCategoryId());
        
        book.setBookPrice(49.99);
        assertEquals(49.99, book.getBookPrice());
    }

    @Test
    @DisplayName("Test Book with all category types")
    void testWithAllCategories() {
        String[] categories = {"CAT-FIC", "CAT-SCI", "CAT-HIS", "CAT-BIO", 
                               "CAT-FAN", "CAT-MYS", "CAT-ROM", "CAT-THR", "CAT-HOR"};
        
        for (String category : categories) {
            Book book = Book.builder()
                    .bookId(1L)
                    .bookTitle("Test")
                    .bookAuthorId("AUTH-1")
                    .bookCategoryId(category)
                    .bookPrice(29.99)
                    .build();
            
            assertEquals(category, book.getBookCategoryId());
        }
    }
}
