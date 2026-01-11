# 🚀 Topic 4: Spring Boot - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Spring Boot concepts and features.

---

## Q1: What is Spring Boot? How is it different from Spring Framework?

**Answer:**

**Spring Boot** is an opinionated framework built on top of Spring Framework that simplifies application development by providing auto-configuration, embedded servers, and production-ready features.

### Spring vs Spring Boot:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING vs SPRING BOOT                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SPRING FRAMEWORK (Manual Setup):                                       │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  1. Create Maven/Gradle project                                    ││
│   │  2. Add 10+ dependencies manually                                  ││
│   │  3. Configure web.xml                                              ││
│   │  4. Configure DispatcherServlet                                    ││
│   │  5. Configure DataSource bean                                      ││
│   │  6. Configure EntityManager                                        ││
│   │  7. Configure TransactionManager                                   ││
│   │  8. Configure ViewResolver                                         ││
│   │  9. Deploy to external Tomcat server                               ││
│   │  10. Debug configuration issues...                                 ││
│   │                                                                     ││
│   │  ⏱️ Time: Hours to Days                                            ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   SPRING BOOT (Automatic Setup):                                         │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  1. Go to start.spring.io                                          ││
│   │  2. Select dependencies (Web, JPA, MySQL)                          ││
│   │  3. Download and run!                                              ││
│   │                                                                     ││
│   │  @SpringBootApplication                                             ││
│   │  public class Application {                                         ││
│   │      public static void main(String[] args) {                       ││
│   │          SpringApplication.run(Application.class, args);            ││
│   │      }                                                              ││
│   │  }                                                                  ││
│   │                                                                     ││
│   │  ⏱️ Time: Minutes!                                                  ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Differences:

| Aspect | Spring Framework | Spring Boot |
|--------|-----------------|-------------|
| **Configuration** | Manual XML/Java config | Auto-configuration |
| **Dependencies** | Add each manually | Starter POMs bundle them |
| **Server** | External (Tomcat/JBoss) | Embedded (Tomcat/Jetty) |
| **Deployment** | WAR file | JAR file (java -jar) |
| **Setup Time** | Hours | Minutes |
| **Production Ready** | Manual setup | Built-in (Actuator) |
| **Opinionated** | No defaults | Sensible defaults |

### Core Features of Spring Boot:
1. **Auto-Configuration** - Automatically configures beans based on classpath
2. **Starter Dependencies** - Pre-bundled dependency sets
3. **Embedded Servers** - No external server needed
4. **Actuator** - Production monitoring endpoints
5. **DevTools** - Hot reload for development
6. **Externalized Configuration** - Properties/YAML files

---

## Q2: What is Auto-Configuration? How does it work?

**Answer:**

**Auto-Configuration** automatically configures Spring beans based on the dependencies present in your classpath.

### How It Works:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTO-CONFIGURATION PROCESS                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. YOU ADD DEPENDENCY:                                                 │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  <dependency>                                                       ││
│   │      <groupId>org.springframework.boot</groupId>                    ││
│   │      <artifactId>spring-boot-starter-data-jpa</artifactId>          ││
│   │  </dependency>                                                      ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                   │                                      │
│                                   ▼                                      │
│   2. SPRING BOOT DETECTS CLASSES ON CLASSPATH:                          │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  Classes detected:                                                  ││
│   │  - javax.persistence.EntityManager ✓                               ││
│   │  - org.hibernate.SessionFactory ✓                                  ││
│   │  - spring-data-jpa ✓                                               ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                   │                                      │
│                                   ▼                                      │
│   3. AUTO-CONFIGURATION KICKS IN:                                        │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  @Configuration                                                     ││
│   │  @ConditionalOnClass(EntityManager.class)  // Only if JPA present  ││
│   │  @EnableJpaRepositories                                             ││
│   │  public class JpaRepositoriesAutoConfiguration {                    ││
│   │                                                                     ││
│   │      @Bean                                                          ││
│   │      @ConditionalOnMissingBean  // Only if user hasn't defined     ││
│   │      public EntityManager entityManager() { ... }                   ││
│   │                                                                     ││
│   │      @Bean                                                          ││
│   │      @ConditionalOnMissingBean                                      ││
│   │      public TransactionManager transactionManager() { ... }         ││
│   │  }                                                                  ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                   │                                      │
│                                   ▼                                      │
│   4. RESULT:                                                             │
│   DataSource, EntityManager, TransactionManager all configured!        │
│   You just define application.properties and entities!                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Annotations:

| Annotation | Purpose |
|------------|---------|
| `@ConditionalOnClass` | Configure only if class exists on classpath |
| `@ConditionalOnMissingBean` | Configure only if user hasn't already defined |
| `@ConditionalOnProperty` | Configure based on property value |
| `@ConditionalOnWebApplication` | Configure only in web applications |

### How to See Auto-Configurations:
```properties
# application.properties
debug=true  # Prints AUTO-CONFIGURATION REPORT at startup
```

### Override Auto-Configuration:
```java
// Spring Boot auto-configures DataSource
// But you can override with your own:
@Configuration
public class DatabaseConfig {
    
    @Bean
    @Primary  // My bean takes precedence
    public DataSource customDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://custom-host/db");
        return ds;
    }
}
```

---

## Q3: What are Starter Dependencies? List some common starters.

**Answer:**

**Starter Dependencies** are curated sets of dependencies bundled together for specific functionality. They eliminate dependency management headaches.

### How Starters Work:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       STARTER DEPENDENCIES                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WITHOUT STARTERS (Manual):                                             │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  <!-- For a web app with JPA, you need: -->                        ││
│   │  spring-core                                                        ││
│   │  spring-context                                                     ││
│   │  spring-web                                                         ││
│   │  spring-webmvc                                                      ││
│   │  spring-orm                                                         ││
│   │  spring-data-jpa                                                    ││
│   │  hibernate-core                                                     ││
│   │  hibernate-entitymanager                                            ││
│   │  jackson-databind                                                   ││
│   │  tomcat-embed-core                                                  ││
│   │  validation-api                                                     ││
│   │  ... 20+ more dependencies with version conflicts! 😱               ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│   WITH STARTERS (Simple):                                                │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │  <!-- Just TWO starters! -->                                       ││
│   │  <dependency>                                                       ││
│   │      <groupId>org.springframework.boot</groupId>                    ││
│   │      <artifactId>spring-boot-starter-web</artifactId>               ││
│   │  </dependency>                                                      ││
│   │  <dependency>                                                       ││
│   │      <groupId>org.springframework.boot</groupId>                    ││
│   │      <artifactId>spring-boot-starter-data-jpa</artifactId>          ││
│   │  </dependency>                                                      ││
│   │                                                                     ││
│   │  ✅ All 20+ dependencies included with compatible versions!        ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common Starters:

