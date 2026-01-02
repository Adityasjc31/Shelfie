package com.book.management.inventory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Gateway Security.
 * 
 * Validates that incoming requests originate from the API Gateway
 * by checking for a secret header. This prevents direct access to
 * the microservice, enforcing all traffic through the gateway.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-02
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityProperties {

    /**
     * Enable/disable gateway security validation.
     * Set to false during local testing if needed.
     */
    private boolean enabled = true;

    /**
     * Name of the header containing the gateway secret.
     */
    private String headerName = "X-Gateway-Secret";

    /**
     * Expected token value from the gateway.
     * Should match the token configured in api-gateway.
     */
    private String expectedToken;

    /**
     * List of public paths that bypass gateway validation.
     * Typically includes actuator endpoints for health checks.
     */
    private List<String> publicPaths = new ArrayList<>();
}
