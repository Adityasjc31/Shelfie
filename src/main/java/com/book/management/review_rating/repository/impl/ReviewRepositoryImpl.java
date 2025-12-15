package com.book.management.review_rating.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.book.management.review_rating.model.Review;
import com.book.management.review_rating.model.Review.ReviewStatus;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository class for Review entity using in-memory data structures.
 * Uses ConcurrentHashMap for thread-safe operations.
 * Can be easily replaced with JPA repository interface later.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Repository
@Slf4j
public class ReviewRepositoryImpl {

    // In-memory storage
    private final Map<Long, Review> reviewStore = new ConcurrentHashMap<>();
    
    // Indexes for quick lookups
    private final Map<Long, Set<Long>> bookIdToReviewIdsIndex = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> userIdToReviewIdsIndex = new ConcurrentHashMap<>();
    
    // Auto-increment ID generator
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Initializes repository with sample review data.
     */
    @PostConstruct
    public void initializeSampleData() {
        log.info("Initializing repository with sample review data");
        
        // Sample reviews for different books and users
        createSampleReview(1001L, 101L, 5, "Excellent book! Highly recommended.", ReviewStatus.APPROVED);
        createSampleReview(1002L, 101L, 4, "Great read, very informative.", ReviewStatus.APPROVED);
        createSampleReview(1003L, 102L, 5, "One of the best books I've read!", ReviewStatus.APPROVED);
        createSampleReview(1001L, 103L, 3, "Good but could be better.", ReviewStatus.APPROVED);
        createSampleReview(1004L, 104L, 2, "Not what I expected.", ReviewStatus.PENDING);
        createSampleReview(1005L, 105L, 5, "Masterpiece! Must read.", ReviewStatus.APPROVED);
        createSampleReview(1003L, 106L, 4, "Very helpful and practical.", ReviewStatus.APPROVED);
        createSampleReview(1006L, 107L, 1, "Terrible content and poor quality.", ReviewStatus.REJECTED);
        createSampleReview(1002L, 108L, 4, "Well written and engaging.", ReviewStatus.PENDING);
        createSampleReview(1007L, 109L, 5, "Absolutely fantastic!", ReviewStatus.APPROVED);
        
        log.info("Sample data initialized: {} review records", reviewStore.size());
    }

    /**
     * Helper method to create sample review.
     */
    private void createSampleReview(Long userId, Long bookId, Integer rating, 
                                   String comment, ReviewStatus status) {
        Review review = Review.builder()
                .reviewId(idGenerator.getAndIncrement())
                .userId(userId)
                .bookId(bookId)
                .rating(rating)
                .comment(comment)
                .status(status)
                .helpfulCount(new Random().nextInt(20))
                .createdAt(LocalDateTime.now().minusDays(new Random().nextInt(30)))
                .updatedAt(LocalDateTime.now().minusDays(new Random().nextInt(15)))
                .build();
        
        reviewStore.put(review.getReviewId(), review);
        
        // Update indexes
        bookIdToReviewIdsIndex.computeIfAbsent(bookId, k -> ConcurrentHashMap.newKeySet())
                .add(review.getReviewId());
        userIdToReviewIdsIndex.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(review.getReviewId());
    }

    /**
     * Saves review record (create or update).
     * 
     * @param review the review to save
     * @return the saved review
     */
    public Review save(Review review) {
        if (review.getReviewId() == null) {
            review.setReviewId(idGenerator.getAndIncrement());
            review.setCreatedAt(LocalDateTime.now());
        }
        review.setUpdatedAt(LocalDateTime.now());
        
        reviewStore.put(review.getReviewId(), review);
        
        // Update indexes
        bookIdToReviewIdsIndex.computeIfAbsent(review.getBookId(), k -> ConcurrentHashMap.newKeySet())
                .add(review.getReviewId());
        userIdToReviewIdsIndex.computeIfAbsent(review.getUserId(), k -> ConcurrentHashMap.newKeySet())
                .add(review.getReviewId());
        
        log.debug("Saved review: {}", review);
        return review;
    }

    /**
     * Finds review by ID.
     * 
     * @param reviewId the review ID
     * @return Optional containing the review if found
     */
    public Optional<Review> findById(Long reviewId) {
        return Optional.ofNullable(reviewStore.get(reviewId));
    }

