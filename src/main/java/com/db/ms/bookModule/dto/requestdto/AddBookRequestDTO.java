package com.db.ms.bookModule.dto.requestdto;


/**
 * Request DTO: Add Book
 * - Uses wrappers to allow null checks in service.
 */


public class AddBookRequestDTO {
    private Long bookId;               // optional; if null or <=0, server assigns
    private String bookTitle;
    private String bookAuthorId;
    private String bookCategoryId;     // e.g., "CAT-FIC"
    private Double bookPrice;          // non-negative
    private Long bookStockQuantity;    // non-negative

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthorId() {
        return bookAuthorId;
    }

    public String getBookCategoryId() {
        return bookCategoryId;
    }

    public Double getBookPrice() {
        return bookPrice;
    }

    public Long getBookStockQuantity() {
        return bookStockQuantity;
    }
}
