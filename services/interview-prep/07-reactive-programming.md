# ⚡ Topic 7: Reactive Programming Basics - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Reactive Programming concepts, Project Reactor, and Spring WebFlux.

---

## Q1: What is Reactive Programming? Why do we need it?

**Answer:**

**Reactive Programming** is a programming paradigm focused on asynchronous data streams and the propagation of change. It allows building non-blocking, event-driven applications.

### The Problem with Traditional Approach:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                 TRADITIONAL (Blocking) vs REACTIVE                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   TRADITIONAL (Thread-per-request):                                      │
│   ────────────────────────────────                                       │
│                                                                          │
│   Request 1 ───▶ Thread 1 ━━━━━[DB Call - 500ms]━━━━━▶ Response         │
│                           (Thread BLOCKED waiting!)                      │
│                                                                          │
│   Request 2 ───▶ Thread 2 ━━━━━[DB Call - 500ms]━━━━━▶ Response         │
│                           (Thread BLOCKED waiting!)                      │
│                                                                          │
│   Request 3 ───▶ Thread 3 ━━━━━[API Call - 1s]━━━━━━━▶ Response         │
│                           (Thread BLOCKED waiting!)                      │
│                                                                          │
│   ⚠️ Problem: 1000 concurrent requests = 1000 threads needed!           │
│              Threads are expensive (memory, context switching)          │
│              Most time spent idle/waiting!                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   REACTIVE (Non-blocking, Event-driven):                                 │
│   ──────────────────────────────────────                                 │
│                                                                          │
│   Request 1 ───▶ Thread 1 ──[Start DB]──▶ (releases thread)            │
│   Request 2 ───▶ Thread 1 ──[Start DB]──▶ (releases thread)            │
│   Request 3 ───▶ Thread 1 ──[Start API]─▶ (releases thread)            │
│                                                                          │
│   ...Thread 1 does other work while waiting...                          │
│                                                                          │
│   [DB Done] ───▶ Thread 2 ──▶ Response 1                                │
│   [DB Done] ───▶ Thread 1 ──▶ Response 2                                │
│   [API Done] ──▶ Thread 2 ──▶ Response 3                                │
│                                                                          │
│   ✅ With just a few threads, handle thousands of requests!             │
│   ✅ Threads never block - always doing useful work                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Concepts:

| Concept | Description |
|---------|-------------|
| **Asynchronous** | Operations don't block the calling thread |
| **Non-blocking** | Thread doesn't wait for result, continues work |
| **Event-driven** | React to events/data as they arrive |
| **Backpressure** | Consumer controls data flow rate |
| **Streams** | Data flows as a sequence of events |

### When to Use Reactive:

✅ **Use Reactive when:**
- High concurrency with many simultaneous connections
- I/O-bound operations (database, network calls)
- Real-time data streams (WebSocket, notifications)
- Microservices with many inter-service calls

❌ **Don't use when:**
- CPU-intensive operations
- Simple CRUD with low concurrency
- Team unfamiliar with reactive concepts
- Debugging simplicity is important

---

## Q2: What is the Reactive Streams Specification?

**Answer:**

**Reactive Streams** is a standard for asynchronous stream processing with non-blocking backpressure. It's a specification with four core interfaces.

