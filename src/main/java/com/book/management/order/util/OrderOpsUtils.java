
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

Design
1. All methods are static; the class is non-instantiable via a private constructor.
2. No logging inside utils; service layer is responsible for logging and orchestration.
3. Initial validation uses a supplied handler to mirror the service’s centralized exception handling.

Methods
1. initialValidation
   - Validates userId, bookOrder and entries; derives bookIds list.
   - Uses the provided handler to throw IllegalArgumentException consistently with the service’s policy.
   - Returns the derived bookIds for downstream steps.

2. priceCheck
   - Calls BookService to fetch a price map for given bookIds.
   - Ensures price map is non-null/non-empty and no requested id has a null price.
   - Propagates BookNotFoundException for missing/null prices; wraps other errors in OrderNotPlacedException.

3. stockCheck
   - Calls InventoryService to check bulk availability for the requested items.
   - Ensures the availability map covers all requested ids; throws InsufficientStockException when any unavailable.

4. stockReduction
   - Calls InventoryService to reduce bulk inventory for the requested items.
   - Propagates InsufficientStockException on failures.

5. computeTotalAndSaveOrder
   - Computes total (price * qty) and persists a new Order with the given initial status.
   - Throws OrderNotPlacedException on persistence failures or null returns.

Usage
1. In OrderServiceImpl.preProcessAndSaveOrder: call initialValidation → priceCheck → stockCheck → stockReduction → computeTotalAndSaveOrder.
*/

/**
 * Util for Order Management.
 *
 * @author Rehan Ashraf
 * @version 1.1 (delegate service calls to utila)
 * @since 2024-12-15
 */
public final class OrderOpsUtils {

    private OrderOpsUtils() {
    }

    /*
    Functional contract for centralized validation error handling.

    Purpose
    1. Allow utils to delegate exception throwing to the service’s handler.
    2. Keep consistency with service-level constants and messaging.

    Signature
    - handle(exceptionType, logMessage, errorMessage)
    */
    @FunctionalInterface
    public interface ValidationErrorHandler {
        void handle(String exceptionType, String logMessage, String errorMessage);
    }

    /*
    initialValidation
    Inputs
    1. PlaceOrderRequestDTO request: incoming order request (== null).
    2. ValidationErrorHandler handler: service-supplied handler (lambda, not a method reference).
    3. String IAE: the service’s IllegalArgumentException type constant (e.g., "IllegalArgumentException").

    Sonar null-safety
    - Guard access to request before dereferencing request.getUserId().
    - Guard access to bookOrder before iteration or keySet() usage.

    Steps
    1. If request is null, invoke handler and then explicitly throw IllegalArgumentException to satisfy static analysis.
    2. Validate userId > 0.
    3. Safely read bookOrder, ensure non-null and non-empty.
    4. Validate each entry: bookId > 0 and qty > 0.
    5. Derive and return bookIds list from bookOrder keys.

    Output
    - List<Long> bookIds for downstream price and stock operations.
    */
    public static List<Long> initialValidation(PlaceOrderRequestDTO request, ValidationErrorHandler handler, String iae) {
        if (request == null) {
            handler.handle(iae, "Invalid request or userId", "Invalid request or userId provided.");
            throw new IllegalArgumentException("Invalid request or userId provided.");
        }

        if (request.getUserId() <= 0) {
            handler.handle(iae, "Invalid userId", "Invalid request or userId provided.");
            throw new IllegalArgumentException("Invalid request or userId provided.");
        }

        final Map<Long, Integer> bookOrder = request.getBookOrder(); // may be null, guard below
        if (bookOrder == null || bookOrder.isEmpty()) {
            handler.handle(iae, "Empty bookOrder", "bookOrder must not be empty");
            throw new IllegalArgumentException("bookOrder must not be empty");
        }

        // Defensive iteration with null checks at entry level
        for (Map.Entry<Long, Integer> entry : bookOrder.entrySet()) {
            final Long bookId = entry.getKey();
            final Integer qty = entry.getValue();
            if (bookId == null || bookId <= 0 || qty == null || qty <= 0) {
                handler.handle(iae, String.format("Invalid bookId (%s) or quantity (%s)", bookId, qty), "Invalid bookId or quantity in order list.");
                throw new IllegalArgumentException("Invalid bookId or quantity in order list.");
            }
        }

        return new ArrayList<>(bookOrder.keySet());
    }

    /*
    priceCheck
    Inputs
    1. BookService bookService: external service to fetch prices.
    2. List<Long> bookIds: ids to price.

    Steps
    1. Call bookService.getBookPricesMap(bookIds).
    2. Extract price map; ensure non-null and non-empty.
    3. Ensure that for each requested id, price exists and is non-null.

    Output
    - Map<Long, Double> priceMap.

    Exceptions
    - BookNotFoundException when price map is empty/null or any requested id has null price.
    - OrderNotPlacedException for other system errors while calling BookService.
    */
    public static Map<Long, Double> priceCheck(BookService bookService, List<Long> bookIds)
            throws BookNotFoundException, OrderNotPlacedException {
        try {
            final BookPriceResponseDTO priceResponse = bookService.getBookPricesMap(bookIds);
            final Map<Long, Double> priceMap = (priceResponse == null) ? null : priceResponse.getPrices();

            if (priceMap == null || priceMap.isEmpty()) {
                throw new BookNotFoundException("Book Service returned an empty or null price map for requested books.");
            }

            for (Long id : bookIds) {
                if (priceMap.containsKey(id) && priceMap.get(id) == null) {
                    throw new BookNotFoundException("Null price found for books: [" + id + "]");
                }
            }

            return priceMap;

        } catch (BookNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OrderNotPlacedException("Failed to retrieve prices from the Book service due to a system error.");
        }
    }