| Starter | Purpose | Key Dependencies Included |
|---------|---------|--------------------------|
| `spring-boot-starter-web` | Web applications | Spring MVC, Tomcat, Jackson |
| `spring-boot-starter-data-jpa` | JPA + Hibernate | Spring Data JPA, Hibernate, HikariCP |
| `spring-boot-starter-security` | Security | Spring Security, filters |
| `spring-boot-starter-test` | Testing | JUnit, Mockito, AssertJ |
| `spring-boot-starter-actuator` | Monitoring | Health, metrics, info endpoints |
| `spring-boot-starter-validation` | Validation | Hibernate Validator |
| `spring-boot-starter-webflux` | Reactive web | Project Reactor, Netty |
| `spring-boot-starter-cache` | Caching | Spring Cache abstraction |
| `spring-boot-starter-mail` | Email | JavaMail |
| `spring-boot-starter-oauth2-client` | OAuth2 | OAuth2 client support |

### Spring Cloud Starters (for Microservices):

| Starter | Purpose |
|---------|---------|
| `spring-cloud-starter-netflix-eureka-client` | Service discovery client |
| `spring-cloud-starter-netflix-eureka-server` | Service discovery server |
| `spring-cloud-starter-config` | Config client |
| `spring-cloud-starter-gateway` | API Gateway |
| `spring-cloud-starter-openfeign` | Declarative REST client |
| `spring-cloud-starter-circuitbreaker-resilience4j` | Circuit breaker |

---

## Q4: Explain @SpringBootApplication annotation.

**Answer:**

**@SpringBootApplication** is a convenience annotation that combines three essential annotations:

### Annotation Breakdown:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    @SpringBootApplication                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   @SpringBootApplication = 3 Annotations Combined                        │
│                                                                          │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │                                                                     ││
│   │  @SpringBootApplication                                             ││
│   │         │                                                           ││
│   │         ├──▶ @SpringBootConfiguration                              ││
│   │         │         │                                                 ││
│   │         │         └──▶ @Configuration                              ││
│   │         │              (This class can define @Bean methods)        ││
│   │         │                                                           ││
│   │         ├──▶ @EnableAutoConfiguration                              ││
│   │         │         │                                                 ││
│   │         │         └──▶ Enables Spring Boot auto-configuration      ││
│   │         │              (Configures beans based on classpath)        ││
│   │         │                                                           ││
│   │         └──▶ @ComponentScan                                        ││
│   │                   │                                                 ││
│   │                   └──▶ Scans for @Component, @Service, etc.        ││
│   │                        (From this package and sub-packages)         ││
│   │                                                                     ││
│   └────────────────────────────────────────────────────────────────────┘│
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Detailed Explanation:

```java
@SpringBootApplication
public class BookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}

// Is equivalent to:
@Configuration           // Can define @Bean methods
@EnableAutoConfiguration // Enable auto-config
@ComponentScan           // Scan for components
public class BookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
```

### Important: Package Structure

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    COMPONENT SCAN SCOPE                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   com.book.management/                                                   │
│   │                                                                      │
│   ├── BookstoreApplication.java    ← @SpringBootApplication here        │
│   │                                                                      │
│   ├── controller/                  ← ✅ SCANNED (sub-package)           │
│   │   └── BookController.java                                           │
│   │                                                                      │
│   ├── service/                     ← ✅ SCANNED (sub-package)           │
│   │   └── BookService.java                                              │
│   │                                                                      │
│   └── repository/                  ← ✅ SCANNED (sub-package)           │
│       └── BookRepository.java                                           │
│                                                                          │
│   com.other.package/               ← ❌ NOT SCANNED (different root)    │
│   └── SomeClass.java                                                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Customizing:
```java
@SpringBootApplication(
    scanBasePackages = {"com.book.management", "com.other.package"},
    exclude = {DataSourceAutoConfiguration.class}  // Exclude specific auto-config
)
public class BookstoreApplication { }
```

---

## Q5: What are Spring Boot Profiles? How do you use them?

**Answer:**

**Profiles** allow you to configure different settings for different environments (dev, test, prod).

### Profile Usage:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       SPRING BOOT PROFILES                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   PROBLEM: Different settings for different environments                 │
│                                                                          │
│   Development:                Production:                                │
│   - Local MySQL             - Cloud MySQL                               │
│   - Debug logging           - Info logging                              │
│   - Mock email service      - Real email service                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   SOLUTION: Profile-specific property files                              │
│                                                                          │
│   src/main/resources/                                                    │
│   ├── application.properties         ← Default (always loaded)         │
│   ├── application-dev.properties     ← Loaded when profile = dev       │
│   ├── application-test.properties    ← Loaded when profile = test      │
│   └── application-prod.properties    ← Loaded when profile = prod      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example Configuration:

**application.properties (default/common):**
```properties
spring.application.name=bookstore
server.port=8080
```

**application-dev.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_dev
spring.datasource.username=root
spring.datasource.password=root
logging.level.root=DEBUG
spring.jpa.show-sql=true
```

**application-prod.properties:**
```properties
spring.datasource.url=jdbc:mysql://prod-db.example.com:3306/bookstore
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
logging.level.root=INFO
spring.jpa.show-sql=false
```

### Activating Profiles:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ACTIVATING PROFILES                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. In application.properties:                                          │
│      spring.profiles.active=dev                                          │
│                                                                          │
│   2. Command line argument:                                              │
│      java -jar app.jar --spring.profiles.active=prod                    │
│                                                                          │
│   3. Environment variable:                                               │
│      export SPRING_PROFILES_ACTIVE=prod                                 │
│      java -jar app.jar                                                  │
│                                                                          │
│   4. JVM argument:                                                       │
│      java -Dspring.profiles.active=prod -jar app.jar                    │
│                                                                          │
│   5. In tests:                                                           │
│      @ActiveProfiles("test")                                             │
│      class MyTest { }                                                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Profile-Specific Beans:
```java
@Configuration
public class EmailConfig {
    
    @Bean
    @Profile("dev")  // Only created in dev profile
    public EmailService mockEmailService() {
        return new MockEmailService();  // Doesn't send real emails
    }
    
    @Bean
    @Profile("prod")  // Only created in prod profile
    public EmailService realEmailService() {
        return new SmtpEmailService();  // Sends real emails
    }
    
