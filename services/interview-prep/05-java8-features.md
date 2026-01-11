# ☕ Topic 5: Java 8 Features - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Java 8 features including Lambda Expressions, Streams, Optional, Functional Interfaces, and more.

---

## Q1: What are the major features introduced in Java 8?

**Answer:**

Java 8, released in March 2014, introduced several game-changing features:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       JAVA 8 MAJOR FEATURES                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. LAMBDA EXPRESSIONS                                                  │
│      Compact way to represent anonymous functions                        │
│      (x, y) -> x + y                                                    │
│                                                                          │
│   2. FUNCTIONAL INTERFACES                                               │
│      Interfaces with single abstract method                             │
│      @FunctionalInterface                                                │
│                                                                          │
│   3. STREAM API                                                          │
│      Functional-style operations on collections                         │
│      list.stream().filter().map().collect()                             │
│                                                                          │
│   4. OPTIONAL CLASS                                                      │
│      Container to avoid NullPointerException                            │
│      Optional<String> name = Optional.of("John");                       │
│                                                                          │
│   5. DEFAULT & STATIC METHODS IN INTERFACES                              │
│      Interfaces can have method implementations                         │
│      default void print() { ... }                                       │
│                                                                          │
│   6. METHOD REFERENCES                                                   │
│      Shorthand for lambdas calling existing methods                     │
│      System.out::println                                                │
│                                                                          │
│   7. NEW DATE/TIME API (java.time)                                       │
│      Immutable, thread-safe date/time classes                           │
│      LocalDate, LocalTime, LocalDateTime, ZonedDateTime                 │
│                                                                          │
│   8. NASHORN JAVASCRIPT ENGINE                                           │
│      Run JavaScript within Java                                         │
│                                                                          │
│   9. COLLECTORS                                                          │
│      Utility class for Stream terminal operations                       │
│      Collectors.toList(), Collectors.groupingBy()                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q2: What are Lambda Expressions? Explain with examples.

**Answer:**

**Lambda Expressions** are anonymous functions that provide a concise way to implement functional interfaces.

### Syntax:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       LAMBDA SYNTAX                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   (parameters) -> expression                                             │
│   (parameters) -> { statements; }                                        │
│                                                                          │
│   EXAMPLES:                                                              │
│   ─────────                                                              │
│   () -> 42                          // No params, returns 42            │
│   x -> x * 2                        // One param, returns x * 2         │
│   (x, y) -> x + y                   // Two params, returns sum          │
│   (String s) -> s.length()          // Explicit type                    │
│   (x, y) -> { return x + y; }       // With block body                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Before vs After Lambda:

```java
// BEFORE JAVA 8 (Anonymous inner class)
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello from thread!");
    }
};

// AFTER JAVA 8 (Lambda)
Runnable runnable = () -> System.out.println("Hello from thread!");
```

```java
// BEFORE: Sorting with Comparator
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});

// AFTER: Lambda expression
Collections.sort(names, (a, b) -> a.compareTo(b));

// EVEN SHORTER: Method reference
Collections.sort(names, String::compareTo);
```

### Common Use Cases:

```java
// 1. Collection iteration
List<String> names = Arrays.asList("John", "Jane", "Bob");
names.forEach(name -> System.out.println(name));

// 2. Filtering
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// 3. Mapping/Transformation
List<String> upperNames = names.stream()
    .map(name -> name.toUpperCase())
    .collect(Collectors.toList());

// 4. Event handlers
button.setOnClickListener(event -> handleClick(event));

// 5. Thread creation
new Thread(() -> {
    System.out.println("Running in new thread");
}).start();
```

### Key Characteristics:
- Lambdas can only implement **Functional Interfaces** (single abstract method)
- Variables used inside lambda must be **effectively final**
- `this` inside lambda refers to the enclosing class, not the lambda itself

---

## Q3: What are Functional Interfaces? List built-in functional interfaces.

**Answer:**

A **Functional Interface** is an interface with exactly **one abstract method** (SAM - Single Abstract Method). It can have multiple default or static methods.

### Definition:

```java
@FunctionalInterface  // Optional but recommended
public interface Calculator {
    int calculate(int a, int b);  // Single abstract method
    
    // Can have default methods
    default void printResult(int result) {
        System.out.println("Result: " + result);
    }
    
    // Can have static methods
    static Calculator getAddCalculator() {
        return (a, b) -> a + b;
    }
}

// Usage
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;

System.out.println(add.calculate(5, 3));      // 8
System.out.println(multiply.calculate(5, 3)); // 15
```

### Built-in Functional Interfaces (java.util.function):

```
┌─────────────────────────────────────────────────────────────────────────┐
│              COMMONLY USED FUNCTIONAL INTERFACES                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   INTERFACE          METHOD              DESCRIPTION                     │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Predicate<T>       test(T) → boolean   Tests a condition               │
│   ──────────────────────────────────────────────────────────────────     │
│   Predicate<Integer> isEven = n -> n % 2 == 0;                          │
│   isEven.test(4);  // true                                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Function<T,R>      apply(T) → R        Transforms T to R               │
│   ──────────────────────────────────────────────────────────────────     │
│   Function<String, Integer> length = s -> s.length();                   │
│   length.apply("Hello");  // 5                                           │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Consumer<T>        accept(T) → void    Consumes input, no return       │
│   ──────────────────────────────────────────────────────────────────     │
│   Consumer<String> print = s -> System.out.println(s);                  │
│   print.accept("Hello");  // prints "Hello"                              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Supplier<T>        get() → T           Supplies a value, no input      │
│   ──────────────────────────────────────────────────────────────────     │
│   Supplier<Double> random = () -> Math.random();                        │
│   random.get();  // 0.7234...                                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   BiFunction<T,U,R>  apply(T,U) → R      Two inputs, one output          │
│   ──────────────────────────────────────────────────────────────────     │
│   BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;          │
│   add.apply(3, 5);  // 8                                                 │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   UnaryOperator<T>   apply(T) → T        Same type in and out            │
│   ──────────────────────────────────────────────────────────────────     │
│   UnaryOperator<Integer> square = x -> x * x;                           │
│   square.apply(5);  // 25                                                │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   BinaryOperator<T>  apply(T,T) → T      Two same-type inputs            │
│   ──────────────────────────────────────────────────────────────────     │
│   BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;                │
│   max.apply(10, 20);  // 20                                              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Chaining Functional Interfaces:

```java
// Predicate chaining
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isPositiveEven = isEven.and(isPositive);

isPositiveEven.test(4);   // true
isPositiveEven.test(-4);  // false

// Function chaining
Function<String, String> trim = String::trim;
Function<String, String> toUpper = String::toUpperCase;
Function<String, String> trimAndUpper = trim.andThen(toUpper);

trimAndUpper.apply("  hello  ");  // "HELLO"
```

---

## Q4: Explain the Stream API with examples.

**Answer:**

**Stream API** provides a functional approach to process collections of objects. Streams don't store data; they operate on the source data.

### Stream Pipeline:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       STREAM PIPELINE                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SOURCE           INTERMEDIATE OPS        TERMINAL OP                  │
│   (Collection)     (Lazy, return Stream)   (Eager, triggers execution)  │
│                                                                          │
│   list.stream()  → filter() → map() → sorted() → collect()/forEach()   │
│                                                                          │
│   ┌──────────┐    ┌──────────────────────┐    ┌─────────────────────┐   │
│   │  Source  │───▶│ Intermediate Ops     │───▶│ Terminal Operation  │   │
│   │          │    │ (Lazy - not executed │    │ (Triggers pipeline) │   │
│   │ List     │    │  until terminal op)  │    │                     │   │
│   │ Set      │    │ - filter()           │    │ - collect()         │   │
│   │ Array    │    │ - map()              │    │ - forEach()         │   │
│   │ Stream.of│    │ - flatMap()          │    │ - reduce()          │   │
│   │          │    │ - sorted()           │    │ - count()           │   │
│   │          │    │ - distinct()         │    │ - findFirst()       │   │
│   │          │    │ - limit()            │    │ - anyMatch()        │   │
│   │          │    │ - skip()             │    │ - toArray()         │   │
│   └──────────┘    └──────────────────────┘    └─────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common Stream Operations:

```java
List<Employee> employees = Arrays.asList(
    new Employee("John", 50000, "IT"),
    new Employee("Jane", 60000, "HR"),
    new Employee("Bob", 45000, "IT"),
    new Employee("Alice", 70000, "Finance")
);

// 1. FILTER - Select elements matching condition
List<Employee> itEmployees = employees.stream()
    .filter(e -> e.getDepartment().equals("IT"))
    .collect(Collectors.toList());
