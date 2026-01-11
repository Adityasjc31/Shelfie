# ☕ Topic 6: Core Java - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Core Java concepts including Collections, Multithreading, Exception Handling, String, and Memory Management.

---

## Q1: What is the difference between == and equals() in Java?

**Answer:**

**==** compares object references (memory addresses), while **equals()** compares object content (if overridden).

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       == vs equals()                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   == (Reference Comparison)                                              │
│   ─────────────────────────                                              │
│   Compares if two references point to the SAME object in memory        │
│                                                                          │
│   String a = new String("Hello");                                       │
│   String b = new String("Hello");                                       │
│   String c = a;                                                         │
│                                                                          │
│   ┌─────────────────┐     ┌─────────────────┐                           │
│   │ a ──────────────┼────▶│ "Hello" (0x100) │                           │
│   └─────────────────┘     └─────────────────┘                           │
│                                                                          │
│   ┌─────────────────┐     ┌─────────────────┐                           │
│   │ b ──────────────┼────▶│ "Hello" (0x200) │  ← Different object!     │
│   └─────────────────┘     └─────────────────┘                           │
│                                                                          │
│   ┌─────────────────┐                                                    │
│   │ c ──────────────┼────▶ Same as 'a' (0x100)                          │
│   └─────────────────┘                                                    │
│                                                                          │
│   a == b  → false (different objects)                                   │
│   a == c  → true  (same object)                                         │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   equals() (Content Comparison)                                          │
│   ─────────────────────────────                                          │
│   Compares if two objects have the SAME content                         │
│   (when overridden - default behavior is same as ==)                    │
│                                                                          │
│   a.equals(b)  → true  (same content "Hello")                           │
│   a.equals(c)  → true  (same content "Hello")                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### String Pool Special Case:

```java
// String literals use String Pool (interning)
String s1 = "Hello";  // From pool
String s2 = "Hello";  // Same object from pool
String s3 = new String("Hello");  // New object on heap

s1 == s2   // true (same pooled object)
s1 == s3   // false (different objects)
s1.equals(s3)  // true (same content)
```

### Overriding equals():

```java
public class User {
    private Long id;
    private String email;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Same reference
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);  // Must override hashCode too!
    }
}
```

### Key Points:
| Aspect | == | equals() |
|--------|----|---------| 
| Compares | References | Content (if overridden) |
| Primitives | Works | Not applicable |
| Objects | Reference check | Content check |
| Can be overridden | No | Yes |
| Null-safe | Yes | No (NPE if null) |

---

## Q2: Explain the Java Collection Framework hierarchy.

**Answer:**

The **Java Collection Framework** provides a unified architecture for storing and manipulating groups of objects.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    COLLECTION FRAMEWORK HIERARCHY                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                           Iterable<E>                                    │
│                               │                                          │
│                         Collection<E>                                    │
│                               │                                          │
│         ┌───────────────┬─────┴─────────┬──────────────────┐            │
│         │               │               │                  │            │
│         ▼               ▼               ▼                  ▼            │
│      List<E>         Set<E>        Queue<E>           Map<K,V>          │
│   (Ordered,       (Unique,      (FIFO/Priority)    (Key-Value)         │
│    Indexed)       No duplicates)                                        │
│         │               │               │                  │            │
│    ┌────┴────┐     ┌────┴────┐     ┌────┴────┐      ┌─────┴────┐       │
│    │         │     │         │     │         │      │          │       │
│    ▼         ▼     ▼         ▼     ▼         ▼      ▼          ▼       │
│ ArrayList LinkedList HashSet TreeSet PriorityQueue HashMap TreeMap    │
│ Vector            LinkedHashSet      LinkedList  LinkedHashMap        │
│ Stack                                ArrayDeque  ConcurrentHashMap    │
│ CopyOnWriteArrayList                 Deque                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### List Implementations:

| Implementation | Backing | Access | Insert/Delete | Thread-Safe |
|---------------|---------|--------|---------------|-------------|
| **ArrayList** | Array | O(1) | O(n) | No |
| **LinkedList** | Doubly-linked | O(n) | O(1) | No |
| **Vector** | Array | O(1) | O(n) | Yes (synchronized) |
| **CopyOnWriteArrayList** | Array | O(1) | O(n) | Yes |

### Set Implementations:

| Implementation | Ordering | Null | Performance |
|---------------|----------|------|-------------|
| **HashSet** | No order | 1 null | O(1) add/remove/contains |
| **LinkedHashSet** | Insertion order | 1 null | O(1) |
| **TreeSet** | Sorted (natural/comparator) | No null | O(log n) |

### Map Implementations:

| Implementation | Ordering | Null Key | Thread-Safe |
|---------------|----------|----------|-------------|
| **HashMap** | No order | 1 null | No |
| **LinkedHashMap** | Insertion order | 1 null | No |
| **TreeMap** | Sorted | No null | No |
| **Hashtable** | No order | No null | Yes (legacy) |
| **ConcurrentHashMap** | No order | No null | Yes |

### When to Use What:

```java
// Need fast random access → ArrayList
List<String> names = new ArrayList<>();

// Frequent insertions/deletions → LinkedList
List<String> queue = new LinkedList<>();

// Unique elements, no order → HashSet
Set<String> uniqueEmails = new HashSet<>();

// Unique elements, sorted → TreeSet
Set<Integer> sortedNumbers = new TreeSet<>();

// Key-value pairs, fast lookup → HashMap
Map<String, User> usersByEmail = new HashMap<>();

// Key-value pairs, sorted by key → TreeMap
Map<Integer, String> sortedById = new TreeMap<>();

// Thread-safe map → ConcurrentHashMap
Map<String, Integer> counters = new ConcurrentHashMap<>();
```

---

## Q3: What is the difference between ArrayList and LinkedList?

**Answer:**

**ArrayList** uses a dynamic array, while **LinkedList** uses a doubly-linked list structure.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ArrayList vs LinkedList                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ARRAYLIST (Dynamic Array):                                             │
│   ┌───┬───┬───┬───┬───┬───┬───┬───┐                                     │
│   │ A │ B │ C │ D │ E │   │   │   │  ← Contiguous memory               │
│   └───┴───┴───┴───┴───┴───┴───┴───┘                                     │
│     0   1   2   3   4    (empty slots for growth)                       │
│                                                                          │
│   • Random access: list.get(3) → Jump directly to index 3 → O(1)       │
│   • Insert at middle: Shift all elements right → O(n)                  │
│   • Memory: Uses less memory (no node overhead)                         │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   LINKEDLIST (Doubly-Linked List):                                       │
│   ┌───────┐    ┌───────┐    ┌───────┐    ┌───────┐                      │
│   │ A     │ ←→ │ B     │ ←→ │ C     │ ←→ │ D     │                      │
│   │ prev  │    │ prev  │    │ prev  │    │ prev  │                      │
│   │ next  │    │ next  │    │ next  │    │ next  │                      │
│   └───────┘    └───────┘    └───────┘    └───────┘                      │
│                                                                          │
│   • Random access: Traverse from head/tail → O(n)                       │
│   • Insert at middle: Just update prev/next pointers → O(1)*           │
│   • Memory: More memory (prev + next pointers per node)                │
│   * Finding position is O(n), insertion itself is O(1)                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Performance Comparison:

| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| **get(index)** | O(1) ✅ | O(n) |
| **add(element)** at end | O(1) amortized | O(1) ✅ |
| **add(index, element)** | O(n) | O(n)* |
| **remove(index)** | O(n) | O(n)* |
| **contains(element)** | O(n) | O(n) |
| **Iterator.remove()** | O(n) | O(1) ✅ |
| **Memory** | Less ✅ | More |

*O(n) to find position, O(1) to insert/remove

### When to Use:

```java
// Use ArrayList when:
// - Frequent random access (get by index)
// - Mostly adding at the end
// - Memory is a concern
List<String> products = new ArrayList<>();  // ✅ Default choice

// Use LinkedList when:
// - Frequent insertions/deletions at beginning/middle
// - Implementing Queues/Deques
// - Iterator-based removal
Queue<Task> taskQueue = new LinkedList<>();
Deque<String> deque = new LinkedList<>();
```

---

## Q4: Explain HashMap internals. How does it work?

**Answer:**

