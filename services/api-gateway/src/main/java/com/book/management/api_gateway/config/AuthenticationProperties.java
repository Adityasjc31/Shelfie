package com.book.management.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties for authentication.
 * 
 * Provides a hook mechanism to enable/disable authentication during
 * development.
 * When disabled, mock user data is used instead of calling the authentication
 * service.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthenticationProperties {

    /**
     * Enable/disable authentication.
     * Set to false during development to bypass authentication.
     */
    private boolean enabled = true;

    /**
     * Authentication service configuration.
     */
    private Service service = new Service();

    /**
     * Mock user data when authentication is disabled.
     */
    private Mock mock = new Mock();

    @Data
    public static class Service {
        private String url = "lb://authentication-service";
        private String validateEndpoint = "/api/v1/auth/validate";
    }

    @Data
    public static class Mock {
        private String userId = "dev-user-001";
        private String username = "dev@example.com";
        private String roles = "USER,ADMIN";

        /**
         * Returns roles as a list.
         */
        public List<String> getRolesAsList() {
            return List.of(roles.split(","));
        }
    }
}
