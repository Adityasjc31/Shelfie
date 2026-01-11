# 🏗️ Topic 2: Microservices Architecture - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Microservices Architecture concepts and best practices.

---

## Q1: What are Microservices? How are they different from Monolithic Architecture?

**Answer:**

**Microservices** is an architectural style where an application is built as a collection of small, loosely coupled, independently deployable services. Each service focuses on a specific business capability.

### Comparison:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MONOLITHIC vs MICROSERVICES                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   MONOLITHIC                           MICROSERVICES                     │
│   ┌────────────────────────┐          ┌───────┐ ┌───────┐ ┌───────┐    │
│   │                        │          │ User  │ │ Order │ │ Book  │    │
│   │   ┌────────────────┐   │          │Service│ │Service│ │Service│    │
│   │   │   User Module  │   │          └───┬───┘ └───┬───┘ └───┬───┘    │
│   │   ├────────────────┤   │              │         │         │         │
│   │   │   Order Module │   │              └─────────┼─────────┘         │
│   │   ├────────────────┤   │                        │                   │
│   │   │   Book Module  │   │                  ┌─────┴─────┐             │
│   │   ├────────────────┤   │                  │  Network  │             │
│   │   │ Inventory Mod  │   │                  └───────────┘             │
│   │   └────────────────┘   │                                            │
│   │                        │          ┌───────┐ ┌───────┐ ┌───────┐    │
│   │   SINGLE DATABASE      │          │Invent.│ │Review │ │Payment│    │
│   │   ┌────────────────┐   │          │Service│ │Service│ │Service│    │
│   │   │  One Big DB    │   │          └───────┘ └───────┘ └───────┘    │
│   │   └────────────────┘   │               │         │         │        │
│   └────────────────────────┘               ▼         ▼         ▼        │
│                                       ┌──────┐  ┌──────┐  ┌──────┐     │
│   SINGLE DEPLOYMENT UNIT              │  DB  │  │  DB  │  │  DB  │     │
│                                       └──────┘  └──────┘  └──────┘     │
│                                       EACH SERVICE HAS OWN DB           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Detailed Comparison:

| Aspect | Monolithic | Microservices |
|--------|------------|---------------|
| **Deployment** | Single deployable unit | Multiple independent deployments |
| **Scaling** | Scale entire application | Scale individual services |
| **Technology** | Single tech stack | Polyglot (different tech per service) |
| **Database** | Shared single database | Database per service |
| **Team Structure** | Single large team | Small, autonomous teams |
| **Development** | Simple to develop initially | Complex to develop, easier to maintain |
| **Testing** | Simple end-to-end testing | Complex integration testing |
| **Failure Impact** | One bug can crash entire app | Failures are isolated |
| **Communication** | In-process method calls | Network calls (REST, gRPC, messaging) |

### Key Characteristics of Microservices:
1. **Single Responsibility** - Each service does one thing well
2. **Independently Deployable** - Deploy without affecting others
3. **Decentralized Data** - Each service owns its data
4. **Smart Endpoints, Dumb Pipes** - Logic in services, simple communication
5. **Designed for Failure** - Expect and handle failures gracefully

---

## Q2: What are the advantages and disadvantages of Microservices?

**Answer:**