**HashMap** stores key-value pairs using a hash table with buckets. It uses `hashCode()` to find the bucket and `equals()` to find the exact entry.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       HASHMAP INTERNALS                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   When you do: map.put("John", user)                                    │
│                                                                          │
│   1. Calculate hash:     "John".hashCode() → 2314539                    │
│   2. Calculate bucket:   hash % buckets.length → 5 (example)            │
│   3. Store in bucket 5                                                   │
│                                                                          │
│   BUCKET ARRAY (Node<K,V>[]):                                            │
│   ┌────────────────────────────────────────────────────────────────────┐│
│   │ Index │  Content                                                   ││
│   ├───────┼────────────────────────────────────────────────────────────┤│
│   │   0   │  null                                                      ││
│   │   1   │  null                                                      ││
│   │   2   │  [Amy→User] → [Bob→User]  ← Collision: linked list        ││
│   │   3   │  null                                                      ││
│   │   4   │  [Cat→User]                                                ││
│   │   5   │  [John→User]                                               ││
│   │   6   │  null                                                      ││
│   │   7   │  [Dog→User] → [Eve→User] → ... (8+ nodes → Tree)          ││
│   │  ...  │  ...                                                       ││
│   └───────┴────────────────────────────────────────────────────────────┘│
│                                                                          │
│   COLLISION HANDLING:                                                    │
│   - Before Java 8: Always linked list                                   │
│   - Java 8+: Linked list → Red-Black tree (when > 8 nodes)             │
│              O(n) → O(log n) for lookups in large buckets              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Put Operation:

```java
map.put("John", user);

// Simplified internal process:
1. int hash = hash("John".hashCode());
2. int bucket = hash & (table.length - 1);  // Find bucket
3. Node<K,V> node = table[bucket];

4. if (node == null) {
       // Empty bucket - add new node
       table[bucket] = new Node<>(hash, "John", user, null);
   } else {
       // Bucket has nodes - traverse
       while (node != null) {
           if (node.hash == hash && node.key.equals("John")) {
               // Key exists - update value
               node.value = user;
               return;
           }
           node = node.next;
       }
       // Key not found - add to chain
       addToChain(bucket, new Node<>(hash, "John", user, null));
   }

5. if (++size > threshold) {
       resize();  // Double capacity when load factor exceeded
   }
```

### Key Concepts:

| Concept | Default | Description |
|---------|---------|-------------|
| **Initial Capacity** | 16 | Number of buckets |
| **Load Factor** | 0.75 | When to resize (75% full) |
| **Threshold** | capacity × loadFactor | Resize trigger |
| **TREEIFY_THRESHOLD** | 8 | Convert to tree above this |
| **UNTREEIFY_THRESHOLD** | 6 | Convert back to list below this |

### hashCode() and equals() Contract:

```java
// RULE: If two objects are equal, they MUST have same hashCode
// If obj1.equals(obj2) → true
// Then obj1.hashCode() must == obj2.hashCode()

// Breaking this rule causes HashMap to fail!
class BadKey {
    String value;
    
    @Override
    public boolean equals(Object o) {
        return value.equals(((BadKey) o).value);
    }
    
    // ❌ MISSING hashCode() override!
    // Two equal keys might have different hashCodes
    // HashMap will put them in different buckets!
}
```

---

## Q5: What is the difference between final, finally, and finalize?

**Answer:**

Three completely different concepts sharing similar names:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    final vs finally vs finalize                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. FINAL (Keyword - Restriction)                                       │
│   ──────────────────────────────────                                     │
│   Makes things unchangeable                                              │
│                                                                          │
│   final variable:  Cannot be reassigned                                  │
│   final method:    Cannot be overridden                                  │
│   final class:     Cannot be extended (inherited)                        │
│                                                                          │
│   final int MAX = 100;          // Constant                             │
│   final List<String> list;      // Reference can't change, content can │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. FINALLY (Block - Exception Handling)                                │
│   ─────────────────────────────────────────                              │
│   Code that ALWAYS executes after try/catch                             │
│                                                                          │
│   try {                                                                  │
│       // Code that might throw exception                                │
│   } catch (Exception e) {                                               │
│       // Handle exception                                               │
│   } finally {                                                           │
│       // ALWAYS executes (cleanup: close files, connections)            │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. FINALIZE (Method - Deprecated ⚠️)                                   │
│   ─────────────────────────────────────                                  │
│   Called by garbage collector before destroying object                   │
│   DEPRECATED since Java 9 - Don't use!                                  │
│                                                                          │
│   @Override                                                              │
│   protected void finalize() throws Throwable {                           │
│       // Cleanup before GC (unreliable!)                                │
│       super.finalize();                                                  │
│   }                                                                      │
│                                                                          │
│   Use try-with-resources or Cleaner instead                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Final Examples:

```java
// final variable - constant
final int MAX_SIZE = 100;
MAX_SIZE = 200;  // ❌ Compile error

// final reference - can modify object, not reference
final List<String> list = new ArrayList<>();
list.add("Hello");   // ✅ OK - modifying content
list = new ArrayList<>();  // ❌ Compile error - reassigning reference

// final method - cannot override
class Parent {
    public final void display() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    @Override
    public void display() { }  // ❌ Compile error
}

// final class - cannot extend
final class ImmutableClass { }

class SubClass extends ImmutableClass { }  // ❌ Compile error
```

### Finally Examples:

```java
// finally always executes
public String readFile(String path) {
    FileReader reader = null;
    try {
        reader = new FileReader(path);
        return reader.read();
    } catch (IOException e) {
        return "Error reading file";
    } finally {
        // Always close the reader
        if (reader != null) {
            try { reader.close(); } catch (IOException e) { }
        }
    }
}

// Better: try-with-resources (Java 7+)
public String readFile(String path) {
    try (FileReader reader = new FileReader(path)) {
        return reader.read();  // Auto-closes in finally
    } catch (IOException e) {
        return "Error reading file";
    }
}
```

---

## Q6: Explain Exception Handling in Java. Checked vs Unchecked exceptions.

**Answer:**

Java exceptions are divided into two categories based on when they're checked.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    EXCEPTION HIERARCHY                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                          Throwable                                       │
│                              │                                           │
│              ┌───────────────┴───────────────┐                          │
│              │                               │                          │
│           Error                          Exception                       │
│       (Don't catch)                          │                          │
│              │                   ┌───────────┴───────────┐              │
│              │                   │                       │              │
│       OutOfMemoryError    RuntimeException        Checked Exceptions    │
│       StackOverflowError  (Unchecked)             (Must handle)         │
│       VirtualMachineError       │                       │               │
│              │                  │                       │               │
│              │           NullPointerException   IOException             │
│              │           IllegalArgumentException SQLException          │
│              │           IndexOutOfBoundsException FileNotFoundException│
│              │           ClassCastException      ParseException         │
│              │           ArithmeticException                            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Checked vs Unchecked:

```
┌─────────────────────────────────────────────────────────────────────────┐
│              CHECKED vs UNCHECKED EXCEPTIONS                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   CHECKED EXCEPTIONS:                                                    │
│   ───────────────────                                                    │
│   • Checked at COMPILE time                                             │
│   • MUST be handled (try-catch) or declared (throws)                    │
│   • Usually recoverable errors                                          │
│   • Extend Exception (but not RuntimeException)                         │
│                                                                          │
│   public void readFile() throws IOException {  // Must declare         │
│       FileReader reader = new FileReader("file.txt");                   │
│   }                                                                      │
│                                                                          │
│   // OR handle it                                                       │
│   public void readFile() {                                              │
│       try {                                                              │
│           FileReader reader = new FileReader("file.txt");               │
│       } catch (IOException e) {                                         │
│           e.printStackTrace();                                          │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   UNCHECKED EXCEPTIONS (Runtime):                                        │
│   ──────────────────────────────                                         │
│   • Checked at RUNTIME only                                             │
│   • Don't need to be declared or caught                                 │
│   • Usually programming errors (bugs)                                   │
│   • Extend RuntimeException                                              │
│                                                                          │
│   public int divide(int a, int b) {                                     │
│       return a / b;  // ArithmeticException if b=0                      │
│   }                  // No throws declaration needed                    │
│                                                                          │
│   String s = null;                                                      │
│   s.length();  // NullPointerException at runtime                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Exception Handling Best Practices:

```java
// 1. Catch specific exceptions first
try {
    // risky code
} catch (FileNotFoundException e) {  // More specific first
    // handle file not found
} catch (IOException e) {  // More general later
    // handle other IO errors
}

// 2. Don't catch and ignore
try {
    riskyOperation();
} catch (Exception e) {
    // ❌ BAD - Silent failure
}

// 3. Log with context
try {
    processOrder(orderId);
} catch (OrderException e) {
    log.error("Failed to process order {}: {}", orderId, e.getMessage());
    throw e;  // Re-throw after logging
}

// 4. Use try-with-resources for auto-closing
try (Connection conn = dataSource.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // use connection
}  // Auto-closes even on exception

// 5. Create custom exceptions
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("Book not found with ID: " + bookId);
    }
}
```

---

## Q7: What is Multithreading? Explain Thread creation and lifecycle.

**Answer:**

**Multithreading** allows concurrent execution of multiple threads within a program.

### Thread Creation:

```java
// Method 1: Extend Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + getName());
    }
}
MyThread t1 = new MyThread();
t1.start();  // NOT run() - start() creates new thread

