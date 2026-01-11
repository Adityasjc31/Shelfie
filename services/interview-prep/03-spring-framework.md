# 🌱 Topic 3: Spring Framework - Interview Questions & Answers

This document contains comprehensive interview questions and answers about the Spring Framework core concepts.

---

## Q1: What is Spring Framework? What problems does it solve?

**Answer:**

**Spring Framework** is a comprehensive, lightweight, open-source application framework for Java that provides infrastructure support for developing enterprise applications.

### Problems Spring Solves:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    PROBLEMS SPRING SOLVES                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. TIGHT COUPLING (Without Spring)                                      │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │  public class OrderService {                                      │   │
│  │      // Directly creating dependency - TIGHTLY COUPLED! ❌        │   │
│  │      private InventoryService inventoryService =                  │   │
│  │          new InventoryServiceImpl();                              │   │
│  │                                                                   │   │
│  │      // Problems:                                                 │   │
│  │      // - Can't easily swap implementations                       │   │
│  │      // - Hard to test (can't mock)                               │   │
│  │      // - OrderService controls InventoryService lifecycle        │   │
│  │  }                                                                │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  WITH SPRING (Dependency Injection):                                     │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │  @Service                                                         │   │
│  │  public class OrderService {                                      │   │
│  │      // Spring injects the dependency - LOOSELY COUPLED! ✅       │   │
│  │      private final InventoryService inventoryService;             │   │
│  │                                                                   │   │
│  │      @Autowired                                                   │   │
│  │      public OrderService(InventoryService inventoryService) {     │   │
│  │          this.inventoryService = inventoryService;                │   │
│  │      }                                                            │   │
│  │      // Benefits:                                                 │   │
│  │      // - Easy to swap implementations                            │   │
│  │      // - Easy to test with mocks                                 │   │
│  │      // - Spring manages lifecycle                                │   │
│  │  }                                                                │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  2. BOILERPLATE CODE                                                     │
│     - JDBC connection handling → Spring Data handles it                 │
│     - Transaction management → @Transactional annotation                │
│     - Exception handling → @RestControllerAdvice                        │
│                                                                          │
│  3. CROSS-CUTTING CONCERNS                                               │
│     - Logging, Security, Transactions → AOP handles them                │
│                                                                          │
│  4. CONFIGURATION COMPLEXITY                                             │
│     - XML hell → Annotation-based configuration                         │
│     - Manual setup → Auto-configuration (Spring Boot)                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Core Features of Spring:

| Feature | Description |
|---------|-------------|
| **IoC Container** | Manages object creation and lifecycle |
| **Dependency Injection** | Injects dependencies automatically |
| **AOP** | Handles cross-cutting concerns |
| **Transaction Management** | Declarative transaction handling |
| **MVC Framework** | Web application development |
| **Data Access** | JDBC, ORM, Transaction abstraction |
| **Security** | Authentication and authorization |

---

## Q2: What is Inversion of Control (IoC)? Explain with an example.

**Answer:**

**Inversion of Control (IoC)** is a design principle where the control of object creation and lifecycle is transferred from the application code to a container/framework.

### Traditional vs IoC:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    TRADITIONAL vs IoC                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   TRADITIONAL CONTROL:                                                   │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  YOU are in control of everything                                  ││
│   │                                                                     ││
│   │  public class Main {                                                ││
│   │      public static void main(String[] args) {                       ││
│   │          // YOU create objects                                      ││
│   │          UserRepository repo = new UserRepositoryImpl();            ││
│   │          EmailService email = new EmailServiceImpl();               ││
│   │          UserService service = new UserServiceImpl(repo, email);    ││
│   │                                                                     ││
│   │          // YOU call methods                                        ││
│   │          service.createUser(user);                                  ││
│   │      }                                                              ││
│   │  }                                                                  ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   INVERTED CONTROL (IoC):                                                │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  FRAMEWORK is in control                                           ││
│   │                                                                     ││
│   │  @Service                                                           ││
│   │  public class UserServiceImpl implements UserService {              ││
│   │      // Framework creates and injects these                         ││
│   │      private final UserRepository repo;                             ││
│   │      private final EmailService email;                              ││
│   │                                                                     ││
│   │      @Autowired  // Framework handles injection                     ││
│   │      public UserServiceImpl(UserRepository repo,                    ││
│   │                             EmailService email) {                   ││
│   │          this.repo = repo;                                          ││
│   │          this.email = email;                                        ││
│   │      }                                                              ││
│   │  }                                                                  ││
│   │                                                                     ││
│   │  // In REST controller - Framework calls your method!              ││
│   │  @GetMapping("/users/{id}")                                         ││
│   │  public User getUser(@PathVariable Long id) {                       ││
│   │      return userService.findById(id);                               ││
│   │  }                                                                  ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Real-World Analogy:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    IoC ANALOGY: Restaurant                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Without IoC (Cooking at home):                                         │
│   - YOU buy ingredients                                                  │
│   - YOU prepare cooking equipment                                        │
│   - YOU cook the food                                                    │
│   - YOU serve the food                                                   │
│   - YOU clean up                                                         │
│   → YOU control everything                                               │
│                                                                          │
│   With IoC (Restaurant):                                                 │
│   - YOU just order food (define what you need)                           │
│   - RESTAURANT handles:                                                  │
│     - Ingredients (dependencies)                                         │
│     - Cooking (object creation)                                          │
│     - Serving (injection)                                                │
│     - Cleanup (lifecycle management)                                     │
│   → CONTROL is INVERTED to the restaurant                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Benefits of IoC:
- ✅ **Loose Coupling** - Classes don't create their dependencies
- ✅ **Easy Testing** - Can inject mocks for unit testing
- ✅ **Flexible Configuration** - Switch implementations without code changes
- ✅ **Single Responsibility** - Classes focus on business logic, not object creation

---

## Q3: What is Dependency Injection? What are the types of DI in Spring?

**Answer:**

**Dependency Injection (DI)** is a technique where objects receive their dependencies from an external source rather than creating them internally. It's an implementation of IoC.

### Types of Dependency Injection:

