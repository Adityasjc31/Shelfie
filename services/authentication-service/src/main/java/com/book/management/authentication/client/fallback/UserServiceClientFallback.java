package com.book.management.authentication.client.fallback;

import com.book.management.authentication.client.UserServiceClient;
import com.book.management.authentication.dto.UserLoginDTO;
import com.book.management.authentication.dto.UserRegistrationDTO;
import com.book.management.authentication.dto.UserResponseDTO;
import com.book.management.authentication.exception.UserServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for UserServiceClient.
 * 
 * This class is triggered when the User Service is down or responding too
 * slowly.
 * Provides graceful degradation by throwing descriptive exceptions.
 *
 * @author Aditya Srivastava
 * @version 1.0
 */
@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    private static final String SERVICE_UNAVAILABLE_MSG = "User Service is temporarily unavailable. Please try again later.";

    @Override
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.error("FALLBACK: User Service unreachable during registration for email: {}",
                registrationDTO.getEmail());
        throw new UserServiceException(SERVICE_UNAVAILABLE_MSG);
    }

    @Override
    public UserResponseDTO loginUser(UserLoginDTO loginDTO) {
        log.error("FALLBACK: User Service unreachable during login for email: {}",
                loginDTO.getEmail());
        throw new UserServiceException(SERVICE_UNAVAILABLE_MSG);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        log.error("FALLBACK: User Service unreachable while fetching user ID: {}", userId);
        throw new UserServiceException(SERVICE_UNAVAILABLE_MSG);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        log.error("FALLBACK: User Service unreachable while fetching user email: {}", email);
        throw new UserServiceException(SERVICE_UNAVAILABLE_MSG);
    }
}
