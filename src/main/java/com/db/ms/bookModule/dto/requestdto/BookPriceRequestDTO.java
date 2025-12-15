package com.db.ms.bookModule.dto.requestdto;

import lombok.Data;

import java.util.List;

/**
 * Request DTO: multiple bookIds for price lookup.
 * Works for single-book (size=1) and multi-book.
 */
@Data
public class BookPriceRequestDTO {
    private List<Long> bookIds;
}
