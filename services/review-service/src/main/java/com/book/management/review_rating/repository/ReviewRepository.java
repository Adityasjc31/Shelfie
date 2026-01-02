package com.book.management.review_rating.repository;

import com.book.management.review_rating.model.Review;
import com.book.management.review_rating.model.Review.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Review entity.
 * Provides automatic CRUD operations and custom query methods.
 * 
 * Microservices Architecture Benefits:
 * - No boilerplate code needed
 * - Transaction management handled automatically
 * - Query optimization through JPA
 * - Easy to test with embedded databases
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-16
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews for a specific book.
     */
    List<Review> findByBookId(Long bookId);

    /**
     * Finds all reviews by a specific user.
     */
    List<Review> findByUserId(Long userId);

    /**
     * Finds all approved reviews for a book.
     */
    List<Review> findByBookIdAndStatus(Long bookId, ReviewStatus status);

    /**
     * Finds all reviews by status.
     */
    List<Review> findByStatus(ReviewStatus status);

    /**
     * Finds all reviews with a specific rating.
     */
    List<Review> findByRating(Integer rating);

    /**
     * Checks if a user has already reviewed a book.
     */
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Counts total reviews for a book.
     */
    long countByBookId(Long bookId);

    /**
     * Counts approved reviews for a book.
     */
    long countByBookIdAndStatus(Long bookId, ReviewStatus status);

    /**
     * Calculates average rating for a book (approved reviews only).
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.bookId = :bookId AND r.status = 'APPROVED'")
    Double calculateAverageRating(@Param("bookId") Long bookId);

    /**
     * Finds all approved reviews for a book.
     */
    @Query("SELECT r FROM Review r WHERE r.bookId = :bookId AND r.status = 'APPROVED'")
    List<Review> findApprovedReviewsByBookId(@Param("bookId") Long bookId);

    /**
     * Finds pending reviews for moderation.
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PENDING'")
    List<Review> findPendingReviews();

    /**
     * Counts reviews by rating for a book (for distribution statistics).
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.bookId = :bookId AND r.status = 'APPROVED' GROUP BY r.rating")
    List<Object[]> countReviewsByRating(@Param("bookId") Long bookId);
}