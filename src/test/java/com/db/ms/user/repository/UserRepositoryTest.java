// ============================================================================
// FILE: src/test/java/com/bookstore/user/repository/UserRepositoryTest.java
// ============================================================================
package com.db.ms.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;
import com.book.management.user.repository.impl.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for In-Memory UserRepository.
 * Tests all repository operations.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create fresh repository for each test
        userRepository = new UserRepository();
        
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();
    }

    // ========== SAVE TESTS ==========

    @Test
    @DisplayName("Should save user successfully and generate ID")
    void testSaveUser() {
        User savedUser = userRepository.save(testUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should generate incremental IDs for multiple users")
    void testSaveMultipleUsers() {
        User user1 = userRepository.save(testUser);
        
        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();
        User savedUser2 = userRepository.save(user2);

        assertThat(user1.getUserId()).isLessThan(savedUser2.getUserId());
    }

    // ========== FIND BY ID TESTS ==========

    @Test
    @DisplayName("Should find user by ID successfully")
    void testFindById() {
        User savedUser = userRepository.save(testUser);

        Optional<User> found = userRepository.findById(savedUser.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty Optional when user ID not found")
    void testFindById_NotFound() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    // ========== FIND BY EMAIL TESTS ==========

    @Test
    @DisplayName("Should find user by email successfully")
    void testFindByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should find user by email case-insensitively")
    void testFindByEmail_CaseInsensitive() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("TEST@EXAMPLE.COM");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty Optional when email not found")
    void testFindByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("notfound@example.com");

        assertThat(found).isEmpty();
    }

    // ========== EXISTS BY EMAIL TESTS ==========

    @Test
    @DisplayName("Should check if user exists by email")
    void testExistsByEmail() {
        userRepository.save(testUser);

        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("notfound@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should check email existence case-insensitively")
    void testExistsByEmail_CaseInsensitive() {
        userRepository.save(testUser);

        boolean exists = userRepository.existsByEmail("TEST@EXAMPLE.COM");

        assertThat(exists).isTrue();
    }

    // ========== EXISTS BY ID TESTS ==========

    @Test
    @DisplayName("Should check if user exists by ID")
    void testExistsById() {
        User savedUser = userRepository.save(testUser);

        boolean exists = userRepository.existsById(savedUser.getUserId());
        boolean notExists = userRepository.existsById(999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    // ========== FIND ALL TESTS ==========

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(testUser);
        userRepository.save(user2);

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAll_Empty() {
        userRepository.deleteAll(); // Clear any existing data

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).isEmpty();
    }

    // ========== FIND BY ROLE TESTS ==========

    @Test
    @DisplayName("Should find users by role")
    void testFindByRole() {
        User admin = User.builder()
                .name("Admin User")
                .email("admin@test.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(testUser); // CUSTOMER
        userRepository.save(admin);    // ADMIN

        List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);

        assertThat(customers).hasSize(1);
        assertThat(admins).hasSize(1);
        assertThat(customers.get(0).getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(admins.get(0).getRole()).isEqualTo(UserRole.ADMIN);
    }

    // ========== FIND BY IS ACTIVE TESTS ==========

    @Test
    @DisplayName("Should find users by active status")
    void testFindByIsActive() {
        User inactiveUser = User.builder()
                .name("Inactive User")
                .email("inactive@test.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(false)
                .build();

        userRepository.save(testUser);      // Active
        userRepository.save(inactiveUser);  // Inactive

        List<User> activeUsers = userRepository.findByIsActive(true);
        List<User> inactiveUsers = userRepository.findByIsActive(false);

        assertThat(activeUsers).hasSize(1);
        assertThat(inactiveUsers).hasSize(1);
        assertThat(activeUsers.get(0).getIsActive()).isTrue();
        assertThat(inactiveUsers.get(0).getIsActive()).isFalse();
    }

    // ========== DELETE BY ID TESTS ==========

    @Test
    @DisplayName("Should delete user by ID successfully")
    void testDeleteById() {
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getUserId();

        userRepository.deleteById(userId);

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should remove user from email index when deleted")
    void testDeleteById_RemovesEmailIndex() {
        User savedUser = userRepository.save(testUser);
        String email = savedUser.getEmail();

        userRepository.deleteById(savedUser.getUserId());

        boolean exists = userRepository.existsByEmail(email);
        assertThat(exists).isFalse();
    }

    // ========== UPDATE TESTS ==========

    @Test
    @DisplayName("Should update existing user successfully")
    void testUpdateUser() {
        User savedUser = userRepository.save(testUser);
        
        savedUser.setName("Updated Name");
        User updatedUser = userRepository.save(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
    }

    // ========== COUNT TESTS ==========

    @Test
    @DisplayName("Should count total users correctly")
    void testCount() {
        assertThat(userRepository.count()).isEqualTo(0);

        userRepository.save(testUser);
        assertThat(userRepository.count()).isEqualTo(1);

        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();
        userRepository.save(user2);
        
        assertThat(userRepository.count()).isEqualTo(2);
    }

    // ========== DELETE ALL TESTS ==========

    @Test
    @DisplayName("Should delete all users successfully")
    void testDeleteAll() {
        userRepository.save(testUser);
        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();
        userRepository.save(user2);

        assertThat(userRepository.count()).isEqualTo(2);

        userRepository.deleteAll();

        assertThat(userRepository.count()).isEqualTo(0);
        assertThat(userRepository.findAll()).isEmpty();
    }
}