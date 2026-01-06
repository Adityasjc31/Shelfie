package com.book.management.book.service;

import java.util.List;
import java.util.Optional;

import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.BookRatingUpdateRequest;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;

/**
 * Service interface for Book Catalog Management.
 * 
 * Per LLD Section 4.1 - Book Catalog Management Module:
 * - Manage books, authors, categories, and pricing.
 * - Allow bookstore admins to add, update, and remove books.
 * - Book entity: BookID, Title, AuthorID, CategoryID, Price, StockQuantity.
 *
 * @author Shashwat Srivastava
 * @version 2.0
 */
public interface BookService {

    // Create
    BookResponseDTO addBook(AddBookRequestDTO request);

    // Read
    List<BookResponseDTO> getBooksAll();

    Optional<BookResponseDTO> getBookById(long bookId);

    List<BookResponseDTO> getBooksByAuthor(String authorId);

    List<BookResponseDTO> getBooksByCategory(String categoryId);

    List<BookResponseDTO> searchBooksByTitle(String titlePart);

    BookPriceResponseDTO getBookPricesMap(List<Long> bookIds);

    // Update
    BookResponseDTO updateBook(long bookId, UpdateBookRequestDTO request);

    /**
     * Updates book rating statistics.
     * Called by Review Service to sync rating data.
     * 
     * Per LLD Section 4.5 - Review & Rating Module:
     * - Supports customer reviews and ratings.
     * - Rating data is pushed from Review Service to break circular dependency.
     * 
     * @param bookId the BookID to update
     * @param request the rating update containing average rating and total reviews
     * @return updated book response
     */
    BookResponseDTO updateBookRating(long bookId, BookRatingUpdateRequest request);

    /**
     * Checks if a book exists by BookID.
     * Lightweight validation for Review Service.
     * 
     * @param bookId the BookID to check
     * @return true if book exists
     */
    boolean existsById(long bookId);

    // Delete
    void deleteBook(long bookId);
}