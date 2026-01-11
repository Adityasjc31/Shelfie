# Spring Data JPA Interview Questions

This document contains 50 comprehensive interview questions covering Spring Data JPA concepts from beginner to advanced levels.

---

## Q1. What is Spring Data JPA? (Beginner)

**Answer:**

Spring Data JPA is a part of the larger Spring Data family that makes it easy to implement JPA-based repositories. It abstracts much of the boilerplate code required for data access layers, allowing developers to focus on business logic.

**Key Features:**
- **Repository Abstraction**: Provides repository interfaces (`JpaRepository`, `CrudRepository`) that eliminate boilerplate CRUD operations
- **Query Derivation**: Automatically generates queries from method names
- **Custom Queries**: Support for JPQL, native SQL, and `@Query` annotation
- **Pagination & Sorting**: Built-in support for pageable results
- **Auditing**: Automatic population of created/modified timestamps

```java
// Example: Simple Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor(String author);
}
```

---

## Q2. What is the difference between JPA, Hibernate, and Spring Data JPA? (Beginner)

**Answer:**

| Aspect | JPA | Hibernate | Spring Data JPA |
|--------|-----|-----------|-----------------|
| **Type** | Specification | Implementation | Abstraction Layer |
| **Purpose** | Defines ORM standard | Implements JPA spec | Simplifies JPA usage |
| **Provides** | Interfaces & Annotations | Actual functionality | Repository pattern |
| **Dependency** | Just API | Can work standalone | Requires JPA provider |

**Relationship:**
```
Spring Data JPA → Uses → JPA (Specification) → Implemented by → Hibernate
```

- **JPA (Java Persistence API)**: A specification that defines how Java objects should be persisted to relational databases
- **Hibernate**: An ORM framework that implements the JPA specification (also has proprietary features)
- **Spring Data JPA**: A Spring module that sits on top of JPA, providing repository abstraction and reducing boilerplate

---

## Q3. What are the core repository interfaces in Spring Data JPA? (Beginner)

**Answer:**

Spring Data JPA provides a hierarchy of repository interfaces:

```
Repository (marker interface)
    └── CrudRepository (basic CRUD operations)
            └── PagingAndSortingRepository (pagination & sorting)
                    └── JpaRepository (JPA-specific methods)
```

**1. Repository<T, ID>**
- Marker interface with no methods
- Used for custom repository definitions

**2. CrudRepository<T, ID>**
```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);
    Optional<T> findById(ID id);
    Iterable<T> findAll();
    long count();
    void deleteById(ID id);
    boolean existsById(ID id);
}
```

**3. PagingAndSortingRepository<T, ID>**
```java
public interface PagingAndSortingRepository<T, ID> extends CrudRepository<T, ID> {
    Iterable<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
}
```

**4. JpaRepository<T, ID>**
```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
    List<T> findAll();
    List<T> findAllById(Iterable<ID> ids);
    <S extends T> List<S> saveAll(Iterable<S> entities);
    void flush();
    <S extends T> S saveAndFlush(S entity);
    void deleteInBatch(Iterable<T> entities);
    void deleteAllInBatch();
    T getOne(ID id); // Returns reference (lazy loading)
}
```

---

## Q4. What is derived query method in Spring Data JPA? (Beginner)

**Answer:**

Derived query methods are repository methods where Spring Data JPA automatically generates the query based on the method name. The method name is parsed and translated into a database query.

**Syntax Structure:**
```
find/read/get/query/stream + By + PropertyExpression + Condition
```

**Examples:**

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Simple property match
    List<User> findByEmail(String email);
    // SELECT u FROM User u WHERE u.email = ?1
    
    // Multiple conditions with AND
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
    // SELECT u FROM User u WHERE u.firstName = ?1 AND u.lastName = ?2
    
    // Multiple conditions with OR
    List<User> findByEmailOrUsername(String email, String username);
    
    // Comparison operators
    List<User> findByAgeLessThan(int age);
    List<User> findByAgeGreaterThanEqual(int age);
    List<User> findByAgeBetween(int start, int end);
    
    // String operations
    List<User> findByEmailContaining(String keyword);
    List<User> findByEmailStartingWith(String prefix);
    List<User> findByEmailEndingWith(String suffix);
    List<User> findByEmailIgnoreCase(String email);
    
    // Null checks
    List<User> findByPhoneIsNull();
    List<User> findByPhoneIsNotNull();
    
    // Collection operations
    List<User> findByAgeIn(Collection<Integer> ages);
    List<User> findByAgeNotIn(Collection<Integer> ages);
    
    // Boolean checks
    List<User> findByActiveTrue();
    List<User> findByActiveFalse();
    
    // Ordering
    List<User> findByLastNameOrderByFirstNameAsc(String lastName);
    
    // Limiting results
    User findFirstByOrderByCreatedDateDesc();
    List<User> findTop5ByOrderByAgeDesc();
    
    // Nested properties
    List<User> findByAddressCity(String city);
    // Traverses user.address.city
}
```

---

## Q5. What is the @Query annotation and when should you use it? (Beginner)

**Answer:**

The `@Query` annotation allows you to define custom JPQL or native SQL queries directly on repository methods when derived queries become too complex or don't express your intent clearly.

**Use Cases:**
1. Complex queries that can't be expressed with method names
2. Queries requiring JOINs across multiple entities
3. Aggregate functions and grouping
4. Performance optimization with specific query patterns

**JPQL Examples:**

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Positional parameters
    @Query("SELECT b FROM Book b WHERE b.author = ?1 AND b.year > ?2")
    List<Book> findBooksByAuthorAndYear(String author, int year);
    
    // Named parameters (preferred)
    @Query("SELECT b FROM Book b WHERE b.author = :author AND b.price < :maxPrice")
    List<Book> findAffordableBooks(@Param("author") String author, 
                                   @Param("maxPrice") BigDecimal maxPrice);
    
    // JOIN queries
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.name = :category")
    List<Book> findByCategory(@Param("category") String category);
    
    // Aggregate functions
    @Query("SELECT AVG(b.price) FROM Book b WHERE b.author = :author")
    Double findAveragePriceByAuthor(@Param("author") String author);
    
    // Using LIKE
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword%")
    List<Book> searchByTitle(@Param("keyword") String keyword);
    
    // DTO Projection
    @Query("SELECT new com.example.dto.BookSummary(b.id, b.title, b.author) FROM Book b")
    List<BookSummary> findAllBookSummaries();
}
```

**Native SQL Queries:**

```java
@Query(value = "SELECT * FROM books WHERE YEAR(published_date) = :year", 
       nativeQuery = true)
List<Book> findByPublishedYear(@Param("year") int year);

// With pagination
@Query(value = "SELECT * FROM books ORDER BY created_at DESC",
       countQuery = "SELECT COUNT(*) FROM books",
       nativeQuery = true)
Page<Book> findAllBooksNative(Pageable pageable);
```

---

## Q6. What is the difference between save() and saveAndFlush()? (Beginner)

**Answer:**

| Aspect | save() | saveAndFlush() |
|--------|--------|----------------|
| **Persistence** | Marks entity for persistence | Persists immediately |
| **Flush Timing** | Delayed until transaction commit | Immediate |
| **Database Round-trip** | Batched | Immediate |
| **Use Case** | Normal operations | Need ID immediately |

**save() Method:**
- Persists the entity in the persistence context (first-level cache)
- Changes are synchronized with the database when:
  - Transaction commits
  - `flush()` is called explicitly
  - A query is executed requiring fresh data

```java
@Transactional
public void demonstrateSave() {
    Book book = new Book("Spring in Action");
    Book savedBook = bookRepository.save(book);
    // At this point, the book may not be in the database yet
    // ID might be assigned (if GenerationType.IDENTITY) or not
    
    // Changes are flushed when transaction commits
}
```

**saveAndFlush() Method:**
- Immediately flushes changes to the database
- Useful when you need the generated ID right away
- Useful when subsequent operations depend on the persisted state

```java
@Transactional
public void demonstrateSaveAndFlush() {
    Book book = new Book("Spring Boot Guide");
    Book savedBook = bookRepository.saveAndFlush(book);
    // Book is immediately persisted in the database
    // savedBook.getId() is guaranteed to be populated
    
    // Use the ID for subsequent operations
    auditService.logCreation(savedBook.getId());
}
```

**Performance Consideration:**
```java
// Inefficient - multiple database round-trips
for (Book book : books) {
    bookRepository.saveAndFlush(book);
}

// Efficient - single batch operation
bookRepository.saveAll(books);
// Changes flushed at transaction commit
```

---

## Q7. What is the N+1 query problem and how do you solve it? (Intermediate)

**Answer:**

The N+1 problem occurs when fetching a parent entity triggers N additional queries to fetch related child entities, resulting in poor performance.

**Example of the Problem:**

```java
@Entity
public class Author {
    @Id
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "author")
    private List<Book> books; // LAZY by default for collections
}

// This triggers N+1 queries
List<Author> authors = authorRepository.findAll(); // 1 query for authors
for (Author author : authors) {
    System.out.println(author.getBooks().size()); // N queries for books
}
// Total: 1 + N queries
```

**Solutions:**

**1. JOIN FETCH (Recommended)**
```java
@Query("SELECT a FROM Author a JOIN FETCH a.books")
List<Author> findAllWithBooks();
// Single query with JOIN
```

**2. @EntityGraph**
```java
@EntityGraph(attributePaths = {"books"})
@Query("SELECT a FROM Author a")
List<Author> findAllWithBooksGraph();

// Or define a named entity graph
@Entity
@NamedEntityGraph(name = "Author.books", 
    attributeNodes = @NamedAttributeNode("books"))
public class Author { ... }

@EntityGraph(value = "Author.books")
List<Author> findAll();
```

**3. @BatchSize (Hibernate-specific)**
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    @BatchSize(size = 25) // Fetches 25 collections at a time
    private List<Book> books;
}
// Reduces N+1 to 1 + (N/25) queries
```

**4. Subselect Fetching (Hibernate-specific)**
```java
@OneToMany(mappedBy = "author")
@Fetch(FetchMode.SUBSELECT)
private List<Book> books;
// Uses subquery to fetch all collections in one go
```

---

## Q8. What is the difference between FetchType.LAZY and FetchType.EAGER? (Intermediate)

**Answer:**

| Aspect | LAZY | EAGER |
|--------|------|-------|
| **Loading** | On-demand | Immediate |
| **Performance** | Better for large relations | Risk of N+1 |
| **Memory** | Lower | Higher |
| **Default For** | @OneToMany, @ManyToMany | @ManyToOne, @OneToOne |
| **Outside Transaction** | LazyInitializationException | Works fine |

**LAZY Loading:**
```java
@Entity
public class Order {
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items; // Loaded only when accessed
}

@Transactional
public void processOrder(Long orderId) {
    Order order = orderRepository.findById(orderId).get();
    // items NOT loaded yet (proxy)
    
    int itemCount = order.getItems().size();
    // NOW items are loaded (query executed)
}
```

**LazyInitializationException:**
```java
public Order getOrder(Long id) {
    return orderRepository.findById(id).get();
}

// In another class without transaction
public void processOrder() {
    Order order = getOrder(1L);
    order.getItems().size(); // EXCEPTION! Session closed
}
```

**Solutions to LazyInitializationException:**
```java
// 1. Use @Transactional
@Transactional(readOnly = true)
public void processOrder(Long orderId) {
    Order order = orderRepository.findById(orderId).get();
    order.getItems().size(); // Works - session still open
}