    @Bean
    @Profile("!prod")  // Created in all profiles EXCEPT prod
    public DataSeeder dataSeeder() {
        return new DataSeeder();
    }
}
```

---

## Q6: What is Spring Boot Actuator? What endpoints does it provide?

**Answer:**

**Spring Boot Actuator** provides production-ready features for monitoring and managing your application.

### Enabling Actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Key Endpoints:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ACTUATOR ENDPOINTS                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   HEALTH & INFO:                                                         │
│   ─────────────────                                                      │
│   /actuator/health        → Application health status                   │
│   /actuator/info          → Application information                     │
│                                                                          │
│   METRICS & MONITORING:                                                  │
│   ─────────────────────                                                  │
│   /actuator/metrics       → Application metrics (memory, CPU, etc.)    │
│   /actuator/prometheus    → Metrics in Prometheus format               │
│   /actuator/loggers       → View and modify log levels                 │
│   /actuator/httptrace     → Recent HTTP request/response traces        │
│                                                                          │
│   APPLICATION INSIGHTS:                                                  │
│   ─────────────────────                                                  │
│   /actuator/beans         → All Spring beans in context                │
│   /actuator/env           → Environment properties                     │
│   /actuator/configprops   → @ConfigurationProperties beans             │
│   /actuator/mappings      → All @RequestMapping paths                  │
│   /actuator/conditions    → Auto-configuration conditions              │
│                                                                          │
│   MANAGEMENT:                                                            │
│   ───────────                                                            │
│   /actuator/shutdown      → Gracefully shutdown (POST, disabled)       │
│   /actuator/threaddump    → Thread dump                                │
│   /actuator/heapdump      → Heap dump                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Configuration:

```properties
# application.properties

# Expose all endpoints (default only health and info)
management.endpoints.web.exposure.include=*

# Or expose specific ones
management.endpoints.web.exposure.include=health,info,metrics,prometheus

# Custom path (default is /actuator)
management.endpoints.web.base-path=/manage

# Show full health details
management.endpoint.health.show-details=always

# Enable shutdown endpoint (disabled by default)
management.endpoint.shutdown.enabled=true
```

### Health Endpoint Response:
```json
GET /actuator/health

{
    "status": "UP",
    "components": {
        "db": {
            "status": "UP",
            "details": {
                "database": "MySQL",
                "validationQuery": "isValid()"
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 499963174912,
                "free": 250000000000
            }
        },
        "eureka": {
            "status": "UP",
            "details": {
                "applications": {
                    "USER-SERVICE": 1,
                    "ORDER-SERVICE": 1
                }
            }
        }
    }
}
```

### Custom Health Indicator:
```java
@Component
public class InventoryHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        int lowStockCount = inventoryRepository.countLowStock();
        
        if (lowStockCount > 10) {
            return Health.status("WARNING")
                    .withDetail("lowStockItems", lowStockCount)
                    .withDetail("message", "Many items are low on stock")
                    .build();
        }
        
        return Health.up()
                .withDetail("lowStockItems", lowStockCount)
                .build();
    }
}
```

---

## Q7: How does Spring Boot handle external configuration?

**Answer:**

Spring Boot supports **externalized configuration** through multiple sources with a defined precedence order.

### Configuration Sources (Priority Order):

```
┌─────────────────────────────────────────────────────────────────────────┐
│              CONFIGURATION PRECEDENCE (Highest to Lowest)                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   HIGHEST PRIORITY (Overrides everything below)                          │
│   ▼                                                                      │
│   1. Command line arguments                                              │
│      java -jar app.jar --server.port=9090                               │
│                                                                          │
│   2. SPRING_APPLICATION_JSON                                             │
│      Environment variable with JSON                                      │
│                                                                          │
│   3. ServletConfig/ServletContext parameters                             │
│                                                                          │
│   4. JNDI attributes                                                     │
│                                                                          │
│   5. Java System properties                                              │
│      java -Dserver.port=9090 -jar app.jar                               │
│                                                                          │
│   6. OS Environment variables                                            │
│      export SERVER_PORT=9090                                            │
│                                                                          │
│   7. RandomValuePropertySource (random.*)                                │
│                                                                          │
│   8. Profile-specific properties (outside JAR)                           │
│      config/application-{profile}.properties                            │
│                                                                          │
│   9. Profile-specific properties (inside JAR)                            │
│      application-{profile}.properties                                   │
│                                                                          │
│   10. Application properties (outside JAR)                               │
│       config/application.properties                                     │
│                                                                          │
│   11. Application properties (inside JAR)                                │
│       application.properties                                            │
│                                                                          │
│   12. @PropertySource on @Configuration classes                          │
│                                                                          │
│   13. Default properties (SpringApplication.setDefaultProperties)        │
│   ▼                                                                      │
│   LOWEST PRIORITY                                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Property File Locations:
```
┌─────────────────────────────────────────────────────────────────────────┐
│                    PROPERTY FILE LOCATIONS                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Spring Boot searches these locations (in order):                       │
│                                                                          │
│   1. ./config/application.properties      ← Current dir/config          │
│   2. ./config/*/application.properties    ← Config subdirectories       │
│   3. ./application.properties             ← Current directory           │
│   4. classpath:/config/application.properties                           │
│   5. classpath:/application.properties    ← Inside JAR (default)        │
│                                                                          │
│   Later ones override earlier ones.                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### YAML vs Properties:

```yaml
# application.yml (YAML format)
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost/db
    username: root
    password: secret
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

```properties
# application.properties (Properties format)
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:mysql://localhost/db
spring.datasource.username=root
spring.datasource.password=secret

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Environment Variable Binding:
```
Property Name              → Environment Variable
─────────────────────────────────────────────────
spring.datasource.url     → SPRING_DATASOURCE_URL
server.port               → SERVER_PORT
jwt.secret-key            → JWT_SECRET_KEY or JWT_SECRETKEY
```

---

## Q8: What is Spring Boot DevTools? What features does it provide?

**Answer:**

**Spring Boot DevTools** provides development-time features to improve developer productivity.

### Enabling DevTools:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### Key Features:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       DEVTOOLS FEATURES                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. AUTOMATIC RESTART                                                   │
│   ────────────────────                                                   │
│   - Automatically restarts when files change                            │
│   - Uses two classloaders:                                              │
│     - Base classloader (unchanged libraries)                            │
│     - Restart classloader (your code) ← Only this restarts             │
│   - Much faster than cold restart                                       │
│                                                                          │
│   ┌────────────────────────────────────────────────────────────────┐    │
│   │  You change BookController.java                                │    │
│   │           ↓                                                     │    │
│   │  DevTools detects change                                        │    │
│   │           ↓                                                     │    │
│   │  Restart classloader reloads (2-3 seconds)                     │    │
│   │           ↓                                                     │    │
│   │  Application running with new code!                            │    │
│   └────────────────────────────────────────────────────────────────┘    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. LIVERELOAD                                                          │
│   ────────────                                                           │
│   - Automatically refreshes browser                                      │
│   - Works with LiveReload browser extension                             │
│   - Great for frontend development                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. PROPERTY DEFAULTS                                                   │
│   ────────────────────                                                   │
│   - Disables template caching                                           │
│   - Enables debug logging                                               │
│   - Shows more error details                                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   4. REMOTE DEVELOPMENT                                                  │
│   ─────────────────────                                                  │
│   - Debug applications running on remote servers                        │
│   - Tunnel through HTTP                                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Configuration:

```properties
# application.properties