// Method 2: Implement Runnable (PREFERRED)
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}
Thread t2 = new Thread(new MyRunnable());
t2.start();

// Method 3: Lambda (Java 8+)
Thread t3 = new Thread(() -> System.out.println("Lambda thread"));
t3.start();

// Method 4: ExecutorService (RECOMMENDED for production)
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> System.out.println("Task executed"));
executor.shutdown();
```

### Thread Lifecycle:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       THREAD LIFECYCLE                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                    ┌──────────────┐                                     │
│      new Thread()  │     NEW      │                                     │
│                    └──────┬───────┘                                     │
│                           │ start()                                     │
│                           ▼                                              │
│                    ┌──────────────┐                                     │
│                    │   RUNNABLE   │ ←────────────┐                      │
│                    │ (Ready/Running)             │                      │
│                    └──────┬───────┘              │                      │
│                           │                      │                      │
│         ┌─────────────────┼──────────────────────┤                      │
│         │                 │                      │                      │
│         ▼                 ▼                      │                      │
│   ┌───────────┐    ┌───────────┐    ┌───────────┐                      │
│   │  BLOCKED  │    │  WAITING  │    │  TIMED    │                      │
│   │           │    │           │    │  WAITING  │                      │
│   └─────┬─────┘    └─────┬─────┘    └─────┬─────┘                      │
│         │                │                │                             │
│         │ lock acquired  │ notify()       │ timeout/notify             │
│         └────────────────┴────────────────┴────────▶ Back to RUNNABLE  │
│                                                                          │
│                    ┌──────────────┐                                     │
│       run() ends   │  TERMINATED  │                                     │
│                    └──────────────┘                                     │
│                                                                          │
│   STATE TRANSITIONS:                                                    │
│   - NEW → RUNNABLE: start() called                                      │
│   - RUNNABLE → BLOCKED: waiting for lock                                │
│   - RUNNABLE → WAITING: wait(), join() called                          │
│   - RUNNABLE → TIMED_WAITING: sleep(ms), wait(ms)                      │
│   - * → TERMINATED: run() completes or exception                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Thread Methods:

| Method | Description |
|--------|-------------|
| `start()` | Starts thread execution |
| `run()` | Contains thread code |
| `sleep(ms)` | Pauses for specified time |
| `join()` | Wait for thread to complete |
| `yield()` | Hint to scheduler to pause |
| `interrupt()` | Interrupts sleeping/waiting thread |
| `isAlive()` | Check if thread is running |

---

## Q8: What is String Pool? Difference between String, StringBuilder, StringBuffer?

**Answer:**

### String Pool:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       STRING POOL                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   HEAP MEMORY:                                                           │
│   ┌─────────────────────────────────────────────────────────────────────┐
│   │                                                                      │
│   │   String Pool (Special area in heap):                               │
│   │   ┌─────────────────────────────────────────────────────────────┐   │
│   │   │  "Hello"  │  "World"  │  "Java"  │  ...                    │   │
│   │   └─────────────────────────────────────────────────────────────┘   │
│   │         ▲                                                           │
│   │         │                                                           │
│   │   String s1 = "Hello";  ← Uses pooled string                       │
│   │   String s2 = "Hello";  ← Same pooled string (s1 == s2 is true)   │
│   │                                                                      │
│   │   Regular Heap:                                                     │
│   │   ┌─────────────────────────────────────────────────────────────┐   │
│   │   │  new String("Hello")  │  new String("Hello")  │  ...        │   │
│   │   └─────────────────────────────────────────────────────────────┘   │
│   │         ▲                          ▲                                │
│   │         │                          │                                │
│   │   String s3 = new String("Hello"); ← New object on heap           │
│   │   String s4 = new String("Hello"); ← Different new object         │
│   │                                                                      │
│   │   s1 == s2  // true (same pooled object)                            │
│   │   s3 == s4  // false (different heap objects)                       │
│   │   s1 == s3  // false (pool vs heap)                                 │
│   │   s3.intern() == s1  // true (intern() returns pooled version)     │
│   │                                                                      │
│   └─────────────────────────────────────────────────────────────────────┘
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### String vs StringBuilder vs StringBuffer:

```
┌─────────────────────────────────────────────────────────────────────────┐
│           String vs StringBuilder vs StringBuffer                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   String (IMMUTABLE):                                                    │
│   ───────────────────                                                    │
│   String s = "Hello";                                                   │
│   s = s + " World";  // Creates NEW String object!                      │
│                                                                          │
│   Memory: "Hello" (still exists) + "Hello World" (new)                  │
│                                                                          │
│   ❌ Inefficient for many concatenations (creates garbage)              │
│   ✅ Thread-safe (immutable)                                            │
│   ✅ Safe for HashMap keys                                              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   StringBuilder (MUTABLE, NOT thread-safe):                              │
│   ──────────────────────────────────────────                             │
│   StringBuilder sb = new StringBuilder("Hello");                        │
│   sb.append(" World");  // Modifies SAME object                         │
│                                                                          │
│   ✅ Efficient for many modifications                                   │
│   ✅ Faster than StringBuffer (no synchronization)                      │
│   ❌ NOT thread-safe                                                    │
│   Use when: Single-threaded, many modifications                         │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   StringBuffer (MUTABLE, thread-safe):                                   │
│   ────────────────────────────────────                                   │
│   StringBuffer sb = new StringBuffer("Hello");                          │
│   sb.append(" World");  // Modifies SAME object (synchronized)          │
│                                                                          │
│   ✅ Thread-safe (synchronized methods)                                 │
│   ❌ Slower than StringBuilder                                          │
│   Use when: Multi-threaded, shared mutable string                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Performance Comparison:

```java
// ❌ BAD - String concatenation in loop (creates many objects)
String result = "";
for (int i = 0; i < 10000; i++) {
    result += i;  // Creates new String each iteration!
}

// ✅ GOOD - StringBuilder for loops
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);  // Modifies same object
}
String result = sb.toString();
```

### Summary:

| Feature | String | StringBuilder | StringBuffer |
|---------|--------|---------------|--------------|
| **Mutability** | Immutable | Mutable | Mutable |
| **Thread-safe** | Yes (immutable) | No | Yes |
| **Performance** | Slowest (many ops) | Fastest | Moderate |
| **Use case** | Few operations | Single-threaded | Multi-threaded |

---

## Q9: What is the difference between synchronized and volatile?

**Answer:**

