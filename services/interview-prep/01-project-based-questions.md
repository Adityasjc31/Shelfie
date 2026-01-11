# 📚 Topic 1: Project-Based Interview Questions & Answers

## Digital Bookstore Management System

This document contains comprehensive interview questions and answers based on the Digital Bookstore Management System microservices project.

---

## Q1: Can you give a high-level overview of the Bookstore Management System you worked on?

**Answer:**

The Digital Bookstore Management System is a **microservices-based backend application** built using **Java 21**, **Spring Boot 4.0**, and **Spring Cloud 2025.1.0**. It consists of 9 independent microservices, each handling a specific business domain:

| Service | Port | Responsibility |
|---------|------|----------------|
| **Eureka Service** | 8761 | Service Discovery - maintains registry of all running services |
| **Config Service** | 8888 | Centralized Configuration - manages configs for all services |
| **API Gateway** | 9080 | Single entry point - routing, JWT validation, rate limiting |
| **User Service** | 9082 | User CRUD operations |
| **Authentication Service** | 9081 | JWT authentication (login, register, token management) |
| **Book Service** | 9084 | Book catalog management |
| **Inventory Service** | 9083 | Stock levels, low-stock alerts, bulk operations |
| **Order Service** | 9085 | Order processing and status management |
| **Review Service** | 9086 | Book reviews and ratings |

### Architecture Diagram:
```
                                    ┌─────────────────┐
                                    │   Eureka Server │
                                    │   (Port 8761)   │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
           ┌─────────────┐          ┌─────────────┐          ┌─────────────┐
           │Config Server│          │ API Gateway │◄─────────│   Client    │
           │ (Port 8888) │          │ (Port 9080) │          │  Requests   │
           └─────────────┘          └──────┬──────┘          └─────────────┘
                                           │
        ┌──────────┬──────────┬────────────┼───────────┬──────────┬──────────┐
        ▼          ▼          ▼            ▼           ▼          ▼          ▼
   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
   │  User   │ │  Auth   │ │Inventory│ │  Book   │ │  Order  │ │ Review  │
   │ Service │ │ Service │ │ Service │ │ Service │ │ Service │ │ Service │
   └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘
```

**Key Technologies Used:**
- **Netflix Eureka** for service discovery
- **Spring Cloud Config** for centralized configuration
- **Resilience4j** for circuit breaker patterns
- **JWT** for stateless authentication
- **Spring Data JPA** with MySQL for persistence
- **SpringDoc OpenAPI** for API documentation

---

## Q2: Why did you choose a microservices architecture over a monolithic approach?

**Answer:**

We chose microservices architecture for several key reasons:

### Advantages:

| Benefit | Explanation |
|---------|-------------|
| **Independent Deployment** | Each service can be deployed, updated, and scaled independently. During a sale, we can scale only Order Service. |
| **Technology Flexibility** | Different services can use different databases or frameworks if needed. |
| **Fault Isolation** | If Review Service crashes, ordering and inventory continue working. |
| **Team Scalability** | Different teams can work on different services simultaneously. |
| **Easier Maintenance** | Each service has single responsibility, making code smaller and cleaner. |
| **Business Alignment** | Each microservice maps to a business capability (Orders, Inventory, Users). |

### Trade-offs We Accepted:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    TRADE-OFFS OF MICROSERVICES                          │
├─────────────────────────────────────────────────────────────────────────┤
│ ⚠️ Increased operational complexity (service discovery, config mgmt)   │
│ ⚠️ Network latency between services                                    │
│ ⚠️ Need for distributed tracing and logging                            │
│ ⚠️ Data consistency challenges (eventual consistency patterns)         │
│ ⚠️ More complex testing (integration tests across services)            │
└─────────────────────────────────────────────────────────────────────────┘
```

### When to Choose Microservices:
- Large, complex applications with distinct business domains
- Need for independent scaling of components
- Multiple teams working simultaneously
- High availability requirements

### When to Stick with Monolith:
- Small applications or MVPs
- Small team
- Simple business domain
- Rapid prototyping phase

---

## Q3: Explain the role of Eureka Service in your architecture.

**Answer:**

**Eureka** is our **Service Discovery** server. Its role is to maintain a registry of all running service instances.

### How It Works:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        EUREKA WORKFLOW                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. REGISTRATION                                                         │
│     ┌──────────────┐      "Hey Eureka, I'm         ┌──────────────┐     │
│     │ Order Service│  ─────inventory-service────▶  │    Eureka    │     │
│     │   Starting   │       at 192.168.1.5:9083     │    Server    │     │
│     └──────────────┘                                └──────────────┘     │
│                                                                          │
│  2. HEARTBEAT (every 30 seconds)                                         │
│     ┌──────────────┐      "I'm still alive!"       ┌──────────────┐     │
│     │   Inventory  │  ─────────────────────────▶   │    Eureka    │     │
│     │   Service    │                                │    Server    │     │
│     └──────────────┘                                └──────────────┘     │
│                                                                          │
│  3. DISCOVERY                                                            │
│     ┌──────────────┐   "Give me inventory-service" ┌──────────────┐     │
│     │Order Service │  ─────────────────────────▶   │    Eureka    │     │
│     │              │  ◀─────────────────────────   │    Server    │     │
│     └──────────────┘   [192.168.1.5:9083,          └──────────────┘     │
│                         192.168.1.6:9083]                                │
│                                                                          │
│  4. CLIENT-SIDE LOAD BALANCING                                           │
│     Order Service picks one instance (round-robin) and makes the call   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Code Configuration:

**Eureka Server (EurekaServiceApplication.java):**
```java
@SpringBootApplication
@EnableEurekaServer  // Makes this a Eureka Server
public class EurekaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceApplication.class, args);
    }
}
```

**Eureka Client (in each microservice's properties):**
```properties
# Register with Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# Service name (used for discovery)
spring.application.name=inventory-service
```

### Benefits:
- ✅ No hardcoded service URLs
- ✅ Automatic failover if an instance dies
- ✅ Dynamic scaling without configuration changes
- ✅ Health monitoring via dashboard at `http://localhost:8761`
- ✅ Self-preservation mode prevents accidental de-registration

---

## Q4: How does the Config Service work and why is it important?

**Answer:**

**Spring Cloud Config Server** provides **externalized, centralized configuration** for all microservices.

### Why It's Important:

| Problem | Solution with Config Server |
|---------|----------------------------|
| Duplicate configurations across services | Single source of truth in `config-repo/` |
| Hardcoded values in each service | Environment-specific configurations |
| Restart needed for config changes | Runtime refresh with `@RefreshScope` |
| Scattered sensitive data | Centralized, optionally encrypted |

### How It Works:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     CONFIG SERVER WORKFLOW                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   config-repo/                          Config Server                    │
│   ├── application.properties     ──▶   (Port 8888)                      │
│   ├── book-service.properties                │                          │
│   ├── inventory-service.properties           │                          │
│   └── order-service.properties               │                          │
│                                              │                          │
│                                              ▼                          │
│                              ┌───────────────────────────────┐          │
│                              │  Service Startup Request      │          │
│                              │  "GET /inventory-service/     │          │
│                              │       default"                │          │
│                              └───────────────────────────────┘          │
│                                              │                          │
│                                              ▼                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │  Config Server returns:                                         │   │
│   │  - application.properties (shared settings)                     │   │
│   │  - inventory-service.properties (service-specific)              │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Folder Structure:
```
config-service/src/main/resources/
├── config-repo/
│   ├── application.properties           # Shared (Eureka URL, JWT secret)
│   ├── api-gateway.properties
│   ├── authentication-service.properties
│   ├── book-service.properties
│   ├── inventory-service.properties
│   ├── order-service.properties
│   ├── review-service.properties
│   └── user-service.properties
└── application.properties                # Config Server's own config
```

### Client Configuration:
```properties
# In inventory-service's bootstrap.properties or application.properties
spring.cloud.config.uri=http://localhost:8888
spring.config.import=configserver:
spring.application.name=inventory-service
```

