package com.book.management.review_rating.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration.
 * 
 * Enables automatic population of @CreatedDate and @LastModifiedDate fields.
 * Separated from the main application class to allow proper test slicing
 * with @WebMvcTest sans JPA infrastructure.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-07
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