### Advantages:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ADVANTAGES OF MICROSERVICES                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ✅ INDEPENDENT DEVELOPMENT & DEPLOYMENT                                 │
│     Teams can work on different services simultaneously                  │
│     Deploy updates without full system downtime                         │
│                                                                          │
│  ✅ TECHNOLOGY FLEXIBILITY (Polyglot)                                    │
│     User Service → Java + MySQL                                         │
│     Analytics Service → Python + MongoDB                                │
│     Real-time Service → Node.js + Redis                                 │
│                                                                          │
│  ✅ SCALABILITY                                                          │
│     Scale only what needs scaling                                       │
│     Example: Scale Order Service during Black Friday sales              │
│                                                                          │
│  ✅ FAULT ISOLATION                                                      │
│     If Review Service crashes, Users can still order books              │
│                                                                          │
│  ✅ EASIER MAINTENANCE                                                   │
│     Smaller codebases are easier to understand                          │
│     New developers onboard faster                                       │
│                                                                          │
│  ✅ ORGANIZATIONAL ALIGNMENT                                             │
│     Teams own services end-to-end (Conway's Law)                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Disadvantages:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                  DISADVANTAGES OF MICROSERVICES                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ❌ DISTRIBUTED SYSTEM COMPLEXITY                                        │
│     Network latency, partial failures, eventual consistency             │
│                                                                          │
│  ❌ OPERATIONAL OVERHEAD                                                 │
│     More services = more deployments, monitoring, logging               │
│     Need DevOps expertise and proper tooling                            │
│                                                                          │
│  ❌ DATA CONSISTENCY CHALLENGES                                          │
│     No ACID transactions across services                                │
│     Must use eventual consistency or Saga pattern                       │
│                                                                          │
│  ❌ TESTING COMPLEXITY                                                   │
│     Integration testing across services is hard                         │
│     Need contract testing, end-to-end testing                           │
│                                                                          │
│  ❌ DEBUGGING DIFFICULTY                                                 │
│     Tracing a request across 10 services is complex                     │
│     Need distributed tracing (Zipkin, Jaeger)                           │
│                                                                          │
│  ❌ INITIAL DEVELOPMENT OVERHEAD                                         │
│     More infrastructure setup (Eureka, Config, Gateway)                 │
│     Not suitable for simple applications                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### When to Use Microservices:
- ✅ Large, complex applications with distinct domains
- ✅ High scalability requirements
- ✅ Multiple teams working simultaneously
- ✅ Need for technology flexibility
- ✅ High availability is critical

### When NOT to Use Microservices:
- ❌ Small applications or MVPs
- ❌ Small team (< 5 developers)
- ❌ Simple business domain
- ❌ Tight deadlines without DevOps maturity
- ❌ No clear domain boundaries

---

## Q3: What is Service Discovery? Why is it needed?

**Answer:**

**Service Discovery** is a mechanism that allows services to find and communicate with each other dynamically, without hardcoding network locations.

### Why It's Needed:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    THE PROBLEM WITHOUT SERVICE DISCOVERY                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Without Service Discovery (WRONG ❌):                                  │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │  // Hardcoded URL - BAD!                                         │  │
│   │  private static final String INVENTORY_URL =                     │  │
│   │      "http://192.168.1.100:9083/api/inventory";                  │  │
│   │                                                                   │  │
│   │  // What if:                                                      │  │
│   │  // - IP address changes?                                         │  │
│   │  // - Service moves to different port?                            │  │
│   │  // - Multiple instances for load balancing?                      │  │
│   │  // - Instance goes down?                                         │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│   With Service Discovery (CORRECT ✅):                                   │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │  // Just use service name!                                       │  │
│   │  @FeignClient(name = "inventory-service")                        │  │
│   │  public interface InventoryClient { ... }                        │  │
│   │                                                                   │  │
│   │  // Service discovery handles:                                    │  │
│   │  // - Finding available instances                                 │  │
│   │  // - Load balancing                                              │  │
│   │  // - Failover on instance failure                                │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Types of Service Discovery:

#### 1. Client-Side Discovery (Eureka pattern):
```
┌─────────────┐      2. Get instances     ┌─────────────────┐
│  Order      │ ─────────────────────────▶│ Service Registry│
│  Service    │ ◀─────────────────────────│    (Eureka)     │
│             │   [inst1, inst2, inst3]   └─────────────────┘
└──────┬──────┘
       │ 3. Client picks one (load balancing)
       │
       ▼
┌─────────────┐
│  Inventory  │
│  Service    │
│  (inst2)    │
└─────────────┘
```
- Client is responsible for choosing an instance
- Client-side load balancing (Spring Cloud LoadBalancer)
- Used in: Netflix Eureka, Consul (client mode)

#### 2. Server-Side Discovery (Load Balancer pattern):
```
┌─────────────┐                           ┌─────────────────┐
│  Order      │ ─────────────────────────▶│  Load Balancer  │
│  Service    │                           │  (knows all     │
│             │                           │   instances)    │
└─────────────┘                           └───────┬─────────┘
                                                  │
                      ┌───────────────────────────┼─────────────────┐
                      ▼                           ▼                 ▼
               ┌─────────────┐            ┌─────────────┐    ┌─────────────┐
               │  Inventory  │            │  Inventory  │    │  Inventory  │
               │  (inst1)    │            │  (inst2)    │    │  (inst3)    │
               └─────────────┘            └─────────────┘    └─────────────┘
```
- Load balancer picks the instance
- Client doesn't know about individual instances
- Used in: Kubernetes, AWS ELB, Nginx

### Key Components:
| Component | Purpose |
|-----------|---------|
| **Service Registry** | Central database of all service instances |
| **Registration** | Services register on startup |
| **Heartbeat** | Services send periodic health signals |
| **Discovery** | Clients query registry for instances |
| **Health Check** | Registry removes unhealthy instances |

---

## Q4: What is an API Gateway? What are its responsibilities?

**Answer:**

An **API Gateway** is a single entry point for all client requests. It acts as a reverse proxy that routes requests to appropriate backend services.

### API Gateway Pattern:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         API GATEWAY PATTERN                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Without Gateway (PROBLEM):                                             │
│   ┌──────────┐                                                          │
│   │ Mobile   │───▶ User Service (https://users.api.com)                 │
│   │ App      │───▶ Order Service (https://orders.api.com)               │
│   │          │───▶ Book Service (https://books.api.com)                 │
│   └──────────┘     ❌ Client needs to know multiple endpoints            │
│                    ❌ Security handled at each service                   │
│                    ❌ No centralized logging/monitoring                  │
│                                                                          │
│   With Gateway (SOLUTION):                                               │
│   ┌──────────┐         ┌─────────────────────────────────────────────┐  │
│   │ Mobile   │  ──▶    │              API GATEWAY                    │  │
│   │ App      │         │  (https://api.bookstore.com)                │  │
│   └──────────┘         │                                              │  │
│                        │  ✅ Single Entry Point                       │  │
│   ┌──────────┐  ──▶    │  ✅ Centralized Authentication              │  │
│   │ Web App  │         │  ✅ Rate Limiting                           │  │
│   └──────────┘         │  ✅ Request Routing                         │  │
│                        │  ✅ Load Balancing                          │  │
│                        │  ✅ Logging & Monitoring                    │  │
│                        └──────────────────┬──────────────────────────┘  │
│                                           │                              │
│              ┌────────────────────────────┼────────────────────────┐    │
│              ▼                            ▼                        ▼    │
│        ┌──────────┐               ┌──────────┐              ┌──────────┐│
│        │ User     │               │ Order    │              │ Book     ││
│        │ Service  │               │ Service  │              │ Service  ││
│        └──────────┘               └──────────┘              └──────────┘│
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Core Responsibilities:

| Responsibility | Description |
|----------------|-------------|
| **Routing** | Routes `/api/users/**` to User Service, `/api/orders/**` to Order Service |
| **Authentication** | Validates JWT tokens before forwarding requests |
| **Rate Limiting** | Limits requests per second to prevent abuse |
| **Load Balancing** | Distributes requests across service instances |
| **Circuit Breaking** | Fails fast when downstream services are unhealthy |
| **Request/Response Transformation** | Modifies headers, aggregates responses |
| **CORS Handling** | Manages Cross-Origin Resource Sharing |
| **API Versioning** | Routes to different versions (`/v1/users`, `/v2/users`) |
| **Logging & Monitoring** | Centralized request logging and metrics |
| **SSL Termination** | Handles HTTPS at gateway, internal traffic is HTTP |

### Popular API Gateway Solutions:
- **Spring Cloud Gateway** (Spring ecosystem, reactive)
- **Netflix Zuul** (older, servlet-based)
- **Kong** (Open source, Nginx-based)
- **AWS API Gateway** (Managed service)
- **Nginx** / **HAProxy** (Traditional reverse proxies)

---

## Q5: What is the Circuit Breaker Pattern? Why is it important?

**Answer:**

**Circuit Breaker** is a design pattern that prevents cascade failures by stopping calls to a failing service temporarily.

### The Problem:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    WITHOUT CIRCUIT BREAKER (PROBLEM)                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Order Service         Inventory Service (DOWN!)                        │
│   ┌──────────────┐      ┌──────────────┐                                │
│   │              │ ──▶  │  💀 DEAD     │                                │
│   │  Thread 1    │ wait │              │  Timeout: 30 seconds           │
│   │              │ ...  └──────────────┘                                │
│   │              │                                                       │
│   │  Thread 2    │ ──▶  💀 wait...                                      │
│   │              │                                                       │
│   │  Thread 3    │ ──▶  💀 wait...                                      │
│   │              │                                                       │
│   │  ...         │                                                       │
│   │              │                                                       │
│   │  Thread 100  │ ──▶  💀 wait...                                      │
│   └──────────────┘                                                       │
│                                                                          │
│   RESULT: All threads exhausted → Order Service ALSO crashes!           │
│   This is called "CASCADE FAILURE"                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Circuit Breaker States:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CIRCUIT BREAKER STATE MACHINE                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                         ┌──────────────────┐                            │
│                         │                  │                            │
│                    ┌────┴────┐        failure threshold                 │
│      Success       │         │             exceeded                     │
│      ◀─────────────│ CLOSED  │─────────────────────▶                   │
│                    │(Normal) │                     │                    │
│                    └─────────┘                     │                    │
│                         ▲                          │                    │
│                         │                          ▼                    │
│                    success               ┌─────────────────┐            │
│                    threshold             │                 │            │
│                    met                   │      OPEN       │            │
│                         │                │ (Fail Fast)     │            │
│                         │                │                 │            │
│                    ┌────┴──────┐         └────────┬────────┘            │
│                    │           │                  │                     │
│                    │ HALF-OPEN │◀─────────────────┘                     │
│                    │   (Test)  │    wait duration                       │
│                    │           │      expires                           │
│                    └───────────┘                                        │
│                         │                                               │
│                    if test calls                                        │
│                    fail → back to OPEN                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### State Descriptions:

| State | Description | Behavior |
|-------|-------------|----------|
| **CLOSED** | Normal operation | All calls go through, failures tracked |
| **OPEN** | Circuit is tripped | All calls fail immediately (no waiting) |
| **HALF-OPEN** | Testing recovery | Limited test calls allowed |

### Implementation with Resilience4j:

```java
@Service
public class OrderServiceImpl {

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackCheckStock")
    public boolean checkStock(Long bookId, int quantity) {
        return inventoryClient.checkAvailability(bookId, quantity);
    }
    
    // Fallback method - called when circuit is OPEN
    public boolean fallbackCheckStock(Long bookId, int quantity, Exception ex) {
        log.warn("Circuit breaker active. Returning cached/default value.");
        return false; // Safe default
    }
}
```

### Configuration:
```properties
resilience4j.circuitbreaker.instances.inventoryService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.inventoryService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.inventoryService.waitDurationInOpenState=10000
resilience4j.circuitbreaker.instances.inventoryService.permittedNumberOfCallsInHalfOpenState=3
```

| Configuration | Value | Meaning |
|--------------|-------|---------|
| slidingWindowSize | 10 | Track last 10 calls |
| failureRateThreshold | 50 | Open if 50%+ fail |
| waitDurationInOpenState | 10s | Wait before testing |
| permittedNumberOfCallsInHalfOpenState | 3 | Test with 3 calls |

---

## Q6: How did you implement Feign Fallback handling in your project?

**Answer:**

In our Book Management project, we implemented **FallbackFactory** pattern for handling Feign client failures when the Order Service communicates with the Inventory Service. This provides graceful degradation and detailed error logging when downstream services are unavailable.

### Why FallbackFactory over Simple Fallback?

```
┌─────────────────────────────────────────────────────────────────────────┐
│           SIMPLE FALLBACK vs FALLBACK FACTORY                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SIMPLE FALLBACK (Limited):                                             │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │  @FeignClient(name = "inventory-service",                        │  │
│   │               fallback = InventoryFallback.class)                │  │
│   │                                                                   │  │
│   │  // Problem: No access to the CAUSE of the failure!              │  │
│   │  public class InventoryFallback implements InventoryClient {     │  │
│   │      public void reduceStock(...) {                              │  │
│   │          // ❌ We don't know WHY it failed                        │  │
│   │          //    Was it timeout? Connection refused? 500 error?    │  │
│   │      }                                                            │  │
│   │  }                                                                │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│   FALLBACK FACTORY (Better - What we use):                               │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │  @FeignClient(name = "inventory-service",                        │  │
│   │               fallbackFactory = InventoryClientFallbackFactory)  │  │
│   │                                                                   │  │
│   │  // ✅ We get the Throwable cause - can log and handle properly  │  │
│   │  public class InventoryFallbackFactory                           │  │
│   │          implements FallbackFactory<InventoryClient> {           │  │
│   │      public InventoryClient create(Throwable cause) { ... }      │  │
│   │  }                                                                │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Our Implementation:

#### 1. Feign Client Interface (`InventoryServiceClient.java`):

```java
@FeignClient(
    name = "inventory-service", 
    path = "/api/v1/inventory", 
    fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryServiceClient {

    @PatchMapping(value = "/bulk/reduce", consumes = MediaType.APPLICATION_JSON_VALUE)
    void reduceStock(@RequestBody ReduceInventoryStockRequestDTO request);
}
```

**Key Points:**
- `name = "inventory-service"` → Resolves via Eureka Service Discovery
- `path = "/api/v1/inventory"` → Base path for all endpoints
- `fallbackFactory` → Links to our custom fallback factory

#### 2. Fallback Factory Implementation (`InventoryClientFallbackFactory.java`):

```java
@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {

    @Override
    public InventoryServiceClient create(Throwable cause) {
        return new InventoryServiceClient() {
            @Override
            public void reduceStock(ReduceInventoryStockRequestDTO request) {
                // Log at ERROR level for monitoring/alerting
                log.error("CRITICAL: Inventory Service call failed for items: {} | Cause: {} - {}",
                        request.getBookQuantities(),
                        cause.getClass().getSimpleName(),
                        cause.getMessage());

                // Log full stack trace at DEBUG level for debugging
                log.debug("Full exception details:", cause);

                // Throw custom exception to propagate failure to caller
                throw new OrderNotPlacedException(
                        "Inventory Service unavailable: " + cause.getMessage());
            }
        };
    }
}
```

### Flow Visualization:

```
┌─────────────────────────────────────────────────────────────────────────┐
│           ORDER SERVICE → INVENTORY SERVICE FLOW                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   HAPPY PATH ✅:                                                          │
│   ┌────────────┐        ┌─────────────────────┐        ┌─────────────┐  │
│   │   Order    │ ──────▶│   Feign Client      │ ──────▶│  Inventory  │  │
│   │   Service  │        │ (inventory-service) │        │  Service    │  │
│   └────────────┘        └─────────────────────┘        └──────┬──────┘  │
│                                                               │         │
│                         Response: 200 OK  ◀───────────────────┘         │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   FAILURE PATH ❌:                                                        │
│   ┌────────────┐        ┌─────────────────────┐        ┌─────────────┐  │
│   │   Order    │ ──────▶│   Feign Client      │ ───X──▶│  Inventory  │  │
│   │   Service  │        │                     │        │  (DOWN!)    │  │
│   └────────────┘        └──────────┬──────────┘        └─────────────┘  │
│         ▲                          │                                     │
│         │                          ▼                                     │
│         │               ┌──────────────────────────────┐                │
│         │               │  FallbackFactory.create()    │                │
│         │               │  - Receives: Throwable cause │                │
│         │               │  - Logs: Error + Debug       │                │
│         │               │  - Throws: OrderNotPlaced    │                │
│         │               └──────────────┬───────────────┘                │
│         │                              │                                 │
│         └──────────────────────────────┘                                 │
│                    OrderNotPlacedException                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Why We Chose This Approach:

| Decision | Reasoning |
|----------|-----------|
| **FallbackFactory over Fallback** | Access to `Throwable cause` enables proper error logging |
| **Log at ERROR level** | Critical failures should trigger monitoring alerts |
| **Log at DEBUG level** | Full stack trace available for debugging without cluttering logs |
| **Throw Custom Exception** | Propagate failure to caller with meaningful message |
| **Include request details in log** | Helps identify which order/books caused the failure |

### Configuration Required:

```properties
# Enable Feign clients
spring.cloud.openfeign.enabled=true

# Enable fallbacks (required for fallbackFactory to work)
spring.cloud.openfeign.circuitbreaker.enabled=true
```

### Interview Follow-up Questions:

1. **Why not return a cached value instead of throwing an exception?**
   - In our case, failing silently would cause data inconsistency (order placed but inventory not reduced)
   
2. **Did you combine this with Circuit Breaker?**
   - Yes! We have Resilience4j Circuit Breakers configured in `api-gateway.properties`:
   ```properties
   resilience4j.circuitbreaker.instances.inventoryCircuitBreaker.base-config=default
   resilience4j.timelimiter.instances.inventoryCircuitBreaker.base-config=default
   ```
   - The FallbackFactory handles graceful degradation when the circuit is open

3. **Do you use any retry mechanism before falling back?**
   - Yes! We have retry configured at the gateway level in `api-gateway.properties`:
   ```properties
   gateway.retry.max-attempts=3
   gateway.retry.backoff-multiplier=2
   ```

---

## Q7: What is the difference between synchronous and asynchronous communication?

**Answer:**

### Synchronous Communication:
The caller **waits** for the response before continuing.

### Asynchronous Communication:
The caller **doesn't wait** - sends message and continues.

### Visual Comparison:

```
┌─────────────────────────────────────────────────────────────────────────┐
│              SYNCHRONOUS vs ASYNCHRONOUS COMMUNICATION                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SYNCHRONOUS (REST/HTTP):                                               │
│   ─────────────────────────                                              │
│   Order         Inventory                                                │
│   Service       Service                                                  │
│      │                │                                                  │
│      │──── Request ──▶│                                                  │
│      │   checkStock() │                                                  │
│      │                │ ⏳ Processing...                                 │
│      │  (WAITING)     │                                                  │
│      │                │                                                  │
│      │◀── Response ───│                                                  │
│      │   {available}  │                                                  │
│      │                │                                                  │
│      ▼ (continues)    │                                                  │
│                                                                          │
│   Properties:                                                            │
│   ✅ Simple to implement                                                 │
│   ✅ Immediate response                                                  │
│   ❌ Creates temporal coupling                                           │
│   ❌ Caller blocked if service is slow/down                              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   ASYNCHRONOUS (Message Queue):                                          │
│   ─────────────────────────────                                          │
│   Order        Message         Inventory      Notification               │
│   Service      Queue           Service        Service                    │
│      │            │                │                │                    │
│      │── Publish ▶│                │                │                    │
│      │   ORDER_   │                │                │                    │
│      │   CREATED  │                │                │                    │
│      │            │                │                │                    │
│      ▼ (continues │◀── Consume ────│                │                    │
│       immediately)│    ORDER_      │                │                    │
│                   │    CREATED     ▼ (Process)      │                    │
│                   │                │                │                    │
│                   │ ◀── Publish ───│                │                    │
│                   │    STOCK_      │                │                    │
│                   │    RESERVED    │── Consume ────▶│                    │
│                   │                                 ▼ (Send email)       │
│                                                                          │
│   Properties:                                                            │
│   ✅ Decoupled services                                                  │
│   ✅ Better scalability                                                  │
│   ✅ Resilient to failures                                               │
│   ❌ More complex                                                        │
│   ❌ Eventual consistency                                                │
│   ❌ Harder to debug                                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### When to Use Each:

| Use Case | Communication Type | Reason |
|----------|-------------------|--------|
| User login | Synchronous | Need immediate response |
| Place order | Synchronous | User waits for confirmation |
| Send email notification | Asynchronous | User doesn't need to wait |
| Update analytics | Asynchronous | Background processing |
| Check inventory before order | Synchronous | Critical for business logic |
| Generate report | Asynchronous | Long-running task |

### Popular Technologies:

| Type | Technologies |
|------|-------------|
| **Synchronous** | REST (HTTP), gRPC, GraphQL |
| **Asynchronous** | RabbitMQ, Apache Kafka, AWS SQS, Redis Pub/Sub |

---

## Q8: What is the Database per Service pattern?

**Answer:**

**Database per Service** means each microservice has its own private database that only it can access directly.

### Pattern Visualization:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                   DATABASE PER SERVICE PATTERN                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WRONG: Shared Database (Anti-pattern) ❌                               │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │  │
│   │   │  User    │  │  Order   │  │ Inventory│  │  Book    │        │  │
│   │   │ Service  │  │ Service  │  │ Service  │  │ Service  │        │  │
│   │   └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘        │  │
│   │        │             │             │             │               │  │
│   │        └─────────────┴─────────────┴─────────────┘               │  │
│   │                          │                                        │  │
│   │                    ┌─────┴─────┐                                 │  │
│   │                    │  SHARED   │  ❌ Tight coupling               │  │
│   │                    │  DATABASE │  ❌ Schema changes affect all    │  │
│   │                    │           │  ❌ Single point of failure      │  │
│   │                    └───────────┘  ❌ Cannot scale independently   │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│   CORRECT: Database per Service ✅                                       │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │  │
│   │   │  User    │  │  Order   │  │ Inventory│  │  Book    │        │  │
│   │   │ Service  │  │ Service  │  │ Service  │  │ Service  │        │  │
│   │   └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘        │  │
│   │        │             │             │             │               │  │
│   │   ┌────┴────┐   ┌────┴────┐   ┌────┴────┐   ┌────┴────┐         │  │
│   │   │ User DB │   │Order DB │   │Inven DB │   │ Book DB │         │  │
│   │   │ (MySQL) │   │(Postgres)│  │ (Redis) │   │ (Mongo) │         │  │
│   │   └─────────┘   └─────────┘   └─────────┘   └─────────┘         │  │
│   │                                                                   │  │
│   │   ✅ Loose coupling          ✅ Independent scaling               │  │
│   │   ✅ Technology freedom      ✅ Isolated failures                 │  │
│   │   ✅ Independent deployments ✅ Schema freedom                    │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Rules:

1. **No direct database access** - Services must use APIs, not direct SQL
2. **Data duplication is OK** - Services can store copies of data they need
3. **Eventual consistency** - Accept that data may be temporarily out of sync
4. **API for data sharing** - Need user data? Call User Service API

### Challenges and Solutions:

| Challenge | Solution |
|-----------|----------|
| Need data from another service | Call that service's API |
| Join queries across services | API Composition, CQRS |
| Distributed transactions | Saga Pattern |
| Data consistency | Event-driven updates, eventual consistency |
| Reporting across all data | Separate reporting database with data sync |

---

## Q9: What is CQRS (Command Query Responsibility Segregation)?

> ⚠️ **Not Implemented in Our Project**: Our Book Management project uses the traditional approach (same model for read & write). CQRS was not needed given our project's scope and complexity.

**Answer:**

**CQRS** separates read operations (Queries) from write operations (Commands) into different models or even different databases.

### Traditional vs CQRS:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    TRADITIONAL vs CQRS                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   TRADITIONAL (Same model for read & write):                             │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │                                                                   │  │
│   │   ┌────────────────────────────────────────────┐                 │  │
│   │   │            Book Service                    │                 │  │
│   │   │                                            │                 │  │
│   │   │   Create Book ───┐      ┌─── Get Book     │                 │  │
│   │   │   Update Book ───┼──────┼─── Get All      │                 │  │
│   │   │   Delete Book ───┘      └─── Search       │                 │  │
│   │   │                    │                       │                 │  │
│   │   │              ┌─────┴─────┐                 │                 │  │
│   │   │              │Same Model │                 │                 │  │
│   │   │              │ Same DB   │                 │                 │  │
│   │   │              └───────────┘                 │                 │  │
│   │   └────────────────────────────────────────────┘                 │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│   CQRS (Separate models for read & write):                               │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │                                                                   │  │
│   │   COMMAND SIDE (Write)           QUERY SIDE (Read)               │  │
│   │   ┌────────────────────┐         ┌────────────────────┐          │  │
│   │   │ Create Book        │         │ Get Book           │          │  │
│   │   │ Update Book        │         │ Get All Books      │          │  │
│   │   │ Delete Book        │         │ Search Books       │          │  │
│   │   │                    │         │ Get by Category    │          │  │
│   │   │ ┌────────────────┐ │  Event  │ ┌────────────────┐ │          │  │
│   │   │ │ Write Model    │ │ ──────▶ │ │ Read Model     │ │          │  │
│   │   │ │ (Normalized)   │ │  Sync   │ │ (Denormalized) │ │          │  │
│   │   │ └───────┬────────┘ │         │ └───────┬────────┘ │          │  │
│   │   │         │          │         │         │          │          │  │
│   │   │   ┌─────┴─────┐    │         │   ┌─────┴─────┐    │          │  │
│   │   │   │ Write DB  │    │         │   │ Read DB   │    │          │  │
│   │   │   │ (MySQL)   │    │         │   │(Elastic)  │    │          │  │
│   │   │   └───────────┘    │         │   └───────────┘    │          │  │
│   │   └────────────────────┘         └────────────────────┘          │  │
│   │                                                                   │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Benefits:

| Benefit | Explanation |
|---------|-------------|
| **Optimized Queries** | Read model can be denormalized for fast queries |
| **Scalability** | Scale read and write sides independently |
| **Flexibility** | Use different databases (SQL for write, Elasticsearch for read) |
| **Complex Queries** | Read model designed for specific query patterns |
| **Event Sourcing Compatible** | Works well with event-driven architectures |

### When to Use CQRS:
- ✅ Read-heavy applications (e.g., e-commerce product catalog)
- ✅ Complex domain with different read/write patterns
- ✅ Need for specialized read databases (search, analytics)
- ✅ High scalability requirements

### When NOT to Use:
- ❌ Simple CRUD applications
- ❌ Real-time consistency requirements
- ❌ Small teams without event-driven experience

---

## Q10: How do you handle distributed tracing in microservices?

> ⚠️ **Not Implemented in Our Project**: Our Book Management project does not use distributed tracing tools like Zipkin or Jaeger. We rely on centralized logging for debugging.

**Answer:**

**Distributed Tracing** tracks a request as it flows through multiple microservices, helping debug and monitor complex systems.

### The Problem:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                 THE DEBUGGING NIGHTMARE                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   User complains: "My order took 10 seconds!"                           │
│                                                                          │
│   Request flow:                                                          │
│   Client → Gateway → Order → Inventory → Book → Payment → Notification  │
│                                                                          │
│   Questions:                                                             │
│   - Which service was slow?                                             │
│   - How long did each service take?                                     │
│   - Were there any retries?                                             │
│   - What was the exact path?                                            │
│                                                                          │
│   Without tracing: 🤷 Check logs of 7 services manually                 │
│   With tracing: 📊 See entire flow in one dashboard                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### How Distributed Tracing Works:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                 DISTRIBUTED TRACING CONCEPT                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   TRACE ID: abc123 (Identifies entire request journey)                  │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │                                                                     ││
│   │  SPAN: API Gateway (ID: span1, Parent: none)                       ││
│   │  ├── Duration: 50ms                                                 ││
│   │  │                                                                  ││
│   │  └── SPAN: Order Service (ID: span2, Parent: span1)                ││
│   │      ├── Duration: 200ms                                            ││
│   │      │                                                              ││
│   │      ├── SPAN: Inventory Service (ID: span3, Parent: span2)        ││
│   │      │   └── Duration: 80ms                                         ││
│   │      │                                                              ││
│   │      └── SPAN: Payment Service (ID: span4, Parent: span2)          ││
│   │          └── Duration: 120ms  ◀── SLOWEST!                         ││
│   │                                                                     ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   KEY TERMS:                                                             │
│   - Trace: The entire journey of a request                              │
│   - Span: A single operation within a trace                             │
│   - Trace ID: Unique identifier for the entire trace                    │
│   - Span ID: Unique identifier for each span                            │
│   - Parent Span ID: Links child span to parent                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Implementation with Spring Cloud Sleuth + Zipkin:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

```properties
# application.properties
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
```

### What You See in Zipkin Dashboard:

```
┌─────────────────────────────────────────────────────────────────────────┐
│  TRACE: abc123                               Total Duration: 450ms      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  api-gateway     ████████████████████████████████████████████  450ms    │
│                                                                          │
│    order-service     ████████████████████████████████████  400ms        │
│                                                                          │
│      inventory-svc       ████████████  80ms                             │
│                                                                          │
│      payment-svc             ████████████████████  120ms  ← INVESTIGATE │
│                                                                          │
│      notification-svc                             ████  15ms            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Popular Tracing Tools:

| Tool | Description |
|------|-------------|
| **Zipkin** | Open source, simple setup |
| **Jaeger** | Open source by Uber, more features |
| **AWS X-Ray** | AWS managed service |
| **Datadog APM** | Commercial, full-featured |
| **New Relic** | Commercial, enterprise |

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Microservices** | Small, independent services with single responsibility |
| **Service Discovery** | Dynamic service location (Eureka) |
| **API Gateway** | Single entry point for clients |
| **Circuit Breaker** | Prevents cascade failures |
| **Saga Pattern** | Distributed transaction management |
| **Sync vs Async** | REST for immediate response, MQ for decoupling |
| **Database per Service** | Each service owns its data |
| **CQRS** | Separate read/write models |
| **Distributed Tracing** | Track requests across services |

---

---

## 🟢 Beginner Level Questions (Q11-Q20)

---

## Q11: What is loose coupling in microservices?

**Answer:**
Loose coupling means services are independent and changes in one service don't require changes in others. Services communicate via well-defined APIs, not shared databases or internal implementation details.

**Key aspects:**
- Services don't share databases
- Communication through APIs (REST, gRPC, messaging)
- Each service can be deployed independently
- Technology choices are independent per service

---

## Q12: What is the role of Eureka Server in microservices?

**Answer:**
Eureka Server is a **service registry** that maintains a list of all available service instances. Services register themselves on startup and send periodic heartbeats.

**How it works:**
1. Service starts → registers with Eureka
2. Service sends heartbeats every 30 seconds
3. Client queries Eureka to find service instances
4. Eureka removes services that miss heartbeats

```java
@EnableEurekaServer  // On Eureka Server
@EnableDiscoveryClient  // On client services
```

---

## Q13: What is the difference between Eureka and Consul?

**Answer:**

| Feature | Eureka | Consul |
|---------|--------|--------|
| **Created by** | Netflix | HashiCorp |
| **Health Check** | Client heartbeat | Agent-based |
| **Consistency** | AP (Available, Partition tolerant) | CP (Consistent, Partition tolerant) |
| **Key-Value Store** | No | Yes |
| **Multi-datacenter** | Limited | Yes |
| **Language** | Java | Go |

---

## Q14: What is a Config Server and why is it needed?

**Answer:**
A **Config Server** centralizes configuration management for all microservices. Instead of each service having its own properties file, configurations are stored in a central location (Git, file system).

**Benefits:**
- Single source of truth for configuration
- Environment-specific configs (dev, staging, prod)
- Dynamic configuration updates without restart
- Secure storage for sensitive data

---

## Q15: What is the difference between @RestController and @Controller?

**Answer:**

| Annotation | Purpose |
|------------|---------|
| `@Controller` | Returns view names (HTML pages) |
| `@RestController` | Returns data directly (JSON/XML) |

`@RestController` = `@Controller` + `@ResponseBody`

```java
@RestController
public class BookController {
    @GetMapping("/books")
    public List<Book> getBooks() {  // Returns JSON directly
        return bookService.findAll();
    }
}
```

---

## Q16: What is Feign Client and how does it simplify service communication?

**Answer:**
**Feign** is a declarative HTTP client that makes calling other services look like calling local methods.

**Without Feign (Manual RestTemplate):**
```java
ResponseEntity<Inventory> response = restTemplate.getForEntity(
    "http://inventory-service/api/inventory/" + bookId, Inventory.class);
```

**With Feign (Declarative):**
```java
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/inventory/{bookId}")
    InventoryResponse getInventory(@PathVariable Long bookId);
}
```

---

## Q17: What is Load Balancing in microservices?

**Answer:**
Load balancing distributes incoming requests across multiple service instances to ensure no single instance is overwhelmed.

**Types:**
1. **Client-side** (Spring Cloud LoadBalancer): Client chooses instance
2. **Server-side** (Nginx, AWS ELB): Load balancer chooses instance

**Algorithms:**
- Round Robin (default)
- Random
- Weighted
- Least Connections

---

## Q18: What are Health Checks and Actuator Endpoints?

> ✅ **Implemented in Our Project**: All services have Actuator configured with health, info, metrics, and prometheus endpoints exposed. See `api-gateway.properties` for complete configuration.

**Answer:**
**Health checks** verify if a service is running and can handle requests.

**Spring Boot Actuator** provides built-in endpoints:

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Service health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics |
| `/actuator/env` | Environment properties |

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

---

## Q19: What is the Strangler Fig Pattern?

**Answer:**
A migration pattern for gradually replacing a monolithic application with microservices.

**Steps:**
1. Create new microservice for one feature
2. Route traffic for that feature to new service
3. Keep old monolith running for other features
4. Repeat until monolith is fully replaced
5. Decommission the monolith

**Why "Strangler Fig"?** Named after the vine that grows around a tree and eventually replaces it.

---

## Q20: What is API Versioning and what are common strategies?

**Answer:**
API versioning allows you to make breaking changes without affecting existing clients.

**Strategies:**

| Strategy | Example |
|----------|---------|
| **URI Path** | `/api/v1/books`, `/api/v2/books` |
| **Query Param** | `/api/books?version=1` |
| **Header** | `X-API-Version: 1` |
| **Media Type** | `Accept: application/vnd.api.v1+json` |

**Most common:** URI Path versioning for simplicity.

---

## 🟡 Intermediate Level Questions (Q21-Q30)

---

## Q21: What is Spring Cloud LoadBalancer and how does it work?

**Answer:**

**Spring Cloud LoadBalancer** is a client-side load balancing library that distributes requests across multiple service instances.

### 🎯 Simple Analogy: Restaurant Host

Like a restaurant host who directs customers to available tables:
- **LoadBalancer** = Host at reception
- **Service Instances** = Available tables
- **Requests** = Customers waiting to be seated

### How It Works

```
┌─────────────────────────────────────────────────────────────────────┐
│                  CLIENT-SIDE LOAD BALANCING                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  Order Service                        Inventory Service Instances   │
│  ┌─────────────────┐                  ┌──────────────────────┐     │
│  │ @FeignClient    │                  │ Instance 1 (8081)    │     │
│  │ with            │  ───Round───▶    ├──────────────────────┤     │
│  │ LoadBalancer    │  ───Robin───▶    │ Instance 2 (8082)    │     │
│  │                 │  ───────────▶    ├──────────────────────┤     │
│  └─────────────────┘                  │ Instance 3 (8083)    │     │
│                                       └──────────────────────┘     │
└─────────────────────────────────────────────────────────────────────┘
```

### Load Balancing Strategies

| Strategy | Description |
|----------|-------------|
| **Round Robin** | Cycles through instances sequentially (default) |
| **Random** | Picks a random instance |
| **Weighted** | More requests to healthier/faster instances |

### Configuration

```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false  # Use Spring Cloud LoadBalancer instead of Ribbon
```

**With Feign Client:**
```java
@FeignClient(name = "inventory-service")  // LoadBalancer enabled by default
public interface InventoryServiceClient {
    @GetMapping("/api/v1/inventory/{bookId}")
    InventoryResponse getInventory(@PathVariable Long bookId);
}
```

---

## Q22: What is the Bulkhead Pattern?

**Answer:**
**Bulkhead** isolates failures by partitioning resources (like ship compartments).

```
┌─────────────────────────────────────────┐
│  Thread Pool for Service A (10 threads) │  ← If A fails, only these affected
├─────────────────────────────────────────┤
│  Thread Pool for Service B (10 threads) │  ← B continues working
├─────────────────────────────────────────┤
│  Thread Pool for Service C (10 threads) │  ← C continues working
└─────────────────────────────────────────┘
```

**Implementation with Resilience4j:**
```java
@Bulkhead(name = "inventoryService", type = Bulkhead.Type.THREADPOOL)
public InventoryResponse checkStock(Long bookId) { ... }
```

---

## Q23: What is the Retry Pattern?

**Answer:**
Automatically retry failed operations, useful for transient failures (network glitches).

```java
@Retry(name = "inventoryService", fallbackMethod = "fallback")
public InventoryResponse getInventory(Long bookId) {
    return inventoryClient.getInventory(bookId);
}
```

```properties
resilience4j.retry.instances.inventoryService.maxAttempts=3
resilience4j.retry.instances.inventoryService.waitDuration=1000
resilience4j.retry.instances.inventoryService.retryExceptions=java.io.IOException
```

**Best practices:**
- Use exponential backoff
- Set maximum retry attempts
- Only retry on transient failures

---

## Q24: What is Rate Limiting and why is it important?

**Answer:**
**Rate limiting** restricts the number of requests a client can make in a time period.

**Why needed:**
- Prevent abuse and DoS attacks
- Fair resource distribution
- Protect backend services

**Common algorithms:**
- Token Bucket
- Leaky Bucket
- Fixed Window
- Sliding Window

```yaml
# Spring Cloud Gateway
spring:
  cloud:
    gateway:
      routes:
        - id: book-service
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

---

## Q25: How do you configure Feign Client for microservices communication?

**Answer:**

**OpenFeign** is a declarative REST client that simplifies HTTP API calls between microservices.

### 🎯 Simple Analogy: Phone Contact List

Feign is like your phone's contact list:
- Instead of dialing numbers manually, you just tap a name
- Feign abstracts away HTTP details - you just call a method

### Configuration Steps

**1. Add Dependency:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

**2. Enable Feign:**
```java
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication { }
```

**3. Define Client Interface:**
```java
@FeignClient(
    name = "inventory-service",
    fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryServiceClient {
    @GetMapping("/api/v1/inventory/{bookId}")
    InventoryResponse getInventory(@PathVariable Long bookId);
}
```

### Key Properties

```properties
# Enable circuit breaker for fallbacks
spring.cloud.openfeign.circuitbreaker.enabled=true

# Connection timeouts
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=5000
```

---

## Q26: How do Gateway Filters work in Spring Cloud Gateway?

**Answer:**

**Gateway Filters** modify requests and responses as they pass through the API Gateway.

### Types of Filters

| Filter Type | When It Runs | Use Case |
|-------------|--------------|----------|
| **Pre-filters** | Before routing | Authentication, logging, adding headers |
| **Post-filters** | After response | Response modification, metrics |

### Our Project's Filters

```java
// AuthenticationFilter - Pre-filter
@Component
public class AuthenticationFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Extract JWT from Authorization header
        // 2. Validate token
        // 3. Add user info to headers
        // 4. Forward to downstream service
        return chain.filter(exchange);
    }
}
```

### Common Built-in Filters

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: book-service
          filters:
            - AddRequestHeader=X-Gateway-Secret, ${gateway.secret}
            - RewritePath=/api/(?<segment>.*), /$\{segment}
            - name: CircuitBreaker
              args:
                name: bookCircuitBreaker
                fallbackUri: forward:/fallback
```

---

## Q27: What are RESTful API best practices for microservices?

**Answer:**

### Key Best Practices

| Practice | Example |
|----------|---------|
| **Use Nouns for Endpoints** | `/api/v1/books` (not `/api/v1/getBooks`) |
| **Use HTTP Methods Correctly** | GET=read, POST=create, PUT=update, DELETE=remove |
| **Version Your APIs** | `/api/v1/books`, `/api/v2/books` |
| **Use Proper Status Codes** | 200=OK, 201=Created, 400=Bad Request, 404=Not Found |
| **Implement Pagination** | `/api/v1/books?page=0&size=10` |

### Our Project's API Design

```java
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @GetMapping          // GET /api/v1/books - list all
    @GetMapping("/{id}") // GET /api/v1/books/123 - get one
    @PostMapping         // POST /api/v1/books - create
    @PutMapping("/{id}") // PUT /api/v1/books/123 - update
    @DeleteMapping("/{id}") // DELETE /api/v1/books/123 - delete
}
```

### Response Structure

```json
{
    "status": "success",
    "data": { ... },
    "message": "Book retrieved successfully",
    "timestamp": "2024-01-15T10:30:00"
}
```

---

## Q28: How do you document APIs with OpenAPI/Swagger?

**Answer:**

**OpenAPI** (Swagger) provides interactive API documentation for your microservices.

### Configuration

```java
@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Book Service API")
                .version("1.0")
                .description("API for managing books"));
    }
}
```

### Adding Documentation to Controllers

```java
@Tag(name = "Books", description = "Book management APIs")
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @Operation(summary = "Get all books")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List retrieved"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public List<BookDTO> getAllBooks() { ... }
}
```

### Access Swagger UI

`http://localhost:8081/swagger-ui.html`

---

## Q29: How do you configure request routing in API Gateway?

**Answer:**

**Spring Cloud Gateway** routes requests to appropriate microservices based on path patterns.

### Route Configuration

```properties
# Book Service Routes
spring.cloud.gateway.routes[0].id=book-service
spring.cloud.gateway.routes[0].uri=lb://book-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/books/**

# Order Service Routes
spring.cloud.gateway.routes[1].id=order-service
spring.cloud.gateway.routes[1].uri=lb://order-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/v1/orders/**

# Inventory Service Routes
spring.cloud.gateway.routes[2].id=inventory-service
spring.cloud.gateway.routes[2].uri=lb://inventory-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/v1/inventory/**
```

### How lb:// Works

`lb://book-service` means:
- **lb** = LoadBalancer (route through service discovery)
- **book-service** = Eureka service name

The Gateway fetches available instances from Eureka and load-balances between them.

---

## Q30: How do you secure microservices communication?

> ✅ **Implemented in Our Project**: We use a Gateway Secret Token pattern (`X-Gateway-Secret` header) to ensure all requests pass through the API Gateway. See Q51-Q56 for detailed project-specific implementations.

**Answer:**

### 🎯 Simple Analogy

Think of microservices security like **airport security**:
- **External Authentication (JWT)** = Your boarding pass - proves you can enter
- **API Gateway** = Airport entrance checkpoint - validates everyone
- **Service-to-Service Security (mTLS/Secret Token)** = Staff-only areas require special ID badges
- **Data Encryption** = Luggage screening - contents are protected

---

### Security Layers Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                    MICROSERVICES SECURITY LAYERS                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────┐                                                    │
│  │   Client    │                                                    │
│  └──────┬──────┘                                                    │
│         │ HTTPS + JWT                                               │
│         ▼                                                           │
│  ┌────────────────────────────────────────┐                         │
│  │           API GATEWAY                   │  Layer 1: Edge        │
│  │  • JWT Validation                       │           Security    │
│  │  • Rate Limiting                        │                        │
│  │  • IP Whitelisting                      │                        │
│  │  • Adds X-Gateway-Secret header         │                        │
│  └────────────────────┬───────────────────┘                         │
│                       │                                              │
│         ┌─────────────┼─────────────┐                               │
│         ▼             ▼             ▼                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐   Layer 2: Service       │
│  │ Service  │  │ Service  │  │ Service  │            Security      │
│  │    A     │◄─┤    B     │◄─┤    C     │                          │
│  └──────────┘  └──────────┘  └──────────┘                          │
│       │             │             │                                  │
│       ▼             ▼             ▼                                  │
│  ┌─────────────────────────────────────┐   Layer 3: Data           │
│  │        Encrypted Database           │            Security        │
│  └─────────────────────────────────────┘                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

### Layer 1: API Gateway Security (Edge Security)

| Technique | Purpose | Implementation |
|-----------|---------|----------------|
| **JWT Validation** | Verify user identity | Spring Security OAuth2 Resource Server |
| **Rate Limiting** | Prevent DDoS/abuse | Resilience4j RateLimiter |
| **IP Whitelisting** | Restrict access | Gateway filters |
| **HTTPS** | Encrypt traffic | TLS certificates |

```properties
# JWT validation in API Gateway
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://auth-server
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://auth-server/.well-known/jwks.json
```

---

### Layer 2: Service-to-Service Security

#### Option A: Gateway Secret Token (Our Approach)

```
┌─────────────────────────────────────────────────────────────────┐
│                    GATEWAY SECRET PATTERN                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ✓ VALID: Request through gateway                              │
│   Client → Gateway (adds secret) → Service ✓                    │
│                                                                  │
│   ✗ BLOCKED: Direct access attempt                              │
│   Hacker → Service (no secret) → 401 Rejected                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

```java
// Backend service validates gateway secret
@Component
public class GatewaySecretFilter extends OncePerRequestFilter {
    @Value("${gateway.secret.token}")
    private String expectedToken;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) {
        String secret = request.getHeader("X-Gateway-Secret");
        if (!expectedToken.equals(secret)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    }
}
```

#### Option B: Mutual TLS (mTLS)

Both client and server verify each other's certificates:

```properties
# Service A (client)
feign.client.config.service-b.ssl.key-store=classpath:service-a.p12
feign.client.config.service-b.ssl.trust-store=classpath:truststore.p12

# Service B (server)
server.ssl.client-auth=need
server.ssl.trust-store=classpath:truststore.p12
```

#### Option C: OAuth2 Client Credentials

Services authenticate with their own credentials:

```java
@FeignClient(name = "inventory-service", configuration = OAuth2FeignConfig.class)
public interface InventoryClient { }
```

---

### Layer 3: Data Security

| Technique | Purpose |
|-----------|---------|
| **Encryption at Rest** | Protect stored data |
| **Encryption in Transit** | HTTPS everywhere |
| **Secrets Management** | Store sensitive config (Vault, AWS Secrets Manager) |
| **Least Privilege** | Minimal database permissions |

---

### Comparison of Approaches

| Approach | Complexity | Security Level | Use Case |
|----------|------------|----------------|----------|
| **Gateway Secret Token** | Low | Medium | Internal services, development |
| **mTLS** | High | Very High | Production, compliance requirements |
| **OAuth2 Client Credentials** | Medium | High | When identity matters between services |
| **Service Mesh (Istio)** | Very High | Very High | Large-scale production |

---

### Our Project Implementation

```properties
# API Gateway adds this header to all outgoing requests
gateway.secret.enabled=true
gateway.secret.header-name=X-Gateway-Secret
gateway.secret.token=${GATEWAY_SECRET_TOKEN:dev-secret}
```

Each backend service validates this header, rejecting any request that doesn't have it (meaning it bypassed the gateway).

---

## 🟠 Intermediate-Hard Level Questions (Q31-Q40)

---

## Q31: What is the difference between Orchestration and Choreography?

**Answer:**

### 🎯 Simple Analogy

- **Orchestration** = Orchestra conductor directing all musicians → One central controller tells everyone what to do
- **Choreography** = Dance troupe where each dancer knows their moves → Each service reacts to events independently

---

### What is Orchestration?

**Orchestration** uses a **central coordinator (orchestrator)** that controls the interaction between services.

```
┌─────────────────────────────────────────────────────────────┐
│                    ORCHESTRATION PATTERN                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│                    ┌──────────────┐                         │
│                    │ Orchestrator │                         │
│                    │   (Saga)     │                         │
│                    └──────┬───────┘                         │
│           ┌───────────────┼───────────────┐                 │
│           ▼               ▼               ▼                 │
│     ┌──────────┐   ┌──────────┐   ┌──────────┐             │
│     │ Order    │   │ Payment  │   │ Inventory│             │
│     │ Service  │   │ Service  │   │ Service  │             │
│     └──────────┘   └──────────┘   └──────────┘             │
│                                                              │
│     Orchestrator CALLS each service in sequence             │
└─────────────────────────────────────────────────────────────┘
```

#### Orchestration Example: Order Processing Saga

```java
@Service
public class OrderOrchestrator {
    
    public Order processOrder(OrderRequest request) {
        // Step 1: Create order
        Order order = orderService.createOrder(request);
        
        // Step 2: Reserve inventory
        try {
            inventoryService.reserveItems(order.getItems());
        } catch (Exception e) {
            orderService.cancelOrder(order.getId()); // Compensate
            throw e;
        }
        
        // Step 3: Process payment
        try {
            paymentService.processPayment(order);
        } catch (Exception e) {
            inventoryService.releaseItems(order.getItems()); // Compensate
            orderService.cancelOrder(order.getId());          // Compensate
            throw e;
        }
        
        // Step 4: Confirm order
        return orderService.confirmOrder(order.getId());
    }
}
```

---

### What is Choreography?

**Choreography** uses **events** to coordinate services. Each service listens for events and reacts independently.

```
┌─────────────────────────────────────────────────────────────┐
│                   CHOREOGRAPHY PATTERN                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│     ┌──────────┐   publishes   ┌─────────────────────┐      │
│     │ Order    │──────────────▶│ "OrderCreated"      │      │
│     │ Service  │               │     Event           │      │
│     └──────────┘               └──────────┬──────────┘      │
│                                           │                  │
│                   ┌───────────────────────┼───────────────┐ │
│                   │ subscribes            │ subscribes    │ │
│                   ▼                       ▼               │ │
│            ┌──────────┐            ┌──────────┐           │ │
│            │ Payment  │            │ Inventory│           │ │
│            │ Service  │            │ Service  │           │ │
│            └──────────┘            └──────────┘           │ │
│                                                              │
│     Each service LISTENS to events and reacts               │
└─────────────────────────────────────────────────────────────┘
```

#### Choreography Example with Events

```java
// Order Service - publishes event
@Service
public class OrderService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public Order createOrder(OrderRequest request) {
        Order order = orderRepository.save(new Order(request));
        
        // Publish event - other services will react
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        return order;
    }
}

// Inventory Service - listens for event
@Service
public class InventoryEventHandler {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserveItems(event.getOrder().getItems());
    }
}

// Payment Service - listens for same event
@Service
public class PaymentEventHandler {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        paymentService.processPayment(event.getOrder());
    }
}
```

---

### Comparison Table

| Aspect | Orchestration | Choreography |
|--------|---------------|--------------|
| **Control** | Central coordinator | No central control |
| **Communication** | Command-based (sync/async) | Event-based (async) |
| **Coupling** | Services coupled to orchestrator | Loosely coupled |
| **Visibility** | Easy to track flow | Harder to trace |
| **Failure Handling** | Centralized compensation | Distributed compensation |
| **Best for** | Complex workflows | Simple event reactions |

---

### When to Use Each?

| Use Orchestration When | Use Choreography When |
|------------------------|----------------------|
| Complex multi-step workflows | Simple reactions to events |
| Need clear visibility of process | Services are truly independent |
| Compensation logic is complex | Scalability is priority |
| Strict ordering required | Loose coupling is critical |

---

### Our Project's Approach

> ✅ **Our project uses Orchestration** - The `OrderService` orchestrates calls to `InventoryService` via Feign Client, controlling the flow with fallback handling for failures.

---

## Q32: How do you handle idempotency in microservices?

**Answer:**
**Idempotency** ensures that making the same request multiple times has the same effect as making it once.

**Techniques:**
1. **Idempotency Key**: Client sends unique ID with request
2. **Database Constraints**: Unique constraints prevent duplicates
3. **Conditional Updates**: Check state before updating

```java
@PostMapping("/orders")
public Order createOrder(@RequestHeader("Idempotency-Key") String key, @RequestBody OrderDTO dto) {
    Optional<Order> existing = orderRepo.findByIdempotencyKey(key);
    if (existing.isPresent()) return existing.get();
    return orderService.create(dto, key);
}
```

---

## Q33: How do you implement asynchronous communication between microservices?

**Answer:**

### 🎯 Simple Analogy: Text Message vs Phone Call

- **Synchronous (REST)** = Phone call - you wait for response
- **Asynchronous (Message Queue)** = Text message - send and continue

### Common Approaches

| Method | Tool | Use Case |
|--------|------|----------|
| **Message Queues** | RabbitMQ, ActiveMQ | Task processing, email notifications |
| **Event Streaming** | Apache Kafka | Real-time analytics, event sourcing |
| **Pub/Sub** | Redis Pub/Sub | Broadcast notifications |

### Example with RabbitMQ

```java
// Producer
@Service
public class OrderEventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishOrderCreated(Order order) {
        rabbitTemplate.convertAndSend("order-exchange", "order.created", order);
    }
}

// Consumer
@RabbitListener(queues = "order-queue")
public void handleOrderCreated(Order order) {
    // Process the order asynchronously
}
```

---

## Q34: How do you configure timeouts in microservices?

**Answer:**

Timeouts prevent cascading failures by ensuring services don't wait indefinitely.

### Key Timeout Types

| Timeout Type | Description | Typical Value |
|--------------|-------------|---------------|
| **Connection** | Time to establish connection | 2-5 seconds |
| **Read** | Time to receive response | 5-30 seconds |
| **Call** | Total end-to-end time | 30-60 seconds |

### Configuration in Our Project

```properties
# Feign Client Timeouts
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=10000

# Resilience4j Timeout
resilience4j.timelimiter.instances.default.timeoutDuration=5s
resilience4j.timelimiter.instances.default.cancelRunningFuture=true
```

### Best Practice

Always set timeouts shorter as you go downstream:
- Gateway timeout: 30s
- Service A timeout: 15s
- Service B timeout: 5s

---

## Q35: How do you manage environment-specific configurations?

**Answer:**

### 🎯 Spring Cloud Config + Profiles

Use different configurations for dev, staging, and production environments.

### Profile-Specific Files

```
config-repo/
├── book-service.properties          # Common settings
├── book-service-dev.properties      # Development
├── book-service-staging.properties  # Staging
└── book-service-prod.properties     # Production
```

### Activating Profiles

```properties
# Application startup
spring.profiles.active=dev

# Or via environment variable
SPRING_PROFILES_ACTIVE=prod
```

### Example: Database Config by Environment

```properties
# book-service-dev.properties
spring.datasource.url=jdbc:h2:mem:testdb

# book-service-prod.properties
spring.datasource.url=jdbc:mysql://prod-db:3306/books
```

### Our Project's Config Structure

The Config Server (`config-service`) serves environment-specific configurations from `config-repo/`.

---

## Q37: What is the difference between Horizontal and Vertical Scaling?

**Answer:**

| Scaling Type | Description | Microservices Fit |
|--------------|-------------|-------------------|
| **Vertical** | Add more CPU/RAM to existing server | Limited |
| **Horizontal** | Add more server instances | ✅ Preferred |

Microservices favor horizontal scaling because each service can scale independently based on its load.

---

## Q38: How do you implement canary deployments?

**Answer:**
Gradually roll out changes to a small subset of users before full deployment.

**Steps:**
1. Deploy new version alongside old
2. Route 5% traffic to new version
3. Monitor for errors
4. Gradually increase to 100%
5. Decommission old version

**Tools:** Kubernetes, Istio, AWS CodeDeploy

---

## Q39: How do you secure service-to-service communication?

**Answer:**

### 🎯 Simple Analogy: VIP Section at a Concert

Imagine your microservices are behind a VIP section at a concert:
- **API Gateway** = Security guard at the main entrance (stamps your wristband)
- **Backend Services** = VIP rooms that ONLY allow people with the stamped wristband
- **X-Gateway-Secret** = The special wristband stamp
- **Direct access attempt** = Someone trying to sneak in through a back door → **REJECTED!**

---

### The Problem We're Solving

```
❌ WITHOUT PROTECTION:
   Hacker ─────────────────────────▶ Backend Service
                                      (VULNERABLE!)

✅ WITH GATEWAY SECRET:
   Hacker ─────────────────────────▶ Backend Service
                                      ↓
                                   No secret? → 403 FORBIDDEN!
   
   User ──▶ API Gateway ──▶ Backend Service
            (adds secret)    (validates secret ✓)
```

---

### How It Works (Step-by-Step)

1. **User makes request** → Goes to API Gateway first
2. **API Gateway adds a secret header** (`X-Gateway-Secret: my-secret-token`)
3. **Request forwarded** to the backend service
4. **Backend service checks** → Does it have the secret header?
   - ✅ **Yes** → Process the request
   - ❌ **No** → Return 403 Forbidden (request was bypassed!)

---

### Our Project's Implementation

#### Step 1: Gateway Adds the Secret

```java
// In API Gateway - adds secret to EVERY outgoing request
@Component
public class GatewaySecretFilter implements GlobalFilter {
    
    @Value("${gateway.secret}")  // Secret from config file
    private String gatewaySecret;
    
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Add the secret header before forwarding
        return chain.filter(exchange.mutate()
            .request(r -> r.header("X-Gateway-Secret", gatewaySecret))
            .build());
    }
}
```

#### Step 2: Backend Service Validates the Secret

```java
// In each backend service - validates every incoming request
@Component
public class GatewaySecretValidationFilter extends OncePerRequestFilter {
    
