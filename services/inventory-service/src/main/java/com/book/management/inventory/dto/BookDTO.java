package com.book.management.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Book data received from Book Service.
 * 
 * Used for inter-service communication via FeignClient.
 * Matches the structure of BookResponseDTO from book-service.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    /**
     * Unique identifier for the book.
     */
    private Long bookId;

    /**
     * Title of the book.
     */
    private String bookTitle;

    /**
     * Author ID of the book.
     */
    private String bookAuthorId;

    /**
     * Category ID of the book.
     */
    private String bookCategoryId;

    /**
     * Price of the book.
     */
    private Double bookPrice;

    /**
     * Current stock quantity from book-service.
     */
    private Long bookStockQuantity;
}
