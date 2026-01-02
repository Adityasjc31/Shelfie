package com.book.management.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Feign Client for Book Service.
 * Fetches prices for the requested books.
 */
@FeignClient(name = "book-service", path = "/api/v1/books")
public interface BookClient {
    @GetMapping("/prices")
    Map<Long, Double> getBookPrices(@RequestParam List<Long> bookIds);
}

