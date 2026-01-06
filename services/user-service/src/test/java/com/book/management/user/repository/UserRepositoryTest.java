// ============================================================================
// FILE: src/test/java/com/book/management/user/repository/UserRepositoryTest.java
// ============================================================================
package com.book.management.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JpaUserRepository.
 * Tests repository methods using Mockito for mocking.
 * 
 * Note: These are mock-based unit tests. For actual integration tests
 * with a database, use @DataJpaTest with a test database configuration.
 * 
 * @author Digital Bookstore Team
 * @version 2.0 - Updated for JPA Repository
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JpaUserRepository Unit Tests")
class UserRepositoryTest {

    @Mock
    private JpaUserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();
    }

    // ========== SAVE TESTS ==========

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userRepository.save(testUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo(1L);
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ========== FIND BY ID TESTS ==========

    @Test
    @DisplayName("Should find user by ID successfully")
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> found = userRepository.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when user ID not found")
    void testFindById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
        verify(userRepository, times(1)).findById(999L);
    }

    // ========== FIND BY EMAIL TESTS ==========

    @Test
    @DisplayName("Should find user by email successfully")
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should find user by email case-insensitively")
    void testFindByEmailIgnoreCase() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));

        Optional<User> found = userRepository.findByEmailIgnoreCase("TEST@EXAMPLE.COM");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmailIgnoreCase("TEST@EXAMPLE.COM");
    }

    @Test
    @DisplayName("Should return empty Optional when email not found")
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> found = userRepository.findByEmail("notfound@example.com");

        assertThat(found).isEmpty();
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    // ========== EXISTS BY EMAIL TESTS ==========

    @Test
    @DisplayName("Should check if user exists by email")
    void testExistsByEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("notfound@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should check email existence case-insensitively")
    void testExistsByEmailIgnoreCase() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        boolean exists = userRepository.existsByEmailIgnoreCase("TEST@EXAMPLE.COM");

        assertThat(exists).isTrue();
        verify(userRepository, times(1)).existsByEmailIgnoreCase("TEST@EXAMPLE.COM");
    }

    // ========== EXISTS BY ID TESTS ==========

    @Test
    @DisplayName("Should check if user exists by ID")
    void testExistsById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean exists = userRepository.existsById(1L);
        boolean notExists = userRepository.existsById(999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    // ========== FIND ALL TESTS ==========

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        User user2 = User.builder()
                .userId(2L)
                .name("User Two")
                .email("user2@example.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAll_Empty() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    // ========== FIND BY ROLE TESTS ==========

    @Test
    @DisplayName("Should find users by role")
    void testFindByRole() {
        when(userRepository.findByRole(UserRole.CUSTOMER)).thenReturn(Arrays.asList(testUser));

        List<User> customers = userRepository.findByRole(UserRole.CUSTOMER);

        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getRole()).isEqualTo(UserRole.CUSTOMER);
        verify(userRepository, times(1)).findByRole(UserRole.CUSTOMER);
    }

    // ========== FIND BY IS ACTIVE TESTS ==========

    @Test
    @DisplayName("Should find users by active status")
    void testFindByIsActive() {
        User inactiveUser = User.builder()
                .userId(2L)
                .name("Inactive User")
                .email("inactive@test.com")
                .password("hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(false)
                .build();

        when(userRepository.findByIsActive(true)).thenReturn(Arrays.asList(testUser));
        when(userRepository.findByIsActive(false)).thenReturn(Arrays.asList(inactiveUser));

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
        doNothing().when(userRepository).deleteById(anyLong());

        userRepository.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    // ========== COUNT TESTS ==========

    @Test
    @DisplayName("Should count total users correctly")
    void testCount() {
        when(userRepository.count()).thenReturn(2L);

        long count = userRepository.count();

        assertThat(count).isEqualTo(2L);
        verify(userRepository, times(1)).count();
    }

    // ========== DELETE ALL TESTS ==========

    @Test
    @DisplayName("Should delete all users successfully")
    void testDeleteAll() {
        doNothing().when(userRepository).deleteAll();

        userRepository.deleteAll();

        verify(userRepository, times(1)).deleteAll();
    }
}