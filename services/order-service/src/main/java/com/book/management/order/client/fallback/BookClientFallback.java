package com.book.management.order.client.fallback;

import com.book.management.order.client.BookServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback implementation for BookClient.
 * Triggered when Book Service is unreachable or times out.
 */
@Component
@Slf4j
public class BookClientFallback implements BookServiceClient {

    @Override
    public Map<Long, Double> getBookPrices(List<Long> bookIds) {
        log.error("Book Service is currently unavailable. Returning empty price map.");
        // We return an empty map; the ServiceImpl will catch this or throw an exception
        return Collections.emptyMap();
    }
}