### The Four Interfaces:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    REACTIVE STREAMS SPECIFICATION                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. PUBLISHER<T>                                                        │
│   ───────────────                                                        │
│   Produces data, potentially unlimited                                   │
│                                                                          │
│   public interface Publisher<T> {                                        │
│       void subscribe(Subscriber<? super T> subscriber);                  │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. SUBSCRIBER<T>                                                       │
│   ────────────────                                                       │
│   Consumes data from Publisher                                          │
│                                                                          │
│   public interface Subscriber<T> {                                       │
│       void onSubscribe(Subscription s);  // Called first               │
│       void onNext(T item);               // Receive each item          │
│       void onError(Throwable t);         // Error occurred             │
│       void onComplete();                 // Stream finished            │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. SUBSCRIPTION                                                        │
│   ───────────────                                                        │
│   Link between Publisher and Subscriber (enables backpressure)          │
│                                                                          │
│   public interface Subscription {                                        │
│       void request(long n);   // Request n items (backpressure!)       │
│       void cancel();          // Cancel subscription                    │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   4. PROCESSOR<T,R>                                                      │
│   ─────────────────                                                      │
│   Both Publisher AND Subscriber (transformation stage)                  │
│                                                                          │
│   public interface Processor<T,R> extends Subscriber<T>, Publisher<R> { │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Data Flow:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    REACTIVE STREAMS FLOW                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   PUBLISHER                                    SUBSCRIBER                │
│   ┌──────────┐                                ┌──────────┐              │
│   │          │ ────── subscribe() ──────────▶ │          │              │
│   │          │                                │          │              │
│   │          │ ◀───── onSubscribe(sub) ────── │          │              │
│   │   Data   │                                │ Consumer │              │
│   │  Source  │ ◀───── request(10) ────────── │          │              │
│   │          │        (backpressure)          │          │              │
│   │          │                                │          │              │
│   │          │ ────── onNext(item1) ────────▶ │          │              │
│   │          │ ────── onNext(item2) ────────▶ │          │              │
│   │          │ ────── ...                     │          │              │
│   │          │ ────── onNext(item10) ───────▶ │          │              │
│   │          │                                │          │              │
│   │          │ ◀───── request(10) ────────── │          │              │
│   │          │                                │          │              │
│   │          │ ────── onComplete() ─────────▶ │          │              │
│   │          │        OR onError(e)           │          │              │
│   └──────────┘                                └──────────┘              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Implementations:
- **Project Reactor** (Spring WebFlux) - Mono, Flux
- **RxJava** - Observable, Flowable
- **Akka Streams**
- **Java 9 Flow API**

---

## Q3: What is Project Reactor? Explain Mono and Flux.

**Answer:**

**Project Reactor** is the reactive library used by Spring WebFlux. It provides two main types: **Mono** and **Flux**.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       MONO vs FLUX                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   MONO<T> - 0 or 1 element                                               │
│   ────────────────────────                                               │
│   Like Optional, but reactive and async                                  │
│                                                                          │
│   Timeline: ────────●───────|                                            │
│                    emit    complete                                      │
│                                                                          │
│   // Creating Mono                                                       │
│   Mono<String> empty = Mono.empty();                                    │
│   Mono<String> hello = Mono.just("Hello");                              │
│   Mono<User> user = Mono.fromCallable(() -> userRepo.findById(1));      │
│   Mono<String> error = Mono.error(new RuntimeException());              │
│                                                                          │
│   // Use cases                                                           │
│   - Single database record                                              │
│   - Single API response                                                 │
│   - Void operations (Mono<Void>)                                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   FLUX<T> - 0 to N elements                                              │
│   ─────────────────────────                                              │
│   Stream of multiple elements                                           │
│                                                                          │
│   Timeline: ●───●───●───●───●───|                                        │
│            emit emit emit emit complete                                  │
│                                                                          │
│   // Creating Flux                                                       │
│   Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5);                     │
│   Flux<String> fromList = Flux.fromIterable(myList);                    │
│   Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));          │
│   Flux<Integer> range = Flux.range(1, 100);                             │
│                                                                          │
│   // Use cases                                                           │
│   - List of records from database                                       │
│   - Stream of events                                                    │
│   - Real-time data feeds                                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common Operations:

```java
// TRANSFORMATION
Flux<String> names = Flux.just("john", "jane", "bob");

Flux<String> upperNames = names
    .map(String::toUpperCase);  // JOHN, JANE, BOB

Flux<Integer> lengths = names
    .map(String::length);  // 4, 4, 3

// FILTERING
Flux<String> filtered = names
    .filter(name -> name.startsWith("j"));  // john, jane

// FLATMAP (async operations)
Flux<User> users = names
    .flatMap(name -> userService.findByName(name));  // Async lookup

// COMBINING
Mono<String> first = Mono.just("Hello");
Mono<String> second = Mono.just("World");

Mono<String> combined = Mono.zip(first, second, 
    (a, b) -> a + " " + b);  // "Hello World"

// ERROR HANDLING
Mono<User> user = userService.findById(id)
    .onErrorReturn(new User("default"))  // Fallback value
    .onErrorResume(e -> Mono.just(new User("fallback")));  // Fallback Mono

// SUBSCRIBING (triggers execution)
names.subscribe(
    name -> System.out.println("Received: " + name),  // onNext
    error -> System.err.println("Error: " + error),   // onError
    () -> System.out.println("Completed!")            // onComplete
);
```

### Key Point - Nothing Happens Until Subscribe:

```java
// This does NOTHING - not executed!
Flux.just(1, 2, 3)
    .map(i -> {
        System.out.println("Processing " + i);
        return i * 2;
    });

// This EXECUTES - subscribe triggers the pipeline
Flux.just(1, 2, 3)
    .map(i -> {
        System.out.println("Processing " + i);
        return i * 2;
    })
    .subscribe(result -> System.out.println("Result: " + result));
```

---

## Q4: What is Backpressure? How does Reactor handle it?

**Answer:**

**Backpressure** is a mechanism where the consumer controls how much data it can handle, preventing it from being overwhelmed by a fast producer.

### The Problem:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    BACKPRESSURE PROBLEM                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WITHOUT BACKPRESSURE:                                                  │
│   ─────────────────────                                                  │
│                                                                          │
│   Producer (Fast)              Consumer (Slow)                          │
│   ┌──────────────┐             ┌──────────────┐                         │
│   │ 1000 items/s │ ═════════▶  │  10 items/s  │                         │
│   └──────────────┘             └──────────────┘                         │
│                                      │                                   │
│                                      ▼                                   │
│                               ┌──────────────┐                          │
│                               │    BUFFER    │  ← Grows infinitely!    │
│                               │   OVERFLOW   │  ← OutOfMemoryError!    │
│                               │      💥       │                          │
│                               └──────────────┘                          │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   WITH BACKPRESSURE:                                                     │
│   ──────────────────                                                     │
│                                                                          │
│   Producer                     Consumer                                  │
│   ┌──────────────┐             ┌──────────────┐                         │
│   │              │ ◀─request(5)│              │                         │
│   │              │             │              │                         │
│   │   Wait...    │ ─5 items──▶ │  Process 5   │                         │
│   │              │             │              │                         │
│   │              │ ◀─request(5)│              │                         │
│   │              │             │              │                         │
│   │   Continue   │ ─5 items──▶ │  Process 5   │                         │
│   └──────────────┘             └──────────────┘                         │
│                                                                          │
│   ✅ Consumer controls the flow rate!                                   │
│   ✅ No memory overflow                                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Backpressure Strategies in Reactor:

```java
// 1. BUFFER - Store excess items (default, can cause OOM)
Flux.range(1, 1000)
    .onBackpressureBuffer(100)  // Buffer up to 100 items
    .subscribe();

// 2. DROP - Drop items that can't be processed
Flux.range(1, 1000)
    .onBackpressureDrop(dropped -> log.warn("Dropped: " + dropped))
    .subscribe();

// 3. LATEST - Keep only the latest item
Flux.range(1, 1000)
    .onBackpressureLatest()
    .subscribe();

// 4. ERROR - Signal error when overwhelmed
Flux.range(1, 1000)
    .onBackpressureError()
    .subscribe();

// 5. Request-based control
Flux.range(1, 100)
    .subscribe(new BaseSubscriber<Integer>() {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            request(10);  // Request only 10 items
        }
        
        @Override
        protected void hookOnNext(Integer value) {
            process(value);
            request(1);  // Request next item when ready
        }
    });
```

### Operators That Help:

```java
// limitRate - Prefetch in smaller batches
Flux.range(1, 1000)
    .limitRate(100)  // Request 100 at a time
    .subscribe();

// buffer - Group items into batches
Flux.range(1, 1000)
    .buffer(100)  // Emit List<Integer> of 100 items
    .subscribe(batch -> processBatch(batch));

// window - Similar but with Flux<Flux<T>>
Flux.range(1, 1000)
    .window(100)
    .flatMap(window -> processWindow(window));

// sample - Take periodic samples
Flux.interval(Duration.ofMillis(10))
    .sample(Duration.ofSeconds(1))  // One per second
    .subscribe();
```

---

## Q5: What is Spring WebFlux? How is it different from Spring MVC?

**Answer:**

**Spring WebFlux** is the reactive web framework in Spring, designed for non-blocking, asynchronous applications.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SPRING MVC vs SPRING WEBFLUX                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SPRING MVC (Servlet-based):                                            │
│   ───────────────────────────                                            │
│                                                                          │
│   ┌────────────┐    ┌────────────────────────────────────────────────┐  │
│   │   Request  │───▶│              Servlet Container                 │  │
│   │            │    │  ┌────────────────────────────────────────────┐│  │
│   │            │    │  │  Thread Pool (200 threads)                 ││  │
│   │            │    │  │                                            ││  │
│   │            │    │  │  Thread 1 ━━━━[Blocking I/O]━━━━▶ Response ││  │
│   │            │    │  │  Thread 2 ━━━━[Blocking I/O]━━━━▶ Response ││  │
│   │            │    │  │  Thread N ...                              ││  │
│   │            │    │  └────────────────────────────────────────────┘│  │
│   └────────────┘    └────────────────────────────────────────────────┘  │
│                                                                          │
│   ✅ Simpler programming model                                           │
│   ✅ Better debugging (stack traces)                                     │
│   ❌ Limited scalability (threads = connections)                         │
│   ❌ Threads block during I/O                                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   SPRING WEBFLUX (Reactive):                                             │
│   ──────────────────────────                                             │
│                                                                          │
│   ┌────────────┐    ┌────────────────────────────────────────────────┐  │
│   │   Request  │───▶│              Netty/Undertow                    │  │
│   │            │    │  ┌────────────────────────────────────────────┐│  │
│   │            │    │  │  Event Loop (few threads, e.g., 4)        ││  │
│   │            │    │  │                                            ││  │
│   │            │    │  │  Thread 1: Handle many requests            ││  │
│   │            │    │  │            non-blocking style              ││  │
│   │            │    │  │                                            ││  │
│   │            │    │  │  [Req1]──[Req2]──[Req3]──[Req1 I/O done]  ││  │
│   │            │    │  └────────────────────────────────────────────┘│  │
│   └────────────┘    └────────────────────────────────────────────────┘  │
│                                                                          │
│   ✅ High scalability (thousands of connections)                         │
│   ✅ Efficient resource usage                                            │
│   ❌ Steeper learning curve                                              │
│   ❌ Harder to debug                                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Code Comparison:

```java
// SPRING MVC (Blocking)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id);  // Blocks thread!
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();  // Blocks thread!
    }
}

// SPRING WEBFLUX (Non-blocking)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id);  // Non-blocking!
    }
    
    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll();  // Non-blocking stream!
    }
}
```

### Comparison Table:

| Aspect | Spring MVC | Spring WebFlux |
|--------|------------|----------------|
| **Programming Model** | Synchronous | Asynchronous/Reactive |
| **Server** | Servlet (Tomcat, Jetty) | Netty, Undertow, Servlet 3.1+ |
| **Threading** | Thread-per-request | Event loop |
| **Return Types** | Objects, ResponseEntity | Mono, Flux |
| **Blocking I/O** | Supported | ❌ Avoid! Defeats purpose |
| **JDBC** | ✅ Supported | ❌ Use R2DBC |
| **Scalability** | Moderate | High |
| **Complexity** | Lower | Higher |

---

## Q6: Explain common Reactor operators with examples.

**Answer:**

### Transformation Operators:

```java
// MAP - Transform each element
Flux.just(1, 2, 3)
    .map(i -> i * 2)  // 2, 4, 6
    .subscribe(System.out::println);

// FLATMAP - Transform to Publisher and flatten
Flux.just("user1", "user2")
    .flatMap(userId -> userService.findById(userId))  // Async lookup
    .subscribe(System.out::println);

// FLATMAP with concurrency limit
Flux.range(1, 100)
    .flatMap(i -> expensiveOperation(i), 10)  // Max 10 concurrent
    .subscribe();

// CONCATMAP - Like flatMap but preserves order
Flux.just("a", "b", "c")
    .concatMap(s -> Mono.just(s).delayElement(Duration.ofMillis(100)))
    .subscribe(System.out::println);  // a, b, c (ordered)

// SWITCHMAP - Cancel previous, use latest
searchInput
    .switchMap(query -> searchService.search(query))  // Only latest search
    .subscribe(results -> updateUI(results));
```

### Filtering Operators:

```java
// FILTER
Flux.range(1, 10)
    .filter(i -> i % 2 == 0)  // 2, 4, 6, 8, 10
    .subscribe(System.out::println);

// TAKE - First N elements
Flux.range(1, 100)
    .take(5)  // 1, 2, 3, 4, 5
    .subscribe(System.out::println);

// SKIP - Skip first N
Flux.range(1, 10)
    .skip(5)  // 6, 7, 8, 9, 10
    .subscribe(System.out::println);

// DISTINCT
Flux.just(1, 2, 2, 3, 3, 3)
    .distinct()  // 1, 2, 3
    .subscribe(System.out::println);

// TAKWHILE / SKIPWHILE
Flux.range(1, 10)
    .takeWhile(i -> i < 5)  // 1, 2, 3, 4
    .subscribe(System.out::println);
```

### Combining Operators:

```java
// MERGE - Interleave multiple sources
Flux<String> source1 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
Flux<String> source2 = Flux.just("1", "2", "3").delayElements(Duration.ofMillis(150));

Flux.merge(source1, source2)
    .subscribe(System.out::println);  // Interleaved: A, 1, B, 2, C, 3

// CONCAT - Ordered, one after another
Flux.concat(source1, source2)
    .subscribe(System.out::println);  // A, B, C, 1, 2, 3

// ZIP - Combine pairwise
Flux.zip(
    Flux.just("John", "Jane"),
    Flux.just(25, 30),
    (name, age) -> name + " is " + age
).subscribe(System.out::println);  // "John is 25", "Jane is 30"

// COMBINELATEST - Combine with latest from each
Flux.combineLatest(
    priceStream,
    quantityStream,
    (price, qty) -> price * qty
).subscribe(System.out::println);
```

### Error Handling:

```java
// ONERRORRETURN - Fallback value
userService.findById(id)
    .onErrorReturn(new User("default"))
    .subscribe(System.out::println);

// ONERRORRESUME - Fallback publisher
userService.findById(id)
    .onErrorResume(e -> {
        log.error("Error: {}", e.getMessage());
        return Mono.just(new User("fallback"));
    })
    .subscribe(System.out::println);

// RETRY - Retry on error
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .retry(3)  // Retry 3 times
    .subscribe();

// RETRYWHEN - Retry with backoff
.retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
```

---

## Q7: How do you test Reactive code?

**Answer:**

Project Reactor provides **StepVerifier** for testing reactive streams.

### StepVerifier Basics:

```java
@Test
void testFlux() {
    Flux<String> source = Flux.just("Hello", "World");
    
    StepVerifier.create(source)
        .expectNext("Hello")
        .expectNext("World")
        .verifyComplete();  // Verify completes without error
}

@Test
void testMono() {
    Mono<User> userMono = userService.findById(1L);
    
    StepVerifier.create(userMono)
        .assertNext(user -> {
            assertThat(user.getName()).isEqualTo("John");
            assertThat(user.getId()).isEqualTo(1L);
        })
        .verifyComplete();
}

@Test
void testError() {
    Mono<User> errorMono = userService.findById(-1L);
    
    StepVerifier.create(errorMono)
        .expectError(UserNotFoundException.class)
        .verify();
}
```

### Testing with Virtual Time:

```java
@Test
void testWithDelay() {
    // Without virtual time - would actually wait 1 hour!
    Flux<Long> longDelay = Flux.interval(Duration.ofMinutes(1))
        .take(60);  // 60 minutes
    
    // With virtual time - instant!
    StepVerifier.withVirtualTime(() -> longDelay)
        .thenAwait(Duration.ofHours(1))
        .expectNextCount(60)
        .verifyComplete();
}
```

### Testing WebFlux Controllers:

```java
@WebFluxTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private WebTestClient webClient;
    
    @MockBean
    private UserService userService;
    
    @Test
    void getUser_shouldReturnUser() {
        when(userService.findById(1L))
            .thenReturn(Mono.just(new User(1L, "John")));
        
        webClient.get()
            .uri("/api/users/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(User.class)
            .isEqualTo(new User(1L, "John"));
    }
    
    @Test
    void getAllUsers_shouldReturnFlux() {
        when(userService.findAll())
            .thenReturn(Flux.just(
                new User(1L, "John"),
                new User(2L, "Jane")
            ));
        
        webClient.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)
            .hasSize(2);
    }
}
```

---

## Q8: What is WebClient? How is it different from RestTemplate?

**Answer:**

**WebClient** is the reactive, non-blocking HTTP client in Spring WebFlux, replacing the blocking **RestTemplate**.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    RestTemplate vs WebClient                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   RestTemplate (BLOCKING):                                               │
│   ────────────────────────                                               │
│   Thread 1: ━━━━━━[HTTP Call]━━━━━━▶ Response                           │
│             (Thread waits/blocked)                                       │
│                                                                          │
│   ⚠️ Deprecated in Spring 5+                                            │
│   ✅ Simple to use                                                       │
│   ❌ Blocks thread during I/O                                            │
│   ❌ Not suitable for reactive apps                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   WebClient (NON-BLOCKING):                                              │
│   ─────────────────────────                                              │
│   Thread 1: ──[Start HTTP]──▶ (free for other work)                     │
│   ...later...                                                            │
│   Thread 2: ◀──[Response arrives]──                                     │
│                                                                          │
│   ✅ Non-blocking                                                        │
│   ✅ Supports reactive streams                                           │
│   ✅ Fluent API                                                          │
│   ✅ Works with both reactive and blocking code                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### WebClient Usage:

```java
// Creating WebClient
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl("https://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}

// GET Request
Mono<User> user = webClient.get()
    .uri("/users/{id}", userId)
    .retrieve()
    .bodyToMono(User.class);

Flux<User> users = webClient.get()
    .uri("/users")
    .retrieve()
    .bodyToFlux(User.class);

// POST Request
Mono<User> created = webClient.post()
    .uri("/users")
    .bodyValue(newUser)
    .retrieve()
    .bodyToMono(User.class);

// With error handling
Mono<User> user = webClient.get()
    .uri("/users/{id}", userId)
    .retrieve()
    .onStatus(HttpStatus::is4xxClientError, 
        response -> Mono.error(new UserNotFoundException()))
    .onStatus(HttpStatus::is5xxServerError,
        response -> Mono.error(new ServerException()))
    .bodyToMono(User.class);

// Using exchange for full response
Mono<ResponseEntity<User>> response = webClient.get()
    .uri("/users/{id}", userId)
    .exchangeToMono(response -> {
        if (response.statusCode().equals(HttpStatus.OK)) {
            return response.toEntity(User.class);
        } else {
            return response.createException().flatMap(Mono::error);
        }
    });
```

### Using WebClient in Blocking Code:

```java
// If you must block (not recommended in reactive apps)
User user = webClient.get()
    .uri("/users/{id}", userId)
    .retrieve()
    .bodyToMono(User.class)
    .block();  // Blocks until response received

// With timeout
User user = webClient.get()
    .uri("/users/{id}", userId)
    .retrieve()
    .bodyToMono(User.class)
    .block(Duration.ofSeconds(5));
```

---

## Q9: What is R2DBC? How is it different from JDBC?

**Answer:**

**R2DBC** (Reactive Relational Database Connectivity) provides reactive, non-blocking database access for relational databases.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       JDBC vs R2DBC                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   JDBC (Blocking):                                                       │
│   ────────────────                                                       │
│   Thread 1: ━━━━[Execute Query]━━━━━━━━━━━━━━━━▶ ResultSet              │
│             (Thread BLOCKED waiting for DB response)                     │
│                                                                          │
│   Connection conn = dataSource.getConnection();  // Blocks              │
│   PreparedStatement stmt = conn.prepareStatement(sql);                  │
│   ResultSet rs = stmt.executeQuery();  // Blocks                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   R2DBC (Non-blocking):                                                  │
│   ─────────────────────                                                  │
│   Thread 1: ──[Send Query]──▶ (free for other work)                     │
│   ...later...                                                            │
│   Thread 2: ◀──[Results arrive as stream]──                             │
│                                                                          │
│   Mono<Connection> conn = connectionFactory.create();                   │
│   Flux<User> users = repository.findAll();  // Non-blocking stream     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### R2DBC Setup:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-mysql</artifactId>  <!-- or r2dbc-postgresql -->
</dependency>
```

```properties
# application.properties
spring.r2dbc.url=r2dbc:mysql://localhost:3306/bookstore
spring.r2dbc.username=root
spring.r2dbc.password=password
```

### R2DBC Repository:

```java
// Entity
@Table("users")
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
}

// Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    
    Flux<User> findByName(String name);
    
    Mono<User> findByEmail(String email);
    
    @Query("SELECT * FROM users WHERE department = :dept")
    Flux<User> findByDepartment(String dept);
}

// Service
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public Flux<User> findAll() {
        return userRepository.findAll();
    }
    
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }
    
    public Mono<User> create(User user) {
        return userRepository.save(user);
    }
}
```

### Comparison:

| Aspect | JDBC | R2DBC |
|--------|------|-------|
| **Model** | Blocking | Non-blocking |
| **API** | ResultSet | Publisher (Flux/Mono) |
| **Thread usage** | Thread-per-query | Event loop |
| **Transactions** | ThreadLocal | Reactive context |
| **Maturity** | Mature | Evolving |
| **ORM** | JPA/Hibernate | Spring Data R2DBC |

---

## Q10: What are the challenges with Reactive Programming?

**Answer:**

While powerful, reactive programming comes with significant challenges:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    REACTIVE PROGRAMMING CHALLENGES                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. DEBUGGING IS HARD                                                   │
│   ────────────────────                                                   │
│   Stack traces are hard to follow                                       │
│                                                                          │
│   // Blocking stack trace - clear:                                      │
│   at UserService.findById(UserService.java:25)                          │
│   at UserController.getUser(UserController.java:15)                     │
│                                                                          │
│   // Reactive stack trace - confusing:                                  │
│   at reactor.core.publisher.MonoFlatMap$FlatMapInner.onNext(...)       │
│   at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber...   │
│   at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber...      │
│   ... 50 more lines of Reactor internals ...                            │
│                                                                          │
│   Solution: Use Hooks.onOperatorDebug() in dev (expensive!)            │
│             Or use ReactorDebugAgent                                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. BLOCKING CALLS BREAK EVERYTHING                                    │
│   ──────────────────────────────────                                     │
│   One blocking call in reactive pipeline defeats the purpose            │
│                                                                          │
│   ❌ BAD - Blocks event loop!                                           │
│   Mono.just(userId)                                                     │
│       .map(id -> jdbcRepository.findById(id))  // JDBC blocks!         │
│       .subscribe();                                                     │
│                                                                          │
│   ✅ GOOD - Use reactive alternatives                                   │
│   Mono.just(userId)                                                     │
│       .flatMap(id -> r2dbcRepository.findById(id))  // R2DBC           │
│       .subscribe();                                                     │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. STEEP LEARNING CURVE                                               │
│   ───────────────────────                                                │
│   - Many new concepts (Mono, Flux, operators)                          │
│   - Different mental model                                              │
│   - Team needs training                                                 │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   4. CONTEXT PROPAGATION                                                │
│   ──────────────────────                                                 │
│   ThreadLocal doesn't work (threads change frequently)                  │
│                                                                          │
│   ❌ BAD - ThreadLocal won't work                                       │
│   SecurityContextHolder.getContext()  // Returns wrong context!        │
│                                                                          │
│   ✅ GOOD - Use Reactor Context                                         │
│   Mono.deferContextual(ctx -> {                                         │
│       String userId = ctx.get("userId");                                │
│       return doSomething(userId);                                       │
│   });                                                                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   5. ECOSYSTEM LIMITATIONS                                               │
│   ────────────────────────                                               │
│   - Not all libraries are reactive                                      │
│   - JDBC is blocking (need R2DBC)                                       │
│   - Some cloud SDKs are blocking                                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   6. ERROR HANDLING COMPLEXITY                                           │
│   ────────────────────────────                                           │
│   Errors in async code are harder to manage                             │
│                                                                          │
│   .onErrorReturn()      // Fallback value                               │
│   .onErrorResume()      // Fallback publisher                           │
│   .onErrorMap()         // Transform error                              │
│   .doOnError()          // Side effect on error                         │
│   .retry()              // Retry on error                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Best Practices:

```java
// 1. Never block in reactive pipeline
// ❌ 
.map(id -> blockingCall(id))

