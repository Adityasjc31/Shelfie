package com.book.management.book.dto.requestdto;


/**
 * Request DTO: Partial update of Book
 */
public class UpdateBookRequestDTO {
    private String bookTitle;
    private String bookAuthorId;
    private String bookCategoryId;     // e.g., "CAT-FIC"
    private Double bookPrice;          // non-negative if provided
    private Long bookStockQuantity;    // non-negative if provided

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
