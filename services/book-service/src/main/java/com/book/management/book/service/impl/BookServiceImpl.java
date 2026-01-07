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
import com.book.management.book.exception.InvalidCategoryException;
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
        try {
            List<Book> books = bookRepository.findAll();
            List<BookResponseDTO> responseList = new ArrayList<>();

            for (Book book : books) {
                responseList.add(toResponseDTOWithInventoryLookup(book));
            }
            return responseList;
        } catch (Exception e) {
            log.error("Error fetching all books: {}", e.getMessage());
            throw new InvalidBookDataException("Error fetching all books: " + e.getMessage());
        }
    }

    @Override
    public List<BookResponseDTO> getBooksByAuthor(String authorId) {
        try {
            if (authorId == null || authorId.isBlank()) {
                throw new InvalidBookDataException("Author ID cannot be null or empty");
            }
            
            List<Book> books = bookRepository.findByBookAuthorId(authorId.trim());
            List<BookResponseDTO> responseList = new ArrayList<>();

            for (Book book : books) {
                responseList.add(toResponseDTOWithInventoryLookup(book));
            }
            return responseList;
        } catch (InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching books by author: {}", e.getMessage());
            throw new InvalidBookDataException("Error fetching books by author: " + e.getMessage());
        }
    }

    @Override
    public List<BookResponseDTO> getBooksByCategory(String categoryId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId.isBlank()) {
                throw new InvalidCategoryException("Category ID cannot be null or empty");
            }
            
            // Validate if category ID is a valid category
            String normalizedCategoryId = categoryId.trim().toUpperCase();
            CategoryEnum category = CategoryEnum.fromId(normalizedCategoryId);
            
            // If the category resolves to OTHER but the input wasn't CAT-OTH, it's invalid
            if (category == CategoryEnum.OTHER && !normalizedCategoryId.equals("CAT-OTH")) {
                throw new InvalidCategoryException(categoryId);
            }
            
            List<Book> books = bookRepository.findByBookCategoryId(category.getId());
            List<BookResponseDTO> responseList = new ArrayList<>();

            for (Book book : books) {
                responseList.add(toResponseDTOWithInventoryLookup(book));
            }
            return responseList;
        } catch (InvalidCategoryException e) {
            log.error("Invalid category ID provided: {}", categoryId);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching books by category: {}", e.getMessage());
            throw new InvalidBookDataException("Error fetching books by category: " + e.getMessage());
        }
    }

    @Override
    public List<BookResponseDTO> searchBooksByTitle(String title) {
        try {
            if (title == null || title.isBlank()) {
                throw new InvalidBookDataException("Search title cannot be null or empty");
            }
            
            // 1. Change the type from Optional<Object> to List<Book>
            List<Book> books = bookRepository.findByBookTitleContainingIgnoreCase(title.trim());

            List<BookResponseDTO> responseList = new ArrayList<>();

            // 2. Now the for-each loop will work perfectly
            for (Book book : books) {
                responseList.add(toResponseDTOWithInventoryLookup(book));
            }

            return responseList;
        } catch (InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error searching books by title: {}", e.getMessage());
            throw new InvalidBookDataException("Error searching books by title: " + e.getMessage());
        }
    }

    @Override
    public BookResponseDTO getBookById(long bookId) {
        try {
            if (bookId <= 0) {
                throw new InvalidBookDataException("Book ID must be a positive number");
            }
            
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isEmpty()) {
                log.error("Book not found with ID: {}", bookId);
                throw new BookNotFoundException((int) bookId);
            }
            
            BookResponseDTO dto = toResponseDTOWithInventoryLookup(bookOpt.get());
            return dto;
        } catch (BookNotFoundException e) {
            throw e;
        } catch (InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching book by ID {}: {}", bookId, e.getMessage());
            throw new InvalidBookDataException("Error fetching book: " + e.getMessage());
        }
    }

    @Override
    public BookResponseDTO addBook(AddBookRequestDTO request) {
        try {
            log.info("Adding new book: '{}'", request.getBookTitle());
            validateCreate(request);

            // Validate category ID
            CategoryEnum category = CategoryEnum.fromId(request.getBookCategoryId());
            if (category == CategoryEnum.OTHER && 
                !request.getBookCategoryId().trim().toUpperCase().equals("CAT-OTH")) {
                throw new InvalidCategoryException(request.getBookCategoryId());
            }
            String canonicalCategoryId = category.getId();

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
        } catch (InvalidCategoryException | DuplicateBookException | InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding book: {}", e.getMessage());
            throw new InvalidBookDataException("Error adding book: " + e.getMessage());
        }
    }

    @Override
    public BookResponseDTO updateBook(long bookId, UpdateBookRequestDTO request) {
        try {
            if (bookId <= 0) {
                throw new InvalidBookDataException("Book ID must be a positive number");
            }
            
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
                if (request.getBookPrice() < 0) {
                    throw new InvalidBookDataException("Price cannot be negative");
                }
                existing.setBookPrice(request.getBookPrice());
                isUpdated = true;
            }

            if (isUpdated) {
                existing = bookRepository.save(existing);
            }

            return toResponseDTOWithInventoryLookup(existing);
        } catch (BookNotFoundException | InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating book {}: {}", bookId, e.getMessage());
            throw new InvalidBookDataException("Error updating book: " + e.getMessage());
        }
    }

    @Override
    public BookPriceResponseDTO getBookPricesMap(List<Long> bookIds) {
        try {
            if (bookIds == null || bookIds.isEmpty()) {
                throw new InvalidBookDataException("Book IDs list cannot be null or empty");
            }
            
            Map<Long, Double> prices = new LinkedHashMap<>();
            for (Long id : bookIds) {
                if (id == null || id <= 0) {
                    throw new InvalidBookDataException("Invalid book ID in list: " + id);
                }
                Optional<Book> bOpt = bookRepository.findById(id);
                if (bOpt.isEmpty()) {
                    throw new BookNotFoundException("Book ID not found: " + id);
                }
                prices.put(bOpt.get().getBookId(), bOpt.get().getBookPrice());
            }
            return new BookPriceResponseDTO(prices);
        } catch (BookNotFoundException | InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching book prices: {}", e.getMessage());
            throw new InvalidBookDataException("Error fetching book prices: " + e.getMessage());
        }
    }

    @Override
    public void deleteBook(long bookId) {
        try {
            log.info("Attempting to delete book with ID: {}", bookId);

            if (bookId <= 0) {
                throw new InvalidBookDataException("Book ID must be a positive number");
            }

            // 1. Check if the book exists before attempting deletion
            if (!bookRepository.existsById(bookId)) {
                throw new BookNotFoundException("Cannot delete. Book not found with ID: " + bookId);
            }

            // 2. Delete the book from the local database
            bookRepository.deleteById(bookId);
            log.info("Book {} deleted from Book Repository", bookId);

            // 3. Sync with Inventory Service (External Call)
            try {
                log.info("Notifying Inventory Service to delete records for book: {}", bookId);
                inventoryClient.deleteInventoryByBookId(bookId);
            } catch (Exception e) {
                // We log the error but don't throw an exception.
                // This ensures the book stays deleted locally even if the inventory cleanup fails.
                log.error("Failed to delete inventory for book {}. Error: {}", bookId, e.getMessage());
            }
        } catch (BookNotFoundException | InvalidBookDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting book {}: {}", bookId, e.getMessage());
            throw new InvalidBookDataException("Error deleting book: " + e.getMessage());
        }
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