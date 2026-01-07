package com.book.management.order;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for {@link OrderApplication}.
 *
 * Tests the Spring Boot application startup and context loading.
 * Verifies that all beans are properly configured and the application
 * context loads successfully.
 *
 * Test Coverage:
 * - Application context loads successfully
 * - Main application class exists and is properly configured
 * - All required beans are present in context
 * - Application is ready for deployment
 *
 * Note: Disabled by default. Requires database configuration in application-test.properties
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires database configuration. Enable when test profile is configured.")
class OrderApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Test: Verify that the Spring application context loads successfully.
     *
     * This is a critical smoke test that ensures:
     * - All configuration classes are valid
     * - All beans can be instantiated
     * - No circular dependencies exist
     * - Application is properly configured
     *
     * Expected: Context loads without errors.
     */
    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
        assertTrue(applicationContext.getBeanDefinitionCount() > 0,
            "Application context should contain bean definitions");
    }

    /**
     * Test: Verify that the main OrderApplication class is loaded in context.
     *
     * Expected: OrderApplication bean exists in the application context.
     */
    @Test
    void testOrderApplicationBeanExists() {
        assertNotNull(applicationContext, "Application context should not be null");
        assertTrue(applicationContext.containsBean("orderApplication"),
            "OrderApplication bean should exist in context");
    }

    /**
     * Test: Verify that essential service beans are loaded.
     *
     * Checks that critical beans required for the application to function
     * are properly registered in the Spring context.
     *
     * Expected: Essential beans like OrderService are present.
     */
    @Test
    void testEssentialBeansAreLoaded() {
        assertNotNull(applicationContext, "Application context should not be null");

        // Verify service beans exist
        String[] serviceBeans = applicationContext.getBeanNamesForType(
            org.springframework.stereotype.Service.class);
        assertTrue(serviceBeans.length > 0,
            "At least one @Service bean should be present");

        // Verify repository beans exist
        String[] repositoryBeans = applicationContext.getBeanNamesForType(
            org.springframework.stereotype.Repository.class);
        assertTrue(repositoryBeans.length > 0,
            "At least one @Repository bean should be present");

        // Verify controller beans exist
        String[] controllerBeans = applicationContext.getBeanNamesForType(
            org.springframework.stereotype.Controller.class);
        assertTrue(controllerBeans.length > 0,
            "At least one @Controller or @RestController bean should be present");
    }

    /**
     * Test: Verify that the application properties are loaded correctly.
     *
     * Expected: Application context environment is properly configured.
     */
    @Test
    void testApplicationPropertiesLoaded() {
        assertNotNull(applicationContext.getEnvironment(),
            "Application environment should not be null");

        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertTrue(activeProfiles.length > 0,
            "At least one active profile should be set");
        assertEquals("test", activeProfiles[0],
            "Test profile should be active");
    }

    /**
     * Test: Verify that Feign clients are properly configured.
     *
     * Expected: Feign client beans exist in the context.
     */
    @Test
    void testFeignClientsAreConfigured() {
        assertNotNull(applicationContext, "Application context should not be null");

        // Check if any Feign client beans are present
        String[] feignBeans = applicationContext.getBeanDefinitionNames();
        long feignClientCount = java.util.Arrays.stream(feignBeans)
            .filter(name -> name.toLowerCase().contains("client"))
            .count();

        assertTrue(feignClientCount > 0,
            "At least one Feign client bean should be configured");
    }

    /**
     * Test: Verify main method exists and can be called.
     *
     * This is a simple sanity check that the main method exists
     * and doesn't throw exceptions when the class is loaded.
     *
     * Expected: No exceptions thrown.
     */
    @Test
    void testMainMethodExists() {
        assertDoesNotThrow(() -> {
            // Verify the main method exists and is accessible
            OrderApplication.class.getMethod("main", String[].class);
        }, "Main method should exist and be accessible");
    }
}