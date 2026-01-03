package com.book.management.order.client.fallback;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.dto.requestdto.GetBookPriceRequestDTO;
import com.book.management.order.dto.responsedto.GetBookPriceResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Fallback implementation for BookClient.
 * Triggered when Book Service is unreachable or times out.
 */
@Component
@Slf4j
public class BookClientFallback implements BookServiceClient {

    @Override
    public GetBookPriceResponseDTO getBookPrices(GetBookPriceRequestDTO request) {
        log.error("Book Service is currently unavailable. Returning empty price map for books: {}",
                request.getBookIds());
        // Return empty price map - ServiceImpl will handle this appropriately
        return new GetBookPriceResponseDTO(Collections.emptyMap());
    }
}