// [John, Bob]

// 2. MAP - Transform elements
List<String> names = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.toList());
// [John, Jane, Bob, Alice]

// 3. SORTED - Sort elements
List<Employee> sortedBySalary = employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary))
    .collect(Collectors.toList());
// [Bob, John, Jane, Alice]

// 4. REDUCE - Combine elements to single value
int totalSalary = employees.stream()
    .map(Employee::getSalary)
    .reduce(0, Integer::sum);
// 225000

// 5. COLLECT - Gather results
Map<String, List<Employee>> byDepartment = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));
// {IT=[John, Bob], HR=[Jane], Finance=[Alice]}

// 6. FLATMAP - Flatten nested structures
List<List<String>> nestedList = Arrays.asList(
    Arrays.asList("a", "b"),
    Arrays.asList("c", "d")
);
List<String> flatList = nestedList.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
// [a, b, c, d]

// 7. DISTINCT - Remove duplicates
List<Integer> unique = Arrays.asList(1, 2, 2, 3, 3, 3)
    .stream()
    .distinct()
    .collect(Collectors.toList());
// [1, 2, 3]

// 8. LIMIT & SKIP - Pagination
List<Employee> page = employees.stream()
    .skip(2)    // Skip first 2
    .limit(2)   // Take next 2
    .collect(Collectors.toList());

// 9. ALLMATCH, ANYMATCH, NONEMATCH
boolean allHighEarners = employees.stream()
    .allMatch(e -> e.getSalary() > 40000);  // true

boolean anyIT = employees.stream()
    .anyMatch(e -> e.getDepartment().equals("IT"));  // true

// 10. FINDFIRST, FINDANY
Optional<Employee> first = employees.stream()
    .filter(e -> e.getSalary() > 50000)
    .findFirst();  // Jane
```

### Parallel Streams:
```java
// For large datasets, use parallel streams for better performance
long count = employees.parallelStream()
    .filter(e -> e.getSalary() > 50000)
    .count();
```

---

## Q5: What is the difference between map() and flatMap()?

**Answer:**

Both transform elements, but they differ in how they handle the result.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       map() vs flatMap()                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   map(): One-to-One transformation                                       │
│   ─────────────────────────────────                                      │
│   Each element → exactly one element                                     │
│                                                                          │
│   Input:    [A, B, C]                                                    │
│   Function: x -> x.toLowerCase()                                        │
│   Output:   [a, b, c]                                                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   flatMap(): One-to-Many transformation (then flatten)                   │
│   ─────────────────────────────────────────────────────                  │
│   Each element → stream of elements → flattened to single stream        │
│                                                                          │
│   Input:    [[1,2], [3,4], [5,6]]                                        │
│   Function: list -> list.stream()                                       │
│   Output:   [1, 2, 3, 4, 5, 6]                                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Visual Example:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    VISUAL COMPARISON                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Using map() with nested list:                                          │
│                                                                          │
│   List<List<String>> words = [["Hello", "World"], ["Java", "8"]]        │
│                                                                          │
│   words.stream()                                                         │
│        .map(list -> list.stream())     // Returns Stream<Stream<String>>│
│        .collect(Collectors.toList());                                    │
│                                                                          │
│   Result: [Stream@1a2b, Stream@3c4d]  ← NOT what we want!               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Using flatMap() with nested list:                                      │
│                                                                          │
│   words.stream()                                                         │
│        .flatMap(list -> list.stream())  // Flattens to Stream<String>   │
│        .collect(Collectors.toList());                                    │
│                                                                          │
│   Result: ["Hello", "World", "Java", "8"]  ← Flattened! ✅              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Practical Examples:

```java
// Example 1: Get all characters from list of words
List<String> words = Arrays.asList("Hello", "World");

// Using map - WRONG (gives Stream of arrays)
List<String[]> wrongResult = words.stream()
    .map(word -> word.split(""))
    .collect(Collectors.toList());
// [[H, e, l, l, o], [W, o, r, l, d]]

// Using flatMap - CORRECT (gives flat stream)
List<String> correctResult = words.stream()
    .map(word -> word.split(""))
    .flatMap(Arrays::stream)
    .distinct()
    .collect(Collectors.toList());
// [H, e, l, o, W, r, d]


// Example 2: Get all orders from all customers
List<Customer> customers = getCustomers();

// Get all orders (each customer has List<Order>)
List<Order> allOrders = customers.stream()
    .flatMap(customer -> customer.getOrders().stream())
    .collect(Collectors.toList());


// Example 3: Optional flatMap
Optional<String> name = Optional.of("  John  ");
Optional<String> trimmed = name.flatMap(n -> 
    n.trim().isEmpty() ? Optional.empty() : Optional.of(n.trim())
);
```

### Summary:

| Aspect | map() | flatMap() |
|--------|-------|-----------|
| **Output** | Same number of elements | Can be more/fewer elements |
| **Return type** | `Stream<R>` | Expects `Stream<R>` from function |
| **Use case** | Transform each element | Flatten nested structures |
| **Function** | `T -> R` | `T -> Stream<R>` |

---

## Q6: What is Optional? How do you use it?

**Answer:**

**Optional** is a container that may or may not contain a non-null value. It's designed to avoid `NullPointerException`.

### The Problem:

```java
// BEFORE OPTIONAL - Null checks everywhere! ❌
public String getUserCity(User user) {
    if (user != null) {
        Address address = user.getAddress();
        if (address != null) {
            City city = address.getCity();
            if (city != null) {
                return city.getName();
            }
        }
    }
    return "Unknown";
}
```

### Creating Optional:

```java
// 1. Create with non-null value
Optional<String> name = Optional.of("John");

// 2. Create with possibly null value
String nullableName = getName();  // might be null
Optional<String> optName = Optional.ofNullable(nullableName);

// 3. Create empty Optional
Optional<String> empty = Optional.empty();
```

### Using Optional:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       OPTIONAL METHODS                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   CHECKING VALUE:                                                        │
│   ────────────────                                                       │
│   isPresent()    → true if value exists                                 │
│   isEmpty()      → true if no value (Java 11+)                          │
│                                                                          │
│   GETTING VALUE:                                                         │
│   ───────────────                                                        │
│   get()          → returns value, throws if empty ⚠️                    │
│   orElse(T)      → returns value or default                             │
│   orElseGet(Supplier)  → returns value or computed default              │
│   orElseThrow()  → returns value or throws exception                    │
│                                                                          │
│   TRANSFORMING:                                                          │
│   ─────────────                                                          │
│   map(Function)     → transforms value if present                       │
│   flatMap(Function) → transforms to another Optional                    │
│   filter(Predicate) → returns Optional if predicate matches             │
│                                                                          │
│   EXECUTING:                                                             │
│   ──────────                                                             │
│   ifPresent(Consumer)        → executes if value present                │
│   ifPresentOrElse(Consumer, Runnable) → executes based on presence     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Practical Examples:

```java
Optional<String> name = Optional.of("John");

// 1. orElse - Provide default value
String result = name.orElse("Unknown");  // "John"

Optional<String> empty = Optional.empty();
String result2 = empty.orElse("Unknown");  // "Unknown"

// 2. orElseGet - Lazy default (computed only if needed)
String result3 = empty.orElseGet(() -> computeExpensiveDefault());

// 3. orElseThrow - Throw exception if empty
String result4 = empty.orElseThrow(() -> 
    new UserNotFoundException("User not found"));

// 4. map - Transform value
Optional<Integer> length = name.map(String::length);  // Optional[4]

// 5. filter - Conditional Optional
Optional<String> longName = name.filter(n -> n.length() > 5);  // empty

// 6. ifPresent - Execute if value present
name.ifPresent(n -> System.out.println("Hello, " + n));

// 7. ifPresentOrElse (Java 9+)
name.ifPresentOrElse(
    n -> System.out.println("Hello, " + n),
    () -> System.out.println("No name provided")
);
```

### Chaining with Optional:

```java
// Clean null-safe navigation with Optional
public String getUserCity(User user) {
    return Optional.ofNullable(user)
        .map(User::getAddress)
        .map(Address::getCity)
        .map(City::getName)
        .orElse("Unknown");
}

// Compare with ugly nested null checks before!
```

### Optional Best Practices:

| Do ✅ | Don't ❌ |
|------|---------|
| Use as return type | Use as method parameter |
| Use `orElse`/`orElseGet` | Use `get()` without checking |
| Use in Stream operations | Store in class fields |
| Use `map`/`flatMap` for chaining | Use `isPresent()` + `get()` |

```java
// ❌ BAD - Don't use Optional as parameter
public void processUser(Optional<User> user) { }

