package com.book.management.book.repository;

import com.book.management.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Book entity.
 * Provides CRUD operations via Spring Data JPA.
 * 
 * @author Aditya Srivastava
 * @version 2.0 - Converted from in-memory to JPA
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find books by author ID.
     */
    List<Book> findByBookAuthorId(String authorId);

    /**
     * Find books by category ID.
     */
    List<Book> findByBookCategoryId(String categoryId);

    /**
     * Search books by title (case-insensitive partial match).
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%', :titlePart, '%'))")
    List<Book> searchByTitle(@Param("titlePart") String titlePart);

    List<Book> findByBookTitleContainingIgnoreCase(String title);
}
