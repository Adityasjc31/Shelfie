package com.book.management.book.dto;

import com.book.management.book.dto.requestdto.AddBookRequestDTO;
import com.book.management.book.dto.requestdto.BookPriceRequestDTO;
import com.book.management.book.dto.requestdto.InventoryCreateDTO;
import com.book.management.book.dto.requestdto.UpdateBookRequestDTO;
import com.book.management.book.dto.responsedto.BookPriceResponseDTO;
import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.dto.responsedto.InventoryResponseDTO;
import com.book.management.book.client.dto.BookRatingStatsDTO;
import com.book.management.book.client.dto.RatingDistribution;
import com.book.management.book.client.dto.ReviewResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO Tests")
class DTOTest {

    @Nested
    @DisplayName("AddBookRequestDTO Tests")
    class AddBookRequestDTOTests {
        
        @Test
        @DisplayName("Should get all fields")
        void shouldGetAllFields() {
            AddBookRequestDTO dto = new AddBookRequestDTO();
            
            assertNull(dto.getBookId());
            assertNull(dto.getBookTitle());
            assertNull(dto.getBookAuthorId());
            assertNull(dto.getBookCategoryId());
            assertNull(dto.getBookPrice());
            assertNull(dto.getBookStockQuantity());
        }
    }

    @Nested
    @DisplayName("UpdateBookRequestDTO Tests")
    class UpdateBookRequestDTOTests {
        
        @Test
        @DisplayName("Should get all fields")
        void shouldGetAllFields() {
            UpdateBookRequestDTO dto = new UpdateBookRequestDTO();
            
            assertNull(dto.getBookTitle());
            assertNull(dto.getBookAuthorId());
            assertNull(dto.getBookCategoryId());
            assertNull(dto.getBookPrice());
        }
    }

    @Nested
    @DisplayName("BookPriceRequestDTO Tests")
    class BookPriceRequestDTOTests {
        
        @Test
        @DisplayName("Should create and set bookIds")
        void shouldCreateAndSetBookIds() {
            BookPriceRequestDTO dto = new BookPriceRequestDTO();
            dto.setBookIds(Arrays.asList(1L, 2L, 3L));
            
            assertEquals(3, dto.getBookIds().size());
            assertTrue(dto.getBookIds().contains(1L));
            assertTrue(dto.getBookIds().contains(2L));
            assertTrue(dto.getBookIds().contains(3L));
        }
    }

    @Nested
    @DisplayName("InventoryCreateDTO Tests")
    class InventoryCreateDTOTests {
        
        @Test
        @DisplayName("Should create using builder")
        void shouldCreateUsingBuilder() {
            InventoryCreateDTO dto = InventoryCreateDTO.builder()
                    .bookId(1L)
                    .quantity(50)
                    .lowStockThreshold(10)
                    .build();
            
            assertEquals(1L, dto.getBookId());
            assertEquals(50, dto.getQuantity());
            assertEquals(10, dto.getLowStockThreshold());
        }
    }

    @Nested
    @DisplayName("BookResponseDTO Tests")
    class BookResponseDTOTests {
        
        @Test
        @DisplayName("Should create with all-args constructor")
        void shouldCreateWithAllArgsConstructor() {
            BookResponseDTO dto = new BookResponseDTO(1L, "Test Book", "author-123", "CAT-FIC", 29.99, 100);
            
            assertEquals(1L, dto.getBookId());
            assertEquals("Test Book", dto.getBookTitle());
            assertEquals("author-123", dto.getBookAuthorId());
            assertEquals("CAT-FIC", dto.getBookCategoryId());
            assertEquals(29.99, dto.getBookPrice());
            assertEquals(100, dto.getBookStockQuantity());
        }

        @Test
        @DisplayName("Should create with no-args constructor and setters")
        void shouldCreateWithNoArgsConstructorAndSetters() {
            BookResponseDTO dto = new BookResponseDTO();
            dto.setBookId(2L);
            dto.setBookTitle("Another Book");
            dto.setBookAuthorId("author-456");
            dto.setBookCategoryId("CAT-SCI");
            dto.setBookPrice(39.99);
            dto.setBookStockQuantity(50);
            
            assertEquals(2L, dto.getBookId());
            assertEquals("Another Book", dto.getBookTitle());
            assertEquals("author-456", dto.getBookAuthorId());
            assertEquals("CAT-SCI", dto.getBookCategoryId());
            assertEquals(39.99, dto.getBookPrice());
            assertEquals(50, dto.getBookStockQuantity());
        }
    }

    @Nested
    @DisplayName("BookPriceResponseDTO Tests")
    class BookPriceResponseDTOTests {
        
