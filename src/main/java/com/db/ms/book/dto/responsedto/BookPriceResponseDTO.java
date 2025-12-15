package com.db.ms.book.dto.responsedto;

import lombok.Data;

import java.util.Map;

/**
 * Response DTO: map of {bookId -> price}.
 */
@Data
public class BookPriceResponseDTO {
    private Map<Long, Double> prices;

    public BookPriceResponseDTO() {}

    public BookPriceResponseDTO(Map<Long, Double> prices) {
        this.prices = prices;
    }
}