// ✅
.flatMap(id -> reactiveCall(id))
.subscribeOn(Schedulers.boundedElastic())  // If must block

// 2. Use subscribeOn for blocking calls
Mono.fromCallable(() -> blockingLegacyService.getData())
    .subscribeOn(Schedulers.boundedElastic())  // Offload to thread pool
    .flatMap(data -> processReactively(data));

// 3. Add context for logging/tracing
Mono.just(request)
    .contextWrite(ctx -> ctx.put("requestId", UUID.randomUUID()))
    .doOnEach(signal -> {
        String requestId = signal.getContextView().get("requestId");
        log.info("Processing request: {}", requestId);
    });

// 4. Handle errors properly
userService.findById(id)
    .onErrorResume(NotFoundException.class, e -> Mono.empty())
    .onErrorResume(e -> {
        log.error("Unexpected error", e);
        return Mono.error(new ServiceException("Failed to get user"));
    });
```

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Reactive Programming** | Non-blocking, event-driven, handles backpressure |
| **Reactive Streams** | Publisher, Subscriber, Subscription, Processor |
| **Mono** | 0 or 1 element (like reactive Optional) |
| **Flux** | 0 to N elements (reactive stream) |
| **Backpressure** | Consumer controls data flow rate |
| **WebFlux** | Reactive web framework, event-loop based |
| **StepVerifier** | Testing tool for reactive streams |
| **WebClient** | Non-blocking HTTP client |
| **R2DBC** | Reactive database connectivity |
| **Challenges** | Debugging, blocking calls, learning curve, context |

---

> **Next Topic:** Git Commands & Concepts

---

## Q11: What is the difference between `map()` and `flatMap()` in Reactor? (Beginner)

**Answer:**

| Aspect | `map()` | `flatMap()` |
|--------|---------|-------------|
| **Returns** | Transformed value `T -> R` | Publisher `T -> Publisher<R>` |
| **Use case** | Synchronous transformation | Asynchronous operations |
| **Flattening** | No | Yes (flattens nested Publishers) |

```java
// map() - synchronous transformation
Flux.just("hello", "world")
    .map(String::toUpperCase)  // Returns String
    .subscribe(System.out::println);  // HELLO, WORLD

// flatMap() - asynchronous operation
Flux.just(1, 2, 3)
    .flatMap(id -> userRepository.findById(id))  // Returns Mono<User>
    .subscribe(System.out::println);
```

---

## Q12: What is a Cold vs Hot Publisher? (Beginner)

**Answer:**

| Type | Behavior | Example |
|------|----------|---------|
| **Cold** | Generates data for each subscriber independently | HTTP request, database query |
| **Hot** | Shares data among all subscribers | Mouse events, stock prices |

```java
// COLD - Each subscriber gets fresh data
Flux<Integer> cold = Flux.range(1, 3);
cold.subscribe(i -> System.out.println("Subscriber 1: " + i));  // 1, 2, 3
cold.subscribe(i -> System.out.println("Subscriber 2: " + i));  // 1, 2, 3