// 2. Use JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Optional<Order> findByIdWithItems(@Param("id") Long id);

// 3. Use @EntityGraph
@EntityGraph(attributePaths = {"items"})
Optional<Order> findById(Long id);

// 4. Initialize before closing session
@Transactional
public Order getOrderWithItems(Long id) {
    Order order = orderRepository.findById(id).get();
    Hibernate.initialize(order.getItems()); // Force initialization
    return order;
}
```

---

## Q9. What are entity lifecycle callbacks in JPA? (Intermediate)

**Answer:**

JPA provides callback annotations that allow you to execute code at specific points in an entity's lifecycle.

**Lifecycle Callbacks:**

| Callback | Trigger Point |
|----------|--------------|
| `@PrePersist` | Before INSERT |
| `@PostPersist` | After INSERT |
| `@PreUpdate` | Before UPDATE |
| `@PostUpdate` | After UPDATE |
| `@PreRemove` | Before DELETE |
| `@PostRemove` | After DELETE |
| `@PostLoad` | After entity is loaded |

**Example - Entity with Callbacks:**
```java
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        slug = generateSlug(title);
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        slug = generateSlug(title);
    }
    
    @PostLoad
    protected void onLoad() {
        // Initialize transient fields or perform validation
    }
    
    @PostPersist
    protected void afterPersist() {
        // Trigger async events, logging, etc.
    }
    
    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("\\s+", "-");
    }
}
```

**Using EntityListener (Separation of Concerns):**
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String modifiedBy;
}

// Custom listener
public class AuditListener {
    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof Auditable) {
            ((Auditable) entity).setCreatedAt(LocalDateTime.now());
        }
    }
    
    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof Auditable) {
            ((Auditable) entity).setUpdatedAt(LocalDateTime.now());
        }
    }
}
```

---

## Q10. What is the difference between @ManyToOne and @OneToMany? (Beginner)

**Answer:**

These annotations define the cardinality of relationships between entities.

**@ManyToOne:**
- Many instances of the current entity relate to one instance of another entity
- The owning side of the relationship (contains the foreign key)
- Default fetch type: EAGER

```java
@Entity
public class Book {
    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id") // FK in books table
    private Author author;
}
// Database: books table has author_id foreign key
```

**@OneToMany:**
- One instance of the current entity relates to many instances of another entity
- Usually the inverse (non-owning) side
- Default fetch type: LAZY

```java
@Entity
public class Author {
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}
// No FK in authors table; books.author_id references this
```

**Bidirectional Relationship Best Practices:**
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    // Helper methods for bidirectional sync
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
```

**Unidirectional @OneToMany (Avoid if possible):**
```java
@Entity
public class Author {
    @OneToMany
    @JoinColumn(name = "author_id") // Creates FK in books table
    private List<Book> books;
}
// This approach uses extra UPDATE statements and is less efficient
```

---

## Q11. What are projections in Spring Data JPA? (Intermediate)

**Answer:**

Projections allow you to retrieve partial data from entities, improving performance by selecting only required fields instead of entire entities.

**Types of Projections:**

**1. Interface-Based Projection (Closed Projection)**
```java
// Define a projection interface
public interface BookSummary {
    String getTitle();
    String getAuthor();
    BigDecimal getPrice();
}

// Use in repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<BookSummary> findByCategory(String category);
    // Only fetches title, author, price columns
}
```

**2. Interface-Based Projection (Open Projection)**
```java
public interface BookView {
    String getTitle();
    
    @Value("#{target.author + ' (' + target.publishYear + ')'}")
    String getAuthorInfo(); // SpEL expression
    
    default String getFormattedPrice() {
        return "$" + getPrice();
    }
}
```

**3. Class-Based Projection (DTO)**
```java
public class BookDTO {
    private final String title;
    private final String author;
    
    public BookDTO(String title, String author) {
        this.title = title;
        this.author = author;
    }
    // getters
}

// Using @Query with constructor expression
@Query("SELECT new com.example.dto.BookDTO(b.title, b.author) FROM Book b")
List<BookDTO> findAllBookDTOs();
```

**4. Dynamic Projections**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    <T> List<T> findByAuthor(String author, Class<T> type);
}

// Usage
List<BookSummary> summaries = bookRepo.findByAuthor("John", BookSummary.class);
List<Book> fullBooks = bookRepo.findByAuthor("John", Book.class);
```

**5. Nested Projections**
```java
public interface OrderSummary {
    Long getId();
    LocalDateTime getOrderDate();
    CustomerInfo getCustomer(); // Nested projection
    
    interface CustomerInfo {
        String getName();
        String getEmail();
    }
}
```

---

## Q12. What is the @Modifying annotation used for? (Intermediate)

**Answer:**

The `@Modifying` annotation is used with `@Query` to indicate that the query modifies the database (UPDATE, DELETE, INSERT operations) rather than selecting data.

**Key Properties:**
- `flushAutomatically`: Flush persistence context before executing (default: false)
- `clearAutomatically`: Clear persistence context after executing (default: false)

**Examples:**

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Update query
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.lastLoginDate < :date")
    int deactivateInactiveUsers(@Param("status") String status, 
                                @Param("date") LocalDateTime date);
    
    // Delete query
    @Modifying
    @Query("DELETE FROM User u WHERE u.status = 'DELETED'")
    void purgeDeletedUsers();
    
    // Bulk update with clear
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.emailVerified = true WHERE u.id = :id")
    int verifyEmail(@Param("id") Long id);
    
    // Native query
    @Modifying
    @Query(value = "UPDATE users SET login_count = login_count + 1 WHERE id = ?1", 
           nativeQuery = true)
    void incrementLoginCount(Long userId);
}
```

**Why clearAutomatically is Important:**
```java
@Transactional
public void updateAndFetch(Long userId) {
    userRepository.verifyEmail(userId); // Updates in DB
    
    // Without clearAutomatically = true:
    User user = userRepository.findById(userId).get();
    // user.isEmailVerified() might still be false (stale cache)!
    
    // With clearAutomatically = true:
    // Persistence context is cleared, fresh data is loaded
}
```

**Important Notes:**
- `@Modifying` queries must be executed within a transaction (`@Transactional`)
- They return `int` (number of affected rows) or `void`
- Bulk operations bypass entity lifecycle callbacks

---

## Q13. What is Specification in Spring Data JPA? (Advanced)

**Answer:**

Specifications provide a way to build dynamic, type-safe queries using the Criteria API. They implement the Specification pattern for composable query predicates.

**Setup:**
```java
// Repository must extend JpaSpecificationExecutor
public interface ProductRepository extends JpaRepository<Product, Long>,
                                           JpaSpecificationExecutor<Product> {
}
```

**Creating Specifications:**
```java
public class ProductSpecifications {
    
    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("category"), category);
    }
    
    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.between(root.get("price"), min, max);
    }
    
    public static Specification<Product> nameContains(String keyword) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                "%" + keyword.toLowerCase() + "%"
            );
    }
    
    public static Specification<Product> isInStock() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThan(root.get("stockQuantity"), 0);
    }
    
    // With JOIN
    public static Specification<Product> hasSupplier(String supplierName) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Supplier> supplier = root.join("supplier");
            return criteriaBuilder.equal(supplier.get("name"), supplierName);
        };
    }
}
```

**Using Specifications:**
```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> searchProducts(ProductSearchCriteria criteria) {
        Specification<Product> spec = Specification.where(null);
        
        if (criteria.getCategory() != null) {
            spec = spec.and(ProductSpecifications.hasCategory(criteria.getCategory()));
        }
        
        if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null) {
            spec = spec.and(ProductSpecifications.priceBetween(
                criteria.getMinPrice(), criteria.getMaxPrice()));
        }
        
        if (criteria.getKeyword() != null) {
            spec = spec.and(ProductSpecifications.nameContains(criteria.getKeyword()));
        }
        
        if (criteria.isOnlyInStock()) {
            spec = spec.and(ProductSpecifications.isInStock());
        }
        
        return productRepository.findAll(spec, Sort.by("name"));
    }
    
    // With pagination
    public Page<Product> searchProductsPaged(ProductSearchCriteria criteria, 
                                             Pageable pageable) {
        Specification<Product> spec = buildSpec(criteria);
        return productRepository.findAll(spec, pageable);
    }
}
```

---

## Q14. What is the difference between getOne(), findById(), and getReferenceById()? (Intermediate)

**Answer:**

| Method | Return Type | Behavior if Not Found | Loading | Use Case |
|--------|-------------|----------------------|---------|----------|
| `findById()` | `Optional<T>` | `Optional.empty()` | Eager | When you need entity data |
| `getOne()` | `T` (proxy) | `EntityNotFoundException` (on access) | Lazy | Deprecated, use getReferenceById |
| `getReferenceById()` | `T` (proxy) | `EntityNotFoundException` (on access) | Lazy | When setting relations |

**findById():**
```java
// Immediately executes SELECT query
Optional<Book> book = bookRepository.findById(1L);
book.ifPresent(b -> System.out.println(b.getTitle()));
// Safe - returns Optional.empty() if not found
```

**getReferenceById() (formerly getOne()):**
```java
// Does NOT execute query - returns a proxy
Book bookRef = bookRepository.getReferenceById(1L);
// No SQL executed yet!

// Query executed when property is accessed
String title = bookRef.getTitle(); 
// EntityNotFoundException thrown here if ID doesn't exist
```

**Use Case for getReferenceById():**
```java
// Efficient for setting relationships
@Transactional
public void assignAuthorToBook(Long bookId, Long authorId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    
    // Only need the author reference, not the full entity
    Author author = authorRepository.getReferenceById(authorId);
    // No SELECT query for author!
    
    book.setAuthor(author);
    // Only book is updated, author reference is set via FK
}
```

**Key Difference:**
```java
// findById - 2 queries
Book book = bookRepository.findById(1L).get();    // SELECT * FROM books WHERE id=1
Author author = authorRepository.findById(2L).get(); // SELECT * FROM authors WHERE id=2
book.setAuthor(author);

// getReferenceById - 1 query
Book book = bookRepository.findById(1L).get();    // SELECT * FROM books WHERE id=1
Author author = authorRepository.getReferenceById(2L); // No query!
book.setAuthor(author);
```

---

## Q15. How does pagination work in Spring Data JPA? (Beginner)

**Answer:**

Spring Data JPA provides built-in pagination support through `Pageable` and `Page` interfaces.

**Basic Pagination:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByAuthor(String author, Pageable pageable);
    
    // Slice (no total count query)
    Slice<Book> findByCategory(String category, Pageable pageable);
    
    // List with pagination (no metadata)
    List<Book> findByPublisherOrderByTitleAsc(String publisher, Pageable pageable);
}
```

**Creating Pageable:**
```java
// Simple pagination
Pageable pageable = PageRequest.of(0, 10); // page 0, 10 items per page

// With sorting
Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

// Multiple sort properties
Pageable pageable = PageRequest.of(0, 10, 
    Sort.by("author").ascending()
        .and(Sort.by("title").descending()));

// Using Sort.Order for more control
Pageable pageable = PageRequest.of(0, 10,
    Sort.by(
        Sort.Order.asc("author").ignoreCase(),
        Sort.Order.desc("publishDate").nullsLast()
    ));
```

