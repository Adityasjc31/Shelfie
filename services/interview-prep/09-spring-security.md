# 🔐 Topic 9: Spring Security - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Spring Security, including authentication, authorization, JWT, OAuth2, and security best practices.

---

## Q1: What is Spring Security? What are its core features?

**Answer:**

**Spring Security** is a powerful and customizable authentication and access-control framework for Java applications. It's the de-facto standard for securing Spring-based applications.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING SECURITY FEATURES                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   AUTHENTICATION (Who are you?)                                          │
│   ─────────────────────────────                                          │
│   • Form-based login                                                    │
│   • HTTP Basic authentication                                           │
│   • JWT token authentication                                            │
│   • OAuth2 / OpenID Connect                                             │
│   • LDAP authentication                                                 │
│   • Remember-me authentication                                          │
│                                                                          │
│   AUTHORIZATION (What can you do?)                                       │
│   ────────────────────────────────                                       │
│   • URL-based authorization                                             │
│   • Method-level security (@PreAuthorize, @Secured)                     │
│   • Role-based access control (RBAC)                                    │
│   • Permission-based access control                                     │
│   • Domain object security (ACL)                                        │
│                                                                          │
│   PROTECTION AGAINST ATTACKS                                             │
│   ───────────────────────────                                            │
│   • CSRF (Cross-Site Request Forgery)                                   │
│   • Session Fixation                                                    │
│   • Clickjacking (X-Frame-Options)                                      │
│   • XSS (Cross-Site Scripting)                                          │
│   • SQL Injection (via prepared statements)                             │
│                                                                          │
│   INTEGRATION                                                            │
│   ───────────                                                            │
│   • Spring MVC and WebFlux                                              │
│   • Spring Boot auto-configuration                                      │
│   • OAuth2 clients and resource servers                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Basic Setup:

```xml
<!-- Spring Boot Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```java
// Default behavior with Spring Security:
// - All endpoints require authentication
// - Generates random password on startup
// - Provides /login and /logout endpoints
// - Username is "user"
```

---

## Q2: Explain the Spring Security Filter Chain.

**Answer:**

Spring Security works through a chain of servlet filters. Each filter performs a specific security function.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SECURITY FILTER CHAIN                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   HTTP Request                                                           │
│        │                                                                 │
│        ▼                                                                 │
│   ┌─────────────────────────────────────────────────────────────┐       │
│   │                  DelegatingFilterProxy                       │       │
│   │              (Entry point from Servlet Container)            │       │
│   └─────────────────────────┬───────────────────────────────────┘       │
│                             │                                            │
│                             ▼                                            │
│   ┌─────────────────────────────────────────────────────────────┐       │
│   │              FilterChainProxy                                │       │
│   │         (Manages Security Filter Chains)                    │       │
│   └─────────────────────────┬───────────────────────────────────┘       │
│                             │                                            │
│   ┌─────────────────────────▼───────────────────────────────────┐       │
│   │         SECURITY FILTER CHAIN                                │       │
│   │                                                               │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 1. SecurityContextPersistenceFilter                   │  │       │
│   │  │    Load/save SecurityContext between requests         │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 2. HeaderWriterFilter                                  │  │       │
│   │  │    Add security headers (X-Frame-Options, etc.)        │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 3. CsrfFilter                                          │  │       │
│   │  │    CSRF protection                                     │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 4. LogoutFilter                                        │  │       │
│   │  │    Handle logout requests                              │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 5. UsernamePasswordAuthenticationFilter               │  │       │
│   │  │    Process form login                                  │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 6. BasicAuthenticationFilter                          │  │       │
│   │  │    Process HTTP Basic auth                             │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 7. ExceptionTranslationFilter                         │  │       │
│   │  │    Handle security exceptions                          │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                           │                                  │       │
│   │  ┌───────────────────────────────────────────────────────┐  │       │
│   │  │ 8. FilterSecurityInterceptor                          │  │       │
│   │  │    Authorization decision (allow/deny)                 │  │       │
│   │  └───────────────────────────────────────────────────────┘  │       │
│   │                                                               │       │
│   └─────────────────────────┬───────────────────────────────────┘       │
│                             │                                            │
│                             ▼                                            │
│                    Your Controller                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Custom Filter Example:

```java
// Custom JWT Authentication Filter
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && jwtUtil.validateToken(token)) {
            Authentication auth = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);  // Continue chain
    }
}

// Register in Security Configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

---

## Q3: Explain Authentication vs Authorization.

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│              AUTHENTICATION vs AUTHORIZATION                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   AUTHENTICATION (AuthN)                                                 │
│   ──────────────────────                                                 │
│   "Who are you?"                                                        │
│                                                                          │
│   ┌─────────┐     Username: john@email.com      ┌─────────────┐         │
│   │  User   │ ──────────────────────────────▶   │   System    │         │
│   │         │     Password: ********            │             │         │
│   │         │ ◀──────────────────────────────   │  Verified!  │         │
│   │         │     ✓ Identity Confirmed          │             │         │
│   └─────────┘                                   └─────────────┘         │
│                                                                          │
│   Methods:                                                               │
│   • Username/Password                                                   │
│   • JWT tokens                                                          │
│   • OAuth2/Social login                                                 │
│   • Biometrics                                                          │
│   • API Keys                                                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   AUTHORIZATION (AuthZ)                                                  │
│   ─────────────────────                                                  │
│   "What can you do?"                                                    │
│                                                                          │
│   ┌─────────┐     Access /admin page?           ┌─────────────┐         │
│   │  User   │ ──────────────────────────────▶   │   System    │         │
│   │ (Authenticated)                             │             │         │
│   │         │ ◀──────────────────────────────   │  Check      │         │
│   │         │     Role: ADMIN required          │  Permissions│         │
│   │         │     Your role: USER               │             │         │
│   │         │     ✗ Access Denied               │             │         │
│   └─────────┘                                   └─────────────┘         │
│                                                                          │
│   Methods:                                                               │
│   • Role-based (RBAC): USER, ADMIN, MANAGER                             │
│   • Permission-based: READ, WRITE, DELETE                               │
│   • Attribute-based (ABAC): Time, location, resource attributes        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Spring Security Implementation:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // AUTHENTICATION - How to authenticate
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
            )
            
            // AUTHORIZATION - Who can access what
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()        // Anyone
                .requestMatchers("/user/**").hasRole("USER")      // USER role
                .requestMatchers("/admin/**").hasRole("ADMIN")    // ADMIN role
                .anyRequest().authenticated()                      // Must be logged in
            );
            
        return http.build();
    }
}
```

### Method-Level Authorization:

```java
@EnableMethodSecurity
@Configuration
public class MethodSecurityConfig { }

@Service
public class UserService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        // Only ADMIN can delete
    }
    
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
    public User getUser(Long userId) {
        // USER can only get their own data
    }
    
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        // Can only return if user owns the document
    }
    
    @Secured("ROLE_MANAGER")
    public void approveRequest() {
        // Simpler annotation
    }
}
```

---

## Q4: What is JWT? How does JWT authentication work?

**Answer:**

**JWT (JSON Web Token)** is a compact, URL-safe token format for securely transmitting claims between parties.

### JWT Structure:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       JWT STRUCTURE                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Header.Payload.Signature                                               │
│                                                                          │
│   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIn0.XbPfbIHMI6arZ3Y922BhjWgQzWX  │
│   │                      │                     │                         │
│   │                      │                     └── Signature             │
│   │                      │                                               │
│   │                      └── Payload (Claims)                            │
│   │                          {                                           │
│   │                            "sub": "john@email.com",                  │
│   │                            "roles": ["USER", "ADMIN"],               │
│   │                            "exp": 1704070800,                        │
│   │                            "iat": 1704067200                         │
│   │                          }                                           │
│   │                                                                      │
│   └── Header                                                             │
│       {                                                                  │
│         "alg": "HS256",                                                  │
│         "typ": "JWT"                                                     │
│       }                                                                  │
│                                                                          │
│   SIGNATURE = HMAC-SHA256(                                               │
│       base64UrlEncode(header) + "." + base64UrlEncode(payload),         │
│       secret_key                                                         │
│   )                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### JWT Authentication Flow:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    JWT AUTHENTICATION FLOW                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. LOGIN REQUEST                                                       │
│   ┌────────┐                                      ┌──────────────┐      │
│   │ Client │ ── POST /login ─────────────────────▶│ Auth Server  │      │
│   │        │    {email, password}                 │              │      │
│   │        │                                      │ 1. Validate  │      │
│   │        │ ◀─────────────────────────────────── │ 2. Generate  │      │
│   │        │    {accessToken, refreshToken}       │    JWT       │      │
│   └────────┘                                      └──────────────┘      │
│                                                                          │
│   2. AUTHENTICATED REQUEST                                               │
│   ┌────────┐                                      ┌──────────────┐      │
│   │ Client │ ── GET /api/data ───────────────────▶│ API Server   │      │
│   │        │    Authorization: Bearer <jwt>       │              │      │
│   │        │                                      │ 1. Extract   │      │
│   │        │                                      │    token     │      │
│   │        │                                      │ 2. Validate  │      │
│   │        │                                      │    signature │      │
│   │        │ ◀─────────────────────────────────── │ 3. Check     │      │
│   │        │    {data}                            │    expiration│      │
│   └────────┘                                      │ 4. Extract   │      │
│                                                   │    claims    │      │
│                                                   └──────────────┘      │
│                                                                          │
│   3. TOKEN REFRESH (When access token expires)                          │
│   ┌────────┐                                      ┌──────────────┐      │
│   │ Client │ ── POST /refresh ───────────────────▶│ Auth Server  │      │
│   │        │    {refreshToken}                    │              │      │
│   │        │ ◀─────────────────────────────────── │ New access   │      │
│   │        │    {newAccessToken}                  │ token        │      │
│   └────────┘                                      └──────────────┘      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### JWT Implementation:

```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long expiration;  // e.g., 3600000 (1 hour)
    
    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("userId", user.getId())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
