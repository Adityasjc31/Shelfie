
package com.book.management.order.client;

import com.book.management.order.client.fallback.BookClientFallback;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for Book Service.
 * Fetches unit prices for the requested books via a typed request DTO.
 *
 * <p><b>Endpoint (via Gateway):</b> POST /api/v1/books/bulk/prices</p>
 *
 * <p><b>Request Body:</b></p>
 * <pre>
 * {
 *   "bookIds": [101, 105, 110]
 * }
 * </pre>
 *
 * <p><b>Response (DTO):</b></p>
 * <pre>
 * {
 *   "bookPrice": {
 *     "101": 399.0,
 *     "105": 249.5,
 *     "110": 799.0
 *   }
 * }
 * </pre>
 */
@FeignClient(
        name = "book-service",
        path = "/api/v1/book",
        fallback = BookClientFallback.class
)
public interface BookServiceClient {

    @PostMapping(
            value = "/bulk/prices",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    GetBookPriceResponseDTO getBookPrices(@RequestBody GetBookPriceRequestDTO request);
}