// HOT - Subscribers share the same stream
Sinks.Many<Integer> hotSource = Sinks.many().multicast().onBackpressureBuffer();
Flux<Integer> hot = hotSource.asFlux();

hot.subscribe(i -> System.out.println("Sub 1: " + i));
hotSource.tryEmitNext(1);  // Sub 1 sees this
hot.subscribe(i -> System.out.println("Sub 2: " + i));
hotSource.tryEmitNext(2);  // Both see this
```

---

## Q13: What is `subscribeOn()` vs `publishOn()`? (Intermediate)

**Answer:**

| Operator | Affects | Use Case |
|----------|---------|----------|
| `subscribeOn()` | Entire upstream chain | Offload blocking source |
| `publishOn()` | Downstream operators | Switch threads mid-pipeline |

```java
Flux.range(1, 3)
    .doOnNext(i -> log("Source"))        // Runs on boundedElastic
    .subscribeOn(Schedulers.boundedElastic())
    .map(i -> i * 2)
    .doOnNext(i -> log("After map"))     // Runs on boundedElastic
    .publishOn(Schedulers.parallel())
    .filter(i -> i > 2)
    .doOnNext(i -> log("After filter"))  // Runs on parallel
    .subscribe();
```

---

## Q14: What are Reactor Schedulers? (Beginner)

**Answer:**

Schedulers determine which thread(s) execute reactive operations.

| Scheduler | Threads | Best For |
|-----------|---------|----------|
| `immediate()` | Current thread | Testing |
| `single()` | Single reusable thread | Sequential work |
| `parallel()` | CPU cores count | CPU-bound work |
| `boundedElastic()` | Elastic pool (capped) | Blocking I/O |
| `newParallel(name)` | Custom parallel pool | Custom parallelism |

```java
// Offload blocking call
Mono.fromCallable(() -> blockingService.getData())
    .subscribeOn(Schedulers.boundedElastic())
    .subscribe();
```

---

## Q15: How do you handle errors in Reactive streams? (Beginner)

**Answer:**

```java
// 1. onErrorReturn - Fallback value
Mono.error(new RuntimeException())
    .onErrorReturn("default");

// 2. onErrorResume - Fallback publisher
Mono.error(new RuntimeException())
    .onErrorResume(e -> Mono.just("fallback"));

// 3. onErrorMap - Transform error
Mono.error(new IOException())
    .onErrorMap(e -> new CustomException(e));

// 4. doOnError - Side effect (logging)
Mono.error(new RuntimeException())
    .doOnError(e -> log.error("Error: {}", e.getMessage()))
    .onErrorReturn("default");

// 5. retry - Retry on failure
webClient.get().retrieve()
    .bodyToMono(String.class)
    .retry(3);
```

---

## Q16: What is `Mono.defer()` and when to use it? (Intermediate)

**Answer:**

`Mono.defer()` creates a new publisher for each subscriber, enabling lazy evaluation.

```java
// WITHOUT defer - evaluates immediately at assembly time
Mono<Long> eager = Mono.just(System.currentTimeMillis());
eager.subscribe(t -> System.out.println(t));  // Same value
Thread.sleep(1000);
eager.subscribe(t -> System.out.println(t));  // Same value!

// WITH defer - evaluates at subscription time
Mono<Long> lazy = Mono.defer(() -> Mono.just(System.currentTimeMillis()));
lazy.subscribe(t -> System.out.println(t));   // First timestamp
Thread.sleep(1000);
lazy.subscribe(t -> System.out.println(t));   // Different timestamp!
```

**Use cases:** Conditional logic, fresh data per subscriber, wrapping blocking calls.

---

## Q17: What is `Mono.zip()` and `Flux.zip()`? (Beginner)

**Answer:**

`zip()` combines multiple publishers into a single result, waiting for all to emit.

```java
Mono<String> name = Mono.just("John");
Mono<Integer> age = Mono.just(30);
Mono<String> city = Mono.just("NYC");

// Combine into tuple
Mono<Tuple3<String, Integer, String>> tuple = Mono.zip(name, age, city);

// Combine with combinator function
Mono<String> combined = Mono.zip(name, age, city,
    (n, a, c) -> n + " is " + a + " from " + c);
// "John is 30 from NYC"
```

---

## Q18: What is the difference between `block()` and `subscribe()`? (Beginner)

**Answer:**

| Aspect | `block()` | `subscribe()` |
|--------|-----------|---------------|
| **Behavior** | Waits for result (blocking) | Triggers execution (non-blocking) |
| **Returns** | The actual value | Disposable |
| **Use in WebFlux** | ❌ Avoid | ✅ Preferred |

```java
// block() - Returns actual value, blocks current thread
String result = Mono.just("Hello").block();

// subscribe() - Non-blocking, returns immediately
Mono.just("Hello")
    .subscribe(
        value -> System.out.println(value),
        error -> System.err.println(error),
        () -> System.out.println("Done")
    );
```

⚠️ Never use `block()` in reactive web applications as it defeats the purpose.

---

## Q19: What is `Flux.create()` vs `Flux.generate()`? (Intermediate)

**Answer:**

| Aspect | `create()` | `generate()` |
|--------|-----------|--------------|
| **Emission** | Multiple items at once | One item per request |
| **Backpressure** | Manual handling needed | Built-in |
| **Use case** | Bridge async APIs | Synchronous generation |

```java
// create() - Bridge callback-based API
Flux.create(sink -> {
    eventListener.onData(data -> sink.next(data));
    eventListener.onComplete(() -> sink.complete());
    eventListener.onError(e -> sink.error(e));
});

// generate() - Stateful synchronous generation
Flux.generate(
    () -> 0,  // Initial state
    (state, sink) -> {
        sink.next("Value " + state);
        if (state == 10) sink.complete();
        return state + 1;
    }
);
```

---

## Q20: What is `switchIfEmpty()`? (Beginner)

**Answer:**

`switchIfEmpty()` provides a fallback publisher when the original emits no items.

```java
userRepository.findByEmail(email)
    .switchIfEmpty(Mono.error(new UserNotFoundException(email)));

// With default value
userRepository.findByEmail(email)
    .switchIfEmpty(Mono.just(defaultUser));

// Difference from defaultIfEmpty()
Mono.empty()
    .defaultIfEmpty("default")  // Static fallback value
    .subscribe();

Mono.empty()
    .switchIfEmpty(fetchFromBackup())  // Dynamic fallback publisher
    .subscribe();
```

---

## Q21: Explain `doOnNext()`, `doOnError()`, `doOnComplete()`. (Beginner)

**Answer:**

These are side-effect operators that don't modify the stream.

```java
Flux.just(1, 2, 3)
    .doOnSubscribe(sub -> log.info("Subscribed"))
    .doOnNext(item -> log.info("Processing: {}", item))
    .doOnError(e -> log.error("Error: {}", e.getMessage()))
    .doOnComplete(() -> log.info("Completed"))
    .doFinally(signal -> log.info("Finally: {}", signal))
    .subscribe();

// Output:
// Subscribed
// Processing: 1
// Processing: 2
// Processing: 3
// Completed
// Finally: onComplete
```

---

## Q22: What is `Mono.fromCallable()` vs `Mono.just()`? (Intermediate)

**Answer:**

| Aspect | `Mono.just()` | `Mono.fromCallable()` |
|--------|---------------|----------------------|
| **Evaluation** | Immediate (eager) | Lazy (on subscribe) |
| **Null handling** | Throws NullPointerException | Returns empty Mono |
| **Use case** | Already computed values | Blocking/lazy computation |

```java
// just() - Evaluated immediately
Mono.just(expensiveComputation());  // Runs NOW, even if never subscribed

// fromCallable() - Evaluated on subscribe
Mono.fromCallable(() -> expensiveComputation());  // Runs only when subscribed

// Null handling
Mono.just(null);  // ❌ NullPointerException
Mono.fromCallable(() -> null);  // ✅ Empty Mono
```

---

## Q23: What is `Flux.buffer()` and its use cases? (Intermediate)

**Answer:**

`buffer()` collects items into batches (Lists).

```java
// Buffer by count
Flux.range(1, 10)
    .buffer(3)  // [1,2,3], [4,5,6], [7,8,9], [10]
    .subscribe(System.out::println);