Both are used for thread safety but in different ways:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    synchronized vs volatile                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   VOLATILE:                                                              │
│   ─────────                                                              │
│   Ensures visibility - all threads see the latest value                 │
│   Does NOT ensure atomicity for compound operations                     │
│                                                                          │
│   private volatile boolean running = true;                              │
│                                                                          │
│   Thread 1:                    Thread 2:                                │
│   while(running) {             running = false;  // Change visible     │
│       doWork();                // immediately to Thread 1               │
│   }                                                                      │
│                                                                          │
│   ❌ NOT atomic:                                                        │
│   volatile int count = 0;                                               │
│   count++;  // Read-Modify-Write: NOT atomic!                           │
│             // Thread 1 reads 0                                         │
│             // Thread 2 reads 0                                         │
│             // Both write 1 (should be 2)                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   SYNCHRONIZED:                                                          │
│   ─────────────                                                          │
│   Ensures visibility AND atomicity (mutual exclusion)                   │
│   Only one thread can execute synchronized block at a time              │
│                                                                          │
│   private int count = 0;                                                │
│                                                                          │
│   public synchronized void increment() {                                │
│       count++;  // Now atomic! Only one thread at a time               │
│   }                                                                      │
│                                                                          │
│   // Or synchronized block                                              │
│   public void increment() {                                             │
│       synchronized(this) {                                               │
│           count++;                                                       │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   ┌─────────────────────────────────────────┐                           │
│   │ Thread 1 acquires lock                  │                           │
│   │     counts++                            │                           │
│   │ Thread 1 releases lock                  │                           │
│   │     Thread 2 acquires lock              │                           │
│   │         count++                         │                           │
│   │     Thread 2 releases lock              │                           │
│   │         Result: count = 2 ✅            │                           │
│   └─────────────────────────────────────────┘                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Comparison:

| Aspect | volatile | synchronized |
|--------|----------|--------------|
| **Visibility** | ✅ Yes | ✅ Yes |
| **Atomicity** | ❌ No | ✅ Yes |
| **Blocking** | ❌ No | ✅ Yes |
| **Performance** | Faster | Slower |
| **Use case** | Flags, single read/write | Compound operations |

### When to Use:

```java
// Use volatile for:
// - Simple flags
private volatile boolean shutdown = false;

// - Status checks
private volatile Status currentStatus = Status.RUNNING;


// Use synchronized for:
// - Compound operations (read-modify-write)
public synchronized void transfer(Account from, Account to, int amount) {
    from.withdraw(amount);
    to.deposit(amount);
}

// - Multiple shared variables that need consistent view
public synchronized void updateUserProfile(String name, String email) {
    this.name = name;
    this.email = email;
}


// Better alternative: java.util.concurrent
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();  // Thread-safe, no synchronization needed
```

---

## Q10: What is the difference between Comparable and Comparator?

**Answer:**

Both are used for sorting objects but with different approaches:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    Comparable vs Comparator                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   COMPARABLE (java.lang.Comparable<T>):                                  │
│   ──────────────────────────────────────                                 │
│   • Defines NATURAL ordering                                            │
│   • Implemented BY the class itself                                     │
│   • Single sorting sequence only                                        │
│   • Method: compareTo(T other)                                          │
│                                                                          │
│   public class Employee implements Comparable<Employee> {               │
│       private String name;                                               │
│       private int salary;                                                │
│                                                                          │
│       @Override                                                          │
│       public int compareTo(Employee other) {                            │
│           return this.name.compareTo(other.name);  // Natural: by name │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   // Usage                                                               │
│   Collections.sort(employees);  // Uses natural ordering                │
│   Arrays.sort(employeeArray);   // Uses natural ordering                │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   COMPARATOR (java.util.Comparator<T>):                                  │
│   ─────────────────────────────────────                                  │
│   • Defines CUSTOM ordering                                             │
│   • Separate class (not in the object)                                  │
│   • Multiple sorting sequences possible                                 │
│   • Method: compare(T o1, T o2)                                         │
│                                                                          │
│   // Separate comparator classes                                        │
│   public class SalaryComparator implements Comparator<Employee> {       │
│       @Override                                                          │
│       public int compare(Employee e1, Employee e2) {                    │
│           return Integer.compare(e1.getSalary(), e2.getSalary());       │
│       }                                                                  │
│   }                                                                      │
│                                                                          │
│   // Usage                                                               │
│   Collections.sort(employees, new SalaryComparator());                  │
│                                                                          │
│   // Or lambda (Java 8+)                                                 │
│   employees.sort((e1, e2) -> e1.getSalary() - e2.getSalary());          │
│                                                                          │
│   // Or method reference                                                │
│   employees.sort(Comparator.comparing(Employee::getSalary));            │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Practical Examples:

```java
// Multiple Comparators for same class
List<Employee> employees = getEmployees();

// Sort by name (ascending)
employees.sort(Comparator.comparing(Employee::getName));

// Sort by salary (descending)
employees.sort(Comparator.comparing(Employee::getSalary).reversed());

// Sort by department, then by name
employees.sort(Comparator
    .comparing(Employee::getDepartment)
    .thenComparing(Employee::getName));

// Sort by salary descending, then by name ascending
employees.sort(Comparator
    .comparing(Employee::getSalary).reversed()
    .thenComparing(Employee::getName));

// Null-safe comparator
employees.sort(Comparator
    .comparing(Employee::getManager, Comparator.nullsLast(String::compareTo)));
```

### Comparison:

| Aspect | Comparable | Comparator |
|--------|------------|------------|
| **Package** | java.lang | java.util |
| **Method** | compareTo(T) | compare(T, T) |
| **Modification** | Modify class | Separate class |
| **Sorting sequences** | One (natural) | Multiple |
| **Best for** | Default ordering | Custom/multiple orderings |

---

## Q11: What are the four pillars of Object-Oriented Programming (OOP)?

**Answer:**

| Pillar | Description | Example |
|--------|-------------|---------|
| **Encapsulation** | Bundling data and methods, hiding internal state | private fields + public getters/setters |
| **Abstraction** | Hiding complex implementation, showing only essentials | Abstract classes, interfaces |
| **Inheritance** | Child class inherits from parent class | `class Dog extends Animal` |
| **Polymorphism** | Same interface, different implementations | Method overriding, method overloading |

```java
// Encapsulation
public class BankAccount {
    private double balance;  // Hidden
    public double getBalance() { return balance; }
}

// Abstraction
public abstract class Shape {
    public abstract double area();  // What, not how
}

// Inheritance
public class Circle extends Shape {
    private double radius;
    @Override
    public double area() { return Math.PI * radius * radius; }
}

// Polymorphism
Shape s = new Circle();  // Parent reference, child object
s.area();  // Calls Circle's implementation
```

---

## Q12: What is the difference between JDK, JRE, and JVM?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────┐
│                           JDK                                    │
│   (Java Development Kit - for developers)                        │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │ javac (compiler), javadoc, jar, jdb (debugger)          │   │
│   │                                                          │   │
│   │               JRE                                        │   │
│   │   (Java Runtime Environment - to run Java apps)          │   │
│   │   ┌──────────────────────────────────────────────────┐  │   │
│   │   │ Libraries (rt.jar, etc.)                          │  │   │
│   │   │                                                    │  │   │
│   │   │            JVM                                     │  │   │
│   │   │   (Java Virtual Machine - executes bytecode)       │  │   │
│   │   │   ┌────────────────────────────────────────────┐  │  │   │
│   │   │   │ Class Loader, Memory Areas, Execution Engine│  │  │   │
│   │   │   └────────────────────────────────────────────┘  │  │   │
│   │   └──────────────────────────────────────────────────┘  │   │
│   └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

| Component | Purpose | Contains |
|-----------|---------|----------|
| **JVM** | Executes bytecode | ClassLoader, Memory, GC |
| **JRE** | Run Java programs | JVM + libraries |
| **JDK** | Develop Java programs | JRE + compiler + tools |

---

## Q13: What is method overloading vs method overriding?

**Answer:**

| Aspect | Overloading | Overriding |
|--------|-------------|------------|
| **Definition** | Same method name, different parameters | Same signature in child class |
| **Binding** | Compile-time (static) | Runtime (dynamic) |
| **Class** | Same class | Parent-child classes |
| **Return type** | Can differ | Must be same or covariant |
| **Access** | Can change | Cannot be more restrictive |

```java
// OVERLOADING (compile-time polymorphism)
class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }  // Different params
    int add(int a, int b, int c) { return a + b + c; }  // Different count
}

// OVERRIDING (runtime polymorphism)
class Animal {
    void sound() { System.out.println("Some sound"); }
}
class Dog extends Animal {
    @Override
    void sound() { System.out.println("Bark"); }  // Same signature
}
```

---

## Q14: What is the difference between abstract class and interface?

**Answer:**

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| **Methods** | Abstract + concrete | Abstract (default/static since Java 8) |
| **Variables** | Any type | public static final only |
| **Inheritance** | Single (extends) | Multiple (implements) |
| **Constructor** | Can have | Cannot have |
| **Access modifiers** | Any | public only (for methods) |

```java
// Abstract class - partial implementation, IS-A relationship
abstract class Animal {
    String name;  // Instance variable
    
    Animal(String name) { this.name = name; }  // Constructor
    
    abstract void sound();  // Abstract method
    void sleep() { System.out.println("Sleeping"); }  // Concrete method
}

// Interface - contract, CAN-DO relationship
interface Flyable {
    int MAX_SPEED = 100;  // public static final
    
    void fly();  // public abstract
    default void land() { System.out.println("Landing"); }  // Java 8+
}

// Usage
class Bird extends Animal implements Flyable {
    Bird(String name) { super(name); }
    void sound() { System.out.println("Chirp"); }
    public void fly() { System.out.println("Flying"); }
}
```

---

## Q15: What is Java Garbage Collection? How does it work?

**Answer:**

**Garbage Collection (GC)** automatically reclaims memory by removing unreachable objects.

```
┌─────────────────────────────────────────────────────────────────┐
│                      HEAP MEMORY                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Young Generation (Minor GC - frequent, fast)                   │
│   ┌─────────────┬─────────────┬─────────────┐                   │
│   │    Eden     │  Survivor 0 │  Survivor 1 │                   │
│   │  (new objs) │    (S0)     │    (S1)     │                   │
│   └─────────────┴─────────────┴─────────────┘                   │
│         │                                                        │
│         ▼  Objects surviving multiple GCs                       │
│   Old Generation (Major GC - less frequent, slower)              │
│   ┌────────────────────────────────────────────┐                │
│   │        Long-lived objects                   │                │
│   └────────────────────────────────────────────┘                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**GC Algorithms:**
- **Serial GC**: Single thread, for small apps
- **Parallel GC**: Multiple threads, for throughput
- **G1 GC**: Low latency, default in Java 9+
- **ZGC/Shenandoah**: Ultra-low latency (<10ms pauses)

```java
// Making object eligible for GC
Object obj = new Object();
obj = null;  // Now eligible for GC

// Request GC (not guaranteed)
System.gc();
```

---

## Q16: What are access modifiers in Java?

**Answer:**

| Modifier | Class | Package | Subclass | World |
|----------|-------|---------|----------|-------|
| **public** | ✅ | ✅ | ✅ | ✅ |
| **protected** | ✅ | ✅ | ✅ | ❌ |
| **default** (no modifier) | ✅ | ✅ | ❌ | ❌ |
| **private** | ✅ | ❌ | ❌ | ❌ |

```java
public class MyClass {
    public int publicVar;       // Accessible everywhere
    protected int protectedVar; // Class + package + subclasses
    int defaultVar;             // Class + package only
    private int privateVar;     // This class only
}
```

---

## Q17: What is the difference between shallow copy and deep copy?

**Answer:**

```
SHALLOW COPY:                          DEEP COPY:
Original ─┐                           Original ─┐
          │                                     │
          ▼                                     ▼
     ┌─────────┐                          ┌─────────┐
     │ Object  │                          │ Object  │
     │  ref ───┼──┐                       │  ref ───┼──► ┌─────────┐
     └─────────┘  │                       └─────────┘    │ Nested  │
                  │                                      │  (copy) │
Copy ────┐        │                       Copy ────┐     └─────────┘
         │        │                                │
         ▼        │                                ▼
    ┌─────────┐   │                          ┌─────────┐
    │ Object  │   │                          │ Object  │
    │  ref ───┼───┘                          │  ref ───┼──► ┌─────────┐
    └─────────┘                              └─────────┘    │ Nested  │
         ▲                                                  │  (copy) │
    Same nested object!                                     └─────────┘
                                                  Different nested objects
```

```java
// Shallow copy - nested objects are shared
class Address { String city; }
class Person implements Cloneable {
    String name;
    Address address;
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow - address is shared
    }
}

// Deep copy - nested objects are also copied
@Override
protected Object clone() throws CloneNotSupportedException {
    Person copy = (Person) super.clone();
    copy.address = new Address();
    copy.address.city = this.address.city;  // Copy nested object
    return copy;
}
```

---

## Q18: What are Generics in Java? Why use them?

**Answer:**

**Generics** enable type-safe code by parameterizing types.

```java
// Without Generics (before Java 5)
List list = new ArrayList();
list.add("Hello");
list.add(123);  // No compile error!
String s = (String) list.get(1);  // ClassCastException at runtime!

// With Generics (type-safe)
List<String> list = new ArrayList<>();
list.add("Hello");
list.add(123);  // ❌ Compile error!
String s = list.get(0);  // No cast needed
```

**Generic wildcards:**
```java
// Unbounded: any type
List<?> anyList;

// Upper bound: Number or subclass (read-only)
List<? extends Number> numbers;  // Can read as Number
numbers.add(1);  // ❌ Cannot add (except null)

// Lower bound: Integer or superclass (write-only)
List<? super Integer> integers;  // Can add Integer
integers.add(1);  // ✅ Can add Integer
Number n = integers.get(0);  // ❌ Can only get Object
```

**PECS: Producer Extends, Consumer Super**

---

## Q19: What is the static keyword in Java?

**Answer:**

**static** members belong to the class, not instances.

| Type | Description | Access |
|------|-------------|--------|
| Static variable | Shared across all instances | `ClassName.variable` |
| Static method | No instance needed | `ClassName.method()` |
| Static block | Runs once when class loads | Initialization |
| Static nested class | Doesn't need outer instance | `Outer.Inner` |

```java
public class Counter {
    private static int count = 0;  // Shared variable
    private int id;
    
    static {  // Static block - runs once
        System.out.println("Class loaded");
    }
    
    public Counter() {
        this.id = ++count;  // Increment shared counter
    }
    
    public static int getCount() {  // Static method
        return count;
    }
}

// Usage
Counter c1 = new Counter();
Counter c2 = new Counter();
Counter.getCount();  // Returns 2
```

---

## Q20: What is the difference between throw and throws?

**Answer:**

| Aspect | throw | throws |
|--------|-------|--------|
| **Purpose** | Throw an exception | Declare possible exceptions |
| **Location** | Inside method body | Method signature |
| **Count** | Single exception | Multiple exceptions |
| **Type** | Exception object | Exception classes |

```java
// throws - declaration in method signature
public void readFile(String path) throws IOException, FileNotFoundException {
    // Method might throw these exceptions
}

// throw - actually throwing an exception
public void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative");
    }
}

// Combined usage
public Book findBook(Long id) throws BookNotFoundException {
    Book book = repository.findById(id);
    if (book == null) {
        throw new BookNotFoundException("Book not found: " + id);
    }
    return book;
}
```

---

## Q21: What is Serialization in Java?

**Answer:**

**Serialization** converts an object to a byte stream for storage/transmission. **Deserialization** reverses the process.

```java
// Mark class as serializable
public class User implements Serializable {
    private static final long serialVersionUID = 1L;  // Version control
    
    private String name;
    private transient String password;  // Won't be serialized
}

// Serialize (write)
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("user.dat"))) {
    oos.writeObject(user);
}