// ✅ GOOD - Handle null inside method
public void processUser(User user) {
    Optional.ofNullable(user).ifPresent(this::process);
}

// ❌ BAD - Don't use isPresent + get
if (optional.isPresent()) {
    return optional.get();
}

// ✅ GOOD - Use orElse or orElseGet
return optional.orElse(defaultValue);
```

---

## Q7: What are Method References? Explain the types.

**Answer:**

**Method References** are shorthand for lambda expressions that call an existing method.

### Syntax: `ClassName::methodName` or `object::methodName`

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    TYPES OF METHOD REFERENCES                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. STATIC METHOD REFERENCE                                             │
│   ───────────────────────────                                            │
│   ClassName::staticMethod                                                │
│                                                                          │
│   Lambda:      (x) -> Math.abs(x)                                        │
│   Reference:   Math::abs                                                 │
│                                                                          │
│   Example:                                                               │
│   List<Integer> numbers = Arrays.asList(-1, -2, 3, -4);                 │
│   numbers.stream()                                                       │
│          .map(Math::abs)       // Instead of n -> Math.abs(n)           │
│          .collect(Collectors.toList());                                  │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. INSTANCE METHOD REFERENCE (of particular object)                    │
│   ───────────────────────────────────────────────────                    │
│   object::instanceMethod                                                 │
│                                                                          │
│   Lambda:      (x) -> printer.print(x)                                   │
│   Reference:   printer::print                                            │
│                                                                          │
│   Example:                                                               │
│   Printer printer = new Printer();                                       │
│   names.forEach(printer::print);  // Instead of n -> printer.print(n)  │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. INSTANCE METHOD REFERENCE (of arbitrary object)                     │
│   ───────────────────────────────────────────────────                    │
│   ClassName::instanceMethod                                              │
│                                                                          │
│   Lambda:      (s) -> s.length()                                         │
│   Reference:   String::length                                            │
│                                                                          │
│   Example:                                                               │
│   names.stream()                                                         │
│        .map(String::toUpperCase)  // Instead of s -> s.toUpperCase()    │
│        .collect(Collectors.toList());                                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   4. CONSTRUCTOR REFERENCE                                               │
│   ────────────────────────                                               │
│   ClassName::new                                                         │
│                                                                          │
│   Lambda:      () -> new ArrayList<>()                                   │
│   Reference:   ArrayList::new                                            │
│                                                                          │
│   Example:                                                               │
│   Supplier<List<String>> listSupplier = ArrayList::new;                 │
│   List<String> list = listSupplier.get();                               │
│                                                                          │
│   // With Stream                                                         │
│   List<User> users = names.stream()                                      │
│       .map(User::new)       // Calls User(String name) constructor      │
│       .collect(Collectors.toList());                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common Method References:

```java
// Printing
list.forEach(System.out::println);  // Consumer

// String operations
list.stream().map(String::toLowerCase);  // Function
list.stream().map(String::length);       // Function

// Comparisons
list.sort(String::compareTo);            // Comparator
list.stream().sorted(Comparator.comparing(User::getName));

// Object creation
Stream.generate(Random::new);            // Supplier
list.stream().map(User::new);            // Function

// Boolean checks
list.stream().filter(String::isEmpty);   // Predicate
```

---

## Q8: What are Default and Static methods in interfaces?

**Answer:**

Java 8 allows interfaces to have method implementations using `default` and `static` keywords.

### Default Methods:

```java
public interface Vehicle {
    // Abstract method (as before)
    void start();
    
    // Default method - has implementation
    default void honk() {
        System.out.println("Beep beep!");
    }
    
    // Default method with logic
    default void startAndHonk() {
        start();
        honk();
    }
}

public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car starting...");
    }
    
    // Can optionally override default method
    @Override
    public void honk() {
        System.out.println("Car honking: BEEP!");
    }
}

// Usage
Vehicle car = new Car();
car.start();        // "Car starting..."
car.honk();         // "Car honking: BEEP!"
car.startAndHonk(); // Uses default implementation
```

### Why Default Methods?

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    WHY DEFAULT METHODS?                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   PROBLEM: Adding new method to interface breaks all implementations    │
│                                                                          │
│   interface List<E> {                                                    │
│       void add(E e);                                                     │
│       E get(int index);                                                  │
│       // Adding this would break ALL existing List implementations!     │
│       void forEach(Consumer<E> action);  ← NEW METHOD                   │
│   }                                                                      │
│                                                                          │
│   SOLUTION: Default methods with implementation                          │
│                                                                          │
│   interface List<E> {                                                    │
│       void add(E e);                                                     │
│       E get(int index);                                                  │
│       default void forEach(Consumer<E> action) {                        │
│           // Default implementation                                     │
│           for (E e : this) {                                            │
│               action.accept(e);                                          │
│           }                                                              │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ✅ Existing implementations still work!                                │
│   ✅ New implementations get default behavior                            │
│   ✅ Implementations can override if needed                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Static Methods in Interfaces:

```java
public interface MathUtils {
    // Static method - called on interface, not instance
    static int add(int a, int b) {
        return a + b;
    }
    
    static int multiply(int a, int b) {
        return a * b;
    }
}

// Usage
int sum = MathUtils.add(5, 3);       // 8
int product = MathUtils.multiply(5, 3);  // 15
```

### Diamond Problem Resolution:

```java
interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

interface B {
    default void hello() {
        System.out.println("Hello from B");
    }
}

// Class implementing both must override to resolve ambiguity
class C implements A, B {
    @Override
    public void hello() {
        // Choose one or provide own implementation
        A.super.hello();  // Call A's default
        // OR
        B.super.hello();  // Call B's default
        // OR
        System.out.println("Hello from C");
    }
}
```

---

## Q9: Explain the new Date/Time API in Java 8.

**Answer:**

Java 8 introduced a new Date/Time API in `java.time` package to replace the flawed `java.util.Date` and `java.util.Calendar`.

### Problems with Old API:

```
┌─────────────────────────────────────────────────────────────────────────┐
│              OLD API PROBLEMS vs NEW API SOLUTIONS                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   OLD (java.util.Date)           NEW (java.time)                        │
│   ─────────────────────────────────────────────────────────────────────  │
│   ❌ Mutable                     ✅ Immutable                            │
│   ❌ Not thread-safe             ✅ Thread-safe                          │
│   ❌ Poor API design             ✅ Clean, fluent API                    │
│   ❌ Month is 0-indexed          ✅ Month is 1-indexed (1=Jan)           │
│   ❌ Year is 1900-based          ✅ Year is actual year                  │
│   ❌ No timezone support         ✅ Excellent timezone support           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Classes:

```java
// 1. LocalDate - Date without time
LocalDate today = LocalDate.now();                    // 2024-01-10
LocalDate birthday = LocalDate.of(1990, 5, 15);       // 1990-05-15
LocalDate parsed = LocalDate.parse("2024-01-10");     // 2024-01-10

// 2. LocalTime - Time without date
LocalTime now = LocalTime.now();                      // 14:30:45.123
LocalTime meeting = LocalTime.of(14, 30);             // 14:30
LocalTime parsed = LocalTime.parse("14:30:00");       // 14:30:00

// 3. LocalDateTime - Date and time without timezone
LocalDateTime now = LocalDateTime.now();
LocalDateTime event = LocalDateTime.of(2024, 12, 25, 10, 30);

// 4. ZonedDateTime - Date and time with timezone
ZonedDateTime nowInIndia = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
ZonedDateTime nowInNY = ZonedDateTime.now(ZoneId.of("America/New_York"));

// 5. Instant - Machine timestamp (epoch seconds)
Instant timestamp = Instant.now();  // 2024-01-10T09:00:45.123Z

// 6. Duration - Time-based amount
Duration twoHours = Duration.ofHours(2);
Duration between = Duration.between(start, end);

// 7. Period - Date-based amount
Period oneMonth = Period.ofMonths(1);
Period age = Period.between(birthday, LocalDate.now());
```

### Common Operations:

```java
LocalDate today = LocalDate.now();

// Manipulation (returns new instance - immutable)
LocalDate tomorrow = today.plusDays(1);
LocalDate nextMonth = today.plusMonths(1);
LocalDate lastYear = today.minusYears(1);

// Comparison
boolean isBefore = today.isBefore(tomorrow);   // true
boolean isAfter = today.isAfter(tomorrow);     // false

// Getting parts
int year = today.getYear();           // 2024
Month month = today.getMonth();       // JANUARY
int day = today.getDayOfMonth();      // 10
DayOfWeek dow = today.getDayOfWeek(); // FRIDAY

// Formatting
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
String formatted = today.format(formatter);  // "10-01-2024"

// Parsing
LocalDate parsed = LocalDate.parse("10-01-2024", formatter);

// Working with time zones
ZoneId kolkata = ZoneId.of("Asia/Kolkata");
ZonedDateTime indiaTime = ZonedDateTime.now(kolkata);
ZonedDateTime utcTime = indiaTime.withZoneSameInstant(ZoneId.of("UTC"));
```