```

### JWT vs Session-based:

| Aspect | JWT | Session |
|--------|-----|---------|
| **Storage** | Client (localStorage/cookie) | Server (memory/DB) |
| **Scalability** | Stateless, easy to scale | Need session replication |
| **Size** | Larger (contains claims) | Small (just session ID) |
| **Revocation** | Harder (blacklist needed) | Easy (delete session) |
| **Best for** | APIs, microservices | Traditional web apps |

---

## Q5: What is CSRF? How does Spring Security protect against it?

**Answer:**

**CSRF (Cross-Site Request Forgery)** is an attack that tricks authenticated users into performing unwanted actions.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       CSRF ATTACK                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. User logs into bank.com (has session cookie)                       │
│                                                                          │
│   ┌────────────┐        ┌────────────────────┐                          │
│   │   User     │ ──────▶│     bank.com       │                          │
│   │ (Browser)  │ ◀──────│  Session: abc123   │                          │
│   │            │        └────────────────────┘                          │
│   │ Cookie:    │                                                        │
│   │ session=abc│                                                        │
│   └────────────┘                                                        │
│                                                                          │
│   2. User visits evil.com (attacker's site)                             │
│                                                                          │
│   ┌────────────────────────────────────────────────────────────────┐    │
│   │                     evil.com                                    │    │
│   │                                                                 │    │
│   │   "You won a prize! Click here!"                                │    │
│   │                                                                 │    │
│   │   <img src="https://bank.com/transfer?to=attacker&amount=1000">│    │
│   │   OR                                                            │    │
│   │   <form action="https://bank.com/transfer" method="POST">      │    │
│   │     <input name="to" value="attacker" hidden>                  │    │
│   │     <input name="amount" value="1000" hidden>                  │    │
│   │   </form>                                                       │    │
│   │   <script>document.forms[0].submit();</script>                 │    │
│   │                                                                 │    │
│   └────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│   3. Browser sends request to bank.com WITH the session cookie!        │
│                                                                          │
│   ┌────────────┐   POST /transfer         ┌────────────────────┐        │
│   │   User     │ ─────────────────────────▶│     bank.com       │        │
│   │ (Browser)  │   Cookie: session=abc    │                    │        │
│   │            │   to=attacker            │ Request looks      │        │
│   │            │   amount=1000            │ legitimate!        │        │
│   └────────────┘                          │ Money transferred! │        │
│                                           └────────────────────┘        │
│                                                                          │
│   ⚠️ User didn't intend this! Attack successful!                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### CSRF Protection:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CSRF TOKEN PROTECTION                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. Server generates unique CSRF token per session                     │
│                                                                          │
│   ┌────────────┐                          ┌────────────────────┐        │
│   │   User     │ ◀─────────────────────── │     bank.com       │        │
│   │ (Browser)  │   CSRF Token: xyz789     │                    │        │
│   │            │   (in form hidden field) │                    │        │
│   └────────────┘                          └────────────────────┘        │
│                                                                          │
│   2. All state-changing requests must include CSRF token                │
│                                                                          │
│   <form action="/transfer" method="POST">                               │
│     <input type="hidden" name="_csrf" value="xyz789">                  │
│     <input name="to" value="friend">                                   │
│     <input name="amount" value="100">                                  │
│     <button>Transfer</button>                                          │
│   </form>                                                               │
│                                                                          │
│   3. evil.com cannot read the CSRF token (Same-Origin Policy)          │
│      Their forged request will be missing the token → REJECTED!        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Spring Security CSRF:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF enabled by default for form-based apps
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            );
            
        // For stateless APIs (JWT), CSRF is typically disabled
        // because there's no session cookie to steal
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}

// Thymeleaf automatically adds CSRF token
// <form th:action="@{/transfer}" method="post">
//     <!-- CSRF token automatically included -->
// </form>
```

### When to Disable CSRF:

| Scenario | CSRF |
|----------|------|
| Server-rendered forms | ✅ Enable |
| REST API with JWT | ❌ Disable (stateless) |
| REST API with cookies | ✅ Enable |
| Public read-only endpoints | ❌ Not needed |

---

## Q6: What is OAuth2? Explain the authorization grant types.

**Answer:**

