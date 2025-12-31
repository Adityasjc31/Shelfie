package com.book.management.authentication.scheduled;

import com.book.management.authentication.repository.ITokenBlacklistRepository;
import com.book.management.authentication.repository.IRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Token Cleanup Tasks
 * 
 * Handles automatic cleanup of:
 * - Expired blacklisted tokens
 * - Expired refresh tokens
 * 
 * NO SESSION CLEANUP (stateless architecture)
 * 
 * Schedules:
 * - Cleanup runs every 15 minutes
 * - Statistics logged every hour
 * 
 * @author Aditya Srivastava
 * @version 2.0 - REST API Compliant
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final ITokenBlacklistRepository tokenBlacklistRepository;
    private final IRefreshTokenRepository refreshTokenRepository;

    /**
     * Cleans up expired blacklisted tokens.
     * 
     * Runs every 15 minutes.
     * Removes tokens that have naturally expired (past JWT expiration).
     */
    @Scheduled(fixedDelay = 900000) // 15 minutes
    public void cleanupBlacklistedTokens() {
        log.info("Starting blacklisted token cleanup");
        
        try {
            int deletedCount = tokenBlacklistRepository.deleteExpiredTokens();
            long remainingCount = tokenBlacklistRepository.count();
            
            log.info("Blacklisted token cleanup completed. Deleted: {}, Remaining: {}", 
                    deletedCount, remainingCount);
            
        } catch (Exception e) {
            log.error("Error during blacklisted token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Cleans up expired refresh tokens.
     * 
     * Runs every 15 minutes.
     * Removes tokens that have expired (typically 7 days).
     */
    @Scheduled(fixedDelay = 900000) // 15 minutes
    public void cleanupRefreshTokens() {
        log.info("Starting refresh token cleanup");
        
        try {
            int deletedCount = refreshTokenRepository.deleteExpiredTokens();
            long remainingCount = refreshTokenRepository.count();
            
            log.info("Refresh token cleanup completed. Deleted: {}, Remaining: {}", 
                    deletedCount, remainingCount);
            
        } catch (Exception e) {
            log.error("Error during refresh token cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Logs token statistics for monitoring.
     * 
     * Runs every hour.
     * Provides insights into:
     * - Active blacklisted tokens
     * - Active refresh tokens
     * - Token usage patterns
     */
    @Scheduled(fixedDelay = 3600000) // 1 hour
    public void logTokenStatistics() {
        try {
            long blacklistedCount = tokenBlacklistRepository.count();
            long refreshTokenCount = refreshTokenRepository.count();
            
            log.info("═══════════════════════════════════════════════════════");
            log.info("Token Statistics:");
            log.info("  - Blacklisted Tokens: {}", blacklistedCount);
            log.info("  - Active Refresh Tokens: {}", refreshTokenCount);
            log.info("  - Total Tracked Tokens: {}", blacklistedCount + refreshTokenCount);
            log.info("═══════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            log.error("Error logging token statistics: {}", e.getMessage());
        }
    }

    /**
     * Performs comprehensive cleanup on startup.
     * 
     * Runs once when application starts.
     * Cleans up any stale data from previous runs.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE) // Run once on startup
    public void initialCleanup() {
        log.info("Performing initial token cleanup on startup");
        
        try {
            int blacklistedDeleted = tokenBlacklistRepository.deleteExpiredTokens();
            int refreshDeleted = refreshTokenRepository.deleteExpiredTokens();
            
            log.info("Initial cleanup completed. Blacklisted: {}, Refresh: {}", 
                    blacklistedDeleted, refreshDeleted);
            
        } catch (Exception e) {
            log.error("Error during initial cleanup: {}", e.getMessage());
        }
    }
}