**Using Page Result:**
```java
@GetMapping("/books")
public ResponseEntity<Map<String, Object>> getBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "title") String sortBy) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    Page<Book> bookPage = bookRepository.findAll(pageable);
    
    Map<String, Object> response = new HashMap<>();
    response.put("books", bookPage.getContent());
    response.put("currentPage", bookPage.getNumber());
    response.put("totalItems", bookPage.getTotalElements());
    response.put("totalPages", bookPage.getTotalPages());
    response.put("hasNext", bookPage.hasNext());
    response.put("hasPrevious", bookPage.hasPrevious());
    
    return ResponseEntity.ok(response);
}
```

**Page vs Slice:**
```java
// Page - executes count query (expensive for large datasets)
Page<Book> page = bookRepository.findByAuthor("John", pageable);
long totalElements = page.getTotalElements(); // Requires COUNT query

// Slice - no count query (better for infinite scroll)
Slice<Book> slice = bookRepository.findByCategory("Fiction", pageable);
boolean hasNext = slice.hasNext(); // Fetches n+1 records to determine
```

---

## Q16. What is the cascade type in JPA and what are the different options? (Intermediate)

**Answer:**

Cascade types define how persistence operations on a parent entity should propagate to related child entities.

**Cascade Types:**

| Type | Description |
|------|-------------|
| `PERSIST` | Cascade persist operation |
| `MERGE` | Cascade merge operation |
| `REMOVE` | Cascade remove operation |
| `REFRESH` | Cascade refresh operation |
| `DETACH` | Cascade detach operation |
| `ALL` | All of the above |

**Examples:**

```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // CASCADE PERSIST - When order is saved, items are saved too
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> items = new ArrayList<>();
    
    // CASCADE ALL - Full lifecycle management
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderNote> notes = new ArrayList<>();
}
```

**CascadeType.PERSIST:**
```java
Order order = new Order();
OrderItem item = new OrderItem("Product A", 2);
order.getItems().add(item);
item.setOrder(order);

orderRepository.save(order);
// Both order AND item are persisted
```

**CascadeType.REMOVE:**
```java
@OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
private List<OrderItem> items;

orderRepository.deleteById(orderId);
// Order deleted, AND all related items are deleted
```

**CascadeType.MERGE:**
```java
Order detachedOrder = // ... order from different session
detachedOrder.getItems().get(0).setQuantity(5);

Order mergedOrder = orderRepository.save(detachedOrder);
// Both order AND modified items are merged
```

**orphanRemoval vs CascadeType.REMOVE:**
```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items;

// orphanRemoval = true
order.getItems().remove(0); // Removes from collection
orderRepository.save(order);
// The removed item is DELETED from database

// Without orphanRemoval
order.getItems().remove(0);
orderRepository.save(order);
// Item still exists in database (just FK set to null or orphaned)
```

**Best Practices:**
```java
// Avoid CASCADE on @ManyToOne
@ManyToOne
@JoinColumn(name = "customer_id")
private Customer customer; // No cascade - customer lifecycle is independent

// Use CASCADE on @OneToMany for parent-child relationships
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items; // Items belong to order
```

---

## Q17. What is the difference between @JoinColumn and @JoinTable? (Intermediate)

**Answer:**

| Aspect | @JoinColumn | @JoinTable |
|--------|-------------|------------|
| **Creates** | Foreign key in entity table | Separate join table |
| **Used For** | @ManyToOne, @OneToOne, unidirectional @OneToMany | @ManyToMany, some @OneToMany |
| **Storage** | FK in one entity's table | Separate table with two FKs |

**@JoinColumn:**
```java
@Entity
public class Book {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(
        name = "author_id",          // FK column name
        referencedColumnName = "id", // PK in Author (default)
        nullable = false,            // NOT NULL constraint
        foreignKey = @ForeignKey(name = "fk_book_author")
    )
    private Author author;
}
// Result: books table has author_id column
```

**@JoinTable (for @ManyToMany):**
```java
@Entity
public class Book {
    @Id
    private Long id;
    
    @ManyToMany
    @JoinTable(
        name = "book_categories",           // Join table name
        joinColumns = @JoinColumn(name = "book_id"),        // FK to Book
        inverseJoinColumns = @JoinColumn(name = "category_id") // FK to Category
    )
    private Set<Category> categories = new HashSet<>();
}

@Entity
public class Category {
    @Id
    private Long id;
    
    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new HashSet<>();
}
// Result: Creates book_categories table with book_id and category_id columns
```

**@JoinTable for @OneToMany (unidirectional without FK in child):**
```java
@Entity
public class Employee {
    @Id
    private Long id;
    
    @OneToMany
    @JoinTable(
        name = "employee_phones",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "phone_id")
    )
    private List<Phone> phones;
}
// Result: phones table has no employee_id
// Instead, employee_phones join table manages relationship
```

**Composite Join Columns:**
```java
@ManyToOne
@JoinColumns({
    @JoinColumn(name = "country_code", referencedColumnName = "code"),
    @JoinColumn(name = "region_id", referencedColumnName = "id")
})
private Region region;
```

---

## Q18. What is Spring Data JPA Auditing? (Intermediate)

**Answer:**

Spring Data JPA Auditing automatically populates audit-related fields (created date, modified date, created by, modified by) without manual intervention.

**Setup:**

**1. Enable Auditing:**
```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

**2. Create Auditable Base Entity:**
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
    
    // Getters and setters
}
```

**3. Extend in Entities:**
```java
@Entity
public class Book extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String author;
    
    // Book-specific fields
}
```

**Usage:**
```java
@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Transactional
    public Book createBook(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        
        return bookRepository.save(book);
        // createdAt, createdBy automatically set
    }
    
    @Transactional
    public Book updateBook(Long id, BookDTO dto) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        
        book.setTitle(dto.getTitle());
        
        return bookRepository.save(book);
        // updatedAt, updatedBy automatically set
    }
}
```

**Custom AuditorAware for Different User Types:**
```java
@Bean
public AuditorAware<Long> auditorProvider() {
    return () -> Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(auth -> ((UserPrincipal) auth.getPrincipal()).getId());
}
```

---

## Q19. What is the difference between @Embeddable and @Entity? (Intermediate)

**Answer:**

| Aspect | @Entity | @Embeddable |
|--------|---------|-------------|
| **Identity** | Has own ID | No identity |
| **Table** | Own table | Embedded in parent table |
| **Lifecycle** | Independent | Tied to parent |
| **Relationships** | Can have any | Limited |
| **Querying** | Directly queryable | Through parent |

**@Entity:**
```java
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String street;
    private String city;
    private String zipCode;
}

@Entity
public class User {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address; // Separate table, join required
}
```

**@Embeddable:**
```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String zipCode;
    // No @Id - doesn't have own identity
}

@Entity
public class User {
    @Id
    private Long id;
    
    @Embedded
    private Address address; // Columns embedded in users table
}
// Result: users table has street, city, zipCode columns directly
```

**Attribute Overrides:**
```java
@Entity
public class Company {
    @Id
    private Long id;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip"))
    })
    private Address billingAddress;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "shipping_zip"))
    })
    private Address shippingAddress;
}
```

**Collection of Embeddables:**
```java
@Entity
public class User {
    @Id
    private Long id;
    
    @ElementCollection
    @CollectionTable(
        name = "user_addresses",
        joinColumns = @JoinColumn(name = "user_id")
    )
    private List<Address> addresses = new ArrayList<>();
}
// Creates user_addresses table with user_id FK and address columns
```

---

## Q20. What is optimistic locking and how does @Version work? (Advanced)

**Answer:**

Optimistic locking is a concurrency control strategy that allows multiple transactions to complete without locking resources, detecting conflicts at commit time.

**How @Version Works:**
```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
    private int quantity;
    
    @Version
    private Long version; // Or Integer, Short, Timestamp
}
```

**Conflict Detection:**
```java
// Transaction 1
Product product = productRepository.findById(1L).get(); // version = 0
product.setQuantity(50);

// Transaction 2 (concurrent)
Product sameProduct = productRepository.findById(1L).get(); // version = 0
sameProduct.setQuantity(40);
productRepository.save(sameProduct); // version becomes 1

// Back to Transaction 1
productRepository.save(product); 
// OptimisticLockException! version mismatch (expected 0, found 1)
```

**Generated SQL:**
```sql
-- Update includes version check
UPDATE product 
SET name = ?, price = ?, quantity = ?, version = ? 
WHERE id = ? AND version = ?

-- If WHERE clause doesn't match any rows -> OptimisticLockException
```

**Handling OptimisticLockException:**
```java
@Service
public class ProductService {
    
    public Product updateProductWithRetry(Long id, int newQuantity) {
        int maxRetries = 3;
        int attempt = 0;
        
        while (attempt < maxRetries) {
            try {
                return updateProduct(id, newQuantity);
            } catch (OptimisticLockingFailureException e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new ConcurrentModificationException(
                        "Failed to update after " + maxRetries + " attempts");
                }
                // Maybe add delay before retry
            }
        }
        throw new IllegalStateException("Update failed");
    }
    
    @Transactional
    public Product updateProduct(Long id, int newQuantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }
}
```

**Versioned Update Queries:**
```java
// Custom query with version
@Modifying
@Query("UPDATE Product p SET p.quantity = :qty, p.version = p.version + 1 " +
       "WHERE p.id = :id AND p.version = :version")
int updateQuantityWithVersion(@Param("id") Long id, 
                               @Param("qty") int quantity,
                               @Param("version") Long version);
```

---

## Q21. What is @Transactional and how does it work in Spring Data JPA? (Intermediate)

**Answer:**

`@Transactional` defines transaction boundaries for methods, ensuring atomicity, consistency, isolation, and durability (ACID) of database operations.

**Key Attributes:**

| Attribute | Description | Default |
|-----------|-------------|---------|
| `propagation` | Transaction propagation behavior | REQUIRED |
| `isolation` | Isolation level | DEFAULT (DB default) |
| `readOnly` | Read-only optimization hint | false |
| `timeout` | Transaction timeout in seconds | -1 (no timeout) |
| `rollbackFor` | Exceptions that trigger rollback | RuntimeException |
| `noRollbackFor` | Exceptions that don't trigger rollback | None |

**Examples:**

```java
@Service
public class OrderService {
    
    // Basic usage
    @Transactional
    public Order createOrder(OrderDTO dto) {
        Order order = new Order();
        // All operations within single transaction
        order = orderRepository.save(order);
        inventoryService.decreaseStock(dto.getItems());
        paymentService.processPayment(dto.getPaymentInfo());
        return order;
    }
    
    // Read-only transaction (optimizations applied)
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    // Custom rollback rules
    @Transactional(rollbackFor = Exception.class, 
                   noRollbackFor = EmailException.class)
    public Order processOrder(Long orderId) {
        Order order = updateOrderStatus(orderId);
        try {
            emailService.sendConfirmation(order); // May throw EmailException
        } catch (EmailException e) {
            log.warn("Email failed, but order processed");
        }
        return order;
    }
    
    // Timeout
    @Transactional(timeout = 30)
    public void longRunningOperation() {
        // Will rollback if execution exceeds 30 seconds
    }
}
```