**OAuth2** is an authorization framework that enables applications to obtain limited access to user accounts on third-party services.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       OAUTH2 PARTIES                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   RESOURCE OWNER: The user who owns the data                            │
│   CLIENT: Your application (wants to access user's data)                │
│   AUTHORIZATION SERVER: Issues tokens (e.g., Google, GitHub)            │
│   RESOURCE SERVER: Hosts protected resources (e.g., Google's API)       │
│                                                                          │
│   ┌──────────────┐      ┌──────────────┐      ┌─────────────────┐       │
│   │   Resource   │      │    Client    │      │  Authorization  │       │
│   │    Owner     │      │ (Your App)   │      │     Server      │       │
│   │   (User)     │      │              │      │  (Google OAuth) │       │
│   └──────────────┘      └──────────────┘      └─────────────────┘       │
│                                                                          │
│                                               ┌─────────────────┐       │
│                                               │   Resource      │       │
│                                               │    Server       │       │
│                                               │ (Google APIs)   │       │
│                                               └─────────────────┘       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Authorization Code Flow (Most Common):

```
┌─────────────────────────────────────────────────────────────────────────┐
│                AUTHORIZATION CODE FLOW                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   User        Your App               Auth Server          Resource      │
│    │             │                        │                Server       │
│    │                                      │                   │         │
│    │ 1. Click "Login with Google"         │                   │         │
│    │─────────▶│                           │                   │         │
│    │          │                           │                   │         │
│    │          │ 2. Redirect to Google     │                   │         │
│    │◀─────────│──────────────────────────▶│                   │         │
│    │          │                           │                   │         │
│    │ 3. User logs in and grants permission│                   │         │
│    │─────────────────────────────────────▶│                   │         │
│    │                                      │                   │         │
│    │          │ 4. Redirect with auth code│                   │         │
│    │◀─────────│◀──────────────────────────│                   │         │
│    │          │                           │                   │         │
│    │          │ 5. Exchange code for token│                   │         │
│    │          │──────────────────────────▶│                   │         │
│    │          │                           │                   │         │
│    │          │ 6. Access token returned  │                   │         │
│    │          │◀──────────────────────────│                   │         │
│    │          │                           │                   │         │
│    │          │ 7. Access API with token  │                   │         │
│    │          │───────────────────────────────────────────────▶│        │
│    │          │                           │                   │         │
│    │          │ 8. Protected data         │                   │         │
│    │◀─────────│◀──────────────────────────────────────────────│         │
│    │                                                                    │
└─────────────────────────────────────────────────────────────────────────┘
```

### Grant Types:

| Grant Type | Use Case | Flow |
|------------|----------|------|
| **Authorization Code** | Web apps with backend | Code → Token exchange |
| **Authorization Code + PKCE** | SPAs, mobile apps | Code + Verifier → Token |
| **Client Credentials** | Machine-to-machine | Client ID/Secret → Token |
| **Refresh Token** | Extend access | Refresh token → New access token |

### Spring Security OAuth2:

```java
// application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-client-id
            client-secret: your-client-secret
            scope: openid, profile, email
          github:
            client-id: your-github-id
            client-secret: your-github-secret

// Security Configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/dashboard")
            );
            
        return http.build();
    }
}
```

---

## Q7: How do you configure Spring Security for REST APIs?

**Answer:**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // Stateless session (no server-side session)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            // JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Exception handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\": \"Unauthorized\", \"message\": \"" + 
                        authException.getMessage() + "\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\": \"Forbidden\", \"message\": \"Access denied\"}"
                    );
                })
            )
            
            // Disable form login for APIs
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### JWT Authentication Filter:

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Extract token from header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(7);
        
        try {
            // 2. Validate token
            if (jwtUtil.validateToken(token)) {
                // 3. Extract user info
                String email = jwtUtil.extractEmail(token);
                
                // 4. Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                
                // 5. Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                // 6. Set in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

## Q8: What is CORS? How do you configure it in Spring Security?

**Answer:**

**CORS (Cross-Origin Resource Sharing)** is a mechanism that allows web applications to make requests to different domains.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       CORS EXPLAINED                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SAME-ORIGIN (Allowed by default):                                     │
│                                                                          │
│   Frontend: https://myapp.com  →  API: https://myapp.com/api ✅        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   CROSS-ORIGIN (Blocked by default):                                    │
│                                                                          │
│   Frontend: https://myapp.com  →  API: https://api.myapp.com ❌        │
│   Frontend: http://localhost:3000  →  API: http://localhost:8080 ❌    │
│                                                                          │
│   Browser blocks for security! Need CORS headers to allow.             │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   PREFLIGHT REQUEST (For complex requests):                              │
│                                                                          │
│   ┌────────┐                              ┌──────────┐                  │
│   │Browser │ ── OPTIONS /api/data ───────▶│  Server  │                  │
│   │        │    Origin: localhost:3000    │          │                  │
│   │        │                              │          │                  │
│   │        │ ◀────────────────────────────│          │                  │
│   │        │    Access-Control-Allow-Origin: *       │                  │
│   │        │    Access-Control-Allow-Methods: GET,POST                 │
│   │        │                              │          │                  │
│   │        │ ── Actual GET /api/data ─────▶│          │                  │
│   │        │ ◀─────── Response ───────────│          │                  │
│   └────────┘                              └──────────┘                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### CORS Configuration:

```java
// Method 1: In Security Configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            // ... other config
            ;
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allowed origins
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "https://myapp.com"
        ));
        // OR allow all
        // config.setAllowedOrigins(Arrays.asList("*"));
        
        // Allowed methods
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Allowed headers
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With"
        ));
        
        // Allow credentials (cookies, auth headers)
        config.setAllowCredentials(true);
        
        // How long preflight response can be cached
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

// Method 2: Controller-level
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    @CrossOrigin(origins = "*")  // Override at method level
    @GetMapping("/public")
    public String publicEndpoint() {
        return "accessible from anywhere";
    }
}

// Method 3: Global WebMvc Config
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

## Q9: What is method-level security?

**Answer:**

**Method-level security** allows you to secure individual methods using annotations.

```java
@Configuration
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize, @Secured
public class MethodSecurityConfig { }

@Service
public class DocumentService {
    
    // Only users with ADMIN role can execute
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllDocuments() {
        // Admin-only operation
    }
    
    // Only authenticated users
    @PreAuthorize("isAuthenticated()")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    // Check using method parameter
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public User getUserDetails(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }
    
    // Check return value after execution
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        return documentRepository.findById(id).orElseThrow();
    }
    
    // Filter collection based on authorization
    @PostFilter("filterObject.owner == authentication.name or hasRole('ADMIN')")
    public List<Document> getMyDocuments() {
        return documentRepository.findAll();  // Returns only owned docs
    }
    
    // Pre-filter incoming collection
    @PreFilter("filterObject.owner == authentication.name")
    public void processDocuments(List<Document> documents) {
        // Only processes user's own documents
    }
    
    // Simpler annotation (just role check)
    @Secured("ROLE_MANAGER")
    public void approveRequest(Long requestId) {
        // Manager-only
    }
    
    // JSR-250 annotation
    @RolesAllowed({"ADMIN", "MANAGER"})
    public void generateReport() {
        // Admin or Manager
    }
}
```

### SpEL Expressions:

| Expression | Description |
|------------|-------------|
| `hasRole('ADMIN')` | Has ROLE_ADMIN authority |
| `hasAnyRole('ADMIN', 'USER')` | Has any of the roles |
| `hasAuthority('DELETE_USER')` | Has specific authority |
| `isAuthenticated()` | User is authenticated |
| `isAnonymous()` | User is anonymous |
| `#userId == authentication.principal.id` | Parameter check |
| `returnObject.owner == authentication.name` | Return value check |
| `@myBean.checkAccess(#id)` | Call bean method |

---

## Q10: What are Spring Security best practices?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SECURITY BEST PRACTICES                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. PASSWORD HANDLING                                                   │
│   ────────────────────                                                   │
│   ✅ Always use BCryptPasswordEncoder                                    │
│   ✅ Never store plain text passwords                                    │
│   ✅ Use strong password policies                                        │
│                                                                          │
│   @Bean                                                                  │
│   public PasswordEncoder passwordEncoder() {                            │
│       return new BCryptPasswordEncoder(12);  // 12 rounds              │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. JWT SECURITY                                                        │
│   ───────────────                                                        │
│   ✅ Use strong secrets (256+ bits)                                      │
│   ✅ Short access token expiration (15-60 min)                          │
│   ✅ Implement refresh token rotation                                    │
│   ✅ Store secrets in environment variables                             │
│   ❌ Never log tokens                                                    │
│   ❌ Never put sensitive data in JWT payload                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. HTTPS EVERYWHERE                                                    │
│   ───────────────────                                                    │
│   ✅ Enforce HTTPS in production                                         │
│   ✅ Use HSTS headers                                                    │
│                                                                          │
│   http.requiresChannel(channel -> channel                               │
│       .anyRequest().requiresSecure()                                    │
│   );                                                                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   4. PROTECT AGAINST COMMON ATTACKS                                     │
│   ─────────────────────────────────                                      │
│   ✅ Enable CSRF for stateful apps                                       │
│   ✅ Set security headers (X-Frame-Options, X-XSS-Protection)           │
│   ✅ Validate and sanitize all inputs                                   │
│   ✅ Use parameterized queries (prevent SQL injection)                  │
│                                                                          │
│   http.headers(headers -> headers                                       │
│       .frameOptions(frame -> frame.deny())                              │
│       .xssProtection(xss -> xss.enable())                               │
│       .contentSecurityPolicy(csp -> csp                                 │
│           .policyDirectives("default-src 'self'"))                      │
│   );                                                                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   5. PRINCIPLE OF LEAST PRIVILEGE                                        │
│   ───────────────────────────────                                        │
│   ✅ Default deny, explicitly permit                                     │
│   ✅ Use specific roles/authorities                                      │
│   ✅ Implement method-level security                                     │
│                                                                          │
│   .authorizeHttpRequests(auth -> auth                                   │
│       .requestMatchers("/public/**").permitAll()                        │
│       .anyRequest().authenticated()  // Default: must authenticate      │
│   )                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   6. SECURE ERROR HANDLING                                               │
│   ────────────────────────                                               │
│   ✅ Return generic error messages                                       │
│   ❌ Never expose stack traces                                           │
│   ❌ Never reveal "user not found" vs "wrong password"                   │
│                                                                          │
│   // BAD                                                                 │
│   throw new Exception("User john@email.com not found");                 │
│                                                                          │
│   // GOOD                                                                │
│   throw new BadCredentialsException("Invalid credentials");             │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   7. LOGGING & MONITORING                                                │
│   ───────────────────────                                                │
│   ✅ Log authentication attempts                                         │
│   ✅ Log authorization failures                                          │
│   ✅ Implement rate limiting                                             │
│   ❌ Never log passwords or tokens                                       │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   8. KEEP DEPENDENCIES UPDATED                                           │
│   ────────────────────────────                                           │
│   ✅ Regularly update Spring Security                                    │
│   ✅ Monitor for CVEs                                                    │
│   ✅ Use dependency-check tools                                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q11: What is UserDetailsService? How do you implement it?

**Answer:**

**UserDetailsService** is a core interface for loading user-specific data during authentication.

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

### Custom Implementation:

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found: " + username
            ));
        
        // Convert to Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())  // Already encoded
            .authorities(getAuthorities(user.getRoles()))
            .accountExpired(false)
            .accountLocked(user.isLocked())
            .credentialsExpired(false)
            .disabled(!user.isEnabled())
            .build();
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
    }
}
```

### Register in Security Config:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
```

---

## Q12: Explain PasswordEncoder and different encoding strategies.

**Answer:**

**PasswordEncoder** is an interface for encoding passwords and verifying encoded passwords.

```java
public interface PasswordEncoder {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
```

### Available Encoders:

| Encoder | Description | Use Case |
|---------|-------------|----------|
| **BCryptPasswordEncoder** | Adaptive hash (recommended) | Most applications |
| **Argon2PasswordEncoder** | Memory-hard hash | High security needs |
| **SCryptPasswordEncoder** | Memory-hard hash | Alternative to Argon2 |
| **Pbkdf2PasswordEncoder** | PBKDF2 algorithm | FIPS compliance |
| **NoOpPasswordEncoder** | No encoding (plain text) | Testing only ❌ |

### BCrypt Example:

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Strength: 4-31 (default: 10)
        // Higher = more secure but slower
        return new BCryptPasswordEncoder(12);
    }
}

@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        return userRepository.save(user);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

### Delegating Password Encoder (Multiple Algorithms):

```java
@Bean
public PasswordEncoder passwordEncoder() {
    // Supports multiple encoding formats
    // Useful when migrating from one encoder to another
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
// Stores as: {bcrypt}$2a$10$... or {argon2}$argon2id$...
```

---

## Q13: What is SecurityContext and SecurityContextHolder?

**Answer:**

**SecurityContext** stores security information about the current request, primarily the authenticated user.

**SecurityContextHolder** provides access to the SecurityContext using a strategy pattern.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SECURITY CONTEXT HIERARCHY                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ┌──────────────────────────────────────────────────────────┐          │
│   │                  SecurityContextHolder                    │          │
│   │     (Static class to access SecurityContext)             │          │
│   └──────────────────────┬───────────────────────────────────┘          │
│                          │                                               │
│   ┌──────────────────────▼───────────────────────────────────┐          │
│   │                   SecurityContext                         │          │
│   │     (Holds Authentication object)                         │          │
│   └──────────────────────┬───────────────────────────────────┘          │
│                          │                                               │
│   ┌──────────────────────▼───────────────────────────────────┐          │
│   │                   Authentication                          │          │
│   │     Principal: UserDetails (who)                          │          │
│   │     Credentials: Password (proof)                         │          │
│   │     Authorities: Roles/Permissions (what can do)          │          │
│   └──────────────────────────────────────────────────────────┘          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Usage Example:

```java
// Get current authenticated user
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

if (auth != null && auth.isAuthenticated()) {
    String username = auth.getName();
    Object principal = auth.getPrincipal();  // Usually UserDetails
    Collection<?> authorities = auth.getAuthorities();
}

// In a Controller - inject directly
@GetMapping("/me")
public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    return userService.findByEmail(userDetails.getUsername());
}

// Or using SecurityContextHolder
@GetMapping("/profile")
public UserDto getProfile() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    return userService.findByEmail(email);
}
```

### Strategy Modes:

| Mode | Description |
|------|-------------|
| `MODE_THREADLOCAL` | Default. Context per thread |
| `MODE_INHERITABLETHREADLOCAL` | Child threads inherit context |
| `MODE_GLOBAL` | Static context (all threads share) |

---

## Q14: What is the difference between hasRole() and hasAuthority()?

**Answer:**

Both are used for authorization checks, but they handle the `ROLE_` prefix differently.

```java
// hasRole() automatically adds "ROLE_" prefix
@PreAuthorize("hasRole('ADMIN')")      // Checks for ROLE_ADMIN authority
@PreAuthorize("hasRole('USER')")       // Checks for ROLE_USER authority

// hasAuthority() checks the exact authority string
@PreAuthorize("hasAuthority('ROLE_ADMIN')")   // Checks for ROLE_ADMIN
@PreAuthorize("hasAuthority('DELETE_USER')")  // Checks for DELETE_USER
```

### Comparison:

| Aspect | hasRole() | hasAuthority() |
|--------|-----------|----------------|
| **Prefix** | Auto-adds "ROLE_" | No prefix modification |
| **Use for** | Role-based access | Fine-grained permissions |
| **Example authority** | ROLE_ADMIN, ROLE_USER | DELETE_USER, READ_REPORTS |

### In Security Configuration:

```java
http.authorizeHttpRequests(auth -> auth
    // These are equivalent
    .requestMatchers("/admin/**").hasRole("ADMIN")           // Checks ROLE_ADMIN
    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Checks ROLE_ADMIN
    
    // For permissions (no ROLE_ prefix)
    .requestMatchers("/reports/**").hasAuthority("VIEW_REPORTS")
    .requestMatchers("/users/delete/**").hasAuthority("DELETE_USER")
);
```

### Best Practice:

```java
// Use roles for broad access control
public enum Role {
    USER, ADMIN, MANAGER
}

// Use authorities/permissions for fine-grained control
public enum Permission {
    READ_USER, WRITE_USER, DELETE_USER,
    READ_REPORT, GENERATE_REPORT
}

// In UserDetails
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    
    // Add role
    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
    
    // Add permissions
    role.getPermissions().forEach(permission ->
        authorities.add(new SimpleGrantedAuthority(permission.name()))
    );
    
    return authorities;
}
```

---

## Q15: How do you handle session management in Spring Security?

**Answer:**

Spring Security provides comprehensive session management capabilities.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                // Session creation policy
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                
                // Maximum sessions per user
                .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)  // Prevent new login
                    // OR
                    .expiredUrl("/session-expired")  // Redirect if expired
                    .sessionRegistry(sessionRegistry())
            )
            
            // Session fixation protection
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()  // Create new session, copy attributes
            )
            
            // Invalid session handling
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?invalid")
            );
            
        return http.build();
    }
    
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
```

### Session Creation Policies:

| Policy | Description |
|--------|-------------|
| `ALWAYS` | Always create session |
| `IF_REQUIRED` | Create if needed (default) |
| `NEVER` | Never create, but use if exists |
| `STATELESS` | Never create or use (for REST APIs) |

### Session Fixation Protection:

```java
// Available strategies:
.sessionFixation().migrateSession()    // New session, copy attributes (default)
.sessionFixation().newSession()         // New session, no copy
.sessionFixation().changeSessionId()    // Change ID only (Servlet 3.1+)
.sessionFixation().none()               // No protection (not recommended)
```

### Concurrent Session Control:

```java
// Allow only 1 session per user, kick out old session
http.sessionManagement(session -> session
    .maximumSessions(1)
);

// Allow only 1 session, prevent new login if already logged in
http.sessionManagement(session -> session
    .maximumSessions(1)
    .maxSessionsPreventsLogin(true)
);

// Get active sessions for a user
@Autowired
private SessionRegistry sessionRegistry;

public List<SessionInformation> getActiveSessions(String username) {
    List<Object> principals = sessionRegistry.getAllPrincipals();
    return principals.stream()
        .filter(p -> ((UserDetails)p).getUsername().equals(username))
        .flatMap(p -> sessionRegistry.getAllSessions(p, false).stream())
        .collect(Collectors.toList());
}
```

---

## Q16: What is @EnableWebSecurity and @EnableMethodSecurity?

**Answer:**

These annotations enable Spring Security features in your application.

### @EnableWebSecurity:

```java
@Configuration
@EnableWebSecurity  // Enables Spring Security's web security support
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure HTTP security
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
            
        return http.build();
    }
}
```

**What it does:**
- Creates the Spring Security filter chain
- Enables `HttpSecurity` configuration
- Registers the `SecurityFilterChain` bean
- Provides default security configuration

### @EnableMethodSecurity:

```java
@Configuration
@EnableMethodSecurity(
    prePostEnabled = true,   // Enable @PreAuthorize, @PostAuthorize (default: true)
    securedEnabled = true,   // Enable @Secured
    jsr250Enabled = true     // Enable @RolesAllowed (JSR-250)
)
public class MethodSecurityConfig { }

@Service
public class UserService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) { }
    
    @Secured("ROLE_MANAGER")
    public void approveRequest() { }
    
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Report generateReport() { }
}
```

### Comparison:

| Annotation | Scope | Purpose |
|------------|-------|---------|
| `@EnableWebSecurity` | HTTP requests | URL-based security |
| `@EnableMethodSecurity` | Methods | Method-level security |

---

## Q17: How does Remember-Me authentication work?

**Answer:**

**Remember-Me** allows users to stay logged in across browser sessions using a persistent token.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    REMEMBER-ME FLOW                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. INITIAL LOGIN                                                       │
│   ┌────────┐     Login + Remember-Me checkbox    ┌──────────┐           │
│   │ Client │ ───────────────────────────────────▶│  Server  │           │
│   │        │                                     │          │           │
│   │        │ ◀───────────────────────────────────│          │           │
│   │        │   Set-Cookie: remember-me=token     │          │           │
│   └────────┘                                     └──────────┘           │
│                                                                          │
│   2. SUBSEQUENT REQUEST (Session expired)                                │
│   ┌────────┐     Cookie: remember-me=token       ┌──────────┐           │
│   │ Client │ ───────────────────────────────────▶│  Server  │           │
│   │        │                                     │ Validate │           │
│   │        │ ◀───────────────────────────────────│  token   │           │
│   │        │   Auto-login successful!            │          │           │
│   └────────┘                                     └──────────┘           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(Customizer.withDefaults())
            
            // Simple hash-based remember-me (token in cookie)
            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")      // For token hashing
                .tokenValiditySeconds(604800)   // 7 days
                .rememberMeParameter("remember") // Form parameter name
            );
            
        return http.build();
    }
}
```

