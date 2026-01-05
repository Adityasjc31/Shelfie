
package com.book.management.order.client;

import com.book.management.order.client.fallback.BookClientFallbackFactory;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for Book Service.
 * Fetches unit prices for the requested books via a typed request DTO.

 * Endpoint (via Gateway): POST /api/v1/books/bulk/prices

 * Request Body:
 * {
 *   "bookIds": [101, 105, 110]
 * }

 * Response (DTO):
 * {
 *   "bookPrice": {
 *     "101": 399.0,
 *     "105": 249.5,
 *     "110": 799.0
 *   }
 * }
 */
@FeignClient(
        name = "book-service",
        path = "/api/v1/book",
        fallbackFactory = BookClientFallbackFactory.class
)
public interface BookServiceClient {

    @PostMapping(
            value = "/bulk/prices",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    GetBookPriceResponseDTO getBookPrices(@RequestBody GetBookPriceRequestDTO request);
}