### Comparison with Old API:

```java
// OLD WAY ❌
Date date = new Date();
Calendar calendar = Calendar.getInstance();
calendar.setTime(date);
calendar.add(Calendar.DAY_OF_MONTH, 1);
Date tomorrow = calendar.getTime();

// NEW WAY ✅
LocalDate tomorrow = LocalDate.now().plusDays(1);
```

---

## Q10: What are Collectors in Java 8? Give examples.

**Answer:**

**Collectors** is a utility class providing common reduction operations for Stream `collect()` terminal operation.

### Common Collectors:

```java
List<Employee> employees = getEmployees();

// 1. toList() - Collect to List
List<String> names = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.toList());

// 2. toSet() - Collect to Set (removes duplicates)
Set<String> departments = employees.stream()
    .map(Employee::getDepartment)
    .collect(Collectors.toSet());

// 3. toMap() - Collect to Map
Map<Long, String> idToName = employees.stream()
    .collect(Collectors.toMap(
        Employee::getId,      // Key mapper
        Employee::getName     // Value mapper
    ));

// 4. joining() - Concatenate strings
String allNames = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", "));  // "John, Jane, Bob"

String csv = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));  // "[John, Jane, Bob]"

// 5. counting() - Count elements
long count = employees.stream()
    .filter(e -> e.getSalary() > 50000)
    .collect(Collectors.counting());

// 6. summingInt/Double/Long() - Sum values
int totalSalary = employees.stream()
    .collect(Collectors.summingInt(Employee::getSalary));

// 7. averagingInt/Double/Long() - Average
double avgSalary = employees.stream()
    .collect(Collectors.averagingDouble(Employee::getSalary));

// 8. maxBy/minBy() - Find max/min
Optional<Employee> highestPaid = employees.stream()
    .collect(Collectors.maxBy(Comparator.comparing(Employee::getSalary)));

// 9. groupingBy() - Group by property
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));
// {IT=[John, Bob], HR=[Jane], Finance=[Alice]}

// 10. groupingBy() with downstream collector
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.counting()
    ));
// {IT=2, HR=1, Finance=1}

// 11. partitioningBy() - Split into two groups
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 50000));
// {true=[Jane, Alice], false=[John, Bob]}

// 12. summarizingInt/Double/Long() - Get statistics
IntSummaryStatistics stats = employees.stream()
    .collect(Collectors.summarizingInt(Employee::getSalary));
// count=4, sum=225000, min=45000, avg=56250, max=70000

// 13. collectingAndThen() - Transform result
List<Employee> unmodifiableList = employees.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        Collections::unmodifiableList
    ));

// 14. mapping() - Map then collect
Map<String, List<String>> namesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));
// {IT=[John, Bob], HR=[Jane], Finance=[Alice]}
```

### Complex Example:

```java
// Get department with highest total salary
String topDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.summingInt(Employee::getSalary)
    ))
    .entrySet().stream()
    .max(Map.Entry.comparingByValue())
    .map(Map.Entry::getKey)
    .orElse("None");
```

---

## Q11: What is the difference between findFirst() and findAny()?

**Answer:**

Both return an `Optional` with a matching element, but they differ in behavior with parallel streams.

| Method | Sequential | Parallel |
|--------|------------|----------|
| `findFirst()` | First match | First match (in encounter order) |
| `findAny()` | First match | Any match (faster, non-deterministic) |

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Sequential - both return 2
Optional<Integer> first = numbers.stream()
    .filter(n -> n % 2 == 0)
    .findFirst();  // Always 2

Optional<Integer> any = numbers.stream()
    .filter(n -> n % 2 == 0)
    .findAny();    // Always 2

// Parallel - findAny may return any even number
Optional<Integer> anyParallel = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .findAny();    // Could be 2, 4, 6, 8, or 10
```

**Use Case:**
- `findFirst()` - When order matters
- `findAny()` - When any match is acceptable (better parallel performance)

---

## Q12: What are Parallel Streams? When should you use them?

**Answer:**

**Parallel Streams** split the source data into multiple chunks and process them concurrently using the ForkJoinPool.

```java
// Sequential stream
list.stream()
    .filter(...)
    .map(...)
    .collect(...);

// Parallel stream
list.parallelStream()
    .filter(...)
    .map(...)
    .collect(...);

// Or convert to parallel
list.stream()
    .parallel()
    .filter(...)
    .collect(...);
```

### When to Use:

| Use Parallel | Avoid Parallel |
|--------------|----------------|
| Large datasets (10,000+ elements) | Small datasets |
| CPU-intensive operations | I/O operations |
| Independent operations | Order-dependent operations |
| No shared mutable state | Operations with side effects |

```java
// Good for parallel - CPU-intensive, no side effects
long sum = numbers.parallelStream()
    .filter(n -> isPrime(n))
    .mapToLong(n -> n)
    .sum();

// Bad for parallel - shared mutable state
List<Integer> results = new ArrayList<>();  // NOT thread-safe!
numbers.parallelStream()
    .filter(n -> n > 5)
    .forEach(results::add);  // Race condition!
```

---

## Q13: What are Primitive Streams in Java 8?

**Answer:**

Java 8 provides specialized streams for primitives to avoid boxing/unboxing overhead.

| Stream | Type | Methods |
|--------|------|---------|
| `IntStream` | int | `sum()`, `average()`, `range()` |
| `LongStream` | long | `sum()`, `average()`, `range()` |
| `DoubleStream` | double | `sum()`, `average()` |

```java
// Creating primitive streams
IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
IntStream range = IntStream.range(1, 10);       // 1-9
IntStream rangeClosed = IntStream.rangeClosed(1, 10); // 1-10

// Converting from Object stream
IntStream lengths = names.stream()
    .mapToInt(String::length);

// Primitive stream operations
int sum = IntStream.rangeClosed(1, 100).sum();  // 5050
OptionalDouble avg = IntStream.of(1, 2, 3).average();  // 2.0

// Boxing back to Object stream
Stream<Integer> boxed = IntStream.range(1, 5).boxed();

// Efficient number processing
IntSummaryStatistics stats = IntStream.of(1, 2, 3, 4, 5)
    .summaryStatistics();
// count=5, sum=15, min=1, average=3.0, max=5
```

---

## Q14: How do you handle exceptions in Lambda expressions?

**Answer:**

Lambda expressions don't allow checked exceptions directly. Here are solutions:

### Problem:
```java
// Won't compile - checked exception
list.stream()
    .map(s -> Files.readString(Path.of(s)))  // IOException!
    .collect(Collectors.toList());