**Propagation Types:**
```java
// REQUIRED (default) - Join existing or create new
@Transactional(propagation = Propagation.REQUIRED)

// REQUIRES_NEW - Always create new transaction
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void auditLog(String message) {
    // Executes in separate transaction
    // Commits even if calling method rolls back
}

// MANDATORY - Must run within existing transaction
@Transactional(propagation = Propagation.MANDATORY)

// NESTED - Nested transaction with savepoints
@Transactional(propagation = Propagation.NESTED)

// SUPPORTS - Use transaction if exists, otherwise non-transactional
@Transactional(propagation = Propagation.SUPPORTS)

// NOT_SUPPORTED - Suspend current transaction
@Transactional(propagation = Propagation.NOT_SUPPORTED)

// NEVER - Exception if transaction exists
@Transactional(propagation = Propagation.NEVER)
```

**Common Pitfall - Self-Invocation:**
```java
@Service
public class OrderService {
    
    public void processOrders(List<Long> ids) {
        for (Long id : ids) {
            processOrder(id); // @Transactional IGNORED! Same class call
        }
    }
    
    @Transactional
    public void processOrder(Long id) {
        // This won't be in a transaction when called from processOrders
    }
    
    // Solution: Inject self or use separate service
    @Autowired
    private OrderService self;
    
    public void processOrdersFixed(List<Long> ids) {
        for (Long id : ids) {
            self.processOrder(id); // Works - goes through proxy
        }
    }
}
```

---

## Q22. What are the different ID generation strategies in JPA? (Beginner)

**Answer:**

JPA provides four strategies for generating primary key values:

| Strategy | Description | Database Dependency |
|----------|-------------|---------------------|
| `AUTO` | JPA chooses based on DB | Varies |
| `IDENTITY` | DB auto-increment | DB manages |
| `SEQUENCE` | DB sequence | Requires sequence support |
| `TABLE` | Separate table for IDs | DB independent |

**GenerationType.IDENTITY:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
// MySQL: AUTO_INCREMENT
// SQL Server: IDENTITY
// PostgreSQL: SERIAL

// Limitation: Batch inserts disabled (needs ID after each insert)
```

**GenerationType.SEQUENCE:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    @SequenceGenerator(
        name = "book_seq",
        sequenceName = "book_sequence",
        initialValue = 1,
        allocationSize = 50 // Pre-allocate 50 IDs at a time
    )
    private Long id;
}
// Best for Oracle, PostgreSQL
// Allows batch inserts (pre-fetches IDs)
```

**GenerationType.TABLE:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "book_gen")
    @TableGenerator(
        name = "book_gen",
        table = "id_generator",
        pkColumnName = "gen_name",
        valueColumnName = "gen_value",
        pkColumnValue = "book_id",
        allocationSize = 50
    )
    private Long id;
}
// Creates: id_generator table with gen_name, gen_value columns
// Database agnostic but has performance overhead
```

**GenerationType.AUTO:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
// JPA provider decides based on database dialect
// Hibernate 5+: Uses SEQUENCE by default (creates hibernate_sequence)
```

**UUID Generation:**
```java
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Or manually in @PrePersist
    @Id
    private String id;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
```

---

## Q23. What is the difference between JPQL and Native Query? (Intermediate)

**Answer:**

| Aspect | JPQL | Native Query |
|--------|------|--------------|
| **Syntax** | Entity and field names | Table and column names |
| **Portability** | Database agnostic | Database specific |
| **Features** | JPA features only | Full SQL features |
| **Result Mapping** | Automatic | Manual or @SqlResultSetMapping |
| **Query Cache** | Fully supported | Limited support |

**JPQL Examples:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Uses entity name (Book) and field names (title, author)
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword%")
    List<Book> searchByTitle(@Param("keyword") String keyword);
    
    // JOIN uses entity relationships
    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.name = :name")
    List<Book> findByAuthorName(@Param("name") String name);
    
    // Aggregate functions
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category = :category")
    Long countByCategory(@Param("category") String category);
    
    // Constructor expression (DTO projection)
    @Query("SELECT new com.example.BookDTO(b.id, b.title) FROM Book b")
    List<BookDTO> findAllDTOs();
}
```

**Native Query Examples:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Uses table name (books) and column names (title)
    @Query(value = "SELECT * FROM books WHERE title LIKE %:keyword%", 
           nativeQuery = true)
    List<Book> searchByTitleNative(@Param("keyword") String keyword);
    
    // Database-specific functions
    @Query(value = "SELECT * FROM books WHERE MATCH(title, description) " +
                   "AGAINST(:searchTerm IN BOOLEAN MODE)", 
           nativeQuery = true)
    List<Book> fullTextSearch(@Param("searchTerm") String searchTerm);
    
    // Complex analytics
    @Query(value = "SELECT category, COUNT(*) as count, AVG(price) as avg_price " +
                   "FROM books GROUP BY category HAVING COUNT(*) > 5",
           nativeQuery = true)
    List<Object[]> getCategoryStats();
    
    // With pagination (need countQuery)
    @Query(value = "SELECT * FROM books ORDER BY created_at DESC",
           countQuery = "SELECT COUNT(*) FROM books",
           nativeQuery = true)
    Page<Book> findAllNativePaged(Pageable pageable);
}
```

**Result Mapping for Native Queries:**
```java
// Using @SqlResultSetMapping
@Entity
@SqlResultSetMapping(
    name = "BookStatsMapping",
    classes = @ConstructorResult(
        targetClass = BookStats.class,
        columns = {
            @ColumnResult(name = "category", type = String.class),
            @ColumnResult(name = "count", type = Long.class),
            @ColumnResult(name = "avg_price", type = Double.class)
        }
    )
)
public class Book { ... }

// In repository
@Query(value = "SELECT category, COUNT(*) as count, AVG(price) as avg_price " +
               "FROM books GROUP BY category",
       nativeQuery = true)
@SqlResultSetMapping("BookStatsMapping")
List<BookStats> getCategoryStatsMapped();

// Or using interface projection
public interface CategoryStats {
    String getCategory();
    Long getCount();
    Double getAvgPrice();
}

@Query(value = "SELECT category, COUNT(*) as count, AVG(price) as avgPrice " +
               "FROM books GROUP BY category", nativeQuery = true)
List<CategoryStats> getCategoryStatsProjection();
```

---

## Q24. How do you handle soft deletes in Spring Data JPA? (Intermediate)

**Answer:**

Soft delete marks records as deleted without physically removing them from the database. This preserves data for audit trails and allows recovery.

**Implementation:**

**1. Add Deleted Flag to Entity:**
```java
@Entity
@Where(clause = "deleted = false") // Hibernate filter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(name = "deleted")
    private boolean deleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    private String deletedBy;
}
```

**2. Custom Repository Implementation:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Override delete methods
    @Modifying
    @Query("UPDATE Book b SET b.deleted = true, b.deletedAt = :now, " +
           "b.deletedBy = :user WHERE b.id = :id")
    void softDelete(@Param("id") Long id, 
                    @Param("now") LocalDateTime now,
                    @Param("user") String user);
    
    // Find including deleted
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdIncludingDeleted(@Param("id") Long id);
    
    // Find only deleted
    @Query("SELECT b FROM Book b WHERE b.deleted = true")
    List<Book> findAllDeleted();
    
    // Restore
    @Modifying
    @Query("UPDATE Book b SET b.deleted = false, b.deletedAt = null, " +
           "b.deletedBy = null WHERE b.id = :id")
    void restore(@Param("id") Long id);
}
```

**3. Using @SQLDelete (Hibernate):**
```java
@Entity
@SQLDelete(sql = "UPDATE books SET deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted = false")
public class Book {
    @Id
    private Long id;
    private String title;
    private boolean deleted = false;
    private LocalDateTime deletedAt;
}

// Now standard delete methods perform soft delete
bookRepository.deleteById(1L); // Executes UPDATE, not DELETE
```

**4. Global Filter (Hibernate):**
```java
@Entity
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
public class Book { ... }

// Enable filter in service
@Service
public class BookService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Book> findAllIncludingDeleted() {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("deletedFilter");
        return bookRepository.findAll();
    }
    
    public List<Book> findOnlyDeleted() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("deletedFilter").setParameter("isDeleted", true);
        return bookRepository.findAll();
    }
}
```

---

## Q25. What is the @EntityGraph annotation and when should you use it? (Intermediate)

**Answer:**

`@EntityGraph` provides a way to define fetch plans that override lazy/eager fetch settings, helping to solve N+1 problems and optimize queries.

**Types of EntityGraphs:**

| Type | Description |
|------|-------------|
| `FETCH` | Attributes in graph are EAGER, others are LAZY |
| `LOAD` | Attributes in graph are EAGER, others use default |

**Inline EntityGraph:**
```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Fetch order with customer and items in single query
    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<Order> findById(Long id);
    
    // Can be combined with query methods
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByStatus(OrderStatus status);
    
    // With nested attributes
    @EntityGraph(attributePaths = {"customer", "customer.addresses", "items.product.category"})
    List<Order> findByCustomerId(Long customerId);
}
```

**Named EntityGraph:**
```java
@Entity
@NamedEntityGraph(
    name = "Order.withCustomerAndItems",
    attributeNodes = {
        @NamedAttributeNode("customer"),
        @NamedAttributeNode(value = "items", subgraph = "items-subgraph")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "items-subgraph",
            attributeNodes = @NamedAttributeNode("product")
        )
    }
)
public class Order {
    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
    
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items;
}

// Use in repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @EntityGraph(value = "Order.withCustomerAndItems", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByStatus(OrderStatus status);
}
```

**Dynamic EntityGraph:**
```java
@Service
public class OrderService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Order> findOrdersWithGraph(String... attributePaths) {
        EntityGraph<Order> graph = entityManager.createEntityGraph(Order.class);
        for (String path : attributePaths) {
            graph.addAttributeNodes(path);
        }
        
        return entityManager.createQuery("SELECT o FROM Order o", Order.class)
            .setHint("javax.persistence.fetchgraph", graph)
            .getResultList();
    }
}
```

**EntityGraph vs JOIN FETCH:**
```java
// EntityGraph - cleaner, reusable
@EntityGraph(attributePaths = {"items"})
List<Order> findAll();

// JOIN FETCH - more flexible for complex queries
@Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.status = :status")
List<Order> findByStatusWithItems(@Param("status") OrderStatus status);
```

---

## Q26. How do you implement inheritance mapping in JPA? (Advanced)

**Answer:**

JPA supports three inheritance strategies for mapping entity hierarchies to database tables:

**1. SINGLE_TABLE (Default):**
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}

@Entity
@DiscriminatorValue("CREDIT_CARD")
public class CreditCardPayment extends Payment {
    private String cardNumber;
    private String expiryDate;
}

@Entity
@DiscriminatorValue("BANK_TRANSFER")
public class BankTransferPayment extends Payment {
    private String bankName;
    private String accountNumber;
}
// Result: Single 'payment' table with all columns + payment_type discriminator
```

| Pros | Cons |
|------|------|
| Simple queries | Nullable columns for subclass fields |
| No JOINs needed | Table can get wide |
| Best performance | No NOT NULL constraints on subclass fields |

**2. JOINED (Table per Subclass):**
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
}

@Entity
@PrimaryKeyJoinColumn(name = "payment_id")
public class CreditCardPayment extends Payment {
    private String cardNumber;
}