```
┌─────────────────────────────────────────────────────────────────────────┐
│               TYPES OF DEPENDENCY INJECTION IN SPRING                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. CONSTRUCTOR INJECTION (RECOMMENDED ✅)                              │
│   ──────────────────────────────────────                                 │
│   @Service                                                               │
│   public class OrderService {                                            │
│       private final InventoryService inventoryService;                   │
│       private final PaymentService paymentService;                       │
│                                                                          │
│       @Autowired  // Optional in Spring 4.3+ with single constructor    │
│       public OrderService(InventoryService inventoryService,             │
│                          PaymentService paymentService) {                │
│           this.inventoryService = inventoryService;                      │
│           this.paymentService = paymentService;                          │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ✅ Immutable dependencies (final fields)                               │
│   ✅ All required dependencies visible                                   │
│   ✅ Easy to test                                                        │
│   ✅ Prevents null dependencies                                          │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. SETTER INJECTION                                                    │
│   ─────────────────────                                                  │
│   @Service                                                               │
│   public class OrderService {                                            │
│       private InventoryService inventoryService;                         │
│                                                                          │
│       @Autowired                                                         │
│       public void setInventoryService(InventoryService service) {        │
│           this.inventoryService = service;                               │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ✅ Good for optional dependencies                                      │
│   ❌ Mutable - can be changed after construction                         │
│   ❌ Possible NullPointerException if not set                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. FIELD INJECTION (NOT RECOMMENDED ❌)                                │
│   ────────────────────────────────────────                               │
│   @Service                                                               │
│   public class OrderService {                                            │
│                                                                          │
│       @Autowired                                                         │
│       private InventoryService inventoryService;  // Inject directly    │
│   }                                                                      │
│                                                                          │
│   ❌ Cannot make fields final                                            │
│   ❌ Hidden dependencies                                                 │
│   ❌ Hard to test (requires reflection or Spring context)                │
│   ❌ Can lead to NullPointerException                                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Why Constructor Injection is Preferred:

| Aspect | Constructor | Setter | Field |
|--------|-------------|--------|-------|
| **Immutability** | ✅ final fields | ❌ mutable | ❌ mutable |
| **Required deps visible** | ✅ Yes | ❌ No | ❌ No |
| **Easy testing** | ✅ Yes | ⚠️ Possible | ❌ Needs reflection |
| **Null safety** | ✅ Guaranteed | ❌ Possible null | ❌ Possible null |
| **Circular dep detection** | ✅ At startup | ❌ At runtime | ❌ At runtime |

### Using with Lombok:
```java
@Service
@RequiredArgsConstructor  // Generates constructor for final fields
public class OrderService {
    private final InventoryService inventoryService;  // Injected via constructor
    private final PaymentService paymentService;      // Injected via constructor
}
```

---

## Q4: What is the Spring IoC Container? Explain BeanFactory vs ApplicationContext.

**Answer:**

The **Spring IoC Container** is the core of Spring Framework. It creates objects, wires them together, configures them, and manages their lifecycle.

### Container Visualization:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING IoC CONTAINER                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Configuration                          IoC Container                   │
│   (What to create)                      (Factory + Manager)              │
│                                                                          │
│   ┌─────────────────┐                   ┌─────────────────────────────┐ │
│   │ @Configuration  │                   │                             │ │
│   │ @Component      │ ─── Reads ───────▶│   ┌─────────────────────┐   │ │
│   │ @Service        │                   │   │   Bean Definitions   │   │ │
│   │ @Repository     │                   │   │   (Metadata)         │   │ │
│   │ application.yml │                   │   └──────────┬──────────┘   │ │
│   └─────────────────┘                   │              │              │ │
│                                         │              ▼              │ │
│                                         │   ┌─────────────────────┐   │ │
│                                         │   │   Fully Configured  │   │ │
│                                         │   │   Ready-to-use      │   │ │
│                                         │   │   BEANS             │   │ │
│                                         │   │                     │   │ │
│                                         │   │ ┌────┐ ┌────┐ ┌────┐│   │ │
│                                         │   │ │User│ │Order│ │Book││   │ │
│                                         │   │ │Svc │ │Svc │ │Svc ││   │ │
│                                         │   │ └────┘ └────┘ └────┘│   │ │
│                                         │   └─────────────────────┘   │ │
│                                         │                             │ │
│                                         └─────────────────────────────┘ │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### BeanFactory vs ApplicationContext:

```
┌─────────────────────────────────────────────────────────────────────────┐
│               BeanFactory vs ApplicationContext                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   BeanFactory (Basic Container)                                          │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  - Provides basic DI capabilities                                  ││
│   │  - Lazy initialization (creates beans when requested)              ││
│   │  - Lightweight, less memory                                        ││
│   │                                                                     ││
│   │  BeanFactory factory = new XmlBeanFactory(                         ││
│   │      new ClassPathResource("beans.xml"));                          ││
│   │  UserService userService = factory.getBean(UserService.class);     ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   ApplicationContext (Advanced Container) - RECOMMENDED                  │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  - Everything BeanFactory does, PLUS:                              ││
│   │  - Eager initialization (creates all singleton beans at startup)  ││
│   │  - Event publication (ApplicationEvent)                            ││
│   │  - Internationalization (i18n)                                     ││
│   │  - Environment abstraction (profiles, properties)                  ││
│   │  - AOP integration                                                 ││
│   │  - Web application support                                         ││
│   │                                                                     ││
│   │  ApplicationContext context =                                       ││
│   │      new AnnotationConfigApplicationContext(AppConfig.class);      ││
│   │                                                                     ││
│   │  // Common implementations:                                         ││
│   │  // - AnnotationConfigApplicationContext (Java config)             ││
│   │  // - ClassPathXmlApplicationContext (XML config)                  ││
│   │  // - WebApplicationContext (Web apps)                             ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Comparison Table:

| Feature | BeanFactory | ApplicationContext |
|---------|-------------|-------------------|
| **Bean Instantiation** | Lazy (on request) | Eager (at startup) |
| **Event Handling** | ❌ No | ✅ Yes |
| **i18n Support** | ❌ No | ✅ Yes |
| **AOP Integration** | Manual | Automatic |
| **Annotation Processing** | ❌ No | ✅ Yes |
| **Enterprise Features** | ❌ No | ✅ Yes |
| **Memory** | Lower | Higher |

> **Note:** In modern Spring applications, always use `ApplicationContext`. `BeanFactory` is rarely used directly.

---

## Q5: What are Spring Bean Scopes? Explain each scope.

**Answer:**

**Bean Scope** defines the lifecycle and visibility of a bean within the Spring container.

