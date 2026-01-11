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

## Q6: What is the Saga Pattern? When would you use it?

**Answer:**

**Saga Pattern** is a way to manage distributed transactions across microservices where traditional ACID transactions are not possible.

### The Problem:

```
┌─────────────────────────────────────────────────────────────────────────┐
│               THE DISTRIBUTED TRANSACTION PROBLEM                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   In a monolith (EASY):                                                  │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  @Transactional                                                    ││
│   │  public void placeOrder(OrderDTO dto) {                            ││
│   │      userService.deductBalance(userId, amount);   // Step 1        ││
│   │      inventoryService.reduceStock(bookId, qty);   // Step 2        ││
│   │      orderService.createOrder(dto);               // Step 3        ││
│   │      // If Step 3 fails, Steps 1 & 2 are AUTOMATICALLY rolled back││
│   │  }                                                                 ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   In microservices (PROBLEM):                                            │
│   Each service has its OWN database!                                     │
│   @Transactional doesn't span multiple databases!                        │
│                                                                          │
│   User Service ───┐     Inventory Service ───┐     Order Service ───┐   │
│   ┌─────────────┐ │     ┌─────────────────┐  │     ┌──────────────┐ │   │
│   │ User DB     │ │     │ Inventory DB    │  │     │ Order DB     │ │   │
│   └─────────────┘ │     └─────────────────┘  │     └──────────────┘ │   │
│                   │                          │                       │   │
│   Step 1: OK! ────┘     Step 2: OK! ─────────┘     Step 3: FAIL! ───┘   │
│                                                                          │
│   PROBLEM: How do we rollback Steps 1 & 2?                              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Saga Pattern Solution:

A **Saga** is a sequence of local transactions where each step has a **compensating transaction** to undo its effects.

### Types of Saga:

#### 1. Choreography-based Saga (Event-driven):

```
┌─────────────────────────────────────────────────────────────────────────┐
│               CHOREOGRAPHY SAGA (Event-Driven)                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ┌─────────────┐    ORDER_CREATED    ┌─────────────────┐               │
│   │   Order     │ ──────────────────▶ │   Inventory     │               │
│   │   Service   │                     │   Service       │               │
│   └─────────────┘                     └────────┬────────┘               │
│         ▲                                      │                         │
│         │           STOCK_RESERVED             │                         │
│         │ ◀────────────────────────────────────┘                         │
│         │                                                                │
│         │ STOCK_RESERVED    ┌─────────────────┐                         │
│         └─────────────────▶ │   Payment       │                         │
│                             │   Service       │                         │
│                             └────────┬────────┘                         │
│                                      │                                   │
│   ┌─────────────┐  PAYMENT_SUCCESS   │                                   │
│   │   Order     │ ◀──────────────────┘                                   │
│   │   Service   │                                                        │
│   │ (Completes) │                                                        │
│   └─────────────┘                                                        │
│                                                                          │
│   IF PAYMENT FAILS: Payment Service publishes PAYMENT_FAILED             │
│   Inventory Service listens → publishes STOCK_RELEASED (compensate)     │
│   Order Service listens → updates order to CANCELLED                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 2. Orchestration-based Saga (Central coordinator):

