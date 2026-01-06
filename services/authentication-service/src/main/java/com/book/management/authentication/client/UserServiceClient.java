package com.book.management.authentication.client;

import com.book.management.authentication.client.fallback.UserServiceClientFallback;
import com.book.management.authentication.dto.UserLoginDTO;
import com.book.management.authentication.dto.UserRegistrationDTO;
import com.book.management.authentication.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for User Service communication.
 * 
 * Provides declarative REST client for user operations:
 * - User registration
 * - User authentication (login)
 * - User retrieval by ID or email
 *
 * @author Aditya Srivastava
 * @version 1.0
 */
@FeignClient(name = "user-service", path = "/api/v1/users", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    /**
     * Registers a new user.
     *
     * @param registrationDTO user registration details
     * @return registered user response
     */
    @PostMapping("/register")
    UserResponseDTO registerUser(@RequestBody UserRegistrationDTO registrationDTO);

    /**
     * Authenticates a user with credentials.
     *
     * @param loginDTO user login credentials
     * @return authenticated user response
     */
    @PostMapping("/login")
    UserResponseDTO loginUser(@RequestBody UserLoginDTO loginDTO);

    /**
     * Retrieves a user by ID.
     *
     * @param userId the user ID
     * @return user response
     */
    @GetMapping("/{userId}")
    UserResponseDTO getUserById(@PathVariable("userId") Long userId);

    /**
     * Retrieves a user by email.
     *
     * @param email the user email
     * @return user response
     */
    @GetMapping("/email/{email}")
    UserResponseDTO getUserByEmail(@PathVariable("email") String email);
}