```

### Solution 1: Wrapper method
```java
private String readFileSafe(String path) {
    try {
        return Files.readString(Path.of(path));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

list.stream()
    .map(this::readFileSafe)
    .collect(Collectors.toList());
```

### Solution 2: Functional wrapper
```java
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
    
    static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

// Usage
list.stream()
    .map(ThrowingFunction.wrap(s -> Files.readString(Path.of(s))))
    .collect(Collectors.toList());
```

---

## Q15: What is the difference between reduce() and collect()?

**Answer:**

Both are terminal operations that combine stream elements, but they work differently.

| Aspect | reduce() | collect() |
|--------|----------|-----------|
| **Purpose** | Combine to single value | Mutable accumulation |
| **Return** | Single value | Collection or object |
| **Thread-safe** | Yes (immutable) | Needs combiner for parallel |
| **Use case** | Sum, min, max, concat | List, Set, Map, String |

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// reduce() - combines to single value
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);  // 15

Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);  // Optional[5]

// collect() - accumulates into collection
List<Integer> list = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());  // [3, 4, 5]

String joined = numbers.stream()
    .map(String::valueOf)
    .collect(Collectors.joining(", "));  // "1, 2, 3, 4, 5"
```

### When to use:
- **reduce()**: Math operations (sum, product, min, max)
- **collect()**: Building collections, grouping, partitioning

---

## Q16: How do you sort a stream with multiple fields?

**Answer:**

Use `Comparator.comparing()` with `thenComparing()` for multi-field sorting.

```java
List<Employee> employees = getEmployees();

// Sort by single field
List<Employee> byName = employees.stream()
    .sorted(Comparator.comparing(Employee::getName))
    .collect(Collectors.toList());

// Sort by multiple fields
List<Employee> sorted = employees.stream()
    .sorted(Comparator
        .comparing(Employee::getDepartment)      // Primary
        .thenComparing(Employee::getSalary)      // Secondary
        .thenComparing(Employee::getName))       // Tertiary
    .collect(Collectors.toList());

// Descending order
List<Employee> descending = employees.stream()
    .sorted(Comparator
        .comparing(Employee::getSalary)
        .reversed())
    .collect(Collectors.toList());

// Null-safe sorting
List<Employee> nullSafe = employees.stream()
    .sorted(Comparator
        .comparing(Employee::getManager, 
            Comparator.nullsLast(Comparator.naturalOrder())))
    .collect(Collectors.toList());
```

---

## Q17: What is StringJoiner and how does it relate to Stream API?

**Answer:**

**StringJoiner** (Java 8) builds strings with delimiters, prefix, and suffix.

```java
// Basic StringJoiner
StringJoiner joiner = new StringJoiner(", ");
joiner.add("Apple");
joiner.add("Banana");
joiner.add("Cherry");
System.out.println(joiner.toString());  // "Apple, Banana, Cherry"

// With prefix and suffix
StringJoiner joinerWithBrackets = new StringJoiner(", ", "[", "]");
joinerWithBrackets.add("1").add("2").add("3");
System.out.println(joinerWithBrackets);  // "[1, 2, 3]"

// Using with Stream
String result = Stream.of("A", "B", "C")
    .collect(Collectors.joining(", ", "[", "]"));
// "[A, B, C]"

// String.join() - simpler alternative
String joined = String.join(", ", "Apple", "Banana", "Cherry");
// "Apple, Banana, Cherry"
```

---

## Q18: How do you convert between Stream, List, Set, and Array?

**Answer:**

```java
// List to Stream
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();

// Array to Stream
String[] array = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(array);
Stream<String> stream3 = Stream.of(array);

// Stream to List
List<String> toList = stream1.collect(Collectors.toList());
List<String> toUnmodifiable = stream1.toList();  // Java 16+

// Stream to Set
Set<String> toSet = stream2.collect(Collectors.toSet());

// Stream to Array
String[] toArray = stream3.toArray(String[]::new);

// Set to List
Set<String> set = new HashSet<>(Arrays.asList("a", "b"));
List<String> setToList = new ArrayList<>(set);
// Or with Stream
List<String> setToList2 = set.stream().collect(Collectors.toList());

// Stream to Map
Map<String, Integer> toMap = Stream.of("a", "bb", "ccc")
    .collect(Collectors.toMap(
        s -> s,              // key
        String::length       // value
    ));
// {a=1, bb=2, ccc=3}
```

---

## Q19: What is the peek() method used for?

**Answer:**

`peek()` is an intermediate operation for debugging - it performs an action on each element without modifying the stream.

```java
List<String> result = Stream.of("one", "two", "three")
    .filter(s -> s.length() > 3)
    .peek(s -> System.out.println("Filtered: " + s))
    .map(String::toUpperCase)
    .peek(s -> System.out.println("Mapped: " + s))
    .collect(Collectors.toList());

// Output:
// Filtered: three
// Mapped: THREE
// Result: [THREE]
```

### Important Notes:
- **Lazy execution** - peek() only runs for elements that reach it
- **Debugging only** - Don't use for business logic
- **May not execute** - If no terminal operation, peek() never runs

```java
// ⚠️ This prints NOTHING - no terminal operation!
Stream.of("a", "b", "c")
    .peek(System.out::println);

// ✅ This prints - has terminal operation
Stream.of("a", "b", "c")
    .peek(System.out::println)
    .count();  // Terminal operation
```

---

## Q20: What is the difference between Collection.stream() and Stream.of()?

**Answer:**

| Method | Source | Use Case |
|--------|--------|----------|
| `collection.stream()` | Existing collection | Process List, Set, etc. |
| `Stream.of(...)` | Varargs or array | Create stream from values |
| `Arrays.stream()` | Array | Array to stream |
| `Stream.generate()` | Supplier | Infinite stream |
| `Stream.iterate()` | Initial + UnaryOperator | Sequence generation |

```java
// From collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> s1 = list.stream();

// From values
Stream<String> s2 = Stream.of("a", "b", "c");
Stream<Integer> s3 = Stream.of(1, 2, 3);

// From array
String[] arr = {"a", "b", "c"};
Stream<String> s4 = Arrays.stream(arr);

// Generate infinite stream
Stream<Double> randoms = Stream.generate(Math::random).limit(5);

// Iterate (infinite by default)
Stream<Integer> evens = Stream.iterate(0, n -> n + 2).limit(10);
// [0, 2, 4, 6, 8, 10, 12, 14, 16, 18]

// Iterate with predicate (Java 9+)
Stream<Integer> limited = Stream.iterate(1, n -> n < 100, n -> n * 2);
// [1, 2, 4, 8, 16, 32, 64]
```

---

## Q21: What are the differences between orElse() and orElseGet()?

**Answer:**

Both provide default values for empty Optional, but `orElseGet()` is lazy.

```java
// orElse() - ALWAYS evaluates the default
Optional<String> opt = Optional.of("value");
String result = opt.orElse(expensiveOperation());  // Method called!

// orElseGet() - ONLY evaluates if Optional is empty
String result2 = opt.orElseGet(() -> expensiveOperation());  // NOT called

// Example showing the difference
public String expensiveOperation() {
    System.out.println("Expensive operation called!");
    return "default";
}

Optional<String> present = Optional.of("value");

// orElse - prints "Expensive operation called!"
present.orElse(expensiveOperation());

// orElseGet - prints nothing (lazy!)
present.orElseGet(this::expensiveOperation);
```

### When to use:
- **orElse()**: Simple, constant defaults
- **orElseGet()**: Expensive computations, database calls, I/O

---

## Q22: How do you use groupingBy() with downstream collectors?

**Answer:**

`groupingBy()` can have a downstream collector for additional processing.

```java
List<Employee> employees = getEmployees();

// Simple grouping
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// Count employees per department
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.counting()
    ));

// Sum salaries per department
Map<String, Integer> salaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.summingInt(Employee::getSalary)
    ));

// Get max salary per department
Map<String, Optional<Employee>> maxByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.maxBy(Comparator.comparing(Employee::getSalary))
    ));

// Get names per department
Map<String, List<String>> namesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));

// Nested grouping
Map<String, Map<String, List<Employee>>> byDeptAndCity = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.groupingBy(Employee::getCity)
    ));
```

---

## Q23: What is partitioningBy()? How is it different from groupingBy()?

**Answer:**

`partitioningBy()` divides elements into exactly two groups based on a predicate.

| Feature | groupingBy() | partitioningBy() |
|---------|--------------|------------------|
| Groups | Any number | Exactly 2 (true/false) |
| Key type | Any | Boolean |
| Use case | Category grouping | Binary classification |

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// partitioningBy - two groups
Map<Boolean, List<Integer>> evenOdd = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// {false=[1,3,5,7,9], true=[2,4,6,8,10]}

// With downstream collector
Map<Boolean, Long> evenOddCount = numbers.stream()
    .collect(Collectors.partitioningBy(
        n -> n % 2 == 0,
        Collectors.counting()
    ));
// {false=5, true=5}

// Example: Separate passing/failing students
Map<Boolean, List<Student>> passedFailed = students.stream()
    .collect(Collectors.partitioningBy(s -> s.getScore() >= 60));
// {true=[passed students], false=[failed students]}
```

---

## Q24: How do you handle null values in Streams?

**Answer:**

Streams don't handle null well by default. Here are strategies:

```java
List<String> names = Arrays.asList("John", null, "Jane", null, "Bob");

// Filter out nulls
List<String> nonNull = names.stream()
    .filter(Objects::nonNull)
    .collect(Collectors.toList());
// [John, Jane, Bob]

// Replace nulls with default
List<String> withDefault = names.stream()
    .map(n -> n == null ? "Unknown" : n)
    .collect(Collectors.toList());
// [John, Unknown, Jane, Unknown, Bob]

// Using Optional
List<String> usingOptional = names.stream()
    .map(Optional::ofNullable)
    .map(opt -> opt.orElse("Unknown"))
    .collect(Collectors.toList());

// Safe mapping that might return null
List<String> cities = employees.stream()
    .map(Employee::getAddress)
    .filter(Objects::nonNull)
    .map(Address::getCity)
    .filter(Objects::nonNull)
    .collect(Collectors.toList());
```

---

## Q25: What is CompletableFuture in Java 8? (Intermediate)

**Answer:**

**CompletableFuture** provides asynchronous programming with callbacks and composition.

```java
// Basic async execution
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // Runs in ForkJoinPool
    return fetchDataFromDB();
});

