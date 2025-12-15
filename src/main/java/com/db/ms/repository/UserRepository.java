
package com.db.ms.repository;

import com.db.ms.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);                       // Create/Update

    List<User> findAll();                       // Read All

    Optional<User> findById(Long id);           // Read By ID

    Optional<User> findByEmail(String email);   // Read By Field (email)

    boolean delete(Long id);                    // Delete
}