
package com.db.ms.dto.requestdto;

import lombok.Data;
import lombok.Getter;

/**
 * Request DTO: Create Book
 * - Allows optional bookId; if provided and exists, DuplicateBookException is thrown.
 * - Category ID is normalized to canonical ID via CategoryEnum in service.
 */
@Data
@Getter
public class CreateBookRequestDTO {
    private Integer bookId;            // optional; if null/<=0, server assigns
    private String bookTitle;
    private String bookAuthorId;
    private String bookCategoryId;     // e.g., "CAT-FIC"
    private Integer bookPrice;         // non-negative
    private Integer bookStockQuantity; // non-negative
}
