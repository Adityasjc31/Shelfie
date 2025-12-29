package com.book.management.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory repository implementation for User entity.
 * Uses HashMap for storage with email as key for fast lookups.
 * 
 * NOTE: When switching to JPA later, replace this with:
 * public interface UserRepository extends JpaRepository<User, Long> { ... }
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
@Repository
@Slf4j
public class UserRepository {

    // Thread-safe data structures for concurrent access
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final Map<String, Long> emailIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Initializes repository with dummy data.
     * This method runs after bean construction.
     */
    @PostConstruct
    public void initializeData() {
        log.info("Initializing user repository with dummy data...");
        
        // Create dummy users
        User customer1 = createDummyUser("Alice Johnson", "alice@example.com", 
                                         "password123", UserRole.CUSTOMER);
        User customer2 = createDummyUser("Bob Smith", "bob@example.com", 
                                         "password123", UserRole.CUSTOMER);
        User admin = createDummyUser("Admin User", "admin@example.com", 
                                     "admin123", UserRole.ADMIN);
        
        // Save dummy users
        save(customer1);
        save(customer2);
        save(admin);
        
        log.info("Initialized {} users in repository", userStore.size());
        log.info("Dummy users - Alice: alice@example.com/password123, Bob: bob@example.com/password123, Admin: admin@example.com/admin123");
    }

    /**
     * Helper method to create dummy users.
     */
    private User createDummyUser(String name, String email, String password, UserRole role) {
        return User.builder()
                .name(name)
                .email(email)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .role(role)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Saves a user to the repository.
     * 
     * @param user the user to save
     * @return the saved user with generated ID
     */
    public User save(User user) {
        if (user.getUserId() == null) {
            // New user - generate ID
            user.setUserId(idGenerator.getAndIncrement());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            log.debug("Creating new user with ID: {}", user.getUserId());
        } else {
            // Update existing user
            user.setUpdatedAt(LocalDateTime.now());
            log.debug("Updating user with ID: {}", user.getUserId());
        }
        
        userStore.put(user.getUserId(), user);
        emailIndex.put(user.getEmail().toLowerCase(), user.getUserId());
        
        return user;
    }

    /**
     * Finds a user by ID.
     * 
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userStore.get(userId));
    }

    /**
     * Finds a user by email (case-insensitive).
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        Long userId = emailIndex.get(email.toLowerCase());
        if (userId != null) {
            return Optional.ofNullable(userStore.get(userId));
        }
        return Optional.empty();
    }

    /**
     * Checks if a user exists with the given email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email.toLowerCase());
    }

    /**
     * Checks if a user exists with the given ID.
     * 
     * @param userId the user ID to check
     * @return true if user exists, false otherwise
     */
    public boolean existsById(Long userId) {
        return userStore.containsKey(userId);
    }

    /**
     * Finds all users in the repository.
     * 
     * @return list of all users
     */
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    /**
     * Finds all users with a specific role.
     * 
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    public List<User> findByRole(UserRole role) {
        return userStore.values().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }

    /**
     * Finds all active or inactive users.
     * 
     * @param isActive the active status to filter by
     * @return list of users matching the active status
     */
    public List<User> findByIsActive(Boolean isActive) {
        return userStore.values().stream()
                .filter(user -> user.getIsActive().equals(isActive))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a user by ID.
     * 
     * @param userId the user ID to delete
     */
    public void deleteById(Long userId) {
        User user = userStore.remove(userId);
        if (user != null) {
            emailIndex.remove(user.getEmail().toLowerCase());
            log.debug("Deleted user with ID: {}", userId);
        }
    }

    /**
     * Counts the total number of users.
     * 
     * @return total user count
     */
    public long count() {
        return userStore.size();
    }

    /**
     * Clears all users from the repository (for testing).
     */
    public void deleteAll() {
        userStore.clear();
        emailIndex.clear();
        log.debug("Cleared all users from repository");
    }
}
