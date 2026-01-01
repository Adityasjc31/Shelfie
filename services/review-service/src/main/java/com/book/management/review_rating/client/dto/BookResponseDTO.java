package com.book.management.review_rating.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Book information received from Book Microservice.
 * 
 * This DTO represents the contract between Review Service and Book Service.
 * It should match the response structure from Book Service.
 * 
 * Microservices Best Practice:
 * - Keep only fields needed by Review Service
 * - Maintain version compatibility
 * - Use defensive copying if needed
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {
    
    private Long bookId;
    private String bookTitle;
    private Long bookAuthorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal bookPrice;
    private Integer stockQuantity;
    private String isbn;
    private String description;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private Integer pages;
    private Double averageRating;
    private Long totalReviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
