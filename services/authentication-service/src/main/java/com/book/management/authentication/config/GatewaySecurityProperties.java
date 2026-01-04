package com.book.management.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Gateway Security.
 * 
 * Used by the Feign request interceptor to inject the gateway secret
 * header into outgoing inter-service calls.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-04
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityProperties {

    /**
     * Enable/disable gateway security validation.
     */
    private boolean enabled = true;

    /**
     * Name of the header containing the gateway secret.
     */
    private String headerName = "X-Gateway-Secret";

    /**
     * Expected token value from the gateway.
     */
    private String expectedToken;

    /**
     * List of public paths that bypass gateway validation.
     */
    private List<String> publicPaths = new ArrayList<>();
}