    @Value("${gateway.secret}")  // Same secret as gateway
    private String expectedSecret;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) {
        
        // Get the secret from incoming request header
        String secret = request.getHeader("X-Gateway-Secret");
        
        // Does it match our expected secret?
        if (!expectedSecret.equals(secret)) {
            // NO! This request did NOT come through the gateway
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            return;  // Stop processing - reject the request
        }
        
        // YES! Request is valid, continue processing
        chain.doFilter(request, response);
    }
}
```

---

### Why Is This Important?

| Scenario | What Happens |
|----------|--------------|
| User → Gateway → Service | ✅ Request has secret, **allowed** |
| Hacker → Service directly | ❌ No secret, **blocked** |
| Hacker guesses secret | ❌ Wrong secret, **blocked** |

---

### Alternative Approaches (More Complex)

| Method | Complexity | Security Level | When to Use |
|--------|------------|----------------|-------------|
| **API Keys (Gateway Secret)** | ⭐ Easy | Medium | ✅ Development, small teams |
| **mTLS** | ⭐⭐⭐ Hard | Very High | Production, compliance needs |
| **OAuth2 Client Credentials** | ⭐⭐ Medium | High | When services need identity |

> 💡 **Our project uses the Gateway Secret approach** because it's simple, effective, and easy to understand!

---

## Q40: How do you handle service versioning during deployments?

**Answer:**

### 🎯 Simple Analogy: Restaurant Menu Update

Imagine you run a restaurant and want to update your menu without closing:
- **Blue-Green** = Have two kitchens, switch customers to the new one instantly
- **Rolling** = Train one chef at a time while others keep cooking
- **Canary** = Let one table try the new dish first, expand if they like it
- **Feature Flags** = Same menu, but waiter only tells VIPs about special items

---

### 1. Blue-Green Deployment 💙💚

**Two identical environments** - switch traffic instantly between them.

```
BEFORE:                           AFTER:
┌─────────┐                      ┌─────────┐
│  BLUE   │◀── Traffic          │  BLUE   │  (idle)
│  v1.0   │                      │  v1.0   │
└─────────┘                      └─────────┘

