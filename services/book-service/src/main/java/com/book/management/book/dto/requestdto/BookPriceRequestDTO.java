package com.book.management.book.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * Request DTO: multiple bookIds for price lookup.
 * Works for single-book (size=1) and multi-book.
 */
@Data
public class BookPriceRequestDTO {
    @NotEmpty
    private List<@NotNull @Positive Long> bookIds;
}
