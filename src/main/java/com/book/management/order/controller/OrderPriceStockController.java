package com.db.ms.order.controller;

import com.db.ms.order.dto.requestdto.PlaceOrderRequestDTO;
import com.db.ms.order.dto.responsedto.OrderPriceStockResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/order-price-stock")
@RequiredArgsConstructor
@Slf4j
public class OrderPriceStockController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.books.base-url:http://localhost:8080/api/v1/book}")
    private String booksServiceBaseUrl;

    @Value("${services.inventory.base-url:http://localhost:8080/api/v1/inventory}")
    private String inventoryServiceBaseUrl;

    // ... (fetch method remains the same)

//    @PostMapping("/fetch")
//    public ResponseEntity<OrderPriceStockResponseDTO> fetch(@RequestBody PlaceOrderRequestDTO request) {
//        OrderPriceStockResponseDTO dto = computePriceAndStock(request);
//        return ResponseEntity.ok(dto);
//    }


    public OrderPriceStockResponseDTO computePriceAndStock(PlaceOrderRequestDTO request) {
        // --- Validate payload ---
        if (request == null || request.getBookOrder() == null || request.getBookOrder().isEmpty()) {
            log.warn("Invalid request for price/stock: payload missing or empty bookOrder");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookOrder must contain at least one item");
        }

        Map<Long, Integer> bookOrder = request.getBookOrder();
        List<Long> bookIds = new ArrayList<>(bookOrder.keySet()); // Extract just the IDs

        log.info("Computing price & stock for {} items (userId={})", bookOrder.size(), request.getUserId());

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // --- 1) Fetch prices from Books service ---

        // HttpEntity for Books Service (List<Long>)
        HttpEntity<List<Long>> booksEntity = new HttpEntity<>(bookIds, headers); // <--- CHANGE 1: Sending List<Long>

        String getPriceUrl = booksServiceBaseUrl + "/getPrice";
        ResponseEntity<Map<Long, Double>> priceResponse;
        try {
            priceResponse = restTemplate.exchange(
                    getPriceUrl,
                    HttpMethod.POST,
                    booksEntity, // <--- CHANGE 2: Using booksEntity (List<Long> body)
                    new ParameterizedTypeReference<Map<Long, Double>>() {}
            );
        } catch (Exception ex) {
            log.error("Books service price fetch failed: url={}, error={}", getPriceUrl, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to fetch prices from Books service");
        }

        Map<Long, Double> priceMap = priceResponse.getBody();
        if (priceMap == null || priceMap.isEmpty()) {
            log.warn("Books service returned empty/null price map");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to fetch prices for requested books");
        }

        // --- 2) Check stock via Inventory service ---

        // HttpEntity for Inventory Service (Map<Long, Integer>) - Requires quantity
        HttpEntity<Map<Long, Integer>> inventoryEntity = new HttpEntity<>(bookOrder, headers); // <--- NEW ENTITY: Sending Map<Long, Integer>

        String checkStockUrl = inventoryServiceBaseUrl + "/checkStock";
        ResponseEntity<Map<Long, Boolean>> stockResponse;
        try {
            stockResponse = restTemplate.exchange(
                    checkStockUrl,
                    HttpMethod.POST,
                    inventoryEntity, // <--- CHANGE 3: Using inventoryEntity (Map<Long, Integer> body)
                    new ParameterizedTypeReference<Map<Long, Boolean>>() {}
            );
        } catch (Exception ex) {
            log.error("Inventory service stock check failed: url={}, error={}", checkStockUrl, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to check stock from Inventory service");
        }

        Map<Long, Boolean> stockMap = stockResponse.getBody();
        if (stockMap == null || stockMap.isEmpty()) {
            log.warn("Inventory service returned empty/null stock map");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to verify stock for requested books");
        }

        // --- 3) Validate presence and stock availability for all requested IDs ---
        // (This logic remains valid as it uses the local bookOrder keys)
        List<Long> missingPrice = new ArrayList<>();
        List<Long> unavailable = new ArrayList<>();
        for (Long bookId : bookOrder.keySet()) {
            if (!priceMap.containsKey(bookId)) {
                missingPrice.add(bookId);
            }
            Boolean ok = stockMap.get(bookId);
            if (ok == null || !ok) {
                unavailable.add(bookId);
            }
        }
        if (!missingPrice.isEmpty()) {
            log.warn("Missing prices for books: {}", missingPrice);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing prices for books: " + missingPrice);
        }
        if (!unavailable.isEmpty()) {
            log.warn("Insufficient stock for books: {}", unavailable);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for books: " + unavailable);
        }

        // --- 4) Return combined DTO ---
        OrderPriceStockResponseDTO combined = new OrderPriceStockResponseDTO(priceMap, stockMap);
        log.info("Price & stock checks successful for {} items", bookOrder.size());
        return combined;
    }
}