// Deserialize (read)
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("user.dat"))) {
    User user = (User) ois.readObject();
}
```

**Key points:**
- `transient` fields are skipped
- `serialVersionUID` ensures version compatibility
- Static fields are not serialized

---

## Q22: What is the Java Memory Model?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────┐
│                      JVM MEMORY AREAS                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   HEAP (shared by all threads):                                  │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │  Objects, instance variables, arrays                      │  │
│   │  Young Gen (Eden, S0, S1) + Old Gen                       │  │
│   └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│   STACK (per thread):                                            │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │  Method calls, local variables, references                │  │
│   │  Each thread has its own stack                            │  │
│   └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│   METASPACE (Java 8+, was PermGen):                              │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │  Class metadata, static variables, method bytecode        │  │
│   └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│   PC Register + Native Method Stack (per thread)                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

| Area | Stores | Thread Safety |
|------|--------|---------------|
| Heap | Objects | Shared (needs sync) |
| Stack | Locals, calls | Thread-private |
| Metaspace | Class info | Shared |

---

## Q23: What is autoboxing and unboxing?

**Answer:**

**Autoboxing**: Automatic conversion from primitive to wrapper class.
**Unboxing**: Automatic conversion from wrapper to primitive.

```java
// Autoboxing (primitive → wrapper)
int primitive = 10;
Integer wrapper = primitive;  // Auto-boxed to Integer

// Unboxing (wrapper → primitive)  
Integer wrapper = Integer.valueOf(20);
int primitive = wrapper;  // Auto-unboxed to int

// In collections
List<Integer> list = new ArrayList<>();
list.add(5);  // Autoboxing: 5 → Integer.valueOf(5)
int num = list.get(0);  // Unboxing: Integer → int

// Watch out for null!
Integer nullValue = null;
int x = nullValue;  // ❌ NullPointerException!
```

**Primitive-Wrapper pairs:**
| Primitive | Wrapper |
|-----------|---------|
| int | Integer |
| double | Double |
| boolean | Boolean |
| char | Character |
| long | Long |

---

## Q24: What is the difference between fail-fast and fail-safe iterators?

**Answer:**

| Aspect | Fail-Fast | Fail-Safe |
|--------|-----------|-----------|
| **Exception** | ConcurrentModificationException | No exception |
| **Collection** | Original | Copy of original |
| **Memory** | Less | More |
| **Examples** | ArrayList, HashMap | CopyOnWriteArrayList, ConcurrentHashMap |

```java
// FAIL-FAST (ArrayList)
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
for (String s : list) {
    list.remove(s);  // ❌ ConcurrentModificationException
}

