package com.book.management.book.service.impl;

import com.book.management.book.client.InventoryClient;
import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.InventoryCreateDTO;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.dto.responsedto.InventoryResponseDTO;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final InventoryClient inventoryClient;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, InventoryClient inventoryClient) {
        this.bookRepository = bookRepository;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public List<BookResponseDTO> getBooksAll() {
        List<Book> books = bookRepository.findAll();
        List<BookResponseDTO> responseList = new ArrayList<>();

        for (Book book : books) {
            responseList.add(toResponseDTOWithInventoryLookup(book));
        }
        return responseList;
    }

    @Override
    public List<BookResponseDTO> getBooksByAuthor(String authorId) {
        List<Book> books = bookRepository.findByBookAuthorId(authorId.trim());
        List<BookResponseDTO> responseList = new ArrayList<>();

        for (Book book : books) {
            responseList.add(toResponseDTOWithInventoryLookup(book));
        }
        return responseList;
    }

    @Override
    public List<BookResponseDTO> getBooksByCategory(String categoryId) {
        List<Book> books = bookRepository.findByBookCategoryId(categoryId);
        List<BookResponseDTO> responseList = new ArrayList<>();

        for (Book book : books) {
            responseList.add(toResponseDTOWithInventoryLookup(book));
        }
        return responseList;
    }

    @Override
    public List<BookResponseDTO> searchBooksByTitle(String title) {
        // 1. Change the type from Optional<Object> to List<Book>
        List<Book> books = bookRepository.findByBookTitleContainingIgnoreCase(title.trim());

        List<BookResponseDTO> responseList = new ArrayList<>();

        // 2. Now the for-each loop will work perfectly
        for (Book book : books) {
            responseList.add(toResponseDTOWithInventoryLookup(book));
        }

        return responseList;
    }

    @Override
    public Optional<BookResponseDTO> getBookById(long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            BookResponseDTO dto = toResponseDTOWithInventoryLookup(bookOpt.get());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public BookResponseDTO addBook(AddBookRequestDTO request) {
        log.info("Adding new book: '{}'", request.getBookTitle());
        validateCreate(request);

        String canonicalCategoryId = CategoryEnum.fromId(request.getBookCategoryId()).getId();

        // 1. Check for duplicates ONLY if an ID is provided
        if (request.getBookId() != null) {
            if (bookRepository.existsById(request.getBookId())) {
                throw new DuplicateBookException(request.getBookId().intValue());
            }
        }

        // 2. Build Entity
        Book book = new Book();
        // Only set ID if provided, otherwise let @GeneratedValue do its job
        if (request.getBookId() != null) {
            book.setBookId(request.getBookId());
        }

        book.setBookTitle(request.getBookTitle().trim());
        book.setBookAuthorId(request.getBookAuthorId().trim());
        book.setBookCategoryId(canonicalCategoryId);
        book.setBookPrice(request.getBookPrice());

        // 3. Save to DB (This generates the ID for savedBook)
        Book savedBook = bookRepository.save(book);

        try {
            // 4. Handle Stock Null-Safety and use InventoryCreateDTO
            Integer stockQuantity = (request.getBookStockQuantity() != null)
                    ? request.getBookStockQuantity()
                    : 0;

            // Using your provided InventoryCreateDTO from com.book.management.book.dto.requestdto
            InventoryCreateDTO invRequest = InventoryCreateDTO.builder()
                    .bookId(savedBook.getBookId())
                    .quantity(stockQuantity)
                    .lowStockThreshold(5) // Threshold set to 5 as requested
                    .build();

            // 5. Sync with Inventory Service via Feign Client
            // This returns your provided InventoryResponseDTO from com.book.management.book.dto.responsedto
            InventoryResponseDTO invResponse = inventoryClient.createInventory(invRequest);

            // Convert the Integer quantity from invResponse to Long for the response DTO
            Long finalQuantity = (invResponse.getQuantity() != null)
                    ? invResponse.getQuantity().longValue()
                    : 0L;

            return toResponseDTO(savedBook, finalQuantity);

        } catch (Exception e) {
            log.error("Inventory creation failed for book {}: {}", savedBook.getBookId(), e.getMessage());
            // Fallback: return book data with 0 stock if inventory service fails
            return toResponseDTO(savedBook, 0L);
        }
    }

    @Override
    public BookResponseDTO updateBook(long bookId, UpdateBookRequestDTO request) {
        Optional<Book> existingOpt = bookRepository.findById(bookId);
        if (existingOpt.isEmpty()) {
            throw new BookNotFoundException((int) bookId);
        }

        Book existing = existingOpt.get();
        boolean isUpdated = false;

        if (request.getBookTitle() != null && !request.getBookTitle().isBlank()) {
            existing.setBookTitle(request.getBookTitle().trim());
            isUpdated = true;
        }
        if (request.getBookPrice() != null) {
            if (request.getBookPrice() < 0) throw new InvalidBookDataException("Price negative");
            existing.setBookPrice(request.getBookPrice());
            isUpdated = true;
        }

        if (isUpdated) {
            existing = bookRepository.save(existing);
        }

        return toResponseDTOWithInventoryLookup(existing);
    }

    @Override
    public BookPriceResponseDTO getBookPricesMap(List<Long> bookIds) {
        Map<Long, Double> prices = new LinkedHashMap<>();
        for (Long id : bookIds) {
            Optional<Book> bOpt = bookRepository.findById(id);
            if (bOpt.isEmpty()) {
                throw new BookNotFoundException("ID not found: " + id);
            }
            prices.put(bOpt.get().getBookId(), bOpt.get().getBookPrice());
        }
        return new BookPriceResponseDTO(prices);
    }

    @Override
    public void deleteBook(long bookId) {
        bookRepository.deleteById(bookId);
    }

    // Helper Methods
    private BookResponseDTO toResponseDTOWithInventoryLookup(Book book) {
        long stock = 0L;
        try {
            InventoryResponseDTO inv = inventoryClient.getInventoryByBookId(book.getBookId());
            if (inv != null) {
                stock = inv.getQuantity();
            }
        } catch (Exception e) {
            log.warn("Feign Lookup Failed for book {}", book.getBookId());
        }
        return toResponseDTO(book, stock);
    }

    private BookResponseDTO toResponseDTO(Book book, long stock) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getBookTitle());
        dto.setBookAuthorId(book.getBookAuthorId());
        dto.setBookCategoryId(book.getBookCategoryId());
        dto.setBookPrice(book.getBookPrice());
        dto.setBookStockQuantity(stock);
        return dto;
    }

    private void validateCreate(AddBookRequestDTO req) {
        if (req.getBookTitle() == null || req.getBookTitle().isBlank()) throw new InvalidBookDataException("Title missing");
        if (req.getBookPrice() == null || req.getBookPrice() < 0) throw new InvalidBookDataException("Invalid price");
        if (req.getBookStockQuantity() == null || req.getBookStockQuantity() < 0) throw new InvalidBookDataException("Invalid stock");
    }
}