```
┌─────────────────────────────────────────────────────────────────────────┐
│              ORCHESTRATION SAGA (Central Coordinator)                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                        ┌─────────────────────┐                          │
│                        │   Saga Orchestrator │                          │
│                        │   (Order Saga)      │                          │
│                        └─────────┬───────────┘                          │
│                                  │                                       │
│        Step 1: Create Order      │                                       │
│        ┌─────────────────────────┼───────────────────────────┐          │
│        ▼                         ▼                           ▼          │
│   ┌──────────┐            ┌──────────────┐            ┌──────────┐     │
│   │  Order   │            │  Inventory   │            │ Payment  │     │
│   │ Service  │            │  Service     │            │ Service  │     │
│   └──────────┘            └──────────────┘            └──────────┘     │
│        │                         │                           │          │
│        │ OK                      │ OK                        │ FAIL     │
│        └─────────────────────────┼───────────────────────────┘          │
│                                  │                                       │
│                                  ▼                                       │
│                        ┌─────────────────────┐                          │
│                        │   Orchestrator      │                          │
│                        │   DETECTS FAILURE   │                          │
│                        │                     │                          │
│                        │   Triggers:         │                          │
│                        │   - cancelOrder()   │                          │
│                        │   - releaseStock()  │                          │
│                        └─────────────────────┘                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Comparison:

| Aspect | Choreography | Orchestration |
|--------|--------------|---------------|
| **Coordination** | Decentralized (events) | Centralized (orchestrator) |
| **Coupling** | Loose | Tighter (to orchestrator) |
| **Complexity** | Harder to track flow | Easier to understand |
| **Single Point of Failure** | No | Yes (orchestrator) |
| **Best For** | Simple sagas | Complex multi-step sagas |

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

## Q21: What is Event Sourcing?

**Answer:**
Instead of storing current state, **Event Sourcing** stores all state changes as a sequence of events.

**Traditional:** Store current balance = $100
**Event Sourcing:** Store events:
- Deposit $50
- Withdraw $20
- Deposit $70

**Benefits:**
- Complete audit trail
- Can rebuild state at any point
- Natural fit for event-driven architectures

**Challenges:**
- More complex queries
- Event schema evolution
- Storage requirements

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

## Q25: What is Service Mesh?

**Answer:**
A **Service Mesh** is a dedicated infrastructure layer for handling service-to-service communication.

**Features:**
- Traffic management
- Security (mTLS)
- Observability
- Load balancing

**Popular solutions:**
- Istio
- Linkerd
- Consul Connect

**Architecture:**
- Data plane: Sidecar proxies (Envoy)
- Control plane: Configuration and policies

---

## Q26: What is the Sidecar Pattern?

**Answer:**
Deploy helper components alongside the main service in a separate container.

```
┌─────────────────────────────┐
│         POD                  │
│  ┌─────────┐  ┌──────────┐  │
│  │ Main    │  │ Sidecar  │  │
│  │ Service │──│ (Envoy)  │  │
│  └─────────┘  └──────────┘  │
└─────────────────────────────┘
```

**Use cases:**
- Logging agents
- Proxy for network traffic
- Config watchers
- Security agents

---

## Q27: What is the difference between REST and gRPC?

**Answer:**

| Aspect | REST | gRPC |
|--------|------|------|
| **Protocol** | HTTP/1.1 | HTTP/2 |
| **Data Format** | JSON (text) | Protobuf (binary) |
| **Performance** | Slower | Faster (10x) |
| **Browser Support** | Native | Requires proxy |
| **Streaming** | Limited | Bidirectional |
| **Contract** | OpenAPI (optional) | Required (.proto) |

**Use REST for:** Public APIs, browser clients
**Use gRPC for:** Internal microservice communication, performance-critical

---

## Q28: What is Contract Testing?

**Answer:**
Verifies that services can communicate correctly by testing the "contract" between consumer and provider.

**Tools:** Pact, Spring Cloud Contract

**How it works:**
1. Consumer defines expected request/response
2. Provider verifies it can fulfill the contract
3. Both sides run tests independently

**Benefits:**
- Catch integration issues early
- No need for running all services
- Fast feedback

---

## Q29: What is the Ambassador Pattern?

**Answer:**
An **Ambassador** is a helper service that handles network-related tasks on behalf of the main service.

**Responsibilities:**
- Retries and circuit breaking
- Monitoring and logging
- Authentication
- TLS termination

Similar to Sidecar but specifically for network communication.

---

## Q30: How do you secure microservices communication?

**Answer:**

**1. API Gateway Security:**
- JWT validation
- Rate limiting
- IP whitelisting

**2. Service-to-Service:**
- Mutual TLS (mTLS)
- Service mesh (Istio)
- API keys for internal services

**3. Data Security:**
- Encrypt sensitive data
- Use secrets management (Vault)
- Principle of least privilege

```properties
# Example: JWT validation in API Gateway
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://auth-server
```

---

## 🟠 Intermediate-Hard Level Questions (Q31-Q40)

---

## Q31: What is the difference between Orchestration and Choreography?

**Answer:**

| Aspect | Orchestration | Choreography |
|--------|---------------|--------------|
| **Control** | Central coordinator | No central control |
| **Communication** | Command-based | Event-based |
| **Coupling** | Services coupled to orchestrator | Loosely coupled |
| **Visibility** | Easy to track flow | Harder to trace |
| **Best for** | Complex workflows | Simple event reactions |

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

## Q33: What is the Outbox Pattern?

**Answer:**
Ensures reliable event publishing by storing events in an outbox table within the same transaction.

**Flow:**
1. Save entity + event to database (same transaction)
2. Background process reads outbox table
3. Publishes events to message broker
4. Marks events as processed

**Benefit:** Guarantees exactly-once delivery even if message broker is down.

---

## Q34: How do you implement distributed locking?

**Answer:**
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

## Q35: What is the Backends for Frontends (BFF) Pattern?

**Answer:**
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

## Q36: How do you handle data consistency across microservices?

**Answer:**

| Strategy | Description | Use Case |
|----------|-------------|----------|
| **Eventual Consistency** | Data syncs over time via events | Most cases |
| **Saga Pattern** | Distributed transactions with compensation | Orders, payments |
| **Two-Phase Commit** | Strong consistency (avoid if possible) | Rarely used |
| **CQRS + Events** | Separate read/write with event sync | Read-heavy apps |

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

## Q39: What is the Anti-Corruption Layer (ACL) pattern?

**Answer:**
A translation layer between your system and external/legacy systems.

**Purpose:**
- Isolate your domain model from external changes
- Translate between different data formats
- Prevent legacy concepts from polluting your codebase

---

## Q40: How do you handle service versioning during deployments?

**Answer:**

**Blue-Green Deployment:**
- Two identical environments (Blue and Green)
- Switch traffic instantly between them

**Rolling Deployment:**
- Gradually replace instances one by one
- No downtime

**Feature Flags:**
- Toggle features without deployment
- A/B testing capability

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

**Definition:** Data will be consistent eventually, but not immediately.

**Trade-offs:**

| Pros | Cons |
|------|------|
| High availability | Temporary inconsistency |
| Better performance | Complex conflict resolution |
| Partition tolerant | Harder to reason about |

**CAP Theorem:** You can only have 2 of 3: Consistency, Availability, Partition tolerance.

---

## Q43: How do you handle cross-cutting concerns in microservices?

**Answer:**

| Concern | Solution |
|---------|----------|
| **Logging** | Centralized logging (ELK), correlation IDs |
| **Security** | API Gateway, OAuth2/JWT |
| **Monitoring** | Prometheus, Grafana |
| **Tracing** | Zipkin, Jaeger |
| **Configuration** | Config Server |

**Implementation:** Service Mesh (Istio) or shared libraries

---

## Q44: How do you design for failure in microservices?

**Answer:**

1. **Circuit Breakers**: Fail fast, prevent cascade
2. **Timeouts**: Never wait forever
3. **Retries with Backoff**: Handle transient failures
4. **Bulkheads**: Isolate failures
5. **Fallbacks**: Graceful degradation
6. **Health Checks**: Detect failures quickly
7. **Chaos Engineering**: Test failure scenarios

---

## Q45: What is the difference between API Composition and Event-driven queries?

**Answer:**

| Aspect | API Composition | Event-Driven |
|--------|-----------------|--------------|
| **Pattern** | Aggregate at query time | Pre-aggregated views |
| **Latency** | Higher (multiple calls) | Lower (single read) |
| **Consistency** | Strong | Eventual |
| **Complexity** | Simpler to implement | More infrastructure |

---

## Q46: How do you handle schema evolution in event-driven systems?

**Answer:**

1. **Schema Registry**: Centralized schema management (Confluent)
2. **Backward Compatibility**: New consumers read old events
3. **Forward Compatibility**: Old consumers read new events
4. **Versioning**: Multiple schema versions
5. **Avro/Protobuf**: Built-in evolution support

---

## Q47: Explain the Split Brain problem and how to handle it

**Answer:**

**Problem:** Network partition causes cluster to split into two groups, both thinking they're the leader.

**Solutions:**
- **Quorum-based decisions**: Majority required for writes
- **Fencing tokens**: Monotonically increasing tokens
- **Lease-based leadership**: Leaders have time-limited leases

---

## Q48: How do you implement zero-downtime database migrations?

**Answer:**

1. **Expand-Contract Pattern:**
   - Add new columns (nullable)
   - Deploy code that writes to both
   - Migrate existing data
   - Deploy code that reads from new
   - Remove old columns

2. **Tools:** Flyway, Liquibase with proper versioning

---

## Q49: What is the difference between Reactive and Traditional microservices?

**Answer:**

| Aspect | Traditional | Reactive |
|--------|-------------|----------|
| **Threading** | Thread-per-request | Event loop |
| **Blocking** | Yes | Non-blocking |
| **Scalability** | Limited by threads | Highly scalable |
| **Frameworks** | Spring MVC | Spring WebFlux |
| **Use Case** | CRUD apps | High-throughput, streaming |

---

## Q50: How would you migrate a monolith to microservices?

**Answer:**

**Strategy:**
1. **Identify Bounded Contexts**: Domain-driven design
2. **Strangler Fig Pattern**: Gradual migration
3. **Start with least coupled modules**: Extract incrementally
4. **Build events/APIs**: Communication layer first
5. **Database decomposition**: Split shared database last

**Anti-patterns to avoid:**
- Big bang rewrite
- Distributed monolith
- Shared database between services

---

## Updated Summary

| Level | Questions | Topics Covered |
|-------|-----------|----------------|
| **Beginner** | Q1-Q20 | Basics, patterns, discovery, gateway |
| **Intermediate** | Q21-Q40 | Resilience, security, deployment |
| **Hard** | Q41-Q50 | Debugging, consistency, migration |

---

> **Next Topic:** Spring Framework