// FAIL-SAFE (CopyOnWriteArrayList)
List<String> list = new CopyOnWriteArrayList<>();
list.add("A");
list.add("B");
for (String s : list) {
    list.remove(s);  // ✅ Works (iterates over copy)
}
```

---

## Q25: What is a deadlock? How to prevent it?

**Answer:**

**Deadlock** occurs when two or more threads are blocked forever, waiting for each other's locks.

```
Thread 1:                    Thread 2:
Lock A ─────────┐            Lock B ─────────┐
       Waiting  │                   Waiting  │
       for B ◄──┼────────────────── Holds B  │
                │                            │
Holds A ────────┼──────────────────► for A   │
                │                   Waiting  │
                └───────────────────────────-┘
                     DEADLOCK!
```

**Prevention strategies:**
```java
// 1. Lock ordering - always acquire locks in same order
synchronized (lockA) {
    synchronized (lockB) {
        // Safe - consistent order
    }
}

// 2. Try-lock with timeout
Lock lock1 = new ReentrantLock();
Lock lock2 = new ReentrantLock();

if (lock1.tryLock(1, TimeUnit.SECONDS)) {
    try {
        if (lock2.tryLock(1, TimeUnit.SECONDS)) {
            try {
                // Work
            } finally { lock2.unlock(); }
        }
    } finally { lock1.unlock(); }
}

// 3. Use higher-level concurrency utilities
ExecutorService executor = Executors.newFixedThreadPool(4);
```

---

## Q26: What are annotations in Java?

**Answer:**

**Annotations** are metadata that provide information to the compiler and runtime.

```java
// Built-in annotations
@Override           // Verifies method overrides parent
@Deprecated         // Marks as outdated
@SuppressWarnings   // Suppresses compiler warnings
@FunctionalInterface // Validates single abstract method

// Custom annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogExecutionTime {
    String value() default "method";
}

// Usage
@LogExecutionTime("processOrder")
public void processOrder(Order order) {
    // Processing logic
}

// Reading at runtime
Method method = obj.getClass().getMethod("processOrder", Order.class);
if (method.isAnnotationPresent(LogExecutionTime.class)) {
    LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);
    System.out.println(annotation.value());  // "processOrder"
}
```

---

## Q27: What is the difference between Hashtable and HashMap?

**Answer:**

| Feature | HashMap | Hashtable |
|---------|---------|-----------|
| **Thread-safe** | No | Yes (synchronized) |
| **Null keys** | 1 allowed | Not allowed |
| **Null values** | Allowed | Not allowed |
| **Performance** | Faster | Slower (sync overhead) |
| **Legacy** | Java 1.2 | Java 1.0 (legacy) |
| **Iterator** | Fail-fast | Fail-fast |

```java
// HashMap - preferred for single-threaded
Map<String, Integer> map = new HashMap<>();
map.put(null, 1);  // ✅ Allowed
map.put("key", null);  // ✅ Allowed

// Hashtable - legacy, avoid
Map<String, Integer> table = new Hashtable<>();
table.put(null, 1);  // ❌ NullPointerException
table.put("key", null);  // ❌ NullPointerException

// For thread-safety, use ConcurrentHashMap instead
Map<String, Integer> concurrent = new ConcurrentHashMap<>();
```

---

## Q28: What is the this and super keyword?

**Answer:**

| Keyword | Purpose | Usage |
|---------|---------|-------|
| **this** | Reference to current object | Access current class members |
| **super** | Reference to parent class | Access parent class members |

```java
class Animal {
    String name;
    
    Animal(String name) {
        this.name = name;
    }
    
    void display() {
        System.out.println("Animal: " + name);
    }
}

class Dog extends Animal {
    String breed;
    
    Dog(String name, String breed) {
        super(name);  // Call parent constructor
        this.breed = breed;  // Current object's field
    }
    
    @Override
    void display() {
        super.display();  // Call parent method
        System.out.println("Breed: " + this.breed);
    }
    
    Dog getThis() {
        return this;  // Return current object
    }
}
```

---

## Q29: What are wrapper classes? Why do we need them?

**Answer:**

**Wrapper classes** provide object representation of primitives.

| Primitive | Wrapper | Size |
|-----------|---------|------|
| byte | Byte | 8 bits |
| short | Short | 16 bits |
| int | Integer | 32 bits |
| long | Long | 64 bits |
| float | Float | 32 bits |
| double | Double | 64 bits |
| char | Character | 16 bits |
| boolean | Boolean | 1 bit |

**Why needed:**
```java
// 1. Collections require objects
List<Integer> list = new ArrayList<>();  // Can't use List<int>
list.add(5);  // Autoboxed

// 2. Null values
Integer mayBeNull = null;  // Primitives can't be null

// 3. Utility methods
String s = "123";
int num = Integer.parseInt(s);  // String to int
String binary = Integer.toBinaryString(10);  // "1010"

// 4. Generics
Optional<Integer> optional = Optional.of(42);
```

---

## Q30: What is the difference between == and .equals() for Integer?

**Answer:**

```java
// Integer caching: -128 to 127 are cached
Integer a = 100;  // Cached
Integer b = 100;  // Same cached object
System.out.println(a == b);  // true (same object)

Integer c = 200;  // Not cached
Integer d = 200;  // Different object
System.out.println(c == d);  // false (different objects)
System.out.println(c.equals(d));  // true (same value)

// Always use .equals() for wrapper comparison!
Integer x = 1000;
Integer y = 1000;
System.out.println(x.equals(y));  // ✅ true (correct way)
```

**Integer Cache range:** -128 to 127 (configurable with `-XX:AutoBoxCacheMax`)

---

## Q31: What is an immutable class? How to create one?

**Answer:**

**Immutable class**: Once created, its state cannot be modified.

**Rules to create:**
1. Make class `final`
2. Make all fields `private final`
3. No setters
4. Initialize via constructor
5. Return copies of mutable objects

```java
public final class ImmutablePerson {
    private final String name;
    private final Date birthDate;  // Mutable field
    
    public ImmutablePerson(String name, Date birthDate) {
        this.name = name;
        this.birthDate = new Date(birthDate.getTime());  // Defensive copy
    }
    
    public String getName() {
        return name;  // String is immutable
    }
    
    public Date getBirthDate() {
        return new Date(birthDate.getTime());  // Return copy
    }
}
```

**Benefits:** Thread-safe, hashable, cacheable.
**Examples:** String, Integer, LocalDate

---

## Q32: What is ClassLoader in Java?

**Answer:**

**ClassLoader** loads `.class` files into JVM memory.

```
┌─────────────────────────────────────────────────────────────────┐
│                    CLASSLOADER HIERARCHY                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   1. Bootstrap ClassLoader (Native C code)                       │
│      └─ Loads: rt.jar, core Java classes (java.lang.*)          │
│              │                                                   │
│              ▼                                                   │
│   2. Extension ClassLoader (jre/lib/ext)                         │
│      └─ Loads: Extension JARs                                    │
│              │                                                   │
│              ▼                                                   │
│   3. Application ClassLoader (classpath)                         │
│      └─ Loads: Your application classes                          │
│              │                                                   │
│              ▼                                                   │
│   4. Custom ClassLoader (optional)                               │
│      └─ Loads: Custom source (network, encrypted, etc.)          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Delegation Model:** Child asks parent first before loading itself.

```java
// Get classloader info
String.class.getClassLoader();  // null (Bootstrap)
MyClass.class.getClassLoader();  // AppClassLoader
```

---

## Q33: What is Reflection in Java?

**Answer:**

**Reflection** allows inspecting and modifying class behavior at runtime.

```java
// Get class information
Class<?> clazz = MyClass.class;
// or
Class<?> clazz = Class.forName("com.example.MyClass");
// or
Class<?> clazz = obj.getClass();

// Create instance
Object obj = clazz.getDeclaredConstructor().newInstance();

// Access private field
Field field = clazz.getDeclaredField("privateField");
field.setAccessible(true);  // Bypass access check
Object value = field.get(obj);

// Invoke private method
Method method = clazz.getDeclaredMethod("privateMethod", String.class);
method.setAccessible(true);
method.invoke(obj, "param");
```

**Use cases:** Frameworks (Spring, Hibernate), testing, serialization
**Drawbacks:** Slower, breaks encapsulation, security risks

---

## Q34: What are inner classes? Types of inner classes?

**Answer:**

| Type | Location | Access to Outer |
|------|----------|-----------------|
| **Member Inner** | Inside class, outside method | Yes (including private) |
| **Static Nested** | static inside class | Only static members |
| **Local Inner** | Inside method | Yes + effectively final locals |
| **Anonymous** | Inline, no name | Yes + effectively final locals |

