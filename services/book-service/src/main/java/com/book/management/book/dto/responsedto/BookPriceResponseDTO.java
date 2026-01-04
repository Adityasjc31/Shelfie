package com.book.management.book.dto.responsedto;

import lombok.Data;

import java.util.Map;

/**
 * Response DTO: map of {bookId -> price}.
 */
@Data
public class BookPriceResponseDTO {
    private Map<Long, Double> bookPrice;

    public BookPriceResponseDTO() {}

    public BookPriceResponseDTO(Map<Long, Double> bookPrice) {
        this.bookPrice = bookPrice;
    }
}