@Entity
@PrimaryKeyJoinColumn(name = "payment_id")
public class BankTransferPayment extends Payment {
    private String bankName;
}
// Result: Separate tables, JOINed by payment_id
```

| Pros | Cons |
|------|------|
| Normalized schema | JOINs required |
| NOT NULL possible | Complex queries |
| Clean table structure | Slower for polymorphic queries |

**3. TABLE_PER_CLASS:**
```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Cannot use IDENTITY
    private Long id;
    private BigDecimal amount;
}

@Entity
public class CreditCardPayment extends Payment {
    private String cardNumber;
}

@Entity
public class BankTransferPayment extends Payment {
    private String bankName;
}
// Result: Completely separate tables, no relation
```

| Pros | Cons |
|------|------|
| Each class has own table | UNION for polymorphic queries |
| No unused columns | ID generation complexity |
| Simple subclass queries | Worst for polymorphic access |

**Polymorphic Queries:**
```java
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Returns all types (CreditCardPayment, BankTransferPayment)
    List<Payment> findByAmountGreaterThan(BigDecimal amount);
}

// Type-specific repository
public interface CreditCardPaymentRepository extends JpaRepository<CreditCardPayment, Long> {
    List<CreditCardPayment> findByCardNumberEndingWith(String lastFour);
}
```

---

## Q27. What is @MappedSuperclass and when should you use it? (Intermediate)

**Answer:**

`@MappedSuperclass` defines a base class whose fields are inherited by subclasses but the superclass itself is not an entity and has no corresponding table.

**Difference from @Inheritance:**

| Aspect | @MappedSuperclass | @Inheritance |
|--------|-------------------|--------------|
| Parent table | None | Created (except TABLE_PER_CLASS) |
| Parent queries | Not possible | Possible |
| Parent repository | Not possible | Possible |
| Use case | Shared fields | True IS-A relationship |

**Example - Auditable Base Class:**
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    // Common fields for all entities
    // Getters and setters
}

@Entity
@Table(name = "books")
public class Book extends BaseEntity {
    private String title;
    private String author;
    // id, createdAt, updatedAt, version inherited
}

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String email;
    // id, createdAt, updatedAt, version inherited
}
// Result: 'books' and 'users' tables each have id, created_at, updated_at, version columns
// No 'base_entity' table exists
```

**Cannot Query MappedSuperclass:**
```java
// This is NOT possible
public interface BaseEntityRepository extends JpaRepository<BaseEntity, Long> { }

// You must query specific entities
public interface BookRepository extends JpaRepository<Book, Long> { }
public interface UserRepository extends JpaRepository<User, Long> { }
```

**Combining with @Embeddable:**
```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private Audit audit = new Audit();
}

@Embeddable
public class Audit {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

---

## Q28. How do you handle database schema generation in Spring Data JPA? (Beginner)

**Answer:**

Spring Data JPA provides options to automatically generate or update database schema based on entity definitions.

**Configuration Properties:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Schema generation mode
    show-sql: true      # Log SQL statements
    properties:
      hibernate:
        format_sql: true  # Pretty print SQL
        dialect: org.hibernate.dialect.MySQL8Dialect
```

**DDL-Auto Options:**

| Value | Behavior | Use Case |
|-------|----------|----------|
| `none` | No schema management | Production |
| `validate` | Validate schema matches entities | Production |
| `update` | Update schema (add columns/tables) | Development |
| `create` | Drop and create on startup | Testing |
| `create-drop` | Create on startup, drop on shutdown | Testing |

**Best Practices:**

**Development:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

**Production:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

**Using Flyway for Migrations:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none  # Flyway handles schema
  flyway:
    enabled: true
    locations: classpath:db/migration
```

```sql
-- V1__create_books_table.sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V2__add_price_column.sql
ALTER TABLE books ADD COLUMN price DECIMAL(10,2);
```

**Schema Export to File:**
```yaml
spring:
  jpa:
    properties:
      javax:
        persistence:
          schema-generation:
            scripts:
              action: create
              create-target: target/schema.sql
```

---

## Q29. What is the difference between persist() and merge() in JPA? (Intermediate)

**Answer:**

| Aspect | persist() | merge() |
|--------|-----------|---------|
| **Entity State** | New entity | Detached entity |
| **ID Required** | No | Usually yes |
| **Return Value** | void (entity managed) | Managed copy returned |
| **Persistence Context** | Adds entity | Copies state to managed entity |

**persist():**
```java
@Transactional
public void createBook() {
    Book book = new Book();
    book.setTitle("Spring in Action");
    // book is TRANSIENT (new, not managed)
    
    entityManager.persist(book);
    // book is now PERSISTENT (managed)
    // Any changes to 'book' will be tracked
    
    book.setAuthor("Craig Walls");
    // This change will be saved at commit (dirty checking)
}
```

**merge():**
```java
@Transactional
public Book updateBook(Book detachedBook) {
    // detachedBook came from outside transaction (e.g., from client)
    // It's DETACHED - has ID but not in persistence context
    
    Book managedBook = entityManager.merge(detachedBook);
    // Creates a managed COPY of detachedBook
    // detachedBook remains detached
    
    managedBook.setCategory("Technology");
    // Changes to managedBook are tracked
    
    detachedBook.setCategory("Programming");
    // Changes to detachedBook are NOT tracked!
    
    return managedBook; // Return the managed instance
}
```

**Spring Data JPA save() Behavior:**
```java
// JpaRepository.save() implementation
@Transactional
public <S extends T> S save(S entity) {
    if (entityInformation.isNew(entity)) {
        entityManager.persist(entity); // New entity
        return entity;
    } else {
        return entityManager.merge(entity); // Existing entity
    }
}
```

**isNew() Detection:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // isNew() returns true if id is null
}

// Custom isNew with Persistable
@Entity
public class Book implements Persistable<Long> {
    @Id
    private Long id;
    
    @Transient
    private boolean isNew = true;
    
    @Override
    public boolean isNew() {
        return isNew;
    }
    
    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
```

---

## Q30. How do you implement a composite primary key in JPA? (Intermediate)

**Answer:**

JPA provides two approaches for composite primary keys: `@IdClass` and `@EmbeddedId`.

**1. Using @IdClass:**
```java
// Step 1: Create ID class
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long productId;
    
    // Default constructor
    public OrderItemId() {}
    
    public OrderItemId(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }
    
    // equals() and hashCode() required!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(orderId, that.orderId) && 
               Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}

// Step 2: Use in Entity
@Entity
@IdClass(OrderItemId.class)
public class OrderItem {
    @Id
    private Long orderId;
    
    @Id
    private Long productId;
    
    private int quantity;
    private BigDecimal price;
}

// Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByOrderId(Long orderId);
}

// Usage
OrderItemId id = new OrderItemId(1L, 100L);
Optional<OrderItem> item = orderItemRepository.findById(id);
```

**2. Using @EmbeddedId:**
```java
// Step 1: Create Embeddable ID
@Embeddable
public class OrderItemId implements Serializable {
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "product_id")
    private Long productId;
    
    // Constructors, equals(), hashCode()
}

// Step 2: Use in Entity
@Entity
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;
    
    private int quantity;
    private BigDecimal price;
    
    // Access ID components
    public Long getOrderId() {
        return id.getOrderId();
    }
}

// Query by partial key
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.id.orderId = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}
```

**With Relationships:**
```java
@Entity
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;
    
    @ManyToOne
    @MapsId("orderId") // Maps to id.orderId
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne
    @MapsId("productId") // Maps to id.productId
    @JoinColumn(name = "product_id")
    private Product product;
    
    private int quantity;
}
```

---

## Q31. What is the difference between JpaRepository and CrudRepository? (Beginner)

**Answer:**

| Feature | CrudRepository | JpaRepository |
|---------|----------------|---------------|
| **Hierarchy** | Extends Repository | Extends PagingAndSortingRepository |
| **Return Type** | Iterable | List |
| **Batch Operations** | No | Yes (saveAll, deleteInBatch) |
| **Pagination** | No | Yes |
| **Flushing** | No | Yes (flush, saveAndFlush) |
| **Reference Loading** | No | Yes (getOne/getReferenceById) |

**CrudRepository Methods:**
```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    Iterable<T> findAll();
    Iterable<T> findAllById(Iterable<ID> ids);
    long count();
    void deleteById(ID id);
    void delete(T entity);
    void deleteAllById(Iterable<? extends ID> ids);
    void deleteAll(Iterable<? extends T> entities);
    void deleteAll();
}
```

**JpaRepository Additional Methods:**
```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
    // Return List instead of Iterable
    List<T> findAll();
    List<T> findAllById(Iterable<ID> ids);
    <S extends T> List<S> saveAll(Iterable<S> entities);
    
    // Flush operations
    void flush();
    <S extends T> S saveAndFlush(S entity);
    <S extends T> List<S> saveAllAndFlush(Iterable<S> entities);
    
    // Batch delete (single DELETE statement)
    void deleteAllInBatch(Iterable<T> entities);
    void deleteAllByIdInBatch(Iterable<ID> ids);
    void deleteAllInBatch();
    
    // Reference/Proxy loading
    T getReferenceById(ID id);  // Formerly getOne()
    
    // Example queries with Example object
    <S extends T> List<S> findAll(Example<S> example);
    <S extends T> List<S> findAll(Example<S> example, Sort sort);
}
```