### Runtime Refresh:
```java
@RestController
@RefreshScope  // Values will be refreshed when /actuator/refresh is called
public class InventoryController {
    
    @Value("${inventory.low-stock-threshold:10}")
    private int lowStockThreshold;  // Can be updated without restart!
}
```

---

## Q5: How does JWT authentication work in your project?

**Answer:**

We use **JSON Web Tokens (JWT)** for stateless authentication. Here's the complete flow:

### Authentication Flow Diagram:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         JWT AUTHENTICATION FLOW                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  STEP 1: LOGIN                                                           │
│  ┌────────┐    POST /api/v1/auth/login     ┌────────────────────┐       │
│  │ Client │ ───────────────────────────▶   │ Authentication     │       │
│  │        │    {email, password}           │ Service            │       │
│  └────────┘                                 └─────────┬──────────┘       │
│                                                       │                  │
│  STEP 2: VALIDATE & GENERATE TOKENS                   ▼                  │
│                                             ┌────────────────────┐       │
│                                             │ Validate against   │       │
│                                             │ User Service       │       │
│                                             └─────────┬──────────┘       │
│                                                       │                  │
│  STEP 3: RETURN TOKENS                                ▼                  │
│  ┌────────┐    {accessToken, refreshToken} ┌────────────────────┐       │
│  │ Client │ ◀──────────────────────────────│ Auth Service       │       │
│  │        │    (JWT tokens)                │ (JwtUtil.java)     │       │
│  └────────┘                                 └────────────────────┘       │
│                                                                          │
│  STEP 4: ACCESS PROTECTED RESOURCE                                       │
│  ┌────────┐  GET /api/v1/books             ┌────────────────────┐       │
│  │ Client │ ───────────────────────────▶   │ API Gateway        │       │
│  │        │  Header: Authorization:        │ (validates token)  │       │
│  └────────┘  Bearer <accessToken>          └─────────┬──────────┘       │
│                                                       │ if valid        │
│                                                       ▼                  │
│                                             ┌────────────────────┐       │
│                                             │ Book Service       │       │
│                                             │ (returns data)     │       │
│                                             └────────────────────┘       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### JWT Structure:
```
┌─────────────────────────────────────────────────────────────────────────┐
│                          JWT TOKEN STRUCTURE                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.                    ◀── HEADER    │
│  eyJ1c2VySWQiOiIxIiwiZW1haWwiOiJqb2huQGV4YW1wbGUuY29tIi...◀── PAYLOAD   │
│  SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c             ◀── SIGNATURE  │
│                                                                          │
│  HEADER (Algorithm & Token Type):                                        │
│  { "alg": "HS256", "typ": "JWT" }                                       │
│                                                                          │
│  PAYLOAD (Claims):                                                       │
│  {                                                                       │
│    "userId": "1",                                                        │
│    "email": "john@example.com",                                          │
│    "roles": ["CUSTOMER"],                                                │
│    "tokenType": "ACCESS",                                                │
│    "iss": "digital-bookstore",                                           │
│    "iat": 1704891234,                                                    │
│    "exp": 1704977634                                                     │
│  }                                                                       │
│                                                                          │
│  SIGNATURE:                                                              │
│  HMACSHA256(base64UrlEncode(header) + "." +                             │
│             base64UrlEncode(payload), secret)                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Token Generation Code (JwtUtil.java):
```java
public String generateToken(String userId, String email, List<String> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("email", email);
    claims.put("roles", roles);
    claims.put("tokenType", "ACCESS");
    
    return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuer(issuer)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
}
```

### Token Validation:
```java
public boolean validateToken(String token) {
    try {
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token);
        
        return !isTokenExpired(token);
    } catch (Exception e) {
        log.error("Token validation failed: {}", e.getMessage());
        return false;
    }
}
```

### Two Types of Tokens:
| Token Type | Purpose | Expiration |
|------------|---------|------------|
| **Access Token** | Used for API requests | 24 hours |
| **Refresh Token** | Used to get new access tokens | 7 days |

---

## Q6: Explain the LoggingAspect in your project and how AOP helps.

**Answer:**

**LoggingAspect** uses **Aspect-Oriented Programming (AOP)** to centralize logging across all layers of services.

### Problem Without AOP:
```java
// Without AOP - Repetitive logging in every method! ❌
public InventoryResponseDTO createInventory(InventoryCreateDTO dto) {
    log.info("Entering createInventory with: {}", dto);  // REPETITIVE!
    long startTime = System.currentTimeMillis();
    
    // business logic here...
    
    long duration = System.currentTimeMillis() - startTime;
    log.info("Exiting createInventory, took {} ms", duration);  // REPETITIVE!
    return result;
}
```

### Solution With AOP:
```java
// Clean business logic - no logging clutter! ✅
public InventoryResponseDTO createInventory(InventoryCreateDTO dto) {
    // Pure business logic only
    Inventory inventory = mapToEntity(dto);
    return mapToResponse(inventoryRepository.save(inventory));
}
```

### AOP Concepts Diagram:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          AOP CORE CONCEPTS                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ASPECT = POINTCUT + ADVICE                                              │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │  POINTCUT (WHERE to apply)                                          │ │
│  │  "within(com.book.management.inventory.controller..*)"              │ │
│  │                                                                      │ │
│  │  This means: "All methods in any class inside controller package"   │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│                                +                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │  ADVICE (WHAT to do and WHEN)                                       │ │
│  │                                                                      │ │
│  │  @Before  ──▶ Execute BEFORE method runs                            │ │
│  │  @After   ──▶ Execute AFTER method completes (success or failure)   │ │
│  │  @AfterReturning ──▶ Execute AFTER successful completion            │ │
│  │  @AfterThrowing  ──▶ Execute AFTER exception is thrown              │ │
│  │  @Around  ──▶ Wraps method (before AND after)                       │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### LoggingAspect Implementation:
```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // POINTCUT: Define WHERE to apply
    @Pointcut("within(com.book.management.inventory.controller..*)")
    public void controllerLayer() {}

    @Pointcut("within(com.book.management.inventory.service..*)")
    public void serviceLayer() {}

    // ADVICE: Log BEFORE method execution
    @Before("controllerLayer() || serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info("→ Entering: {}.{}() with arguments: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // ADVICE: Log AFTER successful return
    @AfterReturning(pointcut = "controllerLayer() || serviceLayer()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        log.info("← Exiting: {}.{}() with result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result != null ? result.getClass().getSimpleName() : "void");
    }

    // ADVICE: Measure execution time (AROUND wraps the method)
    @Around("controllerLayer() || serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        Object result = joinPoint.proceed();  // Execute the actual method
        
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("⏱ {}.{}() executed in {} ms",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), executionTime);
        return result;
    }

    // ADVICE: Log exceptions
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        log.error("✗ Exception in {}.{}(): {} - {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }
}
```

### Benefits of AOP:
| Benefit | Explanation |
|---------|-------------|
| **DRY** | Logging logic written once, applied everywhere |
| **Separation of Concerns** | Business logic stays clean and focused |
| **Easy Modifications** | Change logging format in one place |
| **Performance Monitoring** | Easily identify slow methods |
| **Consistent Format** | All logs follow the same pattern |

---

## Q7: How does inter-service communication work in your project?

**Answer:**

We use **synchronous REST-based communication** via **OpenFeign** declarative clients.

### Feign Client Communication Flow:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    FEIGN CLIENT COMMUNICATION                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Order Service                    Eureka                Inventory       │
│   ┌──────────────┐                ┌──────────┐          ┌──────────────┐│
│   │              │  1. Lookup     │          │          │              ││
│   │ OrderService │ ─────────────▶ │  Eureka  │          │  Inventory   ││
│   │   Impl       │  "inventory-   │  Server  │          │   Service    ││
│   │              │   service"     │          │          │              ││
│   └──────────────┘                └────┬─────┘          └──────────────┘│
│         │                              │                       ▲        │
│         │                              │ 2. Returns            │        │
│         │                              │ [192.168.1.5:9083,    │        │
│         │                              │  192.168.1.6:9083]    │        │
│         │                              ▼                       │        │
│         │                    3. LoadBalancer picks one         │        │
│         │                       (Round Robin)                  │        │
│         │                              │                       │        │
│         └──────────────────────────────┼───────────────────────┘        │
│                        4. HTTP Call to selected instance                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Step 1: Define Feign Client Interface
```java
@FeignClient(name = "inventory-service", path = "/api/v1/inventory")
public interface InventoryClient {
    