### Persistent Token (More Secure):

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .rememberMe(remember -> remember
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(604800)
                .userDetailsService(userDetailsService)
            );
            
        return http.build();
    }
    
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
}

// Required table
CREATE TABLE persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);
```

### Security Considerations:

| Approach | Security | Recommendation |
|----------|----------|----------------|
| Hash-based | Lower | OK for low-risk apps |
| Persistent token | Higher | Recommended |
| None | Highest | Sensitive applications |

---

## Q18: What is the Authentication object in Spring Security?

**Answer:**

**Authentication** represents the token for an authentication request or an authenticated principal.

```java
public interface Authentication extends Principal, Serializable {
    
    // The authorities granted to the principal
    Collection<? extends GrantedAuthority> getAuthorities();
    
    // The credentials (usually password)
    Object getCredentials();
    
    // Additional details (IP address, certificate, etc.)
    Object getDetails();
    
    // The principal being authenticated (usually UserDetails)
    Object getPrincipal();
    
    // Whether the token has been authenticated
    boolean isAuthenticated();
    
    // Set authentication status
    void setAuthenticated(boolean isAuthenticated);
}
```

### Common Implementations:

| Implementation | Use Case |
|----------------|----------|
| `UsernamePasswordAuthenticationToken` | Form login, Basic auth |
| `JwtAuthenticationToken` | JWT authentication |
| `OAuth2AuthenticationToken` | OAuth2 login |
| `RememberMeAuthenticationToken` | Remember-me |
| `AnonymousAuthenticationToken` | Anonymous users |

### Creating Authentication:

```java
// Before authentication (request token)
UsernamePasswordAuthenticationToken authRequest = 
    new UsernamePasswordAuthenticationToken(
        username,           // Principal (who)
        password            // Credentials (proof)
    );

// After authentication (authenticated token)
UsernamePasswordAuthenticationToken authResult = 
    new UsernamePasswordAuthenticationToken(
        userDetails,                    // Principal
        null,                           // Credentials cleared
        userDetails.getAuthorities()    // Granted authorities
    );

// Set in context
SecurityContextHolder.getContext().setAuthentication(authResult);
```

### Accessing in Controller:

```java
@RestController
public class UserController {
    
    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", authentication.getName());
        info.put("authorities", authentication.getAuthorities());
        info.put("isAuthenticated", authentication.isAuthenticated());
        return info;
    }
    
    // Or get the principal directly
    @GetMapping("/user/details")
    public UserDetails getDetails(@AuthenticationPrincipal UserDetails user) {
        return user;
    }
}
```

---

## Q19: How do you implement custom authentication in Spring Security?

**Answer:**

Custom authentication involves creating a custom `AuthenticationProvider`.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                AUTHENTICATION FLOW                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Filter ─▶ AuthenticationManager ─▶ AuthenticationProvider ─▶ Success  │
│                                              │                           │
│                                              ▼                           │
│                                      UserDetailsService                 │
│                                       + PasswordEncoder                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Custom AuthenticationProvider:

```java
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;  // Custom OTP validation
    
    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // Load user
        UserDetails user = userDetailsService.loadUserByUsername(username);
        
        // Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        
        // Custom validation (e.g., OTP)
        CustomAuthToken customAuth = (CustomAuthToken) authentication;
        if (!otpService.validate(username, customAuth.getOtp())) {
            throw new BadCredentialsException("Invalid OTP");
        }
        
        // Check if account is active
        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        
        // Return authenticated token
        return new UsernamePasswordAuthenticationToken(
            user, 
            null, 
            user.getAuthorities()
        );
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        // Which Authentication types this provider handles
        return CustomAuthToken.class.isAssignableFrom(authentication);
    }
}

// Custom Authentication Token
public class CustomAuthToken extends UsernamePasswordAuthenticationToken {
    
    private final String otp;
    
    public CustomAuthToken(String username, String password, String otp) {
        super(username, password);
        this.otp = otp;
    }
    
    public String getOtp() {
        return otp;
    }
}
```

### Register Provider:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public AuthenticationManager authenticationManager(
            CustomAuthenticationProvider customProvider) {
        
        return new ProviderManager(List.of(customProvider));
    }
}
```

---

## Q20: What is Spring Security's DelegatingFilterProxy?

**Answer:**

**DelegatingFilterProxy** is a Servlet Filter that bridges the Servlet container and Spring's ApplicationContext.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                FILTER CHAIN ARCHITECTURE                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SERVLET CONTAINER                         SPRING CONTEXT              │
│   ─────────────────                         ──────────────              │
│                                                                          │
│   ┌─────────────────┐                                                   │
│   │  Servlet Filter │                                                   │
│   │    Chain        │                                                   │
│   └────────┬────────┘                                                   │
│            │                                                             │
│   ┌────────▼────────┐                       ┌──────────────────┐        │
│   │DelegatingFilter │──── Delegates to ────▶│ FilterChainProxy │        │
│   │     Proxy       │                       │  (Spring Bean)   │        │
│   └─────────────────┘                       └────────┬─────────┘        │
│                                                      │                   │
│                                             ┌────────▼─────────┐        │
│                                             │SecurityFilterChain│        │
│                                             │   • AuthFilter    │        │
│                                             │   • CsrfFilter    │        │
│                                             │   • ...           │        │
│                                             └──────────────────┘        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Points:

```java
// DelegatingFilterProxy is registered automatically by Spring Boot
// It looks up a bean named "springSecurityFilterChain"

// In non-Spring Boot apps, manually register:
public class SecurityInitializer 
        extends AbstractSecurityWebApplicationInitializer {
    // This registers DelegatingFilterProxy with name "springSecurityFilterChain"
}

// FilterChainProxy manages multiple SecurityFilterChains
@Bean
public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    return http.build();
}

@Bean
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}
```

### Why DelegatingFilterProxy?

| Problem | Solution |
|---------|----------|
| Servlet filters created by container | Delegate to Spring-managed beans |
| Need dependency injection in filters | Spring beans have full DI |
| Need Spring context access | Access ApplicationContext |

---

## Q21: How do you secure a reactive WebFlux application?

**Answer:**

Spring Security for WebFlux uses reactive programming patterns.

```java
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebFluxSecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/public/**").permitAll()
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .anyExchange().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form
                .loginPage("/login")
            )
            .build();
    }
    
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();
            
        return new MapReactiveUserDetailsService(user);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Reactive vs Servlet Comparison:

| Servlet (WebMVC) | Reactive (WebFlux) |
|------------------|-------------------|
| `@EnableWebSecurity` | `@EnableWebFluxSecurity` |
| `SecurityFilterChain` | `SecurityWebFilterChain` |
| `HttpSecurity` | `ServerHttpSecurity` |
| `UserDetailsService` | `ReactiveUserDetailsService` |
| `@EnableMethodSecurity` | `@EnableReactiveMethodSecurity` |
| `.authorizeHttpRequests()` | `.authorizeExchange()` |
| `SecurityContextHolder` | `ReactiveSecurityContextHolder` |

### Accessing Security Context in WebFlux:

```java
@RestController
public class UserController {
    
    @GetMapping("/me")
    public Mono<String> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName);
    }
    
    // Or inject directly
    @GetMapping("/details")
    public Mono<UserDetails> getDetails(
            @AuthenticationPrincipal Mono<UserDetails> user) {
        return user;
    }
}
```

---

## Q22: What are Security Headers and how does Spring Security handle them?

**Answer:**

Security headers protect against common web vulnerabilities.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // Prevent clickjacking
                .frameOptions(frame -> frame.deny())
                
                // XSS protection
                .xssProtection(xss -> xss.enable())
                
                // Prevent MIME sniffing
                .contentTypeOptions(Customizer.withDefaults())
                
                // Content Security Policy
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self'")
                )
                
                // HTTP Strict Transport Security
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
                
                // Referrer Policy
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                
                // Permissions Policy
                .permissionsPolicy(permissions -> permissions
                    .policy("camera=(), microphone=(), geolocation=()")
                )
            );
            
        return http.build();
    }
}
```

### Common Security Headers:

| Header | Purpose | Spring Security |
|--------|---------|-----------------|
| `X-Frame-Options` | Prevent clickjacking | Enabled by default |
| `X-Content-Type-Options` | Prevent MIME sniffing | Enabled by default |
| `X-XSS-Protection` | Enable browser XSS filter | Enabled by default |
| `Strict-Transport-Security` | Force HTTPS | Disabled by default |
| `Content-Security-Policy` | Control resource loading | Manual config |
| `Referrer-Policy` | Control referrer info | Manual config |