### Scope Visualization:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       SPRING BEAN SCOPES                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. SINGLETON (Default)                                                  │
│  ─────────────────────                                                   │
│  One instance per Spring container                                       │
│                                                                          │
│  Request 1 ─────────┐                                                    │
│                     │     ┌─────────────────┐                           │
│  Request 2 ─────────┼────▶│  Same Instance  │                           │
│                     │     │  (UserService)  │                           │
│  Request 3 ─────────┘     └─────────────────┘                           │
│                                                                          │
│  @Scope("singleton")  // or just @Service (default)                     │
│  public class UserService { }                                            │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────   │
│                                                                          │
│  2. PROTOTYPE                                                            │
│  ────────────                                                            │
│  New instance every time bean is requested                               │
│                                                                          │
│  Request 1 ────────────▶ ┌─────────────────┐                            │
│                          │   Instance 1     │                            │
│                          └─────────────────┘                            │
│  Request 2 ────────────▶ ┌─────────────────┐                            │
│                          │   Instance 2     │                            │
│                          └─────────────────┘                            │
│  Request 3 ────────────▶ ┌─────────────────┐                            │
│                          │   Instance 3     │                            │
│                          └─────────────────┘                            │
│                                                                          │
│  @Scope("prototype")                                                     │
│  public class ShoppingCart { }                                           │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────   │
│                                                                          │
│  3. REQUEST (Web only)                                                   │
│  ─────────────────────                                                   │
│  One instance per HTTP request                                           │
│                                                                          │
│  HTTP Request 1 ────▶ ┌──────────────┐ ──▶ Destroyed after response     │
│                       │ Instance A   │                                   │
│                       └──────────────┘                                   │
│  HTTP Request 2 ────▶ ┌──────────────┐ ──▶ Destroyed after response     │
│                       │ Instance B   │                                   │
│                       └──────────────┘                                   │
│                                                                          │
│  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)    │
│  public class RequestLogger { }                                          │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────   │
│                                                                          │
│  4. SESSION (Web only)                                                   │
│  ─────────────────────                                                   │
│  One instance per HTTP session                                           │
│                                                                          │
│  User A (Session) ═══════════════════════════▶ ┌──────────────┐         │
│       Request 1 ────────────────────────────▶  │ Instance for │         │
│       Request 2 ────────────────────────────▶  │   User A     │         │
│       Request 3 ────────────────────────────▶  └──────────────┘         │
│                                                                          │
│  User B (Session) ═══════════════════════════▶ ┌──────────────┐         │
│       Request 1 ────────────────────────────▶  │ Instance for │         │
│       Request 2 ────────────────────────────▶  │   User B     │         │
│                                                └──────────────┘         │
│                                                                          │
│  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)    │
│  public class UserPreferences { }                                        │
│                                                                          │
│  ─────────────────────────────────────────────────────────────────────   │
│                                                                          │
│  5. APPLICATION (Web only)                                               │
│  ─────────────────────────                                               │
│  One instance per ServletContext (entire web application)               │
│                                                                          │
│  6. WEBSOCKET (Web only)                                                 │
│  ────────────────────────                                                │
│  One instance per WebSocket session                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Summary Table:

| Scope | Instances | Lifecycle | Use Case |
|-------|-----------|-----------|----------|
| **singleton** | 1 per container | Container lifecycle | Stateless services |
| **prototype** | New each time | Not managed by Spring | Stateful beans |
| **request** | 1 per HTTP request | Request lifecycle | Request-specific data |
| **session** | 1 per HTTP session | Session lifecycle | User session data |
| **application** | 1 per ServletContext | Application lifecycle | App-wide cache |
| **websocket** | 1 per WebSocket | WebSocket lifecycle | WebSocket state |

### Code Examples:
```java
// Singleton (default) - One instance shared
@Service
public class UserService { }

// Prototype - New instance each time
@Component
@Scope("prototype")
public class ReportGenerator { }

// Request scope - One per HTTP request
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext { }
```

---

## Q6: What is the Bean Lifecycle in Spring?

**Answer:**

Spring manages the complete lifecycle of a bean from instantiation to destruction.

### Bean Lifecycle Diagram:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       SPRING BEAN LIFECYCLE                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 1. INSTANTIATION                                                 │   │
│   │    Container creates bean instance using constructor             │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 2. POPULATE PROPERTIES                                           │   │
│   │    Inject dependencies (@Autowired, @Value)                      │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 3. SET BEAN NAME (BeanNameAware)                                 │   │
│   │    void setBeanName(String name)                                 │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 4. SET BEAN FACTORY (BeanFactoryAware)                           │   │
│   │    void setBeanFactory(BeanFactory factory)                      │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 5. SET APPLICATION CONTEXT (ApplicationContextAware)             │   │
│   │    void setApplicationContext(ApplicationContext context)        │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 6. PRE-INITIALIZATION (BeanPostProcessor)                        │   │
│   │    postProcessBeforeInitialization()                             │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 7. INITIALIZATION                                                │   │
│   │    a) @PostConstruct method                                      │   │
│   │    b) InitializingBean.afterPropertiesSet()                      │   │
│   │    c) Custom init-method                                         │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 8. POST-INITIALIZATION (BeanPostProcessor)                       │   │
│   │    postProcessAfterInitialization()                              │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 9. BEAN READY TO USE                                             │   │
│   │    Application uses the bean                                     │   │
│   └───────────────────────────────┬─────────────────────────────────┘   │
│                                   ▼                                      │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 10. DESTRUCTION (on container shutdown)                          │   │
│   │    a) @PreDestroy method                                         │   │
│   │    b) DisposableBean.destroy()                                   │   │
│   │    c) Custom destroy-method                                      │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Practical Example:
```java
@Component
public class DatabaseConnectionPool implements 
        InitializingBean, DisposableBean, BeanNameAware {
    
    private String beanName;
    private Connection connection;
    
    // Step 3: BeanNameAware
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("Bean name set: " + name);
    }
    
    // Step 7a: @PostConstruct (preferred)
    @PostConstruct
    public void init() {
        System.out.println("PostConstruct: Initializing connection pool");
        this.connection = createConnection();
    }
    
    // Step 7b: InitializingBean
    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet: Additional initialization");
    }
    
    // Step 10a: @PreDestroy (preferred)
    @PreDestroy
    public void cleanup() {
        System.out.println("PreDestroy: Closing connections");
    }
    
    // Step 10b: DisposableBean
    @Override
    public void destroy() {
        System.out.println("destroy: Final cleanup");
        closeConnection(connection);
    }
}
```

### Most Common Lifecycle Methods:
| Method | Purpose | When Called |
|--------|---------|-------------|
| **@PostConstruct** | Initialize resources | After DI, before use |
| **@PreDestroy** | Cleanup resources | Before bean destruction |

---

## Q7: What is @Autowired? How does Spring resolve dependencies?

**Answer:**

**@Autowired** tells Spring to automatically inject a dependency. Spring uses several strategies to find the right bean to inject.

### Autowiring Modes:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       @AUTOWIRED RESOLUTION                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. BY TYPE (Default)                                                   │
│   ────────────────────                                                   │
│   Spring looks for a bean of the required TYPE                          │
│                                                                          │
│   @Autowired                                                             │
│   private UserService userService;  // Finds bean of type UserService  │
│                                                                          │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  Container has:                                                    ││
│   │  ┌──────────────────────┐                                          ││
│   │  │ UserServiceImpl      │ ← Type matches UserService               ││
│   │  │ (implements          │                                          ││
│   │  │  UserService)        │ ══════════════════▶ INJECTED!           ││
│   │  └──────────────────────┘                                          ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. MULTIPLE BEANS OF SAME TYPE (Problem!)                              │
│   ─────────────────────────────────────────                              │
│   @Autowired                                                             │
│   private PaymentService paymentService;  // Which one to inject?       │
│                                                                          │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  Container has:                                                    ││
│   │  ┌──────────────────────┐                                          ││
│   │  │ CreditCardPayment    │ ← Both implement PaymentService          ││
│   │  │ (PaymentService)     │                                          ││
│   │  └──────────────────────┘     ❌ NoUniqueBeanDefinitionException   ││
│   │  ┌──────────────────────┐                                          ││
│   │  │ PayPalPayment        │                                          ││
│   │  │ (PaymentService)     │                                          ││
│   │  └──────────────────────┘                                          ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   SOLUTIONS:                                                             │
│   ──────────                                                             │
│                                                                          │
│   a) @Qualifier - Specify bean name                                     │
│      @Autowired                                                          │
│      @Qualifier("creditCardPayment")                                     │
│      private PaymentService paymentService;                              │
│                                                                          │
│   b) @Primary - Mark one as default                                      │
│      @Service                                                            │
│      @Primary  // This will be injected when multiple match             │
│      public class CreditCardPayment implements PaymentService { }        │
│                                                                          │
│   c) Field name matching - Name field same as bean                       │
│      @Autowired                                                          │
│      private PaymentService creditCardPayment;  // Matches bean name     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. OPTIONAL DEPENDENCIES                                               │
│   ────────────────────────                                               │
│   @Autowired(required = false)  // Won't fail if bean not found         │
│   private Optional<CacheService> cacheService;  // Or use Optional       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Resolution Order:
1. **Match by Type** - Find beans of required type
2. **If multiple matches** - Match by qualifier or field name
3. **If still ambiguous** - Check for @Primary bean
4. **If still ambiguous** - Throw `NoUniqueBeanDefinitionException`

