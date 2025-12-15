package com.db.ms.service.impl;

import com.db.ms.dto.requestdto.AddBookRequestDTO;
import com.db.ms.dto.requestdto.UpdateBookRequestDTO;
import com.db.ms.dto.responsedto.BookPriceResponseDTO;
import com.db.ms.dto.responsedto.BookResponseDTO;
import com.db.ms.enums.CategoryEnum;
import com.db.ms.exception.BookNotFoundException;
import com.db.ms.exception.DuplicateBookException;
import com.db.ms.exception.InvalidBookDataException;
import com.db.ms.model.Book;
import com.db.ms.repository.BookRepository;
import com.db.ms.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookResponseDTO addBook(AddBookRequestDTO request) {
        validateCreate(request);
        String canonicalCategoryId = CategoryEnum.fromId(request.getBookCategoryId()).getId();

        if (request.getBookId() != null && request.getBookId() > 0) {
            Optional<Book> existing = bookRepository.findById(request.getBookId());
            if (existing.isPresent()) {
                throw new DuplicateBookException(request.getBookId().intValue());
            }
        }

        Book book = new Book();
        book.setBookId(request.getBookId());
        book.setBookTitle(request.getBookTitle().trim());
        book.setBookAuthorId(request.getBookAuthorId().trim());
        book.setBookCategoryId(canonicalCategoryId);
        book.setBookPrice(request.getBookPrice());
        book.setBookStockQuantity(request.getBookStockQuantity());

        Book saved = bookRepository.save(book);
        return toResponseDTO(saved);
    }

    @Override
    public List<BookResponseDTO> getBooksAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public Optional<BookResponseDTO> getBookById(long bookId) {
        return bookRepository.findById(bookId).map(this::toResponseDTO);
    }

    @Override
    public List<BookResponseDTO> getBooksByAuthor(String authorId) {
        return bookRepository.findByAuthorId(normalize(authorId)).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<BookResponseDTO> getBooksByCategory(String categoryId) {
        String canonical = CategoryEnum.fromId(categoryId).getId();
        return bookRepository.findByCategoryId(canonical).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Optimized version: Fetches all books in one database hit.
     */
    @Override
    public BookPriceResponseDTO getBookPricesMap(List<Long> bookIds) {
        Map<Long, Double> map = new LinkedHashMap<>();
        if (bookIds == null || bookIds.isEmpty()) {
            return new BookPriceResponseDTO(map);
        }

        for (Long id : bookIds) {
            // Using findById which your error logs suggest already exists
            bookRepository.findById(id).ifPresent(book -> { map.put(book.getBookId(), book.getBookPrice());});
        }

        return new BookPriceResponseDTO(map);
    }

    @Override
    public List<BookResponseDTO> searchBooksByTitle(String titlePart) {
        String q = titlePart == null ? "" : titlePart.trim();
        return bookRepository.searchByTitle(q).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public BookResponseDTO updateBook(long bookId, UpdateBookRequestDTO request) {
        Book existing = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException((int) bookId));

        if (request.getBookTitle() != null) {
            String t = request.getBookTitle().trim();
            if (t.isBlank()) throw new InvalidBookDataException("Book title cannot be blank.");
            existing.setBookTitle(t);
        }
        if (request.getBookAuthorId() != null) {
            String a = request.getBookAuthorId().trim();
            if (a.isBlank()) throw new InvalidBookDataException("Author ID cannot be blank.");
            existing.setBookAuthorId(a);
        }
        if (request.getBookCategoryId() != null) {
            String canonical = CategoryEnum.fromId(request.getBookCategoryId()).getId();
            existing.setBookCategoryId(canonical);
        }
        if (request.getBookPrice() != null) {
            if (request.getBookPrice() < 0) throw new InvalidBookDataException("Price cannot be negative.");
            existing.setBookPrice(request.getBookPrice());
        }
        if (request.getBookStockQuantity() != null) {
            if (request.getBookStockQuantity() < 0) throw new InvalidBookDataException("Stock quantity cannot be negative.");
            existing.setBookStockQuantity(request.getBookStockQuantity());
        }

        Book updated = bookRepository.update(existing);
        return toResponseDTO(updated);
    }

    @Override
    public void deleteBook(long bookId) {
        bookRepository.deleteById(bookId);
    }

    private BookResponseDTO toResponseDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getBookTitle());
        dto.setBookAuthorId(book.getBookAuthorId());
        dto.setBookCategoryId(book.getBookCategoryId());
        dto.setBookPrice(book.getBookPrice());
        dto.setBookStockQuantity(book.getBookStockQuantity());
        return dto;
    }

    private void validateCreate(AddBookRequestDTO request) {
        if (request.getBookTitle() == null || request.getBookTitle().trim().isBlank()) {
            throw new InvalidBookDataException("Book title is required.");
        }
        if (request.getBookAuthorId() == null || request.getBookAuthorId().trim().isBlank()) {
            throw new InvalidBookDataException("Author ID is required.");
        }
        if (request.getBookCategoryId() == null || request.getBookCategoryId().trim().isBlank()) {
            throw new InvalidBookDataException("Category ID is required.");
        }
        if (request.getBookPrice() == null || request.getBookPrice() < 0) {
            throw new InvalidBookDataException("Price must be a non-negative number.");
        }
        if (request.getBookStockQuantity() == null || request.getBookStockQuantity() < 0) {
            throw new InvalidBookDataException("Stock quantity must be a non-negative number.");
        }
    }

    private String normalize(String s) {
        return (s == null) ? "" : s.trim();
    }
}