    @GetMapping("/book/{bookId}/availability")
    Boolean checkAvailability(@PathVariable Long bookId, 
                              @RequestParam Integer quantity);
    
    @PatchMapping("/bulk/reduce")
    void reduceBulkInventory(@RequestBody BulkStockReduceDTO request);
    
    @GetMapping("/book/{bookId}")
    InventoryResponseDTO getInventoryByBookId(@PathVariable Long bookId);
}
```

### Step 2: Use in Service
```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final InventoryClient inventoryClient;
    
    public OrderResponseDTO placeOrder(OrderCreateDTO dto) {
        // Step 1: Check if stock is available
        boolean isAvailable = inventoryClient.checkAvailability(
            dto.getBookId(), dto.getQuantity());
        
        if (!isAvailable) {
            throw new InsufficientStockException("Stock not available");
        }
        
        // Step 2: Create order
        Order order = createOrder(dto);
        
        // Step 3: Reduce inventory
        BulkStockReduceDTO reduceRequest = BulkStockReduceDTO.builder()
            .bookQuantities(Map.of(dto.getBookId(), dto.getQuantity()))
            .build();
        inventoryClient.reduceBulkInventory(reduceRequest);
        
        return mapToResponse(orderRepository.save(order));
    }
}
```

### Key Points:
- `name = "inventory-service"` matches the service name registered in Eureka
- `lb://` prefix enables load balancing automatically
- Spring Cloud LoadBalancer picks one healthy instance
- Feign handles HTTP client creation, serialization, error handling

---

## Q8: What resilience patterns have you implemented and why?

**Answer:**

We implemented several patterns using **Resilience4j** to handle failures gracefully.

### Resilience Patterns Diagram:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       RESILIENCE4J PATTERNS                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. CIRCUIT BREAKER                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │                                                                      │ │
│  │   CLOSED ───────────▶ OPEN ───────────▶ HALF-OPEN                   │ │
│  │   (Normal)           (Failures        (Test calls                    │ │
│  │                       exceeded         to check                      │ │
│  │                       threshold)       recovery)                     │ │
│  │        ◀───────────────────────────────────┘                        │ │
│  │        (Success rate OK, return to CLOSED)                          │ │
│  │                                                                      │ │
│  │   Config:                                                            │ │
│  │   - slidingWindowSize=10 (last 10 calls tracked)                    │ │
│  │   - failureRateThreshold=50% (open after 5/10 failures)             │ │
│  │   - waitDurationInOpenState=10s (wait before testing)               │ │
│  │                                                                      │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  2. RETRY                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │                                                                      │ │
│  │   Call ──▶ Fail ──▶ Wait 1s ──▶ Retry ──▶ Fail ──▶ Wait 2s ──▶ ... │ │
│  │                     (exponential backoff)                            │ │
│  │                                                                      │ │
│  │   Config:                                                            │ │
│  │   - maxAttempts=3                                                   │ │
│  │   - waitDuration=1s (with exponential multiplier)                   │ │
│  │                                                                      │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  3. RATE LIMITER                                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │                                                                      │ │
│  │   Limits: 100 requests/second per service                           │ │
│  │   Prevents: Overwhelming downstream services                        │ │
│  │                                                                      │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
│  4. BULKHEAD                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐ │
│  │                                                                      │ │
│  │   Limits concurrent calls to isolate failures                       │ │
│  │   Prevents thread pool exhaustion                                   │ │
│  │                                                                      │ │
│  └─────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Code Implementation:

In our project, resilience is implemented in two places:

**1. API Gateway - Circuit Breakers via GatewayConfig:**
```java
// Routes apply circuit breaker per service
.route("inventory-service", r -> r
        .path("/api/v1/inventory/**")
        .filters(f -> f
                .circuitBreaker(c -> c
                        .setName("inventoryCircuitBreaker")
                        .setFallbackUri("forward:/fallback/inventory"))
                .retry(retryConfig -> retryConfig.setRetries(3)))
        .uri("lb://inventory-service"))
```

**2. Order Service - Feign Client with FallbackFactory:**
```java
// Feign Client with FallbackFactory for graceful degradation
@FeignClient(name = "inventory-service", 
             path = "/api/v1/inventory", 
             fallbackFactory = InventoryClientFallbackFactory.class)
public interface InventoryServiceClient {
    @PatchMapping(value = "/bulk/reduce", consumes = MediaType.APPLICATION_JSON_VALUE)
    void reduceStock(@RequestBody ReduceInventoryStockRequestDTO request);
}

// FallbackFactory captures the actual exception
@Component
@Slf4j
public class InventoryClientFallbackFactory 
        implements FallbackFactory<InventoryServiceClient> {

    @Override
    public InventoryServiceClient create(Throwable cause) {
        return new InventoryServiceClient() {
            @Override
            public void reduceStock(ReduceInventoryStockRequestDTO request) {
                log.error("CRITICAL: Inventory Service call failed for items: {} | Cause: {} - {}",
                        request.getBookQuantities(),
                        cause.getClass().getSimpleName(),
                        cause.getMessage());
                
                throw new OrderNotPlacedException(
                        "Inventory Service unavailable: " + cause.getMessage());
            }
        };
    }
}
```

### Configuration (application.properties - Shared/Default Configs):
```properties
# ==========================================
# CIRCUIT BREAKER - DEFAULT (Shared)
# ==========================================
resilience4j.circuitbreaker.configs.default.register-health-indicator=true
resilience4j.circuitbreaker.configs.default.sliding-window-size=10
resilience4j.circuitbreaker.configs.default.minimum-number-of-calls=5
resilience4j.circuitbreaker.configs.default.failure-rate-threshold=50
resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.configs.default.permitted-number-of-calls-in-half-open-state=3
resilience4j.circuitbreaker.configs.default.slow-call-duration-threshold=2s
resilience4j.circuitbreaker.configs.default.slow-call-rate-threshold=50

# ==========================================
# RETRY - DEFAULT (Shared)
# ==========================================
resilience4j.retry.configs.default.max-attempts=3
resilience4j.retry.configs.default.wait-duration=1s
resilience4j.retry.configs.default.enable-exponential-backoff=true
resilience4j.retry.configs.default.exponential-backoff-multiplier=2

# ==========================================
# RATE LIMITER - DEFAULT (Shared)
# ==========================================
resilience4j.ratelimiter.configs.default.limit-for-period=100
resilience4j.ratelimiter.configs.default.limit-refresh-period=1s
resilience4j.ratelimiter.configs.default.timeout-duration=0s

# ==========================================
# TIMELIMITER (Gateway-Specific)
# ==========================================
resilience4j.timelimiter.configs.default.timeout-duration=5s
resilience4j.timelimiter.configs.default.cancel-running-future=false
```

### Per-Service Circuit Breaker Instances (api-gateway.properties):
```properties
# Each service route has its own circuit breaker inheriting default config
resilience4j.circuitbreaker.instances.authCircuitBreaker.base-config=default
resilience4j.circuitbreaker.instances.bookCircuitBreaker.base-config=default
resilience4j.circuitbreaker.instances.inventoryCircuitBreaker.base-config=default
resilience4j.circuitbreaker.instances.orderCircuitBreaker.base-config=default
resilience4j.circuitbreaker.instances.reviewCircuitBreaker.base-config=default
resilience4j.circuitbreaker.instances.userCircuitBreaker.base-config=default
```

### Why These Patterns:
| Pattern | Problem Solved |
|---------|---------------|
| **Circuit Breaker** | Prevents cascade failures when a service is down |
| **Retry** | Handles transient failures (network blips) |
| **Rate Limiter** | Prevents overwhelming downstream services |
| **Bulkhead** | Isolates failures to prevent thread exhaustion |
| **Fallback** | Provides graceful degradation with default responses |

