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
        // Validate authorId
        if (authorId == null || authorId.isBlank()) {
            throw new InvalidBookDataException("Author ID is required and cannot be blank");
        }
        if (authorId.trim().length() < 2) {
            throw new InvalidBookDataException("Author ID must be at least 2 characters long");
        }
        
        List<Book> books = bookRepository.findByBookAuthorId(authorId.trim());
        
        // Throw exception if no books found for the author
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books found for author ID: " + authorId.trim());
        }
        
        List<BookResponseDTO> responseList = new ArrayList<>();

        for (Book book : books) {
            responseList.add(toResponseDTOWithInventoryLookup(book));
        }
        return responseList;
    }

    @Override
    public List<BookResponseDTO> getBooksByCategory(String categoryId) {
        // Validate categoryId
        if (categoryId == null || categoryId.isBlank()) {
            throw new InvalidBookDataException("Category ID is required and cannot be blank");
        }
        // Validate category is a valid enum value
        if (!isValidCategoryId(categoryId)) {
            throw new InvalidBookDataException("Invalid category ID: '" + categoryId + "'. " + getValidCategoriesMessage());
        }
        
        List<Book> books = bookRepository.findByBookCategoryId(categoryId);
        
        // Throw exception if no books found for the category
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books found for category: " + getCategoryDisplayName(categoryId));
        }
        
        List<BookResponseDTO> responseList = new ArrayList<>();

        for (Book book : books) {
            responseList.add(toResponseDTOWithInventoryLookup(book));
        }
        return responseList;
    }

    @Override
    public List<BookResponseDTO> searchBooksByTitle(String title) {
        // Validate title
        if (title == null || title.isBlank()) {
            throw new InvalidBookDataException("Title search term is required and cannot be blank");
        }
        if (title.trim().length() < 1) {
            throw new InvalidBookDataException("Title search term must be at least 1 character long");
        }
        
        // 1. Change the type from Optional<Object> to List<Book>
        List<Book> books = bookRepository.findByBookTitleContainingIgnoreCase(title.trim());

        // Throw exception if no books found with the title
        if (books.isEmpty()) {
            throw new BookNotFoundException("No books found matching title: '" + title.trim() + "'");
        }
        
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
        log.info("Attempting to delete book with ID: {}", bookId);

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
        // Validate title
        if (req.getBookTitle() == null || req.getBookTitle().isBlank()) {
            throw new InvalidBookDataException("Book title is required and cannot be blank");
        }
        if (req.getBookTitle().trim().length() < 2) {
            throw new InvalidBookDataException("Book title must be at least 2 characters long");
        }
        if (req.getBookTitle().trim().length() > 255) {
            throw new InvalidBookDataException("Book title cannot exceed 255 characters");
        }
        
        // Validate author
        if (req.getBookAuthorId() == null || req.getBookAuthorId().isBlank()) {
            throw new InvalidBookDataException("Author ID is required and cannot be blank");
        }
        if (req.getBookAuthorId().trim().length() < 2) {
            throw new InvalidBookDataException("Author ID must be at least 2 characters long");
        }
        
        // Validate category
        if (req.getBookCategoryId() == null || req.getBookCategoryId().isBlank()) {
            throw new InvalidBookDataException("Category ID is required and cannot be blank");
        }
        // Validate that category exists (not defaulting to OTHER for invalid input)
        if (!isValidCategoryId(req.getBookCategoryId())) {
            throw new InvalidBookDataException("Invalid category ID: '" + req.getBookCategoryId() + "'. " + getValidCategoriesMessage());
        }
        
        // Validate price
        if (req.getBookPrice() == null) {
            throw new InvalidBookDataException("Book price is required");
        }
        if (req.getBookPrice() < 0) {
            throw new InvalidBookDataException("Book price cannot be negative");
        }
        if (req.getBookPrice() > 10000) {
            throw new InvalidBookDataException("Book price cannot exceed 10000");
        }
        
        // Validate stock
        if (req.getBookStockQuantity() == null) {
            throw new InvalidBookDataException("Stock quantity is required");
        }
        if (req.getBookStockQuantity() < 0) {
            throw new InvalidBookDataException("Stock quantity cannot be negative");
        }
    }
    
    private boolean isValidCategoryId(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) return false;
        String normalized = categoryId.trim().toUpperCase();
        for (CategoryEnum c : CategoryEnum.values()) {
            if (c.getId().equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }
    
    private String getValidCategoriesMessage() {
        StringBuilder sb = new StringBuilder("Valid categories are: ");
        CategoryEnum[] categories = CategoryEnum.values();
        for (int i = 0; i < categories.length; i++) {
            CategoryEnum c = categories[i];
            sb.append(c.name().replace("_", " ")).append(" (").append(c.getId()).append(")");
            if (i < categories.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    private String getCategoryDisplayName(String categoryId) {
        for (CategoryEnum c : CategoryEnum.values()) {
            if (c.getId().equalsIgnoreCase(categoryId.trim())) {
                return c.name().replace("_", " ") + " (" + c.getId() + ")";
            }
        }
        return categoryId;
    }
}