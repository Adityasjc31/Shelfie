package com.book.management.order.client.fallback;

import com.book.management.order.client.BookServiceClient;
import com.book.management.order.exception.OrderNotPlacedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookClientFallbackFactory implements FallbackFactory<BookServiceClient> {
    @Override
    public BookServiceClient create(Throwable cause) {
        return request -> {
            log.error("CRITICAL: Book Service failed for IDs: {} | Reason: {}",
                    request.getBookIds(), cause.getMessage());
            throw new OrderNotPlacedException("Book Service unavailable: " + cause.getMessage());
        };
    }
}