---

## Q9: How do you handle exceptions across microservices?

**Answer:**

We use **Global Exception Handling** with `@RestControllerAdvice` for consistent error responses.

### Exception Handling Architecture:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     GLOBAL EXCEPTION HANDLING                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Controller                  GlobalExceptionHandler                     │
│   ┌──────────────┐           ┌────────────────────────────────────────┐ │
│   │              │           │  @RestControllerAdvice                 │ │
│   │  getBook(1)  │ ─throws─▶ │                                        │ │
│   │              │ BookNot   │  @ExceptionHandler(BookNotFound.class) │ │
│   │              │ Found     │  handles → returns 404 with            │ │
│   │              │ Exception │           ErrorResponse JSON           │ │
│   └──────────────┘           └────────────────────────────────────────┘ │
│                                                                          │
│   Response to Client:                                                    │
│   {                                                                      │
│     "status": 404,                                                       │
│     "error": "Not Found",                                                │
│     "message": "Book not found with id: 1",                              │
│     "timestamp": "2024-01-10T15:30:45"                                   │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Custom Exception Classes:
```java
public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
```

### Error Response DTO:
```java
@Data
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}
```

### Global Exception Handler:
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFound(
            InventoryNotFoundException ex, WebRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex) {
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Insufficient Stock")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Benefits:
| Benefit | Description |
|---------|-------------|
| **Consistent Response Format** | All errors follow same JSON structure |
| **Clean Controllers** | No try-catch blocks in controller code |
| **Centralized Logging** | All exceptions logged in one place |
| **Proper HTTP Status Codes** | 404 for not found, 400 for bad request, etc. |
| **Security** | Generic message for 500 errors (no stack trace leak) |

---

## Q10: How is the API Gateway configured in your project?

**Answer:**

The API Gateway is built using **Spring Cloud Gateway** (reactive, WebFlux-based).

### API Gateway Responsibilities:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        API GATEWAY FUNCTIONS                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌────────────┐                                                         │
│  │   Client   │                                                         │
│  └─────┬──────┘                                                         │
│        │                                                                 │
│        ▼                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐│
│  │                      API GATEWAY (Port 9080)                        ││
│  │                                                                      ││
│  │  1. 🔐 AUTHENTICATION                                               ││
│  │     - Validates JWT tokens                                          ││
│  │     - Blocks unauthorized requests                                  ││
│  │                                                                      ││
│  │  2. 🔀 ROUTING                                                      ││
│  │     - Routes /api/v1/users/** → user-service                       ││
│  │     - Routes /api/v1/books/** → book-service                       ││
│  │     - Routes /api/v1/orders/** → order-service                     ││
│  │                                                                      ││
│  │  3. ⚖️ LOAD BALANCING                                               ││
│  │     - Distributes requests across service instances                 ││
│  │                                                                      ││
│  │  4. 🛡️ RATE LIMITING                                                ││
│  │     - Limits 100 requests/second                                    ││
│  │                                                                      ││
│  │  5. 🔄 CIRCUIT BREAKER                                              ││
│  │     - Fails fast when services are down                             ││
│  │                                                                      ││
│  │  6. 🌐 CORS                                                         ││
│  │     - Handles Cross-Origin requests                                 ││
│  │                                                                      ││
│  │  7. 📝 LOGGING                                                      ││
│  │     - Logs all requests and responses                               ││
│  │                                                                      ││
│  └─────────────────────────────────────────────────────────────────────┘│
│        │                                                                 │
│        ▼                                                                 │
│  ┌──────────┬──────────┬──────────┬──────────┬──────────┐              │
│  │  User    │  Book    │  Order   │ Inventory│  Review  │              │
│  │ Service  │ Service  │ Service  │  Service │  Service │              │
│  └──────────┴──────────┴──────────┴──────────┴──────────┘              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Route Configuration (Java-Based via RouteLocatorBuilder):
```java
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;
    private final LoggingFilter loggingFilter;
    private final GatewaySecretProperties gatewaySecretProperties;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Auth Service (no auth filter needed - public endpoints)
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("authCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://authentication-service"))

                // Book Service
                .route("book-service", r -> r
                        .path("/api/v1/book/**")
                        .filters(f -> applyStandardFilters(f, "bookCircuitBreaker",
                                "forward:/fallback/books"))
                        .uri("lb://book-service"))

                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/v1/inventory/**")
                        .filters(f -> applyStandardFilters(f, "inventoryCircuitBreaker",
                                "forward:/fallback/inventory"))
                        .uri("lb://inventory-service"))

                // Order Service
                .route("order-service", r -> r
                        .path("/api/v1/order/**")
                        .filters(f -> applyStandardFilters(f, "orderCircuitBreaker",
                                "forward:/fallback/orders"))
                        .uri("lb://order-service"))

                // Review Service
                .route("review-service", r -> r
                        .path("/api/v1/reviews/**")
                        .filters(f -> applyStandardFilters(f, "reviewCircuitBreaker",
                                "forward:/fallback/reviews"))
                        .uri("lb://review-service"))

                // User Service
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> applyStandardFilters(f, "userCircuitBreaker",
                                "forward:/fallback/users"))
                        .uri("lb://user-service"))

                .build();
    }

    // Helper method that applies: gateway secret header, logging, auth filter,
    // circuit breaker, and retry for authenticated routes
    private GatewayFilterSpec applyStandardFilters(GatewayFilterSpec filters,
            String circuitBreakerName, String fallbackPath) {
        return addGatewaySecretHeader(filters)
                .filter(loggingFilter.apply(new LoggingFilter.Config()))
                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                .circuitBreaker(c -> c
                        .setName(circuitBreakerName)
                        .setFallbackUri(fallbackPath))
                .retry(retryConfig -> retryConfig.setRetries(3));
    }
}
```

> **Note:** `lb://` prefix means "load balanced" - uses Eureka to discover service instances.

### Authentication Filter:
```java
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;
    private final AuthenticationProperties authProperties;
    private final RoleAuthorizationService roleAuthorizationService;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login", "/auth/register", "/auth/refresh",
            "/actuator", "/swagger-ui", "/v3/api-docs");

    public AuthenticationFilter(WebClient.Builder webClientBuilder, 
                                AuthenticationProperties authProperties,
                                RoleAuthorizationService roleAuthorizationService) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.authProperties = authProperties;
        this.roleAuthorizationService = roleAuthorizationService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            // Skip authentication for public endpoints
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            // Check if authentication is disabled (development mode)
            if (!authProperties.isEnabled()) {
                log.warn("⚠ Authentication DISABLED - Using mock user");
                return proceedWithMockUser(exchange, chain);
            }

            // Extract and validate Authorization header
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing/Invalid Authorization header", 
                              HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // Validate token with Authentication Service via WebClient
            return validateToken(token)
                    .flatMap(validationResponse -> {
                        if (validationResponse.isValid()) {
                            List<String> userRoles = validationResponse.getRoles();

                            // Check role-based authorization
                            if (!roleAuthorizationService.isAuthorized(path, 
                                    request.getMethod(), userRoles)) {
                                return onError(exchange, "Access Denied", HttpStatus.FORBIDDEN);
                            }

                            // Add user context to request headers for downstream services
                            ServerHttpRequest modifiedRequest = request.mutate()
                                    .header("X-User-Id", validationResponse.getUserId())
                                    .header("X-User-Email", validationResponse.getUsername())
                                    .header("X-User-Roles", String.join(",", userRoles))
                                    .build();

                            return chain.filter(exchange.mutate()
                                    .request(modifiedRequest).build());
                        }
                        return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                    });
        };
    }

    private Mono<ValidationResponse> validateToken(String token) {
        String validateUrl = authProperties.getService().getUrl() 
                + authProperties.getService().getValidateEndpoint();
        
        return webClientBuilder.build()
                .post()
                .uri(validateUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ValidationResponse.class);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
}
```

### Public Paths Configuration:
```properties
gateway.security.public-paths[0]=/api/v1/auth/login
gateway.security.public-paths[1]=/api/v1/auth/register
gateway.security.public-paths[2]=/actuator/**
gateway.security.public-paths[3]=/swagger-ui/**
gateway.security.public-paths[4]=/v3/api-docs/**
```

---

## 🟢 BEGINNER LEVEL QUESTIONS (Q11-Q18)

---

## Q11: What is the project structure of each microservice?

**Answer:**

Each microservice follows a **layered architecture**:

```
service-name/
├── src/main/java/com/book/management/servicename/
│   ├── ServiceNameApplication.java    # Main class with @SpringBootApplication
│   ├── aspect/                         # AOP aspects (logging, performance)
│   │   └── LoggingAspect.java
│   ├── client/                         # Feign clients for inter-service calls
│   │   ├── OtherServiceClient.java     # Feign client interface
│   │   └── fallback/                   # Fallback implementations
│   │       └── OtherServiceFallbackFactory.java
│   ├── config/                         # Configuration classes
│   │   ├── AppConfig.java
│   │   ├── OpenApiConfig.java          # Swagger documentation
│   │   └── FeignConfig.java            # Feign client configuration
│   ├── controller/                     # REST controllers
│   │   └── EntityController.java
│   ├── dto/                            # Data Transfer Objects
│   │   ├── requestdto/                 # Request DTOs (input)
│   │   │   ├── EntityCreateDTO.java
│   │   │   └── EntityUpdateDTO.java
│   │   └── responsedto/                # Response DTOs (output)
│   │       └── EntityResponseDTO.java
│   ├── enums/                          # Enumeration types
│   │   └── EntityStatus.java
│   ├── exception/                      # Custom exceptions & handlers
│   │   ├── EntityNotFoundException.java
│   │   ├── ValidationException.java
│   │   └── GlobalExceptionHandler.java # @RestControllerAdvice
│   ├── filter/                         # Request/Response filters
│   │   └── GatewaySecurityFilter.java  # Validates gateway secret header
│   ├── model/                          # JPA entities (not "entity/")
│   │   └── Entity.java
│   ├── repository/                     # Data access layer
│   │   └── EntityRepository.java
│   └── service/                        # Business logic
│       ├── EntityService.java          # Interface
│       └── impl/
│           └── EntityServiceImpl.java  # Implementation
└── src/main/resources/
    ├── application.properties          # Bootstrap properties (minimal)
    └── banner.txt                      # Custom startup banner
```

### Layer Responsibilities:

| Layer | Responsibility |
|-------|---------------|
| **Controller** | Handle HTTP requests, validate input, return responses |
| **Service** | Business logic, transaction management |
| **Repository** | Database operations via Spring Data JPA |
| **Model** | JPA entity mapping to database tables |
| **DTO** | Data transfer between layers/services (request/response) |
| **Client** | Feign clients for inter-service communication |
| **Filter** | Request/response processing (e.g., gateway security) |
| **Exception** | Custom exceptions and global error handling |
| **Config** | Spring configuration beans and settings |
| **Enums** | Enumeration types for status, types, etc. |
| **Aspect** | Cross-cutting concerns via AOP (logging, timing) |

---

## Q12: What are DTOs and why do you use them?

**Answer:**

**DTOs (Data Transfer Objects)** are simple objects that carry data between processes or layers.

### Why Use DTOs Instead of Entities Directly?

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    WHY DTOs?                                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ❌ WITHOUT DTOs (Exposing Entity Directly):                            │
│  ─────────────────────────────────────────────                           │
│  @Entity                                                                 │
│  class User {                                                            │
│      Long id;                                                            │
│      String email;                                                       │
│      String password;  ←── ⚠️ EXPOSED TO CLIENT!                        │
│      String internalNote;  ←── ⚠️ INTERNAL DATA LEAKED!                 │
│  }                                                                       │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────   │
│                                                                          │
│  ✅ WITH DTOs (Controlled Response):                                    │
│  ───────────────────────────────────                                     │
│  class UserResponseDTO {                                                 │
│      Long id;                                                            │
│      String email;                                                       │
│      // password NOT included                                            │
│      // internalNote NOT included                                        │
│  }                                                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Types of DTOs in Our Project:

```java
// FOR CREATING - only fields needed for creation
public record InventoryCreateDTO(
    @NotNull Long bookId,
    @Min(0) Integer quantity,
    @Min(1) Integer lowStockThreshold
) {}

// FOR RESPONSE - what client receives
public record InventoryResponseDTO(
    Long id,
    Long bookId,
    Integer availableQuantity,
    Boolean lowStock,
    LocalDateTime lastUpdated
) {}

// FOR UPDATING - partial update fields
public record InventoryUpdateDTO(
    Integer quantity,
    Integer lowStockThreshold
) {}
```

### Benefits:

| Benefit | Explanation |
|---------|-------------|
| **Security** | Hide sensitive fields (password, internal IDs) |
| **Decoupling** | API contract independent of database schema |
| **Flexibility** | Different DTOs for different use cases |
| **Validation** | Validation annotations on input DTOs |
| **Performance** | Return only needed fields |

---

## Q13: How do you validate input data in your APIs?

**Answer:**

We use **Bean Validation (Jakarta Validation)** with annotations.

### Validation Annotations:

```java
public class BookCreateDTO {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be 1-255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^\\d{13}$", message = "ISBN must be 13 digits")
    private String isbn;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @DecimalMax(value = "9999.99", message = "Price cannot exceed 9999.99")
    private BigDecimal price;
    
    @Min(value = 1, message = "Page count must be at least 1")
    private Integer pageCount;
    
    @Email(message = "Invalid email format")
    private String publisherEmail;
    
    @Past(message = "Publication date must be in the past")
    private LocalDate publicationDate;
}
```

### Controller Usage:

```java
@PostMapping
public ResponseEntity<BookResponseDTO> createBook(
        @Valid @RequestBody BookCreateDTO dto) {  // @Valid triggers validation
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookService.createBook(dto));
}
```

### Common Validation Annotations:

| Annotation | Purpose |
|------------|---------|
| `@NotNull` | Field must not be null |
| `@NotBlank` | String must not be null or empty |
| `@NotEmpty` | Collection/array must have elements |
| `@Size(min, max)` | String length or collection size |
| `@Min`, `@Max` | Numeric range |
| `@Email` | Valid email format |
| `@Pattern` | Regex pattern match |
| `@Past`, `@Future` | Date constraints |

---

## Q14: How do you connect to the database in each service?

**Answer:**

We use **Spring Data JPA** with **MySQL**.

### Configuration:

```properties
# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Entity Example:

```java
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long bookId;
    
    @Column(nullable = false)
    private Integer availableQuantity;
    
    @Column(nullable = false)
    private Integer lowStockThreshold;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
```

### Repository:

```java
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByBookId(Long bookId);
    
    List<Inventory> findByAvailableQuantityLessThanEqual(Integer threshold);
    
    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity < i.lowStockThreshold")
    List<Inventory> findAllLowStock();
}
```

---

## Q15: What HTTP methods and status codes do you use?

**Answer:**

### HTTP Methods:

| Method | Purpose | Example |
|--------|---------|---------|
| **GET** | Retrieve resource(s) | `GET /api/v1/books/{id}` |
| **POST** | Create new resource | `POST /api/v1/books` |
| **PUT** | Full update (replace) | `PUT /api/v1/books/{id}` |
| **PATCH** | Partial update | `PATCH /api/v1/inventory/{id}` |
| **DELETE** | Remove resource | `DELETE /api/v1/books/{id}` |

### HTTP Status Codes We Use:

| Code | When Used |
|------|-----------|
| **200 OK** | Successful GET, PUT, PATCH |
| **201 Created** | Successful POST |
| **204 No Content** | Successful DELETE |
| **400 Bad Request** | Validation failed, invalid input |
| **401 Unauthorized** | Missing or invalid token |
| **403 Forbidden** | Valid token but insufficient permissions |
| **404 Not Found** | Resource doesn't exist |
| **409 Conflict** | Duplicate resource (e.g., ISBN exists) |
| **500 Internal Server Error** | Unexpected server error |

### Controller Example:

```java
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));  // 200
    }
    
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.create(dto));  // 201
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id, @Valid @RequestBody BookUpdateDTO dto) {
        return ResponseEntity.ok(bookService.update(id, dto));  // 200
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();  // 204
    }
}
```

---

## Q16: How do you document your APIs?

**Answer:**

We use **SpringDoc OpenAPI** (Swagger) for API documentation.

### Dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Access:

- **Swagger UI**: `http://localhost:9080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:9080/v3/api-docs`

### Annotations:

```java
@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    @Operation(
        summary = "Get inventory by book ID",
        description = "Returns inventory details for a specific book"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<InventoryResponseDTO> getByBookId(
            @Parameter(description = "ID of the book") 
            @PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryService.getByBookId(bookId));
    }
}
```

---

## Q17: What is the purpose of the `@RestController` annotation?

**Answer:**

`@RestController` is a combination of two annotations:

```java
@RestController = @Controller + @ResponseBody
```

### Comparison:

| Annotation | Returns | Use Case |
|------------|---------|----------|
| `@Controller` | View name (HTML) | Web MVC with Thymeleaf |
| `@RestController` | JSON/XML directly | REST APIs |

### Example:

```java
// With @RestController - returns JSON automatically
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @GetMapping("/{id}")
    public BookResponseDTO getBook(@PathVariable Long id) {
        return bookService.findById(id);  // Automatically serialized to JSON
    }
}

// Equivalent with @Controller
@Controller
@RequestMapping("/api/v1/books")
public class BookController {
    
    @GetMapping("/{id}")
    @ResponseBody  // Need to add this!
    public BookResponseDTO getBook(@PathVariable Long id) {
        return bookService.findById(id);
    }
}
```

---

## Q18: What dependencies did you use in pom.xml?

**Answer:**

Key dependencies per service type:

```xml
<!-- COMMON TO ALL SERVICES -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- FOR DATABASE SERVICES -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- FOR INTER-SERVICE COMMUNICATION -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- FOR RESILIENCE -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>

<!-- FOR API DOCUMENTATION -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

---

## 🟡 INTERMEDIATE LEVEL QUESTIONS (Q19-Q30)

---

## Q19: How do you handle service-to-service authentication?

**Answer:**

When one service calls another, we forward the JWT token:

```java
// Feign RequestInterceptor to forward tokens
@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate template) {
        // Get token from current request context
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        
        if (requestAttributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null) {
                template.header("Authorization", authHeader);
            }
        }
    }
}
```

### Flow:

```
Client → API Gateway (validates JWT) → Order Service → (forwards JWT) → Inventory Service
```

---

## Q20: How do you handle transactions across multiple database operations?

**Answer:**

We use `@Transactional` for local transactions within a single service:

```java
@Service
@Transactional(readOnly = true)  // Default for class
public class OrderServiceImpl implements OrderService {
    
    @Transactional  // Write transaction for this method
    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        // All operations in this method are in one transaction
        Order order = mapToEntity(dto);
        orderRepository.save(order);
        
        for (OrderItem item : order.getItems()) {
            orderItemRepository.save(item);
        }
        
        // If any save fails, entire transaction rolls back
        return mapToResponse(order);
    }
}
```

### Transaction Properties:

| Property | Purpose |
|----------|---------|
| `readOnly = true` | Optimization for read-only operations |
| `propagation` | How transaction behaves with existing transaction |
| `isolation` | Isolation level (READ_COMMITTED, etc.) |
| `rollbackFor` | Which exceptions trigger rollback |
| `timeout` | Transaction timeout |

---

## Q21: How do you implement pagination in your APIs?

**Answer:**

We use Spring Data's `Pageable` interface:

```java
// Controller
@GetMapping
public ResponseEntity<Page<BookResponseDTO>> getAllBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {
    
    Sort sort = direction.equalsIgnoreCase("desc") 
        ? Sort.by(sortBy).descending() 
        : Sort.by(sortBy).ascending();
    
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(bookService.findAll(pageable));
}

// Service
public Page<BookResponseDTO> findAll(Pageable pageable) {
    return bookRepository.findAll(pageable)
            .map(this::mapToResponse);
}
```

### Response:

```json
{
    "content": [...],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10
    },
    "totalPages": 5,
    "totalElements": 47,
    "first": true,
    "last": false
}
```

---

## Q22: How do you map between Entity and DTO?

**Answer:**

We use **MapStruct** for compile-time mapping:

```java
@Mapper(componentModel = "spring")
public interface InventoryMapper {
    
    InventoryResponseDTO toResponseDTO(Inventory entity);
    
    Inventory toEntity(InventoryCreateDTO dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(InventoryUpdateDTO dto, @MappingTarget Inventory entity);
    
    List<InventoryResponseDTO> toResponseDTOList(List<Inventory> entities);
}
```

### Usage in Service:

```java
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMapper mapper;
    
    public InventoryResponseDTO create(InventoryCreateDTO dto) {
        Inventory entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(inventoryRepository.save(entity));
    }
}
```

---

## Q23: How do you test your microservices?

**Answer:**

We implement multiple levels of testing:

### 1. Unit Tests (Service Layer):

```java
@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    @Mock
    private InventoryMapper mapper;
    
    @InjectMocks
    private InventoryServiceImpl inventoryService;
    
    @Test
    void getByBookId_WhenExists_ReturnsInventory() {
        // Arrange
        Inventory inventory = new Inventory(1L, 100L, 50, 10, null);
        when(inventoryRepository.findByBookId(100L))
            .thenReturn(Optional.of(inventory));
        when(mapper.toResponseDTO(inventory))
            .thenReturn(new InventoryResponseDTO(1L, 100L, 50, false, null));
        
        // Act
        InventoryResponseDTO result = inventoryService.getByBookId(100L);
        
        // Assert
        assertNotNull(result);
        assertEquals(100L, result.bookId());
        verify(inventoryRepository).findByBookId(100L);
    }
    
    @Test
    void getByBookId_WhenNotExists_ThrowsException() {
        when(inventoryRepository.findByBookId(999L))
            .thenReturn(Optional.empty());
        
        assertThrows(InventoryNotFoundException.class, 
            () -> inventoryService.getByBookId(999L));
    }
}
```

### 2. Integration Tests:

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InventoryControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void createInventory_ReturnsCreated() throws Exception {
        String json = """
            {"bookId": 1, "quantity": 100, "lowStockThreshold": 10}
            """;
        
        mockMvc.perform(post("/api/v1/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.availableQuantity").value(100));
    }
}
```

---

## Q24: How do you handle database migrations?

**Answer:**

We use **Flyway** for version-controlled database migrations:

```
src/main/resources/db/migration/
├── V1__Create_inventory_table.sql
├── V2__Add_low_stock_threshold.sql
└── V3__Add_last_updated_column.sql
```

### Migration Script Example:

```sql
-- V1__Create_inventory_table.sql
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL UNIQUE,
    available_quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V2__Add_low_stock_threshold.sql
ALTER TABLE inventory 
ADD COLUMN low_stock_threshold INT NOT NULL DEFAULT 10;
```

### Configuration:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

---

## Q25: How do you implement logging across services?

**Answer:**

We use **SLF4J with Logback** and include **correlation IDs** for distributed tracing:

```java
// Adding correlation ID to all requests
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) {
        
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        MDC.put("correlationId", correlationId);
        response.setHeader("X-Correlation-ID", correlationId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

### Logback Configuration:

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss} [%X{correlationId}] %-5level %logger{36} - %msg%n</pattern>
```

### Log Output:

```
2024-01-10 15:30:45 [abc-123-xyz] INFO  OrderService - Creating order for user 5
2024-01-10 15:30:45 [abc-123-xyz] INFO  InventoryClient - Checking inventory for book 10
```

---

## Q26: What is the difference between Eureka self-preservation mode?

**Answer:**

**Self-preservation mode** prevents Eureka from removing instances during network partitions.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    EUREKA SELF-PRESERVATION                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SCENARIO: Network partition between Eureka and services               │
│                                                                          │
│   ┌────────────┐          X X X          ┌───────────────┐              │
│   │   Eureka   │◄──── Network ───────────│  All Services │              │
│   │   Server   │       Failure           │  (Still alive)│              │
│   └────────────┘                         └───────────────┘              │
│                                                                          │
│   WITHOUT Self-Preservation:                                             │
│   • Eureka removes all services (no heartbeats)                         │
│   • Clients can't discover ANY service                                  │
│   • Complete system failure! ❌                                          │
│                                                                          │
│   WITH Self-Preservation:                                                │
│   • Eureka notices: "I haven't received heartbeats from 85% of services"│
│   • Eureka assumes: "Network issue, not service deaths"                 │
│   • Eureka keeps: Registry intact                                       │
│   • Better than nothing! ✅                                              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Configuration:

```properties
eureka.server.enable-self-preservation=true
eureka.server.renewal-percent-threshold=0.85
```

---

## Q27: How do you handle environment-specific configurations?

**Answer:**

Using Spring Profiles and Config Server:

```
config-repo/
├── application.properties           # Common to all
├── application-dev.properties       # Dev environment
├── application-prod.properties      # Production
├── inventory-service.properties     # Service-specific
├── inventory-service-dev.properties # Service + dev
└── inventory-service-prod.properties# Service + prod
```

### Activation:

```properties
# Set active profile
spring.profiles.active=dev

# OR via command line
java -jar app.jar --spring.profiles.active=prod
```

### Example Differences:

```properties
# application-dev.properties
spring.jpa.show-sql=true
logging.level.root=DEBUG

# application-prod.properties
spring.jpa.show-sql=false
logging.level.root=WARN
```

---

## Q28: How do you start services in the correct order?

**Answer:**

Services have startup dependencies:

```
1. Eureka Server (must be first)
    ↓
2. Config Server (needs Eureka for registration)
    ↓
3. All other services (need both Eureka and Config)
```

### We use `docker-compose` dependencies:

```yaml
services:
  eureka-server:
    build: ./eureka-service
    ports:
      - "8761:8761"
    
  config-server:
    build: ./config-service
    depends_on:
      eureka-server:
        condition: service_healthy
    
  inventory-service:
    build: ./inventory-service
    depends_on:
      config-server:
        condition: service_healthy
```

### Spring Boot Retry for Config:

```properties
spring.cloud.config.fail-fast=true
spring.cloud.config.retry.initial-interval=1000
spring.cloud.config.retry.max-attempts=10
```

---

## Q29: What is the purpose of the `application.properties` vs `bootstrap.properties`?

**Answer:**

| File | Loaded When | Purpose |
|------|-------------|---------|
| `bootstrap.properties` | Very early (before app context) | Config Server URL, encryption |
| `application.properties` | Normal startup | App configuration |

### bootstrap.properties:

```properties
# Loaded FIRST - tells app where to get config
spring.application.name=inventory-service
spring.cloud.config.uri=http://localhost:8888
```

### application.properties:

```properties
# Loaded AFTER config server properties
server.port=9083
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
```

> **Note:** In Spring Boot 3.x, you can use `spring.config.import=configserver:` instead of bootstrap.properties.

---

## Q30: How do you handle bulk operations in your APIs?

**Answer:**

Example from Inventory Service - bulk stock reduction:

```java
@PatchMapping("/bulk/reduce")
public ResponseEntity<BulkOperationResultDTO> reduceBulkStock(
        @Valid @RequestBody BulkStockReduceDTO request) {
    return ResponseEntity.ok(inventoryService.reduceBulkStock(request));
}
```

### DTO:

```java
public record BulkStockReduceDTO(
    @NotEmpty Map<Long, Integer> bookQuantities  // bookId -> quantity to reduce
) {}

public record BulkOperationResultDTO(
    int successCount,
    int failureCount,
    List<String> errors
) {}
```

### Service Implementation:

```java
@Transactional
public BulkOperationResultDTO reduceBulkStock(BulkStockReduceDTO request) {
    List<String> errors = new ArrayList<>();
    int success = 0;
    
    for (Map.Entry<Long, Integer> entry : request.bookQuantities().entrySet()) {
        try {
            reduceStock(entry.getKey(), entry.getValue());
            success++;
        } catch (Exception e) {
            errors.add("Book " + entry.getKey() + ": " + e.getMessage());
        }
    }
    
    return new BulkOperationResultDTO(success, errors.size(), errors);
}
```

---

## 🔴 HARD LEVEL QUESTIONS (Q31-Q40)

---

## Q31: How would you implement distributed transactions across services?

**Answer:**

Since each service has its own database, we use the **Saga Pattern** for distributed transactions:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SAGA PATTERN - CHOREOGRAPHY                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ORDER CREATION SAGA:                                                   │
│                                                                          │
│   Step 1: Create Order (PENDING)                                        │
│       ↓                                                                  │
│   Step 2: Reserve Inventory                                             │
│       ↓ (success)                                                        │
│   Step 3: Process Payment                                               │
│       ↓ (success)                                                        │
│   Step 4: Confirm Order (CONFIRMED)                                     │
│                                                                          │
│   COMPENSATING TRANSACTIONS (on failure):                               │
│                                                                          │
│   If Step 3 Fails:                                                      │
│       → Release Inventory (undo Step 2)                                 │
│       → Cancel Order (undo Step 1)                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Implementation Example:

```java
@Service
public class OrderSagaOrchestrator {
    
    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        Order order = null;
        boolean inventoryReserved = false;
        
        try {
            // Step 1: Create order in PENDING state
            order = orderService.createPendingOrder(dto);
            
            // Step 2: Reserve inventory
            inventoryClient.reserveStock(dto.getBookId(), dto.getQuantity());
            inventoryReserved = true;
            
            // Step 3: Process payment (if applicable)
            // paymentClient.processPayment(...);
            
            // Step 4: Confirm order
            order = orderService.confirmOrder(order.getId());
            
            return mapToResponse(order);
            
        } catch (Exception e) {
            // COMPENSATING TRANSACTIONS
            if (inventoryReserved) {
                inventoryClient.releaseStock(dto.getBookId(), dto.getQuantity());
            }
            if (order != null) {
                orderService.cancelOrder(order.getId());
            }
            throw new OrderCreationFailedException("Order saga failed: " + e.getMessage());
        }
    }
}
```

---

## Q32: How would you implement eventual consistency between services?

**Answer:**

For order placement, we implement eventual consistency using the **Outbox Pattern**:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       OUTBOX PATTERN                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ORDER SERVICE                                                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                                                                  │   │
│   │   1. @Transactional                                              │   │
│   │      - Save Order to orders table                                │   │
│   │      - Save Event to outbox table (same transaction)            │   │
│   │                                                                  │   │
│   │   ┌───────────────┐      ┌───────────────┐                      │   │
│   │   │    orders     │      │    outbox     │                      │   │
│   │   │   (table)     │      │   (table)     │                      │   │
│   │   └───────────────┘      └───────┬───────┘                      │   │
│   │                                   │                              │   │
│   └───────────────────────────────────┼──────────────────────────────┘   │
│                                       │                                  │
│   2. Outbox Poller (scheduled job)    │                                  │
│      - Reads unpublished events       ▼                                  │
│      - Publishes to message broker ────────▶  Kafka/RabbitMQ            │
│      - Marks event as published                    │                     │
│                                                    │                     │
│   3. Inventory Service consumes event              ▼                     │
│                                       ┌─────────────────────────┐       │
│                                       │   INVENTORY SERVICE     │       │
│                                       │  Reduces stock based    │       │
│                                       │  on order event         │       │
│                                       └─────────────────────────┘       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Outbox Table:

```sql
CREATE TABLE outbox (
    id BIGINT PRIMARY KEY,
    aggregate_type VARCHAR(255),  -- 'Order'
    aggregate_id BIGINT,          -- Order ID
    event_type VARCHAR(255),      -- 'OrderCreated'
    payload JSON,                  -- Event data
    created_at TIMESTAMP,
    published BOOLEAN DEFAULT FALSE
);
```

---

## Q33: How do you handle service discovery in Kubernetes (vs Eureka)?

**Answer:**

In Kubernetes, you can use **Kubernetes native service discovery** instead of Eureka:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    K8S vs EUREKA DISCOVERY                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WITH EUREKA:                                                           │
│   App registers with Eureka → Clients query Eureka → Client-side LB    │
│                                                                          │
│   WITH KUBERNETES:                                                       │
│   App deployed as Service → DNS resolves service name → K8s handles LB │
│                                                                          │
│   K8S Service YAML:                                                      │
│   apiVersion: v1                                                         │
│   kind: Service                                                          │
│   metadata:                                                              │
│     name: inventory-service                                              │
│   spec:                                                                  │
│     selector:                                                            │
│       app: inventory                                                     │
│     ports:                                                               │
│       - port: 9083                                                       │
│                                                                          │
│   FEIGN CLIENT CHANGE:                                                   │
│   @FeignClient(name = "inventory-service",                              │
│                url = "http://inventory-service:9083")                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q34: How would you implement rate limiting per user?

**Answer:**

Using Redis for distributed rate limiting:

```java
@Component
@RequiredArgsConstructor
public class UserRateLimiter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String userId, int maxRequests, int windowSeconds) {
        String key = "rate_limit:" + userId;
        Long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount == 1) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
        
        return currentCount <= maxRequests;
    }
}

// In Filter
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) {
        
        String userId = extractUserId(request);
        
        if (!rateLimiter.isAllowed(userId, 100, 60)) {  // 100 req/min
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        
        chain.doFilter(request, response);
    }
}
```

---

## Q35: How do you implement caching in your services?

**Answer:**

We use **Spring Cache** with **Redis**:

```java
@Service
@CacheConfig(cacheNames = "books")
public class BookServiceImpl implements BookService {
    
    @Cacheable(key = "#id")
    public BookResponseDTO findById(Long id) {
        return mapper.toResponseDTO(
            bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id)));
    }
    
    @CachePut(key = "#result.id")
    public BookResponseDTO create(BookCreateDTO dto) {
        return mapper.toResponseDTO(bookRepository.save(mapper.toEntity(dto)));
    }
    
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
    
    @CacheEvict(allEntries = true)
    @Scheduled(fixedRate = 3600000)  // Clear cache every hour
    public void clearCache() {
        log.info("Clearing book cache");
    }
}
```

### Configuration:

```properties
spring.cache.type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.redis.time-to-live=3600000  # 1 hour
```

---

## Q36: How would you implement API versioning?

**Answer:**

We use **URL path versioning**:

```java
@RestController
@RequestMapping("/api/v1/books")
public class BookControllerV1 {
    
    @GetMapping("/{id}")
    public BookResponseDTOV1 getBook(@PathVariable Long id) {
        return bookService.findByIdV1(id);
    }
}

@RestController
@RequestMapping("/api/v2/books")
public class BookControllerV2 {
    
    @GetMapping("/{id}")
    public BookResponseDTOV2 getBook(@PathVariable Long id) {
        // V2 might have different response structure
        return bookService.findByIdV2(id);
    }
}
```

### API Gateway Routing:

```properties
# Route v1 requests
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/books/**
spring.cloud.gateway.routes[0].uri=lb://book-service-v1

# Route v2 requests
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/v2/books/**
spring.cloud.gateway.routes[1].uri=lb://book-service-v2
```

---

## Q37: How do you handle data consistency when a service is temporarily down?

**Answer:**

We implement **retry with exponential backoff** and **dead letter queues**:

```java
@RetryableTopic(
    attempts = "4",
    backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000),
    dltStrategy = DltStrategy.FAIL_ON_ERROR
)
@KafkaListener(topics = "order-events")
public void processOrderEvent(OrderEvent event) {
    try {
        inventoryService.reduceStock(event.getBookId(), event.getQuantity());
    } catch (Exception e) {
        throw e;  // Will retry, eventually go to DLT
    }
}

// Dead Letter Topic handler
@KafkaListener(topics = "order-events-dlt")
public void handleFailedEvents(OrderEvent event) {
    log.error("Failed to process order event: {}", event);
    alertService.notifyOperations(event);
    // Store for manual review
    failedEventRepository.save(event);
}
```

---

## Q38: How would you implement request tracing across services?

**Answer:**

Using **Micrometer Tracing** (formerly Spring Cloud Sleuth):

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

### Configuration:

```properties
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
```

### Log Output with Trace:

```
2024-01-10 15:30:45 [traceId=abc123, spanId=def456] order-service - Creating order
2024-01-10 15:30:45 [traceId=abc123, spanId=ghi789] inventory-service - Checking stock
```

---

## Q39: How do you handle secret management in production?

**Answer:**

Using **HashiCorp Vault** or **AWS Secrets Manager**:

```java
// With Spring Cloud Vault
@Configuration
@EnableVaultRepositories
public class VaultConfig {
    // Secrets auto-loaded from Vault
}
```

### Configuration:

```properties
spring.cloud.vault.uri=https://vault.company.com
spring.cloud.vault.token=${VAULT_TOKEN}
spring.cloud.vault.kv.backend=secret
spring.cloud.vault.kv.application-name=inventory-service
```

### Secrets stored in Vault:

```
secret/inventory-service
├── spring.datasource.username=dbuser
├── spring.datasource.password=securepass123
└── jwt.secret=my-super-secret-key
```

---

## Q40: How would you design the system for high availability?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    HIGH AVAILABILITY ARCHITECTURE                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   LOAD BALANCER (AWS ALB / Nginx)                                       │
│           │                                                              │
│           ▼                                                              │
│   ┌───────────────────────────────────────────────────────────────┐     │
│   │         API GATEWAY CLUSTER (3 instances)                     │     │
│   │    ┌─────────┐    ┌─────────┐    ┌─────────┐                 │     │
│   │    │ Gateway │    │ Gateway │    │ Gateway │                 │     │
│   │    │   #1    │    │   #2    │    │   #3    │                 │     │
│   │    └─────────┘    └─────────┘    └─────────┘                 │     │
│   └───────────────────────────────────────────────────────────────┘     │
│           │                                                              │
│           ▼                                                              │
│   ┌───────────────────────────────────────────────────────────────┐     │
│   │         EUREKA CLUSTER (3 instances, peer awareness)          │     │
│   └───────────────────────────────────────────────────────────────┘     │
│           │                                                              │
│           ▼                                                              │
│   ┌───────────────────────────────────────────────────────────────┐     │
│   │         SERVICE INSTANCES (min 2 per service)                 │     │
│   │                                                                │     │
│   │   Inventory ×2    Order ×3    Book ×2    User ×2              │     │
│   └───────────────────────────────────────────────────────────────┘     │
│           │                                                              │
│           ▼                                                              │
│   ┌───────────────────────────────────────────────────────────────┐     │
│   │         DATABASE CLUSTER                                       │     │
│   │   ┌────────────┐        ┌────────────┐                        │     │
│   │   │   Primary  │ ─────▶ │  Replica   │  (Async replication)   │     │
│   │   │  (Write)   │        │  (Read)    │                        │     │
│   │   └────────────┘        └────────────┘                        │     │
│   └───────────────────────────────────────────────────────────────┘     │
│                                                                          │
│   KEY STRATEGIES:                                                        │
│   • Multiple instances of each service (min 2)                          │
│   • Eureka peer replication for registry HA                             │
│   • Database replication with read replicas                             │
│   • Redis cluster for distributed caching                               │
│   • Health checks and auto-scaling                                      │
│   • Multi-AZ deployment in cloud                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Summary

| Difficulty | Topics Covered |
|------------|---------------|
| **🟢 Beginner (Q11-Q18)** | Project structure, DTOs, validation, database, HTTP methods, documentation |
| **🟡 Intermediate (Q19-Q30)** | Service auth, transactions, pagination, mapping, testing, logging, profiles |
| **🔴 Hard (Q31-Q40)** | Distributed transactions, eventual consistency, K8s, rate limiting, caching, HA |

---

> **Next Topic:** Microservices Architecture (Generic Questions)