// Buffer by time
Flux.interval(Duration.ofMillis(100))
    .buffer(Duration.ofMillis(500))  // Collect 5 items every 500ms
    .subscribe(System.out::println);

// Buffer with skip (sliding window)
Flux.range(1, 5)
    .buffer(3, 1)  // [1,2,3], [2,3,4], [3,4,5], [4,5], [5]
    .subscribe(System.out::println);
```

**Use cases:** Batch processing, rate limiting, aggregation.

---

## Q24: What is `Mono.cache()` and when to use it? (Intermediate)

**Answer:**

`cache()` memoizes the result for reuse by multiple subscribers.

```java
// Without cache - HTTP call made for each subscriber
Mono<String> apiCall = webClient.get().uri("/data").retrieve().bodyToMono(String.class);
apiCall.subscribe();  // HTTP call #1
apiCall.subscribe();  // HTTP call #2

// With cache - Single HTTP call, result shared
Mono<String> cached = webClient.get().uri("/data").retrieve()
    .bodyToMono(String.class)
    .cache();
cached.subscribe();  // HTTP call made
cached.subscribe();  // Uses cached result

// Cache with TTL
Mono<String> timedCache = expensiveCall()
    .cache(Duration.ofMinutes(5));
```

---

## Q25: What is the Reactor Context and how to use it? (Intermediate)

**Answer:**

Reactor Context replaces ThreadLocal for passing data through reactive pipelines.

```java
// Writing context
Mono.just("data")
    .contextWrite(Context.of("userId", "user123"))
    .contextWrite(ctx -> ctx.put("requestId", UUID.randomUUID().toString()));

// Reading context
Mono.deferContextual(ctx -> {
    String userId = ctx.get("userId");
    String requestId = ctx.get("requestId");
    return processWithContext(userId, requestId);
});

// Practical example - MDC for logging
Mono.just(request)
    .doOnEach(signal -> {
        if (!signal.isOnComplete()) {
            String requestId = signal.getContextView().getOrDefault("requestId", "N/A");
            MDC.put("requestId", requestId);
        }
    })
    .contextWrite(Context.of("requestId", UUID.randomUUID().toString()));
```

---

## Q26: What is `Flux.window()` vs `Flux.buffer()`? (Intermediate)

**Answer:**

| Aspect | `buffer()` | `window()` |
|--------|-----------|-----------|
| **Returns** | `Flux<List<T>>` | `Flux<Flux<T>>` |
| **Memory** | Holds all items | Streams items |
| **Use case** | Small batches | Large streams |

```java
// buffer() - Returns lists
Flux.range(1, 6)
    .buffer(3)
    .subscribe(list -> System.out.println(list));  // [1,2,3], [4,5,6]

// window() - Returns Flux of Flux
Flux.range(1, 6)
    .window(3)
    .flatMap(window -> window.collectList())
    .subscribe(list -> System.out.println(list));  // [1,2,3], [4,5,6]
```

---

## Q27: How do you handle timeouts in Reactive streams? (Beginner)

**Answer:**

```java
// timeout() - Error after duration
webClient.get().uri("/slow-api")
    .retrieve()
    .bodyToMono(String.class)
    .timeout(Duration.ofSeconds(5))  // TimeoutException after 5s
    .onErrorReturn("Timeout fallback");

// timeout() with fallback publisher
Mono.delay(Duration.ofSeconds(10))
    .timeout(Duration.ofSeconds(2), Mono.just("Fallback value"));

// Combining with retry
webClient.get().uri("/api")
    .retrieve()
    .bodyToMono(String.class)
    .timeout(Duration.ofSeconds(3))
    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)));
```

---

## Q28: What is `Flux.merge()` vs `Flux.concat()`? (Beginner)

**Answer:**

| Aspect | `merge()` | `concat()` |
|--------|----------|-----------|
| **Order** | Interleaved (first-come) | Sequential |
| **Subscription** | All at once | One at a time |
| **Use case** | Parallel processing | Ordered processing |

```java
Flux<String> fast = Flux.just("A", "B").delayElements(Duration.ofMillis(100));
Flux<String> slow = Flux.just("1", "2").delayElements(Duration.ofMillis(150));

// merge() - Interleaved
Flux.merge(fast, slow).subscribe(System.out::print);  // A1B2 (order varies)

// concat() - Sequential
Flux.concat(fast, slow).subscribe(System.out::print);  // AB12
```

---

## Q29: What is ServerSentEvents (SSE) in WebFlux? (Intermediate)

**Answer:**

SSE allows servers to push events to clients over HTTP.

```java
@RestController
public class SSEController {

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(seq -> ServerSentEvent.<String>builder()
                .id(String.valueOf(seq))
                .event("message")
                .data("Event #" + seq)
                .build());
    }
}

// Client side (JavaScript)
// const eventSource = new EventSource('/events');
// eventSource.onmessage = (e) => console.log(e.data);
```

---

## Q30: What is `Sinks` in Reactor? (Intermediate)

**Answer:**

`Sinks` provide a programmatic way to create hot publishers.

```java
// Unicast - Single subscriber
Sinks.One<String> one = Sinks.one();
one.tryEmitValue("Hello");
one.asMono().subscribe(System.out::println);

// Multicast - Multiple subscribers
Sinks.Many<String> many = Sinks.many().multicast().onBackpressureBuffer();
Flux<String> flux = many.asFlux();

flux.subscribe(s -> System.out.println("Sub1: " + s));
flux.subscribe(s -> System.out.println("Sub2: " + s));

many.tryEmitNext("Event 1");  // Both subscribers receive
many.tryEmitNext("Event 2");

// Replay - New subscribers get history
Sinks.Many<String> replay = Sinks.many().replay().all();
```

---

## Q31: What is `retryWhen()` and how to implement exponential backoff? (Intermediate)

**Answer:**

`retryWhen()` provides advanced retry logic with configurable strategies.

```java
// Simple retry with backoff
webClient.get().uri("/api")
    .retrieve()
    .bodyToMono(String.class)
    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
        .maxBackoff(Duration.ofSeconds(10))
        .jitter(0.5));

// Custom retry logic
.retryWhen(Retry.from(companion -> companion
    .zipWith(Flux.range(1, 4), (error, index) -> {
        if (index < 4) {
            return index;
        } else {
            throw Exceptions.propagate(error.failure());
        }
    })
    .flatMap(index -> Mono.delay(Duration.ofSeconds((long) Math.pow(2, index))))
));

// Retry only specific exceptions
.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
    .filter(ex -> ex instanceof WebClientResponseException.ServiceUnavailable));
```

---

## Q32: What is `transform()` vs `transformDeferred()`? (Hard)

**Answer:**

Both allow reusable operator chains, but differ in evaluation timing.

```java
// transform() - Applied once at assembly time
Function<Flux<String>, Flux<String>> addLogging = flux -> flux
    .doOnNext(s -> log.info("Value: {}", s))
    .doOnError(e -> log.error("Error", e));

Flux.just("a", "b")
    .transform(addLogging)
    .subscribe();

// transformDeferred() - Applied per subscriber
AtomicInteger counter = new AtomicInteger();
Function<Flux<String>, Flux<String>> addSubscriberLogging = flux -> {
    int subId = counter.incrementAndGet();
    return flux.doOnNext(s -> log.info("Sub{}: {}", subId, s));
};

Flux<String> source = Flux.just("a", "b")
    .transformDeferred(addSubscriberLogging);

source.subscribe();  // Logs "Sub1: a", "Sub1: b"
source.subscribe();  // Logs "Sub2: a", "Sub2: b"
```

---

## Q33: How do you implement parallel processing in Reactor? (Intermediate)

**Answer:**

```java
// Using parallel() and runOn()
Flux.range(1, 100)
    .parallel(4)  // Split into 4 rails
    .runOn(Schedulers.parallel())  // Run on parallel scheduler
    .map(i -> processItem(i))
    .sequential()  // Merge back to sequential
    .subscribe();