┌─────────┐                      ┌─────────┐
│  GREEN  │  (idle)              │  GREEN  │◀── Traffic
│  v2.0   │                      │  v2.0   │
└─────────┘                      └─────────┘
```

| Pros | Cons |
|------|------|
| ✅ Instant rollback | ❌ Need 2x resources |
| ✅ Zero downtime | ❌ Database migrations tricky |
| ✅ Easy to test before switch | |

---

### 2. Rolling Deployment 🔄

**Gradually replace instances** one by one - no downtime.

```
Step 1: [v1] [v1] [v1] [v1]  ← All old version
Step 2: [v2] [v1] [v1] [v1]  ← 1 new instance
Step 3: [v2] [v2] [v1] [v1]  ← 2 new instances  
Step 4: [v2] [v2] [v2] [v1]  ← 3 new instances
Step 5: [v2] [v2] [v2] [v2]  ← All updated! ✓
```

| Pros | Cons |
|------|------|
| ✅ No extra resources | ❌ Slower rollout |
| ✅ Gradual testing | ❌ Mixed versions during update |

---

### 3. Canary Deployment 🐤

**Route small % of traffic** to new version first, then gradually increase.

```
Phase 1:  [v1 - 95%] ████████████████████ 
          [v2 -  5%] █

Phase 2:  [v1 - 80%] ████████████████ 
          [v2 - 20%] ████

