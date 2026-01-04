package com.book.management.book.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.BookPriceRequestDTO;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

//    @PostMapping("/add")
//    public ResponseEntity<BookResponseDTO> addBook(@RequestBody AddBookRequestDTO request) {
//        BookResponseDTO created = bookService.addBook(request);
//        return ResponseEntity.ok(created);
//    }

//    @GetMapping("/getAll")
//    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
//        return ResponseEntity.ok(bookService.getBooksAll());
//    }

//    @GetMapping("/getById/{bookId}")
//    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable long bookId) {
//        return bookService.getBookById(bookId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

//    @GetMapping("/getByAuthor/{authorId}")
//    public ResponseEntity<List<BookResponseDTO>> getBooksByAuthor(@PathVariable String authorId) {
//        return ResponseEntity.ok(bookService.getBooksByAuthor(authorId));
//    }

//    @GetMapping("/getByCategory")
//    public ResponseEntity<List<BookResponseDTO>> getBooksByCategory(@RequestParam String categoryId) {
//        return ResponseEntity.ok(bookService.getBooksByCategory(categoryId));
//    }

    /**
     * Endpoint to fetch a Map of Book IDs and Prices.
     * Uses POST because it accepts a RequestBody list of IDs.
     */
    @PostMapping("/bulk/prices")
    public ResponseEntity<BookPriceResponseDTO> getBookPricesMap(@Valid @RequestBody BookPriceRequestDTO request) {
        BookPriceResponseDTO dto = bookService.getBookPricesMap(request.getBookIds());
        return ResponseEntity.ok(dto);
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<BookResponseDTO>> searchBooksByTitle(@RequestParam String title) {
//        return ResponseEntity.ok(bookService.searchBooksByTitle(title));
//    }

//    @PatchMapping("/update/{bookId}")
//    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable long bookId,
//                                                      @RequestBody UpdateBookRequestDTO request) {
//        BookResponseDTO updated = bookService.updateBook(bookId, request);
//        return ResponseEntity.ok(updated);
//    }

//    @DeleteMapping("/delete/{bookId}")
//    public ResponseEntity<Void> deleteBook(@PathVariable long bookId) {
//        bookService.deleteBook(bookId);
//        return ResponseEntity.noContent().build();
//    }
}