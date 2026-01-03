
package com.book.management.order.client.fallback;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import com.book.management.order.exception.OrderNotPlacedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for BookClient.
 * Triggered when Book Service is unreachable or times out.
 * We fail fast by throwing OrderNotPlacedException instead of returning an
 * empty price map to avoid partial or misleading billing.
 */

@Component
@Slf4j
public class BookClientFallback implements BookServiceClient {

    /**
     * Fallback for price lookup.
     * We fail fast instead of returning an empty price map to avoid misleading billing.
     */
    @Override
    public GetBookPriceResponseDTO getBookPrices(GetBookPriceRequestDTO request) {
        log.error("CRITICAL: Book Service is unreachable. Price lookup failed for books: {}",
                request.getBookIds());
        throw new OrderNotPlacedException("Book Service is temporarily unavailable. Please try again later.");
    }
}