// Get result (blocking)
String result = future.get();

// Non-blocking with callback
future.thenAccept(data -> System.out.println("Got: " + data));

// Chaining
CompletableFuture<Integer> chained = CompletableFuture
    .supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenApply(String::length);

// Combining futures
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> combined = future1.thenCombine(future2, 
    (s1, s2) -> s1 + " " + s2);  // "Hello World"

// Exception handling
CompletableFuture<String> withError = CompletableFuture
    .supplyAsync(() -> {
        if (true) throw new RuntimeException("Error!");
        return "Success";
    })
    .exceptionally(ex -> "Fallback: " + ex.getMessage());
```

---

## Q26: How do you create custom Collectors?

**Answer:**

Create custom collectors using `Collector.of()`:

```java
// Custom collector to join strings with comma
Collector<String, StringJoiner, String> joiningCollector = 
    Collector.of(
        () -> new StringJoiner(", "),      // Supplier
        StringJoiner::add,                   // Accumulator
        StringJoiner::merge,                 // Combiner (for parallel)
        StringJoiner::toString               // Finisher
    );

String result = Stream.of("A", "B", "C")
    .collect(joiningCollector);
// "A, B, C"

// Custom collector for immutable list
Collector<String, ?, List<String>> toImmutableList = 
    Collector.of(
        ArrayList::new,
        ArrayList::add,
        (left, right) -> { left.addAll(right); return left; },
        Collections::unmodifiableList
    );
```

---

## Q27: What is the difference between intermediate and terminal operations?

**Answer:**

| Aspect | Intermediate | Terminal |
|--------|--------------|----------|
| **Returns** | Stream | Non-stream (value, collection) |
| **Execution** | Lazy | Eager (triggers pipeline) |
| **Can chain** | Yes | No (ends pipeline) |
| **Examples** | filter, map, sorted | collect, forEach, count |

```java
// Intermediate operations (nothing happens yet!)
Stream<String> stream = names.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);  // NOT printed!
        return n.length() > 3;
    })
    .map(String::toUpperCase);

System.out.println("Pipeline created");

// Terminal operation triggers everything
List<String> result = stream.collect(Collectors.toList());
// NOW "Filtering: ..." is printed

// Common intermediate operations
.filter()    .map()       .flatMap()
.sorted()    .distinct()  .limit()
.skip()      .peek()      .takeWhile() (Java 9)

// Common terminal operations
.collect()   .forEach()   .count()
.reduce()    .findFirst() .findAny()
.anyMatch()  .allMatch()  .noneMatch()
.toArray()   .min()       .max()
```

---

## Q28: How do you use takeWhile() and dropWhile()? (Java 9+)

**Answer:**

These methods process ordered streams based on predicates.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 4, 3, 2, 1);

// takeWhile - takes elements WHILE predicate is true
List<Integer> taken = numbers.stream()
    .takeWhile(n -> n < 4)
    .collect(Collectors.toList());
// [1, 2, 3] - stops at first 4

// dropWhile - drops elements WHILE predicate is true
List<Integer> dropped = numbers.stream()
    .dropWhile(n -> n < 4)
    .collect(Collectors.toList());
// [4, 5, 4, 3, 2, 1] - drops until first n >= 4

// Comparison with filter
List<Integer> filtered = numbers.stream()
    .filter(n -> n < 4)
    .collect(Collectors.toList());
// [1, 2, 3, 3, 2, 1] - processes ALL elements
```

---

## Q29: What are the differences between Java 8 Date/Time classes?

**Answer:**

Java 8 introduced `java.time` package with immutable, thread-safe classes.

| Class | Represents | Use Case |
|-------|------------|----------|
| `LocalDate` | Date only | Birthdays, holidays |
| `LocalTime` | Time only | Store hours, alarms |
| `LocalDateTime` | Date + Time | Appointments |
| `ZonedDateTime` | Date + Time + Zone | Global scheduling |
| `Instant` | Timestamp | Machine time |
| `Duration` | Time-based amount | Hours, minutes, seconds |
| `Period` | Date-based amount | Days, months, years |

```java
// Creating
LocalDate date = LocalDate.of(2024, 1, 15);
LocalTime time = LocalTime.of(14, 30);
LocalDateTime dateTime = LocalDateTime.of(date, time);
ZonedDateTime zoned = ZonedDateTime.now(ZoneId.of("America/New_York"));
Instant instant = Instant.now();

// Parsing
LocalDate parsed = LocalDate.parse("2024-01-15");
LocalDateTime parsedDT = LocalDateTime.parse("2024-01-15T14:30:00");

// Formatting
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
String formatted = date.format(formatter);  // "15/01/2024"

// Manipulation (immutable - returns new instance)
LocalDate tomorrow = date.plusDays(1);
LocalDate nextMonth = date.plusMonths(1);

// Duration and Period
Duration duration = Duration.between(time1, time2);
Period period = Period.between(date1, date2);
```

---

## Q30: How do you iterate over a Map using Java 8 features?

**Answer:**

```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
map.put("B", 2);
map.put("C", 3);

// forEach with lambda
map.forEach((key, value) -> 
    System.out.println(key + " = " + value));

// Stream over entries
map.entrySet().stream()
    .filter(e -> e.getValue() > 1)
    .forEach(e -> System.out.println(e.getKey()));

// Stream over keys
map.keySet().stream()
    .filter(k -> k.startsWith("A"))
    .forEach(System.out::println);

// Stream over values
int sum = map.values().stream()
    .mapToInt(Integer::intValue)
    .sum();

// Collect to new map
Map<String, Integer> filtered = map.entrySet().stream()
    .filter(e -> e.getValue() > 1)
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue
    ));

// getOrDefault (Java 8)
int value = map.getOrDefault("D", 0);  // 0

// computeIfAbsent
map.computeIfAbsent("D", k -> computeValue(k));

// merge
map.merge("A", 10, Integer::sum);  // A -> 11
```

---

## Q31: What is the difference between a Predicate and a BiPredicate?

**Answer:**

| Interface | Parameters | Method |
|-----------|------------|--------|
| `Predicate<T>` | 1 | `test(T) → boolean` |
| `BiPredicate<T, U>` | 2 | `test(T, U) → boolean` |

```java
// Predicate - single parameter
Predicate<String> isLong = s -> s.length() > 5;
isLong.test("Hello");  // false

// BiPredicate - two parameters
BiPredicate<String, Integer> hasMinLength = (s, len) -> s.length() >= len;
hasMinLength.test("Hello", 3);  // true

// Chaining
BiPredicate<String, Integer> complex = hasMinLength
    .and((s, len) -> s.startsWith("H"));
```

**Other Bi-variants:**
- `BiFunction<T, U, R>` - two inputs, one output
- `BiConsumer<T, U>` - two inputs, no output

---

## Q32: How do you create an infinite Stream? (Intermediate)

**Answer:**

Use `Stream.generate()` or `Stream.iterate()` with `limit()`.

```java
// generate() - uses Supplier
Stream<Double> randoms = Stream.generate(Math::random)
    .limit(5);  // Must limit!

Stream<String> constants = Stream.generate(() -> "Hello")
    .limit(3);
// [Hello, Hello, Hello]

// iterate() - uses initial value and UnaryOperator
Stream<Integer> evens = Stream.iterate(0, n -> n + 2)
    .limit(10);
// [0, 2, 4, 6, 8, 10, 12, 14, 16, 18]

// iterate() with predicate (Java 9+)
Stream<Integer> powers = Stream.iterate(1, n -> n < 1000, n -> n * 2);
// [1, 2, 4, 8, 16, 32, 64, 128, 256, 512]

// Fibonacci sequence
Stream.iterate(new int[]{0, 1}, arr -> new int[]{arr[1], arr[0] + arr[1]})
    .limit(10)
    .map(arr -> arr[0])
    .forEach(System.out::println);
// 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
```

---

## Q33: How do you use Collectors.toMap() with duplicate keys? (Intermediate)

**Answer:**

By default, `toMap()` throws `IllegalStateException` on duplicate keys. Use merge function:

```java
List<Employee> employees = Arrays.asList(
    new Employee("IT", 50000),
    new Employee("IT", 60000),
    new Employee("HR", 55000)
);

// Throws exception on duplicate keys!
Map<String, Integer> wrong = employees.stream()
    .collect(Collectors.toMap(
        Employee::getDepartment,
        Employee::getSalary
    ));  // IllegalStateException!

// Use merge function to handle duplicates
Map<String, Integer> correct = employees.stream()
    .collect(Collectors.toMap(
        Employee::getDepartment,
        Employee::getSalary,
        (existing, replacement) -> existing + replacement  // Sum salaries
    ));
// {IT=110000, HR=55000}

// Keep first value
Map<String, Integer> keepFirst = employees.stream()
    .collect(Collectors.toMap(
        Employee::getDepartment,
        Employee::getSalary,
        (existing, replacement) -> existing  // Keep first
    ));

// Keep last value
Map<String, Integer> keepLast = employees.stream()
    .collect(Collectors.toMap(
        Employee::getDepartment,
        Employee::getSalary,
        (existing, replacement) -> replacement  // Keep last
    ));
```

---

## Q34: What is the Spliterator interface? (Hard)

**Answer:**

**Spliterator** (Splitable Iterator) is designed for parallel traversal of elements. It's the internal mechanism for parallel streams.

```java
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
    Spliterator<T> trySplit();  // For parallelism
    long estimateSize();
    int characteristics();
}
```

### Key Methods:
| Method | Purpose |
|--------|---------|
| `tryAdvance()` | Process one element |
| `trySplit()` | Split for parallel processing |
| `estimateSize()` | Estimate remaining elements |
| `characteristics()` | Properties (ORDERED, SIZED, etc.) |

```java
List<String> list = Arrays.asList("A", "B", "C", "D");

Spliterator<String> spliterator = list.spliterator();

// Get characteristics
System.out.println(spliterator.characteristics());

// Split for parallel processing
Spliterator<String> split = spliterator.trySplit();

// Process elements
spliterator.forEachRemaining(System.out::println);
```

---

## Q35: How do you get stream statistics efficiently?

**Answer:**

Use `summaryStatistics()` for primitives or `summarizingInt/Long/Double` collectors.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Primitive stream statistics
IntSummaryStatistics stats = numbers.stream()
    .mapToInt(Integer::intValue)
    .summaryStatistics();

System.out.println("Count: " + stats.getCount());    // 10
System.out.println("Sum: " + stats.getSum());        // 55
System.out.println("Min: " + stats.getMin());        // 1
System.out.println("Max: " + stats.getMax());        // 10
System.out.println("Avg: " + stats.getAverage());    // 5.5

// Using Collectors
IntSummaryStatistics empStats = employees.stream()
    .collect(Collectors.summarizingInt(Employee::getSalary));

// Individual operations (less efficient - multiple traversals)
int count = (int) numbers.stream().count();
int sum = numbers.stream().mapToInt(i -> i).sum();
int max = numbers.stream().mapToInt(i -> i).max().orElse(0);
```

---

## Q36: What is effectively final? Why is it required in lambdas?

**Answer:**

**Effectively final** means a variable is not modified after initialization, even without the `final` keyword. Lambdas require this for thread safety.

```java
// Effectively final - not modified
String prefix = "Hello, ";  // No final keyword, but never modified
names.forEach(name -> System.out.println(prefix + name));  // OK

// Not effectively final - modified
String message = "Hi";
message = "Hello";  // Modified!
names.forEach(name -> System.out.println(message + name));  // ERROR!

// Why? Lambdas capture variable VALUES, not references
// If variable could change, captured value would be stale
```

### Workarounds:
```java
// Use array/AtomicReference for mutable state
AtomicInteger counter = new AtomicInteger(0);
names.forEach(name -> counter.incrementAndGet());  // OK

// Or use holder objects
int[] count = {0};
names.forEach(name -> count[0]++);  // OK (array reference is final)
```

---

## Q37: How do you collect to unmodifiable collections? (Java 10+)

**Answer:**

```java
List<String> names = Arrays.asList("John", "Jane", "Bob");

// Java 10+ - Collectors.toUnmodifiableList()
List<String> immutable = names.stream()
    .filter(n -> n.length() > 3)
    .collect(Collectors.toUnmodifiableList());

Set<String> immutableSet = names.stream()
    .collect(Collectors.toUnmodifiableSet());

Map<String, Integer> immutableMap = names.stream()
    .collect(Collectors.toUnmodifiableMap(
        s -> s,
        String::length
    ));

// Java 16+ - Stream.toList()
List<String> simpler = names.stream()
    .filter(n -> n.length() > 3)
    .toList();  // Returns unmodifiable list

// Pre-Java 10 workaround
List<String> preJava10 = names.stream()
    .filter(n -> n.length() > 3)
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        Collections::unmodifiableList
    ));
```

---

## Q38: What is the difference between sequential() and parallel()?

**Answer:**

| Method | Processing | Use Case |
|--------|------------|----------|
| `sequential()` | Single thread | Default, order matters |
| `parallel()` | Multiple threads | Large data, CPU-intensive |

```java
List<Integer> numbers = IntStream.rangeClosed(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

// Sequential (default)
long seqStart = System.currentTimeMillis();
numbers.stream()
    .filter(n -> isPrime(n))
    .count();
long seqTime = System.currentTimeMillis() - seqStart;

// Parallel
long parStart = System.currentTimeMillis();
numbers.parallelStream()
    .filter(n -> isPrime(n))
    .count();
long parTime = System.currentTimeMillis() - parStart;

// Switch between modes
numbers.stream()
    .parallel()     // Switch to parallel
    .filter(n -> n > 500000)
    .sequential()   // Switch back to sequential
    .forEach(System.out::println);
```

---

## Q39: How do you use Optional.or() and Optional.stream()? (Java 9+)

**Answer:**

```java
// or() - provide alternative Optional
Optional<String> primary = Optional.empty();
Optional<String> backup = Optional.of("backup");

Optional<String> result = primary.or(() -> backup);
// Optional[backup]

// Chaining or() calls
Optional<String> chained = Optional.<String>empty()
    .or(() -> findInCache())
    .or(() -> findInDatabase())
    .or(() -> Optional.of("default"));

// stream() - convert Optional to Stream
Optional<String> opt = Optional.of("Hello");
Stream<String> stream = opt.stream();  // Stream of 0 or 1 elements

// Useful for flatMapping a list of Optionals
List<Optional<String>> optionals = Arrays.asList(
    Optional.of("A"),
    Optional.empty(),
    Optional.of("B")
);

List<String> values = optionals.stream()
    .flatMap(Optional::stream)
    .collect(Collectors.toList());
// [A, B] - empties are filtered out
```

---

## Q40: What are the performance considerations for Streams? (Hard)

**Answer:**

### 1. Boxing/Unboxing Overhead
```java
// BAD - boxing overhead
long sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// GOOD - use primitive streams
long sum = numbers.stream()
    .mapToLong(Long::longValue)
    .sum();
```

### 2. Short-circuiting Operations
```java
// GOOD - short-circuits at first match
boolean found = largeList.stream()
    .anyMatch(x -> x.equals(target));

// BAD - processes all elements
boolean found = largeList.stream()
    .filter(x -> x.equals(target))
    .count() > 0;
```

### 3. Data Source Matters
```java
// ArrayList - good for parallel (random access)
// LinkedList - poor for parallel (sequential access)
// TreeSet - already sorted, skip sorted()
```

### 4. Avoid Stateful Operations in Parallel
```java
// BAD - non-deterministic order
List<Integer> result = new ArrayList<>();
numbers.parallelStream()
    .forEach(result::add);  // Race condition!

// GOOD - use collect
List<Integer> result = numbers.parallelStream()
    .collect(Collectors.toList());
```

---

## Q41: How do you use Stream.ofNullable()? (Java 9+)

**Answer:**

`Stream.ofNullable()` creates a stream of 0 or 1 elements, handling null gracefully.

```java
// Before Java 9
String value = getNullableValue();
Stream<String> stream = value == null 
    ? Stream.empty() 
    : Stream.of(value);

// Java 9+
Stream<String> stream = Stream.ofNullable(getNullableValue());

// Useful for flatMapping nullable values
List<String> names = Arrays.asList("John", null, "Jane", null);

List<String> nonNull = names.stream()
    .flatMap(Stream::ofNullable)
    .collect(Collectors.toList());
// [John, Jane]

// With method that might return null
List<String> cities = employees.stream()
    .map(Employee::getAddress)
    .flatMap(Stream::ofNullable)
    .map(Address::getCity)
    .flatMap(Stream::ofNullable)
    .collect(Collectors.toList());
```

---

## Q42: What is the teeing collector? (Java 12+)

**Answer:**

`Collectors.teeing()` passes elements to two collectors and combines results.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Calculate both sum and average in one pass
var result = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.summingInt(Integer::intValue),  // Collector 1
        Collectors.averagingInt(Integer::intValue), // Collector 2
        (sum, avg) -> "Sum: " + sum + ", Avg: " + avg // Merger
    ));
// "Sum: 15, Avg: 3.0"

// Get both min and max
var minMax = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.minBy(Integer::compareTo),
        Collectors.maxBy(Integer::compareTo),
        (min, max) -> new int[]{min.orElse(0), max.orElse(0)}
    ));
// [1, 5]

// Partition with counts
var countStats = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.filtering(n -> n % 2 == 0, Collectors.toList()),
        Collectors.filtering(n -> n % 2 != 0, Collectors.toList()),
        (evens, odds) -> Map.of("evens", evens, "odds", odds)
    ));
```

---

## Q43: How do you flatten nested Optional? (Hard)

**Answer:**

Use `flatMap()` to avoid nested `Optional<Optional<T>>`.

```java
// Nested structure
class User {
    Optional<Address> getAddress() { ... }
}
class Address {
    Optional<City> getCity() { ... }
}
class City {
    String getName() { ... }
}

// Without flatMap - nested Optionals
Optional<User> user = findUser(id);
Optional<Optional<Address>> address = user.map(User::getAddress);  // Nested!

// With flatMap - flattened
Optional<String> cityName = findUser(id)
    .flatMap(User::getAddress)
    .flatMap(Address::getCity)
    .map(City::getName);

// Common pattern: safe navigation
String city = findUser(id)
    .flatMap(User::getAddress)
    .flatMap(Address::getCity)
    .map(City::getName)
    .orElse("Unknown");
```

---

## Q44: What is the difference between reduce() variants?

**Answer:**

`reduce()` has three overloaded versions:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 1. reduce(BinaryOperator) - returns Optional
Optional<Integer> sum1 = numbers.stream()
    .reduce((a, b) -> a + b);  // Optional[15]

// Empty stream returns empty Optional
Optional<Integer> empty = Stream.<Integer>empty()
    .reduce((a, b) -> a + b);  // Optional.empty()

// 2. reduce(identity, BinaryOperator) - returns value
Integer sum2 = numbers.stream()
    .reduce(0, (a, b) -> a + b);  // 15

// Empty stream returns identity
Integer emptySum = Stream.<Integer>empty()
    .reduce(0, (a, b) -> a + b);  // 0

// 3. reduce(identity, accumulator, combiner) - for parallel
Integer parallelSum = numbers.parallelStream()
    .reduce(
        0,                  // Identity
        (a, b) -> a + b,    // Accumulator
        (a, b) -> a + b     // Combiner (for parallel)
    );
```

---

## Q45: How do you convert old Date/Calendar to new Date/Time API?

**Answer:**

```java
// java.util.Date to java.time
Date oldDate = new Date();
Instant instant = oldDate.toInstant();
LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
LocalDate ld = ldt.toLocalDate();
ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());

