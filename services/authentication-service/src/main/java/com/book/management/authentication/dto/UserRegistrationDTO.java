package com.book.management.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 * Used for communication with User Service via FeignClient.
 *
 * @author Aditya Srivastava
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    private String name;
    private String email;
    private String password;
    private String role;
}