# Disable auto-restart
spring.devtools.restart.enabled=false

# Exclude paths from triggering restart
spring.devtools.restart.exclude=static/**,public/**

# Add additional paths to watch
spring.devtools.restart.additional-paths=scripts/

# Disable LiveReload
spring.devtools.livereload.enabled=false

# Trigger file (touch this file to restart)
spring.devtools.restart.trigger-file=.reloadtrigger
```

### Important Notes:
- ⚠️ DevTools is **automatically disabled** in production (when running as `java -jar`)
- ⚠️ DevTools is **only for development** - never include in production builds
- `optional=true` ensures it's not transitively included in other projects

---

## Q9: How do you create a REST API in Spring Boot?

**Answer:**

Spring Boot makes creating REST APIs straightforward with annotations and auto-configuration.

### Complete REST Controller Example:

```java
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    // GET all books
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    // GET book by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(
            @PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    // GET with query parameters
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            bookService.search(title, author, page, size));
    }

    // POST - Create new book
    @PostMapping
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody BookCreateDTO createDTO) {
        BookDTO created = bookService.create(createDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // PUT - Full update
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateDTO updateDTO) {
        return ResponseEntity.ok(bookService.update(id, updateDTO));
    }

    // PATCH - Partial update
    @PatchMapping("/{id}")
    public ResponseEntity<BookDTO> partialUpdateBook(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(bookService.partialUpdate(id, updates));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Common Annotations:

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@RestController` | Marks REST controller (includes @ResponseBody) | Class level |
| `@RequestMapping` | Base path for all methods | `/api/v1/books` |
| `@GetMapping` | Handle GET requests | `@GetMapping("/{id}")` |
| `@PostMapping` | Handle POST requests | `@PostMapping` |
| `@PutMapping` | Handle PUT requests | `@PutMapping("/{id}")` |
| `@PatchMapping` | Handle PATCH requests | `@PatchMapping("/{id}")` |
| `@DeleteMapping` | Handle DELETE requests | `@DeleteMapping("/{id}")` |
| `@PathVariable` | Extract from URL path | `/books/{id}` → `id` |
| `@RequestParam` | Extract query parameter | `?title=Java` → `title` |
| `@RequestBody` | Parse request body as object | JSON → DTO |
| `@Valid` | Trigger validation | On DTO |

### Response Codes:
```java
ResponseEntity.ok(data);                    // 200 OK
ResponseEntity.created(location).body(data);// 201 Created
ResponseEntity.noContent().build();         // 204 No Content
ResponseEntity.badRequest().body(error);    // 400 Bad Request
ResponseEntity.notFound().build();          // 404 Not Found
ResponseEntity.status(HttpStatus.CONFLICT); // 409 Conflict
```

---

## Q10: What is the difference between @Controller and @RestController?

**Answer:**

Both handle web requests, but they differ in how they handle responses.

### Comparison:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                 @Controller vs @RestController                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   @Controller (MVC - Returns Views)                                      │
│   ─────────────────────────────────                                      │
│   @Controller                                                            │
│   public class PageController {                                          │
│                                                                          │
│       @GetMapping("/home")                                               │
│       public String homePage(Model model) {                              │
│           model.addAttribute("title", "Welcome");                        │
│           return "home";  // Returns VIEW NAME (home.html)              │
│       }                   // ViewResolver finds and renders template    │
│                                                                          │
│       @GetMapping("/api/data")                                           │
│       @ResponseBody  // Required for returning data, not view           │
│       public Data getData() {                                            │
│           return new Data();  // Returns JSON/XML                       │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   Flow: Controller → View Name → ViewResolver → HTML Template → Client  │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   @RestController (REST API - Returns Data)                              │
│   ─────────────────────────────────────────                              │
│   @RestController  // = @Controller + @ResponseBody                     │
│   public class ApiController {                                           │
│                                                                          │
│       @GetMapping("/users")                                              │
│       public List<User> getUsers() {                                     │
│           return userService.findAll();  // Directly returns JSON       │
│       }                                   // No @ResponseBody needed    │
│                                                                          │
│       @GetMapping("/users/{id}")                                         │
│       public User getUser(@PathVariable Long id) {                       │
│           return userService.findById(id);  // Returns JSON             │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   Flow: Controller → Object → HttpMessageConverter → JSON/XML → Client  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Visual Comparison:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    RESPONSE HANDLING                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   @Controller                                                            │
│   Request ──▶ Method returns "home" ──▶ ViewResolver ──▶ home.html      │
│                                                                          │
│   @Controller + @ResponseBody (or @RestController)                       │
│   Request ──▶ Method returns User ──▶ Jackson ──▶ {"name":"John",...}   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Summary:

| Aspect | @Controller | @RestController |
|--------|-------------|-----------------|
| **Primary Use** | Web pages (MVC) | REST APIs |
| **Returns** | View names | Data objects |
| **@ResponseBody** | Required for data | Included |
| **Content Type** | text/html | application/json |
| **View Resolution** | Yes | No |
| **Template Engine** | Thymeleaf, JSP | Not used |

### When to Use:
- **@Controller** → Server-side rendered pages (Thymeleaf, JSP)
- **@RestController** → REST APIs, JSON/XML responses
- **Mix** → Use @Controller with @ResponseBody on specific methods

---

## Q11: What is an Embedded Server? Which servers does Spring Boot support?

**Answer:**

An **Embedded Server** is a server that is bundled within your application JAR file, eliminating the need for external server installation.

### Supported Servers:

| Server | Default | Use Case |
|--------|---------|----------|
| **Tomcat** | ✅ Yes | General purpose, most common |
| **Jetty** | No | Lightweight, async support |
| **Undertow** | No | High performance, non-blocking |
| **Netty** | Reactive only | WebFlux reactive applications |

### Switching Servers:

```xml
<!-- Exclude Tomcat -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Add Jetty -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

---

## Q12: How do you handle exceptions globally in Spring Boot?

**Answer:**

Use `@RestControllerAdvice` with `@ExceptionHandler` for centralized exception handling.

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ErrorResponse(400, "Validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return new ErrorResponse(500, "Internal server error", null);
    }
}
```

---

## Q13: What is Bean Validation? How do you use it in Spring Boot?

**Answer:**

**Bean Validation** (JSR 380) provides declarative validation using annotations on DTOs and entities.

### Common Annotations:

| Annotation | Purpose |
|------------|---------|
| `@NotNull` | Must not be null |
| `@NotBlank` | Must not be null or empty (strings) |
| `@Size(min, max)` | String/collection size constraints |
| `@Min`, `@Max` | Numeric range constraints |
| `@Email` | Valid email format |
| `@Pattern` | Regex pattern match |
| `@Past`, `@Future` | Date constraints |

### Example:

```java
public class BookCreateDTO {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be 1-200 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    @Email(message = "Invalid email format")
    private String publisherEmail;
}

// Controller usage
@PostMapping
public ResponseEntity<Book> create(@Valid @RequestBody BookCreateDTO dto) {
    // Validation happens automatically
    return ResponseEntity.ok(bookService.create(dto));
}
```

---

## Q14: What is Spring Boot Testing? Explain the testing annotations.

**Answer:**

Spring Boot provides excellent testing support with annotations for different test types.

### Key Annotations:

| Annotation | Purpose | Loads |
|------------|---------|-------|
| `@SpringBootTest` | Full integration test | Entire context |
| `@WebMvcTest` | Controller layer test | Web layer only |
| `@DataJpaTest` | Repository layer test | JPA components |
| `@MockBean` | Mock a bean in context | N/A |
| `@TestConfiguration` | Test-specific config | N/A |

### Examples:

```java
// Integration Test
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void getAllBooks_ShouldReturnBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}

// Unit Test with Mocks
@WebMvcTest(BookController.class)
class BookControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @Test
    void getById_ShouldReturnBook() throws Exception {
        when(bookService.findById(1L)).thenReturn(testBook);
        
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Book"));
    }
}
```

---

## Q15: How do you configure CORS in Spring Boot?

**Answer:**

**CORS (Cross-Origin Resource Sharing)** can be configured at method, controller, or global level.

### Method Level:
```java
@CrossOrigin(origins = "http://localhost:3000")
@GetMapping("/books")
public List<Book> getBooks() { ... }
```

### Controller Level:
```java
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController { ... }
```

### Global Configuration:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "https://myapp.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## Q16: What is the purpose of application.properties vs application.yml?

**Answer:**

Both serve the same purpose—externalizing configuration—but with different formats.

### Comparison:

| Aspect | .properties | .yml |
|--------|-------------|------|
| **Format** | Key-value pairs | Hierarchical YAML |
| **Readability** | Flat structure | Nested structure |
| **Comments** | `#` | `#` |
| **Multi-line** | Escape needed | Native support |
| **Profiles** | Separate files | Can use `---` |

### Example:

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost/db
spring.datasource.username=root
spring.jpa.hibernate.ddl-auto=update
```

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost/db
    username: root
  jpa:
    hibernate:
      ddl-auto: update
```

---

## Q17: How do you implement pagination in Spring Boot?

**Answer:**

Spring Data JPA provides built-in pagination support through `Pageable` and `Page`.

```java
// Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByAuthorContaining(String author, Pageable pageable);
}

// Service
public Page<BookDTO> getAllBooks(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return bookRepository.findAll(pageable)
        .map(this::convertToDTO);
}

// Controller
@GetMapping
public ResponseEntity<Page<BookDTO>> getBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
    return ResponseEntity.ok(bookService.getAllBooks(page, size, sortBy));
}
```

---

## Q18: What is @ConfigurationProperties and how is it different from @Value?

**Answer:**

Both inject properties, but `@ConfigurationProperties` is better for grouped properties.

### @Value (Single Properties):
```java
@Service
public class EmailService {
    @Value("${email.host}")
    private String host;
    
    @Value("${email.port}")
    private int port;
}
```

### @ConfigurationProperties (Grouped):
```java
@ConfigurationProperties(prefix = "email")
@Validated
public class EmailProperties {
    @NotBlank
    private String host;
    private int port = 587;
    private boolean ssl = true;
    // getters, setters
}

// Enable in main class
@EnableConfigurationProperties(EmailProperties.class)
@SpringBootApplication
public class Application { }
```

| Aspect | @Value | @ConfigurationProperties |
|--------|--------|--------------------------|
| **Use Case** | Single values | Related properties |
| **Type Safety** | Limited | Full |
| **Validation** | Manual | @Validated support |
| **Immutable** | Yes | Can be mutable/immutable |

---

## Q19: What is Spring Boot Caching? How do you enable it?

**Answer:**

Spring Boot provides caching abstraction to cache method return values.

### Enable Caching:
```java
@SpringBootApplication
@EnableCaching
public class Application { }
```

### Using Cache:
```java
@Service
public class BookService {
    
    @Cacheable(value = "books", key = "#id")
    public Book findById(Long id) {
        // Called only if not in cache
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
    public void clearCache() {
        // Clears entire cache
    }
}
```

---

## Q20: What is the difference between WAR and JAR deployment in Spring Boot?

**Answer:**

### JAR Deployment (Recommended):
- Contains embedded server
- Run with `java -jar app.jar`
- Self-contained, portable

### WAR Deployment (Traditional):
- Deployed to external server (Tomcat, JBoss)
- Requires server installation
- Useful for legacy infrastructure

### Creating WAR:
```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```xml
<packaging>war</packaging>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

---

## Q21: What are CommandLineRunner and ApplicationRunner?

**Answer:**

Both execute code after the application starts, useful for initialization tasks.

```java
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        // args = raw command line arguments
        System.out.println("Initializing data...");
    }
}

@Component
@Order(2)
public class CacheWarmer implements ApplicationRunner {
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // args = parsed arguments with options
        if (args.containsOption("warm-cache")) {
            warmCache();
        }
    }
}
```

| Aspect | CommandLineRunner | ApplicationRunner |
|--------|-------------------|-------------------|
| **Arguments** | String[] (raw) | ApplicationArguments (parsed) |
| **Use Case** | Simple scripts | Complex argument handling |

---

## Q22: How do you implement scheduled tasks in Spring Boot?

**Answer:**

Use `@Scheduled` annotation with `@EnableScheduling`.

```java
@Configuration
@EnableScheduling
public class SchedulingConfig { }

@Component
@Slf4j
public class ScheduledTasks {
    
    // Fixed rate: runs every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("Time: {}", LocalDateTime.now());
    }
    
    // Fixed delay: 5 seconds after previous completion
    @Scheduled(fixedDelay = 5000, initialDelay = 1000)
    public void processQueue() {
        // Process items
    }
    
    // Cron expression: every weekday at 9 AM
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void sendDailyReport() {
        // Send report
    }
    
    // Configurable via properties
    @Scheduled(cron = "${app.cleanup.cron}")
    public void cleanup() {
        // Cleanup task
    }
}
```

---

## Q23: What is Spring Boot Logging? How do you configure it?

**Answer:**

Spring Boot uses **SLF4J** with **Logback** as the default logging framework.

### Configuration:
```properties
# application.properties

# Root log level
logging.level.root=INFO

# Package-specific levels
logging.level.com.myapp=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# Log file output
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# Custom pattern
logging.pattern.console=%d{HH:mm:ss} %-5level %logger{36} - %msg%n
```

### Usage:
```java
@Service
@Slf4j  // Lombok annotation
public class BookService {
    
    public Book findById(Long id) {
        log.debug("Finding book with id: {}", id);
        log.info("Book found: {}", book.getTitle());
        log.warn("Low stock for book: {}", id);
        log.error("Failed to find book", exception);
    }
}
```

---

## Q24: What is the purpose of @Transactional in Spring Boot?

**Answer:**

`@Transactional` manages database transactions declaratively, ensuring ACID properties.

```java
@Service
public class OrderService {
    
    @Transactional  // All operations in one transaction
    public Order createOrder(OrderDTO dto) {
        Order order = orderRepository.save(new Order(dto));
        inventoryService.reduceStock(dto.getItems());  // Same transaction
        paymentService.processPayment(dto);            // Same transaction
        return order;
        // If any fails, ALL operations roll back
    }
    
    @Transactional(readOnly = true)  // Optimization for read-only
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Transactional(
        propagation = Propagation.REQUIRES_NEW,  // New transaction
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,  // seconds
        rollbackFor = PaymentException.class
    )
    public void processPayment(Long orderId) { }
}
```

---

## Q25: How do you implement file upload/download in Spring Boot?

**Answer:**

```java
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    // Upload
    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }
        
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, 
            StandardCopyOption.REPLACE_EXISTING);
        
        return ResponseEntity.ok("Uploaded: " + file.getOriginalFilename());
    }
    
    // Download
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) 
            throws IOException {
        
        Path filePath = Paths.get(uploadDir, filename);
        Resource resource = new UrlResource(filePath.toUri());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + filename + "\"")
            .body(resource);
    }
}
```

```properties
# application.properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=./uploads
```

---

## Q26: What is @Async in Spring Boot? (Intermediate)

**Answer:**

`@Async` executes methods asynchronously in a separate thread pool.

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}

@Service
public class NotificationService {
    
    @Async
    public void sendEmailAsync(String to, String message) {
        // Runs in separate thread
        emailSender.send(to, message);
    }
    
    @Async
    public CompletableFuture<Report> generateReportAsync(Long userId) {
        Report report = createReport(userId);
        return CompletableFuture.completedFuture(report);
    }
}
```

---

## Q27: How do you implement internationalization (i18n) in Spring Boot?

**Answer:**

Spring Boot supports internationalization through message bundles and `LocaleResolver`.

### Configuration:
```java
@Configuration
public class I18nConfig implements WebMvcConfigurer {
    
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = 
            new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
```

### Message Files:
```properties
# messages.properties (default)
welcome.message=Welcome!

# messages_es.properties (Spanish)
welcome.message=¡Bienvenido!

# messages_fr.properties (French)
welcome.message=Bienvenue!
```

### Usage:
```java
@RestController
public class GreetingController {
    
    @Autowired
    private MessageSource messageSource;
    
    @GetMapping("/greeting")
    public String greet(@RequestHeader("Accept-Language") Locale locale) {
        return messageSource.getMessage("welcome.message", null, locale);
    }
}
```

---

## Q28: What is Spring Boot Security? How do you secure endpoints?

**Answer:**

Spring Security provides comprehensive security features for Spring Boot applications.

### Basic Configuration:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Q29: What is conditional bean creation in Spring Boot? (Intermediate)

**Answer:**

Spring Boot provides `@Conditional` annotations for conditional bean creation.

| Annotation | Bean Created When |
|------------|-------------------|
| `@ConditionalOnClass` | Class exists on classpath |
| `@ConditionalOnMissingClass` | Class doesn't exist |
| `@ConditionalOnBean` | Specific bean exists |
| `@ConditionalOnMissingBean` | Bean doesn't exist |
| `@ConditionalOnProperty` | Property has specific value |
| `@ConditionalOnExpression` | SpEL expression is true |
| `@ConditionalOnWebApplication` | Is web application |

```java
@Configuration
public class ConditionalConfig {
    
    @Bean
    @ConditionalOnProperty(name = "feature.email.enabled", havingValue = "true")
    public EmailService emailService() {
        return new SmtpEmailService();
    }
    
    @Bean
    @ConditionalOnMissingBean(EmailService.class)
    public EmailService mockEmailService() {
        return new MockEmailService();
    }
}
```

---

## Q30: What is the difference between @Component, @Service, @Repository, and @Controller?

**Answer:**

All are stereotypes that mark classes for component scanning, but with semantic differences:

| Annotation | Layer | Special Features |
|------------|-------|------------------|
| `@Component` | Generic | Base stereotype |
| `@Service` | Business logic | Semantic only |
| `@Repository` | Data access | Exception translation |
| `@Controller` | Web layer | Request mapping |

```java
@Component  // Generic bean
public class EmailValidator { }

@Service  // Business logic
public class OrderService { }

@Repository  // Data access - translates DB exceptions
public class OrderRepository { }

@Controller  // Web request handling
public class OrderController { }
```

---

## Q31: What is Spring AOP? How do you use it in Spring Boot? (Intermediate)

**Answer:**

**Aspect-Oriented Programming (AOP)** allows you to separate cross-cutting concerns (logging, security, transactions) from business logic.

### Key Concepts:

| Term | Description |
|------|-------------|
| **Aspect** | A module containing cross-cutting logic |
| **Advice** | Action taken at a specific join point |
| **Pointcut** | Expression that selects join points |
| **Join Point** | Point in execution (method call) |

### Example - Logging Aspect:

```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    // Before method execution
    @Before("execution(* com.bookstore.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling: {}", joinPoint.getSignature().getName());
    }
    
    // After method returns
    @AfterReturning(
        pointcut = "execution(* com.bookstore.service.*.*(..))",
        returning = "result"
    )
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("Returned: {}", result);
    }
    
    // Around - full control
    @Around("@annotation(com.bookstore.annotation.Timed)")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Execution time: {}ms", duration);
        return result;
    }
}
```

---

## Q32: What is the difference between @RequestParam, @PathVariable, and @RequestBody?

**Answer:**

| Annotation | Source | Use Case |
|------------|--------|----------|
| `@PathVariable` | URL path | `/books/{id}` → Extract `id` |
| `@RequestParam` | Query string | `/books?title=Java` → Extract `title` |
| `@RequestBody` | Request body | JSON payload → Object |

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    // PathVariable: GET /api/books/123
    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) { ... }
    
    // RequestParam: GET /api/books?author=John&page=0
    @GetMapping
    public List<Book> search(
        @RequestParam String author,
        @RequestParam(defaultValue = "0") int page
    ) { ... }
    
    // RequestBody: POST /api/books with JSON body
    @PostMapping
    public Book create(@RequestBody BookDTO dto) { ... }
}
```

---

## Q33: How do you implement circuit breaker pattern in Spring Boot? (Hard)

**Answer:**

Use **Resilience4j** for implementing circuit breaker pattern.

```java
// Dependency
// spring-cloud-starter-circuitbreaker-resilience4j

@Service
public class OrderService {
    
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackInventory")
    @Retry(name = "inventory", fallbackMethod = "fallbackInventory")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<InventoryStatus> checkInventory(Long productId) {
        return CompletableFuture.supplyAsync(() -> 
            inventoryClient.getStatus(productId)
        );
    }
    
    public CompletableFuture<InventoryStatus> fallbackInventory(
            Long productId, Throwable t) {
        log.warn("Fallback for product {}: {}", productId, t.getMessage());
        return CompletableFuture.completedFuture(
            new InventoryStatus(productId, "UNKNOWN", 0)
        );
    }
}
```

### Configuration:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      inventory:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
  retry:
    instances:
      inventory:
        maxAttempts: 3
        waitDuration: 500
```

---

## Q34: What is Spring Data REST? (Intermediate)

**Answer:**

**Spring Data REST** automatically exposes JPA repositories as RESTful endpoints.

```java
// Just define the repository
@RepositoryRestResource(path = "books")
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByAuthor(String author);
}
```

This automatically creates:
- `GET /books` - List all
- `GET /books/{id}` - Get one
- `POST /books` - Create
- `PUT /books/{id}` - Update
- `DELETE /books/{id}` - Delete
- `GET /books/search/findByAuthor?author=John` - Custom query

### Configuration:
```properties
spring.data.rest.base-path=/api
spring.data.rest.default-page-size=20
```

---

## Q35: What is @ControllerAdvice vs @RestControllerAdvice?

**Answer:**

| Annotation | Returns | Use Case |
|------------|---------|----------|
| `@ControllerAdvice` | View names | MVC applications |
| `@RestControllerAdvice` | Response body | REST APIs |

`@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(BookNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }
}
```

---

## Q36: How do you implement custom auto-configuration? (Hard)

**Answer:**

Create your own auto-configuration class:

```java
@Configuration
@ConditionalOnClass(MyService.class)
@EnableConfigurationProperties(MyProperties.class)
public class MyAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyProperties properties) {
        return new MyService(properties.getName(), properties.getTimeout());
    }
}

@ConfigurationProperties(prefix = "my.service")
public class MyProperties {
    private String name = "default";
    private int timeout = 5000;
    // getters, setters
}
```

### Register in META-INF/spring.factories:
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.mylib.MyAutoConfiguration
```

---

## Q37: What is Spring WebFlux? How is it different from Spring MVC? (Hard)

**Answer:**

**Spring WebFlux** is the reactive web framework in Spring.

| Aspect | Spring MVC | Spring WebFlux |
|--------|------------|----------------|
| **Model** | Blocking (thread per request) | Non-blocking (reactive) |
| **Server** | Servlet (Tomcat) | Netty, Undertow |
| **Concurrency** | One thread per request | Event loop |
| **Use Case** | Traditional apps | High concurrency, streaming |

```java
// Spring MVC (Blocking)
@GetMapping("/users")
public List<User> getUsers() {
    return userRepository.findAll();  // Blocks thread
}

// Spring WebFlux (Reactive)
@GetMapping("/users")
public Flux<User> getUsers() {
    return userRepository.findAll();  // Non-blocking
}

@GetMapping("/user/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userRepository.findById(id);
}
```

---

## Q38: What is Spring Boot Admin?

**Answer:**

**Spring Boot Admin** is a UI for managing and monitoring Spring Boot applications.

### Server Setup:
```java
@SpringBootApplication
@EnableAdminServer
public class AdminServerApplication { }
```

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
</dependency>
```

### Client Setup:
```properties
spring.boot.admin.client.url=http://localhost:8080
spring.boot.admin.client.instance.name=book-service
```

Features:
- View application health and info
- Monitor metrics and traces
- View and change log levels
- Notify on status changes

---

## Q39: How do you implement request/response logging? (Intermediate)

**Answer:**

```java
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper requestWrapper = 
            new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = 
            new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        chain.doFilter(requestWrapper, responseWrapper);
        
        long duration = System.currentTimeMillis() - startTime;
        
        log.info("Request: {} {} - Response: {} - {}ms",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            duration
        );
        
        responseWrapper.copyBodyToResponse();
    }
}
```

---

## Q40: What is the difference between @Bean and @Component?

**Answer:**

| Aspect | @Bean | @Component |
|--------|-------|------------|
| **Target** | Method | Class |
| **Location** | @Configuration class | Any class |
| **Control** | Manual instantiation | Auto-detected |
| **Third-party** | Yes, can create beans | No, need source code |

```java
// @Component - on your classes
@Component
public class EmailValidator { }

// @Bean - for third-party or manual creation
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
```

---

## Q41: How do you secure REST APIs with JWT in Spring Boot? (Hard)

**Answer:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        username, null, jwtUtil.extractAuthorities(token));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        chain.doFilter(request, response);
    }
}
```

---

## Q42: What is OpenAPI/Swagger in Spring Boot?

**Answer:**

**SpringDoc** provides OpenAPI 3.0 documentation for Spring Boot REST APIs.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```

### Access Swagger UI:
- UI: `http://localhost:8080/swagger-ui.html`
- JSON: `http://localhost:8080/v3/api-docs`

### Annotations:
```java
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management APIs")
public class BookController {
    
    @Operation(summary = "Get book by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public Book getById(
        @Parameter(description = "Book ID") @PathVariable Long id
    ) { ... }
}
```

---

## Q43: How do you connect to multiple databases in Spring Boot? (Hard)

**Answer:**

Define separate DataSource beans for each database:

```java
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {
    
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}

// Primary EntityManager
@Configuration
@EnableJpaRepositories(
    basePackages = "com.app.primary.repository",
    entityManagerFactoryRef = "primaryEntityManager",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDbConfig { ... }

// Secondary EntityManager
@Configuration
@EnableJpaRepositories(
    basePackages = "com.app.secondary.repository",
    entityManagerFactoryRef = "secondaryEntityManager",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryDbConfig { ... }
```

---

## Q44: What is Spring Cloud Config? (Intermediate)

**Answer:**

**Spring Cloud Config** provides centralized external configuration for distributed applications.

### Config Server:
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication { }
```

```yaml
# application.yml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/repo/config
          default-label: main
```

### Config Client:
```yaml
spring:
  application:
    name: book-service
  config:
    import: optional:configserver:http://localhost:8888
```

Config files in Git repo:
- `book-service.yml` (default)
- `book-service-dev.yml` (dev profile)
- `book-service-prod.yml` (prod profile)

---

## Q45: What is @EnableDiscoveryClient and how does service discovery work?

**Answer:**

`@EnableDiscoveryClient` enables service registration with a service registry (Eureka).

```java
@SpringBootApplication
@EnableDiscoveryClient
public class BookServiceApplication { }
```

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### Using Discovered Services:
```java
@Service
public class OrderService {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    public List<ServiceInstance> getInstances() {
        return discoveryClient.getInstances("book-service");
    }
}

// Or with LoadBalancer
@FeignClient(name = "book-service")
public interface BookClient {
    @GetMapping("/api/books/{id}")
    Book getBook(@PathVariable Long id);
}
```

---

## Q46: What is Spring Boot's default error handling mechanism?

**Answer:**

Spring Boot provides `/error` endpoint and `BasicErrorController` for error handling.

### Default Behavior:
- Returns JSON for REST requests
- Returns HTML error page for browser requests

### Customizing:
```java
@ControllerAdvice
public class CustomErrorController implements ErrorController {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleError(Exception e) {
        return ResponseEntity.status(500)
            .body(new ErrorResponse(500, e.getMessage()));
    }
}
```

### Custom Error Pages:
```
src/main/resources/
├── templates/
│   └── error/
│       ├── 404.html
│       ├── 500.html
│       └── error.html (fallback)
```

---

## Q47: How do you optimize Spring Boot application performance? (Hard)

**Answer:**

### 1. JVM Tuning:
```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC -jar app.jar
```

### 2. Connection Pooling:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 30000
```

### 3. Caching:
```java
@EnableCaching
@Cacheable("books")
public Book findById(Long id) { ... }
```

### 4. Lazy Initialization:
```yaml
spring:
  main:
    lazy-initialization: true
```

### 5. Response Compression:
```yaml
server:
  compression:
    enabled: true
    mime-types: application/json
    min-response-size: 1024
```

### 6. Database Optimization:
```yaml
spring:
  jpa:
    open-in-view: false  # Disable OSIV
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

---

## Q48: What is @Qualifier and when do you use it?

**Answer:**

`@Qualifier` specifies which bean to inject when multiple beans of the same type exist.

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Qualifier("mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://localhost/db")
            .build();
    }
    
    @Bean
    @Qualifier("postgres")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost/db")
            .build();
    }
}

@Service
public class ReportService {
    
    @Autowired
    @Qualifier("mysql")
    private DataSource dataSource;  // Injects MySQL
}
```

---

## Q49: What is the difference between @Autowired, @Resource, and @Inject?

**Answer:**

| Annotation | Package | Matching |
|------------|---------|----------|
| `@Autowired` | Spring | By type first, then name |
| `@Resource` | Jakarta EE | By name first, then type |
| `@Inject` | Jakarta CDI | By type (like @Autowired) |

```java
// @Autowired - Spring specific
@Autowired
private UserService userService;

// @Resource - by name
@Resource(name = "emailService")
private NotificationService notificationService;

// @Inject - standard CDI
@Inject
private OrderService orderService;
```

**Recommendation**: Use `@Autowired` for Spring apps, `@Inject` for portability.

---

## Q50: How do you handle distributed transactions in microservices? (Hard)

**Answer:**

### 1. Saga Pattern (Choreography):
```java
@Service
public class OrderSaga {
    
    @Transactional
    public void createOrder(OrderDTO dto) {
        // 1. Create order (local transaction)
        Order order = orderRepository.save(new Order(dto));
        
        // 2. Publish event
        eventPublisher.publish(new OrderCreatedEvent(order.getId()));
    }
    
    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // Compensate - cancel order
        orderRepository.updateStatus(event.getOrderId(), "CANCELLED");
    }
}
```

### 2. Saga Pattern (Orchestration):
```java
@Service
public class OrderOrchestrator {
    
    public void processOrder(OrderDTO dto) {
        try {
            Order order = orderService.create(dto);
            inventoryService.reserve(dto.getItems());
            paymentService.charge(dto.getPayment());
            orderService.confirm(order.getId());
        } catch (Exception e) {
            // Compensate in reverse order
            paymentService.refund(dto.getPayment());
            inventoryService.release(dto.getItems());
            orderService.cancel(order.getId());
        }
    }
}
```

### 3. Outbox Pattern:
Store events in outbox table, publish asynchronously.

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Spring Boot** | Opinionated, auto-configured Spring |
| **Auto-Configuration** | Configures beans based on classpath |
| **Starters** | Pre-bundled dependency sets |
| **@SpringBootApplication** | @Configuration + @EnableAutoConfiguration + @ComponentScan |
| **Profiles** | Environment-specific configuration |
| **Actuator** | Production monitoring endpoints |
| **External Config** | Properties, YAML, env vars with priority |
| **DevTools** | Hot reload, LiveReload for development |
| **REST APIs** | @RestController, @GetMapping, etc. |
| **@RestController** | @Controller + @ResponseBody |

---

> **Next Topic:** Java 8 Features