// java.time to java.util.Date
LocalDateTime localDateTime = LocalDateTime.now();
Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

// Calendar conversion
Calendar calendar = Calendar.getInstance();
Instant calInstant = calendar.toInstant();
ZonedDateTime calZdt = calInstant.atZone(calendar.getTimeZone().toZoneId());

// java.sql.Date conversion
java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());
LocalDate fromSql = sqlDate.toLocalDate();

// java.sql.Timestamp conversion
java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());
LocalDateTime fromTimestamp = timestamp.toLocalDateTime();
```

---

## Q46: What is the difference between filter() and takeWhile()/dropWhile()?

**Answer:**

| Method | Behavior |
|--------|----------|
| `filter()` | Checks ALL elements |
| `takeWhile()` | Stops at first false |
| `dropWhile()` | Drops until first false |

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 4, 3, 2, 1);

// filter - processes ALL elements
List<Integer> filtered = numbers.stream()
    .filter(n -> n < 4)
    .collect(Collectors.toList());
// [1, 2, 3, 3, 2, 1] - all < 4

// takeWhile - STOPS at first >= 4
List<Integer> taken = numbers.stream()
    .takeWhile(n -> n < 4)
    .collect(Collectors.toList());
// [1, 2, 3] - stops when it hits 4

// dropWhile - DROPS until first >= 4
List<Integer> dropped = numbers.stream()
    .dropWhile(n -> n < 4)
    .collect(Collectors.toList());
// [4, 5, 4, 3, 2, 1] - starts from 4
```

**Best for sorted/ordered data where you want prefix/suffix operations.**

---

## Q47: How do you use Collectors.filtering() and Collectors.flatMapping()? (Java 9+)

**Answer:**

These collectors are useful as downstream collectors in groupingBy.

```java
List<Employee> employees = getEmployees();

// filtering() as downstream collector
Map<String, List<Employee>> highEarnersByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.filtering(
            e -> e.getSalary() > 50000,
            Collectors.toList()
        )
    ));

// flatMapping() - flatten nested collections
class Employee {
    List<String> getSkills() { ... }
}

Map<String, Set<String>> skillsByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.flatMapping(
            e -> e.getSkills().stream(),
            Collectors.toSet()
        )
    ));
// {IT=[Java, Python, SQL], HR=[Communication, Excel]}
```

---

## Q48: What is a closure in the context of lambdas?

**Answer:**

A **closure** is a lambda that "closes over" variables from its enclosing scope.

```java
public Function<Integer, Integer> createMultiplier(int factor) {
    // factor is captured from enclosing scope
    return x -> x * factor;  // Closure!
}

Function<Integer, Integer> triple = createMultiplier(3);
triple.apply(5);  // 15

// The lambda "remembers" factor = 3 even after createMultiplier() returns

// Another example
String greeting = "Hello";  // Captured variable
Consumer<String> greet = name -> System.out.println(greeting + ", " + name);
greet.accept("John");  // "Hello, John"
```

### Key Points:
- Captured variables must be effectively final
- Lambda captures the VALUE, not a reference to the variable
- Closure enables functional programming patterns

---

## Q49: How do you combine multiple CompletableFutures? (Intermediate)

**Answer:**

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");
CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> 42);

// thenCombine - combine two futures
CompletableFuture<String> combined = future1.thenCombine(future2,
    (s1, s2) -> s1 + " " + s2);  // "Hello World"

// allOf - wait for all to complete
CompletableFuture<Void> all = CompletableFuture.allOf(future1, future2, future3);
all.join();  // Blocks until all complete

// Get results after allOf
String result1 = future1.join();
String result2 = future2.join();
Integer result3 = future3.join();

// anyOf - complete when ANY completes
CompletableFuture<Object> any = CompletableFuture.anyOf(future1, future2);
Object firstResult = any.join();  // First to complete

// Combine list of futures
List<CompletableFuture<String>> futures = Arrays.asList(future1, future2);
CompletableFuture<List<String>> allResults = CompletableFuture.allOf(
        futures.toArray(new CompletableFuture[0]))
    .thenApply(v -> futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList()));
```

---

## Q50: What are the best practices for using Streams? (Hard)

**Answer:**

### 1. Do NOT reuse streams
```java
Stream<String> stream = list.stream();
stream.count();
stream.collect(Collectors.toList());  // ERROR! Stream already closed
```

### 2. Prefer method references when possible
```java
// Lambda
list.stream().map(s -> s.toUpperCase())

// Method reference (preferred)
list.stream().map(String::toUpperCase)
```

### 3. Use primitive streams for numbers
```java
// Avoid boxing
IntStream.range(0, 100).sum();
```

### 4. Be careful with infinite streams
```java
Stream.generate(Math::random)
    .limit(10)  // Always limit!
    .forEach(System.out::println);
```

### 5. Don't use streams for everything
```java
// Simple loop is better for single element check
for (String s : list) {
    if (s.equals(target)) return s;
}

// Good for complex operations
list.stream()
    .filter(s -> s.length() > 5)
    .map(String::toUpperCase)
    .sorted()
    .collect(Collectors.toList());
```

### 6. Avoid side effects
```java
// BAD
List<String> results = new ArrayList<>();
stream.forEach(results::add);

// GOOD
List<String> results = stream.collect(Collectors.toList());
```

---

## Summary

| Feature | Key Point |
|---------|-----------|
| **Lambda** | `(params) -> expression` for functional interfaces |
| **Functional Interface** | Interface with single abstract method |
| **Stream API** | Functional operations on collections |
| **map vs flatMap** | One-to-one vs flatten nested structures |
| **Optional** | Container to avoid NullPointerException |
| **Method Reference** | `Class::method` shorthand for lambdas |
| **Default Methods** | Interface methods with implementation |
| **Date/Time API** | Immutable, thread-safe `java.time` classes |
| **Collectors** | Utility for Stream collect operations |

---

> **Next Topic:** Core Java
