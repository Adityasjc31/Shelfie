package com.book.management.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Gateway Security.
 * Validates that incoming requests originate from the API Gateway.
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityProperties {

    private boolean enabled = true;
    private String headerName = "X-Gateway-Secret";
    private String expectedToken;
    private List<String> publicPaths = new ArrayList<>();
}