---

## Q23: How do you implement rate limiting in Spring Security?

**Answer:**

Rate limiting protects against brute force attacks and abuse.

### Using Bucket4j:

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
</dependency>
```

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, this::createBucket);
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Try again later.");
        }
    }
    
    private Bucket createBucket(String key) {
        // 10 requests per minute
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}

// Register in Security Config
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

### Login-Specific Rate Limiting:

```java
@Component
public class LoginAttemptService {
    
    private final LoadingCache<String, Integer> attemptsCache;
    private static final int MAX_ATTEMPTS = 5;
    
    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });
    }
    
    public void loginSucceeded(String ip) {
        attemptsCache.invalidate(ip);
    }
    
    public void loginFailed(String ip) {
        int attempts = attemptsCache.getUnchecked(ip);
        attemptsCache.put(ip, attempts + 1);
    }
    
    public boolean isBlocked(String ip) {
        return attemptsCache.getUnchecked(ip) >= MAX_ATTEMPTS;
    }
}
```

---

## Q24: What is the difference between @PreAuthorize and @PostAuthorize?

**Answer:**

Both are used for method-level security but execute at different times.

```java
@Service
public class DocumentService {
    
    // @PreAuthorize - Check BEFORE method execution
    // Method doesn't run if check fails
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
    
    // @PreAuthorize with parameter
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }
    
    // @PostAuthorize - Check AFTER method execution
    // Method runs, then check is applied to return value
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        return documentRepository.findById(id).orElseThrow();
        // If owner doesn't match, AccessDeniedException is thrown
    }
    
    // Combined example
    @PreAuthorize("hasRole('USER')")  // Must be USER to call
    @PostAuthorize("returnObject.viewable == true")  // Result must be viewable
    public Report getReport(Long id) {
        return reportRepository.findById(id).orElseThrow();
    }
}
```

### Comparison:

| Aspect | @PreAuthorize | @PostAuthorize |
|--------|---------------|----------------|
| **When** | Before method | After method |
| **Access to** | Method parameters | Return value |
| **Use case** | Input validation | Output filtering |
| **Performance** | Better (fails fast) | Method always runs |
| **SpEL variable** | `#paramName` | `returnObject` |

### @PreFilter and @PostFilter:

```java
@Service
public class DocumentService {
    
    // Filter input collection
    @PreFilter("filterObject.owner == authentication.name")
    public void processDocuments(List<Document> documents) {
        // Only processes user's own documents
    }
    
    // Filter output collection
    @PostFilter("filterObject.owner == authentication.name or hasRole('ADMIN')")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
        // Non-admin users only see their own documents
    }
}
```

---

## Q25: How do you implement API Key authentication?

**Answer:**

API Key authentication is useful for service-to-service communication.

### Custom API Key Filter:

```java
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    @Value("${api.key.header:X-API-KEY}")
    private String apiKeyHeader;
    
    private final ApiKeyService apiKeyService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String apiKey = request.getHeader(apiKeyHeader);
        
        if (apiKey != null) {
            Optional<ApiKeyDetails> keyDetails = apiKeyService.validateKey(apiKey);
            
            if (keyDetails.isPresent()) {
                ApiKeyAuthenticationToken auth = new ApiKeyAuthenticationToken(
                    keyDetails.get(),
                    keyDetails.get().getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip for certain paths
        return request.getRequestURI().startsWith("/public/");
    }
}

// Custom Authentication Token
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    
    private final ApiKeyDetails principal;
    
    public ApiKeyAuthenticationToken(ApiKeyDetails principal,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }
    
    @Override
    public Object getCredentials() {
        return null;
    }
    
    @Override
    public Object getPrincipal() {
        return principal;
    }
}
```

### API Key Service:

```java
@Service
@RequiredArgsConstructor
public class ApiKeyService {
    
    private final ApiKeyRepository apiKeyRepository;
    
    public Optional<ApiKeyDetails> validateKey(String apiKey) {
        String hashedKey = hashKey(apiKey);
        return apiKeyRepository.findByHashedKey(hashedKey)
            .filter(key -> !key.isExpired())
            .filter(key -> key.isActive());
    }
    
    public String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private String hashKey(String key) {
        return Hashing.sha256().hashString(key, StandardCharsets.UTF_8).toString();
    }
}
```

---

## Q26: What is the AuthenticationManager and ProviderManager?

**Answer:**

**AuthenticationManager** is the main interface for authentication. **ProviderManager** is its default implementation.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                AUTHENTICATION MANAGER ARCHITECTURE                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   AuthenticationManager (Interface)                                      │
│   ─────────────────────────────────                                      │
│   └── Authentication authenticate(Authentication)                        │
│                                                                          │
│                    │                                                     │
│                    ▼                                                     │
│                                                                          │
│   ProviderManager (Implementation)                                       │
│   ─────────────────────────────────                                      │
│   └── List<AuthenticationProvider> providers                             │
│       │                                                                  │
│       ├── DaoAuthenticationProvider (username/password)                  │
│       ├── JwtAuthenticationProvider (JWT tokens)                         │
│       ├── OAuth2AuthenticationProvider (OAuth2)                          │
│       └── CustomAuthenticationProvider (your custom)                     │
│                                                                          │
│   Flow: Tries each provider until one succeeds or all fail              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        // Default: uses all AuthenticationProvider beans
        return config.getAuthenticationManager();
    }
    
    // Or customize explicitly
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder);
        
        JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider();
        
        return new ProviderManager(List.of(daoProvider, jwtProvider));
    }
}
```

### Using AuthenticationManager:

```java
@RestController
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user);
            
            return ResponseEntity.ok(new TokenResponse(token));
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
```

---

## Q27: How do you handle security exceptions in Spring Security?

**Answer:**

Spring Security provides comprehensive exception handling.

### Exception Types:

| Exception | HTTP Status | Cause |
|-----------|-------------|-------|
| `AuthenticationException` | 401 | Failed authentication |
| `AccessDeniedException` | 403 | Authorization failure |
| `BadCredentialsException` | 401 | Wrong credentials |
| `UsernameNotFoundException` | 401 | User not found |
| `AccountExpiredException` | 401 | Account expired |
| `LockedException` | 401 | Account locked |
| `DisabledException` | 401 | Account disabled |

### Custom Exception Handling:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .exceptionHandling(ex -> ex
                // For unauthenticated users
                .authenticationEntryPoint(customAuthEntryPoint())
                // For authenticated but unauthorized users
                .accessDeniedHandler(customAccessDeniedHandler())
            );
            
        return http.build();
    }
    
    @Bean
    public AuthenticationEntryPoint customAuthEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", 401);
            error.put("error", "Unauthorized");
            error.put("message", authException.getMessage());
            error.put("path", request.getRequestURI());
            error.put("timestamp", new Date());
            
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        };
    }
    
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", 403);
            error.put("error", "Forbidden");
            error.put("message", "Access denied");
            error.put("path", request.getRequestURI());
            
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        };
    }
}
```

### Global Exception Handler:

```java
@RestControllerAdvice
public class SecurityExceptionHandler {
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException ex) {
        return ResponseEntity.status(401)
            .body(new ErrorResponse("Authentication failed: " + ex.getMessage()));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403)
            .body(new ErrorResponse("Access denied: " + ex.getMessage()));
    }
}
```

---

## Q28: What is SecurityContextHolderStrategy?

**Answer:**

**SecurityContextHolderStrategy** determines how `SecurityContextHolder` stores the `SecurityContext`.

```java
public interface SecurityContextHolderStrategy {
    void clearContext();
    SecurityContext getContext();
    void setContext(SecurityContext context);
    SecurityContext createEmptyContext();
}
```

### Available Strategies:

| Strategy | Class | Use Case |
|----------|-------|----------|
| **ThreadLocal** | `ThreadLocalSecurityContextHolderStrategy` | Default, single thread per request |
| **InheritableThreadLocal** | `InheritableThreadLocalSecurityContextHolderStrategy` | Spawned threads need context |
| **Global** | `GlobalSecurityContextHolderStrategy` | Single-user, embedded apps |

### Configuring Strategy:

```java
// Option 1: System property (at startup)
System.setProperty("spring.security.strategy", "MODE_INHERITABLETHREADLOCAL");

// Option 2: Programmatic (before any security operations)
@PostConstruct
public void init() {
    SecurityContextHolder.setStrategyName(
        SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
    );
}
```

### Async Context Propagation:

```java
// Problem: Async methods lose security context
@Async
public CompletableFuture<String> asyncMethod() {
    // SecurityContextHolder.getContext() returns empty!
}

// Solution: Use DelegatingSecurityContextExecutor
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.initialize();
        
        // Wrap to propagate security context
        return new DelegatingSecurityContextExecutor(executor);
    }
}
```

