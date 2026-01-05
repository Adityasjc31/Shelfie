package com.book.management.book.dto.requestdto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO: Partial update of Book
 */
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequestDTO {
    private String bookTitle;
    private String bookAuthorId;
    private String bookCategoryId;     // e.g., "CAT-FIC"
    private Double bookPrice;          // non-negative if provided
  //  private Long bookStockQuantity;    // non-negative if provided

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

//    public Long getBookStockQuantity() {
//        return bookStockQuantity;
//    }
}
