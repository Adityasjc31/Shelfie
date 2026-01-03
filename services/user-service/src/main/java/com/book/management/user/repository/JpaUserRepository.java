package com.book.management.user.repository;

import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity.
 * 
 * Provides CRUD operations and custom query methods for User management.
 * Extends JpaRepository for full JPA functionality including pagination and
 * sorting.
 * 
 * @author Abdul Ahad
 * @version 2.0 - Spring Data JPA
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address (case-insensitive).
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Finds a user by their email address.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists with the given email (case-insensitive).
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Finds all users with a specific role.
     * 
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Finds all active or inactive users.
     * 
     * @param isActive the active status to filter by
     * @return list of users matching the active status
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Finds all users with a specific role and active status.
     * 
     * @param role     the role to filter by
     * @param isActive the active status to filter by
     * @return list of users matching both criteria
     */
    List<User> findByRoleAndIsActive(UserRole role, Boolean isActive);
}
