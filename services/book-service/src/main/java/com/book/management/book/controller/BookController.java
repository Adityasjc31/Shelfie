package com.book.management.book.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.BookPriceRequestDTO;
import com.book.management.book.dto.requestdto.BookRatingUpdateRequest;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.service.BookService;

import java.util.List;

/**
 * REST Controller for Book Catalog Management.
 * 
 * Per LLD Section 4.1 - Book Catalog Management Module:
 * - View, search, and filter books by category or author.
 * - Admins can manage book listings.
 * - Book entity: BookID, Title, AuthorID, CategoryID, Price, StockQuantity.
 *
 * @author Shashwat Srivastava
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/book")
@Slf4j
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    public ResponseEntity<BookResponseDTO> addBook(@RequestBody AddBookRequestDTO request) {
        BookResponseDTO created = bookService.addBook(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getBooksAll());
    }

    @GetMapping("/getById/{bookId}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable long bookId) {
        return bookService.getBookById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getByAuthor/{authorId}")
    public ResponseEntity<List<BookResponseDTO>> getBooksByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorId));
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<List<BookResponseDTO>> getBooksByCategory(@RequestParam String categoryId) {
        return ResponseEntity.ok(bookService.getBooksByCategory(categoryId));
    }

    /**
     * Endpoint to fetch a Map of Book IDs and Prices.
     * Uses POST because it accepts a RequestBody list of IDs.
     */
    @PostMapping("/bulk/prices")
    public ResponseEntity<BookPriceResponseDTO> getBookPricesMap(@Valid @RequestBody BookPriceRequestDTO request) {
        BookPriceResponseDTO dto = bookService.getBookPricesMap(request.getBookIds());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchBooksByTitle(title));
    }

    @PatchMapping("/update/{bookId}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable long bookId,
                                                      @RequestBody UpdateBookRequestDTO request) {
        BookResponseDTO updated = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates book rating statistics.
     * Called by Review Service when reviews are approved/deleted.
     * 
     * Per LLD Section 4.5 - Review & Rating Module:
     * - Allows customers to submit book reviews and ratings.
     * - This endpoint breaks the circular dependency by allowing
     *   Review Service to push rating updates.
     *
     * @param bookId the BookID to update
     * @param request the rating update request
     * @return ResponseEntity with updated book
     */
    @PutMapping("/{bookId}/rating")
    public ResponseEntity<BookResponseDTO> updateBookRating(
            @PathVariable long bookId,
            @Valid @RequestBody BookRatingUpdateRequest request) {
        log.info("PUT /api/v1/book/{}/rating - Updating rating: avg={}, total={}",
                bookId, request.getAverageRating(), request.getTotalReviews());
        return ResponseEntity.ok(bookService.updateBookRating(bookId, request));
    }

    /**
     * Checks if a book exists.
     * Lightweight endpoint used by Review Service for validation.
     * 
     * Per LLD Section 4.5 - Review entity requires valid BookID.
     *
     * @param bookId the BookID to check
     * @return true if book exists, false otherwise
     */
    @GetMapping("/{bookId}/exists")
    public ResponseEntity<Boolean> checkBookExists(@PathVariable long bookId) {
        log.debug("GET /api/v1/book/{}/exists", bookId);
        return ResponseEntity.ok(bookService.existsById(bookId));
    }
}