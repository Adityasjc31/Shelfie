package com.book.management.book.service.impl;

import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.enums.CategoryEnum;
import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.exception.DuplicateBookException;
import com.book.management.book.exception.InvalidBookDataException;
import com.book.management.book.model.Book;
import com.book.management.book.repository.BookRepository;
import com.book.management.book.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of BookService interface.
 * 
 * This service acts as the primary coordinator for book-related operations.
 * Uses JPA repository for MySQL persistence.
 * 
 * @author Shashwat Srivastava + Aditya Srivastava
 * @version 3.0 - Converted to JPA Repository
 * @since 2024-12-16
 */
@Service
@Slf4j
public class BookServiceImpl implements BookService {

    // ============================================================================
    // Dependencies - Following Dependency Inversion Principle
    // ============================================================================

    private final BookRepository bookRepository;

    /**
     * Constructor-based dependency injection (recommended over field injection).
     * Enables better testability and makes dependencies explicit.
     *
     * @param bookRepository the JPA repository for book persistence
     */
    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        log.info("BookServiceImpl initialized with JPA repository");
    }

    // //
    // ============================================================================
    // // CREATE Operations
    // //
    // ============================================================================
    //
    // /**
    // * Adds a new book to the catalog and creates corresponding inventory record.
    // * <p>
    // * Transaction Flow:
    // * <p>
    // * Validate book data
    // * Save book to repository
    // * Create inventory record via InventoryService
    // * Return enriched book response with stock information
    // * <p>
    // * <p>
    // * Integration Point: Calls InventoryService.createInventory()
    // *
    // * @param request the book creation request with initial stock quantity
    // * @return BookResponseDTO with book details and stock information
    // * @throws InvalidBookDataException if validation fails
    // * @throws DuplicateBookException if book ID already exists
    // */
    //// @Override
    //// public BookResponseDTO addBook(AddBookRequestDTO request) {
    //// log.info("Adding new book: '{}' by author ID: {}",
    //// request.getBookTitle(), request.getBookAuthorId());
    ////
    //// // Step 1: Validate input data
    //// validateCreate(request);
    //// log.debug("Validation passed for book: {}", request.getBookTitle());
    ////
    //// // Step 2: Normalize category to canonical ID
    //// String canonicalCategoryId =
    // CategoryEnum.fromId(request.getBookCategoryId()).getId();
    //// log.debug("Category normalized to: {}", canonicalCategoryId);
    ////
    //// // Step 3: Check for duplicate book ID
    //// if (request.getBookId() != null && request.getBookId() > 0) {
    //// Optional<Book> existing = bookRepository.findById(request.getBookId());
    //// if (existing.isPresent()) {
    //// log.error("Duplicate book ID detected: {}", request.getBookId());
    //// throw new DuplicateBookException(request.getBookId().intValue());
    //// }
    //// }
    ////
    //// // Step 4: Create and save Book entity (without stock quantity)
    //// Book book = new Book();
    //// book.setBookId(request.getBookId());
    //// book.setBookTitle(request.getBookTitle().trim());
    //// book.setBookAuthorId(request.getBookAuthorId().trim());
    //// book.setBookCategoryId(canonicalCategoryId);
    //// book.setBookPrice(request.getBookPrice());
    ////
    //// Book savedBook = bookRepository.save(book);
    //// log.info("Book saved successfully with ID: {}", savedBook.getBookId());
    ////
    //// // Step 5: Create Inventory record via service-to-service call
    //// // This follows the Single Responsibility Principle - Inventory service
    //// // manages all stock-related operations
    //// try {
    //// InventoryCreateDTO inventoryCreateDTO = InventoryCreateDTO.builder()
    //// .bookId(savedBook.getBookId())
    //// .quantity(request.getBookStockQuantity())
    //// .lowStockThreshold(10) // Default threshold, can be made configurable
    //// .build();
    ////
    //// InventoryResponseDTO inventoryResponse =
    // inventoryService.createInventory(inventoryCreateDTO);
    //// log.info("Inventory created successfully with ID: {} for book ID: {}",
    //// inventoryResponse.getInventoryId(), savedBook.getBookId());
    ////
    //// // Return response with stock information from inventory
    //// return toResponseDTOWithStock(savedBook, inventoryResponse.getQuantity());
    ////
    //// } catch (Exception e) {
    //// log.error("Failed to create inventory for book ID: {}. Error: {}",
    //// savedBook.getBookId(), e.getMessage(), e);
    //// // Return book response with zero stock as fallback
    //// // In production, you might want to implement compensating transaction
    //// return toResponseDTOWithStock(savedBook, 0L);
    //// }
    // }
    //
    // //
    // ============================================================================
    // // READ Operations
    // //
    // ============================================================================
    //
    // /**
    // * Retrieves all books with their current stock levels.
    // *
    // * Integration Point: Enriches each book with inventory data
    // *
    // * @return List of books with stock information
    // */
    //// @Override
    //// public List<BookResponseDTO> getBooksAll () {
    //// log.info("Fetching all books with stock information");
    ////
    //// List<Book> books = bookRepository.findAll();
    //// log.debug("Retrieved {} books from repository", books.size());
    ////
    //// // Enrich each book with stock information
    //// return books.stream()
    //// .map(this::toResponseDTOWithInventoryLookup)
    //// .toList();
    //// }
    //
    // /**
    // * Retrieves a specific book by ID with stock information.
    // *
    // * @param bookId the unique identifier of the book
    // * @return Optional containing the book with stock info if found
    // */
    //// @Override
    //// public Optional<BookResponseDTO> getBookById ( long bookId){
    //// log.info("Fetching book by ID: {}", bookId);
    ////
    //// return bookRepository.findById(bookId)
    //// .map(this::toResponseDTOWithInventoryLookup);
    //// }
    //
    // /**
    // * Retrieves books by author with stock information.
    // *
    // * @param authorId the author identifier
    // * @return List of books by the specified author with stock info
    // */
    //// @Override
    //// public List<BookResponseDTO> getBooksByAuthor (String authorId){
    //// log.info("Fetching books by author ID: {}", authorId);
    ////
    //// return bookRepository.findByAuthorId(normalize(authorId)).stream()
    //// .map(this::toResponseDTOWithInventoryLookup)
    //// .toList();
    //// }
    //
    // /**
    // * Retrieves books by category with stock information.
    // *
    // * @param categoryId the category identifier
    // * @return List of books in the specified category with stock info
    // */
    //// @Override
    //// public List<BookResponseDTO> getBooksByCategory (String categoryId){
    //// log.info("Fetching books by category ID: {}", categoryId);
    ////
    //// String canonical = CategoryEnum.fromId(categoryId).getId();
    //// return bookRepository.findByCategoryId(canonical).stream()
    //// .map(this::toResponseDTOWithInventoryLookup)
    //// .toList();
    //// }
    //
    // /**
    // * Searches books by title with stock information.
    // *
    // * @param titlePart partial or complete title to search
    // * @return List of matching books with stock info
    // */
    //// @Override
    //// public List<BookResponseDTO> searchBooksByTitle (String titlePart){
    //// log.info("Searching books by title: {}", titlePart);
    ////
    //// String q = titlePart == null ? "" : titlePart.trim();
    //// return bookRepository.searchByTitle(q).stream()
    //// .map(this::toResponseDTOWithInventoryLookup)
    //// .toList();
    //// }
    //
    /**
     * Retrieves book prices for multiple books.
     * Fetches prices for a list of book IDs.
     * This method is designed for microservice communication.
     */
    @Override
    public BookPriceResponseDTO getBookPricesMap(List<Long> bookIds) {
        log.info("Microservice Request: Fetching prices for {} book IDs", bookIds.size());

        Map<Long, Double> priceMap = new LinkedHashMap<>();

        for (Long id : bookIds) {
            // Finding book or throwing exception for the GlobalExceptionHandler to catch
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Price fetch failed: Book ID {} not found in database", id);
                        return new BookNotFoundException("Book not found with ID: " + id);
                    });

            priceMap.put(book.getBookId(), book.getBookPrice());
        }

        log.info("Successfully retrieved prices for all {} requested books", priceMap.size());
        return new BookPriceResponseDTO(priceMap);
    }

    // //
    // ============================================================================
    // // UPDATE Operations
    // //
    // ============================================================================
    //
    // /**
    // * Updates book information.
    // *
    // * Note: Stock quantity updates should be done via Inventory endpoints
    // * This follows the Single Responsibility Principle - book service manages
    // * book metadata, inventory service manages stock levels.
    // *
    // * @param bookId the book ID to update
    // * @param request the update data
    // * @return Updated book information
    // * @throws BookNotFoundException if book doesn't exist
    // */
    // // @Override
    // // public BookResponseDTO updateBook ( long bookId, UpdateBookRequestDTO
    // request){
    //// log.info("Updating book ID: {}", bookId);
    ////
    //// Book existing = bookRepository.findById(bookId)
    //// .orElseThrow(() -> {
    //// log.error("Book not found with ID: {}", bookId);
    //// return new BookNotFoundException((int) bookId);
    //// });
    ////
    //// // Update only provided fields (partial update pattern)
    //// boolean isUpdated = false;
    ////
    //// if (request.getBookTitle() != null) {
    //// String t = request.getBookTitle().trim();
    //// if (t.isBlank()) {
    //// throw new InvalidBookDataException("Book title cannot be blank.");
    //// }
    //// existing.setBookTitle(t);
    //// isUpdated = true;
    //// log.debug("Updated title to: {}", t);
    //// }
    ////
    //// if (request.getBookAuthorId() != null) {
    //// String a = request.getBookAuthorId().trim();
    //// if (a.isBlank()) {
    //// throw new InvalidBookDataException("Author ID cannot be blank.");
    //// }
    //// existing.setBookAuthorId(a);
    //// isUpdated = true;
    //// log.debug("Updated author ID to: {}", a);
    //// }
    ////
    //// if (request.getBookCategoryId() != null) {
    //// String canonical =
    // CategoryEnum.fromId(request.getBookCategoryId()).getId();
    //// existing.setBookCategoryId(canonical);
    //// isUpdated = true;
    //// log.debug("Updated category to: {}", canonical);
    //// }
    ////
    //// if (request.getBookPrice() != null) {
    //// if (request.getBookPrice() < 0) {
    //// throw new InvalidBookDataException("Price cannot be negative.");
    //// }
    //// existing.setBookPrice(request.getBookPrice());
    //// isUpdated = true;
    //// log.debug("Updated price to: {}", request.getBookPrice());
    //// }
    ////
    //// // Handle stock quantity update via Inventory service
    //// if (request.getBookStockQuantity() != null) {
    //// if (request.getBookStockQuantity() < 0) {
    //// throw new InvalidBookDataException("Stock quantity cannot be negative.");
    //// }
    ////
    //// try {
    //// // Delegate stock update to Inventory service
    //// inventoryService.updateInventoryQuantity(
    //// bookId,
    //// com.book.management.inventory.dto.InventoryUpdateDTO.builder()
    //// .quantity(request.getBookStockQuantity().intValue())
    //// .build()
    //// );
    //// log.info("Stock quantity updated via Inventory service for book ID: {}",
    // bookId);
    //// } catch (Exception e) {
    //// log.warn("Failed to update inventory for book ID: {}. Error: {}",
    //// bookId, e.getMessage());
    //// // Continue with book update even if inventory update fails
    //// // In production, implement proper error handling strategy
    //// }
    //// }
    ////
    //// // Save book updates if any changes were made
    //// if (isUpdated) {
    //// Book updated = bookRepository.update(existing);
    //// log.info("Book ID: {} updated successfully", bookId);
    //// return toResponseDTOWithInventoryLookup(updated);
    //// }
    ////
    //// log.debug("No changes detected for book ID: {}", bookId);
    //// return toResponseDTOWithInventoryLookup(existing);
    // // }
    //
    // //
    // ============================================================================
    // // DELETE Operations
    // //
    // ============================================================================
    //
    // /**
    // * Deletes a book from the catalog.
    // *
    // * Note: This only deletes the book record. The corresponding
    // * inventory record should be deleted separately via Inventory service
    // endpoints.
    // * This maintains loose coupling between services.
    // *
    // * Production Consideration: Implement cascade deletion or
    // * soft delete pattern based on business requirements.
    // *
    // * @param bookId the book ID to delete
    // */
    //// @Override
    //// public void deleteBook ( long bookId){
    //// log.info("Deleting book ID: {}", bookId);
    ////
    //// bookRepository.deleteById(bookId);
    ////
    //// // Note: Inventory deletion should be handled separately
    //// // This maintains loose coupling and allows for different deletion policies
    //// log.warn("Book deleted. Remember to clean up inventory via Inventory API if
    // needed");
    //// }
    //
    // //
    // ============================================================================
    // // Private Helper Methods - Mapping and Validation
    // //
    // ============================================================================
    //
    // /**
    // * Converts Book entity to BookResponseDTO with inventory lookup.
    // *
    // * Integration Pattern: Service-to-Service call for each book
    // * Performance Note: For bulk operations, consider batch inventory lookup
    // *
    // * @param book the book entity
    // * @return BookResponseDTO enriched with stock information
    // */
    //// private BookResponseDTO toResponseDTOWithInventoryLookup(Book book) {
    //// long stockQuantity = 0L;
    ////
    //// try {
    //// // Service-to-service call to get current stock
    //// InventoryResponseDTO inventory =
    // inventoryService.getInventoryByBookId(book.getBookId());
    //// stockQuantity = inventory.getQuantity();
    //// log.trace("Retrieved stock quantity {} for book ID: {}", stockQuantity,
    // book.getBookId());
    ////
    //// } catch (Exception e) {
    //// // Graceful degradation: if inventory lookup fails, return 0 stock
    //// log.warn("Failed to retrieve inventory for book ID: {}. Defaulting to 0.
    // Error: {}",
    //// book.getBookId(), e.getMessage());
    //// }
    ////
    //// return toResponseDTOWithStock(book, stockQuantity);
    //// }
    //
    // /**
    // * Converts Book entity to BookResponseDTO with provided stock quantity.
    // *
    // * @param book the book entity
    // * @param stockQuantity the stock quantity to include in response
    // * @return BookResponseDTO with all fields populated
    // */
    //// private BookResponseDTO toResponseDTOWithStock (Book book,long
    // stockQuantity){
    //// BookResponseDTO dto = new BookResponseDTO();
    //// dto.setBookId(book.getBookId());
    //// dto.setBookTitle(book.getBookTitle());
    //// dto.setBookAuthorId(book.getBookAuthorId());
    //// dto.setBookCategoryId(book.getBookCategoryId());
    //// dto.setBookPrice(book.getBookPrice());
    //// dto.setBookStockQuantity(stockQuantity);
    //// return dto;
    //// }
    //
    // /**
    // * Validates book creation request.
    // *
    // * Follows the Fail-Fast principle for input validation.
    // *
    // * @param request the request to validate
    // * @throws InvalidBookDataException if validation fails
    // */
    //// private void validateCreate(AddBookRequestDTO request) {
    //// if (request.getBookTitle() == null ||
    // request.getBookTitle().trim().isBlank()) {
    //// throw new InvalidBookDataException("Book title is required.");
    //// }
    //// if (request.getBookAuthorId() == null ||
    // request.getBookAuthorId().trim().isBlank()) {
    //// throw new InvalidBookDataException("Author ID is required.");
    //// }
    //// if (request.getBookCategoryId() == null ||
    // request.getBookCategoryId().trim().isBlank()) {
    //// throw new InvalidBookDataException("Category ID is required.");
    //// }
    //// if (request.getBookPrice() == null || request.getBookPrice() < 0) {
    //// throw new InvalidBookDataException("Price must be a non-negative number.");
    //// }
    //// if (request.getBookStockQuantity() == null ||
    // request.getBookStockQuantity() < 0) {
    //// throw new InvalidBookDataException("Stock quantity must be a non-negative
    // number.");
    //// }
    //// }
    //
    // /**
    // * Normalizes string input for consistent data handling.
    // *
    // * @param s the string to normalize
    // * @return normalized string (empty string if null)
    // // */
    //// private String normalize(String s) {
    //// return (s == null) ? "" : s.trim();
    //// }
    // }
}