---

## Q29: How do you implement OAuth2 Resource Server in Spring Security?

**Answer:**

A Resource Server protects resources using OAuth2 access tokens.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### Configuration for JWT:

```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com
          # OR specify JWK Set URI directly
          jwk-set-uri: https://auth.example.com/.well-known/jwks.json
```

```java
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/api/**").authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthConverter())
                )
            );
            
        return http.build();
    }
    
    // Custom JWT converter to extract roles
    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthConverter = 
            new JwtGrantedAuthoritiesConverter();
        grantedAuthConverter.setAuthorityPrefix("ROLE_");
        grantedAuthConverter.setAuthoritiesClaimName("roles");
        
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthConverter);
        return converter;
    }
}
```

### Opaque Token (Introspection):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        opaque-token:
          introspection-uri: https://auth.example.com/introspect
          client-id: resource-server
          client-secret: secret
```

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .oauth2ResourceServer(oauth2 -> oauth2
            .opaqueToken(Customizer.withDefaults())
        );
    return http.build();
}
```

---

## Q30: What are Access Control Lists (ACLs) in Spring Security?

**Answer:**

**ACLs** provide domain object-level security, controlling access to individual objects.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ACL ARCHITECTURE                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Domain Object: Document (id=1)                                         │
│   ─────────────────────────────                                          │
│                                                                          │
│   ┌───────────────────────────────────────────────────────┐              │
│   │                     Access Control List                │              │
│   ├───────────────────────────────────────────────────────┤              │
│   │  Owner: alice                                          │              │
│   │                                                        │              │
│   │  ┌─────────────────────────────────────────────────┐  │              │
│   │  │              Access Control Entries             │  │              │
│   │  ├─────────────┬────────────┬──────────────────────┤  │              │
│   │  │ Principal   │ Permission │ Granted              │  │              │
│   │  ├─────────────┼────────────┼──────────────────────┤  │              │
│   │  │ alice       │ READ       │ ✅                   │  │              │
│   │  │ alice       │ WRITE      │ ✅                   │  │              │
│   │  │ alice       │ DELETE     │ ✅                   │  │              │
│   │  │ bob         │ READ       │ ✅                   │  │              │
│   │  │ bob         │ WRITE      │ ❌                   │  │              │
│   │  │ ROLE_ADMIN  │ ALL        │ ✅                   │  │              │
│   │  └─────────────┴────────────┴──────────────────────┘  │              │
│   └───────────────────────────────────────────────────────┘              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Setup:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-acl</artifactId>
</dependency>
```

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AclConfig {
    
    @Bean
    public AclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }
    
    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(
            dataSource,
            aclCache(),
            aclAuthorizationStrategy(),
            permissionGrantingStrategy()
        );
    }
}
```

### Using ACLs:

```java
@Service
public class DocumentService {
    
    @Autowired
    private MutableAclService aclService;
    
    // Grant permission on object
    public void grantPermission(Document doc, String username, Permission permission) {
        ObjectIdentity oid = new ObjectIdentityImpl(Document.class, doc.getId());
        MutableAcl acl;
        
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException e) {
            acl = aclService.createAcl(oid);
        }
        
        Sid sid = new PrincipalSid(username);
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        aclService.updateAcl(acl);
    }
    
    // Check permission using annotation
    @PreAuthorize("hasPermission(#document, 'WRITE')")
    public void updateDocument(Document document) {
        // Only users with WRITE permission can update
    }
    
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<Document> getAllDocuments() {
        // Returns only documents user can read
        return documentRepository.findAll();
    }
}
```

---

## Q31: What is the difference between stateful and stateless authentication?

**Answer:**

| Aspect | Stateful (Session) | Stateless (JWT) |
|--------|-------------------|-----------------|
| **Storage** | Server stores session | Client stores token |
| **Scalability** | Requires session replication | Easy horizontal scaling |
| **Memory** | Server memory per user | No server memory |
| **Logout** | Immediate (delete session) | Harder (token still valid) |
| **Best for** | Traditional web apps | APIs, microservices |

```java
// Stateless configuration
http.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
);
```

---

## Q32: How do you implement Two-Factor Authentication (2FA)?

**Answer:**

```java
@Service
public class TwoFactorAuthService {
    
    public String generateSecretKey() {
        return new GoogleAuthenticator().createCredentials().getKey();
    }
    
    public boolean verifyCode(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, code);
    }
}

@RestController
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Step 1: Validate username/password
        // Step 2: If 2FA enabled, return partial token requiring OTP
        // Step 3: After OTP validation, return full access token
    }
    
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody TwoFactorRequest request) {
        if (twoFactorService.verifyCode(user.getSecret(), request.getCode())) {
            return ResponseEntity.ok(new TokenResponse(fullToken));
        }
        return ResponseEntity.status(401).body("Invalid OTP");
    }
}
```

---

## Q33: What is Spring Security's ExpressionHandler?

**Answer:**

**ExpressionHandler** evaluates SpEL expressions used in security annotations.

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
    
    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = 
            new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(customPermissionEvaluator());
        return handler;
    }
    
    @Bean
    public PermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator();
    }
}

// Custom Permission Evaluator
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Override
    public boolean hasPermission(Authentication auth, Object target, Object permission) {
        if (target instanceof Document doc) {
            return doc.getOwner().equals(auth.getName());
        }
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, 
                                  String targetType, Object permission) {
        // Check by ID
        return permissionRepository.hasPermission(auth.getName(), targetId, permission);
    }
}

// Usage
@PreAuthorize("hasPermission(#document, 'WRITE')")
public void updateDocument(Document document) { }
```

---

## Q34: How do you secure WebSocket endpoints?

**Answer:**

```java
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {
    
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages = 
            MessageMatcherDelegatingAuthorizationManager.builder();
        
        messages
            .nullDestMatcher().authenticated()
            .simpDestMatchers("/app/**").authenticated()
            .simpSubscribeDestMatchers("/topic/public").permitAll()
            .simpSubscribeDestMatchers("/topic/private/**").authenticated()
            .anyMessage().denyAll();
            
        return messages.build();
    }
}

// JWT Token in WebSocket handshake
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                Authentication auth = validateToken(token.substring(7));
                accessor.setUser(auth);
            }
        }
        return message;
    }
}
```

---

## Q35: What is SecurityContextRepository?

**Answer:**

**SecurityContextRepository** stores and retrieves `SecurityContext` between requests.

```java
public interface SecurityContextRepository {
    SecurityContext loadContext(HttpRequestResponseHolder holder);
    void saveContext(SecurityContext context, HttpServletRequest request, 
                     HttpServletResponse response);
    boolean containsContext(HttpServletRequest request);
}
```

### Implementations:

| Implementation | Storage | Use Case |
|----------------|---------|----------|
| `HttpSessionSecurityContextRepository` | HTTP Session | Traditional web apps |
| `RequestAttributeSecurityContextRepository` | Request attribute | Stateless APIs |
| `NullSecurityContextRepository` | None | Fully stateless |

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .securityContext(context -> context
            .securityContextRepository(new RequestAttributeSecurityContextRepository())
        );
    return http.build();
}
```

---

## Q36: How do you implement IP-based access control?

**Answer:**

```java
@Configuration
@EnableWebSecurity  
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**")
                    .access(new WebExpressionAuthorizationManager(
                        "hasRole('ADMIN') and hasIpAddress('192.168.1.0/24')"
                    ))
                .anyRequest().authenticated()
            );
        return http.build();
    }
}

// Custom IP Filter
@Component
public class IpAddressFilter extends OncePerRequestFilter {
    
    private final Set<String> allowedIps = Set.of("192.168.1.100", "10.0.0.1");
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws Exception {
        String clientIp = request.getRemoteAddr();
        
        if (request.getRequestURI().startsWith("/admin") && 
            !allowedIps.contains(clientIp)) {
            response.setStatus(403);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## Q37: What is Spring Security's RunAsManager?

**Answer:**

**RunAsManager** allows temporarily replacing the authentication during method execution.

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
    
    @Bean
    public RunAsManager runAsManager() {
        RunAsManagerImpl runAsManager = new RunAsManagerImpl();
        runAsManager.setKey("myRunAsKey");
        return runAsManager;
    }
}

@Service
public class BackgroundService {
    
    // Execute this method with elevated privileges
    @Secured({"ROLE_USER", "RUN_AS_ADMIN"})
    public void performPrivilegedOperation() {
        // Inside here, authentication has RUN_AS_ADMIN authority
        // Useful for background tasks needing elevated access
    }
}
```

---

## Q38: How do you handle logout in Spring Security?

