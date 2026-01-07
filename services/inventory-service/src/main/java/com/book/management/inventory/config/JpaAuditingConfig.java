package com.book.management.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration.
 * 
 * Separated from main application class to allow exclusion in unit tests.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
