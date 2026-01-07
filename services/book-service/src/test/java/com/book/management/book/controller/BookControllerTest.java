package com.book.management.book.controller;

import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.exception.GlobalBookExceptionHandler;
import com.book.management.book.exception.InvalidBookDataException;
import com.book.management.book.exception.InvalidCategoryException;
import com.book.management.book.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookController Tests")
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    private BookResponseDTO sampleBookResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalBookExceptionHandler())
                .build();

        // Sample book response
        sampleBookResponse = new BookResponseDTO();
        sampleBookResponse.setBookId(1L);
        sampleBookResponse.setBookTitle("Test Book");
        sampleBookResponse.setBookAuthorId("author-123");
        sampleBookResponse.setBookCategoryId("CAT-FIC");
        sampleBookResponse.setBookPrice(29.99);
        sampleBookResponse.setBookStockQuantity(100L);
    }

    @Nested
    @DisplayName("POST /api/v1/book/add - Add Book")
    class AddBookTests {

        @Test
        @DisplayName("Should add book successfully and return 200 OK")
        void addBook_Success() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookTitle": "Test Book",
                    "bookAuthorId": "author-123",
                    "bookCategoryId": "CAT-FIC",
                    "bookPrice": 29.99,
                    "bookStockQuantity": 100
                }
                """;

            when(bookService.addBook(any(AddBookRequestDTO.class))).thenReturn(sampleBookResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/book/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId").value(1))
                    .andExpect(jsonPath("$.bookTitle").value("Test Book"))
                    .andExpect(jsonPath("$.bookAuthorId").value("author-123"))
                    .andExpect(jsonPath("$.bookCategoryId").value("CAT-FIC"))
                    .andExpect(jsonPath("$.bookPrice").value(29.99))
                    .andExpect(jsonPath("$.bookStockQuantity").value(100));

            verify(bookService, times(1)).addBook(any(AddBookRequestDTO.class));
        }

        @Test
        @DisplayName("Should add book with optional bookId")
        void addBook_WithOptionalBookId() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookId": 5,
                    "bookTitle": "Custom ID Book",
                    "bookAuthorId": "author-456",
                    "bookCategoryId": "CAT-SCI",
                    "bookPrice": 39.99,
                    "bookStockQuantity": 50
                }
                """;

            BookResponseDTO customResponse = new BookResponseDTO();
            customResponse.setBookId(5L);
            customResponse.setBookTitle("Custom ID Book");
            customResponse.setBookAuthorId("author-456");
            customResponse.setBookCategoryId("CAT-SCI");
            customResponse.setBookPrice(39.99);
            customResponse.setBookStockQuantity(50L);

            when(bookService.addBook(any(AddBookRequestDTO.class))).thenReturn(customResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/book/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId").value(5));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/book/getAll - Get All Books")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return all books successfully")
        void getAllBooks_Success() throws Exception {
            // Given
            BookResponseDTO book2 = new BookResponseDTO();
            book2.setBookId(2L);
            book2.setBookTitle("Second Book");
            book2.setBookAuthorId("author-456");
            book2.setBookCategoryId("CAT-SCI");
            book2.setBookPrice(19.99);
            book2.setBookStockQuantity(50L);

            List<BookResponseDTO> books = Arrays.asList(sampleBookResponse, book2);
            when(bookService.getBooksAll()).thenReturn(books);

            // When & Then
            mockMvc.perform(get("/api/v1/book/getAll"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].bookId").value(1))
                    .andExpect(jsonPath("$[1].bookId").value(2));

            verify(bookService, times(1)).getBooksAll();
        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void getAllBooks_EmptyList() throws Exception {
            // Given
            when(bookService.getBooksAll()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/book/getAll"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/book/getById/{bookId} - Get Book By ID")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when found")
        void getBookById_Found() throws Exception {
            // Given
            when(bookService.getBookById(1L)).thenReturn(sampleBookResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/book/getById/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId").value(1))
                    .andExpect(jsonPath("$.bookTitle").value("Test Book"));

            verify(bookService, times(1)).getBookById(1L);
        }

        @Test
        @DisplayName("Should return 404 when book not found")
        void getBookById_NotFound() throws Exception {
            // Given
            when(bookService.getBookById(999L)).thenThrow(new com.book.management.book.exception.BookNotFoundException(999));

            // When & Then
            mockMvc.perform(get("/api/v1/book/getById/999"))
                    .andExpect(status().isNotFound());

            verify(bookService, times(1)).getBookById(999L);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/book/getByAuthor/{authorId} - Get Books By Author")
    class GetBooksByAuthorTests {

        @Test
        @DisplayName("Should return books by author")
        void getBooksByAuthor_Success() throws Exception {
            // Given
            List<BookResponseDTO> authorBooks = Arrays.asList(sampleBookResponse);
            when(bookService.getBooksByAuthor("author-123")).thenReturn(authorBooks);

            // When & Then
            mockMvc.perform(get("/api/v1/book/getByAuthor/author-123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].bookAuthorId").value("author-123"));

            verify(bookService, times(1)).getBooksByAuthor("author-123");
        }

        @Test
        @DisplayName("Should return empty list when author has no books")
        void getBooksByAuthor_NoBooks() throws Exception {
            // Given
            when(bookService.getBooksByAuthor("unknown-author")).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/book/getByAuthor/unknown-author"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 400 when author ID is blank")
        void getBooksByAuthor_BlankAuthorId() throws Exception {
            // Given
            when(bookService.getBooksByAuthor("   ")).thenThrow(new InvalidBookDataException("Author ID cannot be null or empty"));

            // When & Then
            mockMvc.perform(get("/api/v1/book/getByAuthor/   "))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/book/getByCategory - Get Books By Category")
    class GetBooksByCategoryTests {

        @Test
        @DisplayName("Should return books by category")
        void getBooksByCategory_Success() throws Exception {
            // Given
            List<BookResponseDTO> categoryBooks = Arrays.asList(sampleBookResponse);
            when(bookService.getBooksByCategory("CAT-FIC")).thenReturn(categoryBooks);

            // When & Then
            mockMvc.perform(get("/api/v1/book/getByCategory")
                            .param("categoryId", "CAT-FIC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].bookCategoryId").value("CAT-FIC"));

            verify(bookService, times(1)).getBooksByCategory("CAT-FIC");
        }

        @Test
        @DisplayName("Should return empty list for category with no books")
        void getBooksByCategory_NoBooks() throws Exception {
            // Given
            when(bookService.getBooksByCategory("CAT-HIS")).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/book/getByCategory")
                            .param("categoryId", "CAT-HIS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 400 for invalid category ID")
        void getBooksByCategory_InvalidCategoryId() throws Exception {
            // Given
            when(bookService.getBooksByCategory("INVALID-CAT")).thenThrow(new InvalidCategoryException("INVALID-CAT"));

            // When & Then
            mockMvc.perform(get("/api/v1/book/getByCategory")
                            .param("categoryId", "INVALID-CAT"))
                    .andExpect(status().isBadRequest());

            verify(bookService, times(1)).getBooksByCategory("INVALID-CAT");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/book/search - Search Books By Title")
    class SearchBooksByTitleTests {

        @Test
        @DisplayName("Should return books matching title search")
        void searchBooksByTitle_Success() throws Exception {
            // Given
            List<BookResponseDTO> searchResults = Arrays.asList(sampleBookResponse);
            when(bookService.searchBooksByTitle("Test")).thenReturn(searchResults);

            // When & Then
            mockMvc.perform(get("/api/v1/book/search")
                            .param("title", "Test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].bookTitle").value("Test Book"));

            verify(bookService, times(1)).searchBooksByTitle("Test");
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void searchBooksByTitle_NoMatches() throws Exception {
            // Given
            when(bookService.searchBooksByTitle("NonExistent")).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/book/search")
                            .param("title", "NonExistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/book/bulk/prices - Get Book Prices Map")
    class GetBookPricesMapTests {

        @Test
        @DisplayName("Should return prices for multiple books")
        void getBookPricesMap_Success() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookIds": [1, 2, 3]
                }
                """;

            Map<Long, Double> pricesMap = new LinkedHashMap<>();
            pricesMap.put(1L, 29.99);
            pricesMap.put(2L, 19.99);
            pricesMap.put(3L, 39.99);
            BookPriceResponseDTO priceResponse = new BookPriceResponseDTO(pricesMap);

            when(bookService.getBookPricesMap(Arrays.asList(1L, 2L, 3L))).thenReturn(priceResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/book/bulk/prices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookPrice.1").value(29.99))
                    .andExpect(jsonPath("$.bookPrice.2").value(19.99))
                    .andExpect(jsonPath("$.bookPrice.3").value(39.99));

            verify(bookService, times(1)).getBookPricesMap(Arrays.asList(1L, 2L, 3L));
        }

        @Test
        @DisplayName("Should return price for single book")
        void getBookPricesMap_SingleBook() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookIds": [1]
                }
                """;

            Map<Long, Double> pricesMap = new LinkedHashMap<>();
            pricesMap.put(1L, 29.99);
            BookPriceResponseDTO priceResponse = new BookPriceResponseDTO(pricesMap);

            when(bookService.getBookPricesMap(Arrays.asList(1L))).thenReturn(priceResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/book/bulk/prices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookPrice.1").value(29.99));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/book/update/{bookId} - Update Book")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book successfully")
        void updateBook_Success() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookTitle": "Updated Title",
                    "bookPrice": 35.99
                }
                """;

            BookResponseDTO updatedBook = new BookResponseDTO();
            updatedBook.setBookId(1L);
            updatedBook.setBookTitle("Updated Title");
            updatedBook.setBookAuthorId("author-123");
            updatedBook.setBookCategoryId("CAT-FIC");
            updatedBook.setBookPrice(35.99);
            updatedBook.setBookStockQuantity(100L);

            when(bookService.updateBook(eq(1L), any(UpdateBookRequestDTO.class))).thenReturn(updatedBook);

            // When & Then
            mockMvc.perform(patch("/api/v1/book/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookTitle").value("Updated Title"))
                    .andExpect(jsonPath("$.bookPrice").value(35.99));

            verify(bookService, times(1)).updateBook(eq(1L), any(UpdateBookRequestDTO.class));
        }

        @Test
        @DisplayName("Should update only title")
        void updateBook_OnlyTitle() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookTitle": "New Title Only"
                }
                """;

            BookResponseDTO updatedBook = new BookResponseDTO();
            updatedBook.setBookId(1L);
            updatedBook.setBookTitle("New Title Only");
            updatedBook.setBookAuthorId("author-123");
            updatedBook.setBookCategoryId("CAT-FIC");
            updatedBook.setBookPrice(29.99);
            updatedBook.setBookStockQuantity(100L);

            when(bookService.updateBook(eq(1L), any(UpdateBookRequestDTO.class))).thenReturn(updatedBook);

            // When & Then
            mockMvc.perform(patch("/api/v1/book/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookTitle").value("New Title Only"))
                    .andExpect(jsonPath("$.bookPrice").value(29.99));
        }

        @Test
        @DisplayName("Should update only price")
        void updateBook_OnlyPrice() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookPrice": 45.99
                }
                """;

            BookResponseDTO updatedBook = new BookResponseDTO();
            updatedBook.setBookId(1L);
            updatedBook.setBookTitle("Test Book");
            updatedBook.setBookAuthorId("author-123");
            updatedBook.setBookCategoryId("CAT-FIC");
            updatedBook.setBookPrice(45.99);
            updatedBook.setBookStockQuantity(100L);

            when(bookService.updateBook(eq(1L), any(UpdateBookRequestDTO.class))).thenReturn(updatedBook);

            // When & Then
            mockMvc.perform(patch("/api/v1/book/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookPrice").value(45.99));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent book")
        void updateBook_NotFound() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookTitle": "Updated Title"
                }
                """;

            when(bookService.updateBook(eq(999L), any(UpdateBookRequestDTO.class)))
                    .thenThrow(new BookNotFoundException(999));

            // When & Then
            mockMvc.perform(patch("/api/v1/book/update/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isNotFound());

            verify(bookService, times(1)).updateBook(eq(999L), any(UpdateBookRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 when price is negative")
        void updateBook_NegativePrice() throws Exception {
            // Given
            String requestJson = """
                {
                    "bookPrice": -10.0
                }
                """;

            when(bookService.updateBook(eq(1L), any(UpdateBookRequestDTO.class)))
                    .thenThrow(new InvalidBookDataException("Price cannot be negative"));

            // When & Then
            mockMvc.perform(patch("/api/v1/book/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/book/delete/{bookId} - Delete Book")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book successfully and return 204 No Content")
        void deleteBook_Success() throws Exception {
            // Given
            doNothing().when(bookService).deleteBook(1L);

            // When & Then
            mockMvc.perform(delete("/api/v1/book/delete/1"))
                    .andExpect(status().isNoContent());

            verify(bookService, times(1)).deleteBook(1L);
        }

        @Test
        @DisplayName("Should call service delete for any book ID")
        void deleteBook_AnyId() throws Exception {
            // Given
            doNothing().when(bookService).deleteBook(anyLong());

            // When & Then
            mockMvc.perform(delete("/api/v1/book/delete/999"))
                    .andExpect(status().isNoContent());

            verify(bookService, times(1)).deleteBook(999L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent book")
        void deleteBook_NotFound() throws Exception {
            // Given
            doThrow(new BookNotFoundException(999)).when(bookService).deleteBook(999L);

            // When & Then
            mockMvc.perform(delete("/api/v1/book/delete/999"))
                    .andExpect(status().isNotFound());

            verify(bookService, times(1)).deleteBook(999L);
        }
    }
}