**Answer:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .logoutSuccessHandler(customLogoutHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .clearAuthentication(true)
                .addLogoutHandler((request, response, auth) -> {
                    // Custom cleanup (revoke tokens, log event, etc.)
                    tokenRepository.revokeAllUserTokens(auth.getName());
                })
            );
        return http.build();
    }
    
    @Bean
    public LogoutSuccessHandler customLogoutHandler() {
        return (request, response, authentication) -> {
            response.setStatus(200);
            response.getWriter().write("{\"message\": \"Logged out successfully\"}");
        };
    }
}
```

### JWT Logout Strategy:

```java
@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
```

---

## Q39: What is SecurityMetadataSource?

**Answer:**

**SecurityMetadataSource** provides security metadata (required authorities) for secured resources.

```java
public interface SecurityMetadataSource {
    Collection<ConfigAttribute> getAttributes(Object object);
    Collection<ConfigAttribute> getAllConfigAttributes();
    boolean supports(Class<?> clazz);
}
```

### Custom Implementation (Database-driven security):

```java
@Component
public class DatabaseSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    
    @Autowired
    private UrlPermissionRepository urlPermissionRepository;
    
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequestUrl();
        String method = fi.getRequest().getMethod();
        
        List<String> roles = urlPermissionRepository.findRolesForUrl(url, method);
        
        if (roles.isEmpty()) {
            return null;  // No specific security config
        }
        
        return roles.stream()
            .map(SecurityConfig::new)
            .collect(Collectors.toList());
    }
}
```

---

## Q40: How do you implement hierarchical roles?

**Answer:**

Role hierarchy allows higher roles to automatically include lower role permissions.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            ROLE_ADMIN > ROLE_MANAGER
            ROLE_MANAGER > ROLE_USER
            ROLE_USER > ROLE_GUEST
        """);
        return hierarchy;
    }
    
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = 
            new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }
}
```

With this configuration:
- `ROLE_ADMIN` has all permissions of MANAGER, USER, and GUEST
- `ROLE_MANAGER` has all permissions of USER and GUEST
- `ROLE_USER` has all permissions of GUEST

```java
// ADMIN can access this (inherits USER role)
@PreAuthorize("hasRole('USER')")
public void userMethod() { }
```

---

## Q41: How do you test Spring Security?

**Answer:**

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/protected"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void authenticatedUser_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/user"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void adminEndpoint_withAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/admin"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = {"USER"})
    void adminEndpoint_withUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin"))
            .andExpect(status().isForbidden());
    }
}
```

---

## Q42: What is the difference between @WithMockUser and @WithUserDetails?

**Answer:**

| Annotation | Description |
|------------|-------------|
| `@WithMockUser` | Creates a mock user with given username/roles |
| `@WithUserDetails` | Loads user from UserDetailsService |
| `@WithAnonymousUser` | Simulates anonymous user |

```java
// @WithMockUser - Quick, doesn't use real UserDetailsService
@Test
@WithMockUser(username = "admin", roles = {"ADMIN"})
void testWithMockAdmin() { }

// @WithUserDetails - Uses real UserDetailsService bean
@Test
@WithUserDetails(value = "admin@test.com", userDetailsServiceBeanName = "customUserDetailsService")
void testWithRealUser() { }

// Custom annotation for reuse
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public @interface WithAdmin { }

@Test
@WithAdmin
void testAsAdmin() { }
```

---

## Q43: How do you secure actuator endpoints?

**Answer:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when_authorized
```

---

## Q44: What is Content Security Policy (CSP)?

**Answer:**

CSP is an HTTP header that prevents XSS attacks by controlling which resources can load.

```java
http.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives(
            "default-src 'self'; " +
            "script-src 'self' https://trusted.cdn.com; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "connect-src 'self' https://api.example.com"
        )
    )
);
```

| Directive | Purpose |
|-----------|---------|
| `default-src` | Fallback for other directives |
| `script-src` | Allowed script sources |
| `style-src` | Allowed stylesheet sources |
| `img-src` | Allowed image sources |
| `connect-src` | Allowed AJAX/WebSocket endpoints |

---

## Q45: How do you implement password reset securely?

**Answer:**

```java
@Service
public class PasswordResetService {
    
    public void initiateReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseReturn();  // Don't reveal if user exists
        
        // Generate secure token
        String token = UUID.randomUUID().toString();
        
        // Store with expiration
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(hashToken(token));
        resetToken.setUser(user);
        resetToken.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        tokenRepository.save(resetToken);
        
        // Send email with unhashed token
        emailService.sendResetLink(email, token);
    }
    
    public void resetPassword(String token, String newPassword) {
        String hashedToken = hashToken(token);
        PasswordResetToken resetToken = tokenRepository.findByToken(hashedToken)
            .orElseThrow(() -> new InvalidTokenException());
        
        if (resetToken.isExpired()) {
            throw new TokenExpiredException();
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Invalidate token
        tokenRepository.delete(resetToken);
        
        // Invalidate all user sessions
        sessionRegistry.getAllSessions(user, false)
            .forEach(SessionInformation::expireNow);
    }
}
```

---

## Q46: What is Spring Security's ObjectPostProcessor?

**Answer:**

`ObjectPostProcessor` allows customizing security objects after they're created.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                    fsi.setSecurityMetadataSource(customMetadataSource);
                    fsi.setAccessDecisionManager(customAccessDecisionManager);
                    return fsi;
                }
            })
        );
    return http.build();
}
```

---

## Q47: How do you implement LDAP authentication?

**Answer:**

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-ldap</artifactId>
</dependency>
```

```java
@Configuration
@EnableWebSecurity
public class LdapSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
    
    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator(
            BaseLdapPathContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator populator = 
            new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
        populator.setGroupSearchFilter("member={0}");
        return populator;
    }
    
    @Bean
    public AuthenticationManager authManager(BaseLdapPathContextSource source) {
        LdapBindAuthenticationManagerFactory factory = 
            new LdapBindAuthenticationManagerFactory(source);
        factory.setUserDnPatterns("uid={0},ou=people");
        return factory.createAuthenticationManager();
    }
}
```

---

## Q48: What is the difference between permitAll() and anonymous()?

**Answer:**

| Method | Description |
|--------|-------------|
| `permitAll()` | Allows all requests (authenticated or not) |
| `anonymous()` | Only allows unauthenticated users |

```java
http.authorizeHttpRequests(auth -> auth
    // Anyone can access (authenticated users too)
    .requestMatchers("/public/**").permitAll()
    
    // Only non-logged-in users 
    .requestMatchers("/login", "/register").anonymous()
    
    // Authenticated users
    .anyRequest().authenticated()
);
```

Use `anonymous()` for login/register pages to redirect logged-in users away.

---

## Q49: How do you handle security in multi-tenant applications?

**Answer:**

```java
@Component
public class TenantSecurityFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws Exception {
        String tenantId = extractTenantId(request);
        TenantContext.setCurrentTenant(tenantId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

@Service
public class TenantAwareUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByUsernameAndTenantId(username, tenantId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

// Method security with tenant check
@PreAuthorize("@tenantService.belongsToTenant(#resourceId)")
public Resource getResource(Long resourceId) { }
```

---

## Q50: What are common Spring Security vulnerabilities to avoid?

**Answer:**

| Vulnerability | Prevention |
|---------------|------------|
| **Weak passwords** | Use BCrypt, enforce strong password policy |
| **Session fixation** | Enable `.sessionFixation().migrateSession()` |
| **CSRF attacks** | Enable CSRF for stateful apps |
| **XSS attacks** | Use CSP headers, escape output |
| **Broken access control** | Use method-level security, test thoroughly |
| **Insecure direct object references** | Validate user owns resource before access |
| **Sensitive data exposure** | Use HTTPS, don't log sensitive data |
| **JWT vulnerabilities** | Validate algorithm, use short expiry, rotate secrets |

```java
// Security checklist configuration
http
    .csrf(Customizer.withDefaults())
    .sessionManagement(s -> s.sessionFixation().migrateSession())
    .headers(h -> h
        .frameOptions(f -> f.deny())
        .contentSecurityPolicy(c -> c.policyDirectives("default-src 'self'"))
    )
    .requiresChannel(c -> c.anyRequest().requiresSecure());
```

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Spring Security** | Authentication + Authorization + Attack protection |
| **Filter Chain** | Series of filters processing each request |
| **Authentication** | Verifying who you are |
| **Authorization** | Verifying what you can do |
| **JWT** | Stateless token-based auth (Header.Payload.Signature) |
| **CSRF** | Attack using authenticated session; protect with tokens |
| **OAuth2** | Authorization framework for third-party access |
| **CORS** | Cross-origin request permissions |
| **Method Security** | @PreAuthorize, @Secured annotations |
| **Best Practices** | BCrypt, HTTPS, short tokens, least privilege |

---

> **Next Topic:** AOP Terminologies
