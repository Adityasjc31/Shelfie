package com.book.management.review_rating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.book.management.review_rating.client.BookServiceClient;

/**
 * Application context test for ReviewRatingApplication.
 * Verifies that the Spring Boot application context loads correctly.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class ReviewRatingApplicationTests {

    @MockitoBean
    private BookServiceClient bookServiceClient;

    @Test
    void contextLoads() {
        // Application context should load successfully
    }
}
