package com.book.management.book.dto.responsedto;


import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Response DTO: Book (no Builder)
 */
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private long bookId;
    private String bookTitle;
    private String bookAuthorId;
    private String bookCategoryId; // canonical ID like "CAT-FIC"
    private double bookPrice;
    private long bookStockQuantity;

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setBookAuthorId(String bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }

    public void setBookCategoryId(String bookCategoryId) {
        this.bookCategoryId = bookCategoryId;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public void setBookStockQuantity(long bookStockQuantity) {
        this.bookStockQuantity = bookStockQuantity;
    }

    public long getBookId() {
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

    public double getBookPrice() {
        return bookPrice;
    }

    public long getBookStockQuantity() {
        return bookStockQuantity;
    }
}
