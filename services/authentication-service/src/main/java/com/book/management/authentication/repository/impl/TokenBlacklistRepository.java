package com.book.management.authentication.repository.impl;

import com.book.management.authentication.model.BlacklistedToken;
import com.book.management.authentication.repository.ITokenBlacklistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory Token Blacklist Repository Implementation
 * 
 * Stores blacklisted (revoked) JWT tokens until they expire.
 * 
 * Production Alternative:
 * - Redis with TTL (automatic expiration)
 * - Database with indexed token column
 * 
 * Current Implementation:
 * - In-memory ConcurrentHashMap (thread-safe)
 * - Manual cleanup via scheduled task
 * - Fast O(1) lookup for validation
 * 
 * @author Aditya Srivastava
 * @version 1.0 - REST API Compliant
 */
@Repository
@Slf4j
public class TokenBlacklistRepository implements ITokenBlacklistRepository {

    // Thread-safe storage
    private final Map<String, BlacklistedToken> blacklistStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userTokenIndex = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public BlacklistedToken save(BlacklistedToken token) {
        blacklistStore.put(token.getToken(), token);
        
        // Update user index for quick lookup
        userTokenIndex.computeIfAbsent(token.getUserId(), k -> new HashSet<>())
                .add(token.getToken());
        
        log.debug("Token blacklisted: {} for user: {}", 
                token.getToken().substring(0, 20) + "...", token.getUserId());
        
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByToken(String token) {
        BlacklistedToken blacklistedToken = blacklistStore.get(token);
        
        if (blacklistedToken == null) {
            return false;
        }
        
        // Check if token is still within expiry
        if (blacklistedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Token expired, remove from blacklist
            delete(token);
            return false;
        }
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BlacklistedToken> findByToken(String token) {
        BlacklistedToken blacklistedToken = blacklistStore.get(token);
        
        if (blacklistedToken != null && 
            blacklistedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Token expired, remove from blacklist
            delete(token);
            return Optional.empty();
        }
        
        return Optional.ofNullable(blacklistedToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BlacklistedToken> findByUserId(String userId) {
        Set<String> userTokens = userTokenIndex.getOrDefault(userId, new HashSet<>());
        
        return userTokens.stream()
                .map(blacklistStore::get)
                .filter(Objects::nonNull)
                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        
        List<String> expiredTokens = blacklistStore.values().stream()
                .filter(token -> token.getExpiresAt().isBefore(now))
                .map(BlacklistedToken::getToken)
                .collect(Collectors.toList());
        
        expiredTokens.forEach(this::delete);
        
        if (!expiredTokens.isEmpty()) {
            log.info("Cleaned up {} expired blacklisted tokens", expiredTokens.size());
        }
        
        return expiredTokens.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByUserId(String userId) {
        Set<String> userTokens = userTokenIndex.remove(userId);
        
        if (userTokens != null) {
            userTokens.forEach(blacklistStore::remove);
            log.debug("Deleted {} blacklisted tokens for user: {}", userTokens.size(), userId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        // Remove expired tokens before counting
        deleteExpiredTokens();
        return blacklistStore.size();
    }

    /**
     * Deletes a token from blacklist.
     * 
     * @param token JWT token string
     */
    private void delete(String token) {
        BlacklistedToken blacklistedToken = blacklistStore.remove(token);
        
        if (blacklistedToken != null) {
            Set<String> userTokens = userTokenIndex.get(blacklistedToken.getUserId());
            if (userTokens != null) {
                userTokens.remove(token);
                if (userTokens.isEmpty()) {
                    userTokenIndex.remove(blacklistedToken.getUserId());
                }
            }
        }
    }

    /**
     * Clears all blacklisted tokens (for testing).
     */
    public void clear() {
        blacklistStore.clear();
        userTokenIndex.clear();
        log.debug("Cleared all blacklisted tokens");
    }

    /**
     * Gets statistics for monitoring.
     */
    public Map<String, Object> getStatistics() {
        return Map.of(
            "totalBlacklisted", blacklistStore.size(),
            "uniqueUsers", userTokenIndex.size(),
            "timestamp", LocalDateTime.now()
        );
    }
}
