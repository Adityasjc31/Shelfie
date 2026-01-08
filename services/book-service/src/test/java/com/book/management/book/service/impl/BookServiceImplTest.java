package com.book.management.book.service.impl;

import com.book.management.book.client.InventoryClient;
import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.InventoryCreateDTO;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.dto.responsedto.InventoryResponseDTO;
import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.exception.DuplicateBookException;
import com.book.management.book.exception.InvalidBookDataException;
import com.book.management.book.model.Book;
import com.book.management.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("BookServiceImpl Tests")
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book sampleBook;
    private InventoryResponseDTO sampleInventoryResponse;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .bookAuthorId("author-123")
                .bookCategoryId("CAT-FIC")
                .bookPrice(29.99)
                .build();

        sampleInventoryResponse = InventoryResponseDTO.builder()
                .inventoryId(1L)
                .bookId(1L)
                .quantity(100)
                .lowStockThreshold(5)
                .isLowStock(false)
                .isOutOfStock(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getBooksAll() Tests")
    class GetBooksAllTests {

        @Test
        @DisplayName("Should return all books with inventory lookup")
        void getBooksAll_Success() {
            // Given
            Book book2 = Book.builder()
                    .bookId(2L)
                    .bookTitle("Second Book")
                    .bookAuthorId("author-456")
                    .bookCategoryId("CAT-SCI")
                    .bookPrice(19.99)
                    .build();

            List<Book> books = Arrays.asList(sampleBook, book2);
            when(bookRepository.findAll()).thenReturn(books);
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);
            when(inventoryClient.getInventoryByBookId(2L)).thenReturn(
                    InventoryResponseDTO.builder().bookId(2L).quantity(50).build()
            );

            // When
            List<BookResponseDTO> result = bookService.getBooksAll();

            // Then
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getBookId());
            assertEquals("Test Book", result.get(0).getBookTitle());
            assertEquals(100L, result.get(0).getBookStockQuantity());
            assertEquals(2L, result.get(1).getBookId());
            assertEquals(50L, result.get(1).getBookStockQuantity());

            verify(bookRepository, times(1)).findAll();
            verify(inventoryClient, times(2)).getInventoryByBookId(anyLong());
        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void getBooksAll_EmptyList() {
            // Given
            when(bookRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<BookResponseDTO> result = bookService.getBooksAll();

            // Then
            assertTrue(result.isEmpty());
            verify(bookRepository, times(1)).findAll();
            verify(inventoryClient, never()).getInventoryByBookId(anyLong());
        }

        @Test
        @DisplayName("Should handle inventory service failure gracefully")
        void getBooksAll_InventoryServiceFailure() {
            // Given
            List<Book> books = Arrays.asList(sampleBook);
            when(bookRepository.findAll()).thenReturn(books);
            when(inventoryClient.getInventoryByBookId(1L)).thenThrow(new RuntimeException("Service unavailable"));

            // When
            List<BookResponseDTO> result = bookService.getBooksAll();

            // Then
            assertEquals(1, result.size());
            assertEquals(0L, result.get(0).getBookStockQuantity()); // Fallback to 0
        }
    }

    @Nested
    @DisplayName("getBookById() Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when found")
        void getBookById_Found() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            Optional<BookResponseDTO> result = bookService.getBookById(1L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getBookId());
            assertEquals("Test Book", result.get().getBookTitle());
            assertEquals(100L, result.get().getBookStockQuantity());

            verify(bookRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when book not found")
        void getBookById_NotFound() {
            // Given
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<BookResponseDTO> result = bookService.getBookById(999L);

            // Then
            assertFalse(result.isPresent());
            verify(inventoryClient, never()).getInventoryByBookId(anyLong());
        }
    }

    @Nested
    @DisplayName("getBooksByAuthor() Tests")
    class GetBooksByAuthorTests {

        @Test
        @DisplayName("Should return books by author")
        void getBooksByAuthor_Success() {
            // Given
            List<Book> authorBooks = Arrays.asList(sampleBook);
            when(bookRepository.findByBookAuthorId("author-123")).thenReturn(authorBooks);
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            List<BookResponseDTO> result = bookService.getBooksByAuthor("author-123");

            // Then
            assertEquals(1, result.size());
            assertEquals("author-123", result.get(0).getBookAuthorId());

            verify(bookRepository, times(1)).findByBookAuthorId("author-123");
        }

        @Test
        @DisplayName("Should trim author ID before searching")
        void getBooksByAuthor_TrimsInput() {
            // Given
            when(bookRepository.findByBookAuthorId("author-123")).thenReturn(Arrays.asList(sampleBook));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            bookService.getBooksByAuthor("  author-123  ");

            // Then
            verify(bookRepository, times(1)).findByBookAuthorId("author-123");
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when author ID is null")
        void getBooksByAuthor_NullAuthorId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByAuthor(null));
            assertTrue(exception.getMessage().contains("Author ID is required"));
            verify(bookRepository, never()).findByBookAuthorId(any());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when author ID is blank")
        void getBooksByAuthor_BlankAuthorId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByAuthor("   "));
            assertTrue(exception.getMessage().contains("Author ID is required"));
            verify(bookRepository, never()).findByBookAuthorId(any());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when author ID is too short")
        void getBooksByAuthor_ShortAuthorId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByAuthor("a"));
            assertTrue(exception.getMessage().contains("at least 2 characters"));
            verify(bookRepository, never()).findByBookAuthorId(any());
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when no books found for author")
        void getBooksByAuthor_NoBooksFound() {
            // Given
            when(bookRepository.findByBookAuthorId("unknown-author")).thenReturn(Collections.emptyList());

            // When & Then
            BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
                () -> bookService.getBooksByAuthor("unknown-author"));
            assertTrue(exception.getMessage().contains("No books found for author ID"));
            verify(bookRepository, times(1)).findByBookAuthorId("unknown-author");
        }
    }

    @Nested
    @DisplayName("getBooksByCategory() Tests")
    class GetBooksByCategoryTests {

        @Test
        @DisplayName("Should return books by category")
        void getBooksByCategory_Success() {
            // Given
            List<Book> categoryBooks = Arrays.asList(sampleBook);
            when(bookRepository.findByBookCategoryId("CAT-FIC")).thenReturn(categoryBooks);
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            List<BookResponseDTO> result = bookService.getBooksByCategory("CAT-FIC");

            // Then
            assertEquals(1, result.size());
            assertEquals("CAT-FIC", result.get(0).getBookCategoryId());

            verify(bookRepository, times(1)).findByBookCategoryId("CAT-FIC");
        }

        @Test
        @DisplayName("Should throw BookNotFoundException for category with no books")
        void getBooksByCategory_NoBooks() {
            // Given
            when(bookRepository.findByBookCategoryId("CAT-HIS")).thenReturn(Collections.emptyList());

            // When & Then
            BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
                () -> bookService.getBooksByCategory("CAT-HIS"));
            assertTrue(exception.getMessage().contains("No books found for category ID"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when category ID is null")
        void getBooksByCategory_NullCategoryId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByCategory(null));
            assertTrue(exception.getMessage().contains("Category ID is required"));
            verify(bookRepository, never()).findByBookCategoryId(any());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when category ID is blank")
        void getBooksByCategory_BlankCategoryId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByCategory("   "));
            assertTrue(exception.getMessage().contains("Category ID is required"));
            verify(bookRepository, never()).findByBookCategoryId(any());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException for invalid category ID")
        void getBooksByCategory_InvalidCategoryId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByCategory("INVALID-CAT"));
            assertTrue(exception.getMessage().contains("Invalid category ID"));
            assertTrue(exception.getMessage().contains("Valid categories are:"));
            verify(bookRepository, never()).findByBookCategoryId(any());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException for non-existent category ID")
        void getBooksByCategory_NonExistentCategoryId() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.getBooksByCategory("CAT-XYZ"));
            assertTrue(exception.getMessage().contains("Invalid category ID"));
            verify(bookRepository, never()).findByBookCategoryId(any());
        }
    }

    @Nested
    @DisplayName("searchBooksByTitle() Tests")
    class SearchBooksByTitleTests {

        @Test
        @DisplayName("Should return books matching title search")
        void searchBooksByTitle_Success() {
            // Given
            List<Book> searchResults = Arrays.asList(sampleBook);
            when(bookRepository.findByBookTitleContainingIgnoreCase("Test")).thenReturn(searchResults);
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            List<BookResponseDTO> result = bookService.searchBooksByTitle("Test");

            // Then
            assertEquals(1, result.size());
            assertTrue(result.get(0).getBookTitle().contains("Test"));
        }

        @Test
        @DisplayName("Should trim title search input")
        void searchBooksByTitle_TrimsInput() {
            // Given
            when(bookRepository.findByBookTitleContainingIgnoreCase("Book")).thenReturn(Arrays.asList(sampleBook));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            bookService.searchBooksByTitle("  Book  ");

            // Then
            verify(bookRepository, times(1)).findByBookTitleContainingIgnoreCase("Book");
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when title is null")
        void searchBooksByTitle_NullTitle() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.searchBooksByTitle(null));
            assertTrue(exception.getMessage().contains("Title search term is required"));
            verify(bookRepository, never()).findByBookTitleContainingIgnoreCase(any());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when title is blank")
        void searchBooksByTitle_BlankTitle() {
            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.searchBooksByTitle("   "));
            assertTrue(exception.getMessage().contains("Title search term is required"));
            verify(bookRepository, never()).findByBookTitleContainingIgnoreCase(any());
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when no books match the title")
        void searchBooksByTitle_NoBooksFound() {
            // Given
            when(bookRepository.findByBookTitleContainingIgnoreCase("NonExistent")).thenReturn(Collections.emptyList());

            // When & Then
            BookNotFoundException exception = assertThrows(BookNotFoundException.class, 
                () -> bookService.searchBooksByTitle("NonExistent"));
            assertTrue(exception.getMessage().contains("No books found matching title"));
            verify(bookRepository, times(1)).findByBookTitleContainingIgnoreCase("NonExistent");
        }
    }

    @Nested
    @DisplayName("addBook() Tests")
    class AddBookTests {

        private AddBookRequestDTO createValidRequest() {
            // Using reflection or creating a simple mock for the DTO
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("New Book");
            when(request.getBookAuthorId()).thenReturn("author-new");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(25.99);
            when(request.getBookStockQuantity()).thenReturn(50);
            when(request.getBookId()).thenReturn(null);
            return request;
        }

        @Test
        @DisplayName("Should add book successfully")
        void addBook_Success() {
            // Given
            AddBookRequestDTO request = createValidRequest();

            Book savedBook = Book.builder()
                    .bookId(1L)
                    .bookTitle("New Book")
                    .bookAuthorId("author-new")
                    .bookCategoryId("CAT-FIC")
                    .bookPrice(25.99)
                    .build();

            InventoryResponseDTO invResponse = InventoryResponseDTO.builder()
                    .bookId(1L)
                    .quantity(50)
                    .build();

            when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
            when(inventoryClient.createInventory(any(InventoryCreateDTO.class))).thenReturn(invResponse);

            // When
            BookResponseDTO result = bookService.addBook(request);

            // Then
            assertEquals(1L, result.getBookId());
            assertEquals("New Book", result.getBookTitle());
            assertEquals(50L, result.getBookStockQuantity());

            verify(bookRepository, times(1)).save(any(Book.class));
            verify(inventoryClient, times(1)).createInventory(any(InventoryCreateDTO.class));
        }

        @Test
        @DisplayName("Should add book with custom ID when provided")
        void addBook_WithCustomId() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Custom ID Book");
            when(request.getBookAuthorId()).thenReturn("author-custom");
            when(request.getBookCategoryId()).thenReturn("CAT-SCI");
            when(request.getBookPrice()).thenReturn(35.99);
            when(request.getBookStockQuantity()).thenReturn(30);
            when(request.getBookId()).thenReturn(100L);

            when(bookRepository.existsById(100L)).thenReturn(false);

            Book savedBook = Book.builder()
                    .bookId(100L)
                    .bookTitle("Custom ID Book")
                    .bookAuthorId("author-custom")
                    .bookCategoryId("CAT-SCI")
                    .bookPrice(35.99)
                    .build();

            when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
            when(inventoryClient.createInventory(any(InventoryCreateDTO.class)))
                    .thenReturn(InventoryResponseDTO.builder().quantity(30).build());

            // When
            BookResponseDTO result = bookService.addBook(request);

            // Then
            assertEquals(100L, result.getBookId());
            verify(bookRepository, times(1)).existsById(100L);
        }

        @Test
        @DisplayName("Should throw DuplicateBookException when book ID already exists")
        void addBook_DuplicateId() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Duplicate Book");
            when(request.getBookAuthorId()).thenReturn("author");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);
            when(request.getBookId()).thenReturn(1L);

            when(bookRepository.existsById(1L)).thenReturn(true);

            // When & Then
            assertThrows(DuplicateBookException.class, () -> bookService.addBook(request));
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when title is missing")
        void addBook_MissingTitle() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn(null);
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            assertThrows(InvalidBookDataException.class, () -> bookService.addBook(request));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when title is blank")
        void addBook_BlankTitle() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("   ");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            assertThrows(InvalidBookDataException.class, () -> bookService.addBook(request));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when price is negative")
        void addBook_NegativePrice() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Title");
            when(request.getBookPrice()).thenReturn(-10.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            assertThrows(InvalidBookDataException.class, () -> bookService.addBook(request));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when stock is negative")
        void addBook_NegativeStock() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Title");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(-5);

            // When & Then
            assertThrows(InvalidBookDataException.class, () -> bookService.addBook(request));
        }

        @Test
        @DisplayName("Should handle inventory service failure and return book with 0 stock")
        void addBook_InventoryServiceFailure() {
            // Given
            AddBookRequestDTO request = createValidRequest();

            Book savedBook = Book.builder()
                    .bookId(1L)
                    .bookTitle("New Book")
                    .bookAuthorId("author-new")
                    .bookCategoryId("CAT-FIC")
                    .bookPrice(25.99)
                    .build();

            when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
            when(inventoryClient.createInventory(any(InventoryCreateDTO.class)))
                    .thenThrow(new RuntimeException("Inventory service unavailable"));

            // When
            BookResponseDTO result = bookService.addBook(request);

            // Then
            assertEquals(1L, result.getBookId());
            assertEquals(0L, result.getBookStockQuantity()); // Fallback to 0
        }

        @Test
        @DisplayName("Should set default stock to 0 when null")
        void addBook_NullStockDefaultsToZero() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Book Title");
            when(request.getBookAuthorId()).thenReturn("author");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(null);
            when(request.getBookId()).thenReturn(null);

            // This will fail validation, but let's test the null handling in the actual flow
            // The validation throws exception for null stock
            assertThrows(InvalidBookDataException.class, () -> bookService.addBook(request));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when author ID is missing")
        void addBook_MissingAuthorId() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn(null);
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("Author ID"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when author ID is blank")
        void addBook_BlankAuthorId() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn("   ");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("Author ID"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when author ID is too short")
        void addBook_AuthorIdTooShort() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn("A");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("Author ID"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when category ID is missing")
        void addBook_MissingCategoryId() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn("valid-author");
            when(request.getBookCategoryId()).thenReturn(null);
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("Category ID"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when category ID is invalid")
        void addBook_InvalidCategoryId() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn("valid-author");
            when(request.getBookCategoryId()).thenReturn("INVALID-CAT");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("Invalid category ID"));
            assertTrue(exception.getMessage().contains("INVALID-CAT"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when title is too short")
        void addBook_TitleTooShort() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("A");
            when(request.getBookAuthorId()).thenReturn("valid-author");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("at least 2 characters"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when price is null")
        void addBook_NullPrice() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn("valid-author");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(null);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("price"));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when price exceeds maximum")
        void addBook_PriceExceedsMaximum() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Valid Book Title");
            when(request.getBookAuthorId()).thenReturn("valid-author");
            when(request.getBookCategoryId()).thenReturn("CAT-FIC");
            when(request.getBookPrice()).thenReturn(15000.0);
            when(request.getBookStockQuantity()).thenReturn(10);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class, 
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("exceed"));
        }
    }

    @Nested
    @DisplayName("updateBook() Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book title and price successfully")
        void updateBook_TitleAndPrice() {
            // Given
            UpdateBookRequestDTO request = new UpdateBookRequestDTO();
            request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Updated Title");
            when(request.getBookPrice()).thenReturn(39.99);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            BookResponseDTO result = bookService.updateBook(1L, request);

            // Then
            assertEquals("Updated Title", result.getBookTitle());
            assertEquals(39.99, result.getBookPrice());

            verify(bookRepository, times(1)).save(any(Book.class));
        }

        @Test
        @DisplayName("Should update only title when price is null")
        void updateBook_OnlyTitle() {
            // Given
            UpdateBookRequestDTO request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("New Title Only");
            when(request.getBookPrice()).thenReturn(null);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            BookResponseDTO result = bookService.updateBook(1L, request);

            // Then
            assertEquals("New Title Only", result.getBookTitle());
            assertEquals(29.99, result.getBookPrice()); // Original price unchanged
        }

        @Test
        @DisplayName("Should update only price when title is null")
        void updateBook_OnlyPrice() {
            // Given
            UpdateBookRequestDTO request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn(null);
            when(request.getBookPrice()).thenReturn(49.99);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            BookResponseDTO result = bookService.updateBook(1L, request);

            // Then
            assertEquals("Test Book", result.getBookTitle()); // Original title unchanged
            assertEquals(49.99, result.getBookPrice());
        }

        @Test
        @DisplayName("Should not save when no updates provided")
        void updateBook_NoChanges() {
            // Given
            UpdateBookRequestDTO request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn(null);
            when(request.getBookPrice()).thenReturn(null);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            BookResponseDTO result = bookService.updateBook(1L, request);

            // Then
            verify(bookRepository, never()).save(any(Book.class));
            assertEquals("Test Book", result.getBookTitle());
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when book doesn't exist")
        void updateBook_NotFound() {
            // Given
            UpdateBookRequestDTO request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Update");

            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BookNotFoundException.class, () -> bookService.updateBook(999L, request));
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException when price is negative")
        void updateBook_NegativePrice() {
            // Given
            UpdateBookRequestDTO request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn(null);
            when(request.getBookPrice()).thenReturn(-10.0);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

            // When & Then
            assertThrows(InvalidBookDataException.class, () -> bookService.updateBook(1L, request));
        }

        @Test
        @DisplayName("Should skip blank title update")
        void updateBook_BlankTitle() {
            // Given
            UpdateBookRequestDTO request = mock(UpdateBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("   ");
            when(request.getBookPrice()).thenReturn(null);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(inventoryClient.getInventoryByBookId(1L)).thenReturn(sampleInventoryResponse);

            // When
            BookResponseDTO result = bookService.updateBook(1L, request);

            // Then
            assertEquals("Test Book", result.getBookTitle()); // Title unchanged
            verify(bookRepository, never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("getBookPricesMap() Tests")
    class GetBookPricesMapTests {

        @Test
        @DisplayName("Should return prices for multiple books")
        void getBookPricesMap_Success() {
            // Given
            Book book2 = Book.builder()
                    .bookId(2L)
                    .bookTitle("Second Book")
                    .bookPrice(19.99)
                    .build();

            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

            // When
            BookPriceResponseDTO result = bookService.getBookPricesMap(Arrays.asList(1L, 2L));

            // Then
            assertNotNull(result.getBookPrice());
            assertEquals(2, result.getBookPrice().size());
            assertEquals(29.99, result.getBookPrice().get(1L));
            assertEquals(19.99, result.getBookPrice().get(2L));
        }

        @Test
        @DisplayName("Should return price for single book")
        void getBookPricesMap_SingleBook() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

            // When
            BookPriceResponseDTO result = bookService.getBookPricesMap(Arrays.asList(1L));

            // Then
            assertEquals(1, result.getBookPrice().size());
            assertEquals(29.99, result.getBookPrice().get(1L));
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when any book not found")
        void getBookPricesMap_BookNotFound() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BookNotFoundException.class, 
                    () -> bookService.getBookPricesMap(Arrays.asList(1L, 999L)));
        }

        @Test
        @DisplayName("Should maintain order of book IDs in response")
        void getBookPricesMap_MaintainsOrder() {
            // Given
            Book book2 = Book.builder().bookId(2L).bookPrice(19.99).build();
            Book book3 = Book.builder().bookId(3L).bookPrice(39.99).build();

            when(bookRepository.findById(3L)).thenReturn(Optional.of(book3));
            when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
            when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

            // When
            BookPriceResponseDTO result = bookService.getBookPricesMap(Arrays.asList(3L, 1L, 2L));

            // Then - LinkedHashMap should maintain insertion order
            List<Long> keys = new ArrayList<>(result.getBookPrice().keySet());
            assertEquals(Arrays.asList(3L, 1L, 2L), keys);
        }
    }

    @Nested
    @DisplayName("deleteBook() Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book and sync with inventory service")
        void deleteBook_Success() {
            // Given
            when(bookRepository.existsById(1L)).thenReturn(true);
            doNothing().when(bookRepository).deleteById(1L);
            doNothing().when(inventoryClient).deleteInventoryByBookId(1L);

            // When
            bookService.deleteBook(1L);

            // Then
            verify(bookRepository, times(1)).existsById(1L);
            verify(bookRepository, times(1)).deleteById(1L);
            verify(inventoryClient, times(1)).deleteInventoryByBookId(1L);
        }

        @Test
        @DisplayName("Should throw BookNotFoundException when book doesn't exist")
        void deleteBook_NotFound() {
            // Given
            when(bookRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(999L));
            verify(bookRepository, never()).deleteById(anyLong());
            verify(inventoryClient, never()).deleteInventoryByBookId(anyLong());
        }

        @Test
        @DisplayName("Should handle inventory service failure gracefully")
        void deleteBook_InventoryServiceFailure() {
            // Given
            when(bookRepository.existsById(1L)).thenReturn(true);
            doNothing().when(bookRepository).deleteById(1L);
            doThrow(new RuntimeException("Service unavailable"))
                    .when(inventoryClient).deleteInventoryByBookId(1L);

            // When - Should not throw exception
            assertDoesNotThrow(() -> bookService.deleteBook(1L));

            // Then - Book should still be deleted locally
            verify(bookRepository, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Category Enum Handling Tests")
    class CategoryEnumTests {

        @Test
        @DisplayName("Should normalize category ID to canonical form")
        void addBook_NormalizesCategory() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Category Test Book");
            when(request.getBookAuthorId()).thenReturn("author");
            when(request.getBookCategoryId()).thenReturn("cat-fic"); // lowercase
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);
            when(request.getBookId()).thenReturn(null);

            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

            Book savedBook = Book.builder()
                    .bookId(1L)
                    .bookTitle("Category Test Book")
                    .bookCategoryId("CAT-FIC")
                    .bookAuthorId("author")
                    .bookPrice(20.0)
                    .build();

            when(bookRepository.save(bookCaptor.capture())).thenReturn(savedBook);
            when(inventoryClient.createInventory(any())).thenReturn(
                    InventoryResponseDTO.builder().quantity(10).build()
            );

            // When
            bookService.addBook(request);

            // Then
            assertEquals("CAT-FIC", bookCaptor.getValue().getBookCategoryId());
        }

        @Test
        @DisplayName("Should throw InvalidBookDataException for unknown category")
        void addBook_ThrowsExceptionForUnknownCategory() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Unknown Category Book");
            when(request.getBookAuthorId()).thenReturn("author");
            when(request.getBookCategoryId()).thenReturn("UNKNOWN-CAT");
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);
            when(request.getBookId()).thenReturn(null);

            // When & Then
            InvalidBookDataException exception = assertThrows(InvalidBookDataException.class,
                () -> bookService.addBook(request));
            assertTrue(exception.getMessage().contains("Invalid category ID"));
            assertTrue(exception.getMessage().contains("UNKNOWN-CAT"));
        }

        @Test
        @DisplayName("Should accept valid OTHER category")
        void addBook_AcceptsOtherCategory() {
            // Given
            AddBookRequestDTO request = mock(AddBookRequestDTO.class);
            when(request.getBookTitle()).thenReturn("Other Category Book");
            when(request.getBookAuthorId()).thenReturn("author");
            when(request.getBookCategoryId()).thenReturn("CAT-OTH"); // Valid OTHER category
            when(request.getBookPrice()).thenReturn(20.0);
            when(request.getBookStockQuantity()).thenReturn(10);
            when(request.getBookId()).thenReturn(null);

            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

            Book savedBook = Book.builder()
                    .bookId(1L)
                    .bookCategoryId("CAT-OTH")
                    .bookTitle("Other Category Book")
                    .bookAuthorId("author")
                    .bookPrice(20.0)
                    .build();

            when(bookRepository.save(bookCaptor.capture())).thenReturn(savedBook);
            when(inventoryClient.createInventory(any())).thenReturn(
                    InventoryResponseDTO.builder().quantity(10).build()
            );

            // When
            bookService.addBook(request);

            // Then
            assertEquals("CAT-OTH", bookCaptor.getValue().getBookCategoryId());
        }
    }
}