        @Test
        @DisplayName("Should create with map constructor")
        void shouldCreateWithMapConstructor() {
            Map<Long, Double> prices = new HashMap<>();
            prices.put(1L, 19.99);
            prices.put(2L, 29.99);
            
            BookPriceResponseDTO dto = new BookPriceResponseDTO(prices);
            
            assertEquals(2, dto.getBookPrice().size());
            assertEquals(19.99, dto.getBookPrice().get(1L));
            assertEquals(29.99, dto.getBookPrice().get(2L));
        }

        @Test
        @DisplayName("Should create with no-args constructor and setter")
        void shouldCreateWithNoArgsConstructorAndSetter() {
            BookPriceResponseDTO dto = new BookPriceResponseDTO();
            Map<Long, Double> prices = new HashMap<>();
            prices.put(3L, 9.99);
            dto.setBookPrice(prices);
            
            assertEquals(9.99, dto.getBookPrice().get(3L));
        }
    }

    @Nested
    @DisplayName("InventoryResponseDTO Tests")
    class InventoryResponseDTOTests {
        
        @Test
        @DisplayName("Should create using builder")
        void shouldCreateUsingBuilder() {
            LocalDateTime now = LocalDateTime.now();
            InventoryResponseDTO dto = InventoryResponseDTO.builder()
                    .bookId(1L)
                    .quantity(50)
                    .lowStockThreshold(10)
                    .isLowStock(false)
                    .isOutOfStock(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            
            assertEquals(1L, dto.getBookId());
            assertEquals(50, dto.getQuantity());
            assertEquals(10, dto.getLowStockThreshold());
            assertFalse(dto.isLowStock());
            assertFalse(dto.isOutOfStock());
            assertEquals(now, dto.getCreatedAt());
            assertEquals(now, dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("BookRatingStatsDTO Tests")
    class BookRatingStatsDTOTests {
        
        @Test
        @DisplayName("Should create using builder")
        void shouldCreateUsingBuilder() {
            RatingDistribution distribution = RatingDistribution.builder()
                    .fiveStars(10L)
                    .fourStars(5L)
                    .threeStars(3L)
                    .twoStars(1L)
                    .oneStar(0L)
                    .build();
            
            BookRatingStatsDTO dto = BookRatingStatsDTO.builder()
                    .bookId(1L)
                    .averageRating(4.5)
                    .totalReviews(19L)
                    .approvedReviews(15L)
                    .pendingReviews(3L)
                    .rejectedReviews(1L)
                    .ratingDistribution(distribution)
                    .build();
            
            assertEquals(1L, dto.getBookId());
            assertEquals(4.5, dto.getAverageRating());
            assertEquals(19L, dto.getTotalReviews());
            assertEquals(15L, dto.getApprovedReviews());
            assertEquals(3L, dto.getPendingReviews());
            assertEquals(1L, dto.getRejectedReviews());
            assertNotNull(dto.getRatingDistribution());
        }
    }

    @Nested
    @DisplayName("RatingDistribution Tests")
    class RatingDistributionTests {
        
        @Test
        @DisplayName("Should create using builder")
        void shouldCreateUsingBuilder() {
            RatingDistribution dto = RatingDistribution.builder()
                    .fiveStars(100L)
                    .fourStars(50L)
                    .threeStars(25L)
                    .twoStars(10L)
                    .oneStar(5L)
                    .build();
            
            assertEquals(100L, dto.getFiveStars());
            assertEquals(50L, dto.getFourStars());
            assertEquals(25L, dto.getThreeStars());
            assertEquals(10L, dto.getTwoStars());
            assertEquals(5L, dto.getOneStar());
        }
    }

    @Nested
    @DisplayName("ReviewResponseDTO Tests")
    class ReviewResponseDTOTests {
        
        @Test
        @DisplayName("Should create using builder")
        void shouldCreateUsingBuilder() {
            LocalDateTime now = LocalDateTime.now();
            ReviewResponseDTO dto = ReviewResponseDTO.builder()
                    .reviewId(1L)
                    .bookId(10L)
                    .userId(123L)
                    .rating(5)
                    .comment("Great book!")
                    .status("APPROVED")
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            
            assertEquals(1L, dto.getReviewId());
            assertEquals(10L, dto.getBookId());
            assertEquals(123L, dto.getUserId());
            assertEquals(5, dto.getRating());
            assertEquals("Great book!", dto.getComment());
            assertEquals("APPROVED", dto.getStatus());
            assertEquals(now, dto.getCreatedAt());
            assertEquals(now, dto.getUpdatedAt());
        }
    }
}
