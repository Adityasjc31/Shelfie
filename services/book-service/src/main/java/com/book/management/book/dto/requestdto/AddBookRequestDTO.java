package com.book.management.book.dto.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO: Add Book
 * - Uses wrappers to allow null checks in service.
 * - Includes validation annotations for request validation.
 */
public class AddBookRequestDTO {
    
    private Long bookId;               // optional; if null or <=0, server assigns
    
    @NotBlank(message = "Book title is required")
    @Size(min = 1, max = 255, message = "Book title must be between 1 and 255 characters")
    private String bookTitle;
    
    @NotBlank(message = "Author ID is required")
    private String bookAuthorId;
    
    @NotBlank(message = "Category ID is required")
    @Pattern(regexp = "^CAT-(FIC|NF|TCH|SCI|HIS|FAN|BIO|BUS|OTH)$", 
             message = "Invalid category ID. Valid values: CAT-FIC, CAT-NF, CAT-TCH, CAT-SCI, CAT-HIS, CAT-FAN, CAT-BIO, CAT-BUS, CAT-OTH")
    private String bookCategoryId;     // e.g., "CAT-FIC"
    
    @NotNull(message = "Book price is required")
    @Min(value = 0, message = "Book price must be non-negative")
    private Double bookPrice;          // non-negative
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer bookStockQuantity;    // non-negative

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthorId() {
        return bookAuthorId;
    }

    public void setBookAuthorId(String bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }

    public String getBookCategoryId() {
        return bookCategoryId;
    }

    public void setBookCategoryId(String bookCategoryId) {
        this.bookCategoryId = bookCategoryId;
    }

    public Double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(Double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public Integer getBookStockQuantity() {
        return bookStockQuantity;
    }

    public void setBookStockQuantity(Integer bookStockQuantity) {
        this.bookStockQuantity = bookStockQuantity;
    }
}
