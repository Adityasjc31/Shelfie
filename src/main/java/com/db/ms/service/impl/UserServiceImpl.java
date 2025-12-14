package com.db.ms.service.impl;

import com.db.ms.dto.*;
import com.db.ms.model.User;
import com.db.ms.model.UserRole;
import com.db.ms.exception.*;
import com.db.ms.repository.impl.UserRepository;
import com.db.ms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 * Handles all business logic for user management.
 * 
 * Following Single Responsibility and Dependency Inversion Principles (SOLID).
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Attempting to register user with email: {}", registrationDTO.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            log.error("Registration failed - Email already exists: {}", registrationDTO.getEmail());
            throw new UserAlreadyExistsException(registrationDTO.getEmail());
        }

        // Create new user entity
        User user = User.builder()
                .name(registrationDTO.getName())
                .email(registrationDTO.getEmail())
                .password(hashPassword(registrationDTO.getPassword()))
                .role(registrationDTO.getRole())
                .isActive(true)
                .build();

        // Save user to repository
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getUserId());

        return mapToResponseDTO(savedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO loginUser(UserLoginDTO loginDTO) {
        log.info("Login attempt for email: {}", loginDTO.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed - User not found: {}", loginDTO.getEmail());
                    return new InvalidCredentialsException();
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.error("Login failed - User account inactive: {}", loginDTO.getEmail());
            throw new UserInactiveException();
        }

        // Verify password
        if (!verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            log.error("Login failed - Invalid password for: {}", loginDTO.getEmail());
            throw new InvalidCredentialsException();
        }

        log.info("User logged in successfully: {}", user.getUserId());
        return mapToResponseDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException(userId);
                });

        return mapToResponseDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });

        return mapToResponseDTO(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();
        log.info("Retrieved {} users", users.size());

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
        log.info("Fetching users with role: {}", role);

        List<User> users = userRepository.findByRole(role);
        log.info("Retrieved {} users with role: {}", users.size(), role);

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO updateUserProfile(Long userId, UserUpdateDTO updateDTO) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Update failed - User not found with ID: {}", userId);
                    return new UserNotFoundException(userId);
                });

        // Update name if provided
        if (updateDTO.getName() != null && !updateDTO.getName().isBlank()) {
            user.setName(updateDTO.getName());
            log.debug("Updated name for user ID: {}", userId);
        }

        // Update password if provided
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            user.setPassword(hashPassword(updateDTO.getPassword()));
            log.debug("Updated password for user ID: {}", userId);
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return mapToResponseDTO(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Deactivation failed - User not found with ID: {}", userId);
                    return new UserNotFoundException(userId);
                });

        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully: {}", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactivateUser(Long userId) {
        log.info("Reactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Reactivation failed - User not found with ID: {}", userId);
                    return new UserNotFoundException(userId);
                });

        user.setIsActive(true);
        userRepository.save(user);
        log.info("User reactivated successfully: {}", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("Deletion failed - User not found with ID: {}", userId);
            throw new UserNotFoundException(userId);
        }

        userRepository.deleteById(userId);
        log.info("User deleted successfully: {}", userId);
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
     * Maps a User entity to UserResponseDTO.
     * 
     * @param user the user entity
     * @return the user response DTO
     */
    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
