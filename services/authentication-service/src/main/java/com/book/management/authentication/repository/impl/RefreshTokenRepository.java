package com.book.management.authentication.repository.impl;

import com.book.management.authentication.model.RefreshToken;
import com.book.management.authentication.repository.IRefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory Refresh Token Repository Implementation
 * 
 * Stores refresh tokens to enable:
 * - Token rotation (prevent reuse)
 * - Token revocation
 * - Security tracking
 * 
 * Production Alternative:
 * - Redis with 7-day TTL
 * - Database with indexed token column
 * 
 * Current Implementation:
 * - In-memory ConcurrentHashMap (thread-safe)
 * - Manual cleanup via scheduled task
 * - Token rotation detection
 * 
 * @author Aditya Srivastava
 * @version 1.0 - REST API Compliant
 */
@Repository
@Slf4j
public class RefreshTokenRepository implements IRefreshTokenRepository {

    // Thread-safe storage
    private final Map<String, RefreshToken> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userTokenIndex = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        tokenStore.put(refreshToken.getToken(), refreshToken);
        
        // Update user index
        userTokenIndex.computeIfAbsent(refreshToken.getUserId(), k -> new HashSet<>())
                .add(refreshToken.getToken());
        
        log.debug("Refresh token stored for user: {}", refreshToken.getUserId());
        
        return refreshToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        RefreshToken refreshToken = tokenStore.get(token);
        
        if (refreshToken != null && 
            refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Token expired, remove it
            deleteByToken(token);
            return Optional.empty();
        }
        
        return Optional.ofNullable(refreshToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RefreshToken> findByUserId(String userId) {
        Set<String> userTokens = userTokenIndex.getOrDefault(userId, new HashSet<>());
        
        return userTokens.stream()
                .map(tokenStore::get)
                .filter(Objects::nonNull)
                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByToken(String token) {
        RefreshToken refreshToken = tokenStore.get(token);
        
        if (refreshToken == null) {
            return false;
        }
        
        // Check if token is still valid
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            deleteByToken(token);
            return false;
        }
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByToken(String token) {
        RefreshToken refreshToken = tokenStore.remove(token);
        
        if (refreshToken != null) {
            Set<String> userTokens = userTokenIndex.get(refreshToken.getUserId());
            if (userTokens != null) {
                userTokens.remove(token);
                if (userTokens.isEmpty()) {
                    userTokenIndex.remove(refreshToken.getUserId());
                }
            }
            log.debug("Refresh token deleted for user: {}", refreshToken.getUserId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByUserId(String userId) {
        Set<String> userTokens = userTokenIndex.remove(userId);
        
        if (userTokens != null) {
            userTokens.forEach(tokenStore::remove);
            log.info("Deleted {} refresh tokens for user: {}", userTokens.size(), userId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        
        List<String> expiredTokens = tokenStore.values().stream()
                .filter(token -> token.getExpiresAt().isBefore(now))
                .map(RefreshToken::getToken)
                .collect(Collectors.toList());
        
        expiredTokens.forEach(this::deleteByToken);
        
        if (!expiredTokens.isEmpty()) {
            log.info("Cleaned up {} expired refresh tokens", expiredTokens.size());
        }
        
        return expiredTokens.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        // Remove expired tokens before counting
        deleteExpiredTokens();
        return tokenStore.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countByUserId(String userId) {
        return findByUserId(userId).size();
    }

    /**
     * Clears all refresh tokens (for testing).
     */
    public void clear() {
        tokenStore.clear();
        userTokenIndex.clear();
        log.debug("Cleared all refresh tokens");
    }

    /**
     * Gets all refresh tokens (for admin/monitoring).
     */
    public List<RefreshToken> findAll() {
        deleteExpiredTokens();
        return new ArrayList<>(tokenStore.values());
    }

    /**
     * Gets statistics for monitoring.
     */
    public Map<String, Object> getStatistics() {
        deleteExpiredTokens();
        
        long usedTokens = tokenStore.values().stream()
                .filter(RefreshToken::isUsed)
                .count();
        
        return Map.of(
            "totalTokens", tokenStore.size(),
            "usedTokens", usedTokens,
            "activeTokens", tokenStore.size() - usedTokens,
            "uniqueUsers", userTokenIndex.size(),
            "timestamp", LocalDateTime.now()
        );
    }
}
