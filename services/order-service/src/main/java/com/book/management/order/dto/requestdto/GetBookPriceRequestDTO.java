package com.book.management.order.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for fetching unit prices of multiple books.
 *
 * Fields:
 * {@code bookIds}: List of book IDs to fetch prices for.
 *
 *
 * Intended use:
 *   Sent by Order Service to Book Service to retrieve unit prices for the requested books.
 *   Consumed by Book Service controller/handler for bulk price lookup.
 *
 *
 * <p>Example (JSON request body):</p>
 * <pre>
 * {
 *   "bookIds": [101, 105, 110]
 * }
 * </pre>
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetBookPriceRequestDTO {
    /**
     * List of book IDs to price.
     * <p>Element type: {@code Long} (bookId)</p>
     */
    @NotEmpty
    private List<@NotNull @Positive Long> bookIds;
}