Phase 3:  [v1 - 50%] ██████████ 
          [v2 - 50%] ██████████

Final:    [v2 - 100%] ████████████████████ ✓
```

| Pros | Cons |
|------|------|
| ✅ Catch issues early | ❌ More complex setup |
| ✅ Real user feedback | ❌ Need traffic splitting |

---

### 4. Feature Flags 🚩

**Toggle features without deployment** - A/B testing capability.

```java
// Code deployed but feature controlled by flag
if (featureFlagService.isEnabled("new-checkout-flow", user)) {
    return newCheckoutProcess();  // New feature
} else {
    return oldCheckoutProcess();  // Old feature
}
```

| Pros | Cons |
|------|------|
| ✅ Instant enable/disable | ❌ Code complexity increases |
| ✅ Target specific users | ❌ Need cleanup after rollout |
| ✅ A/B testing | |

---

### Quick Comparison

| Strategy | Speed | Risk | Resources | Best For |
|----------|-------|------|-----------|----------|
| **Blue-Green** | ⚡ Instant | Low | 2x | Critical services |
| **Rolling** | 🐢 Gradual | Medium | 1x | Standard deployments |
| **Canary** | 🐢 Gradual | Low | 1.1x | Testing with real users |
| **Feature Flags** | ⚡ Instant | Very Low | 1x | A/B tests, gradual rollouts |

> 💡 **In our project**, we use feature flags for testing new functionality and rolling deployments through Docker containers.

---

## 🔴 Hard Level Questions (Q41-Q50)

---

## Q41: How would you debug a performance issue across 10+ microservices?

**Answer:**

1. **Distributed Tracing** (Zipkin/Jaeger): Identify slow service
2. **Metrics** (Prometheus/Grafana): Check CPU, memory, latency
3. **Logs** (ELK Stack): Correlate logs using trace ID
4. **Profiling**: JProfiler/Async-profiler for CPU/memory analysis
5. **Database Analysis**: Slow query logs, connection pool metrics

---

## Q42: Explain eventual consistency and its trade-offs

**Answer:**

### 🎯 Simple Analogy: Social Media Post

Imagine you post a photo on Instagram:
- Your friend in the **same city** sees it instantly
- Your friend in **another country** might see it 5 seconds later
- Eventually, **everyone** sees the same photo

This is **eventual consistency** - data becomes consistent across all nodes, but not immediately!

---

### What is Eventual Consistency?

```
STRONG CONSISTENCY (Traditional):
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Node A  │ ──► │ Node B  │ ──► │ Node C  │
│ Data: 5 │     │ Data: 5 │     │ Data: 5 │
└─────────┘     └─────────┘     └─────────┘
All nodes have same data IMMEDIATELY (slower, but safe)