### Best Practices:
```java
@Service
@RequiredArgsConstructor  // Use constructor injection
public class OrderService {
    private final PaymentService paymentService;  // Injected via constructor
    
    // If multiple implementations exist, be explicit:
    // Option 1: Use @Qualifier
    public OrderService(@Qualifier("stripe") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

// Option 2: Mark one as @Primary
@Service
@Primary  // Default when multiple PaymentService beans exist
public class StripePaymentService implements PaymentService { }
```

---

## Q8: What is the difference between @Component, @Service, @Repository, and @Controller?

**Answer:**

These are **stereotype annotations** that mark classes as Spring-managed beans. They are functionally similar but provide **semantic meaning** and **specific behaviors**.

### Annotation Hierarchy:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING STEREOTYPE ANNOTATIONS                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                          @Component                                      │
│                    (Generic stereotype)                                  │
│                              │                                           │
│              ┌───────────────┼───────────────┐                          │
│              │               │               │                          │
│              ▼               ▼               ▼                          │
│        @Repository      @Service       @Controller                      │
│        (Data Layer)   (Business)     (Web Layer)                        │
│              │               │               │                          │
│              │               │               └──▶ @RestController       │
│              │               │                   (REST APIs)            │
│              │               │                                          │
│              ▼               ▼                                          │
│   ┌──────────────────┐  ┌──────────────────┐                           │
│   │ UserRepository   │  │ UserService      │                           │
│   │ BookRepository   │  │ OrderService     │                           │
│   └──────────────────┘  └──────────────────┘                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Detailed Comparison:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ANNOTATION COMPARISON                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   @Component                                                             │
│   ──────────                                                             │
│   - Generic stereotype for any Spring-managed component                  │
│   - No special behavior                                                  │
│   - Use when none of the others fit                                     │
│                                                                          │
│   @Component                                                             │
│   public class EmailValidator { }                                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @Service                                                               │
│   ────────                                                               │
│   - Marks business logic / service layer classes                        │
│   - No special behavior (purely semantic)                               │
│   - Makes code intent clear                                             │
│                                                                          │
│   @Service                                                               │
│   public class UserService {                                             │
│       public User createUser(UserDTO dto) { /* business logic */ }      │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @Repository                                                            │
│   ───────────                                                            │
│   - Marks data access / persistence layer classes                       │
│   - SPECIAL BEHAVIOR: Exception translation!                            │
│     Converts database-specific exceptions to Spring's                    │
│     DataAccessException hierarchy                                        │
│                                                                          │
│   @Repository                                                            │
│   public class UserRepositoryImpl implements UserRepository {            │
│       // SQLException → DataAccessException (automatically)             │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @Controller                                                            │
│   ───────────                                                            │
│   - Marks web controller classes (MVC)                                  │
│   - SPECIAL BEHAVIOR: Handles HTTP requests                             │
│   - Returns view names for rendering                                    │
│                                                                          │
│   @Controller                                                            │
│   public class HomeController {                                          │
│       @GetMapping("/")                                                   │
│       public String home(Model model) {                                  │
│           return "home";  // View name                                   │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @RestController = @Controller + @ResponseBody                          │
│   ───────────────                                                        │
│   - For REST APIs                                                        │
│   - Returns data directly (JSON/XML)                                    │
│                                                                          │
│   @RestController                                                        │
│   @RequestMapping("/api/users")                                          │
│   public class UserController {                                          │
│       @GetMapping("/{id}")                                               │
│       public User getUser(@PathVariable Long id) {                       │
│           return userService.findById(id);  // Returns JSON             │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Summary Table:

| Annotation | Layer | Special Behavior |
|------------|-------|-----------------|
| **@Component** | Any | None (generic) |
| **@Service** | Business Logic | None (semantic only) |
| **@Repository** | Data Access | Exception translation |
| **@Controller** | Web (MVC) | Request handling + view |
| **@RestController** | Web (REST) | Request handling + JSON |

---

## Q9: What is @Configuration and @Bean? How do you define beans programmatically?

**Answer:**

**@Configuration** marks a class as a source of bean definitions. **@Bean** marks a method that returns a bean to be managed by Spring.

### Usage:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    @Configuration AND @Bean                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WHY USE @Bean?                                                         │
│   - When you can't annotate the class (third-party library)            │
│   - When you need complex initialization logic                          │
│   - When you need conditional bean creation                             │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @Configuration                                                         │
│   public class AppConfig {                                               │
│                                                                          │
│       // Simple bean definition                                          │
│       @Bean                                                              │
│       public ObjectMapper objectMapper() {                               │
│           ObjectMapper mapper = new ObjectMapper();                      │
│           mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);           │
│           return mapper;                                                 │
│       }                                                                  │
│                                                                          │
│       // Bean with dependencies (injected as method parameter)          │
│       @Bean                                                              │
│       public UserService userService(UserRepository repository,          │
│                                      EmailService emailService) {        │
│           return new UserServiceImpl(repository, emailService);          │
│       }                                                                  │
│                                                                          │
│       // Bean with custom name                                           │
│       @Bean(name = "customDataSource")                                   │
│       public DataSource dataSource() {                                   │
│           HikariDataSource ds = new HikariDataSource();                  │
│           ds.setJdbcUrl("jdbc:mysql://localhost/db");                    │
│           ds.setUsername("user");                                        │
│           ds.setPassword("password");                                    │
│           return ds;                                                     │
│       }                                                                  │
│                                                                          │
│       // Bean with init and destroy methods                              │
│       @Bean(initMethod = "init", destroyMethod = "cleanup")              │
│       public ConnectionPool connectionPool() {                           │
│           return new ConnectionPool();                                   │
│       }                                                                  │
│                                                                          │
│       // Conditional bean                                                │
│       @Bean                                                              │
│       @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")│
│       public CacheManager cacheManager() {                               │
│           return new RedisCacheManager();                                │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### @Component vs @Bean:

| Aspect | @Component | @Bean |
|--------|------------|-------|
| **Target** | Class | Method |
| **Location** | On class itself | In @Configuration class |
| **Use Case** | Your own classes | Third-party or complex beans |
| **Autowiring** | Auto-detected via scanning | Explicit definition |

### Practical Example (Third-party library):
```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.example.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .build();
    }
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
```

---

## Q10: What is @Value annotation? How do you inject properties?

**Answer:**

**@Value** injects values from property files, environment variables, or expressions into fields.

### Usage Examples:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       @VALUE ANNOTATION                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   application.properties:                                                │
│   ───────────────────────                                                │
│   app.name=Digital Bookstore                                             │
│   app.version=1.0.0                                                      │
│   server.port=8080                                                       │
│   jwt.secret=mySecretKey123                                              │
│   jwt.expiration=86400000                                                │
│   features.cache.enabled=true                                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @Service                                                               │
│   public class AppService {                                              │
│                                                                          │
│       // 1. Simple property injection                                    │
│       @Value("${app.name}")                                              │
│       private String appName;  // "Digital Bookstore"                   │
│                                                                          │
│       // 2. With default value (if property not found)                   │
│       @Value("${app.description:No description}")                        │
│       private String description;  // "No description" (default)        │
│                                                                          │
│       // 3. Inject as different type                                     │
│       @Value("${jwt.expiration}")                                        │
│       private Long expirationMs;  // 86400000 (auto-converted)          │
│                                                                          │
│       @Value("${features.cache.enabled}")                                │
│       private boolean cacheEnabled;  // true                            │
│                                                                          │
│       // 4. SpEL (Spring Expression Language)                            │
│       @Value("#{${jwt.expiration} / 1000}")                              │
│       private Long expirationSeconds;  // 86400                         │
│                                                                          │
│       @Value("#{systemProperties['user.home']}")                         │
│       private String userHome;  // System property                       │
│                                                                          │
│       // 5. Inject list                                                  │
│       @Value("${app.supported-languages:en,fr,de}")                      │
│       private List<String> languages;                                    │
│                                                                          │
│       // 6. Environment variable                                         │
│       @Value("${DATABASE_URL:jdbc:mysql://localhost/db}")                │
│       private String dbUrl;  // From env var or default                 │
│                                                                          │
│       // 7. Constructor injection (RECOMMENDED)                          │
│       public AppService(@Value("${jwt.secret}") String secret) {         │
│           this.secret = secret;                                          │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Better Alternative - @ConfigurationProperties:
```java
// application.properties
// jwt.secret=mySecret
// jwt.expiration=86400000
// jwt.refresh-expiration=604800000
// jwt.issuer=digital-bookstore

@ConfigurationProperties(prefix = "jwt")
@Configuration
@Getter @Setter
public class JwtProperties {
    private String secret;
    private Long expiration;
    private Long refreshExpiration;
    private String issuer;
}

// Usage
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProps;  // All JWT properties bundled
    
    public String generateToken() {
        // Use jwtProps.getSecret(), jwtProps.getExpiration()
    }
}
```

### @Value vs @ConfigurationProperties:

| Aspect | @Value | @ConfigurationProperties |
|--------|--------|-------------------------|
| **Granularity** | Per field | Group of properties |
| **Type Safety** | ❌ Less safe | ✅ Type safe |
| **Validation** | Manual | @Validated support |
| **Best For** | Single values | Related configuration |

---

## Q11: What is Spring Boot? How is it different from Spring Framework? (Beginner)

**Answer:**

**Spring Boot** is an extension of Spring Framework that simplifies application setup and development through auto-configuration and convention over configuration.

| Aspect | Spring Framework | Spring Boot |
|--------|------------------|-------------|
| **Configuration** | Manual XML/Java config | Auto-configuration |
| **Setup** | Complex, many dependencies | Starter dependencies |
| **Server** | External server required | Embedded server (Tomcat) |
| **Deployment** | WAR file | Executable JAR |

```java
// Spring Boot - Minimal setup needed!
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## Q12: What is @SpringBootApplication? What does it combine? (Beginner)

**Answer:**

**@SpringBootApplication** is a convenience annotation that combines three annotations:

```
@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
```

| Annotation | Purpose |
|------------|---------|
| **@Configuration** | Marks class as source of bean definitions |
| **@EnableAutoConfiguration** | Enables Spring Boot auto-configuration |
| **@ComponentScan** | Scans for components in current package and sub-packages |

---

## Q13: What is Spring Profiles? How do you use them? (Beginner)

**Answer:**

**Spring Profiles** allow you to define different configurations for different environments (dev, test, prod).

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost/dev_db

# application-prod.yml  
spring:
  datasource:
    url: jdbc:mysql://prod-server/prod_db
```

**Activating profiles:**
```properties
# application.properties
spring.profiles.active=dev

# Or via command line
java -jar app.jar --spring.profiles.active=prod

# Or environment variable
SPRING_PROFILES_ACTIVE=prod
```

**Conditional beans:**
```java
@Service
@Profile("dev")
public class MockEmailService implements EmailService { }

@Service
@Profile("prod")
public class RealEmailService implements EmailService { }
```

---

## Q14: What is AOP (Aspect-Oriented Programming)? Explain key concepts. (Intermediate)

**Answer:**

**AOP** separates cross-cutting concerns (logging, security, transactions) from business logic.

| Term | Description |
|------|-------------|
| **Aspect** | Module containing cross-cutting logic (@Aspect) |
| **Advice** | Action taken (Before, After, Around) |
| **Join Point** | Point in execution (method call) |
| **Pointcut** | Expression matching join points |
| **Weaving** | Linking aspects with target objects |

```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling: " + joinPoint.getSignature().getName());
    }
    
    @Around("@annotation(Timed)")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        log.info("Execution time: {}ms", System.currentTimeMillis() - start);
        return result;
    }
}
```

---

## Q15: What is @Transactional? How does transaction management work? (Intermediate)

**Answer:**

**@Transactional** provides declarative transaction management - Spring handles commit/rollback automatically.

```java
@Service
public class OrderService {
    
    @Transactional  // All-or-nothing operation
    public void placeOrder(Order order) {
        inventoryService.decreaseStock(order.getItems());
        paymentService.processPayment(order.getTotal());
        orderRepository.save(order);
        // If any step fails, entire transaction rolls back
    }
    
    @Transactional(readOnly = true)  // Optimized for reads
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }
    
    @Transactional(rollbackFor = CustomException.class)
    public void customRollback() { }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void newTransaction() { }  // Runs in new transaction
}
```

**Key attributes:**
- `readOnly` - Optimization hint for reads
- `rollbackFor` - Which exceptions trigger rollback
- `propagation` - How transactions interact
- `isolation` - Transaction isolation level

---

## Q16: What is the difference between @Controller and @RestController? (Beginner)

**Answer:**

| Aspect | @Controller | @RestController |
|--------|-------------|-----------------|
| **Returns** | View name (HTML) | Data (JSON/XML) |
| **@ResponseBody** | Required per method | Implicit |
| **Use Case** | MVC web apps | REST APIs |

```java
// @Controller - Returns view
@Controller
public class WebController {
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Hello");
        return "home";  // Renders home.html
    }
}

// @RestController - Returns JSON
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();  // Returns JSON automatically
    }
}
```

---

## Q17: What are Spring Boot Starters? Name some common ones. (Beginner)

**Answer:**

**Starters** are dependency descriptors that bundle related dependencies together.

| Starter | Purpose |
|---------|---------|
| `spring-boot-starter-web` | Web apps, REST APIs |
| `spring-boot-starter-data-jpa` | JPA/Hibernate |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-test` | Testing (JUnit, Mockito) |
| `spring-boot-starter-validation` | Bean validation |
| `spring-boot-starter-actuator` | Production monitoring |

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Includes: Spring MVC, Tomcat, Jackson, Validation -->
```

---

## Q18: What is @RequestMapping and its variants? (Beginner)

**Answer:**

**@RequestMapping** maps HTTP requests to handler methods.

```java
@RestController
@RequestMapping("/api/books")  // Base path
public class BookController {
    
    @GetMapping          // GET /api/books
    public List<Book> getAll() { }
    
    @GetMapping("/{id}") // GET /api/books/123
    public Book getById(@PathVariable Long id) { }
    
    @PostMapping         // POST /api/books
    public Book create(@RequestBody BookDTO dto) { }
    
    @PutMapping("/{id}") // PUT /api/books/123
    public Book update(@PathVariable Long id, @RequestBody BookDTO dto) { }
    
    @DeleteMapping("/{id}") // DELETE /api/books/123
    public void delete(@PathVariable Long id) { }
    
    @PatchMapping("/{id}")  // PATCH /api/books/123
    public Book partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) { }
}
```

---

## Q19: What is @PathVariable vs @RequestParam vs @RequestBody? (Beginner)

**Answer:**

| Annotation | Source | Example |
|------------|--------|---------|
| **@PathVariable** | URL path | `/users/123` → `123` |
| **@RequestParam** | Query string | `/users?name=John` → `John` |
| **@RequestBody** | Request body | JSON payload |

```java
@GetMapping("/users/{id}")  
public User getUser(@PathVariable Long id) { }  // From URL path

@GetMapping("/users")
public List<User> search(
    @RequestParam String name,                    // Required
    @RequestParam(required = false) String city,  // Optional
    @RequestParam(defaultValue = "10") int limit  // With default
) { }

@PostMapping("/users")
public User create(@RequestBody UserDTO dto) { } // From JSON body
```

---

## Q20: What is @Valid and @Validated? How do you validate DTOs? (Intermediate)

**Answer:**

**@Valid** triggers bean validation on request bodies using Jakarta validation annotations.

```java
public class UserDTO {
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @Min(value = 18, message = "Must be at least 18")
    private Integer age;
}

@PostMapping("/users")
public User create(@Valid @RequestBody UserDTO dto) {
    // Throws MethodArgumentNotValidException if validation fails
}

// Handle validation errors globally
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
```

---

## Q21: What is @RestControllerAdvice? How do you handle exceptions globally? (Intermediate)

**Answer:**

**@RestControllerAdvice** provides global exception handling for REST controllers.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex) {
        return new ErrorResponse("ERROR", "An unexpected error occurred");
    }
}
```

---

## Q22: What is Spring Data JPA? What are its benefits? (Intermediate)

**Answer:**

**Spring Data JPA** simplifies database access by providing repository abstractions.

```java
// Just define interface - Spring provides implementation!
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Derived query methods
    List<User> findByName(String name);
    List<User> findByEmailContaining(String email);
    Optional<User> findByEmailAndStatus(String email, Status status);
    
    // Custom JPQL
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
    
    // Native SQL
    @Query(value = "SELECT * FROM users WHERE status = ?1", nativeQuery = true)
    List<User> findByStatusNative(String status);
}
```

**Benefits:**
- ✅ No boilerplate DAO code
- ✅ Automatic query generation from method names
- ✅ Pagination and sorting built-in
- ✅ Auditing support (@CreatedDate, @LastModifiedDate)

---

## Q23: What is the N+1 problem? How do you solve it? (Hard)

**Answer:**

**N+1 Problem**: When fetching N entities, JPA executes 1 query for entities + N queries for relationships.

```java
// PROBLEM: 1 query for books + N queries for authors
List<Book> books = bookRepository.findAll();  // 1 query
for (Book book : books) {
    book.getAuthor().getName();  // N queries (lazy loading)
}

// SOLUTIONS:

// 1. JOIN FETCH
@Query("SELECT b FROM Book b JOIN FETCH b.author")
List<Book> findAllWithAuthors();

// 2. @EntityGraph
@EntityGraph(attributePaths = {"author", "category"})
List<Book> findAll();

// 3. Batch fetching in application.properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
```

---

## Q24: What is the difference between FetchType.LAZY and FetchType.EAGER? (Intermediate)

**Answer:**

| FetchType | When Loaded | Performance |
|-----------|-------------|-------------|
| **LAZY** | On first access | Better (loads when needed) |
| **EAGER** | With parent entity | Can cause N+1 problems |

```java
@Entity
public class Book {
    @ManyToOne(fetch = FetchType.LAZY)  // Default for @ManyToOne is EAGER
    private Author author;  // Loaded only when accessed
    
    @OneToMany(fetch = FetchType.LAZY)  // Default for @OneToMany
    private List<Review> reviews;
}
```

**Best Practice:** Use LAZY by default, fetch eagerly only when needed with JOIN FETCH.

---

## Q25: What is Spring Security? How does authentication work? (Intermediate)

**Answer:**

**Spring Security** provides authentication, authorization, and protection against common attacks.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Q26: What is JWT? How do you implement JWT authentication? (Intermediate)

**Answer:**

**JWT (JSON Web Token)** is a compact, URL-safe token format for stateless authentication.

```
JWT Structure: HEADER.PAYLOAD.SIGNATURE
```

```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    public String generateToken(UserDetails user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

---

## Q27: What is a Filter vs Interceptor in Spring? (Intermediate)

**Answer:**

| Aspect | Filter | Interceptor |
|--------|--------|-------------|
| **Level** | Servlet (before Spring) | Spring MVC |
| **Scope** | All requests | Only to controllers |
| **Access to** | Request/Response only | Handler info, ModelAndView |

```java
// Filter - Servlet level
@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        log.info("Filter: Before request");
        chain.doFilter(req, res);
        log.info("Filter: After request");
    }
}

// Interceptor - Spring MVC level
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        log.info("Interceptor: Before controller");
        return true;  // Continue processing
    }
}
```

---

## Q28: What is @Async? How do you run methods asynchronously? (Intermediate)

**Answer:**

**@Async** runs methods in a separate thread, returning immediately.

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }
}

@Service
public class EmailService {
    @Async
    public void sendEmail(String to, String message) {
        // Runs in background thread
        // Caller doesn't wait for this to complete
    }
    
    @Async
    public CompletableFuture<String> sendEmailWithResult(String to) {
        // Return Future if you need the result later
        return CompletableFuture.completedFuture("Sent!");
    }
}
```

---

## Q29: What is @Scheduled? How do you create scheduled tasks? (Beginner)

**Answer:**

**@Scheduled** runs methods at fixed intervals or using cron expressions.

```java
@Configuration
@EnableScheduling
public class SchedulerConfig { }

@Service
public class CleanupService {
    
    @Scheduled(fixedRate = 60000)  // Every 60 seconds
    public void cleanupExpiredTokens() { }
    
    @Scheduled(fixedDelay = 30000)  // 30 sec after last completion
    public void processQueue() { }
    
    @Scheduled(cron = "0 0 2 * * ?")  // Daily at 2 AM
    public void dailyReport() { }
    
    @Scheduled(cron = "0 */5 * * * ?")  // Every 5 minutes
    public void healthCheck() { }
}
```

---

## Q30: What is Spring Actuator? What endpoints does it provide? (Beginner)

**Answer:**

**Spring Actuator** provides production-ready features for monitoring and managing applications.

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
  endpoint:
    health:
      show-details: always
```

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health status |
| `/actuator/info` | App information |
| `/actuator/metrics` | Application metrics |
| `/actuator/env` | Environment properties |
| `/actuator/beans` | All Spring beans |
| `/actuator/mappings` | All @RequestMapping paths |

---

## Q31: What is Circuit Breaker pattern? How does Resilience4j work? (Hard)

**Answer:**

**Circuit Breaker** prevents cascading failures by stopping requests to failing services.

```
States: CLOSED → OPEN → HALF_OPEN → CLOSED
```

```java
@Service
public class InventoryService {
    
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallback")
    @Retry(name = "inventory")
    public InventoryResponse checkStock(Long bookId) {
        return inventoryClient.getStock(bookId);
    }
    
    public InventoryResponse fallback(Long bookId, Exception ex) {
        return new InventoryResponse(bookId, 0, "Service unavailable");
    }
}
```

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    instances:
      inventory:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
```

---

## Q32: What is @Cacheable? How does Spring caching work? (Intermediate)

**Answer:**

**@Cacheable** caches method results, avoiding repeated expensive operations.

```java
@Configuration
@EnableCaching
public class CacheConfig { }

@Service
public class BookService {
    
    @Cacheable(value = "books", key = "#id")
    public Book findById(Long id) {
        // Only called on cache miss
        return bookRepository.findById(id).orElseThrow();
    }
    
    @CachePut(value = "books", key = "#book.id")
    public Book update(Book book) {
        // Always executes, updates cache
        return bookRepository.save(book);
    }
    
    @CacheEvict(value = "books", key = "#id")
    public void delete(Long id) {
        // Removes from cache
        bookRepository.deleteById(id);
    }
    
    @CacheEvict(value = "books", allEntries = true)
    public void clearCache() { }
}
```

---

## Q33: What is the difference between @Bean and @Component? (Beginner)

**Answer:**

| Aspect | @Bean | @Component |
|--------|-------|------------|
| **Target** | Method | Class |
| **Location** | @Configuration class | On class itself |
| **Use Case** | Third-party libraries | Your own classes |
| **Bean Name** | Method name | Class name (lowercase first) |

```java
// @Bean - For third-party classes you can't annotate
@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// @Component - For your own classes
@Component
public class EmailValidator { }
```

---

## Q34: What is @Conditional? How do you create conditional beans? (Intermediate)

**Answer:**

**@Conditional** creates beans based on certain conditions.

```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    @ConditionalOnProperty(name = "cache.type", havingValue = "redis")
    public CacheManager redisCacheManager() {
        return new RedisCacheManager();
    }
    
    @Bean
    @ConditionalOnProperty(name = "cache.type", havingValue = "memory", matchIfMissing = true)
    public CacheManager memoryCacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource defaultDataSource() { }
    
    @Bean
    @ConditionalOnClass(name = "org.postgresql.Driver")
    public DataSource postgresDataSource() { }
}
```

---

## Q35: What is WebClient? How is it different from RestTemplate? (Intermediate)

**Answer:**

| Aspect | RestTemplate | WebClient |
|--------|-------------|-----------|
| **Style** | Synchronous | Reactive/Async |
| **Blocking** | Yes | No |
| **Status** | Maintenance mode | Recommended |

```java
// WebClient (Modern - non-blocking)
@Service
public class BookClient {
    private final WebClient webClient;
    
    public Mono<Book> getBook(Long id) {
        return webClient.get()
            .uri("/books/{id}", id)
            .retrieve()
            .bodyToMono(Book.class);
    }
    
    // Blocking call if needed
    public Book getBookSync(Long id) {
        return getBook(id).block();
    }
}

// RestTemplate (Legacy - blocking)
Book book = restTemplate.getForObject("/books/{id}", Book.class, id);
```

---

## Q36: What is @EnableAutoConfiguration? How does auto-configuration work? (Intermediate)

**Answer:**

**@EnableAutoConfiguration** automatically configures Spring based on classpath dependencies.

```
How it works:
1. Spring scans META-INF/spring.factories (or spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports)
2. Finds @Configuration classes
3. Applies @Conditional annotations to decide which beans to create
```

```java
// Disable specific auto-configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application { }

// Or in properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

---

## Q37: What is the difference between findById and getById/getReferenceById? (Intermediate)

**Answer:**

| Method | Returns | When to Use |
|--------|---------|-------------|
| `findById()` | `Optional<T>` | When you need entity data |
| `getReferenceById()` | Proxy (lazy) | When setting FK relationships |

```java
// findById - Executes query immediately
Optional<Book> book = bookRepository.findById(1L);

// getReferenceById - Returns proxy, no query until accessed
Book bookRef = bookRepository.getReferenceById(1L);
// Use for setting relationships without loading
order.setBook(bookRef);  // No query executed yet
```

---

## Q38: What is @EntityListeners and JPA Auditing? (Intermediate)

**Answer:**

**JPA Auditing** automatically populates audit fields like createdAt, updatedAt.

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig { }

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String updatedBy;
}

// For @CreatedBy/@LastModifiedBy
@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityContextHolder.getContext()
            .getAuthentication().getName());
    }
}
```

---

## Q39: What is Pagination in Spring Data? How do you implement it? (Beginner)

**Answer:**

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByCategory(String category, Pageable pageable);
}

@GetMapping("/books")
public Page<Book> getBooks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "title") String sortBy
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return bookRepository.findAll(pageable);
}

// Response includes:
// content, totalElements, totalPages, size, number, first, last
```