    /**
     * Finds all reviews.
     * 
     * @return List of all reviews
     */
    public List<Review> findAll() {
        return new ArrayList<>(reviewStore.values());
    }

    /**
     * Finds all reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return List of reviews for the book
     */
    public List<Review> findByBookId(Long bookId) {
        Set<Long> reviewIds = bookIdToReviewIdsIndex.getOrDefault(bookId, Collections.emptySet());
        return reviewIds.stream()
                .map(reviewStore::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Finds all reviews by a specific user.
     * 
     * @param userId the user ID
     * @return List of reviews by the user
     */
    public List<Review> findByUserId(Long userId) {
        Set<Long> reviewIds = userIdToReviewIdsIndex.getOrDefault(userId, Collections.emptySet());
        return reviewIds.stream()
                .map(reviewStore::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Finds all approved reviews for a book.
     * 
     * @param bookId the book ID
     * @return List of approved reviews
     */
    public List<Review> findApprovedReviewsByBookId(Long bookId) {
        return findByBookId(bookId).stream()
                .filter(Review::isApproved)
                .collect(Collectors.toList());
    }

    /**
     * Finds all reviews by status.
     * 
     * @param status the review status
     * @return List of reviews with the given status
     */
    public List<Review> findByStatus(ReviewStatus status) {
        return reviewStore.values().stream()
                .filter(review -> review.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Finds pending reviews (for moderation).
     * 
     * @return List of pending reviews
     */
    public List<Review> findPendingReviews() {
        return findByStatus(ReviewStatus.PENDING);
    }

    /**
     * Finds reviews by rating.
     * 
     * @param rating the rating (1-5)
     * @return List of reviews with the given rating
     */
    public List<Review> findByRating(Integer rating) {
        return reviewStore.values().stream()
                .filter(review -> review.getRating().equals(rating))
                .collect(Collectors.toList());
    }

    /**
     * Calculates average rating for a book.
     * 
     * @param bookId the book ID
     * @return average rating, or 0.0 if no reviews
     */
    public Double calculateAverageRating(Long bookId) {
        List<Review> reviews = findApprovedReviewsByBookId(bookId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Counts total reviews for a book.
     * 
     * @param bookId the book ID
     * @return count of reviews
     */
    public Long countReviewsByBookId(Long bookId) {
        return (long) findByBookId(bookId).size();
    }

    /**
     * Counts approved reviews for a book.
     * 
     * @param bookId the book ID
     * @return count of approved reviews
     */
    public Long countApprovedReviewsByBookId(Long bookId) {
        return (long) findApprovedReviewsByBookId(bookId).size();
    }

    /**
     * Checks if a user has already reviewed a book.
     * 
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if user has reviewed the book
     */
    public boolean existsByUserIdAndBookId(Long userId, Long bookId) {
        return findByUserId(userId).stream()
                .anyMatch(review -> review.getBookId().equals(bookId));
    }

    /**
     * Checks if review exists by ID.
     * 
     * @param reviewId the review ID
     * @return true if exists
     */
    public boolean existsById(Long reviewId) {
        return reviewStore.containsKey(reviewId);
    }

    /**
     * Deletes review by ID.
     * 
     * @param reviewId the review ID
     */
    public void deleteById(Long reviewId) {
        Review review = reviewStore.get(reviewId);
        if (review != null) {
            // Remove from indexes
            Set<Long> bookReviews = bookIdToReviewIdsIndex.get(review.getBookId());
            if (bookReviews != null) {
                bookReviews.remove(reviewId);
            }
            
            Set<Long> userReviews = userIdToReviewIdsIndex.get(review.getUserId());
            if (userReviews != null) {
                userReviews.remove(reviewId);
            }
            
            reviewStore.remove(reviewId);
            log.debug("Deleted review with ID: {}", reviewId);
        }
    }

    /**
     * Deletes all reviews.
     */
    public void deleteAll() {
        reviewStore.clear();
        bookIdToReviewIdsIndex.clear();
        userIdToReviewIdsIndex.clear();
        log.debug("Deleted all reviews");
    }

    /**
     * Counts total reviews.
     * 
     * @return count of reviews
     */
    public long count() {
        return reviewStore.size();
    }
}