EVENTUAL CONSISTENCY (Microservices):
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Node A  │     │ Node B  │     │ Node C  │
│ Data: 5 │     │ Data: 4 │     │ Data: 3 │  ← Different temporarily!
└─────────┘     └─────────┘     └─────────┘
           ↓ After sync ↓
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Node A  │     │ Node B  │     │ Node C  │
│ Data: 5 │     │ Data: 5 │     │ Data: 5 │  ← Eventually same ✓
└─────────┘     └─────────┘     └─────────┘
```

---

### The CAP Theorem Explained

> **You can only pick 2 out of 3!**

```
                    Consistency
                        △
                       / \
                      /   \
                     /     \
          Availability ───── Partition
                             Tolerance
```

| Property | Meaning | Example |
|----------|---------|---------|
| **Consistency** | All nodes see same data | Bank balance is always accurate |
| **Availability** | System always responds | Website never shows error page |
| **Partition Tolerance** | Works if network splits | Works even if datacenter goes down |

---

### Real-World Examples

| System | Choice | Why |
|--------|--------|-----|
| **Banks** | CP (Consistency + Partition) | Can't show wrong balance! |
| **Social Media** | AP (Availability + Partition) | Better to show old likes than error |
| **Shopping Cart** | AP (Availability + Partition) | Can merge conflicts later |

---

### Trade-offs Table

| Pros | Cons |
|------|------|
| ✅ High availability - system rarely goes down | ❌ Temporary inconsistency - users may see stale data |
| ✅ Better performance - no waiting for all nodes | ❌ Complex conflict resolution - what if 2 updates conflict? |
| ✅ Partition tolerant - survives network issues | ❌ Harder to reason about - debugging is tricky |
| ✅ Scalable - easy to add more nodes | ❌ Requires compensation logic |

---

### When to Use What?

| Scenario | Consistency Type | Why |
|----------|------------------|-----|
| 💰 **Financial transactions** | Strong | Money must be exact |
| 👍 **Like counts** | Eventual | Off by 1 is okay |
| 🛒 **Shopping cart** | Eventual | Can merge later |
| 📧 **Email delivery** | Eventual | Delay is acceptable |
| 🔐 **User authentication** | Strong | Security critical |

> 💡 **In microservices**, we often choose **eventual consistency** because services are distributed and we prioritize availability over immediate consistency.

---

## Q43: How do you handle cross-cutting concerns in microservices?

**Answer:**

### 🎯 Simple Analogy: Superhero Utility Belt

Cross-cutting concerns are like a superhero's utility belt - **every superhero needs them, regardless of their specific powers**:
- **Logging** = Your communication device (always know what's happening)
- **Security** = Your armor (protection everywhere)
- **Monitoring** = Your health tracker (know when you're hurt)
- **Tracing** = Your GPS (know where you've been)

---

### What Are Cross-Cutting Concerns?

```
┌─────────────────────────────────────────────────────────────────┐
│                     CROSS-CUTTING CONCERNS                       │
│  (Needed by ALL services, not just one specific service)        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐        │
│   │ Book    │   │ Order   │   │ Auth    │   │Inventory│        │
│   │ Service │   │ Service │   │ Service │   │ Service │        │
│   └────┬────┘   └────┬────┘   └────┬────┘   └────┬────┘        │
│        │             │             │             │              │
│   ┌────┴─────────────┴─────────────┴─────────────┴────┐        │
│   │          All need:                                │        │
│   │  ✓ Logging    ✓ Security   ✓ Monitoring          │        │
│   │  ✓ Tracing    ✓ Configuration                    │        │
│   └───────────────────────────────────────────────────┘        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

### Common Cross-Cutting Concerns

| Concern | What It Does | Solution | Our Project |
|---------|--------------|----------|-------------|
| **Logging** | Track what happened | ELK Stack, Correlation IDs | ✅ SLF4J + Logback |
| **Security** | Protect services | API Gateway, JWT | ✅ Spring Security + JWT |
| **Monitoring** | Watch service health | Prometheus, Grafana | ✅ Spring Boot Actuator |
| **Tracing** | Follow request journey | Zipkin, Jaeger | ⏳ Planned |
| **Configuration** | Centralize settings | Config Server | ✅ Spring Cloud Config |

---

### Implementation Approaches

| Approach | How It Works | Pros | Cons |
|----------|--------------|------|------|
| **API Gateway** | Handles at edge | Centralized | Gateway becomes bottleneck |
| **Shared Libraries** | Include in each service | Easy to implement | Version hell |
| **Service Mesh** (Istio) | Sidecar proxies | No code changes | Complex infrastructure |
| **AOP** (Aspect-Oriented) | Cross-cutting aspects | Clean separation | Can be magical |

---

### Our Project's Approach

```java
// We use AOP for logging across all services
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    @Around("execution(* com.book.management..*Controller.*(..))")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("➡️ Calling: {}", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        log.info("⬅️ Completed: {}", joinPoint.getSignature().getName());
        return result;
    }
}
```

> 💡 **Key insight**: Handle cross-cutting concerns in ONE place to avoid code duplication across all services!

---

## Q44: How do you design for failure in microservices?

**Answer:**

### 🎯 Simple Analogy: Building a Ship

