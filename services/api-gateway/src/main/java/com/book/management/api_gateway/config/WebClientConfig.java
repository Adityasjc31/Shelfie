package com.book.management.api_gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient Configuration for API Gateway.
 * 
 * Configures:
 * - Load balanced WebClient for inter-service communication
 * - Connection timeouts and pooling
 * - Read/Write timeouts
 * - HTTP client settings
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates a load-balanced WebClient builder for service-to-service calls.
     * 
     * Features:
     * - Eureka-based load balancing
     * - Connection pooling (max 500 connections)
     * - Connection timeout: 3 seconds
     * - Read timeout: 5 seconds
     * - Write timeout: 5 seconds
     * 
     * @return LoadBalanced WebClient.Builder
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        // Connection provider with pooling
        ConnectionProvider connectionProvider = ConnectionProvider.builder("gateway-pool")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(45))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        // HTTP client with timeouts
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    /**
     * Creates a standard WebClient builder (non-load balanced).
     * Used for external API calls.
     * 
     * @return WebClient.Builder
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
