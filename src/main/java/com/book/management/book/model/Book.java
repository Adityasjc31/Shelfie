package com.book.management.book.model;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {
    private long bookId;
    private String bookTitle;
    private String bookAuthorId;
    private String bookCategoryId; // canonical ID like "CAT-FIC"
    private double bookPrice;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
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

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

}