Designing for failure is like building a ship:
- **Circuit Breakers** = Watertight compartments (one leak doesn't sink the ship)
- **Timeouts** = Emergency protocols (don't wait forever for a rescue)
- **Fallbacks** = Lifeboats (alternative if main systems fail)
- **Health Checks** = Crew inspections (find problems before they're critical)

---

### The 7 Patterns for Resilience

```
┌─────────────────────────────────────────────────────────────────┐
│                    FAILURE DESIGN PATTERNS                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐          │
│   │ 1. Circuit  │   │ 2. Timeouts │   │ 3. Retries  │          │
│   │   Breaker   │   │             │   │ w/ Backoff  │          │
│   │ "Stop if    │   │ "Don't wait │   │ "Try again  │          │
│   │  failing"   │   │  forever"   │   │  smartly"   │          │
│   └─────────────┘   └─────────────┘   └─────────────┘          │
│                                                                  │
│   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐          │
│   │ 4. Bulkhead │   │ 5. Fallback │   │ 6. Health   │          │
│   │             │   │             │   │   Checks    │          │
│   │ "Isolate    │   │ "Have Plan  │   │ "Monitor    │          │
│   │  failures"  │   │    B"       │   │  constantly"│          │
│   └─────────────┘   └─────────────┘   └─────────────┘          │
│                                                                  │
│                      ┌─────────────┐                            │
│                      │ 7. Chaos    │                            │
│                      │ Engineering │                            │
│                      │ "Test       │                            │
│                      │  failures"  │                            │
│                      └─────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
```

---

### Detailed Breakdown

| Pattern | What It Does | When to Use | Our Project |
|---------|--------------|-------------|-------------|
| **Circuit Breaker** | Stops calling failing service | External service calls | ✅ Resilience4j |
| **Timeouts** | Sets max wait time | All external calls | ✅ Feign timeout config |
| **Retries** | Tries again with delay | Transient failures | ✅ Retry pattern |
| **Bulkhead** | Limits concurrent calls | Heavy load services | ✅ Thread pool limits |
| **Fallback** | Returns default/cached | When failure acceptable | ✅ FallbackFactory |
| **Health Checks** | Monitors service state | Service discovery | ✅ Actuator |
| **Chaos Engineering** | Tests failure scenarios | Production readiness | ⏳ Planned |

---

### Our Project Implementation

```java
// Feign Client with Fallback (Pattern 5)
@FeignClient(name = "inventory-service", 
             fallbackFactory = InventoryClientFallbackFactory.class)
public interface InventoryServiceClient {
    // ...
}

// Fallback provides graceful degradation
@Component
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryServiceClient> {
    @Override
    public InventoryServiceClient create(Throwable cause) {
        return bookId -> {
            log.error("❌ Inventory service unavailable, returning empty");
            throw new OrderNotPlacedException("Cannot check inventory");
        };
    }
}
```

---

### Key Principle

> 💡 **Assume everything will fail** - Design systems that gracefully handle failures rather than expecting perfect uptime!

---

## Q45: What is the difference between API Composition and Event-driven queries?

**Answer:**

### 🎯 Simple Analogy: Making a Salad

**API Composition** = Going to separate stores for each ingredient:
- Visit vegetable store → get tomatoes
- Visit fruit store → get avocados  
- Visit bakery → get croutons
- **Combine at home (query time)**

**Event-Driven** = Ingredients pre-assembled by subscription box:
- Subscribe once → ingredients delivered when available
- **Salad kit ready to use (pre-aggregated)**

---

### Visual Comparison

```
API COMPOSITION (Query-time aggregation):
┌──────────────────────────────────────────────────────────────────┐
│                                                                   │
│   Client Request: "Get Order Details"                            │
│         │                                                         │
│         ▼                                                         │
│   ┌───────────────┐                                              │
│   │  API Gateway  │ ─┬─► Order Service ──► Order data            │
│   │  (Composer)   │  ├─► User Service ───► User data             │
│   │               │  └─► Product Service ► Product data          │
│   └───────────────┘                                              │
│         │                                                         │
│         ▼ Combines all responses                                  │
│   { order, user, products } ── Final Response                    │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘

EVENT-DRIVEN (Pre-aggregated views):
┌──────────────────────────────────────────────────────────────────┐
│                                                                   │
│   Services publish events as data changes:                        │
│                                                                   │
│   Order Created ─┐                                                │
│   User Updated  ─┼──► Event Bus ──► Materialized View Service    │
│   Price Changed ─┘                      │                        │
│                                         ▼                        │
│                               ┌─────────────────┐                │
│                               │ Pre-aggregated  │                │
│   Client Request ───────────►│ Read Database   │                │
│                               │ (Fast read!)    │                │
│                               └─────────────────┘                │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

### Detailed Comparison

| Aspect | API Composition | Event-Driven Queries |
|--------|-----------------|---------------------|
| **How it works** | Fetches from multiple services at query time | Pre-builds views from events |
| **Data freshness** | ✅ Always current | ⏳ Eventually consistent |
| **Read latency** | ❌ Higher (multiple calls) | ✅ Very low (single read) |
| **Write complexity** | ✅ Simple | ❌ Need event handling |
| **Infrastructure** | ✅ Minimal | ❌ Need message broker + storage |
| **Scalability** | ❌ Limited by slowest service | ✅ Highly scalable |

---

### When to Use Each?

| Scenario | Best Approach | Why |
|----------|---------------|-----|
| **Simple dashboards** | API Composition | Easy to implement |
| **High-traffic reads** | Event-Driven | Pre-computed for speed |
| **Real-time data needed** | API Composition | Always fresh |
| **Complex aggregations** | Event-Driven | Avoid N+1 queries |
| **Infrequent queries** | API Composition | Not worth pre-computing |

---

### Code Examples

**API Composition:**
```java
// Composer fetches from multiple services
public OrderDetailsDTO getOrderDetails(Long orderId) {
    Order order = orderClient.getOrder(orderId);          // Call 1
    User user = userClient.getUser(order.getUserId());    // Call 2
    List<Product> products = productClient.getProducts(   // Call 3
        order.getProductIds());
    
    return new OrderDetailsDTO(order, user, products);    // Combine
}
```

**Event-Driven:**
```java
// Listen to events and update materialized view
@EventListener
public void on(OrderCreatedEvent event) {
    OrderView view = buildView(event);
    viewRepository.save(view);  // Pre-aggregated!
}

// Query is simple single read
public OrderView getOrderDetails(Long orderId) {
    return viewRepository.findById(orderId);  // Fast!
}
```

> 💡 **Our project uses API Composition** via Feign Client for simplicity. Event-driven would be considered for high-traffic read scenarios.

---

## Q46: How do you implement centralized log aggregation?

**Answer:**

### 🎯 Simple Analogy: Crime Scene Investigation

Imagine you're a detective investigating a case across multiple cities:
- Each city keeps its own police records (scattered logs = bad!)
- A **centralized database** lets you search ALL records in one place (good!)
- You can trace a suspect's journey across all locations

That's exactly what centralized logging does for microservices!

---

### Why Centralized Logging?

In microservices, logs are scattered across multiple services. Centralized logging collects them in one place for easier debugging.

### Common Stack: ELK (Elasticsearch, Logstash, Kibana)

```
Services → Logstash (collector) → Elasticsearch (storage) → Kibana (visualization)
```

### Our Project's Logging Approach

```java
@Slf4j
@RestController
public class BookController {
    
    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable Long id) {
        log.info("Fetching book with id: {}", id);
        // ...
    }
}
```

### Structured Logging Best Practices

```properties
# Include trace IDs for request correlation
logging.pattern.console=%d{HH:mm:ss} [%X{traceId}] %-5level %logger{36} - %msg%n
```

### Key Fields to Log

| Field | Purpose |
|-------|---------|
| **traceId** | Correlate requests across services |
| **serviceId** | Identify which service logged |
| **userId** | Track user actions |
| **timestamp** | Time ordering |

---

## Q47: How do you implement health checks and heartbeats?

**Answer:**

### 🎯 Simple Analogy: Regular Doctor Checkups

Health checks are like regular checkups at the doctor:
- **Health Check** = "How are you feeling?" (check if service is working)
- **Heartbeat** = Your pulse (regular signal = I'm alive!)
- If you miss your appointments, the doctor assumes something's wrong

In microservices, services send "I'm alive!" signals regularly to Eureka.

---

### 🎯 Spring Boot Actuator Health Checks

```properties
# Enable health endpoint
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when_authorized
```

### Health Endpoint

Access at: `http://localhost:8081/actuator/health`

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

### Custom Health Indicators

```java
@Component
public class InventoryServiceHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check if inventory service is reachable
        boolean isUp = checkInventoryService();
        return isUp 
            ? Health.up().withDetail("inventory", "Available").build()
            : Health.down().withDetail("inventory", "Unavailable").build();
    }
}
```

### How Eureka Uses Health Checks

- Services send **heartbeats** every 30 seconds
- Eureka removes services that miss 3 consecutive heartbeats
- Health check URL is used by load balancers to route traffic

---

## Q48: How do you implement zero-downtime database migrations?

**Answer:**

### 🎯 Simple Analogy: Renovating While Living in Your House

Zero-downtime migration is like renovating a kitchen while still cooking meals:
- Can't just tear down the kitchen (downtime!)
- Build new kitchen alongside old one
- Gradually move appliances and usage
- Only remove old kitchen when new one is fully functional

---

### The Expand-Contract Pattern

```
PHASE 1: EXPAND (Add new structure)
┌─────────────────────────────────────────────────────────────┐
│  old_column  │  new_column (nullable)                       │
│     data     │       null                                   │
└─────────────────────────────────────────────────────────────┘
                  ↓ App writes to BOTH columns

PHASE 2: MIGRATE (Copy existing data)
┌─────────────────────────────────────────────────────────────┐
│  old_column  │  new_column                                  │
│     data     │   data (copied!)                             │
└─────────────────────────────────────────────────────────────┘
                  ↓ App reads from NEW column

PHASE 3: CONTRACT (Remove old structure)
┌─────────────────────────────────────────────────────────────┐
│  new_column                                                 │
│     data                                                    │
└─────────────────────────────────────────────────────────────┘
                  ✅ Migration complete!
```

---

### Step-by-Step Process

| Step | Action | Why |
|------|--------|-----|
| 1 | Add new column (nullable) | Old app still works |
| 2 | Deploy code that writes to both | New data goes everywhere |
| 3 | Backfill existing data | Copy old to new |
| 4 | Deploy code that reads from new | Start using new structure |
| 5 | Deploy code that only writes new | Stop maintaining old |
| 6 | Drop old column | Cleanup |

---

### Example: Renaming a Column

```sql
-- Step 1: Add new column
ALTER TABLE users ADD COLUMN full_name VARCHAR(255);

-- Step 2: Backfill data
UPDATE users SET full_name = name WHERE full_name IS NULL;

-- Step 3: (After code deploys) Drop old column
ALTER TABLE users DROP COLUMN name;
```

---

### Tools for Safe Migrations

| Tool | Features |
|------|----------|
| **Flyway** | Version-controlled SQL migrations |
| **Liquibase** | XML/YAML changesets, rollback support |
| **online-schema-change** | MySQL zero-downtime DDL |
| **pg_repack** | PostgreSQL table reorganization |

---

### Common Mistakes to Avoid

| ❌ Don't | ✅ Do Instead |
|---------|---------------|
| Drop column directly | Use expand-contract |
| Make column NOT NULL immediately | Add nullable first, then constrain |
| Rename column in one step | Add new → copy → drop old |
| Large data migrations in one go | Batch in smaller chunks |

> 💡 **Our project uses Flyway** for versioned database migrations. Each migration is a numbered SQL file that runs automatically on startup.

---

## Q49: How do you configure connection pooling in microservices?

**Answer:**

### 🎯 Simple Analogy: Library Cards

Connection pooling is like a library with a limited number of library cards:
- Creating a new card for every visitor is slow and wasteful
- Instead, you have a **pool of pre-issued cards**
- Visitors borrow a card, use it, and return it for others
- If all cards are taken, new visitors wait in line

Same with database connections - reuse them instead of creating new ones!

---

### Why Connection Pooling?

Creating database connections is expensive. Connection pools maintain reusable connections.

### HikariCP Configuration (Default in Spring Boot)

```properties
# Connection pool settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.max-lifetime=1200000
```

### Pool Size Guidelines

| Factor | Recommendation |
|--------|----------------|
| **CPU Cores** | Pool size = cores × 2 + disk spindles |
| **Microservices** | Smaller pools (5-10) per service |
| **Blocking operations** | Larger pools |
| **Reactive/Non-blocking** | Smaller pools |

### Monitoring Pool Health

```properties
# Expose HikariCP metrics via Actuator
management.metrics.enable.hikaricp=true
```

### Common Issues

- **Pool exhaustion**: All connections busy → increase pool or optimize queries
- **Connection leaks**: Connections not returned → use try-with-resources

---

## Q50: How would you migrate a monolith to microservices?

**Answer:**

### 🎯 Simple Analogy: The Strangler Fig Tree

The Strangler Fig is a plant that grows around a host tree, eventually replacing it completely:
- You don't cut down the old tree immediately (risky!)
- New growth slowly takes over functionality
- Old tree eventually becomes unnecessary
- Same approach with monolith → microservices!

---

### Visual: Strangler Fig Pattern

```
PHASE 1: Start
┌────────────────────────────────────────┐
│           MONOLITH                      │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐   │
│  │ User │ │ Order│ │ Book │ │Review│   │
│  └──────┘ └──────┘ └──────┘ └──────┘   │
└────────────────────────────────────────┘

PHASE 2: Extract first service
┌────────────────────────────────────────┐
│           MONOLITH                      │       ┌──────────┐
│  ┌──────┐ ┌──────┐ ┌──────┐            │ ───── │  Review  │
│  │ User │ │ Order│ │ Book │ (removed) │       │ Service  │
│  └──────┘ └──────┘ └──────┘            │       └──────────┘
└────────────────────────────────────────┘

PHASE 3: Continue extracting
┌─────────────────────────────┐   ┌──────────┐   ┌──────────┐
│        MONOLITH              │   │  Review  │   │   Book   │
│  ┌──────┐ ┌──────┐          │   │ Service  │   │ Service  │
│  │ User │ │ Order│          │   └──────────┘   └──────────┘
│  └──────┘ └──────┘          │
└─────────────────────────────┘

PHASE 4: Monolith eliminated
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│   User   │  │  Order   │  │   Book   │  │  Review  │
│ Service  │  │ Service  │  │ Service  │  │ Service  │
└──────────┘  └──────────┘  └──────────┘  └──────────┘
                    ✅ COMPLETE!
```

---

### Step-by-Step Migration Strategy

| Step | Action | Tips |
|------|--------|------|
| 1️⃣ | **Identify Bounded Contexts** | Use Domain-Driven Design (DDD) |
| 2️⃣ | **Start with least coupled module** | Easier to extract, less risk |
| 3️⃣ | **Create API Gateway** | Route traffic to both old and new |
| 4️⃣ | **Extract service by service** | One at a time, verify each |
| 5️⃣ | **Split shared database LAST** | This is the hardest part |

---

### Which Module to Extract First?

| Good First Candidates | Why |
|----------------------|-----|
| ✅ **Reporting/Analytics** | Usually read-only, low risk |
| ✅ **Notifications** | Isolated functionality |
| ✅ **File upload/storage** | Clear boundaries |
| ❌ **User authentication** | Too many dependencies, extract later |
| ❌ **Core business logic** | Too risky for first extraction |

---

### ❌ Anti-Patterns to Avoid

| Anti-Pattern | Problem | Solution |
|--------------|---------|----------|
| **Big Bang Rewrite** | High risk, takes forever | Use Strangler Fig (gradual) |
| **Distributed Monolith** | Services too tightly coupled | Ensure loose coupling |
| **Shared Database** | Couples services together | Each service owns its data |
| **No API Gateway** | Hard to route traffic | Add gateway first |
| **Extracting too fast** | Overwhelms team | One service at a time |

---

### Database Decomposition (The Hard Part)

```
BEFORE: Shared Database (❌ Tight coupling)
┌─────────────────────────────────────────────────────┐
│                   SHARED DATABASE                    │
│  Users │ Orders │ Books │ Reviews                   │
└─────────────────────────────────────────────────────┘
        ↑           ↑        ↑          ↑
   Service A    Service B  Service C  Service D

AFTER: Database per Service (✅ Loose coupling)
┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
│  Users  │  │ Orders  │  │  Books  │  │ Reviews │
│   DB    │  │   DB    │  │   DB    │  │   DB    │
└────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘
     │            │            │            │
┌────┴────┐  ┌────┴────┐  ┌────┴────┐  ┌────┴────┐
│  User   │  │  Order  │  │  Book   │  │ Review  │
│ Service │  │ Service │  │ Service │  │ Service │
└─────────┘  └─────────┘  └─────────┘  └─────────┘
```

> 💡 **Key insight**: Take it slow! A failed migration is worse than staying on a monolith. Extract one service, validate, then proceed.

---

## Updated Summary

| Level | Questions | Topics Covered |
|-------|-----------|----------------|
| **Beginner** | Q1-Q20 | Basics, patterns, discovery, gateway |
| **Intermediate** | Q21-Q40 | Resilience, security, deployment |
| **Hard** | Q41-Q50 | Debugging, consistency, migration |

---

## 🔐 Project-Specific: Security & Monitoring (Q51-Q56)

> 📌 These questions are based on actual implementations in our Book Management Microservices project.

---

## Q51: How do you implement Spring Security in a microservices architecture?

> ✅ **Implemented in Our Project**: We use Spring Security with stateless JWT authentication across all services.

**Answer:**

### 🎯 Simple Analogy: Airport Security

Think of security in microservices like airport security:
- **Authentication Service** = Check-in counter (verifies your identity, gives you a boarding pass/JWT)
- **API Gateway** = Security checkpoint (checks your boarding pass is valid)
- **Microservices** = Gates/Lounges (trust that security already checked you)

### How It Works in Microservices:

**1. Centralized Authentication Service:**
```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTHENTICATION FLOW                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Client                API Gateway          Auth Service               │
│     │                       │                     │                      │
│     │── Login Request ─────▶│──── Validate ──────▶│                      │
│     │                       │                     │ ✓ Check credentials  │
│     │                       │◀─── JWT Token ──────│ ✓ Generate JWT       │
│     │◀── JWT Token ─────────│                     │                      │
│     │                                                                    │
│   [Subsequent Requests]                                                  │
│     │                       │                                            │
│     │── Request + JWT ─────▶│ ✓ Validate JWT                            │
│     │                       │ ✓ Extract user info                       │
│     │                       │ ✓ Forward to service                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**2. Our Project Implementation:**
- `authentication-service`: Handles login, JWT generation
- `api-gateway`: Validates JWT on every request via `AuthenticationFilter`
- Backend services: Trust validated requests from gateway

**Key Security Config:**
```java
// SecurityConfig in authentication-service
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http
        .csrf(csrf -> csrf.disable())  // Stateless, so no CSRF needed
        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated())
        .build();
}
```

---

## Q52: How do you implement Role-Based Access Control (RBAC) in microservices?

> ✅ **Implemented in Our Project**: We have comprehensive RBAC rules configured in `api-gateway.properties` for ADMIN and CUSTOMER roles.

**Answer:**

### 🎯 Simple Analogy: Movie Theater Access

Think of RBAC like a movie theater:
- **CUSTOMER** = Regular ticket holder (can watch movies, buy popcorn)
- **ADMIN** = Theater manager (can watch movies + access projection room, change schedules)
- Each area has different access rules based on your "role"

### What is RBAC?

RBAC controls access to resources based on user roles. Instead of checking each user individually, you assign users to roles, and roles have permissions.

**Our Project's RBAC Configuration (`api-gateway.properties`):**
```properties
# Enable RBAC
rbac.enabled=true

# Only ADMIN can add/update/delete books
rbac.rules[5].path=/api/v1/book/add
rbac.rules[5].methods=POST
rbac.rules[5].roles=ADMIN

