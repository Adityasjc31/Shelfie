package com.book.management.review_rating.repository;

import com.book.management.review_rating.model.Review;
import com.book.management.review_rating.model.Review.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Review entity.
 * Provides automatic CRUD operations and custom query methods.
 * All queries filter out soft-deleted reviews (isDeleted = false).
 * 
 * Microservices Architecture Benefits:
 * - No boilerplate code needed
 * - Transaction management handled automatically
 * - Query optimization through JPA
 * - Easy to test with embedded databases
 * 
 * @author Aditya Srivastava
 * @version 2.1 - Added soft delete support
 * @since 2024-12-16
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds a review by ID (excludes deleted).
     */
    @Query("SELECT r FROM Review r WHERE r.reviewId = :id AND r.isDeleted = false")
    Optional<Review> findActiveById(@Param("id") Long id);

    /**
     * Finds all non-deleted reviews for a specific book.
     */
    @Query("SELECT r FROM Review r WHERE r.bookId = :bookId AND r.isDeleted = false")
    List<Review> findByBookId(@Param("bookId") Long bookId);

    /**
     * Finds all non-deleted reviews by a specific user.
     */
    @Query("SELECT r FROM Review r WHERE r.userId = :userId AND r.isDeleted = false")
    List<Review> findByUserId(@Param("userId") Long userId);

    /**
     * Finds all non-deleted reviews for a book with specific status.
     */
    @Query("SELECT r FROM Review r WHERE r.bookId = :bookId AND r.status = :status AND r.isDeleted = false")
    List<Review> findByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") ReviewStatus status);

    /**
     * Finds all non-deleted reviews by status.
     */
    @Query("SELECT r FROM Review r WHERE r.status = :status AND r.isDeleted = false")
    List<Review> findByStatus(@Param("status") ReviewStatus status);

    /**
     * Checks if a user has already reviewed a book (non-deleted).
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.userId = :userId AND r.bookId = :bookId AND r.isDeleted = false")
    boolean existsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Counts total non-deleted reviews for a book.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.bookId = :bookId AND r.isDeleted = false")
    long countByBookId(@Param("bookId") Long bookId);

    /**
     * Counts non-deleted reviews for a book with specific status.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.bookId = :bookId AND r.status = :status AND r.isDeleted = false")
    long countByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") ReviewStatus status);

    /**
     * Calculates average rating for a book (approved, non-deleted reviews only).
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.bookId = :bookId AND r.status = 'APPROVED' AND r.isDeleted = false")
    Double calculateAverageRating(@Param("bookId") Long bookId);

    /**
     * Finds all approved, non-deleted reviews for a book.
     */
    @Query("SELECT r FROM Review r WHERE r.bookId = :bookId AND r.status = 'APPROVED' AND r.isDeleted = false")
    List<Review> findApprovedReviewsByBookId(@Param("bookId") Long bookId);

    /**
     * Finds pending, non-deleted reviews for moderation.
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PENDING' AND r.isDeleted = false")
    List<Review> findPendingReviews();

    /**
     * Soft deletes all reviews by a specific user.
     */
    @Modifying
    @Query("UPDATE Review r SET r.isDeleted = true WHERE r.userId = :userId AND r.isDeleted = false")
    int softDeleteByUserId(@Param("userId") Long userId);

    /**
     * Finds all non-deleted reviews.
     */
    @Query("SELECT r FROM Review r WHERE r.isDeleted = false")
    List<Review> findAllActive();
}
