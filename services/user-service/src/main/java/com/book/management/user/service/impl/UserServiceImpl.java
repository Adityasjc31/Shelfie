package com.book.management.user.service.impl;

import com.book.management.user.client.OrderServiceClient;
import com.book.management.user.client.ReviewServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.management.user.dto.*;
import com.book.management.user.exception.*;
import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;
import com.book.management.user.repository.JpaUserRepository;
import com.book.management.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 * Handles all business logic for user management.
 * 
 * Uses Spring Data JPA for persistence.
 * Logging is handled by AOP (LoggingAspect).
 * 
 * Following Single Responsibility and Dependency Inversion Principles (SOLID).
 * 
 * @author Abdul Ahad
 * @version 2.0 - Spring Data JPA
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;
    private final OrderServiceClient orderServiceClient;
    private final ReviewServiceClient reviewServiceClient;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if user already exists
        if (userRepository.existsByEmailIgnoreCase(registrationDTO.getEmail())) {
            throw new UserAlreadyExistsException(registrationDTO.getEmail());
        }

        // Parse role from String
        UserRole role = parseRole(registrationDTO.getRole());

        // Create new user entity
        User user = User.builder()
                .name(registrationDTO.getName())
                .email(registrationDTO.getEmail().toLowerCase())
                .password(hashPassword(registrationDTO.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        // Save user to database
        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO loginUser(UserLoginDTO loginDTO) {
        // Find user by email
        User user = userRepository.findByEmailIgnoreCase(loginDTO.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UserInactiveException();
        }

        // Verify password
        if (!verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return mapToResponseDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return mapToResponseDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return mapToResponseDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserResponseDTO updateUserProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Update name if provided
        if (updateDTO.getName() != null && !updateDTO.getName().isBlank()) {
            user.setName(updateDTO.getName());
        }

        // Update password if provided
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            user.setPassword(hashPassword(updateDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        return mapToResponseDTO(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setIsActive(true);
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteUser(Long userId) {
        // 1. Check if user exists first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Delete the user from the local database
        userRepository.delete(user);

        // 3. Call the Order Service to clean up related data
        try {
            orderServiceClient.deleteOrdersByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to delete orders for user {}: {}", userId, e.getMessage());
        }

        // 4. Call the Review Service to clean up related reviews
        try {
            reviewServiceClient.deleteReviewsByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to delete reviews for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Hashes a plain text password using BCrypt.
     * 
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain text password against a hashed password.
     * 
     * @param plainPassword  the plain text password
     * @param hashedPassword the hashed password
     * @return true if passwords match, false otherwise
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Parses a role string to UserRole enum.
     * 
     * @param roleStr the role string
     * @return the UserRole enum value
     * @throws ValidationException if role is invalid
     */
    private UserRole parseRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            throw new ValidationException("Role is required");
        }

        try {
            return UserRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role: " + roleStr + ". Valid roles are: CUSTOMER, ADMIN");
        }
    }

    /**
     * Maps a User entity to UserResponseDTO.
     * Role is converted to String for auth-service compatibility.
     * 
     * @param user the user entity
     * @return the user response DTO
     */
    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name()) // Convert enum to String
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
