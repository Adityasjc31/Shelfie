
package com.db.ms.user.repository;

import java.util.List;
import java.util.Optional;

import com.db.ms.user.model.User;

public interface UserRepository {
    User save(User user);                       // Create/Update

    List<User> findAll();                       // Read All

    Optional<User> findById(Long id);           // Read By ID

    Optional<User> findByEmail(String email);   // Read By Field (email)

    boolean delete(Long id);                    // Delete
}