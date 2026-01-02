package com.book.management.order.client;

import com.book.management.order.client.fallback.BookClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Feign Client for Book Service.
 * Fetches prices for the requested books.
 */
@FeignClient(name = "book-service", path = "/api/v1/books",fallback = BookClientFallback.class)
public interface BookServiceClient {
    @GetMapping("/prices")
    Map<Long, Double> getBookPrices(@RequestParam List<Long> bookIds);
}