// Using flatMap with concurrency
Flux.range(1, 100)
    .flatMap(
        i -> processAsync(i),
        16  // Max 16 concurrent operations
    )
    .subscribe();

// Using flatMapSequential (preserves order)
Flux.range(1, 100)
    .flatMapSequential(
        i -> processAsync(i),
        16
    )
    .subscribe();
```

---

## Q34: What is `expand()` and `expandDeep()` for recursive operations? (Hard)

**Answer:**

These operators handle tree-like or graph traversal patterns.

```java
// expand() - Breadth-first expansion
Mono.just(1)
    .expand(i -> i < 10 ? Mono.just(i * 2) : Mono.empty())
    .subscribe(System.out::println);  // 1, 2, 4, 8

// expandDeep() - Depth-first expansion
record TreeNode(String name, List<TreeNode> children) {}

Mono.just(rootNode)
    .expandDeep(node -> Flux.fromIterable(node.children()))
    .map(TreeNode::name)
    .subscribe(System.out::println);

// Pagination example
Mono.just(firstPage)
    .expand(page -> page.hasNext() 
        ? fetchNextPage(page.nextCursor()) 
        : Mono.empty())
    .flatMapIterable(Page::items)
    .subscribe();
```

---

## Q35: What is `checkpoint()` for debugging? (Intermediate)

**Answer:**

`checkpoint()` adds assembly-time information to exception stack traces.

```java
Flux.just(1, 2, 0, 3)
    .map(i -> 100 / i)
    .checkpoint("division-operation")
    .map(i -> i * 2)
    .checkpoint("multiplication-operation")
    .subscribe();

// Stack trace includes:
// Assembly trace from producer [checkpoint("division-operation")]
// Error has been observed at the following site(s):
//     |_ checkpoint ⇢ division-operation

// Lighter version (no stack trace capture)
.checkpoint("description", true)  // Includes description only
```

---

## Q36: What is `Mono.using()` for resource management? (Intermediate)

**Answer:**

`using()` ensures proper resource cleanup, similar to try-with-resources.

```java
// Pattern: using(resourceSupplier, sourceSupplier, resourceCleanup)
Mono.using(
    () -> new FileInputStream("data.txt"),  // Create resource
    fis -> Mono.fromCallable(() -> readAll(fis)),  // Use resource
    fis -> {  // Cleanup (always called)
        try { fis.close(); } 
        catch (IOException e) { log.error("Close failed", e); }
    }
);

// With connection pool
Mono.using(
    () -> connectionPool.acquire(),
    conn -> queryDatabase(conn),
    conn -> connectionPool.release(conn)
);
```

---

## Q37: What is `Flux.usingWhen()` for async resource cleanup? (Hard)

**Answer:**

`usingWhen()` handles resources that require async cleanup.

```java
// Pattern: usingWhen(resourceSupplier, resourceClosure, asyncCleanup)
Flux.usingWhen(
    connectionFactory.create(),  // Mono<Connection>
    connection -> connection.createStatement("SELECT * FROM users")
        .execute()
        .flatMap(result -> result.map((row, meta) -> toUser(row))),
    connection -> connection.close(),  // Async cleanup on success
    (connection, err) -> connection.close(),  // Cleanup on error
    connection -> connection.close()  // Cleanup on cancel
);

// Transaction example
Flux.usingWhen(
    Mono.from(connectionFactory.create()),
    conn -> Flux.from(conn.beginTransaction())
        .thenMany(executeQueries(conn)),
    conn -> Mono.from(conn.commitTransaction()).then(Mono.from(conn.close())),
    (conn, err) -> Mono.from(conn.rollbackTransaction()).then(Mono.from(conn.close())),
    conn -> Mono.from(conn.rollbackTransaction()).then(Mono.from(conn.close()))
);
```

---

## Q38: What is `delayElements()` vs `delaySequence()`? (Beginner)

**Answer:**

| Operator | Behavior |
|----------|----------|
| `delayElements()` | Delays each element individually |
| `delaySequence()` | Delays the entire sequence once |

```java
// delayElements() - Each item delayed
Flux.just("A", "B", "C")
    .delayElements(Duration.ofSeconds(1))
    .subscribe(System.out::println);
// A (1s) -> B (1s) -> C

// delaySequence() - Delays start only
Flux.just("A", "B", "C")
    .delaySequence(Duration.ofSeconds(1))
    .subscribe(System.out::println);
// (1s) -> A, B, C (immediate sequence)
```

---

## Q39: What is `groupBy()` and how to use it? (Intermediate)

**Answer:**

`groupBy()` partitions stream into keyed groups.

```java
record Transaction(String type, BigDecimal amount) {}

Flux.just(
    new Transaction("DEPOSIT", new BigDecimal("100")),
    new Transaction("WITHDRAW", new BigDecimal("50")),
    new Transaction("DEPOSIT", new BigDecimal("200"))
)
.groupBy(Transaction::type)
.flatMap(group -> group
    .collectList()
    .map(list -> group.key() + ": " + list))
.subscribe(System.out::println);
// DEPOSIT: [Transaction(...), Transaction(...)]
// WITHDRAW: [Transaction(...)]

// Sum by group
.groupBy(Transaction::type)
.flatMap(group -> group
    .map(Transaction::amount)
    .reduce(BigDecimal.ZERO, BigDecimal::add)
    .map(sum -> group.key() + " total: " + sum))
.subscribe(System.out::println);
```

---

## Q40: What is `firstWithSignal()` vs `firstWithValue()`? (Intermediate)

**Answer:**

| Operator | Behavior |
|----------|----------|
| `firstWithSignal()` | First publisher to emit ANY signal |
| `firstWithValue()` | First publisher to emit a VALUE |

```java
Mono<String> slow = Mono.just("slow").delayElement(Duration.ofSeconds(2));
Mono<String> fast = Mono.just("fast").delayElement(Duration.ofMillis(100));
Mono<String> empty = Mono.empty();

// firstWithSignal - First to emit anything (including complete/error)
Mono.firstWithSignal(slow, fast, empty)
    .subscribe(System.out::println);  // Empty wins (completes first)

// firstWithValue - First to emit actual value
Mono.firstWithValue(slow, fast, empty)
    .subscribe(System.out::println);  // "fast" wins
```

---

## Q41: What are `Hooks` in Reactor? (Hard)

**Answer:**

`Hooks` provide global callbacks for debugging and monitoring.

```java
// Enable debug mode (expensive - dev only!)
Hooks.onOperatorDebug();

// Custom error handling hook
Hooks.onErrorDropped(error -> 
    log.warn("Dropped error: {}", error.getMessage()));

// Operator execution hook
Hooks.onEachOperator(original -> 
    original.doOnSubscribe(sub -> 
        log.debug("Subscribed to: {}", original)));

// Reset all hooks
Hooks.resetOnOperatorDebug();
Hooks.resetOnErrorDropped();
Hooks.resetOnEachOperator();

// Production-safe debugging with ReactorDebugAgent
// Add as JVM agent: -javaagent:reactor-tools.jar
ReactorDebugAgent.init();
```

---

## Q42: What is `ConnectableFlux` and `autoConnect()`? (Intermediate)

**Answer:**

`ConnectableFlux` creates hot publishers from cold sources.

```java
// Create connectable (doesn't start until connect())
ConnectableFlux<Long> connectable = Flux.interval(Duration.ofSeconds(1))
    .publish();

connectable.subscribe(i -> System.out.println("Sub1: " + i));
connectable.subscribe(i -> System.out.println("Sub2: " + i));
connectable.connect();  // Starts emission

// autoConnect() - Starts after N subscribers
Flux<Long> autoConnected = Flux.interval(Duration.ofSeconds(1))
    .publish()
    .autoConnect(2);  // Starts after 2 subscribers

autoConnected.subscribe(i -> System.out.println("Sub1: " + i));
autoConnected.subscribe(i -> System.out.println("Sub2: " + i));  // Starts here