```java
public class Outer {
    private int x = 10;
    
    // 1. Member Inner Class
    class MemberInner {
        void show() { System.out.println(x); }
    }
    
    // 2. Static Nested Class
    static class StaticNested {
        void show() { /* Cannot access x */ }
    }
    
    void method() {
        int y = 20;  // effectively final
        
        // 3. Local Inner Class
        class LocalInner {
            void show() { System.out.println(x + y); }
        }
        
        // 4. Anonymous Inner Class
        Runnable r = new Runnable() {
            @Override
            public void run() { System.out.println(x + y); }
        };
    }
}
```

---

## Q35: What is a marker interface?

**Answer:**

**Marker interface**: Empty interface with no methods, used to mark/tag classes.

```java
// Examples from Java
public interface Serializable { }  // Marks class as serializable
public interface Cloneable { }     // Marks class as cloneable
public interface RandomAccess { }  // Marks list as fast random access

// Custom marker interface
public interface Auditable { }

public class User implements Auditable {
    // This class will be audited
}

// Usage
if (entity instanceof Auditable) {
    auditService.log(entity);
}
```

**Modern alternative:** Annotations `@interface` are preferred.
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Auditable { }
```

---

## Q36: What is type erasure in generics?

**Answer:**

**Type erasure**: Generics are compile-time only; at runtime, generic types are replaced with Object or bounds.

```java
// Source code
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// After type erasure (bytecode)
List strings = new ArrayList();  // Raw type
List integers = new ArrayList(); // Same raw type!

// This is why:
strings.getClass() == integers.getClass()  // true!

// Cannot do at runtime:
if (obj instanceof List<String>) { }  // ❌ Compile error
new T();  // ❌ Cannot create generic instance
new T[10];  // ❌ Cannot create generic array
```

**Workaround for runtime type:**
```java
class Box<T> {
    private Class<T> type;
    
    Box(Class<T> type) { this.type = type; }
    
    T createInstance() throws Exception {
        return type.getDeclaredConstructor().newInstance();
    }
}
```

---

## Q37: What is covariance and contravariance?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────┐
│               COVARIANCE vs CONTRAVARIANCE                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   COVARIANCE (extends) - Producer, Read-only                     │
│   List<? extends Animal> animals;                                │
│   ✅ Can read: Animal a = animals.get(0);                        │
│   ❌ Can't add: animals.add(new Dog()); // What if it's Cat?    │
│                                                                  │
│         Object                                                   │
│            ▲                                                     │
│         Animal  ◄── Upper bound                                  │
│          /  \                                                    │
│        Dog   Cat  ◄── ? can be any subtype                      │
│                                                                  │
│   ─────────────────────────────────────────────────────────────  │
│                                                                  │
│   CONTRAVARIANCE (super) - Consumer, Write-only                  │
│   List<? super Dog> dogs;                                        │
│   ✅ Can add: dogs.add(new Dog());                               │
│   ❌ Can't read as Dog: Dog d = dogs.get(0); // Might be Animal │
│                                                                  │
│         Object  ◄── ? can be any supertype                      │
│            ▲                                                     │
│         Animal                                                   │
│            ▲                                                     │
│          Dog  ◄── Lower bound                                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**PECS Rule:** Producer Extends, Consumer Super

```java
// Copy from source (producer) to dest (consumer)
public static <T> void copy(
    List<? extends T> src,   // Producer - read from
    List<? super T> dest) {  // Consumer - write to
    for (T item : src) {
        dest.add(item);
    }
}
```

---

## Q38: What is a daemon thread?

**Answer:**

**Daemon thread**: Background thread that doesn't prevent JVM from exiting.

| Aspect | User Thread | Daemon Thread |
|--------|-------------|---------------|
| JVM exits when | All user threads complete | Immediately terminates |
| Purpose | Main application work | Background services |
| Examples | main(), custom threads | GC, finalizer |

```java
Thread thread = new Thread(() -> {
    while (true) {
        System.out.println("Background task");
        Thread.sleep(1000);
    }
});

thread.setDaemon(true);  // Must set before start()
thread.start();

// When main thread exits, daemon thread is killed
```

**Use cases:** Garbage collection, heartbeat monitoring, cache cleanup

---

## Q39: What is a thread pool? Types of Executors?

**Answer:**

**Thread pool**: Pre-created threads that execute tasks, avoiding thread creation overhead.

```java
// 1. Fixed Thread Pool - Fixed number of threads
ExecutorService fixed = Executors.newFixedThreadPool(4);

// 2. Cached Thread Pool - Creates threads as needed
ExecutorService cached = Executors.newCachedThreadPool();

// 3. Single Thread - One thread, sequential execution
ExecutorService single = Executors.newSingleThreadExecutor();

// 4. Scheduled - For delayed/periodic tasks
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
scheduled.scheduleAtFixedRate(() -> task(), 0, 1, TimeUnit.SECONDS);

// Submit tasks
Future<String> future = fixed.submit(() -> {
    return "Result";
});
String result = future.get();  // Blocks until complete

// Always shutdown!
fixed.shutdown();
```

**Production recommendation:** Use `ThreadPoolExecutor` directly for more control.

---

## Q40: What is a BlockingQueue?

**Answer:**

**BlockingQueue**: Thread-safe queue that blocks on empty/full conditions.

| Method | Blocks? | On Full | On Empty |
|--------|---------|---------|----------|
| `put()` | Yes | Waits | - |
| `take()` | Yes | - | Waits |
| `offer()` | No | Returns false | - |
| `poll()` | No | - | Returns null |

```java
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

// Producer thread
void produce() throws InterruptedException {
    Task task = createTask();
    queue.put(task);  // Blocks if queue is full
}

// Consumer thread
void consume() throws InterruptedException {
    Task task = queue.take();  // Blocks if queue is empty
    process(task);
}
```

**Implementations:**
- `ArrayBlockingQueue` - bounded, array-backed
- `LinkedBlockingQueue` - optionally bounded
- `PriorityBlockingQueue` - priority ordered
- `SynchronousQueue` - zero capacity, direct handoff

---

## Q41: What is the difference between wait() and sleep()?

**Answer:**

| Aspect | wait() | sleep() |
|--------|--------|---------|
| **Class** | Object | Thread |
| **Lock** | Releases lock | Holds lock |
| **Wake up** | notify()/notifyAll() | After time elapses |
| **Usage** | Inter-thread communication | Pause execution |
| **Context** | Must be in synchronized block | Anywhere |

```java
// wait() - releases lock, waits for notification
synchronized (lock) {
    while (!condition) {
        lock.wait();  // Releases lock
    }
    // ... proceed
}

// In another thread
synchronized (lock) {
    condition = true;
    lock.notify();  // Wake up waiting thread
}

// sleep() - just pauses, keeps lock
synchronized (lock) {
    Thread.sleep(1000);  // Still holds lock!
}
```

---

## Q42: What is the volatile double-checked locking pattern?

**Answer:**

**Double-checked locking**: Lazy singleton initialization with minimal synchronization.

```java
public class Singleton {
    // volatile prevents instruction reordering
    private static volatile Singleton instance;
    
    private Singleton() { }
    
