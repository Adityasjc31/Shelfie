# 🎯 Topic 10: AOP Terminologies - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Aspect-Oriented Programming (AOP), including concepts, terminologies, and Spring AOP implementation.

---

## Q1: What is AOP? Why do we need it?

**Answer:**

**AOP (Aspect-Oriented Programming)** is a programming paradigm that aims to increase modularity by separating cross-cutting concerns from business logic.

### The Problem (Without AOP):

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    WITHOUT AOP - Code Duplication                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   public class UserService {                                             │
│       public User createUser(User user) {                               │
│           log.info("Method: createUser, Args: " + user);  // Logging   │
│           if (!authorized()) throw new UnauthorizedException();  // Sec │
│           long start = System.currentTimeMillis();        // Timing     │
│           try {                                                          │
│               // ⭐ ACTUAL BUSINESS LOGIC                               │
│               User result = userRepository.save(user);                   │
│               return result;                                             │
│           } finally {                                                    │
│               log.info("Time: " + (System.currentTimeMillis()-start)); │
│           }                                                              │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   public class OrderService {                                            │
│       public Order createOrder(Order order) {                           │
│           log.info("Method: createOrder, Args: " + order);  // Same!   │
│           if (!authorized()) throw new UnauthorizedException();         │
│           long start = System.currentTimeMillis();                      │
│           try {                                                          │
│               // ⭐ ACTUAL BUSINESS LOGIC                               │
│               Order result = orderRepository.save(order);               │
│               return result;                                             │
│           } finally {                                                    │
│               log.info("Time: " + (System.currentTimeMillis()-start)); │
│           }                                                              │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ❌ PROBLEMS:                                                           │
│   • Same logging/security/timing code in EVERY method                   │
│   • Hard to maintain (change in 100 places!)                            │
│   • Business logic buried in boilerplate                                │
│   • Violates DRY principle                                              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### The Solution (With AOP):

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    WITH AOP - Clean Separation                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   public class UserService {                                             │
│       public User createUser(User user) {                               │
│           // ⭐ ONLY BUSINESS LOGIC - Clean!                            │
│           return userRepository.save(user);                             │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   public class OrderService {                                            │
│       public Order createOrder(Order order) {                           │
│           // ⭐ ONLY BUSINESS LOGIC - Clean!                            │
│           return orderRepository.save(order);                           │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   @Aspect  // Cross-cutting concerns in ONE place!                      │
│   public class LoggingAspect {                                          │
│       @Around("execution(* com.app.service.*.*(..))")                   │
│       public Object logMethod(ProceedingJoinPoint pjp) {                │
│           log.info("Method: {}, Args: {}", pjp.getSignature(), args);  │
│           long start = System.currentTimeMillis();                      │
│           Object result = pjp.proceed();                                │
│           log.info("Time: {}ms", System.currentTimeMillis() - start);  │
│           return result;                                                 │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ✅ BENEFITS:                                                           │
│   • Logging defined ONCE, applied to ALL methods                        │
│   • Business logic is clean and focused                                 │
│   • Easy to modify (change in ONE place)                                │
│   • Follows Single Responsibility Principle                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Cross-Cutting Concerns:

| Concern | Description |
|---------|-------------|
| **Logging** | Log method entry/exit, parameters, timing |
| **Security** | Authentication/authorization checks |
| **Transaction Management** | Begin/commit/rollback transactions |
| **Caching** | Cache method results |
| **Exception Handling** | Centralized error handling |
| **Performance Monitoring** | Track execution times |
| **Auditing** | Record who did what when |

---

## Q2: Explain the key AOP terminologies.

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       AOP TERMINOLOGIES                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. ASPECT                                                              │
│   ──────────                                                             │
│   A module containing cross-cutting concerns                            │
│   (Like a class for cross-cutting logic)                                │
│                                                                          │
│   @Aspect                                                                │
│   @Component                                                             │
│   public class LoggingAspect {                                          │
│       // Contains advice and pointcuts                                  │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. JOIN POINT                                                          │
│   ──────────────                                                         │
│   A point during program execution where aspect can be applied         │
│   (Method execution, exception handling, field access)                  │
│   In Spring AOP: Only method execution                                  │
│                                                                          │
│   Examples:                                                              │
│   • When userService.createUser() is called                             │
│   • When orderService.processOrder() is called                          │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. POINTCUT                                                            │
│   ───────────                                                            │
│   Expression that selects join points where advice should run          │
│   (WHEN and WHERE to apply the aspect)                                  │
│                                                                          │
│   @Pointcut("execution(* com.app.service.*.*(..))")                     │
│   public void serviceLayer() {}                                          │
│                                                                          │
│   This matches: All methods in service package                          │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   4. ADVICE                                                              │
│   ─────────                                                              │
│   The action taken at a join point (WHAT to do)                         │
│                                                                          │
│   Types:                                                                 │
│   • @Before     - Run before method execution                           │
│   • @After      - Run after method (regardless of outcome)              │
│   • @AfterReturning - Run after successful return                       │
│   • @AfterThrowing  - Run after exception is thrown                     │
│   • @Around     - Wrap around method (most powerful)                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   5. TARGET OBJECT                                                       │
│   ────────────────                                                       │
│   The original object being advised                                      │
│   (The actual UserService instance)                                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   6. PROXY                                                               │
│   ─────────                                                              │
│   Object created by AOP framework to implement aspects                  │
│   (Wraps target object, intercepts calls)                               │
│                                                                          │
│   [Caller] → [Proxy (with aspects)] → [Target Object]                   │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   7. WEAVING                                                             │
│   ──────────                                                             │
│   Process of linking aspects with target objects                        │
│                                                                          │
│   Types:                                                                 │
│   • Compile-time: AspectJ compiler                                      │
│   • Load-time: When class is loaded                                     │
│   • Runtime: Spring AOP uses proxies at runtime                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Visual Summary:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    HOW IT ALL FITS TOGETHER                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   @Aspect                        ← ASPECT (the module)                  │
│   public class LoggingAspect {                                          │
│                                                                          │
│       @Pointcut("execution(* com.app.service.*.*(..))")                 │
│       public void serviceLayer() {}    ← POINTCUT (where to apply)     │
│                                                                          │
│       @Before("serviceLayer()")        ← ADVICE (what to do)           │
│       public void logBefore(JoinPoint jp) {  ← JOIN POINT (when)       │
│           log.info("Calling: " + jp.getSignature());                    │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   At runtime:                                                            │
│   ┌──────────┐     ┌────────────────┐     ┌────────────────┐            │
│   │  Caller  │ ──▶ │     PROXY      │ ──▶ │ TARGET OBJECT  │            │
│   │          │     │ (runs advice)   │     │ (UserService)  │            │
│   └──────────┘     └────────────────┘     └────────────────┘            │
│                                                                          │
│   Weaving happens at runtime when Spring creates the proxy             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q3: Explain the different types of Advice.

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       ADVICE TYPES                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   METHOD EXECUTION TIMELINE:                                             │
│                                                                          │
│   ─────●─────────────────────────────────────────●───────────────────   │
│        │                                          │                      │
│        │  @Before                                 │  @After             │
│        │  (runs first)                            │  (runs last)        │
│        │                                          │                      │
│        ▼                                          │                      │
│   ┌─────────────────────────────────────────────┐ │                      │
│   │          TARGET METHOD EXECUTION            │ │                      │
│   │                                             │ │                      │
│   │  try {                                      │ │                      │
│   │      // Method body                         │ │                      │
│   │      return result;  ────────▶ @AfterReturning                      │
│   │  } catch (Exception e) {                    │ │                      │
│   │      throw e;        ────────▶ @AfterThrowing                       │
│   │  }                                          │ │                      │
│   └─────────────────────────────────────────────┘ │                      │
│                                                   │                      │
│        ◀──────────────────────────────────────────┘                      │
│                                                                          │
│   @Around: Wraps ENTIRE execution (most powerful)                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Examples:

```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    // ═══════════════════════════════════════════════════════════════════
    // @BEFORE - Runs BEFORE method execution
    // Use for: Logging, validation, security checks
    // ═══════════════════════════════════════════════════════════════════
    @Before("execution(* com.app.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("BEFORE: Calling {} with args: {}", 
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs()));
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // @AFTER - Runs AFTER method (regardless of success/failure)
    // Like "finally" block - always executes
    // Use for: Cleanup, releasing resources
    // ═══════════════════════════════════════════════════════════════════
    @After("execution(* com.app.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("AFTER: Completed {}", joinPoint.getSignature().getName());
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // @AFTERRETURNING - Runs after SUCCESSFUL completion
    // Can access the return value
    // Use for: Logging results, post-processing
    // ═══════════════════════════════════════════════════════════════════
    @AfterReturning(
        pointcut = "execution(* com.app.service.*.*(..))",
        returning = "result"  // Bind return value
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("AFTER RETURNING: {} returned: {}", 
            joinPoint.getSignature().getName(), result);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // @AFTERTHROWING - Runs after exception is thrown
    // Can access the exception
    // Use for: Error logging, notification
    // ═══════════════════════════════════════════════════════════════════
    @AfterThrowing(
        pointcut = "execution(* com.app.service.*.*(..))",
        throwing = "exception"  // Bind exception
    )
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        log.error("AFTER THROWING: {} threw: {}", 
            joinPoint.getSignature().getName(), exception.getMessage());
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // @AROUND - Wraps entire method execution (MOST POWERFUL)
    // Can: control execution, modify args/return, handle exceptions
    // Must call pjp.proceed() to execute target method
    // Use for: Timing, caching, transactions, comprehensive logging
    // ═══════════════════════════════════════════════════════════════════
    @Around("execution(* com.app.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        
        log.info("AROUND: Before {}", methodName);
        long startTime = System.currentTimeMillis();
        
        try {
            // Execute the target method
            Object result = pjp.proceed();  // MUST call this!
            
            log.info("AROUND: {} returned: {}", methodName, result);
            return result;
            
        } catch (Exception e) {
            log.error("AROUND: {} threw: {}", methodName, e.getMessage());
            throw e;  // Re-throw or handle
            
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("AROUND: {} took {}ms", methodName, duration);
        }
    }
}
```

### Comparison:

| Advice Type | When | Access To | Use Case |
|-------------|------|-----------|----------|
| **@Before** | Before method | Arguments | Logging, validation |
| **@After** | After (always) | Nothing | Cleanup |
| **@AfterReturning** | After success | Return value | Post-processing |
| **@AfterThrowing** | After exception | Exception | Error logging |
| **@Around** | Wraps entire | Everything | Timing, caching |

---

## Q4: Explain Pointcut expressions with examples.

**Answer:**

Pointcut expressions define **where** advice should be applied using patterns.

### Execution Expression Syntax:

```
execution(modifiers? return-type declaring-type? method-name(params) throws?)

execution(public String com.app.service.UserService.findById(Long))
          │       │      │                         │         │
          │       │      │                         │         └── Parameter type
          │       │      │                         └── Method name
          │       │      └── Class (fully qualified)
          │       └── Return type
          └── Access modifier (optional)
```

### Common Pointcut Expressions:

```java
@Aspect
@Component
public class PointcutExamples {
    
    // ═══════════════════════════════════════════════════════════════════
    // MATCH ALL METHODS IN A PACKAGE
    // ═══════════════════════════════════════════════════════════════════
    
    // All methods in service package
    @Pointcut("execution(* com.app.service.*.*(..))")
    public void allServiceMethods() {}
    
    // All methods in service package and sub-packages (..)
    @Pointcut("execution(* com.app.service..*.*(..))")
    public void allServiceAndSubPackages() {}
    
    
    // ═══════════════════════════════════════════════════════════════════
    // MATCH BY METHOD NAME
    // ═══════════════════════════════════════════════════════════════════
    
    // Methods starting with "get"
    @Pointcut("execution(* com.app.*.get*(..))")
    public void getterMethods() {}
    
    // Methods starting with "set"
    @Pointcut("execution(* com.app.*.set*(..))")
    public void setterMethods() {}
    
    // Methods named exactly "save"
    @Pointcut("execution(* com.app..save(..))")
    public void saveMethods() {}
    
    
    // ═══════════════════════════════════════════════════════════════════
    // MATCH BY RETURN TYPE
    // ═══════════════════════════════════════════════════════════════════
    
    // Methods returning void
    @Pointcut("execution(void com.app..*.*(..))")
    public void voidMethods() {}
    
    // Methods returning User
    @Pointcut("execution(com.app.model.User com.app..*.*(..))")
    public void methodsReturningUser() {}
    
    // Methods returning any type (*)
    @Pointcut("execution(* com.app..*.*(..))")
    public void anyReturnType() {}
    
    
    // ═══════════════════════════════════════════════════════════════════
    // MATCH BY PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    // No parameters
    @Pointcut("execution(* com.app..*.*())")
    public void noArgMethods() {}
    
    // One parameter of any type
    @Pointcut("execution(* com.app..*.*(*))") 
    public void oneArgMethods() {}
    
    // Any number of parameters (..)
    @Pointcut("execution(* com.app..*.*(..))") 
    public void anyArgMethods() {}
    
    // First param is Long
    @Pointcut("execution(* com.app..*.*(Long, ..))") 
    public void firstParamLong() {}
    
    // Specific parameter types
    @Pointcut("execution(* com.app.*.*(String, int))") 
    public void stringAndIntParams() {}
    
    
    // ═══════════════════════════════════════════════════════════════════
    // MATCH BY ANNOTATION
    // ═══════════════════════════════════════════════════════════════════
    
    // Methods annotated with @Transactional
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}
    
    // Custom annotation
    @Pointcut("@annotation(com.app.annotation.Loggable)")
    public void loggableMethods() {}
    
    // Classes annotated with @Service
    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceClasses() {}
    
    // Classes annotated with @RestController
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerClasses() {}
    
    
    // ═══════════════════════════════════════════════════════════════════
    // WITHIN - Match by class/package
    // ═══════════════════════════════════════════════════════════════════
    
    @Pointcut("within(com.app.service.UserService)")
    public void withinUserService() {}
    
    @Pointcut("within(com.app.service..*)")
    public void withinServicePackage() {}
    
    
    // ═══════════════════════════════════════════════════════════════════
    // COMBINING POINTCUTS
    // ═══════════════════════════════════════════════════════════════════
    
    @Pointcut("allServiceMethods() && !getterMethods()")
    public void serviceMethodsExceptGetters() {}
    
    @Pointcut("allServiceMethods() || controllerClasses()")
    public void serviceOrController() {}
    
    @Pointcut("execution(* com.app..*.*(..)) && @annotation(Loggable)")
    public void loggableInApp() {}
}
```

### Wildcards:

| Wildcard | Meaning | Example |
|----------|---------|---------|
| `*` | Any single thing | `* com.app.*.get*(..)` |
| `..` | Any number/depth | `com.app.service..*` (subpackages) |
| `..` | Any parameters | `method(..)` |

---

## Q5: How does Spring AOP work internally (Proxy mechanism)?

**Answer:**

Spring AOP uses **proxy objects** to implement aspects at runtime.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING AOP PROXY MECHANISM                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WITHOUT AOP:                                                           │
│                                                                          │
│   ┌──────────────┐           ┌────────────────────┐                     │
│   │   Client     │ ────────▶ │   UserService      │                     │
│   │              │           │   (Target Object)  │                     │
│   └──────────────┘           └────────────────────┘                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   WITH AOP:                                                              │
│                                                                          │
│   ┌──────────────┐     ┌───────────────────────┐     ┌─────────────────┐│
│   │   Client     │ ──▶ │      PROXY            │ ──▶ │  UserService    ││
│   │              │     │                       │     │ (Target Object) ││
│   └──────────────┘     │  1. @Before advice    │     └─────────────────┘│
│                        │  2. Call target       │                        │
│                        │  3. @After advice     │                        │
│                        └───────────────────────┘                        │
│                                                                          │
│   The client thinks it's calling UserService,                           │
│   but actually calls the Proxy which executes advice!                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Proxy Types:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    JDK DYNAMIC PROXY vs CGLIB PROXY                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. JDK DYNAMIC PROXY (Interface-based):                                │
│   ──────────────────────────────────────                                 │
│   Used when: Target implements an interface                             │
│                                                                          │
│   interface UserService {                                                │
│       User findById(Long id);                                           │
│   }                                                                      │
│                                                                          │
│   class UserServiceImpl implements UserService {                        │
│       public User findById(Long id) { ... }                             │
│   }                                                                      │
│                                                                          │
│   ┌─────────────────────┐                                                │
│   │   <<interface>>     │                                                │
│   │    UserService      │                                                │
│   └──────────┬──────────┘                                                │
│              │                                                           │
│       ┌──────┴──────┐                                                    │
│       │             │                                                    │
│   ┌───▼───┐     ┌───▼───────────┐                                        │
│   │ Proxy │     │UserServiceImpl│                                        │
│   │ (JDK) │     │   (Target)    │                                        │
│   └───────┘     └───────────────┘                                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. CGLIB PROXY (Subclass-based):                                       │
│   ────────────────────────────────                                       │
│   Used when: Target does NOT implement interface                        │
│   Creates: Subclass of target class                                     │
│                                                                          │
│   class UserService {  // No interface!                                 │
│       public User findById(Long id) { ... }                             │
│   }                                                                      │
│                                                                          │
│   ┌─────────────────┐                                                    │
│   │   UserService   │                                                    │
│   │   (Target)      │                                                    │
│   └────────┬────────┘                                                    │
│            │ extends                                                     │
│   ┌────────▼────────┐                                                    │
│   │  UserService$$  │                                                    │
│   │  EnhancerByCGLIB│  ← Dynamically generated subclass                 │
│   │  (Proxy)        │                                                    │
│   └─────────────────┘                                                    │
│                                                                          │
│   ⚠️ Cannot proxy final classes or methods!                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Internal Call Problem:

```java
@Service
public class UserService {
    
    @Transactional  // This will work
    public void methodA() {
        this.methodB();  // ⚠️ Internal call - @Transactional won't work!
    }
    
    @Transactional  // This won't be triggered from methodA!
    public void methodB() {
        // Transaction not started when called from methodA!
    }
}

// WHY? Because 'this' refers to the actual object, not the proxy!
// Proxy is bypassed for internal calls.

// SOLUTION: Inject self or use AopContext
@Service
public class UserService {
    @Autowired
    private UserService self;  // Inject the proxy!
    
    public void methodA() {
        self.methodB();  // Now goes through proxy ✅
    }
}
```

---

## Q6: What is the difference between Spring AOP and AspectJ?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING AOP vs ASPECTJ                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SPRING AOP:                                                            │
│   ───────────                                                            │
│   • Runtime weaving using proxies                                       │
│   • Only method execution join points                                   │
│   • Only Spring beans can be advised                                    │
│   • Simpler, sufficient for most cases                                  │
│   • No special compiler needed                                          │
│                                                                          │
│   ASPECTJ:                                                               │
│   ─────────                                                              │
│   • Compile-time or load-time weaving                                   │
│   • All join points (method, field, constructor, etc.)                  │
│   • Any object can be advised                                           │
│   • More powerful, more complex                                         │
│   • Requires AspectJ compiler (ajc)                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Comparison:

| Feature | Spring AOP | AspectJ |
|---------|------------|---------|
| **Weaving** | Runtime (proxy) | Compile/Load time |
| **Join Points** | Method execution only | All (field, constructor, etc.) |
| **Target** | Spring beans only | Any Java class |
| **Performance** | Slight overhead | Better (no proxy) |
| **Complexity** | Simple | Complex |
| **Internal calls** | Not intercepted | Intercepted |
| **Final classes** | Cannot proxy | Can advise |
| **Use case** | Most Spring apps | Complex requirements |

---

## Q7: Practical AOP example - Logging Aspect

**Answer:**

```java
@Aspect
@Component
@Slf4j
@Order(1)  // Lower number = higher priority
public class LoggingAspect {
    
    // Pointcuts
    @Pointcut("within(com.book.management.*.controller..*)")
    public void controllerLayer() {}
    
    @Pointcut("within(com.book.management.*.service..*)")
    public void serviceLayer() {}
    
    // Log method entry
    @Before("controllerLayer() || serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("➡️  Entering {}.{} with arguments: {}", 
            className, methodName, Arrays.toString(args));
    }
    
    // Log method exit with return value
    @AfterReturning(
        pointcut = "controllerLayer() || serviceLayer()",
        returning = "result"
    )
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("⬅️  Exiting {}.{} with result: {}", 
            className, methodName, result);
    }
    
    // Log exceptions
    @AfterThrowing(
        pointcut = "controllerLayer() || serviceLayer()",
        throwing = "exception"
    )
    public void logException(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("❌ Exception in {}.{}: {}", 
            className, methodName, exception.getMessage());
    }
    
    // Log execution time
    @Around("controllerLayer() || serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = pjp.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("⏱️  {}.{} executed in {}ms", 
                className, methodName, duration);
        }
    }
}
```

### Configuration:

```java
@Configuration
@EnableAspectJAutoProxy  // Enable AOP
public class AopConfig {
    // Aspects are auto-detected via @Component
}

// Or in Spring Boot - auto-enabled with spring-boot-starter-aop
```

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

---

## Q8: How do you create a custom annotation for AOP?

**Answer:**

Creating custom annotations allows you to target specific methods for advice.

```java
// Step 1: Define the annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    String value() default "";
    LogLevel level() default LogLevel.INFO;
}

// Step 2: Create the aspect
@Aspect
@Component
@Slf4j
public class LoggableAspect {
    
    @Around("@annotation(loggable)")  // Bind annotation to parameter
    public Object logMethod(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
        String value = loggable.value();
        LogLevel level = loggable.level();
        
        log.info("[{}] Starting: {}", value, pjp.getSignature().getName());
        
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        
        log.info("[{}] Completed in {}ms", value, duration);
        return result;
    }
}

// Step 3: Use the annotation
@Service
public class UserService {
    
    @Loggable(value = "UserOps", level = LogLevel.DEBUG)
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
```

---

## Q9: What is the order of advice execution when multiple aspects are applied?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ADVICE EXECUTION ORDER                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Multiple Aspects on Same Join Point:                                   │
│                                                                          │
│   @Aspect @Order(1)  ─▶  Higher Priority (runs first for Before)        │
│   @Aspect @Order(2)  ─▶  Lower Priority (runs after)                    │
│                                                                          │
│   Execution Flow:                                                        │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │                                                                   │  │
│   │   @Before (Order 1)                                               │  │
│   │      └─▶ @Before (Order 2)                                        │  │
│   │             └─▶ @Around (before proceed) - Order 1               │  │
│   │                    └─▶ @Around (before proceed) - Order 2        │  │
│   │                           └─▶ TARGET METHOD                      │  │
│   │                           @Around (after proceed) - Order 2      │  │
│   │                    @Around (after proceed) - Order 1             │  │
│   │             @After (Order 2) ◀─┘                                  │  │
│   │      @After (Order 1) ◀─┘                                         │  │
│   │                                                                   │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│   Think of it like:                                                      │
│   • @Before: Lower @Order value runs first                              │
│   • @After: Lower @Order value runs last (LIFO)                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
@Aspect
@Component
@Order(1)  // Highest priority
public class SecurityAspect { }

@Aspect
@Component
@Order(2)  // Lower priority
public class LoggingAspect { }
```

---

## Q10: What is the difference between `execution()` and `within()` pointcuts?

**Answer:**

| Aspect | `execution()` | `within()` |
|--------|---------------|------------|
| **Granularity** | Method level | Class/Package level |
| **Matches** | Specific method signatures | All join points in a class |
| **Use case** | Target specific methods | Target all methods in class |

```java
// execution() - Matches specific method signatures
@Pointcut("execution(* com.app.service.UserService.findById(..))")
public void findByIdMethod() {}  // Only findById method

// within() - Matches all join points in a class
@Pointcut("within(com.app.service.UserService)")
public void allUserServiceMethods() {}  // ALL methods in UserService

// within() with package
@Pointcut("within(com.app.service..*)")  // All classes in service package
public void allServicePackage() {}
```

---

## Q11: How do you access method arguments in advice?

**Answer:**

```java
@Aspect
@Component
public class ArgumentAccessAspect {
    
    // Method 1: Using JoinPoint.getArgs()
    @Before("execution(* com.app.service.*.*(..))")
    public void logArgs(JoinPoint jp) {
        Object[] args = jp.getArgs();
        for (int i = 0; i < args.length; i++) {
            log.info("Arg[{}]: {}", i, args[i]);
        }
    }
    
    // Method 2: Using args() in pointcut - bind specific arguments
    @Before("execution(* com.app.service.*.*(..)) && args(userId, ..)")
    public void logUserId(Long userId) {
        log.info("userId: {}", userId);
    }
    
    // Method 3: Bind multiple arguments
    @Before("execution(* com.app.service.*.*(..)) && args(name, age)")
    public void logNameAndAge(String name, int age) {
        log.info("name: {}, age: {}", name, age);
    }
    
    // Method 4: With @Around
    @Around("execution(* com.app.service.*.*(..))")
    public Object modifyArgs(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        
        // Modify arguments (if needed)
        args[0] = ((String) args[0]).toUpperCase();
        
        // Proceed with modified arguments
        return pjp.proceed(args);
    }
}
```

---

## Q12: What is `@DeclareParents` in AOP?

**Answer:**

`@DeclareParents` is used for **Introduction** - adding new interfaces/methods to existing classes without modifying them.

```java
// Interface to introduce
public interface Auditable {
    void setAuditInfo(String info);
    String getAuditInfo();
}

// Default implementation
public class AuditableImpl implements Auditable {
    private String auditInfo;
    
    public void setAuditInfo(String info) { this.auditInfo = info; }
    public String getAuditInfo() { return auditInfo; }
}

// Aspect that introduces the interface
@Aspect
@Component
public class AuditAspect {
    
    @DeclareParents(
        value = "com.app.service.*+",  // Target classes
        defaultImpl = AuditableImpl.class  // Default implementation
    )
    private Auditable auditable;
}

// Now all service classes implement Auditable!
@Service
public class UserService { }  // Automatically implements Auditable

// Usage
UserService userService = context.getBean(UserService.class);
((Auditable) userService).setAuditInfo("Created by admin");
```

---

## Q13: How does AOP handle exceptions?

**Answer:**

```java
@Aspect
@Component
public class ExceptionHandlingAspect {
    
    // Catch specific exception types
    @AfterThrowing(
        pointcut = "execution(* com.app.service.*.*(..))",
        throwing = "ex"
    )
    public void handleException(JoinPoint jp, Exception ex) {
        log.error("Method {} threw: {}", jp.getSignature().getName(), ex.getMessage());
        // Can notify, log, or trigger alerts
    }
    
    // Catch specific exception type
    @AfterThrowing(
        pointcut = "execution(* com.app.service.*.*(..))",
        throwing = "ex"
    )
    public void handleNotFound(JoinPoint jp, ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
    }
    
    // Transform exception using @Around
    @Around("execution(* com.app.service.*.*(..))")
    public Object transformException(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (SQLException e) {
            // Transform to custom exception
            throw new DataAccessException("Database error", e);
        }
    }
    
    // Suppress exception and return default value
    @Around("execution(* com.app.service.*.findById(..))")
    public Object suppressException(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Exception e) {
            log.warn("Returning null due to: {}", e.getMessage());
            return null;  // Return default instead of throwing
        }
    }
}
```

---

## Q14: What are the limitations of Spring AOP?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING AOP LIMITATIONS                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. ONLY METHOD EXECUTION                                               │
│      • Cannot intercept field access                                    │
│      • Cannot intercept constructor calls                               │
│      • Cannot intercept static methods                                  │
│                                                                          │
│   2. ONLY SPRING BEANS                                                   │
│      • Cannot advise objects created with 'new'                         │
│      • Only works with Spring-managed beans                             │
│                                                                          │
│   3. INTERNAL CALLS NOT INTERCEPTED                                      │
│      • this.method() bypasses proxy                                     │
│      • Self-injection needed for internal calls                         │
│                                                                          │
│   4. FINAL CLASSES/METHODS                                               │
│      • CGLIB cannot proxy final classes                                 │
│      • Cannot override final methods                                    │
│                                                                          │
│   5. PRIVATE METHODS                                                     │
│      • Private methods cannot be intercepted                            │
│      • Only public methods can be advised                               │
│                                                                          │
│   6. RUNTIME OVERHEAD                                                    │
│      • Proxy creation has performance cost                              │
│      • Method invocation through proxy adds latency                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q15: How do you enable AOP in Spring Boot?

**Answer:**

```java
// Spring Boot - Auto-configured with starter
// Just add dependency, no @EnableAspectJAutoProxy needed

// pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

// Or for Gradle
implementation 'org.springframework.boot:spring-boot-starter-aop'

// The aspect is automatically detected via @Component
@Aspect
@Component
public class LoggingAspect {
    // ...
}

// For non-Boot Spring applications:
@Configuration
@EnableAspectJAutoProxy  // Required!
public class AopConfig {
}

// With proxy options:
@EnableAspectJAutoProxy(
    proxyTargetClass = true,  // Force CGLIB proxies
    exposeProxy = true        // Enable AopContext.currentProxy()
)
```

---

## Q16: What is `JoinPoint` and `ProceedingJoinPoint`?

**Answer:**

| Feature | JoinPoint | ProceedingJoinPoint |
|---------|-----------|---------------------|
| **Used with** | @Before, @After, @AfterReturning, @AfterThrowing | @Around only |
| **proceed()** | ❌ No | ✅ Yes |
| **Controls execution** | No | Yes |

```java
@Aspect
@Component
public class JoinPointExample {
    
    // JoinPoint - for all advice types except @Around
    @Before("execution(* com.app.service.*.*(..))")
    public void beforeAdvice(JoinPoint jp) {
        // Get method signature
        String methodName = jp.getSignature().getName();
        String className = jp.getTarget().getClass().getSimpleName();
        
        // Get arguments
        Object[] args = jp.getArgs();
        
        // Get proxy object
        Object proxy = jp.getThis();
        
        // Get target object
        Object target = jp.getTarget();
        
        log.info("Calling {}.{}", className, methodName);
    }
    
    // ProceedingJoinPoint - ONLY for @Around
    @Around("execution(* com.app.service.*.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        // Has all JoinPoint methods PLUS:
        
        // Execute target method
        Object result = pjp.proceed();
        
        // Or with modified arguments
        Object[] newArgs = { "modified", 123 };
        Object result2 = pjp.proceed(newArgs);
        
        return result;
    }
}
```

---

## Q17: How do you implement caching using AOP?

**Answer:**

```java
@Aspect
@Component
public class CachingAspect {
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    @Around("@annotation(Cacheable)")
    public Object cacheResult(ProceedingJoinPoint pjp) throws Throwable {
        // Create cache key from method + args
        String key = generateCacheKey(pjp);
        
        // Check cache
        if (cache.containsKey(key)) {
            log.info("Cache HIT for: {}", key);
            return cache.get(key);
        }
        
        // Execute method
        log.info("Cache MISS for: {}", key);
        Object result = pjp.proceed();
        
        // Store in cache
        cache.put(key, result);
        return result;
    }
    
    private String generateCacheKey(ProceedingJoinPoint pjp) {
        return pjp.getSignature().toShortString() + 
               Arrays.toString(pjp.getArgs());
    }
}

// Custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable { }

// Usage
@Service
public class ProductService {
    
    @Cacheable
    public Product findById(Long id) {
        // Expensive operation
        return productRepository.findById(id);
    }
}
```

> **Note:** Spring provides built-in `@Cacheable`, `@CacheEvict`, `@CachePut` annotations that work with AOP internally.

---

## Q18: What is the `this()` vs `target()` pointcut designator?

**Answer:**

| Designator | Matches | Object Type |
|------------|---------|-------------|
| `this()` | Proxy object | The proxy implementing the type |
| `target()` | Target object | The actual bean being proxied |

```java
// this() - matches if PROXY implements the interface
@Before("this(com.app.service.UserService)")
public void adviseProxy(JoinPoint jp) {
    // jp.getThis() returns the proxy
}

// target() - matches if TARGET implements the interface
@Before("target(com.app.service.UserService)")
public void adviseTarget(JoinPoint jp) {
    // jp.getTarget() returns the actual object
}

// Practical difference with JDK proxy:
// JDK Proxy:
//   this() matches: UserService interface
//   target() matches: UserServiceImpl class

// Binding the object:
@Before("target(userService)")
public void withTarget(UserService userService) {
    // userService is the target object
}

@Before("this(proxy)")
public void withProxy(UserService proxy) {
    // proxy is the proxy object
}
```

---

## Q19: How do you implement method-level security with AOP?

**Answer:**

```java
// Custom security annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    String[] value();  // Required roles
}

// Security Aspect
@Aspect
@Component
@Order(0)  // Run first - security should be checked first
public class SecurityAspect {
    
    @Autowired
    private SecurityContext securityContext;
    
    @Before("@annotation(requiresRole)")
    public void checkRole(JoinPoint jp, RequiresRole requiresRole) {
        String[] requiredRoles = requiresRole.value();
        String currentUserRole = securityContext.getCurrentUserRole();
        
        boolean hasRole = Arrays.asList(requiredRoles)
                                .contains(currentUserRole);
        
        if (!hasRole) {
            throw new AccessDeniedException(
                "User does not have required role: " + 
                Arrays.toString(requiredRoles)
            );
        }
        
        log.info("Security check passed for: {}", 
            jp.getSignature().getName());
    }
}

// Usage
@Service
public class AdminService {
    
    @RequiresRole({"ADMIN", "SUPER_ADMIN"})
    public void deleteUser(Long userId) {
        // Only ADMIN or SUPER_ADMIN can call this
    }
    
    @RequiresRole("ADMIN")
    public void updateConfig(Config config) {
        // Only ADMIN can call this
    }
}
```

---

## Q20: What is load-time weaving (LTW)?

**Answer:**

Load-time weaving applies aspects when classes are loaded into the JVM, providing more power than runtime proxies.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    WEAVING TYPES                                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. COMPILE-TIME WEAVING                                                │
│      • Aspects woven during compilation                                 │
│      • Requires AspectJ compiler (ajc)                                  │
│      • Best performance                                                  │
│                                                                          │
│   2. LOAD-TIME WEAVING (LTW)                                             │
│      • Aspects woven when class is loaded                               │
│      • Uses Java agent                                                   │
│      • More flexible than compile-time                                  │
│                                                                          │
│   3. RUNTIME WEAVING (Spring AOP)                                        │
│      • Uses proxies at runtime                                          │
│      • Most flexible, easiest setup                                     │
│      • Some overhead                                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

**Enabling LTW in Spring:**

```java
@Configuration
@EnableLoadTimeWeaving
public class LtwConfig { }

// META-INF/aop.xml
<aspectj>
    <weaver>
        <include within="com.app..*"/>
    </weaver>
    <aspects>
        <aspect name="com.app.aspect.LoggingAspect"/>
    </aspects>
</aspectj>

// JVM argument
-javaagent:/path/to/spring-instrument.jar
```

---

## Q21: How do you test aspects?

**Answer:**

```java
@SpringBootTest
class LoggingAspectTest {
    
    @Autowired
    private UserService userService;  // Proxied with aspect
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    void testAspectIsApplied() {
        // Given
        User user = new User("John");
        when(userRepository.save(any())).thenReturn(user);
        
        // When
        userService.createUser(user);
        
        // Then - Check logs or verify behavior
        // Use @Captor or log appender to verify logging
    }
}

// Testing aspect in isolation
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoggingAspect.class, TestConfig.class})
@EnableAspectJAutoProxy
class AspectIsolationTest {
    
    @Autowired
    private TestService testService;
    
    @Test
    void aspectIsTriggered() {
        testService.someMethod();
        // Verify aspect behavior
    }
}

// Verifying proxy creation
@Test
void verifyProxyCreated() {
    assertTrue(AopUtils.isAopProxy(userService));
    assertTrue(AopUtils.isCglibProxy(userService));  // or isJdkDynamicProxy
}
```

---

## Q22: What is `AopContext.currentProxy()`?

**Answer:**

Used to get the current proxy for self-invocation scenarios.

```java
@Service
public class OrderService {
    
    public void processOrder(Order order) {
        // Internal call - bypasses proxy!
        this.validateOrder(order);  // ❌ AOP not applied
        
        // Solution 1: Use AopContext
        ((OrderService) AopContext.currentProxy())
            .validateOrder(order);  // ✅ Goes through proxy
    }
    
    @Transactional
    public void validateOrder(Order order) {
        // Transaction only works when called through proxy
    }
}

// Enable proxy exposure
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)  // Required!
public class AopConfig { }

// Solution 2: Self-injection (preferred)
@Service
public class OrderService {
    
    @Autowired
    private OrderService self;  // Inject proxy
    
    public void processOrder(Order order) {
        self.validateOrder(order);  // ✅ Goes through proxy
    }
}
```

---

## Q23: How does Spring AOP integrate with `@Transactional`?

**Answer:**

`@Transactional` is implemented using AOP internally!

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    @TRANSACTIONAL UNDER THE HOOD                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   @Transactional is equivalent to @Around advice:                        │
│                                                                          │
│   try {                                                                  │
│       transactionManager.getTransaction();  // Begin                    │
│       Object result = method.invoke();      // Execute                  │
│       transactionManager.commit();          // Commit                   │
│       return result;                                                     │
│   } catch (Exception e) {                                               │
│       transactionManager.rollback();        // Rollback                 │
│       throw e;                                                           │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// This:
@Service
public class UserService {
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }
}

// Is conceptually similar to:
@Aspect
public class TransactionAspect {
    
    @Around("@annotation(Transactional)")
    public Object manageTransaction(ProceedingJoinPoint pjp) throws Throwable {
        TransactionStatus status = transactionManager.getTransaction();
        try {
            Object result = pjp.proceed();
            transactionManager.commit(status);
            return result;
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
```

---

## Q24: What is aspect precedence and how do you control it?

**Answer:**

```java
// Using @Order annotation - lower value = higher priority
@Aspect
@Component
@Order(1)  // Runs first (for @Before)
public class SecurityAspect { }

@Aspect
@Component
@Order(2)  // Runs second
public class LoggingAspect { }

@Aspect
@Component
@Order(3)  // Runs third
public class AuditAspect { }

// Or implement Ordered interface
@Aspect
@Component
public class SecurityAspect implements Ordered {
    @Override
    public int getOrder() {
        return 1;
    }
}

// Execution order for nested aspects:
// @Before:  Security(1) → Logging(2) → Audit(3) → Method
// @After:   Method → Audit(3) → Logging(2) → Security(1)
```

---

## Q25: What is the `bean()` pointcut designator?

**Answer:**

Spring-specific pointcut that matches by bean name.

```java
// Match specific bean
@Before("bean(userService)")
public void adviseUserService() { }

// Match beans with names ending in 'Service'
@Before("bean(*Service)")
public void adviseAllServices() { }

// Match beans with names starting with 'order'
@Before("bean(order*)")
public void adviseOrderBeans() { }

// Combine with other pointcuts
@Before("bean(*Service) && execution(* save*(..))")
public void adviseServiceSaveMethods() { }

// Exclude beans
@Before("bean(*Service) && !bean(auditService)")
public void adviseServicesExceptAudit() { }
```

> **Note:** `bean()` is Spring AOP specific and not available in pure AspectJ.

---

## Q26: How do you implement retry logic using AOP?

**Answer:**

```java
// Custom retry annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {
    int maxAttempts() default 3;
    long delay() default 1000;  // milliseconds
    Class<? extends Exception>[] retryOn() default {Exception.class};
}

// Retry Aspect
@Aspect
@Component
@Slf4j
public class RetryAspect {
    
    @Around("@annotation(retryable)")
    public Object retry(ProceedingJoinPoint pjp, Retryable retryable) 
            throws Throwable {
        
        int maxAttempts = retryable.maxAttempts();
        long delay = retryable.delay();
        Class<? extends Exception>[] retryExceptions = retryable.retryOn();
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Attempt {} of {}", attempt, maxAttempts);
                return pjp.proceed();
                
            } catch (Exception e) {
                lastException = e;
                
                if (!shouldRetry(e, retryExceptions)) {
                    throw e;
                }
                
                if (attempt < maxAttempts) {
                    log.warn("Attempt {} failed, retrying in {}ms", 
                        attempt, delay);
                    Thread.sleep(delay);
                }
            }
        }
        
        log.error("All {} attempts failed", maxAttempts);
        throw lastException;
    }
    
    private boolean shouldRetry(Exception e, 
            Class<? extends Exception>[] retryExceptions) {
        for (Class<? extends Exception> clazz : retryExceptions) {
            if (clazz.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
}

// Usage
@Service
public class PaymentService {
    
    @Retryable(maxAttempts = 3, delay = 2000, 
               retryOn = {TimeoutException.class, NetworkException.class})
    public PaymentResult processPayment(Payment payment) {
        // May fail due to network issues
        return paymentGateway.process(payment);
    }
}
```

---

## Q27: What are the best practices for AOP?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AOP BEST PRACTICES                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. KEEP ASPECTS FOCUSED                                                │
│      • One aspect per concern (logging, security, caching)             │
│      • Don't mix responsibilities in single aspect                      │
│                                                                          │
│   2. USE DESCRIPTIVE POINTCUT NAMES                                      │
│      • @Pointcut("...") void allServiceMethods() {}                     │
│      • Makes code self-documenting                                       │
│                                                                          │
│   3. ORDER YOUR ASPECTS                                                  │
│      • Security should run first                                        │
│      • Logging typically runs last                                       │
│      • Use @Order explicitly                                             │
│                                                                          │
│   4. BE SPECIFIC WITH POINTCUTS                                          │
│      • Avoid overly broad patterns                                       │
│      • execution(* com.app..*.*(..)) affects everything!                │
│                                                                          │
│   5. HANDLE EXCEPTIONS PROPERLY                                          │
│      • Always re-throw exceptions in @Around                             │
│      • Don't swallow exceptions silently                                 │
│                                                                          │
│   6. AVOID HEAVY PROCESSING                                              │
│      • Aspects run on every matched method                               │
│      • Keep advice lightweight                                           │
│                                                                          │
│   7. TEST YOUR ASPECTS                                                   │
│      • Verify aspects are applied correctly                              │
│      • Test edge cases and exception handling                            │
│                                                                          │
│   8. USE @Around CAREFULLY                                               │
│      • Most powerful but most complex                                    │
│      • Always call proceed() to execute target                           │
│                                                                          │
│   9. DOCUMENT POINTCUTS                                                  │
│      • List which methods are affected                                   │
│      • Explain why aspect is applied                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q28: How do you measure AOP performance impact?

**Answer:**

```java
@Aspect
@Component
@Slf4j
public class PerformanceMonitorAspect {
    
    private final MeterRegistry meterRegistry;
    
    @Around("execution(* com.app.service.*.*(..))")
    public Object measurePerformance(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Object result = pjp.proceed();
            sample.stop(meterRegistry.timer("method.execution",
                "method", methodName,
                "status", "success"
            ));
            return result;
        } catch (Exception e) {
            sample.stop(meterRegistry.timer("method.execution",
                "method", methodName,
                "status", "error"
            ));
            throw e;
        }
    }
}

// Measuring aspect overhead itself
@SpringBootTest
class AspectPerformanceTest {
    
    @Test
    void measureAspectOverhead() {
        long withAopStart = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            service.methodWithAspect();  // Proxied
        }
        long withAop = System.nanoTime() - withAopStart;
        
        // Compare with direct call (if possible)
        log.info("With AOP: {}ns per call", withAop / 10000);
    }
}
```

---

## Q29: What is `@Pointcut` reusability?

**Answer:**

Define reusable pointcuts to avoid duplication and improve maintainability.

```java
@Aspect
@Component
public class PointcutLibrary {
    
    // Base pointcuts - Reusable building blocks
    @Pointcut("within(com.app.service..*)")
    public void inServiceLayer() {}
    
    @Pointcut("within(com.app.controller..*)")
    public void inControllerLayer() {}
    
    @Pointcut("within(com.app.repository..*)")
    public void inRepositoryLayer() {}
    
    // Method pattern pointcuts
    @Pointcut("execution(* find*(..))")
    public void findMethods() {}
    
    @Pointcut("execution(* save*(..))")
    public void saveMethods() {}
    
    @Pointcut("execution(* delete*(..))")
    public void deleteMethods() {}
    
    // Annotation pointcuts
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}
    
    // Combined pointcuts
    @Pointcut("inServiceLayer() && findMethods()")
    public void serviceFindMethods() {}
    
    @Pointcut("(inServiceLayer() || inControllerLayer()) && !findMethods()")
    public void writeOperations() {}
}

// Use in other aspects
@Aspect
@Component
public class LoggingAspect {
    
    @Before("com.app.aspect.PointcutLibrary.serviceFindMethods()")
    public void logFindMethods(JoinPoint jp) {
        log.info("Finding: {}", jp.getSignature().getName());
    }
}
```

---

## Q30: How do you debug AOP issues?

**Answer:**

```java
// 1. Enable AOP debug logging
# application.properties
logging.level.org.springframework.aop=DEBUG
logging.level.org.aspectj=DEBUG

// 2. Check if proxy is created
@Test
void verifyProxy() {
    System.out.println("Is proxy: " + AopUtils.isAopProxy(userService));
    System.out.println("Is CGLIB: " + AopUtils.isCglibProxy(userService));
    System.out.println("Is JDK: " + AopUtils.isJdkDynamicProxy(userService));
    System.out.println("Target class: " + AopUtils.getTargetClass(userService));
}

// 3. List all advisors applied
@Test
void listAdvisors() {
    if (userService instanceof Advised) {
        Advised advised = (Advised) userService;
        for (Advisor advisor : advised.getAdvisors()) {
            System.out.println("Advisor: " + advisor);
            System.out.println("Advice: " + advisor.getAdvice());
        }
    }
}

// 4. Common issues checklist
/*
 * ❌ Aspect not triggered:
 *    - Is @Aspect and @Component present?
 *    - Is pointcut expression correct?
 *    - Is method public (not private)?
 *    - Is it a Spring bean (not new object)?
 *    - Is it internal call (this.method())?
 *    - Is class/method final?
 *
 * ❌ @EnableAspectJAutoProxy missing
 * ❌ spring-boot-starter-aop dependency missing
 * ❌ Pointcut expression syntax error
 */

// 5. Add debug logging in aspect
@Around("execution(* com.app.service.*.*(..))")
public Object debugAround(ProceedingJoinPoint pjp) throws Throwable {
    log.debug("ASPECT TRIGGERED for: {}", pjp.getSignature());
    return pjp.proceed();
}
```

---

## Q31: What is the difference between proxy-based and bytecode weaving?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│            PROXY-BASED vs BYTECODE WEAVING                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   PROXY-BASED (Spring AOP):                                              │
│   ─────────────────────────                                              │
│   • Wraps object at runtime                                             │
│   • Original bytecode unchanged                                          │
│   • Method calls go through proxy                                        │
│   • Simpler, no special tools needed                                     │
│                                                                          │
│   [Client] ──▶ [Proxy] ──▶ [Target]                                     │
│                  │                                                        │
│               Advice                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   BYTECODE WEAVING (AspectJ):                                            │
│   ───────────────────────────                                            │
│   • Modifies actual bytecode                                             │
│   • Compile-time or load-time                                            │
│   • No proxy overhead                                                    │
│   • More powerful, complex setup                                         │
│                                                                          │
│   [Client] ──▶ [Modified Target with woven advice]                       │
│                                                                          │
│   Original bytecode + Aspect code = New bytecode                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

| Feature | Proxy-Based | Bytecode Weaving |
|---------|-------------|------------------|
| **When** | Runtime | Compile/Load time |
| **How** | Wrapper objects | Modifies .class files |
| **Performance** | Slight overhead | No runtime overhead |
| **Internal calls** | Not intercepted | Intercepted |
| **Private methods** | Not supported | Supported |
| **Setup** | Simple | Requires AspectJ |

---

## Q32: How do you conditionally enable/disable aspects?

**Answer:**

```java
// Method 1: Using @ConditionalOnProperty
@Aspect
@Component
@ConditionalOnProperty(
    name = "app.logging.aspect.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class LoggingAspect { }

// Method 2: Using @Profile
@Aspect
@Component
@Profile({"dev", "staging"})  // Only in dev/staging
public class DebugAspect { }

// Method 3: Runtime check in advice
@Aspect
@Component
public class ConditionalAspect {
    
    @Value("${app.aspect.enabled:true}")
    private boolean enabled;
    
    @Around("execution(* com.app.service.*.*(..))")
    public Object conditionalAdvice(ProceedingJoinPoint pjp) throws Throwable {
        if (!enabled) {
            return pjp.proceed();  // Skip advice logic
        }
        
        // Advice logic here
        log.info("Aspect is enabled");
        return pjp.proceed();
    }
}

// Method 4: Using @Conditional
@Aspect
@Component
@Conditional(AspectEnabledCondition.class)
public class CustomConditionalAspect { }

public class AspectEnabledCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(context.getEnvironment()
            .getProperty("app.aspect.enabled"));
    }
}
```

---

## Q33: What is `@args()` pointcut designator?

**Answer:**

`@args()` matches methods where the **arguments have specific annotations**.

```java
// Annotation on parameter type
@Sensitive
public class CreditCardNumber {
    private String number;
}

// Aspect using @args()
@Before("execution(* com.app.service.*.*(..)) && @args(sensitive, ..)")
public void logSensitiveData(JoinPoint jp, Sensitive sensitive) {
    // Triggered when first argument's CLASS is annotated with @Sensitive
    log.warn("Method receiving sensitive data: {}", 
        jp.getSignature().getName());
}

// Usage - this will trigger the advice:
@Service
public class PaymentService {
    public void processPayment(CreditCardNumber card) {  // CreditCardNumber has @Sensitive
        // ...
    }
}

// Compare with args():
// args(type)   - matches parameter TYPE
// @args(ann)   - matches when parameter's CLASS has annotation
```

---

## Q34: How do you implement rate limiting with AOP?

**Answer:**

```java
// Rate limit annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int requests() default 100;
    int duration() default 60;  // seconds
    String key() default "";    // Rate limit key (e.g., per user)
}

// Rate limiting aspect
@Aspect
@Component
public class RateLimitAspect {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) 
            throws Throwable {
        
        String key = generateKey(pjp, rateLimit);
        
        RateLimiter limiter = limiters.computeIfAbsent(key, k -> 
            RateLimiter.create(rateLimit.requests() / (double) rateLimit.duration())
        );
        
        if (!limiter.tryAcquire()) {
            throw new RateLimitExceededException(
                "Rate limit exceeded for: " + key
            );
        }
        
        return pjp.proceed();
    }
    
    private String generateKey(ProceedingJoinPoint pjp, RateLimit rateLimit) {
        if (!rateLimit.key().isEmpty()) {
            // Use SpEL to evaluate key
            return evaluateKey(rateLimit.key(), pjp);
        }
        return pjp.getSignature().toShortString();
    }
}

// Usage
@RestController
public class ApiController {
    
    @RateLimit(requests = 10, duration = 60)  // 10 requests per minute
    @GetMapping("/api/data")
    public Data getData() {
        return dataService.getData();
    }
    
    @RateLimit(requests = 5, duration = 60, key = "#userId")
    @GetMapping("/api/user/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.findById(userId);
    }
}
```

---

## Q35: What is `@target()` vs `@within()` vs `@annotation()`?

**Answer:**

| Designator | Matches | Level |
|------------|---------|-------|
| `@target()` | Runtime type of target has annotation | Class (runtime) |
| `@within()` | Declared type has annotation | Class (compile-time) |
| `@annotation()` | Method has annotation | Method |

```java
@Auditable  // Class-level annotation
@Service
public class UserService {
    
    @Loggable  // Method-level annotation
    public void createUser() { }
}

// @annotation - matches METHOD with annotation
@Before("@annotation(loggable)")
public void methodAnnotation(Loggable loggable) {
    // Only createUser() matches
}

// @within - matches methods where DECLARING CLASS has annotation
@Before("@within(auditable)")
public void withinAnnotation(Auditable auditable) {
    // ALL methods in UserService match
}

// @target - matches methods where RUNTIME TARGET has annotation
@Before("@target(auditable)")
public void targetAnnotation(Auditable auditable) {
    // ALL methods where actual runtime object's class has @Auditable
    // Similar to @within for non-proxied objects
}

// Difference appears with inheritance:
@Auditable
class Parent { void method() {} }

class Child extends Parent { void method() {} }

// @within(Auditable) - matches Parent.method(), NOT Child.method()
// @target(Auditable) - matches Parent.method(), NOT Child.method()
//                      (because Child doesn't have @Auditable)
```

---

## Q36: How do you implement circuit breaker pattern with AOP?

**Answer:**

```java
// Circuit breaker annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreaker {
    int failureThreshold() default 5;
    long resetTimeout() default 30000;  // ms
    Class<? extends Exception>[] exceptions() default {Exception.class};
}

// Circuit breaker states
enum CircuitState { CLOSED, OPEN, HALF_OPEN }

// Circuit breaker aspect
@Aspect
@Component
@Slf4j
public class CircuitBreakerAspect {
    
    private final Map<String, CircuitStatus> circuits = new ConcurrentHashMap<>();
    
    @Around("@annotation(cb)")
    public Object circuitBreaker(ProceedingJoinPoint pjp, CircuitBreaker cb) 
            throws Throwable {
        
        String key = pjp.getSignature().toShortString();
        CircuitStatus status = circuits.computeIfAbsent(key, 
            k -> new CircuitStatus(cb.failureThreshold(), cb.resetTimeout()));
        
        // Check circuit state
        if (status.getState() == CircuitState.OPEN) {
            if (status.shouldAttemptReset()) {
                status.setState(CircuitState.HALF_OPEN);
            } else {
                throw new CircuitOpenException("Circuit is open for: " + key);
            }
        }
        
        try {
            Object result = pjp.proceed();
            status.recordSuccess();
            return result;
            
        } catch (Exception e) {
            if (shouldCount(e, cb.exceptions())) {
                status.recordFailure();
                if (status.getFailureCount() >= cb.failureThreshold()) {
                    status.setState(CircuitState.OPEN);
                    log.warn("Circuit opened for: {}", key);
                }
            }
            throw e;
        }
    }
}

// Usage
@Service
public class ExternalService {
    
    @CircuitBreaker(failureThreshold = 3, resetTimeout = 60000)
    public Response callExternalApi() {
        return restTemplate.getForObject(url, Response.class);
    }
}
```

---

## Q37: How do you apply AOP to third-party libraries?

**Answer:**

Spring AOP **cannot** directly advise third-party classes (they're not Spring beans), but there are workarounds:

```java
// Solution 1: Wrap third-party class in a Spring bean
@Component
public class ThirdPartyWrapper {
    
    private final ThirdPartyLibrary library = new ThirdPartyLibrary();
    
    public Result doSomething() {
        return library.doSomething();  // Wrapper method can be advised
    }
}

// Solution 2: Use AspectJ with compile-time or load-time weaving
// This can advise any class
@Aspect
public class ThirdPartyAspect {
    
    @Before("execution(* com.thirdparty.Library.*(..))")
    public void adviseThirdParty(JoinPoint jp) {
        // Requires AspectJ weaving
    }
}

// Solution 3: Use BeanPostProcessor to wrap beans
@Component
public class ThirdPartyBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof ThirdPartyClass) {
            return createLoggingProxy((ThirdPartyClass) bean);
        }
        return bean;
    }
    
    private ThirdPartyClass createLoggingProxy(ThirdPartyClass target) {
        return (ThirdPartyClass) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            (proxy, method, args) -> {
                log.info("Calling: {}", method.getName());
                return method.invoke(target, args);
            }
        );
    }
}
```

---

## Q38: What is `getStaticPart()` in JoinPoint?

**Answer:**

`getStaticPart()` returns information about the join point that doesn't change between invocations.

```java
@Before("execution(* com.app.service.*.*(..))")
public void logWithStaticPart(JoinPoint jp) {
    // Dynamic info (changes per call)
    Object[] args = jp.getArgs();           // Different each call
    Object target = jp.getTarget();         // Same object, but could be different
    
    // Static info (same for all calls to this join point)
    JoinPoint.StaticPart staticPart = jp.getStaticPart();
    
    String signature = staticPart.getSignature().toString();
    String kind = staticPart.getKind();          // "method-execution"
    int sourceLineNumber = staticPart.getSourceLocation().getLine();
    String sourceFile = staticPart.getSourceLocation().getFileName();
    
    // Useful for caching join point metadata
    log.info("Static: {} at {}:{}", signature, sourceFile, sourceLineNumber);
}

// Performance optimization - cache based on static part
private final Map<JoinPoint.StaticPart, CachedInfo> cache = new ConcurrentHashMap<>();

@Around("execution(* com.app.service.*.*(..))")
public Object optimizedAround(ProceedingJoinPoint pjp) throws Throwable {
    JoinPoint.StaticPart key = pjp.getStaticPart();
    CachedInfo info = cache.computeIfAbsent(key, this::computeInfo);
    // Use cached info...
    return pjp.proceed();
}
```

---

## Q39: How do you implement method-level analytics with AOP?

**Answer:**

```java
@Aspect
@Component
@Slf4j
public class AnalyticsAspect {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Around("@annotation(tracked)")
    public Object trackMethod(ProceedingJoinPoint pjp, Tracked tracked) 
            throws Throwable {
        
        String eventName = tracked.value().isEmpty() 
            ? pjp.getSignature().getName() 
            : tracked.value();
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("method", pjp.getSignature().toShortString());
        properties.put("timestamp", Instant.now().toString());
        properties.put("user", getCurrentUser());
        
        long startTime = System.currentTimeMillis();
        String status = "success";
        
        try {
            Object result = pjp.proceed();
            
            if (result != null) {
                properties.put("resultType", result.getClass().getSimpleName());
            }
            
            return result;
            
        } catch (Exception e) {
            status = "error";
            properties.put("errorType", e.getClass().getSimpleName());
            properties.put("errorMessage", e.getMessage());
            throw e;
            
        } finally {
            properties.put("duration", System.currentTimeMillis() - startTime);
            properties.put("status", status);
            
            analyticsService.track(eventName, properties);
        }
    }
}

// Custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tracked {
    String value() default "";
    String category() default "general";
}

// Usage
@Service
public class OrderService {
    
    @Tracked(value = "order_created", category = "orders")
    public Order createOrder(OrderRequest request) {
        // Analytics automatically tracked
        return orderRepository.save(new Order(request));
    }
}
```

---

## Q40: What is the `perthis()` and `pertarget()` instantiation models?

**Answer:**

By default, aspects are **singletons**. `perthis()` and `pertarget()` create **per-object aspect instances**.

```java
// Default: Singleton - one aspect instance for all targets
@Aspect
@Component
public class SingletonAspect { }  // One instance total

// perthis: One aspect instance per PROXY object
@Aspect("perthis(execution(* com.app.service..*.*(..))")
@Component
@Scope("prototype")  // Required!
public class PerProxyAspect {
    private int callCount = 0;  // Unique per proxy
    
    @Before("execution(* com.app.service..*.*(..))")
    public void countCalls() {
        callCount++;  // Counts calls per proxy instance
    }
}

// pertarget: One aspect instance per TARGET object
@Aspect("pertarget(execution(* com.app.service..*.*(..))")
@Component
@Scope("prototype")
public class PerTargetAspect {
    private int callCount = 0;  // Unique per target
    
    @Before("execution(* com.app.service..*.*(..))")
    public void countCalls() {
        callCount++;  // Counts calls per target instance
    }
}

// Use case: State tracking per service instance
@Aspect("perthis(execution(* com.app.service.UserService.*(..)))")
@Scope("prototype")
public class UserServiceStateAspect {
    
    private List<String> methodsCalledOnThisInstance = new ArrayList<>();
    
    @Before("execution(* com.app.service.UserService.*(..))")
    public void trackMethods(JoinPoint jp) {
        methodsCalledOnThisInstance.add(jp.getSignature().getName());
    }
}
```

---

## Q41: How do you implement input validation using AOP?

**Answer:**

```java
// Validation annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateInput {
    Class<?>[] groups() default {};
}

// Validation aspect
@Aspect
@Component
@Order(1)  // Run early
public class ValidationAspect {
    
    @Autowired
    private Validator validator;
    
    @Before("@annotation(validateInput)")
    public void validateArguments(JoinPoint jp, ValidateInput validateInput) {
        Object[] args = jp.getArgs();
        
        List<String> errors = new ArrayList<>();
        
        for (Object arg : args) {
            if (arg != null) {
                Set<ConstraintViolation<Object>> violations = 
                    validator.validate(arg, validateInput.groups());
                
                for (ConstraintViolation<Object> violation : violations) {
                    errors.add(violation.getPropertyPath() + ": " + 
                              violation.getMessage());
                }
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(
                "Validation failed: " + String.join(", ", errors)
            );
        }
    }
}

// Usage with standard Bean Validation
public class CreateUserRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email")
    private String email;
    
    @Min(value = 18, message = "Must be 18+")
    private int age;
}

@Service
public class UserService {
    
    @ValidateInput
    public User createUser(CreateUserRequest request) {
        // Request is automatically validated before method executes
        return userRepository.save(new User(request));
    }
}
```

---

## Q42: What is `execution()` vs `call()` pointcut?

**Answer:**

| Pointcut | Weaving Point | Supported By |
|----------|---------------|--------------|
| `execution()` | Inside the method | Spring AOP, AspectJ |
| `call()` | At the call site | AspectJ only |

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    execution() vs call()                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   class Client {                                                         │
│       void doWork() {                                                    │
│           service.process();  ←── call() weaves HERE (caller side)     │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   class Service {                                                        │
│       void process() {       ←── execution() weaves HERE (callee side) │
│           // method body                                                 │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Key differences:                                                       │
│   • call() modifies the caller's bytecode                               │
│   • execution() modifies the callee's bytecode                          │
│   • Spring AOP only supports execution()                                │
│   • call() useful when you can't modify target class                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// AspectJ only - call()
@Before("call(* com.app.service.PaymentService.processPayment(..))")
public void beforeCall(JoinPoint jp) {
    // Advice woven into the CALLER, not PaymentService
    // Useful for third-party libraries you can't modify
}

// Both Spring AOP and AspectJ - execution()
@Before("execution(* com.app.service.PaymentService.processPayment(..))")
public void beforeExecution(JoinPoint jp) {
    // Advice woven into PaymentService.processPayment()
}
```

---

## Q43: How do you handle async methods with AOP?

**Answer:**

```java
@Aspect
@Component
@Slf4j
public class AsyncAspect {
    
    // Challenge: @Async methods return immediately (Future/void)
    // Solution: Wrap the async execution
    
    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    public Object aroundAsync(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String threadName = Thread.currentThread().getName();
        
        log.info("Async method {} called from thread: {}", methodName, threadName);
        
        Object result = pjp.proceed();
        
        // Result is CompletableFuture or void
        if (result instanceof CompletableFuture<?>) {
            CompletableFuture<?> future = (CompletableFuture<?>) result;
            
            return future.whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Async {} failed: {}", methodName, ex.getMessage());
                } else {
                    log.info("Async {} completed on thread: {}", 
                        methodName, Thread.currentThread().getName());
                }
            });
        }
        
        return result;
    }
}

// Note: Aspect order matters with @Async
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // Run before async handoff
public class EarlyAspect { }

// Usage
@Service
public class EmailService {
    
    @Async
    public CompletableFuture<Boolean> sendEmailAsync(String to, String message) {
        // Sends on different thread
        return CompletableFuture.completedFuture(emailSender.send(to, message));
    }
}
```

---

## Q44: What are composite pointcuts and how do you use them?

**Answer:**

Composite pointcuts combine multiple simple pointcuts using logical operators.

```java
@Aspect
@Component
public class CompositePointcutAspect {
    
    // Basic pointcuts
    @Pointcut("within(com.app.service..*)")
    private void serviceLayer() {}
    
    @Pointcut("within(com.app.controller..*)")
    private void controllerLayer() {}
    
    @Pointcut("execution(* find*(..))")
    private void findMethods() {}
    
    @Pointcut("execution(* save*(..))")
    private void saveMethods() {}
    
    @Pointcut("@annotation(Auditable)")
    private void auditableMethods() {}
    
    // Composite pointcuts using AND (&&)
    @Pointcut("serviceLayer() && findMethods()")
    public void serviceFindMethods() {}
    
    // Composite pointcuts using OR (||)
    @Pointcut("serviceLayer() || controllerLayer()")
    public void businessLayers() {}
    
    // Composite pointcuts using NOT (!)
    @Pointcut("serviceLayer() && !findMethods()")
    public void serviceWriteMethods() {}
    
    // Complex composite
    @Pointcut("(serviceLayer() || controllerLayer()) && " +
              "(saveMethods() || auditableMethods()) && " +
              "!findMethods()")
    public void auditableWriteOperations() {}
    
    // Use composite pointcut in advice
    @Before("auditableWriteOperations()")
    public void auditWriteOperation(JoinPoint jp) {
        log.info("Auditing write operation: {}", jp.getSignature());
    }
    
    // Inline composite
    @After("serviceLayer() && execution(* delete*(..))")
    public void afterDelete(JoinPoint jp) {
        log.info("Deleted: {}", jp.getSignature());
    }
}
```

---

## Q45: How do you implement distributed tracing with AOP?

**Answer:**

```java
@Aspect
@Component
@Slf4j
public class DistributedTracingAspect {
    
    @Autowired
    private Tracer tracer;  // e.g., Brave/Zipkin tracer
    
    @Around("@annotation(Traced)")
    public Object traceMethod(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        
        // Create a new span
        Span span = tracer.nextSpan().name(methodName).start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            // Add tags
            span.tag("class", pjp.getTarget().getClass().getSimpleName());
            span.tag("method", pjp.getSignature().getName());
            span.tag("args.count", String.valueOf(pjp.getArgs().length));
            
            // Execute method
            Object result = pjp.proceed();
            
            span.tag("status", "success");
            return result;
            
        } catch (Exception e) {
            span.tag("status", "error");
            span.tag("error.type", e.getClass().getSimpleName());
            span.tag("error.message", e.getMessage());
            span.error(e);
            throw e;
            
        } finally {
            span.finish();
        }
    }
    
    // Propagate trace context for async calls
    @Around("@annotation(org.springframework.scheduling.annotation.Async)")
    public Object traceAsync(ProceedingJoinPoint pjp) throws Throwable {
        Span currentSpan = tracer.currentSpan();
        String traceId = currentSpan != null 
            ? currentSpan.context().traceIdString() 
            : "none";
        
        MDC.put("traceId", traceId);
        try {
            return pjp.proceed();
        } finally {
            MDC.remove("traceId");
        }
    }
}

// Custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {
    String value() default "";
}
```

---

## Q46: What are common AOP anti-patterns to avoid?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AOP ANTI-PATTERNS                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ❌ 1. OVERLY BROAD POINTCUTS                                           │
│   ─────────────────────────────                                          │
│   @Before("execution(* *(..))")  // Matches EVERYTHING!                 │
│   // Causes performance issues, unexpected behavior                     │
│                                                                          │
│   ✅ Be specific:                                                        │
│   @Before("execution(* com.app.service.*Service.*(..))")                │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   ❌ 2. BUSINESS LOGIC IN ASPECTS                                        │
│   ─────────────────────────────────                                      │
│   // Aspects should handle cross-cutting, not business logic           │
│   @Before("...")                                                         │
│   void checkInventory() { /* Complex business logic */ }                │
│                                                                          │
│   ✅ Keep business logic in services                                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   ❌ 3. MODIFYING ARGUMENTS UNEXPECTEDLY                                 │
│   ────────────────────────────────────────                               │
│   @Around("...")                                                         │
│   Object around(ProceedingJoinPoint pjp) {                              │
│       Object[] args = pjp.getArgs();                                    │
│       args[0] = "modified!";  // Surprises developers                  │
│       return pjp.proceed(args);                                          │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   ❌ 4. SWALLOWING EXCEPTIONS                                            │
│   ─────────────────────────────                                          │
│   @Around("...")                                                         │
│   Object around(ProceedingJoinPoint pjp) {                              │
│       try { return pjp.proceed(); }                                      │
│       catch (Exception e) { return null; }  // Silent failure!         │
│   }                                                                      │
│                                                                          │
│   ✅ Always re-throw or handle explicitly                                │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   ❌ 5. ASPECT DEPENDENCY CYCLES                                         │
│   ────────────────────────────────                                       │
│   @Aspect class A { @Autowired B b; }                                   │
│   @Aspect class B { @Autowired A a; }  // Circular!                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   ❌ 6. NOT TESTING ASPECTS                                              │
│   ─────────────────────────────                                          │
│   Assuming aspects work without tests                                    │
│                                                                          │
│   ✅ Always verify proxy creation and advice execution                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q47: How do you implement feature flags using AOP?

**Answer:**

```java
// Feature flag annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureFlag {
    String value();  // Feature name
    String fallbackMethod() default "";  // Optional fallback
}

// Feature flag aspect
@Aspect
@Component
@Slf4j
public class FeatureFlagAspect {
    
    @Autowired
    private FeatureFlagService featureFlagService;
    
    @Around("@annotation(featureFlag)")
    public Object checkFeatureFlag(ProceedingJoinPoint pjp, FeatureFlag featureFlag) 
            throws Throwable {
        
        String featureName = featureFlag.value();
        
        if (featureFlagService.isEnabled(featureName)) {
            log.debug("Feature '{}' is enabled, proceeding", featureName);
            return pjp.proceed();
        }
        
        log.info("Feature '{}' is disabled", featureName);
        
        // Try fallback method if specified
        if (!featureFlag.fallbackMethod().isEmpty()) {
            return invokeFallback(pjp, featureFlag.fallbackMethod());
        }
        
        // Return null or throw exception
        throw new FeatureDisabledException(featureName);
    }
    
    private Object invokeFallback(ProceedingJoinPoint pjp, String fallbackMethod) 
            throws Exception {
        Object target = pjp.getTarget();
        Method method = target.getClass().getMethod(fallbackMethod, 
            getParameterTypes(pjp));
        return method.invoke(target, pjp.getArgs());
    }
}

// Usage
@Service
public class PaymentService {
    
    @FeatureFlag(value = "new-payment-gateway", fallbackMethod = "processPaymentLegacy")
    public PaymentResult processPayment(Payment payment) {
        // New payment gateway implementation
        return newGateway.process(payment);
    }
    
    public PaymentResult processPaymentLegacy(Payment payment) {
        // Legacy fallback
        return legacyGateway.process(payment);
    }
}

// Feature flag service
@Service
public class FeatureFlagService {
    
    @Value("#{${feature.flags}}")
    private Map<String, Boolean> flags;
    
    public boolean isEnabled(String feature) {
        return flags.getOrDefault(feature, false);
    }
}
```

---

## Summary

| Term | Definition |
|------|------------|
| **Aspect** | Module containing cross-cutting concerns |
| **Join Point** | Point in execution where aspect can apply |
| **Pointcut** | Expression selecting join points |
| **Advice** | Action to take (Before, After, Around) |
| **Target** | Original object being advised |
| **Proxy** | Wrapper that implements aspects |
| **Weaving** | Process of linking aspects to targets |

---

> **Next Topic:** OOPS Basics
