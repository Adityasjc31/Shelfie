package com.book.management.order.util;

import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.service.BookService;
import com.book.management.inventory.exception.InsufficientStockException;
import com.book.management.inventory.service.InventoryService;
import com.book.management.order.dto.requestdto.PlaceOrderRequestDTO;
import com.book.management.order.enums.OrderEnum;
import com.book.management.order.exception.OrderNotPlacedException;
import com.book.management.order.model.Order;
import com.book.management.order.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
OrderOpsUtils: Single utility class providing stateless operations for order processing.

Purpose
1. Centralize non-persistence order operations in one place for loose coupling.
2. Keep orchestration and logging inside OrderServiceImpl while exposing reusable helpers.
3. Preserve existing exception semantics: IllegalArgumentException, BookNotFoundException,
   InsufficientStockException, OrderNotPlacedException.

Design (Simplified)
1. All methods are static; the class is non-instantiable via a private constructor.
2. Methods throw standard and custom exceptions directly to be caught by Service or Global Handler.
3. Removed functional interface delegation for better readability and standard Java flow.
*/

/**
 * Util for Order Management.
 *
 * @author Rehan Ashraf
 * @version 1.2 (Simplified direct exception flow)
 * @since 2024-12-15
 */
public final class OrderOpsUtils {

    private OrderOpsUtils() {
    }

    /*
    initialValidation
    Inputs
    1. PlaceOrderRequestDTO request: incoming order request.

    Sonar null-safety
    - Explicitly check request for null before dereferencing userId or bookOrder.
    - Guard access to bookOrder before iteration.

    Steps
    1. If request is null, throw IllegalArgumentException.
    2. Validate userId > 0.
    3. Safely read bookOrder, ensure non-null and non-empty.
    4. Validate each entry: bookId > 0 and qty > 0.
    5. Derive and return bookIds list from bookOrder keys.
    */
    public static List<Long> initialValidation(PlaceOrderRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Invalid request provided: request body is null.");
        }

        if (request.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid request: userId must be greater than 0.");
        }

        final Map<Long, Integer> bookOrder = request.getBookOrder();
        if (bookOrder == null || bookOrder.isEmpty()) {
            throw new IllegalArgumentException("Invalid request: bookOrder must not be empty.");
        }

        // Defensive iteration with null checks at entry level
        for (Map.Entry<Long, Integer> entry : bookOrder.entrySet()) {
            final Long bookId = entry.getKey();
            final Integer qty = entry.getValue();
            if (bookId == null || bookId <= 0 || qty == null || qty <= 0) {
                throw new IllegalArgumentException("Invalid bookId or quantity found in order list.");
            }
        }

        return new ArrayList<>(bookOrder.keySet());
    }

    /*
    priceCheck
    Steps
    1. Call bookService.getBookPricesMap(bookIds).
    2. Extract price map; ensure non-null and non-empty.
    3. Ensure that for each requested id, price exists and is non-null.

    Exceptions
    - BookNotFoundException when price map is empty/null or any requested id has null price.
    - OrderNotPlacedException for other system errors while calling BookService.
    */
    public static Map<Long, Double> priceCheck(BookService bookService, List<Long> bookIds)
            throws BookNotFoundException {

        final BookPriceResponseDTO priceResponse = bookService.getBookPricesMap(bookIds);

        if (priceResponse == null || priceResponse.getPrices() == null || priceResponse.getPrices().isEmpty()) {
            throw new BookNotFoundException("Book Service returned no price data for the requested IDs.");
        }

        Map<Long, Double> priceMap = priceResponse.getPrices();

        // Secondary safety check: Ensure every ID requested is present in the response
        for (Long id : bookIds) {
            if (!priceMap.containsKey(id)) {
                throw new BookNotFoundException("Price data missing for book ID: " + id);
            }
        }
            return priceMap;
    }

    /*
    stockCheck
    Steps
    1. If bookOrder is null, throw OrderNotPlacedException.
    2. Call inventoryService.checkBulkAvailability(bookOrder).
    3. Ensure availability map contains all requested keys and all are True.

    Exceptions
    - OrderNotPlacedException if the inventory service fails or returns incomplete results.
    - InsufficientStockException when any requested item is unavailable.
    */
    public static Map<Long, Boolean> stockCheck(InventoryService inventoryService, Map<Long, Integer> bookOrder)
            throws InsufficientStockException {

            Map<Long, Boolean> availability = inventoryService.checkBulkAvailability(bookOrder);

            if (availability == null || !availability.keySet().containsAll(bookOrder.keySet())) {
                throw new OrderNotPlacedException("Inventory check returned incomplete results.");
            }

            List<Long> unavailable = availability.entrySet().stream()
                    .filter(e -> !Boolean.TRUE.equals(e.getValue()))
                    .map(longBooleanEntry -> longBooleanEntry.getKey())
                    .toList();

            if (!unavailable.isEmpty()) {
                throw new InsufficientStockException("Insufficient stock for books: " + unavailable);
            }
            return availability;

    }

    /*
    stockReduction
    Steps
    1. Invoke inventoryService.reduceBulkInventory(bookOrder).
    */
    public static void stockReduction(InventoryService inventoryService, Map<Long, Integer> bookOrder)
            throws InsufficientStockException{
        if (bookOrder == null) {
            throw new OrderNotPlacedException("Stock reduction failed: bookOrder is null.");
        }
        inventoryService.reduceBulkInventory(bookOrder);
    }

    /*
    computeTotalAndSaveOrder
    Steps
    1. Compute totalAmount = sum(priceMap[id] * qty).
    2. Build Order and persist via repository.
    */
    public static Order computeTotalAndSaveOrder(OrderRepository orderRepository,
                                                 long userId,
                                                 Map<Long, Integer> bookOrder,
                                                 Map<Long, Double> priceMap,
                                                 List<Long> bookIds,
                                                 OrderEnum initialStatus)
            throws OrderNotPlacedException {

      /*    if (bookOrder == null || priceMap == null) {
            throw new OrderNotPlacedException("Compute total failed: data maps are null.");
      } */

        double totalAmount = 0.0;
        for (Map.Entry<Long, Integer> longIntegerEntry : bookOrder.entrySet()) {
            final Double price = priceMap.get(longIntegerEntry.getKey());
            if (price == null) {
                throw new IllegalArgumentException("Missing price for bookId: " + longIntegerEntry.getKey());
            }
            double v = price * longIntegerEntry.getValue();
            totalAmount += v;
        }

        final Order order = new Order(0, userId, bookIds, LocalDateTime.now(), totalAmount, initialStatus);

        try {
            return orderRepository.save(order);
        } catch (Exception ex) {
            throw new OrderNotPlacedException("Order database persistence failed.");
        }
    }
}