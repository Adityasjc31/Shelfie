package com.book.management.user.config;

import com.book.management.user.model.User;
import com.book.management.user.model.UserRole;
import com.book.management.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Data Initializer Configuration.
 * 
 * Seeds the database with test users on application startup.
 * Only runs in non-production profiles.
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final JpaUserRepository userRepository;

    /**
     * Seeds initial test data into the database.
     * Creates sample customers and admin users.
     */
    @Bean
    @Profile("!prod")
    public CommandLineRunner initializeData() {
        return args -> {
            // Check if data already exists
            if (userRepository.count() > 0) {
                log.info("Database already contains users, skipping initialization");
                return;
            }

            log.info("Initializing database with test users...");

            // Create test customers
            User customer1 = User.builder()
                    .name("Alice Johnson")
                    .email("alice@example.com")
                    .password(BCrypt.hashpw("password123", BCrypt.gensalt()))
                    .role(UserRole.CUSTOMER)
                    .isActive(true)
                    .build();

            User customer2 = User.builder()
                    .name("Bob Smith")
                    .email("bob@example.com")
                    .password(BCrypt.hashpw("password123", BCrypt.gensalt()))
                    .role(UserRole.CUSTOMER)
                    .isActive(true)
                    .build();

            // Create test admin
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .password(BCrypt.hashpw("admin123", BCrypt.gensalt()))
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .build();

            // Save all users
            userRepository.save(customer1);
            userRepository.save(customer2);
            userRepository.save(admin);

            log.info("Initialized {} users in database", userRepository.count());
            log.info("Test credentials:");
            log.info("  - Customer: alice@example.com / password123");
            log.info("  - Customer: bob@example.com / password123");
            log.info("  - Admin: admin@example.com / admin123");
        };
    }
}