---

## Q40: What are Projections in Spring Data JPA? (Intermediate)

**Answer:**

**Projections** return partial entity data instead of full entities.

```java
// Interface-based projection
public interface BookSummary {
    String getTitle();
    String getAuthorName();
    
    @Value("#{target.title + ' by ' + target.author.name}")
    String getFullTitle();
}

public interface BookRepository extends JpaRepository<Book, Long> {
    List<BookSummary> findByCategory(String category);
}

// Class-based projection (DTO)
public record BookDTO(String title, BigDecimal price) {}

@Query("SELECT new com.example.BookDTO(b.title, b.price) FROM Book b")
List<BookDTO> findAllDTOs();
```

---

## Q41: What is @Lazy annotation? When should you use it? (Intermediate)

**Answer:**

**@Lazy** delays bean initialization until first access.

```java
@Service
@Lazy  // Bean created only when first used
public class ExpensiveService {
    public ExpensiveService() {
        // Heavy initialization
    }
}

@Service
public class OrderService {
    @Lazy  // Lazy inject this specific dependency
    @Autowired
    private ReportService reportService;
}
```

**Use cases:**
- Expensive initialization not always needed
- Breaking circular dependencies
- Reducing startup time

---

## Q42: What is the difference between save() and saveAndFlush()? (Beginner)