**When to Use Which:**
```java
// Use CrudRepository for simple CRUD without JPA-specific features
public interface SimpleBookRepository extends CrudRepository<Book, Long> {
}

// Use JpaRepository for full JPA capabilities (recommended)
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

---

## Q32. What is Query by Example (QBE) in Spring Data JPA? (Intermediate)

**Answer:**

Query by Example allows you to build dynamic queries based on a probe entity, where non-null properties become query conditions.

**Basic Usage:**
```java
@Entity
public class User {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private boolean active;
}

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> searchUsers(UserSearchRequest request) {
        // Create probe entity
        User probe = new User();
        probe.setFirstName(request.getFirstName());
        probe.setLastName(request.getLastName());
        probe.setEmail(request.getEmail());
        // null values are ignored by default
        
        Example<User> example = Example.of(probe);
        return userRepository.findAll(example);
    }
}
```

**With ExampleMatcher:**
```java
public List<User> advancedSearch(UserSearchRequest request) {
    User probe = new User();
    probe.setFirstName(request.getFirstName());
    probe.setEmail(request.getEmail());
    
    ExampleMatcher matcher = ExampleMatcher.matching()
        // String matching options
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        // Case-insensitive
        .withIgnoreCase()
        // Ignore specific properties
        .withIgnorePaths("id", "active")
        // Null handling
        .withIncludeNullValues() // Include IS NULL checks
        // Property-specific matchers
        .withMatcher("email", match -> match.endsWith())
        .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.startsWith());
    
    Example<User> example = Example.of(probe, matcher);
    return userRepository.findAll(example);
}
```

**StringMatcher Options:**

| Matcher | SQL Equivalent |
|---------|----------------|
| DEFAULT | = value |
| EXACT | = value |
| STARTING | LIKE value% |
| ENDING | LIKE %value |
| CONTAINING | LIKE %value% |
| REGEX | (if supported) |

**Limitations:**
- No OR conditions (only AND)
- No nested property constraints
- No range queries (>, <, between)
- No grouping or aggregation

**Combined with Pagination:**
```java
public Page<User> searchUsersPaged(UserSearchRequest request, Pageable pageable) {
    User probe = new User();
    probe.setLastName(request.getLastName());
    
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withStringMatcher(ExampleMatcher.StringMatcher.STARTING)
        .withIgnoreCase();
    
    Example<User> example = Example.of(probe, matcher);
    return userRepository.findAll(example, pageable);
}
```

---

## Q33. How do you implement custom repository methods? (Intermediate)

**Answer:**

Spring Data JPA allows extending repositories with custom implementation for complex queries or operations.

**Step 1: Define Custom Interface:**
```java
public interface CustomBookRepository {
    List<Book> findBooksWithComplexCriteria(BookSearchCriteria criteria);
    void updateBookPrices(BigDecimal percentage, String category);
    List<BookStatistics> getBookStatistics();
}
```

**Step 2: Implement Custom Interface:**
```java
@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Book> findBooksWithComplexCriteria(BookSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (criteria.getTitle() != null) {
            predicates.add(cb.like(cb.lower(book.get("title")), 
                "%" + criteria.getTitle().toLowerCase() + "%"));
        }
        
        if (criteria.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("price"), 
                criteria.getMinPrice()));
        }
        
        if (criteria.getCategories() != null && !criteria.getCategories().isEmpty()) {
            predicates.add(book.get("category").in(criteria.getCategories()));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(book.get("publishDate")));
        
        return entityManager.createQuery(query)
            .setMaxResults(criteria.getLimit())
            .getResultList();
    }
    
    @Override
    @Transactional
    public void updateBookPrices(BigDecimal percentage, String category) {
        String jpql = "UPDATE Book b SET b.price = b.price * :factor " +
                      "WHERE b.category = :category";
        
        entityManager.createQuery(jpql)
            .setParameter("factor", BigDecimal.ONE.add(percentage.divide(new BigDecimal(100))))
            .setParameter("category", category)
            .executeUpdate();
    }
    
    @Override
    public List<BookStatistics> getBookStatistics() {
        String sql = "SELECT category, COUNT(*), AVG(price), SUM(stock) " +
                     "FROM books GROUP BY category";
        
        return entityManager.createNativeQuery(sql)
            .unwrap(org.hibernate.query.Query.class)
            .setResultTransformer(new BookStatisticsResultTransformer())
            .getResultList();
    }
}
```

**Step 3: Extend Main Repository:**
```java
public interface BookRepository extends JpaRepository<Book, Long>, 
                                        CustomBookRepository {
    // Standard Spring Data methods
    List<Book> findByAuthor(String author);
    
    // Custom methods from CustomBookRepository are automatically available
}
```

**Usage:**
```java
@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> search(BookSearchCriteria criteria) {
        // Uses custom implementation
        return bookRepository.findBooksWithComplexCriteria(criteria);
    }
    
    public List<Book> findByAuthor(String author) {
        // Uses Spring Data derived query
        return bookRepository.findByAuthor(author);
    }
}
```

**Important Note on Naming:**
The implementation class must be named `{RepositoryInterface}Impl` or `Custom{RepositoryInterface}Impl`. You can customize the suffix:
```java
@EnableJpaRepositories(
    basePackages = "com.example.repository",
    repositoryImplementationPostfix = "CustomImpl"
)
```

---

## Q34. What is the first-level cache and second-level cache in JPA? (Advanced)

**Answer:**

| Aspect | First-Level Cache | Second-Level Cache |
|--------|-------------------|-------------------|
| **Scope** | Transaction/Session | SessionFactory/Application |
| **Enabled By** | Default | Explicit configuration |
| **Shared** | No (per EntityManager) | Yes (across sessions) |
| **Eviction** | End of transaction | Based on configuration |
| **Provider** | JPA specification | Hibernate + cache provider |

**First-Level Cache (Persistence Context):**
```java
@Transactional
public void demonstrateFirstLevelCache() {
    // First query - hits database
    Book book1 = bookRepository.findById(1L).get();
    
    // Second query - returns cached instance (no SQL)
    Book book2 = bookRepository.findById(1L).get();
    
    // Same object reference
    System.out.println(book1 == book2); // true
    
    // Changes are tracked
    book1.setTitle("New Title");
    // At transaction commit, changes are flushed
}
```

**Second-Level Cache Configuration:**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          use_query_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
        javax:
          cache:
            provider: org.ehcache.jsr107.EhcacheCachingProvider
```

**Add Dependencies:**
```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

**Entity Cache Configuration:**
```java
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Book {
    @Id
    private Long id;
    private String title;
    
    @OneToMany(mappedBy = "book")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Review> reviews;
}
```

**Cache Concurrency Strategies:**

| Strategy | Description | Use Case |
|----------|-------------|----------|
| `READ_ONLY` | Never modified | Static data |
| `READ_WRITE` | Read-mostly, updated | General use |
| `NONSTRICT_READ_WRITE` | Rarely updated | Few updates |
| `TRANSACTIONAL` | JTA transactions | Full ACID |

**Query Cache:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Book> findByCategory(String category);
}

// Or with EntityManager
List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.category = :cat")
    .setParameter("cat", "Fiction")
    .setHint("org.hibernate.cacheable", true)
    .getResultList();
```

**Cache Eviction:**
```java
@Service
public class CacheService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void evictSecondLevelCache() {
        Session session = entityManager.unwrap(Session.class);
        SessionFactory sessionFactory = session.getSessionFactory();
        
        // Evict specific entity
        sessionFactory.getCache().evict(Book.class, bookId);
        
        // Evict all entities of type
        sessionFactory.getCache().evict(Book.class);
        
        // Evict all second-level cache
        sessionFactory.getCache().evictAllRegions();
    }
}
```

---

## Q35. How do you handle transactions across multiple databases? (Advanced)

**Answer:**

Spring supports distributed transactions with JTA (Java Transaction API) or chain transactions with multiple datasources.

**1. Multiple DataSource Configuration:**
```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}
```

**2. Multiple EntityManagers:**
```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.repository.primary",
    entityManagerFactoryRef = "primaryEntityManager",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryJpaConfig {
    
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManager(
            @Qualifier("primaryDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.entity.primary");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }
    
    @Bean
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.repository.secondary",
    entityManagerFactoryRef = "secondaryEntityManager",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryJpaConfig {
    // Similar configuration for secondary datasource
}
```

**3. Using ChainedTransactionManager:**
```java
@Configuration
public class ChainedTransactionConfig {
    
    @Bean
    public PlatformTransactionManager chainedTransactionManager(
            @Qualifier("primaryTransactionManager") PlatformTransactionManager primary,
            @Qualifier("secondaryTransactionManager") PlatformTransactionManager secondary) {
        return new ChainedTransactionManager(primary, secondary);
    }
}

@Service
public class MultiDbService {
    
    @Transactional("chainedTransactionManager")
    public void updateBothDatabases() {
        primaryRepository.save(primaryEntity);
        secondaryRepository.save(secondaryEntity);
        // Both will commit or both will rollback
    }
}
```

**4. JTA for True Distributed Transactions:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```

```java
@Configuration
public class JtaConfig {
    
    @Bean
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setForceShutdown(false);
        return manager;
    }
    
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
            UserTransactionManager atomikosTransactionManager) {
        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setTransactionManager(atomikosTransactionManager);
        manager.setUserTransaction(atomikosTransactionManager);
        return manager;
    }
}

@Service
public class DistributedService {
    
    @Transactional // Uses JTA transaction
    public void distributedOperation() {
        primaryRepository.save(entity1);
        secondaryRepository.save(entity2);
        // Two-phase commit ensures atomicity
    }
}
```

## Q36. How do you call stored procedures in Spring Data JPA? (Intermediate)

**Answer:**

Spring Data JPA provides multiple ways to call stored procedures from your application.

**1. Using @Procedure Annotation:**
```java
// Entity definition
@Entity
@NamedStoredProcedureQuery(
    name = "Book.updatePriceByCategory",
    procedureName = "update_book_prices",
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "category", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "percentage", type = Double.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "updated_count", type = Integer.class)
    }
)
public class Book { ... }

// Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Procedure(name = "Book.updatePriceByCategory")
    Integer updatePriceByCategory(@Param("category") String category, 
                                   @Param("percentage") Double percentage);
    
    // Or directly reference procedure name
    @Procedure(procedureName = "update_book_prices")
    Integer callUpdatePrices(String category, Double percentage);
}
```

**2. Using EntityManager:**
```java
@Service
public class BookService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Integer updatePrices(String category, double percentage) {
        StoredProcedureQuery query = entityManager
            .createStoredProcedureQuery("update_book_prices")
            .registerStoredProcedureParameter("category", String.class, ParameterMode.IN)
            .registerStoredProcedureParameter("percentage", Double.class, ParameterMode.IN)
            .registerStoredProcedureParameter("updated_count", Integer.class, ParameterMode.OUT)
            .setParameter("category", category)
            .setParameter("percentage", percentage);
        
        query.execute();
        
        return (Integer) query.getOutputParameterValue("updated_count");
    }
    
    // Procedure returning result set
    @SuppressWarnings("unchecked")
    public List<Book> getBooksByCategory(String category) {
        StoredProcedureQuery query = entityManager
            .createStoredProcedureQuery("get_books_by_category", Book.class)
            .registerStoredProcedureParameter("category", String.class, ParameterMode.IN)
            .setParameter("category", category);
        
        return query.getResultList();
    }
}
```

**3. Using Native Query:**
```java
@Query(value = "CALL calculate_statistics(:startDate, :endDate)", nativeQuery = true)
List<Object[]> calculateStatistics(@Param("startDate") LocalDate start, 
                                   @Param("endDate") LocalDate end);
```

---

## Q37. What is pessimistic locking and how is it different from optimistic locking? (Advanced)

**Answer:**

| Aspect | Optimistic Locking | Pessimistic Locking |
|--------|-------------------|---------------------|
| **Approach** | Assumes no conflicts | Assumes conflicts likely |
| **Lock Time** | At commit time | At read time |
| **Implementation** | @Version field | Database row locks |
| **Conflicts** | Detected late, retry needed | Prevented upfront |
| **Performance** | Better for low contention | Better for high contention |
| **Deadlock Risk** | None | Possible |

**Pessimistic Locking Types:**

| Lock Type | Description |
|-----------|-------------|
| `PESSIMISTIC_READ` | Shared lock, blocks writes |
| `PESSIMISTIC_WRITE` | Exclusive lock, blocks reads & writes |
| `PESSIMISTIC_FORCE_INCREMENT` | Like WRITE + version increment |

**Using @Lock Annotation:**
```java
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);
    
    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Account> findByCustomerId(Long customerId);
}
```

**Using EntityManager:**
```java
@Service
public class AccountService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        // Lock both accounts
        Account from = entityManager.find(Account.class, fromId, 
            LockModeType.PESSIMISTIC_WRITE);
        Account to = entityManager.find(Account.class, toId, 
            LockModeType.PESSIMISTIC_WRITE);
        
        // Perform transfer
        from.withdraw(amount);
        to.deposit(amount);
    }
    
    // With timeout
    @Transactional
    public Account getAccountWithTimeout(Long id) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.lock.timeout", 5000); // 5 seconds
        
        return entityManager.find(Account.class, id, 
            LockModeType.PESSIMISTIC_WRITE, properties);
    }
}
```

**Lock Timeout Configuration:**
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
Optional<Account> findByIdWithTimeout(Long id);
```

---

## Q38. How do you implement batch processing in Spring Data JPA? (Advanced)

**Answer:**

Batch processing improves performance by reducing database round-trips when handling large datasets.

