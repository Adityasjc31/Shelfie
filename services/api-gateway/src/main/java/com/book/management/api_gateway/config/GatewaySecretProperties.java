package com.book.management.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Gateway Secret Token.
 * 
 * This secret is added as a header to all outgoing requests from the gateway
 * to backend microservices. Services validate this header to ensure requests
 * come only from the gateway, blocking direct access attempts.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-02
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.secret")
public class GatewaySecretProperties {

    /**
     * Enable/disable gateway secret injection.
     * Set to false during development to disable the security mechanism.
     */
    private boolean enabled = true;

    /**
     * Name of the header to add to requests.
     */
    private String headerName = "X-Gateway-Secret";

    /**
     * Secret token value.
     * Should match the expectedToken in backend services.
     */
    private String token;
}