**Answer:**

| Method | Behavior |
|--------|----------|
| `save()` | Queues for persistence, flushes at transaction end |
| `saveAndFlush()` | Immediately writes to database |

```java
// save() - Batched, more efficient
Book book = bookRepository.save(newBook);
// Changes not yet in DB, but book has generated ID

// saveAndFlush() - Immediate
Book book = bookRepository.saveAndFlush(newBook);
// Changes immediately in DB
// Use when you need DB-generated values immediately
```

---

## Q43: What is @Modifying annotation? (Beginner)

**Answer:**

**@Modifying** marks queries that modify data (UPDATE, DELETE).

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Modifying
    @Query("DELETE FROM User u WHERE u.lastLogin < :date")
    void deleteInactiveUsers(@Param("date") LocalDateTime date);
    
    @Modifying(clearAutomatically = true)  // Clear persistence context after
    @Query("UPDATE User u SET u.verified = true WHERE u.id = :id")
    void verifyUser(@Param("id") Long id);
}
```

---

## Q44: What is Spring WebFlux? How is it different from Spring MVC? (Hard)

**Answer:**

| Aspect | Spring MVC | Spring WebFlux |
|--------|------------|----------------|
| **Model** | Blocking, thread-per-request | Non-blocking, reactive |
| **Threads** | Many (one per request) | Few (event loop) |
| **Scalability** | Limited by threads | High concurrency |
| **Use Case** | Traditional apps | High-load, streaming |

```java
// Spring MVC (Blocking)
@GetMapping("/books/{id}")
public Book getBook(@PathVariable Long id) {
    return bookService.findById(id);  // Blocks thread
}