    public static Singleton getInstance() {
        if (instance == null) {           // First check (no lock)
            synchronized (Singleton.class) {
                if (instance == null) {   // Second check (with lock)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**Why volatile?** Without it, JVM may reorder instructions:
1. Allocate memory
2. Assign reference to instance  ← Other thread sees non-null
3. Call constructor              ← But object not fully constructed!

**Better alternative:** Use enum singleton or holder class pattern.

---

## Q43: What are atomic classes?

**Answer:**

**Atomic classes**: Thread-safe operations without explicit synchronization using CAS (Compare-And-Swap).

```java
// Without atomic - race condition
int count = 0;
count++;  // Read-Modify-Write: NOT atomic!

// With AtomicInteger
AtomicInteger atomicCount = new AtomicInteger(0);
atomicCount.incrementAndGet();  // Atomic!

// Common atomic operations
AtomicInteger ai = new AtomicInteger(10);
ai.get();                    // 10
ai.set(20);                  // Sets to 20
ai.incrementAndGet();        // 21
ai.getAndIncrement();        // Returns 21, then 22
ai.compareAndSet(22, 30);    // If 22, set to 30, returns true/false
ai.updateAndGet(x -> x * 2); // 60

// Other atomic classes
AtomicLong, AtomicBoolean, AtomicReference<T>
AtomicIntegerArray, AtomicLongArray
LongAdder, LongAccumulator  // Better for high contention
```

---

## Q44: How to detect and prevent memory leaks?

**Answer:**

**Common causes:**
1. Unclosed resources
2. Static collections holding references
3. Listeners not removed
4. Inner class holding outer reference
5. ThreadLocal not cleaned

```java
// 1. Unclosed resources
try (Connection conn = getConnection()) {
    // Auto-closed
}

// 2. Static collections
static Map<Key, Value> cache = new WeakHashMap<>();  // Use weak refs

// 3. Event listeners
component.removeListener(listener);  // Always unregister

// 4. Inner classes
class Outer {
    class Inner { }  // Holds reference to Outer
    static class StaticInner { }  // No reference - preferred
}

// 5. ThreadLocal
ThreadLocal<Data> threadLocal = new ThreadLocal<>();
try {
    threadLocal.set(data);
    // use
} finally {
    threadLocal.remove();  // Always clean up!
}
```

**Detection:** Use profilers (VisualVM, JProfiler), heap dumps, `-XX:+HeapDumpOnOutOfMemoryError`

---

## Q45: What is the difference between Callable and Runnable?

**Answer:**

| Feature | Runnable | Callable |
|---------|----------|----------|
| **Method** | run() | call() |
| **Return** | void | V (generic) |
| **Exception** | Cannot throw checked | Can throw checked |
| **Result** | No result | Returns Future<V> |

```java
// Runnable - no return value
Runnable runnable = () -> {
    System.out.println("Running");
    // Cannot return or throw checked exception
};
new Thread(runnable).start();

// Callable - returns value
Callable<Integer> callable = () -> {
    if (error) throw new Exception("Failed");
    return 42;
};

ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(callable);
Integer result = future.get();  // 42, blocks until complete
```

---

## Q46: What is ConcurrentHashMap? How is it different from synchronized HashMap?

**Answer:**

| Feature | Collections.synchronizedMap | ConcurrentHashMap |
|---------|----------------------------|-------------------|
| **Locking** | Entire map | Segment/bucket level |
| **Read ops** | Blocked during write | Non-blocking |
| **Performance** | Lower | Higher for concurrent access |
| **Null keys/values** | Allowed | Not allowed |
| **Iteration** | Fail-fast | Weakly consistent |

```java
// Synchronized Map - locks entire map
Map<K,V> syncMap = Collections.synchronizedMap(new HashMap<>());
synchronized(syncMap) {  // Required for iteration
    for (Map.Entry<K,V> e : syncMap.entrySet()) { }
}

// ConcurrentHashMap - fine-grained locking
ConcurrentHashMap<K,V> concurrentMap = new ConcurrentHashMap<>();

// Atomic compound operations
concurrentMap.putIfAbsent(key, value);
concurrentMap.computeIfAbsent(key, k -> createValue());
concurrentMap.merge(key, 1, Integer::sum);  // Increment counter
```

---

## Q47: What is the Fork/Join framework?

**Answer:**

**Fork/Join**: Divide-and-conquer parallelism using work-stealing.

```
┌─────────────────────────────────────────────────────────────────┐
│                      FORK/JOIN PATTERN                           │
│                                                                  │
│           ┌──────────────────────────┐                          │
│           │      Big Task            │                          │
│           └───────────┬──────────────┘                          │
│                       │ fork()                                   │
│           ┌───────────┴───────────┐                             │
│           ▼                       ▼                              │
│    ┌──────────────┐       ┌──────────────┐                      │
│    │  Subtask 1   │       │  Subtask 2   │                      │
│    └──────┬───────┘       └───────┬──────┘                      │
│           │ fork()                 │ fork()                      │
│     ┌─────┴─────┐           ┌─────┴─────┐                       │
│     ▼           ▼           ▼           ▼                       │
│  ┌─────┐    ┌─────┐     ┌─────┐    ┌─────┐                     │
│  │ 1a  │    │ 1b  │     │ 2a  │    │ 2b  │  compute directly   │
│  └──┬──┘    └──┬──┘     └──┬──┘    └──┬──┘                     │
│     └────┬─────┘           └────┬─────┘                         │
│          │ join()               │ join()                         │
│          ▼                      ▼                                │
│     ┌─────────┐            ┌─────────┐                          │
│     │ Result 1│            │ Result 2│                          │
│     └────┬────┘            └────┬────┘                          │
│          └──────────┬───────────┘                               │
│                     │ join()                                     │
│                     ▼                                            │
│              ┌───────────┐                                       │
│              │Final Result│                                      │
│              └───────────┘                                       │
└─────────────────────────────────────────────────────────────────┘
```

```java
class SumTask extends RecursiveTask<Long> {
    private int[] array;
    private int start, end;
    private static final int THRESHOLD = 1000;
    
    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // Base case: compute directly
            return computeDirectly();
        }
        // Fork into subtasks
        int mid = (start + end) / 2;
        SumTask left = new SumTask(array, start, mid);
        SumTask right = new SumTask(array, mid, end);
        left.fork();  // Async
        Long rightResult = right.compute();  // Sync
        Long leftResult = left.join();  // Wait
        return leftResult + rightResult;
    }
}

// Usage
ForkJoinPool pool = ForkJoinPool.commonPool();
Long result = pool.invoke(new SumTask(array, 0, array.length));
```

---

## Q48: What is the difference between String intern()?

**Answer:**

**intern()**: Returns canonical representation from String pool.

```java
String s1 = new String("hello");  // Heap
String s2 = new String("hello");  // Different heap object
String s3 = "hello";              // Pool

s1 == s2;  // false (different objects)
s1 == s3;  // false (heap vs pool)

String s4 = s1.intern();  // Get from pool
s4 == s3;  // true! (same pool object)

// Useful for reducing memory with many duplicate strings
Map<String, Data> map = new HashMap<>();
for (String key : hugeListWithDuplicates) {
    map.put(key.intern(), data);  // Reuse pooled strings
}
```

**Caution:** Overuse can cause PermGen/Metaspace issues.

---

## Q49: What are design patterns commonly used in Java?

**Answer:**

**Creational:**
```java
// Singleton - one instance
public enum Singleton {
    INSTANCE;
    public void doSomething() { }
}

// Factory - create without exposing logic
public static Animal createAnimal(String type) {
    return switch(type) {
        case "dog" -> new Dog();
        case "cat" -> new Cat();
        default -> throw new IllegalArgumentException();
    };
}

// Builder - step-by-step construction
User user = User.builder()
    .name("John")
    .email("john@example.com")
    .build();
```

**Structural:**
```java
// Adapter - convert interface
class LegacyAdapter implements NewInterface {
    private LegacyClass legacy;
    public void newMethod() { legacy.oldMethod(); }
}

// Decorator - add behavior
new BufferedReader(new FileReader("file.txt"));
```

**Behavioral:**
```java
// Observer - notify subscribers
listeners.forEach(l -> l.onEvent(event));

// Strategy - interchangeable algorithms
list.sort(Comparator.comparing(User::getName));
```

---

## Q50: What is the try-with-resources statement?

**Answer:**

**Try-with-resources** (Java 7+): Auto-closes resources implementing `AutoCloseable`.

```java
// Old way - verbose and error-prone
BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader("file.txt"));
    return reader.readLine();
} finally {
    if (reader != null) {
        try { reader.close(); } 
        catch (IOException e) { /* swallowed */ }
    }
}

// Try-with-resources - clean and safe
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    return reader.readLine();
}  // Auto-closed, even on exception

// Multiple resources
try (Connection conn = getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    // All auto-closed in reverse order
}

// Java 9+ - effectively final variables
BufferedReader reader = new BufferedReader(new FileReader("file.txt"));
try (reader) {  // Can use existing variable
    return reader.readLine();
}
```

**Suppressed exceptions:** If close() throws while handling another exception, it's added to suppressed exceptions via `getSuppressed()`.

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **== vs equals()** | Reference vs content comparison |
| **Collections** | List (ordered), Set (unique), Map (key-value) |
| **ArrayList vs LinkedList** | Array-based O(1) access vs node-based O(1) insert |
| **HashMap** | Hash table with buckets, uses hashCode() + equals() |
| **final/finally/finalize** | Constant / cleanup block / deprecated GC method |
| **Exceptions** | Checked (compile-time) vs Unchecked (runtime) |
| **Threads** | NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED |
| **String/Builder/Buffer** | Immutable / mutable fast / mutable thread-safe |
| **synchronized/volatile** | Atomicity + visibility / visibility only |
| **Comparable/Comparator** | Natural ordering / custom ordering |

---

> **Next Topic:** Reactive Programming Basics
