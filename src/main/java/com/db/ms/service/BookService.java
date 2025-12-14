
package com.db.ms.service;

import com.db.ms.dto.requestdto.AddBookRequestDTO;
import com.db.ms.dto.requestdto.UpdateBookRequestDTO;
import com.db.ms.dto.responsedto.BookPriceResponseDTO;
import com.db.ms.dto.responsedto.BookResponseDTO;

import java.util.List;
import java.util.Optional;

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

    // Delete
    void deleteBook(long bookId);
}