**Configuration:**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
          batch_versioned_data: true
        order_inserts: true
        order_updates: true
```

**Batch Insert:**
```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public void batchInsert(List<Product> products) {
        int batchSize = 50;
        
        for (int i = 0; i < products.size(); i++) {
            entityManager.persist(products.get(i));
            
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear(); // Free memory
            }
        }
        
        entityManager.flush();
        entityManager.clear();
    }
    
    // Using saveAll (batches automatically if configured)
    @Transactional
    public void saveProducts(List<Product> products) {
        List<List<Product>> batches = partition(products, 50);
        for (List<Product> batch : batches) {
            productRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

**Batch Update:**
```java
@Transactional
public void batchUpdatePrices(Map<Long, BigDecimal> priceUpdates) {
    int batchSize = 50;
    int count = 0;
    
    for (Map.Entry<Long, BigDecimal> entry : priceUpdates.entrySet()) {
        Product product = entityManager.find(Product.class, entry.getKey());
        product.setPrice(entry.getValue());
        
        count++;
        if (count % batchSize == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}

// Bulk update (better for large datasets)
@Modifying
@Query("UPDATE Product p SET p.price = p.price * :factor WHERE p.category = :category")
int bulkUpdatePrices(@Param("factor") BigDecimal factor, @Param("category") String category);
```

**Batch Delete:**
```java
// Entity-by-entity (triggers lifecycle callbacks)
@Transactional
public void batchDelete(List<Long> ids) {
    List<List<Long>> batches = partition(ids, 50);
    for (List<Long> batch : batches) {
        productRepository.deleteAllById(batch);
    }
}

// Bulk delete (single statement, no callbacks)
@Transactional
public void bulkDelete(List<Long> ids) {
    productRepository.deleteAllByIdInBatch(ids);
}

// Custom bulk delete
@Modifying
@Query("DELETE FROM Product p WHERE p.category = :category")
int deleteByCategory(@Param("category") String category);
```

**Important Notes:**
- `GenerationType.IDENTITY` disables batch inserts
- Use `GenerationType.SEQUENCE` with allocationSize for batching
- Clear persistence context periodically to prevent memory issues

---

## Q39. What is the difference between @OneToOne and @ManyToOne? (Beginner)

**Answer:**

| Aspect | @OneToOne | @ManyToOne |
|--------|-----------|------------|
| **Cardinality** | 1:1 | N:1 |
| **Example** | User-Profile | Book-Author |
| **FK Location** | Either side | Many side |
| **Default Fetch** | EAGER | EAGER |

**@OneToOne Example:**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String bio;
    private String avatarUrl;
    
    @OneToOne(mappedBy = "profile")
    private User user;
}
```

**@ManyToOne Example:**
```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author; // Many books belong to one author
}

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "author")
    private List<Book> books = new ArrayList<>();
}
```

**Shared Primary Key (OneToOne):**
```java
@Entity
public class UserProfile {
    @Id
    private Long id; // Same as User's ID
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    
    private String bio;
}
// user_profile.id = user.id (shared primary key)
```

---

## Q40. How do you implement custom ID generators in JPA? (Advanced)

**Answer:**

Custom ID generators allow you to create IDs following specific business rules or formats.

**1. Using @GenericGenerator (Hibernate):**
```java
@Entity
public class Order {
    @Id
    @GeneratedValue(generator = "custom-order-id")
    @GenericGenerator(
        name = "custom-order-id",
        strategy = "com.example.generator.OrderIdGenerator"
    )
    private String id;
}

// Custom Generator Implementation
public class OrderIdGenerator implements IdentifierGenerator {
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        String prefix = "ORD";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return prefix + "-" + datePart + "-" + randomPart;
        // Example: ORD-20240115-A1B2C3D4
    }
}
```

**2. Using Sequence with Custom Format:**
```java
@Entity
public class Invoice {
    @Id
    @GeneratedValue(generator = "invoice-seq-gen")
    @GenericGenerator(
        name = "invoice-seq-gen",
        strategy = "com.example.generator.InvoiceIdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "invoice_seq"),
            @org.hibernate.annotations.Parameter(name = "prefix", value = "INV")
        }
    )
    private String id;
}

public class InvoiceIdGenerator implements IdentifierGenerator, Configurable {
    private String sequenceName;
    private String prefix;
    
    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        sequenceName = params.getProperty("sequence_name");
        prefix = params.getProperty("prefix");
    }
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Long seqValue = ((Number) session.createNativeQuery(
            "SELECT nextval('" + sequenceName + "')")
            .getSingleResult()).longValue();
        
        return String.format("%s-%s-%06d", 
            prefix, 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")),
            seqValue);
        // Example: INV-202401-000001
    }
}
```

**3. Using @PrePersist:**
```java
@Entity
public class Document {
    @Id
    private String id;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            id = "DOC-" + System.currentTimeMillis() + "-" + 
                 ThreadLocalRandom.current().nextInt(1000, 9999);
        }
    }
}
```

---

## Q41. What is @Converter and how do you use it? (Intermediate)

**Answer:**

`@Converter` allows you to convert entity attribute values between their Java representation and database column representation.

**Basic Converter:**
```java
@Converter(autoApply = true) // Apply to all matching types
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {
    
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute != null && attribute ? "Y" : "N";
    }
    
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equals(dbData);
    }
}
```

**Enum Converter:**
```java
public enum OrderStatus {
    PENDING("P"), CONFIRMED("C"), SHIPPED("S"), DELIVERED("D"), CANCELLED("X");
    
    private final String code;
    
    OrderStatus(String code) { this.code = code; }
    public String getCode() { return code; }
    
    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
            .filter(s -> s.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }
}

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(OrderStatus status) {
        return status != null ? status.getCode() : null;
    }
    
    @Override
    public OrderStatus convertToEntityAttribute(String code) {
        return code != null ? OrderStatus.fromCode(code) : null;
    }
}
```

**JSON Converter:**
```java
@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute != null ? objectMapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }
    
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, 
                new TypeReference<Map<String, Object>>() {}) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}

// Usage in Entity
@Entity
public class Product {
    @Id
    private Long id;
    
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> attributes;
}
```

**Encrypted Field Converter:**
```java
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    
    private static final String SECRET_KEY = "mySecretKey12345";
    
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? encrypt(attribute) : null;
    }
    
    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData != null ? decrypt(dbData) : null;
    }
    
    private String encrypt(String value) { /* AES encryption */ }
    private String decrypt(String value) { /* AES decryption */ }
}
```

---

## Q42. How do you handle database connection pooling in Spring Data JPA? (Intermediate)

**Answer:**

Spring Boot uses HikariCP as the default connection pool. Configuration options include:

**HikariCP Configuration:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
    hikari:
      pool-name: MyHikariPool
      minimum-idle: 5              # Minimum idle connections
      maximum-pool-size: 20        # Maximum pool size
      idle-timeout: 300000         # 5 minutes
      max-lifetime: 1200000        # 20 minutes
      connection-timeout: 30000    # 30 seconds
      leak-detection-threshold: 60000  # 1 minute
      validation-timeout: 5000     # 5 seconds
      connection-test-query: SELECT 1
```

**Java Configuration:**
```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }
}
```

**Monitoring Connection Pool:**
```java
@Component
public class ConnectionPoolMonitor {
    
    @Autowired
    private DataSource dataSource;
    
    @Scheduled(fixedRate = 60000)
    public void logPoolStats() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) dataSource;
            HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
            
            log.info("Pool Stats - Active: {}, Idle: {}, Total: {}, Waiting: {}",
                pool.getActiveConnections(),
                pool.getIdleConnections(),
                pool.getTotalConnections(),
                pool.getThreadsAwaitingConnection());
        }
    }
}
```

**Best Practices:**
- `maximum-pool-size`: Start with `(core_count * 2) + effective_spindle_count`
- `minimum-idle`: Set equal to `maximum-pool-size` for consistent performance
- `max-lifetime`: Set lower than database's connection timeout
- Enable `leak-detection-threshold` in development

---

## Q43. What are the common performance optimization techniques in Spring Data JPA? (Advanced)

**Answer:**

**1. Use Projections Instead of Full Entities:**
```java
// Instead of loading full entity
List<Book> books = bookRepository.findAll();

// Load only needed fields
public interface BookSummary {
    String getTitle();
    String getAuthor();
}
List<BookSummary> summaries = bookRepository.findAllProjectedBy();
```

**2. Avoid N+1 with JOIN FETCH or EntityGraph:**
```java
// Bad - N+1 queries
List<Order> orders = orderRepository.findAll();
orders.forEach(o -> o.getItems().size());

// Good - Single query
@EntityGraph(attributePaths = {"items"})
List<Order> findAll();
```

**3. Use Pagination:**
```java
// Bad - Load everything
List<Book> allBooks = bookRepository.findAll();

// Good - Paginated
Page<Book> page = bookRepository.findAll(PageRequest.of(0, 20));
```

**4. Enable Query Caching:**
```java
@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
List<Book> findByCategory(String category);
```

**5. Use @BatchSize for Collections:**
```java
@OneToMany(mappedBy = "order")
@BatchSize(size = 25)
private List<OrderItem> items;
```

**6. Avoid Unnecessary Data Loading:**
```java
// Bad - Loads entire entity to check existence
if (bookRepository.findById(id).isPresent()) { ... }

// Good - Count query
if (bookRepository.existsById(id)) { ... }
```

**7. Use Bulk Operations:**
```java
// Bad - Multiple UPDATE statements
books.forEach(b -> { b.setStatus("ARCHIVED"); bookRepository.save(b); });

// Good - Single UPDATE statement
@Modifying
@Query("UPDATE Book b SET b.status = :status WHERE b.publishedYear < :year")
int archiveOldBooks(@Param("status") String status, @Param("year") int year);
```

**8. Lazy Loading Configuration:**
```java
// Set LAZY for all relationships by default
@ManyToOne(fetch = FetchType.LAZY)
private Author author;
```

**9. Index Optimization:**
```java
@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_book_isbn", columnList = "isbn"),
    @Index(name = "idx_book_author_title", columnList = "author_id, title")
})
public class Book { ... }
```

---

## Q44. How do you handle database migrations with Spring Data JPA? (Beginner)

**Answer:**

While Spring Data JPA can auto-generate schema, production applications should use database migration tools for version control and reproducibility.

**Flyway Setup:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  jpa:
    hibernate:
      ddl-auto: validate  # Validate against migrations
```

**Migration Files:**
```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V2__add_status_column.sql
ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';

-- V3__create_orders_table.sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Liquibase Alternative:**
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
```

```xml
<!-- db/changelog/db.changelog-master.xml -->
<databaseChangeLog>
    <include file="db/changelog/changes/001-create-users.xml"/>
    <include file="db/changelog/changes/002-add-status.xml"/>
</databaseChangeLog>
```

**Best Practices:**
- Never use `ddl-auto: create` or `update` in production
- Use `ddl-auto: validate` to ensure entity-schema consistency
- Version all schema changes
- Test migrations before production deployment

---

## Q45. What is @DynamicUpdate and when should you use it? (Intermediate)

**Answer:**

`@DynamicUpdate` generates UPDATE statements that include only modified columns instead of all columns.

**Without @DynamicUpdate:**
```java
@Entity
public class User {
    @Id
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
}

