package com.book.management.review_rating.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.book.management.review_rating.controller.ReviewController;
import com.book.management.review_rating.dto.BookRatingStatsDTO;
import com.book.management.review_rating.dto.ReviewCreateDTO;
import com.book.management.review_rating.dto.ReviewResponseDTO;
import com.book.management.review_rating.dto.ReviewUpdateDTO;
import com.book.management.review_rating.model.Review.ReviewStatus;
import com.book.management.review_rating.service.ReviewService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ReviewController.
 * Uses MockMvc for testing REST endpoints with JUnit 5 and Mockito.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    private ReviewResponseDTO responseDTO;
    private ReviewCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        responseDTO = ReviewResponseDTO.builder()
                .reviewId(1L)
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book!")
                .status(ReviewStatus.PENDING)
                .helpfulCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDTO = ReviewCreateDTO.builder()
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book! Highly recommended.")
                .build();
    }

    @Test
    void testCreateReview() throws Exception {
        // Arrange
        when(reviewService.createReview(any(ReviewCreateDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(1L))
                .andExpect(jsonPath("$.userId").value(1001L))
                .andExpect(jsonPath("$.bookId").value(101L))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService, times(1)).createReview(any(ReviewCreateDTO.class));
    }

    @Test
    void testGetReviewById() throws Exception {
        // Arrange
        when(reviewService.getReviewById(1L)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/{reviewId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1L))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService, times(1)).getReviewById(1L);
    }

    @Test
    void testGetAllReviews() throws Exception {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO, responseDTO);
        when(reviewService.getAllReviews()).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(reviewService, times(1)).getAllReviews();
    }

    @Test
    void testGetReviewsByBookId() throws Exception {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO);
        when(reviewService.getReviewsByBookId(101L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/book/{bookId}", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookId").value(101L));

        verify(reviewService, times(1)).getReviewsByBookId(101L);
    }

    @Test
    void testGetApprovedReviewsByBookId() throws Exception {
        // Arrange
        ReviewResponseDTO approvedReview = ReviewResponseDTO.builder()
                .reviewId(1L)
                .bookId(101L)
                .status(ReviewStatus.APPROVED)
                .rating(5)
                .build();
        List<ReviewResponseDTO> reviews = Arrays.asList(approvedReview);
        when(reviewService.getApprovedReviewsByBookId(101L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/book/{bookId}/approved", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(reviewService, times(1)).getApprovedReviewsByBookId(101L);
    }

    @Test
    void testGetReviewsByUserId() throws Exception {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO);
        when(reviewService.getReviewsByUserId(1001L)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/user/{userId}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1001L));

        verify(reviewService, times(1)).getReviewsByUserId(1001L);
    }

    @Test
    void testGetPendingReviews() throws Exception {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO);
        when(reviewService.getPendingReviews()).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(reviewService, times(1)).getPendingReviews();
    }

    @Test
    void testGetReviewsByRating() throws Exception {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(responseDTO);
        when(reviewService.getReviewsByRating(5)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/rating/{rating}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(5));

        verify(reviewService, times(1)).getReviewsByRating(5);
    }

    @Test
    void testUpdateReview() throws Exception {
        // Arrange
        ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Updated comment - still great!")
                .build();
        ReviewResponseDTO updatedResponse = ReviewResponseDTO.builder()
                .reviewId(1L)
                .rating(4)
                .comment("Updated comment - still great!")
                .build();
        when(reviewService.updateReview(eq(1L), eq(1001L), any(ReviewUpdateDTO.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/reviews/{reviewId}", 1L)
                        .param("userId", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4));

        verify(reviewService, times(1)).updateReview(eq(1L), eq(1001L), any(ReviewUpdateDTO.class));
    }

    @Test
    void testApproveReview() throws Exception {
        // Arrange
        ReviewResponseDTO approvedResponse = ReviewResponseDTO.builder()
                .reviewId(1L)
                .status(ReviewStatus.APPROVED)
                .moderatedBy(2001L)
                .build();
        when(reviewService.approveReview(1L, 2001L)).thenReturn(approvedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/reviews/{reviewId}/approve", 1L)
                        .param("moderatorId", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(reviewService, times(1)).approveReview(1L, 2001L);
    }

    @Test
    void testRejectReview() throws Exception {
        // Arrange
        ReviewResponseDTO rejectedResponse = ReviewResponseDTO.builder()
                .reviewId(1L)
                .status(ReviewStatus.REJECTED)
                .moderatedBy(2001L)
                .rejectionReason("Inappropriate content")
                .build();
        when(reviewService.rejectReview(1L, 2001L, "Inappropriate content"))
                .thenReturn(rejectedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/reviews/{reviewId}/reject", 1L)
                        .param("moderatorId", "2001")
                        .param("reason", "Inappropriate content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Inappropriate content"));

        verify(reviewService, times(1)).rejectReview(1L, 2001L, "Inappropriate content");
    }

    @Test
    void testMarkReviewAsHelpful() throws Exception {
        // Arrange
        ReviewResponseDTO helpfulResponse = ReviewResponseDTO.builder()
                .reviewId(1L)
                .helpfulCount(1)
                .build();
        when(reviewService.markReviewAsHelpful(1L)).thenReturn(helpfulResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/reviews/{reviewId}/helpful", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.helpfulCount").value(1));

        verify(reviewService, times(1)).markReviewAsHelpful(1L);
    }

    @Test
    void testGetBookRatingStats() throws Exception {
        // Arrange
        BookRatingStatsDTO stats = BookRatingStatsDTO.builder()
                .bookId(101L)
                .averageRating(4.5)
                .totalReviews(10L)
                .approvedReviews(8L)
                .build();
        when(reviewService.getBookRatingStats(101L)).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/book/{bookId}/stats", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(101L))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.totalReviews").value(10));

        verify(reviewService, times(1)).getBookRatingStats(101L);
    }

    @Test
    void testCalculateAverageRating() throws Exception {
        // Arrange
        when(reviewService.calculateAverageRating(101L)).thenReturn(4.5);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/book/{bookId}/average-rating", 101L))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));

        verify(reviewService, times(1)).calculateAverageRating(101L);
    }

    @Test
    void testDeleteReview() throws Exception {
        // Arrange
        doNothing().when(reviewService).deleteReview(1L, 1001L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", 1L)
                        .param("userId", "1001"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(1L, 1001L);
    }

    @Test
    void testDeleteReviewByAdmin() throws Exception {
        // Arrange
        doNothing().when(reviewService).deleteReviewByAdmin(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/reviews/{reviewId}/admin", 1L))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReviewByAdmin(1L);
    }
}