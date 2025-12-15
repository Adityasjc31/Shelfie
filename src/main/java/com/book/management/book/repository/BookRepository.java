package com.book.management.book.repository;

import java.util.List;
import java.util.Optional;

import com.book.management.book.model.Book;

public interface BookRepository {

    // Create
    Book save(Book book);

    // Read
    List<Book> findAll();
    Optional<Book> findById(long bookId);
    List<Book> findByAuthorId(String authorId);
    List<Book> findByCategoryId(String categoryId);
    List<Book> searchByTitle(String titlePart);

    // Update
    Book update(Book book);

    // Delete
    void deleteById(long bookId);

}