// When updating just email:
user.setEmail("new@email.com");
// Generated SQL:
// UPDATE users SET username=?, email=?, phone=?, address=? WHERE id=?
// All columns included even if unchanged
```

**With @DynamicUpdate:**
```java
@Entity
@DynamicUpdate
public class User {
    @Id
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
}

// When updating just email:
user.setEmail("new@email.com");
// Generated SQL:
// UPDATE users SET email=? WHERE id=?
// Only modified columns included
```

**When to Use:**
- Entities with many columns where few are updated
- Reduce data transferred between application and database
- Avoid overwriting concurrent modifications on unrelated columns
- Wide tables with BLOB/CLOB columns

**When to Avoid:**
- Entities with few columns
- Most updates touch all columns
- Performance-critical batch updates (prepared statement caching less effective)

**@DynamicInsert:**
```java
@Entity
@DynamicInsert
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(insertable = false)
    private LocalDateTime createdAt; // Uses database DEFAULT
}
// INSERT only includes non-null columns
```

---

## Q46. How do you test Spring Data JPA repositories? (Intermediate)

**Answer:**

Spring provides `@DataJpaTest` for repository testing with an embedded database.

**Basic Repository Test:**
```java
@DataJpaTest
class BookRepositoryTest {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void findByAuthor_ShouldReturnBooks() {
        // Given
        Book book = new Book("Spring in Action", "Craig Walls");
        entityManager.persistAndFlush(book);
        
        // When
        List<Book> found = bookRepository.findByAuthor("Craig Walls");
        
        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Spring in Action");
    }
    
    @Test
    void customQuery_ShouldWork() {
        // Given
        entityManager.persist(new Book("Book A", "Author 1"));
        entityManager.persist(new Book("Book B", "Author 1"));
        entityManager.persist(new Book("Book C", "Author 2"));
        entityManager.flush();
        
        // When
        Long count = bookRepository.countByAuthor("Author 1");
        
        // Then
        assertThat(count).isEqualTo(2);
    }
}
```

**Testing with Real Database:**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:tc:mysql:8.0:///testdb"
})
class BookRepositoryIntegrationTest {
    // Uses Testcontainers for MySQL
}
```

**Testing Custom Repository Implementation:**
```java
@DataJpaTest
@Import(CustomBookRepositoryImpl.class)
class CustomBookRepositoryTest {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    void customMethod_ShouldWork() {
        // Test custom repository method
    }
}
```

**Transactional Behavior:**
```java
@DataJpaTest
class TransactionalTest {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    @Rollback(false) // Prevent rollback if needed
    void testWithCommit() {
        // Changes will be committed
    }
}
```

---

## Q47. What is the difference between @Query and derived query methods? (Beginner)

**Answer:**

| Aspect | Derived Query | @Query |
|--------|---------------|--------|
| **Definition** | Method name | Annotation |
| **Complexity** | Simple queries | Any complexity |
| **Readability** | Self-documenting | Requires annotation |
| **Flexibility** | Limited | Full JPQL/SQL |
| **Refactoring** | Risky (name-based) | Safe |

**Derived Query Methods:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Simple queries - method name is the query
    List<User> findByEmail(String email);
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
    List<User> findByAgeGreaterThanOrderByLastNameAsc(int age);
    Optional<User> findFirstByOrderByCreatedAtDesc();
    boolean existsByEmail(String email);
    long countByStatus(String status);
}
```

**@Query - When to Use:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Complex joins
    @Query("SELECT u FROM User u JOIN u.orders o WHERE o.total > :minTotal")
    List<User> findUsersWithOrdersAbove(@Param("minTotal") BigDecimal minTotal);
    
    // Aggregations
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countByStatus();
    
    // Subqueries
    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT o.user.id FROM Order o WHERE o.createdAt > :date)")
    List<User> findActiveUsersSince(@Param("date") LocalDateTime date);
    
    // Native SQL for DB-specific features
    @Query(value = "SELECT * FROM users WHERE MATCH(bio) AGAINST(:term)", 
           nativeQuery = true)
    List<User> fullTextSearch(@Param("term") String term);
    
    // DTO projections
    @Query("SELECT new com.example.dto.UserSummary(u.id, u.email) FROM User u")
    List<UserSummary> findAllSummaries();
}
```

**Best Practice:**
- Use derived queries for simple, self-documenting queries
- Use @Query when method names become too long or complex
- Use @Query for any aggregation, subquery, or special SQL features

---

## Q48. How do you handle lazy loading outside of transaction? (Intermediate)

**Answer:**

Lazy loading requires an active Hibernate session. Outside a transaction, accessing lazy associations throws `LazyInitializationException`.

**Solutions:**

**1. Keep Transaction Open (Open Session in View):**
```yaml
spring:
  jpa:
    open-in-view: true  # Default in Spring Boot
```
*Not recommended for REST APIs due to potential performance issues.*

**2. Initialize in Service Layer:**
```java
@Service
public class OrderService {
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderWithItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        // Force initialization within transaction
        Hibernate.initialize(order.getItems());
        
        return OrderDTO.fromEntity(order);
    }
}
```

**3. Use JOIN FETCH:**
```java
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Optional<Order> findByIdWithItems(@Param("id") Long id);

// Or EntityGraph
@EntityGraph(attributePaths = {"items"})
Optional<Order> findById(Long id);
```

**4. Use DTOs/Projections:**
```java
@Query("SELECT new com.example.dto.OrderSummary(o.id, o.orderDate, SIZE(o.items)) " +
       "FROM Order o WHERE o.id = :id")
Optional<OrderSummary> findOrderSummary(@Param("id") Long id);
```

**5. Disable Open-in-View and Use Controlled Loading:**
```yaml
spring:
  jpa:
    open-in-view: false
```

```java
@RestController
public class OrderController {
    
    @GetMapping("/orders/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        // Service method handles all data loading within transaction
        return orderService.getOrderWithItems(id);
    }
}
```

**Best Practice:** Disable `open-in-view` and load all required data within service transactions using JOIN FETCH or EntityGraph.

---

## Q49. What are named queries and when should you use them? (Intermediate)

**Answer:**

Named queries are predefined, statically checked queries defined at entity level that can be reused across the application.

**Defining Named Queries:**
```java
@Entity
@NamedQueries({
    @NamedQuery(
        name = "Book.findByCategory",
        query = "SELECT b FROM Book b WHERE b.category = :category ORDER BY b.title"
    ),
    @NamedQuery(
        name = "Book.findBestSellers",
        query = "SELECT b FROM Book b WHERE b.salesCount > :minSales ORDER BY b.salesCount DESC"
    )
})
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "Book.findByYearNative",
        query = "SELECT * FROM books WHERE YEAR(published_date) = :year",
        resultClass = Book.class
    )
})
public class Book { ... }
```

**Using Named Queries in Repository:**
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Method name matches named query name (without entity prefix)
    List<Book> findByCategory(@Param("category") String category);
    
    // Explicit reference
    @Query(name = "Book.findBestSellers")
    List<Book> getBestSellers(@Param("minSales") int minSales);
}
```

**Using with EntityManager:**
```java
@Service
public class BookService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Book> findByCategory(String category) {
        return entityManager.createNamedQuery("Book.findByCategory", Book.class)
            .setParameter("category", category)
            .getResultList();
    }
}
```

**Advantages:**
- Validated at startup (syntax errors caught early)
- Can be optimized by JPA provider
- Centralized query definition
- Good for frequently used, complex queries

**When to Use:**
- Complex queries used in multiple places
- Queries that benefit from startup validation
- When you want queries grouped with entity definition

**Alternative - orm.xml:**
```xml
<!-- META-INF/orm.xml -->
<named-query name="Book.findExpensive">
    <query>SELECT b FROM Book b WHERE b.price > :minPrice</query>
</named-query>
```

---

## Q50. What are some common mistakes to avoid in Spring Data JPA? (Advanced)

**Answer:**

**1. N+1 Query Problem:**
```java
// BAD
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
    order.getItems().size(); // N additional queries!
}

// GOOD
@EntityGraph(attributePaths = {"items"})
List<Order> findAll();
```

**2. Ignoring Fetch Types:**
```java
// BAD - EAGER by default causes unnecessary loading
@ManyToOne
private Author author;

// GOOD
@ManyToOne(fetch = FetchType.LAZY)
private Author author;
```

**3. Missing @Transactional:**
```java
// BAD - Each operation in separate transaction
public void updateMultipleEntities() {
    entityA.setName("New");
    entityRepository.save(entityA);
    entityB.setName("New"); // If this fails, entityA is already saved!
    entityRepository.save(entityB);
}

// GOOD
@Transactional
public void updateMultipleEntities() {
    entityA.setName("New");
    entityB.setName("New");
    // Both saved or both rolled back
}
```

**4. Returning Entities from Controllers:**
```java
// BAD - Exposes internal structure, lazy loading issues
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userRepository.findById(id).get();
}

// GOOD - Use DTOs
@GetMapping("/users/{id}")
public UserDTO getUser(@PathVariable Long id) {
    return userService.getUserById(id);
}
```

**5. Not Using existsById:**
```java
// BAD - Loads entire entity
if (userRepository.findById(id).isPresent()) { ... }

// GOOD
if (userRepository.existsById(id)) { ... }
```

**6. Modifying Entities Outside Transaction:**
```java
// BAD - Changes not persisted
public void updateUser(Long id) {
    User user = userRepository.findById(id).get();
    user.setName("New Name");
    // Changes lost!
}

// GOOD
@Transactional
public void updateUser(Long id) {
    User user = userRepository.findById(id).get();
    user.setName("New Name");
    // Dirty checking saves automatically
}
```

**7. Using ddl-auto in Production:**
```yaml
# BAD
spring.jpa.hibernate.ddl-auto: update

# GOOD
spring.jpa.hibernate.ddl-auto: validate
# Use Flyway/Liquibase for migrations
```

**8. Not Handling OptimisticLockException:**
```java
// BAD - Exception crashes application
@Transactional
public void updateProduct(Product product) {
    productRepository.save(product);
}

// GOOD
@Transactional
public void updateProduct(Product product) {
    try {
        productRepository.save(product);
    } catch (OptimisticLockingFailureException e) {
        // Refresh and retry or notify user
    }
}
```

**9. Bidirectional Relationship Not Synced:**
```java
// BAD
Order order = new Order();
OrderItem item = new OrderItem();
order.getItems().add(item);
// item.order is still null!

// GOOD - Use helper methods
public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
}
```

**10. Not Using Indexes:**
```java
// BAD - Slow queries on large tables
SELECT * FROM users WHERE email = ?

// GOOD
@Entity
@Table(indexes = @Index(columnList = "email"))
public class User { ... }
```

---

# Summary

This comprehensive guide covered 50 Spring Data JPA interview questions ranging from beginner to advanced topics:

**Beginner Topics:**
- Repository interfaces and hierarchy
- Derived query methods
- Basic annotations (@Entity, @Id, @GeneratedValue)
- Pagination and sorting
- Relationship mappings

**Intermediate Topics:**
- @Query annotation and JPQL
- Projections and DTOs
- Entity lifecycle callbacks
- Transactions and propagation
- Soft deletes and auditing
- Specifications and dynamic queries

**Advanced Topics:**
- N+1 problem and solutions
- Optimistic and pessimistic locking
- Second-level caching
- Batch processing
- Multiple datasources
- Custom ID generators
- Performance optimization

This knowledge will prepare you for technical interviews focused on Spring Data JPA and database operations in Spring Boot applications.
