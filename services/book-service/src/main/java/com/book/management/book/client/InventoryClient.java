package com.book.management.book.client;

import com.book.management.book.client.fallback.InventoryClientFallbackFactory;
import com.book.management.book.dto.requestdto.InventoryCreateDTO;
import com.book.management.book.dto.responsedto.InventoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "inventory-service",
        path = "/api/v1/inventory",
        fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryClient {

    @PostMapping("/create")
    InventoryResponseDTO createInventory(@RequestBody InventoryCreateDTO request);

    @GetMapping("/book/{bookId}")
    InventoryResponseDTO getInventoryByBookId(@PathVariable("bookId") Long bookId);

    // New Delete Endpoint
    @DeleteMapping("/book/{bookId}")
    void deleteInventoryByBookId(@PathVariable("bookId") Long bookId);

    // Note: If you uncomment the update method later, ensure
    // InventoryUpdateDTO is imported from your dto package.
//    @PutMapping("/update/{bookId}")
//    void updateInventoryQuantity(@PathVariable("bookId") Long bookId, @RequestBody InventoryUpdateDTO request);
}