rbac.rules[6].path=/api/v1/book/update/**
rbac.rules[6].methods=PATCH
rbac.rules[6].roles=ADMIN

# Only ADMIN can manage inventory
rbac.rules[8].path=/api/v1/inventory/create
rbac.rules[8].methods=POST
rbac.rules[8].roles=ADMIN

# CUSTOMER can place orders (default: allow authenticated)
# Both roles can create reviews
```

**How It Works:**
```
┌────────────────────────────────────────────────────────────────────┐
│                      RBAC FLOW                                       │
├────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  Request: POST /api/v1/book/add                                     │
│  JWT Contains: roles=["CUSTOMER"]                                   │
│                                                                      │
│  Gateway checks:                                                    │
│    1. Path matches /api/v1/book/add? ✓                              │
│    2. Method is POST? ✓                                             │
│    3. User has ADMIN role? ❌                                       │
│                                                                      │
│  Result: 403 FORBIDDEN                                              │
│                                                                      │
└────────────────────────────────────────────────────────────────────┘
```

---

## Q53: How does JWT Authentication work in your project?

> ✅ **Implemented in Our Project**: Complete JWT implementation in `authentication-service` with `JwtUtil.java`.

**Answer:**

### 🎯 Simple Analogy: Wristband at a Concert

JWT is like an event wristband:
- **Getting the wristband** = Logging in (you show ID, they give you a wristband)
- **Wearing the wristband** = Carrying JWT in every request
- **Security checking wristband** = Server validating JWT
- **Wristband color indicates access level** = JWT contains your role (CUSTOMER/ADMIN)
- **Wristband expires at midnight** = JWT has expiration time

### What is JWT?

JWT (JSON Web Token) is a compact, self-contained way to securely transmit information as a JSON object. It's "self-contained" because the token itself carries all the user information.

**JWT Structure (like a 3-part code):**
```
┌─────────────────────────────────────────────────────────────────────┐
│  HEADER           PAYLOAD                    SIGNATURE              │
│  ──────           ───────                    ─────────              │
│  {                {                          HMACSHA256(            │
│    "alg": "HS256"   "sub": "user@email.com"   base64(header) +     │
│    "typ": "JWT"     "userId": "123"           "." +                │
│  }                  "roles": ["CUSTOMER"]     base64(payload),     │
│                     "iat": 1699000000         secret                │
│                     "exp": 1699086400       )                       │
│                   }                                                 │
└─────────────────────────────────────────────────────────────────────┘
```

**Our JwtUtil Implementation:**
```java
// Extracting claims from JWT
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

// Validating token
public Boolean validateToken(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (ExpiredJwtException | MalformedJwtException e) {
        return false;
    }
}
```

**Authentication Flow:**
1. User sends login credentials → `AuthController`
2. Auth service validates credentials → generates JWT
3. Client stores JWT → includes in `Authorization: Bearer <token>`
4. API Gateway validates JWT → forwards request with user info
5. Backend services trust the gateway headers

---

## Q54: What is Single Sign-On (SSO) and how would you implement it?

> ⚠️ **Not Implemented in Our Project**: We use simple JWT authentication. SSO would be a future enhancement for enterprise deployments.

**Answer:**

### 🎯 Simple Analogy: Mall Master Key

SSO is like a mall where:
- **Without SSO**: Each store (app) asks for your ID separately
- **With SSO**: You get ONE mall pass at the entrance, and all stores accept it

### What is SSO?

**SSO** allows users to authenticate once and access multiple applications without logging in again. You log into Gmail, and you're automatically logged into YouTube, Google Drive, etc.

**SSO with OAuth 2.0/OIDC:**
```
┌─────────────────────────────────────────────────────────────────────────┐
│                       SSO FLOW (OAuth 2.0)                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  User          App A            Identity Provider         App B         │
│   │               │                    │                    │           │
│   │── Login ─────▶│                    │                    │           │
│   │               │── Redirect ───────▶│                    │           │
│   │◀── Login Page ─────────────────────│                    │           │
│   │── Credentials ────────────────────▶│                    │           │
│   │               │◀─── Auth Code ─────│                    │           │
│   │               │── Exchange ───────▶│                    │           │
│   │               │◀─── ID Token ──────│                    │           │
│   │◀── Logged In ─│                    │                    │           │
│   │                                                                      │
│   │── Access App B ───────────────────────────────────────▶│           │
│   │               │                    │◀── Validate Token ─│           │
│   │               │                    │── Token Valid ────▶│           │
│   │◀── Logged In (No login prompt!) ──────────────────────│           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**To implement SSO:**
- Use OAuth 2.0 Authorization Server (Keycloak, Okta, Auth0)
- Configure services as Resource Servers
- Share identity across all microservices

---

## Q55: How do you set up monitoring with Prometheus and Grafana?

> ⚠️ **Partially Implemented**: Prometheus endpoint is exposed via Actuator. Grafana dashboards are not yet configured.

**Answer:**

### 🎯 Simple Analogy: Car Dashboard

Monitoring microservices is like a car dashboard:
- **Services** = Engine, brakes, fuel (things that need monitoring)
- **Prometheus** = Sensors collecting data (RPM, speed, fuel level)
- **Grafana** = Dashboard showing gauges and warning lights

Just like you glance at your car dashboard to know everything is working, DevOps teams look at Grafana to know all services are healthy.

### How It Works
```
┌─────────────────────────────────────────────────────────────────────────┐
│                 MONITORING ARCHITECTURE                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐               │
│  │ Order Service│    │ Book Service │    │Inventory Svc │               │
│  │ /actuator/   │    │ /actuator/   │    │ /actuator/   │               │
│  │ prometheus   │    │ prometheus   │    │ prometheus   │               │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘               │
│         │                   │                   │                        │
│         └───────────────────┼───────────────────┘                        │
│                             │                                            │
│                      ┌──────▼──────┐                                     │
│                      │ Prometheus  │  ← Scrapes metrics every 15s       │
│                      │  (Metrics   │                                     │
│                      │   Storage)  │                                     │
│                      └──────┬──────┘                                     │
│                             │                                            │
│                      ┌──────▼──────┐                                     │
│                      │   Grafana   │  ← Visualizes metrics              │
│                      │ (Dashboard) │                                     │
│                      └─────────────┘                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Our Actuator Configuration (`api-gateway.properties`):**
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus,gateway
management.endpoint.gateway.enabled=true
management.metrics.enable.gateway=true
```

**Key Metrics Available:**
| Metric | Description |
|--------|-------------|
| `http_server_requests` | Request count, latency |
| `jvm_memory_used` | Memory usage |
| `process_cpu_usage` | CPU utilization |
| `resilience4j_circuitbreaker_state` | Circuit breaker status |

---

## Q56: How do you secure inter-service communication with Gateway Secret?

> ✅ **Implemented in Our Project**: We use `X-Gateway-Secret` header pattern to ensure all traffic passes through the API Gateway.

**Answer:**

### 🎯 Simple Analogy: Office Building Receptionist

Think of it like an office building:
- **API Gateway** = Reception desk (everyone must check in here)
- **X-Gateway-Secret** = Special stamp receptionist puts on your visitor pass
- **Backend Services** = Office rooms that ONLY let in visitors with the stamped pass
- **Hacker trying direct access** = Someone trying to sneak in through a side door (no stamp = no entry!)

### The Problem

Without protection, users could bypass the API Gateway and access microservices directly.

### Our Solution - Gateway Secret Token
```
┌─────────────────────────────────────────────────────────────────────────┐
│                    GATEWAY SECRET PATTERN                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ✓ VALID REQUEST (Through Gateway):                                    │
│   ┌──────┐    ┌───────────────┐    ┌─────────────────┐                 │
│   │Client│───▶│  API Gateway  │───▶│ Backend Service │                 │
│   └──────┘    │ Adds header:  │    │ Validates:      │                 │
│               │ X-Gateway-    │    │ X-Gateway-      │                 │
│               │ Secret: xyz   │    │ Secret = xyz ✓  │                 │
│               └───────────────┘    └─────────────────┘                 │
│                                                                          │
│   ✗ REJECTED REQUEST (Direct Access):                                   │
│   ┌──────┐                         ┌─────────────────┐                 │
│   │Hacker│────────────────────────▶│ Backend Service │                 │
│   └──────┘                         │ No gateway      │                 │
│           (No X-Gateway-Secret)    │ secret header   │                 │
│                                    │ → 401 REJECTED  │                 │
│                                    └─────────────────┘                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Configuration (`api-gateway.properties`):**
```properties
gateway.secret.enabled=true
gateway.secret.header-name=X-Gateway-Secret
gateway.secret.token=${GATEWAY_SECRET_TOKEN:dev-gateway-secret-change-in-production}
```

**Backend Service Validation:**
Each service checks for the header and rejects requests that don't have it.

---

## Final Summary

| Level | Questions | Topics Covered |
|-------|-----------|----------------|
| **Beginner** | Q1-Q20 | Basics, patterns, discovery, gateway |
| **Intermediate** | Q21-Q40 | Resilience, security, deployment |
| **Hard** | Q41-Q50 | Debugging, consistency, migration |
| **Project-Specific** | Q51-Q56 | Spring Security, RBAC, JWT, SSO, Monitoring, Gateway Secret |

---

> **Next Topic:** Spring Framework

---

## 📚 Advanced Topics (Beyond Core Syllabus)

> [!NOTE]
> The following topics are more advanced and not part of the core syllabus. They are included here for reference and further learning.

---

### Event Sourcing

**What is Event Sourcing?**

Stores all changes to application state as a sequence of events rather than just the current state.

**Key Concepts:**
- Events are immutable facts
- Current state rebuilt by replaying events
- Enables audit trail and time-travel debugging

**Example:** Instead of storing "account balance = $100", store events like "deposited $50", "withdrew $20", etc.

---

### Service Mesh

**What is a Service Mesh?**

A dedicated infrastructure layer for handling service-to-service communication.

**Key Components:**
- **Data Plane**: Sidecar proxies (e.g., Envoy)
- **Control Plane**: Configuration and policy management (e.g., Istio)

**Benefits:**
- Automatic mTLS
- Traffic management
- Observability built-in

---

### Sidecar Pattern

**What is the Sidecar Pattern?**

Deploy helper components alongside your main application container.

**Use Cases:**
- Logging agents
- Monitoring collectors
- Service mesh proxies

```
┌─────────────────────────┐
│         Pod             │
│  ┌─────────┐ ┌────────┐ │
│  │  Main   │ │Sidecar │ │
│  │  App    │ │(Envoy) │ │
│  └─────────┘ └────────┘ │
└─────────────────────────┘
```

---

### REST vs gRPC

**Comparison:**

| Aspect | REST | gRPC |
|--------|------|------|
| **Protocol** | HTTP/1.1, JSON | HTTP/2, Protobuf |
| **Performance** | Good | Better (binary) |
| **Browser Support** | Native | Requires proxy |
| **Use Case** | Public APIs | Internal services |

---

### Contract Testing

**What is Contract Testing?**

Ensures that services can communicate correctly by testing the "contract" between them.

**Types:**
- **Consumer-Driven**: Consumer defines expected behavior
- **Provider-Driven**: Provider defines available functionality

**Tools:** Pact, Spring Cloud Contract

---

### Ambassador Pattern

**What is the Ambassador Pattern?**

A helper service that handles outgoing requests on behalf of the main application.

**Use Cases:**
- Retries and circuit breaking
- Logging and monitoring
- Authentication

---

### Outbox Pattern

**What is the Outbox Pattern?**

Ensures reliable event publishing by storing events in an outbox table within the same transaction.

**Flow:**
1. Save entity + event to database (same transaction)
2. Background process reads outbox table
3. Publishes events to message broker
4. Marks events as processed

**Benefit:** Guarantees exactly-once delivery even if message broker is down.

---

### Distributed Locking

**What is Distributed Locking?**

Prevents multiple service instances from accessing the same resource simultaneously.

**Tools:** Redis (Redisson), Zookeeper, Database locks

```java
@Autowired
private RedissonClient redisson;

public void processOrder(Long orderId) {
    RLock lock = redisson.getLock("order:" + orderId);
    try {
        lock.lock(10, TimeUnit.SECONDS);
        // Critical section
    } finally {
        lock.unlock();
    }
}
```

---

### Backends for Frontends (BFF) Pattern

**What is the BFF Pattern?**

Create separate backend services for different client types (mobile, web, IoT).

```
Mobile App → Mobile BFF → Microservices
Web App → Web BFF → Microservices
```

**Benefits:**
- Optimized responses per client
- Client-specific logic isolated
- Independent deployments

---

### Anti-Corruption Layer (ACL)

**What is the ACL Pattern?**

A translation layer between your system and external/legacy systems.

**Purpose:**
- Isolate your domain model from external changes
- Translate between different data formats
- Prevent legacy concepts from polluting your codebase

---

### Schema Evolution in Event-Driven Systems

**How to Handle Schema Evolution:**

1. **Schema Registry**: Centralized schema management (Confluent)
2. **Backward Compatibility**: New consumers read old events
3. **Forward Compatibility**: Old consumers read new events
4. **Versioning**: Multiple schema versions
5. **Avro/Protobuf**: Built-in evolution support

---

### Split Brain Problem

**What is the Split Brain Problem?**

Network partition causes cluster to split into two groups, both thinking they're the leader.

**Solutions:**
- **Quorum-based decisions**: Majority required for writes
- **Fencing tokens**: Monotonically increasing tokens
- **Lease-based leadership**: Leaders have time-limited leases

---

### Reactive vs Traditional Microservices

**Comparison:**

| Aspect | Traditional | Reactive |
|--------|-------------|----------|
| **Threading** | Thread-per-request | Event loop |
| **Blocking** | Yes | Non-blocking |
| **Scalability** | Limited by threads | Highly scalable |
| **Frameworks** | Spring MVC | Spring WebFlux |
| **Use Case** | CRUD apps | High-throughput, streaming |

---

## Summary of All Topics

| Level | Questions | Topics Covered |
|-------|-----------|----------------|
| **Beginner** | Q1-Q20 | Basics, patterns, discovery, gateway |
| **Intermediate** | Q21-Q40 | Resilience, security, deployment |
| **Hard** | Q41-Q50 | Debugging, consistency, migration |
| **Project-Specific** | Q51-Q56 | Spring Security, RBAC, JWT, SSO, Monitoring, Gateway Secret |
| **Advanced** | 13 topics | Event Sourcing, Service Mesh, gRPC, Contract Testing, etc. |

