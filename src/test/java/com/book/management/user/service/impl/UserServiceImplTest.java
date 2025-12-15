// ============================================================================
// FILE: src/test/java/com/bookstore/user/service/impl/UserServiceImplTest.java
// ============================================================================
package com.book.management.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.management.user.dto.*;
import com.book.management.user.exception.*;
import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;
import com.book.management.user.repository.impl.UserRepository;
import com.book.management.user.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl class.
 * Tests all business logic methods with various scenarios.
 * 
 * Uses Mockito for mocking dependencies and JUnit 5 for assertions.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationDTO registrationDTO;
    private UserLoginDTO loginDTO;
    private UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("$2a$10$hashedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registrationDTO = UserRegistrationDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .role(UserRole.CUSTOMER)
                .build();

        loginDTO = UserLoginDTO.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        updateDTO = UserUpdateDTO.builder()
                .name("John Updated")
                .password("newPassword123")
                .build();
    }

    // ========== REGISTRATION TESTS ==========

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO response = userService.registerUser(registrationDTO);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(response.getIsActive()).isTrue();

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterUser_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(registrationDTO))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("john@example.com");

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== LOGIN TESTS ==========

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginUser_Success() {
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("password123", 
                                org.mindrot.jbcrypt.BCrypt.gensalt());
        testUser.setPassword(hashedPassword);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        UserResponseDTO response = userService.loginUser(loginDTO);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getUserId()).isEqualTo(1L);

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found during login")
    void testLoginUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loginUser(loginDTO))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user account is inactive")
    void testLoginUser_InactiveAccount() {
        testUser.setIsActive(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.loginUser(loginDTO))
                .isInstanceOf(UserInactiveException.class)
                .hasMessageContaining("inactive");

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void testLoginUser_InvalidPassword() {
        String wrongHashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("wrongPassword", 
                                     org.mindrot.jbcrypt.BCrypt.gensalt());
        testUser.setPassword(wrongHashedPassword);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.loginUser(loginDTO))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    // ========== GET USER BY ID TESTS ==========

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDTO response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john@example.com");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user ID not found")
    void testGetUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");

        verify(userRepository, times(1)).findById(999L);
    }

    // ========== GET USER BY EMAIL TESTS ==========

    @Test
    @DisplayName("Should get user by email successfully")
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        UserResponseDTO response = userService.getUserByEmail("john@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");

        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should throw exception when email not found")
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("notfound@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("notfound@example.com");

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    // ========== GET ALL USERS TESTS ==========

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers_Success() {
        User user2 = User.builder()
                .userId(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<UserResponseDTO> response = userService.getAllUsers();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(response.get(1).getEmail()).isEqualTo("jane@example.com");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<UserResponseDTO> response = userService.getAllUsers();

        assertThat(response).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    // ========== GET USERS BY ROLE TESTS ==========

    @Test
    @DisplayName("Should get users by role successfully")
    void testGetUsersByRole_Success() {
        when(userRepository.findByRole(UserRole.CUSTOMER))
                .thenReturn(Arrays.asList(testUser));

        List<UserResponseDTO> response = userService.getUsersByRole(UserRole.CUSTOMER);

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getRole()).isEqualTo(UserRole.CUSTOMER);

        verify(userRepository, times(1)).findByRole(UserRole.CUSTOMER);
    }

    // ========== UPDATE USER PROFILE TESTS ==========

    @Test
    @DisplayName("Should update user profile successfully")
    void testUpdateUserProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO response = userService.updateUserProfile(1L, updateDTO);

        assertThat(response).isNotNull();
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update only name when password is null")
    void testUpdateUserProfile_NameOnly() {
        UserUpdateDTO nameOnlyDTO = UserUpdateDTO.builder()
                .name("Updated Name")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO response = userService.updateUserProfile(1L, nameOnlyDTO);

        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserProfile_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserProfile(999L, updateDTO))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== DEACTIVATE USER TESTS ==========

    @Test
    @DisplayName("Should deactivate user successfully")
    void testDeactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when deactivating non-existent user")
    void testDeactivateUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivateUser(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== REACTIVATE USER TESTS ==========

    @Test
    @DisplayName("Should reactivate user successfully")
    void testReactivateUser_Success() {
        testUser.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.reactivateUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ========== DELETE USER TESTS ==========

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}