    /*
    stockCheck
    Inputs
    1. InventoryService inventoryService: external service to check availability.
    2. Map<Long, Integer> bookOrder: requested quantities per bookId (== null when called incorrectly).

    Sonar null-safety
    - If bookOrder is null, fail fast with OrderNotPlacedException.

    Steps
    1. If bookOrder is null, throw OrderNotPlacedException to avoid NPE downstream.
    2. Call inventoryService.checkBulkAvailability(bookOrder).
    3. Ensure availability map contains all requested keys.
    4. Collect any keys with false/null; throw InsufficientStockException if any.

    Output
    - Map<Long, Boolean> availability map, validated to cover all requested keys and all true.

    Exceptions
    - OrderNotPlacedException if the inventory service fails or returns incomplete results or if bookOrder is null.
    - InsufficientStockException when any requested item is unavailable.
    */
    public static Map<Long, Boolean> stockCheck(InventoryService inventoryService,
                                                Map<Long, Integer> bookOrder)
            throws OrderNotPlacedException, InsufficientStockException {
        if (bookOrder == null) {
            throw new OrderNotPlacedException("Availability check failed: bookOrder is null.");
        }

        final Map<Long, Boolean> availability;
        try {
            availability = inventoryService.checkBulkAvailability(bookOrder);
        } catch (Exception ex) {
            throw new OrderNotPlacedException("Failed to check inventory availability due to a system error.");
        }

        if (availability == null || !availability.keySet().containsAll(bookOrder.keySet())) {
            throw new OrderNotPlacedException("Availability check returned incomplete results.");
        }

        final List<Long> unavailable = availability.entrySet().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        if (!unavailable.isEmpty()) {
            throw new InsufficientStockException("Insufficient stock for books: " + unavailable);
        }

        return availability;
    }

    /*
    stockReduction
    Inputs
    1. InventoryService inventoryService: external service to reduce stock.
    2. Map<Long, Integer> bookOrder: requested quantities per bookId (== null when called incorrectly).

    Sonar null-safety
    - If bookOrder is null, fail fast with OrderNotPlacedException (wrapped inside InsufficientStockException not suitable).

    Steps
    1. If bookOrder is null, throw OrderNotPlacedException to avoid NPE downstream.
    2. Invoke inventoryService.reduceBulkInventory(bookOrder).

    Output
    - None.

    Exceptions
    - InsufficientStockException when stock cannot be reduced as requested.
    - OrderNotPlacedException when bookOrder is null.
    - Runtime exceptions propagate to be handled by the service layer if needed.
    */
    public static void stockReduction(InventoryService inventoryService,
                                      Map<Long, Integer> bookOrder)
            throws InsufficientStockException, OrderNotPlacedException {
        if (bookOrder == null) {
            throw new OrderNotPlacedException("Stock reduction failed: bookOrder is null.");
        }
        inventoryService.reduceBulkInventory(bookOrder);
    }

    /*
    computeTotalAndSaveOrder
    Inputs
    1. OrderRepository orderRepository: repository to persist orders.
    2. long userId: validated user identifier.
    3. Map<Long, Integer> bookOrder: requested quantities per bookId.
    4. Map<Long, Double> priceMap: prices per bookId, validated as non-null per id.
    5. List<Long> bookIds: ids included in the order.
    6. OrderEnum initialStatus: initial status to set on the order.

    Sonar null-safety
    - Validate bookOrder and priceMap are non-null before usage.

    Steps
    1. Compute totalAmount = sum(priceMap[id] * qty) for all entries.
    2. Build Order with ID=0 so repository assigns a real ID.
    3. Save via orderRepository.save(order); validate non-null result.

    Output
    - Order savedOrder: persisted order with generated ID.

    Exceptions
    - OrderNotPlacedException when persistence fails or returns null, or when inputs are invalid.
    */
    public static Order computeTotalAndSaveOrder(OrderRepository orderRepository,
                                                 long userId,
                                                 Map<Long, Integer> bookOrder,
                                                 Map<Long, Double> priceMap,
                                                 List<Long> bookIds,
                                                 OrderEnum initialStatus)
            throws OrderNotPlacedException {

        if (bookOrder == null || priceMap == null) {
            throw new OrderNotPlacedException("Compute total failed: bookOrder or priceMap is null.");
        }

        final double totalAmount = bookOrder.entrySet().stream()
                .mapToDouble(e -> {
                    final Double price = priceMap.get(e.getKey());
                    if (price == null) {
                        throw new IllegalArgumentException("Missing price for bookId: " + e.getKey());
                    }
                    return price * e.getValue();
                })
                .sum();

        final Order order = new Order(
                0, // repository will generate the real ID
                userId,
                bookIds,
                LocalDateTime.now(),
                totalAmount,
                initialStatus
        );

        try {
            final Order savedOrder = orderRepository.save(order);
            if (savedOrder == null) {
                throw new OrderNotPlacedException("Order database persistence returned null.");
            }
            return savedOrder;
        } catch (RuntimeException ex) {
            throw new OrderNotPlacedException("Order database persistence failed.");
        }
    }
}