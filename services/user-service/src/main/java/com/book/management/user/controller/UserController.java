package com.book.management.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.book.management.user.dto.*;
import com.book.management.user.model.UserRole;
import com.book.management.user.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for User Management operations.
 * Provides endpoints for user registration, authentication, and profile management.
 * 
 * Base URL: /api/v1/users
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user.
     * 
     * POST /api/v1/users/register
     * 
     * @param registrationDTO the registration details
     * @return ResponseEntity with created user details and 201 status
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {
        
        log.info("Received registration request for email: {}", registrationDTO.getEmail());
        UserResponseDTO response = userService.registerUser(registrationDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user.
     * 
     * POST /api/v1/users/login
     * 
     * @param loginDTO the login credentials
     * @return ResponseEntity with authenticated user details and 200 status
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(
            @Valid @RequestBody UserLoginDTO loginDTO) {
        
        log.info("Received login request for email: {}", loginDTO.getEmail());
        UserResponseDTO response = userService.loginUser(loginDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a user by ID.
     * 
     * GET /api/v1/users/{userId}
     * 
     * @param userId the user ID
     * @return ResponseEntity with user details and 200 status
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        log.info("Received request to fetch user with ID: {}", userId);
        UserResponseDTO response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a user by email.
     * 
     * GET /api/v1/users/email/{email}
     * 
     * @param email the user email
     * @return ResponseEntity with user details and 200 status
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("Received request to fetch user with email: {}", email);
        UserResponseDTO response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all users.
     * 
     * GET /api/v1/users
     * 
     * @return ResponseEntity with list of all users and 200 status
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Received request to fetch all users");
        List<UserResponseDTO> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves users by role.
     * 
     * GET /api/v1/users/role/{role}
     * 
     * @param role the user role (CUSTOMER or ADMIN)
     * @return ResponseEntity with list of users and 200 status
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable UserRole role) {
        log.info("Received request to fetch users with role: {}", role);
        List<UserResponseDTO> response = userService.getUsersByRole(role);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates user profile.
     * 
     * PUT /api/v1/users/{userId}
     * 
     * @param userId    the user ID
     * @param updateDTO the updated profile data
     * @return ResponseEntity with updated user details and 200 status
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        
        log.info("Received request to update profile for user ID: {}", userId);
        UserResponseDTO response = userService.updateUserProfile(userId, updateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a user account.
     * 
     * PATCH /api/v1/users/{userId}/deactivate
     * 
     * @param userId the user ID
     * @return ResponseEntity with 204 No Content status
     */
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        log.info("Received request to deactivate user ID: {}", userId);
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reactivates a user account.
     * 
     * PATCH /api/v1/users/{userId}/reactivate
     * 
     * @param userId the user ID
     * @return ResponseEntity with 204 No Content status
     */
    @PatchMapping("/{userId}/reactivate")
    public ResponseEntity<Void> reactivateUser(@PathVariable Long userId) {
        log.info("Received request to reactivate user ID: {}", userId);
        userService.reactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a user.
     * 
     * DELETE /api/v1/users/{userId}
     * 
     * @param userId the user ID
     * @return ResponseEntity with 204 No Content status
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Received request to delete user ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}