// refCount() - Starts/stops based on subscriber count
Flux<Long> refCounted = Flux.interval(Duration.ofSeconds(1))
    .publish()
    .refCount(1);  // Starts with 1 sub, stops when 0
```

---

## Q43: How do you handle transactions in reactive R2DBC? (Hard)

**Answer:**

```java
@Service
@RequiredArgsConstructor
public class TransactionalService {
    
    private final TransactionalOperator txOperator;
    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    
    // Using @Transactional annotation
    @Transactional
    public Mono<Void> transferFunds(Long fromId, Long toId, BigDecimal amount) {
        return accountRepo.findById(fromId)
            .flatMap(from -> {
                from.debit(amount);
                return accountRepo.save(from);
            })
            .then(accountRepo.findById(toId))
            .flatMap(to -> {
                to.credit(amount);
                return accountRepo.save(to);
            })
            .then();
    }
    
    // Using TransactionalOperator programmatically
    public Mono<User> createUserWithAccount() {
        return txOperator.transactional(
            userRepo.save(new User("John"))
                .flatMap(user -> accountRepo.save(new Account(user.getId()))
                    .thenReturn(user))
        );
    }
}
```

---

## Q44: What is `Mono.fromFuture()` and `Mono.fromCompletionStage()`? (Beginner)

**Answer:**

These bridge CompletableFuture to reactive types.

```java
// fromFuture() - Wraps CompletableFuture
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
Mono<String> mono = Mono.fromFuture(future);

// fromCompletionStage() - Wraps CompletionStage
CompletionStage<String> stage = someAsyncApi.call();
Mono<String> mono2 = Mono.fromCompletionStage(stage);

// Lazy evaluation (deferred)
Mono<String> lazy = Mono.fromFuture(() -> asyncService.call());

// Converting back
Mono.just("Hello")
    .toFuture()  // Returns CompletableFuture<String>
    .thenAccept(System.out::println);
```

---

## Q45: What is `limitRequest()` and `limitRate()`? (Intermediate)

**Answer:**

| Operator | Purpose |
|----------|---------|
| `limitRequest()` | Limits total elements requested |
| `limitRate()` | Controls prefetch/request batch size |

```java
// limitRequest() - Max elements
Flux.range(1, 1000)
    .limitRequest(10)  // Only request 10 elements total
    .subscribe();

// limitRate() - Batch size for backpressure
Flux.range(1, 1000)
    .limitRate(100)  // Request in batches of 100
    .subscribe();

// limitRate() with low tide
Flux.range(1, 1000)
    .limitRate(100, 50)  // Prefetch 100, replenish at 50
    .subscribe();
```

---

## Q46: What is `elapsed()` and `timestamp()`? (Beginner)

**Answer:**

These operators add timing information to elements.

```java
// timestamp() - Adds emission timestamp
Flux.interval(Duration.ofSeconds(1))
    .take(3)
    .timestamp()
    .subscribe(tuple -> 
        System.out.println("Time: " + tuple.getT1() + ", Value: " + tuple.getT2()));

// elapsed() - Time since last element
Flux.interval(Duration.ofMillis(500))
    .take(3)
    .elapsed()
    .subscribe(tuple -> 
        System.out.println("Elapsed: " + tuple.getT1() + "ms, Value: " + tuple.getT2()));

// Output:
// Elapsed: 500ms, Value: 0
// Elapsed: 500ms, Value: 1
// Elapsed: 500ms, Value: 2
```

---

## Q47: What is `materialize()` and `dematerialize()`? (Hard)

**Answer:**

These wrap/unwrap signals as `Signal` objects.

```java
// materialize() - Convert signals to Signal objects
Flux.just(1, 2, 3)
    .materialize()
    .subscribe(signal -> {
        if (signal.isOnNext()) {
            System.out.println("Value: " + signal.get());
        } else if (signal.isOnComplete()) {
            System.out.println("Completed");
        } else if (signal.isOnError()) {
            System.out.println("Error: " + signal.getThrowable());
        }
    });

// dematerialize() - Convert back to normal stream
Flux<Signal<Integer>> signals = Flux.just(
    Signal.next(1),
    Signal.next(2),
    Signal.complete()
);
signals.dematerialize()
    .subscribe(System.out::println);  // 1, 2

// Use case: Error inspection without terminating
Flux.just(1, 2, 3)
    .concatWith(Mono.error(new RuntimeException()))
    .materialize()
    .filter(Signal::isOnNext)
    .dematerialize()
    .subscribe(System.out::println);  // 1, 2, 3 (error ignored)
```

---

## Q48: What is `Mono.whenDelayError()` vs `Mono.when()`? (Intermediate)

**Answer:**

| Method | Error Handling |
|--------|----------------|
| `when()` | Fails fast on first error |
| `whenDelayError()` | Waits for all, then reports errors |

```java
Mono<Void> op1 = Mono.delay(Duration.ofSeconds(1)).then();
Mono<Void> op2 = Mono.error(new RuntimeException("Error 2"));
Mono<Void> op3 = Mono.delay(Duration.ofSeconds(2)).then();

// when() - Stops at first error
Mono.when(op1, op2, op3)
    .subscribe(
        v -> {},
        e -> System.out.println("Error: " + e)  // Immediate
    );

// whenDelayError() - Waits for all, aggregates errors
Mono.whenDelayError(op1, op2, op3)
    .subscribe(
        v -> {},
        e -> System.out.println("Error: " + e)  // After 2 seconds
    );
```

---

## Q49: What is `onErrorContinue()` and its pitfalls? (Hard)

**Answer:**

`onErrorContinue()` drops failing elements and continues processing.

```java
Flux.just(1, 2, 0, 4, 5)
    .map(i -> 100 / i)
    .onErrorContinue((error, value) -> 
        log.warn("Skipped {} due to {}", value, error.getMessage()))
    .subscribe(System.out::println);  // 100, 50, 25, 20 (skips 0)

// ⚠️ PITFALLS:
// 1. Not all operators support it
// 2. Can behave unexpectedly with flatMap
// 3. May mask important errors

// ❌ Doesn't work well with flatMap
Flux.just(1, 2, 0, 4)
    .flatMap(i -> Mono.just(100 / i))  // Error inside flatMap
    .onErrorContinue((e, v) -> log.warn("Skipped"))
    .subscribe();  // May not work as expected!

// ✅ Better approach - handle in flatMap
Flux.just(1, 2, 0, 4)
    .flatMap(i -> Mono.fromCallable(() -> 100 / i)
        .onErrorResume(e -> {
            log.warn("Skipped: {}", i);
            return Mono.empty();
        }))
    .subscribe();
```

---

## Q50: What are best practices for production reactive applications? (Hard)

**Answer:**

```java
// 1. NEVER block in reactive pipeline
// ❌
.map(id -> blockingRepo.findById(id))
// ✅
.flatMap(id -> reactiveRepo.findById(id))
// If must block:
.flatMap(id -> Mono.fromCallable(() -> blockingCall(id))
    .subscribeOn(Schedulers.boundedElastic()))

// 2. Always handle errors
userService.findById(id)
    .onErrorResume(NotFoundException.class, e -> Mono.empty())
    .onErrorResume(e -> {
        log.error("Unexpected error", e);
        return Mono.error(new ServiceException("User fetch failed"));
    });

// 3. Use timeouts
externalApi.call()
    .timeout(Duration.ofSeconds(5))
    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));

// 4. Propagate context for logging/tracing
Mono.just(request)
    .contextWrite(Context.of("traceId", traceId))
    .doOnEach(signal -> {
        if (!signal.isOnComplete()) {
            MDC.put("traceId", signal.getContextView().get("traceId"));
        }
    });

// 5. Test with StepVerifier
StepVerifier.create(service.process())
    .expectNextCount(10)
    .verifyComplete();

// 6. Monitor with metrics
Flux.interval(Duration.ofSeconds(1))
    .name("my-flux")
    .metrics()
    .subscribe();

// 7. Bounded resources
.flatMap(item -> process(item), 10)  // Limit concurrency
.limitRate(100)  // Control prefetch
```
