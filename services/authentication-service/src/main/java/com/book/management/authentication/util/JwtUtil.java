package com.book.management.authentication.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility class for token generation and validation.
 * 
 * Handles:
 * - JWT token creation with claims
 * - Token validation and parsing
 * - Token expiration checking
 * - Extracting user information from tokens
 * 
 * @author Aditya Srivastava
 * @version 1.0
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.issuer:digital-bookstore}")
    private String issuer;

    /**
     * Generates JWT access token for authenticated user.
     * 
     * @param userId   user ID
     * @param email    user email
     * @param roles    user roles
     * @return JWT token
     */
    public String generateToken(String userId, String email, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("roles", roles);
        claims.put("tokenType", "ACCESS");
        
        return createToken(claims, email, expiration);
    }

    /**
     * Generates refresh token for token renewal.
     * 
     * @param userId user ID
     * @param email  user email
     * @return refresh token
     */
    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("tokenType", "REFRESH");
        
        return createToken(claims, email, refreshExpiration);
    }

    /**
     * Creates JWT token with specified claims and expiration.
     */
    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates JWT token.
     * 
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts username (email) from token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts user ID from token.
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extracts roles from token.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    /**
     * Extracts token type (ACCESS or REFRESH).
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    /**
     * Extracts expiration date from token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Gets signing key for JWT.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets token expiration time in milliseconds.
     */
    public Long getExpirationTime() {
        return expiration;
    }

    /**
     * Gets refresh token expiration time in milliseconds.
     */
    public Long getRefreshExpirationTime() {
        return refreshExpiration;
    }
}
