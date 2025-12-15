package com.db.ms.bookModule.repository.impl;

import com.db.ms.bookModule.model.Book;
import com.db.ms.bookModule.repository.BookRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBookRepository implements BookRepository {

    private final Map<Long, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(100L); // starting id

    @Override
    public Book save(Book book) {
        long id = (book.getBookId() > 0) ? book.getBookId() : sequence.incrementAndGet();
        store.put(id, book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Book> findById(long bookId) {
        return Optional.ofNullable(store.get(bookId));
    }

    @Override
    public List<Book> findByAuthorId(String authorId) {
        String normalized = authorId == null ? "" : authorId.trim();
        return store.values().stream()
                .filter(b -> Objects.equals(b.getBookAuthorId(), normalized))
                .toList();
    }

    @Override
    public List<Book> findByCategoryId(String categoryId) {
        String normalized = categoryId == null ? "" : categoryId.trim();
        return store.values().stream()
                .filter(b -> Objects.equals(b.getBookCategoryId(), normalized))
                .toList();
    }

    @Override
    public List<Book> searchByTitle(String titlePart) {
        String q = titlePart == null ? "" : titlePart.trim().toLowerCase(Locale.ROOT);
        return store.values().stream()
                .filter(b -> b.getBookTitle() != null &&
                        b.getBookTitle().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    @Override
    public Book update(Book book) {
        if (!store.containsKey(book.getBookId())) {
            throw new NoSuchElementException("Book not found: " + book.getBookId());
        }
        store.put(book.getBookId(), book);
        return book;
    }

    @Override
    public void deleteById(long bookId) {
        store.remove(bookId);
    }
}
