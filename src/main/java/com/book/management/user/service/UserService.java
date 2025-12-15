package com.book.management.user.service;

import java.util.List;

import com.book.management.user.dto.*;
import com.book.management.user.model.UserRole;

/**
 * Service interface for User Management operations.
 * Defines business logic methods for user-related functionality.
 * 
 * Following Interface Segregation Principle (SOLID).
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * 
     * @param registrationDTO the registration details
     * @return the created user details
     * @throws com.bookstore.user.exception.UserAlreadyExistsException if email already exists
     */
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);

    /**
     * Authenticates a user with email and password.
     * 
     * @param loginDTO the login credentials
     * @return the authenticated user details
     * @throws com.bookstore.user.exception.InvalidCredentialsException if credentials are invalid
     * @throws com.bookstore.user.exception.UserInactiveException if user account is inactive
     */
    UserResponseDTO loginUser(UserLoginDTO loginDTO);

    /**
     * Retrieves a user by their ID.
     * 
     * @param userId the user ID
     * @return the user details
     * @throws com.bookstore.user.exception.UserNotFoundException if user not found
     */
    UserResponseDTO getUserById(Long userId);

    /**
     * Retrieves a user by their email.
     * 
     * @param email the user email
     * @return the user details
     * @throws com.bookstore.user.exception.UserNotFoundException if user not found
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Retrieves all users in the system.
     * 
     * @return list of all users
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Retrieves all users with a specific role.
     * 
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    List<UserResponseDTO> getUsersByRole(UserRole role);

    /**
     * Updates user profile information.
     * 
     * @param userId    the user ID
     * @param updateDTO the updated profile data
     * @return the updated user details
     * @throws com.bookstore.user.exception.UserNotFoundException if user not found
     */
    UserResponseDTO updateUserProfile(Long userId, UserUpdateDTO updateDTO);

    /**
     * Deactivates a user account.
     * 
     * @param userId the user ID
     * @throws com.bookstore.user.exception.UserNotFoundException if user not found
     */
    void deactivateUser(Long userId);

    /**
     * Reactivates a user account.
     * 
     * @param userId the user ID
     * @throws com.bookstore.user.exception.UserNotFoundException if user not found
     */
    void reactivateUser(Long userId);

    /**
     * Deletes a user from the system.
     * 
     * @param userId the user ID
     * @throws com.bookstore.user.exception.UserNotFoundException if user not found
     */
    void deleteUser(Long userId);
}