// Spring WebFlux (Non-blocking)
@GetMapping("/books/{id}")
public Mono<Book> getBook(@PathVariable Long id) {
    return bookService.findById(id);  // Returns immediately
}
```

---

## Q45: What is @EventListener? How do you publish application events? (Intermediate)

**Answer:**

**Application Events** enable loose coupling between components.

```java
// Define event
public class OrderCreatedEvent {
    private final Order order;
    public OrderCreatedEvent(Order order) { this.order = order; }
}

// Publish event
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher publisher;
    
    public Order createOrder(Order order) {
        Order saved = orderRepository.save(order);
        publisher.publishEvent(new OrderCreatedEvent(saved));
        return saved;
    }
}

// Listen to event
@Component
public class NotificationListener {
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        emailService.sendOrderConfirmation(event.getOrder());
    }
    
    @Async
    @EventListener
    public void handleAsync(OrderCreatedEvent event) {
        // Process in background thread
    }
}
```

---

## Q46: What is @Transactional propagation? Explain different types. (Hard)

**Answer:**

**Propagation** defines how transactions interact with each other.

| Type | Description |
|------|-------------|
| **REQUIRED** (default) | Join existing or create new |
| **REQUIRES_NEW** | Always create new (suspend existing) |
| **SUPPORTS** | Use if exists, none otherwise |
| **NOT_SUPPORTED** | Run without transaction |
| **MANDATORY** | Must have existing, else exception |
| **NEVER** | Must NOT have transaction |
| **NESTED** | Nested within existing |

```java
@Transactional
public void outerMethod() {
    // Transaction A starts
    innerMethod();  // Joins Transaction A (REQUIRED)
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void innerMethod() {
    // Suspends A, starts new Transaction B
    // B commits/rolls back independently
}
```

---

## Q47: What is OpenAPI/Swagger? How do you document APIs? (Beginner)

**Answer:**

**OpenAPI** (Swagger) automatically generates API documentation.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

```java
@Operation(summary = "Get book by ID", description = "Returns a single book")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Found the book"),
    @ApiResponse(responseCode = "404", description = "Book not found")
})
@GetMapping("/{id}")
public Book getBook(@Parameter(description = "Book ID") @PathVariable Long id) {
    return bookService.findById(id);
}
```

Access: `http://localhost:8080/swagger-ui.html`

---

## Q48: What is CORS? How do you configure it in Spring? (Beginner)

**Answer:**

**CORS** (Cross-Origin Resource Sharing) allows browsers to make cross-domain requests.

```java
// Method level
@CrossOrigin(origins = "http://localhost:3000")
@GetMapping("/books")
public List<Book> getBooks() { }

// Controller level
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController { }

// Global configuration
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true);
            }
        };
    }
}
```

---

## Q49: What is the difference between monolith and microservices? (Beginner)

**Answer:**

| Aspect | Monolith | Microservices |
|--------|----------|---------------|
| **Deployment** | Single unit | Independent services |
| **Scaling** | Scale entire app | Scale specific services |
| **Tech Stack** | Single | Mixed possible |
| **Complexity** | Simpler initially | Complex infrastructure |
| **Team** | One team | Multiple teams |

```
Monolith:
┌─────────────────────────────────┐
│ User | Book | Order | Payment  │  Single deployable
└─────────────────────────────────┘

Microservices:
┌──────┐ ┌──────┐ ┌───────┐ ┌─────────┐
│ User │ │ Book │ │ Order │ │ Payment │  Independent
└──────┘ └──────┘ └───────┘ └─────────┘
```

---

## Q50: What is Service Discovery? How does Eureka work? (Hard)

**Answer:**

**Service Discovery** allows services to find each other without hardcoded URLs.

```
How Eureka works:
1. Services register with Eureka Server
2. Services fetch registry to find other services
3. Load balancing happens client-side
```

```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication { }

// Client service
@SpringBootApplication
@EnableDiscoveryClient
public class BookServiceApplication { }
```

```yaml
# Client configuration
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

```java
// Call other service by name
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/inventory/{bookId}")
    InventoryResponse getStock(@PathVariable Long bookId);
}
```

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Spring Framework** | Infrastructure for enterprise Java applications |
| **IoC** | Framework controls object creation and lifecycle |
| **DI** | Dependencies injected, not created internally |
| **IoC Container** | Creates, configures, and manages beans |
| **Bean Scopes** | singleton, prototype, request, session |
| **Bean Lifecycle** | @PostConstruct → Bean ready → @PreDestroy |
| **@Autowired** | Auto-inject dependencies by type |
| **Stereotypes** | @Component, @Service, @Repository, @Controller |
| **@Configuration** | Define beans programmatically with @Bean |
| **@Value** | Inject properties into fields |

---

> **Next Topic:** Spring Boot
