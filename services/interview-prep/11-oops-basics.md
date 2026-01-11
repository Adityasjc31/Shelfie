# 🎓 Topic 11: OOPS Basics - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Object-Oriented Programming (OOP) concepts including Encapsulation, Inheritance, Polymorphism, and Abstraction.

---

## Q1: What are the four pillars of OOP?

**Answer:**

Object-Oriented Programming is built on four fundamental principles:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    FOUR PILLARS OF OOP                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│         ┌───────────────┐              ┌───────────────┐                │
│         │ ENCAPSULATION │              │  INHERITANCE  │                │
│         │               │              │               │                │
│         │  Hide data,   │              │  Reuse code   │                │
│         │  expose API   │              │  from parent  │                │
│         └───────────────┘              └───────────────┘                │
│                                                                          │
│         ┌───────────────┐              ┌───────────────┐                │
│         │ POLYMORPHISM  │              │  ABSTRACTION  │                │
│         │               │              │               │                │
│         │ One interface,│              │  Hide complex,│                │
│         │ many forms    │              │  show simple  │                │
│         └───────────────┘              └───────────────┘                │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   1. ENCAPSULATION: Bundling data + methods, hiding internal state     │
│   2. INHERITANCE: Creating new classes from existing ones               │
│   3. POLYMORPHISM: Same method behaving differently                     │
│   4. ABSTRACTION: Hiding complexity, showing only necessary details    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q2: What is Encapsulation? Why is it important?

**Answer:**

**Encapsulation** is bundling data (variables) and methods (functions) that operate on that data within a single unit (class), while restricting direct access to internal state.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         ENCAPSULATION                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   WITHOUT ENCAPSULATION (Bad):                                           │
│   ─────────────────────────────                                          │
│                                                                          │
│   class BankAccount {                                                    │
│       public double balance;  // Directly accessible!                   │
│   }                                                                      │
│                                                                          │
│   BankAccount account = new BankAccount();                              │
│   account.balance = -1000;  // ❌ Invalid negative balance!             │
│   account.balance = 999999999;  // ❌ No validation!                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   WITH ENCAPSULATION (Good):                                             │
│   ──────────────────────────                                             │
│                                                                          │
│   ┌──────────────────────────────────────────────────────────────────┐  │
│   │                      BankAccount                                  │  │
│   │  ┌───────────────────────────────────────────────────────────┐   │  │
│   │  │             PRIVATE (Hidden)                              │   │  │
│   │  │  - balance: double                                        │   │  │
│   │  │  - accountNumber: String                                  │   │  │
│   │  └───────────────────────────────────────────────────────────┘   │  │
│   │                                                                   │  │
│   │  ┌───────────────────────────────────────────────────────────┐   │  │
│   │  │             PUBLIC (Exposed API)                          │   │  │
│   │  │  + getBalance(): double                                   │   │  │
│   │  │  + deposit(amount): void                                  │   │  │
│   │  │  + withdraw(amount): boolean                              │   │  │
│   │  └───────────────────────────────────────────────────────────┘   │  │
│   └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│   Access only through methods - validation enforced!                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example:

```java
public class BankAccount {
    // PRIVATE - cannot be accessed directly
    private double balance;
    private String accountNumber;
    
    // Constructor
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        if (initialBalance >= 0) {
            this.balance = initialBalance;
        } else {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
    }
    
    // PUBLIC - controlled access through methods
    public double getBalance() {
        return balance;  // Read-only access
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;  // Withdrawal failed
    }
}

// Usage
BankAccount account = new BankAccount("ACC001", 1000);
// account.balance = -500;  // ❌ Compile error! Private field
account.deposit(500);        // ✅ Through public method
account.withdraw(200);       // ✅ Validated
```

### Benefits:

| Benefit | Description |
|---------|-------------|
| **Data Protection** | Internal state cannot be corrupted |
| **Validation** | All changes go through validated methods |
| **Flexibility** | Can change internal implementation without affecting external code |
| **Maintainability** | Clear interface, hidden complexity |

---

## Q3: What is Inheritance? Explain with types.

**Answer:**

**Inheritance** allows a class (child/subclass) to inherit attributes and methods from another class (parent/superclass), enabling code reuse and hierarchical relationships.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         INHERITANCE                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   PARENT CLASS (Superclass/Base)                                        │
│   ┌────────────────────────────┐                                        │
│   │         Animal             │                                        │
│   │  - name: String            │                                        │
│   │  - age: int                │                                        │
│   │  + eat(): void             │                                        │
│   │  + sleep(): void           │                                        │
│   └────────────┬───────────────┘                                        │
│                │ extends                                                 │
│        ┌───────┴───────┐                                                 │
│        │               │                                                 │
│   ┌────▼─────┐   ┌─────▼────┐                                           │
│   │   Dog    │   │   Cat    │   CHILD CLASSES (Subclass/Derived)        │
│   │          │   │          │                                           │
│   │+ bark()  │   │+ meow()  │   Inherit name, age, eat(), sleep()       │
│   │+ fetch() │   │+ scratch()│  Add their own methods                   │
│   └──────────┘   └──────────┘                                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example:

```java
// Parent class
public class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
}

// Child class - inherits from Animal
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor
        this.breed = breed;
    }
    
    // Dog-specific method
    public void bark() {
        System.out.println(name + " says Woof!");
    }
    
    // Override parent method
    @Override
    public void eat() {
        System.out.println(name + " is eating dog food");
    }
}

// Usage
Dog dog = new Dog("Buddy", 3, "Golden Retriever");
dog.eat();    // Dog's overridden method
dog.sleep();  // Inherited from Animal
dog.bark();   // Dog's own method
```

### Types of Inheritance:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    TYPES OF INHERITANCE                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. SINGLE INHERITANCE (Java supports)                                  │
│      ┌───┐                                                               │
│      │ A │                                                               │
│      └─┬─┘                                                               │
│        │                                                                 │
│      ┌─▼─┐                                                               │
│      │ B │                                                               │
│      └───┘                                                               │
│                                                                          │
│   2. MULTILEVEL INHERITANCE (Java supports)                              │
│      ┌───┐                                                               │
│      │ A │                                                               │
│      └─┬─┘                                                               │
│        │                                                                 │
│      ┌─▼─┐                                                               │
│      │ B │                                                               │
│      └─┬─┘                                                               │
│        │                                                                 │
│      ┌─▼─┐                                                               │
│      │ C │                                                               │
│      └───┘                                                               │
│                                                                          │
│   3. HIERARCHICAL INHERITANCE (Java supports)                            │
│      ┌───┐                                                               │
│      │ A │                                                               │
│      └─┬─┘                                                               │
│    ┌───┼───┐                                                             │
│    │   │   │                                                             │
│   ┌▼┐ ┌▼┐ ┌▼┐                                                            │
│   │B│ │C│ │D│                                                            │
│   └─┘ └─┘ └─┘                                                            │
│                                                                          │
│   4. MULTIPLE INHERITANCE (Java does NOT support with classes)           │
│      ┌───┐   ┌───┐         ❌ Diamond Problem!                          │
│      │ A │   │ B │         Which method to inherit?                     │
│      └─┬─┘   └─┬─┘                                                       │
│        │       │           ✅ Use interfaces instead!                   │
│        └───┬───┘                                                         │
│          ┌─▼─┐                                                           │
│          │ C │                                                           │
│          └───┘                                                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Important Keywords:

| Keyword | Usage |
|---------|-------|
| `extends` | Class inherits from another class |
| `super` | Reference to parent class |
| `super()` | Call parent constructor |
| `@Override` | Indicate method is overriding parent |

---

## Q4: What is Polymorphism? Explain its types.

**Answer:**

**Polymorphism** means "many forms" - the ability of objects to take different forms and behave differently based on their type.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         POLYMORPHISM                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. COMPILE-TIME POLYMORPHISM (Static/Method Overloading)              │
│   ─────────────────────────────────────────────────────────              │
│   Same method name, different parameters                                │
│   Decided at COMPILE time                                               │
│                                                                          │
│   class Calculator {                                                     │
│       int add(int a, int b) { return a + b; }                           │
│       int add(int a, int b, int c) { return a + b + c; }                │
│       double add(double a, double b) { return a + b; }                  │
│   }                                                                      │
│                                                                          │
│   calc.add(1, 2);        → calls int add(int, int)                      │
│   calc.add(1, 2, 3);     → calls int add(int, int, int)                 │
│   calc.add(1.5, 2.5);    → calls double add(double, double)             │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. RUNTIME POLYMORPHISM (Dynamic/Method Overriding)                    │
│   ──────────────────────────────────────────────────────                 │
│   Same method signature, different implementation in child              │
│   Decided at RUNTIME                                                     │
│                                                                          │
│   class Animal { void makeSound() { print("Some sound"); } }            │
│   class Dog extends Animal { void makeSound() { print("Bark"); } }      │
│   class Cat extends Animal { void makeSound() { print("Meow"); } }      │
│                                                                          │
│   Animal animal = new Dog();  // Upcasting                              │
│   animal.makeSound();         // Output: "Bark" - Dog's method!         │
│                                                                          │
│   Animal animal2 = new Cat();                                           │
│   animal2.makeSound();        // Output: "Meow" - Cat's method!         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Method Overloading (Compile-time):

```java
public class MathOperation {
    
    // Same method name, different parameter count
    public int add(int a, int b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // Same method name, different parameter types
    public double add(double a, double b) {
        return a + b;
    }
    
    // Different parameter order
    public String concat(String s, int n) {
        return s + n;
    }
    
    public String concat(int n, String s) {
        return n + s;
    }
}
```

### Method Overriding (Runtime):

```java
public class Shape {
    public double calculateArea() {
        return 0;
    }
}

public class Circle extends Shape {
    private double radius;
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    @Override
    public double calculateArea() {
        return width * height;
    }
}

// Polymorphic behavior
Shape shape1 = new Circle(5);
Shape shape2 = new Rectangle(4, 6);

shape1.calculateArea();  // Calls Circle's method
shape2.calculateArea();  // Calls Rectangle's method

// Polymorphism with collections
List<Shape> shapes = Arrays.asList(
    new Circle(5),
    new Rectangle(4, 6),
    new Circle(3)
);

for (Shape shape : shapes) {
    System.out.println(shape.calculateArea());  // Each uses its own implementation
}
```

### Comparison:

| Aspect | Overloading | Overriding |
|--------|-------------|------------|
| **Binding** | Compile-time | Runtime |
| **Method Signature** | Different parameters | Same signature |
| **Inheritance** | Not required | Required |
| **Return Type** | Can differ | Must be same or covariant |
| **Access Modifier** | Can differ | Cannot be more restrictive |

---

## Q5: What is Abstraction? How is it achieved in Java?

**Answer:**

**Abstraction** is hiding complex implementation details and showing only the necessary features of an object.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         ABSTRACTION                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   REAL-WORLD EXAMPLE: A Car                                              │
│                                                                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │   WHAT YOU SEE (Interface):                                      │   │
│   │   • Steering wheel                                               │   │
│   │   • Accelerator pedal                                            │   │
│   │   • Brake pedal                                                  │   │
│   │   • Start button                                                 │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │   WHAT'S HIDDEN (Implementation):                                │   │
│   │   • Engine combustion                                            │   │
│   │   • Fuel injection system                                        │   │
│   │   • Transmission mechanics                                       │   │
│   │   • Brake hydraulics                                             │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│   You just press "Start" - you don't need to know how the engine       │
│   ignites fuel and turns pistons!                                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Achieved Through:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ABSTRACTION IN JAVA                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. ABSTRACT CLASSES (0-100% abstraction)                              │
│   ────────────────────────────────────────                               │
│   • Can have abstract and concrete methods                              │
│   • Can have instance variables                                          │
│   • Cannot be instantiated                                              │
│   • Child MUST implement abstract methods                               │
│                                                                          │
│   2. INTERFACES (100% abstraction - traditionally)                       │
│   ─────────────────────────────────────────────────                      │
│   • All methods abstract (before Java 8)                                │
│   • Can have default/static methods (Java 8+)                           │
│   • All variables are public static final                               │
│   • A class can implement multiple interfaces                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Abstract Class Example:

```java
// Abstract class - cannot be instantiated
public abstract class Vehicle {
    protected String brand;
    
    // Constructor
    public Vehicle(String brand) {
        this.brand = brand;
    }
    
    // Abstract method - MUST be implemented by subclasses
    public abstract void start();
    public abstract void stop();
    
    // Concrete method - shared implementation
    public void displayBrand() {
        System.out.println("Brand: " + brand);
    }
}

// Concrete class - must implement abstract methods
public class Car extends Vehicle {
    public Car(String brand) {
        super(brand);
    }
    
    @Override
    public void start() {
        System.out.println("Car starting with key ignition");
    }
    
    @Override
    public void stop() {
        System.out.println("Car stopping with brake");
    }
}

// Usage
Vehicle car = new Car("Toyota");  // ✅ OK
// Vehicle v = new Vehicle();     // ❌ Cannot instantiate abstract class
car.start();         // "Car starting with key ignition"
car.displayBrand();  // "Brand: Toyota" (inherited method)
```

### Interface Example:

```java
// Interface - defines contract
public interface Drivable {
    void accelerate();
    void brake();
    int getSpeed();
    
    // Default method (Java 8+)
    default void honk() {
        System.out.println("Beep!");
    }
}

public interface Electric {
    void charge();
    int getBatteryLevel();
}

// Class can implement MULTIPLE interfaces
public class ElectricCar implements Drivable, Electric {
    private int speed;
    private int batteryLevel;
    
    @Override
    public void accelerate() {
        speed += 10;
    }
    
    @Override
    public void brake() {
        speed = Math.max(0, speed - 15);
    }
    
    @Override
    public int getSpeed() {
        return speed;
    }
    
    @Override
    public void charge() {
        batteryLevel = 100;
    }
    
    @Override
    public int getBatteryLevel() {
        return batteryLevel;
    }
}
```

### Abstract Class vs Interface:

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| **Methods** | Abstract + concrete | Abstract (+ default in Java 8+) |
| **Variables** | Any type | public static final only |
| **Multiple Inheritance** | No | Yes (class can implement many) |
| **Constructor** | Yes | No |
| **Access Modifiers** | Any | public only (methods) |
| **Use When** | Shared code + partial abstraction | Define contract/capability |

---

## Q6: What is the difference between class and object?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CLASS vs OBJECT                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   CLASS = Blueprint/Template                                             │
│   ─────────────────────────                                              │
│   • Defines attributes and behaviors                                    │
│   • Exists at compile time                                              │
│   • No memory allocated until object created                            │
│   • Only one class definition                                           │
│                                                                          │
│   ┌────────────────────────────────────────────┐                        │
│   │           class Car                         │                        │
│   │   ┌────────────────────────────────────┐   │                        │
│   │   │ Attributes:                        │   │                        │
│   │   │   - brand: String                  │   │                        │
│   │   │   - color: String                  │   │                        │
│   │   │   - speed: int                     │   │                        │
│   │   ├────────────────────────────────────┤   │                        │
│   │   │ Methods:                           │   │                        │
│   │   │   + start()                        │   │                        │
│   │   │   + accelerate()                   │   │                        │
│   │   │   + brake()                        │   │                        │
│   │   └────────────────────────────────────┘   │                        │
│   └────────────────────────────────────────────┘                        │
│                         │                                                │
│                         │ new Car()                                      │
│          ┌──────────────┼──────────────┐                                 │
│          │              │              │                                 │
│          ▼              ▼              ▼                                 │
│   ┌────────────┐ ┌────────────┐ ┌────────────┐                          │
│   │  Object 1  │ │  Object 2  │ │  Object 3  │                          │
│   │ brand=BMW  │ │brand=Toyota│ │ brand=Ford │   OBJECTS = Instances    │
│   │ color=Red  │ │color=Blue  │ │color=White │   ─────────────────────  │
│   │ speed=0    │ │ speed=60   │ │ speed=30   │   • Real entities        │
│   └────────────┘ └────────────┘ └────────────┘   • Memory allocated    │
│                                                   • Multiple instances  │
│                                                   • Exist at runtime    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example:

```java
// CLASS - the blueprint
public class Car {
    private String brand;
    private String color;
    private int speed;
    
    public Car(String brand, String color) {
        this.brand = brand;
        this.color = color;
        this.speed = 0;
    }
    
    public void accelerate() {
        speed += 10;
    }
}

// OBJECTS - instances of the class
Car car1 = new Car("BMW", "Red");
Car car2 = new Car("Toyota", "Blue");
Car car3 = new Car("Ford", "White");

// Each object has its own state
car1.accelerate();  // car1.speed = 10
car2.accelerate();  // car2.speed = 10 (independent of car1)
car1.accelerate();  // car1.speed = 20
```

---

## Q7: What is a Constructor? Explain types.

**Answer:**

A **Constructor** is a special method called when an object is created, used to initialize the object's state.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CONSTRUCTORS                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   CHARACTERISTICS:                                                       │
│   • Same name as the class                                              │
│   • No return type (not even void)                                      │
│   • Called automatically when object is created                         │
│   • Can be overloaded                                                   │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   TYPES:                                                                 │
│                                                                          │
│   1. DEFAULT CONSTRUCTOR (No-arg)                                        │
│      - Provided by compiler if no constructor defined                   │
│      - Initializes defaults (0, null, false)                            │
│                                                                          │
│   2. PARAMETERIZED CONSTRUCTOR                                          │
│      - Takes parameters to initialize object                            │
│                                                                          │
│   3. COPY CONSTRUCTOR                                                   │
│      - Creates copy from another object                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Examples:

```java
public class User {
    private Long id;
    private String name;
    private String email;
    private boolean active;
    
    // 1. DEFAULT CONSTRUCTOR (no-arg)
    public User() {
        // Initialize with default values
        this.active = true;
    }
    
    // 2. PARAMETERIZED CONSTRUCTOR
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.active = true;
    }
    
    // 3. FULL PARAMETERIZED CONSTRUCTOR
    public User(Long id, String name, String email, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
    }
    
    // 4. COPY CONSTRUCTOR
    public User(User other) {
        this.id = other.id;
        this.name = other.name;
        this.email = other.email;
        this.active = other.active;
    }
}

// Usage
User user1 = new User();                           // Default
User user2 = new User("John", "john@email.com");   // Parameterized
User user3 = new User(user2);                      // Copy
```

### Constructor Chaining:

```java
public class Employee {
    private Long id;
    private String name;
    private String department;
    
    public Employee() {
        this(null, "Unknown", "General");  // Call another constructor
    }
    
    public Employee(String name) {
        this(null, name, "General");
    }
    
    public Employee(Long id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
}
```

---

## Q8: What are access modifiers in Java?

**Answer:**

**Access modifiers** control the visibility/accessibility of classes, methods, and variables.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       ACCESS MODIFIERS                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   MODIFIER       │ Same │ Same  │ Subclass │ Different │                │
│                  │Class │Package│(diff pkg)│ Package   │                │
│   ───────────────┼──────┼───────┼──────────┼───────────┤                │
│   public         │  ✅  │  ✅   │    ✅    │    ✅     │  Everywhere    │
│   protected      │  ✅  │  ✅   │    ✅    │    ❌     │  + Subclasses  │
│   default (none) │  ✅  │  ✅   │    ❌    │    ❌     │  Package only  │
│   private        │  ✅  │  ❌   │    ❌    │    ❌     │  Class only    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   VISUALIZATION:                                                         │
│                                                                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                         WORLD                                    │   │
│   │   ┌─────────────────────────────────────────────────────────┐   │   │
│   │   │                    PACKAGE                               │   │   │
│   │   │   ┌─────────────────────────────────────────────────┐   │   │   │
│   │   │   │                 CLASS                            │   │   │   │
│   │   │   │   ┌─────────────────────────────────────────┐   │   │   │   │
│   │   │   │   │            private                       │   │   │   │   │
│   │   │   │   └─────────────────────────────────────────┘   │   │   │   │
│   │   │   │   default (package-private)                      │   │   │   │
│   │   │   └─────────────────────────────────────────────────┘   │   │   │
│   │   │   protected (+ subclass anywhere)                        │   │   │
│   │   └─────────────────────────────────────────────────────────┘   │   │
│   │   public                                                         │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example:

```java
package com.example;

public class Person {
    public String name;        // Accessible everywhere
    protected int age;         // Package + subclasses
    String address;            // Package only (default)
    private String ssn;        // This class only
    
    public void publicMethod() { }
    protected void protectedMethod() { }
    void defaultMethod() { }
    private void privateMethod() { }
}

// Same package
package com.example;
class Employee {
    void test() {
        Person p = new Person();
        p.name;      // ✅ public
        p.age;       // ✅ protected (same package)
        p.address;   // ✅ default (same package)
        // p.ssn;    // ❌ private
    }
}

// Different package, subclass
package com.other;
class Student extends Person {
    void test() {
        this.name;     // ✅ public
        this.age;      // ✅ protected (subclass)
        // this.address; // ❌ default
        // this.ssn;    // ❌ private
    }
}

// Different package, not subclass
package com.other;
class Other {
    void test() {
        Person p = new Person();
        p.name;      // ✅ public only
        // p.age;    // ❌
        // p.address;// ❌
        // p.ssn;    // ❌
    }
}
```

---

## Q9: What is the difference between this and super keywords?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       this vs super                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   THIS                                    SUPER                          │
│   ────                                    ─────                          │
│   Reference to CURRENT object             Reference to PARENT class      │
│                                                                          │
│   Uses:                                   Uses:                          │
│   • Access current class members          • Access parent class members  │
│   • Call current class constructor        • Call parent constructor      │
│   • Pass current object as parameter      • Call overridden methods      │
│   • Return current object                                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example:

```java
public class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    public void makeSound() {
        System.out.println("Some generic sound");
    }
    
    public void describe() {
        System.out.println("I am an animal named " + name);
    }
}

public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, String breed) {
        super(name);        // Call parent constructor
        this.breed = breed; // this refers to current class
    }
    
    @Override
    public void makeSound() {
        System.out.println("Bark!");
    }
    
    public void makeAllSounds() {
        this.makeSound();   // Current class method: "Bark!"
        super.makeSound();  // Parent class method: "Some generic sound"
    }
    
    @Override
    public void describe() {
        super.describe();   // Call parent method first
        System.out.println("I am a " + breed);
    }
    
    // Method chaining using 'this'
    public Dog setBreed(String breed) {
        this.breed = breed;
        return this;  // Return current object for chaining
    }
}

// Usage
Dog dog = new Dog("Buddy", "Golden Retriever");
dog.makeAllSounds();
// Output:
// Bark!
// Some generic sound

dog.describe();
// Output:
// I am an animal named Buddy
// I am a Golden Retriever
```

---

## Q10: What is the difference between Aggregation and Composition?

**Answer:**

Both represent **HAS-A** relationships but with different ownership semantics.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                 AGGREGATION vs COMPOSITION                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   AGGREGATION (Weak "HAS-A"):                                           │
│   ───────────────────────────                                            │
│   • Child can exist independently of parent                             │
│   • Weak ownership                                                      │
│   • "Uses" relationship                                                 │
│                                                                          │
│   Example: Department HAS Employees                                      │
│   If department is deleted, employees can still exist                   │
│                                                                          │
│   ┌────────────────┐         ┌────────────────┐                         │
│   │   Department   │ ◇─────▶ │   Employee     │                         │
│   │                │   has   │                │                         │
│   │ List<Employee> │         │ (can exist     │                         │
│   │                │         │  independently)│                         │
│   └────────────────┘         └────────────────┘                         │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   COMPOSITION (Strong "HAS-A"):                                          │
│   ─────────────────────────────                                          │
│   • Child cannot exist without parent                                   │
│   • Strong ownership - parent controls lifecycle                        │
│   • "Owns" relationship                                                 │
│                                                                          │
│   Example: House HAS Rooms                                              │
│   If house is demolished, rooms are destroyed too                       │
│                                                                          │
│   ┌────────────────┐         ┌────────────────┐                         │
│   │     House      │ ◆─────▶ │     Room       │                         │
│   │                │  owns   │                │                         │
│   │ List<Room>     │         │ (destroyed     │                         │
│   │                │         │  with parent)  │                         │
│   └────────────────┘         └────────────────┘                         │
│                                                                          │
│   ◇ = Aggregation (empty diamond)                                       │
│   ◆ = Composition (filled diamond)                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Code Examples:

```java
// ═══════════════════════════════════════════════════════════════════════
// AGGREGATION - Weak ownership
// ═══════════════════════════════════════════════════════════════════════

class Employee {
    private String name;
    // Employee can exist without Department
}

class Department {
    private String name;
    private List<Employee> employees;  // References employees
    
    // Employees are passed in - Department doesn't create them
    public Department(String name, List<Employee> employees) {
        this.name = name;
        this.employees = employees;
    }
    
    // When Department is deleted, Employees still exist elsewhere
}

// Usage
Employee e1 = new Employee("John");
Employee e2 = new Employee("Jane");
List<Employee> employees = Arrays.asList(e1, e2);

Department dept = new Department("IT", employees);
dept = null;  // Department gone, but e1 and e2 still exist!


// ═══════════════════════════════════════════════════════════════════════
// COMPOSITION - Strong ownership
// ═══════════════════════════════════════════════════════════════════════

class Room {
    private String name;
    private int size;
    
    public Room(String name, int size) {
        this.name = name;
        this.size = size;
    }
}

class House {
    private String address;
    private List<Room> rooms;  // House OWNS rooms
    
    public House(String address) {
        this.address = address;
        // House creates its own rooms - strong ownership
        this.rooms = new ArrayList<>();
        this.rooms.add(new Room("Living Room", 300));
        this.rooms.add(new Room("Bedroom", 200));
        this.rooms.add(new Room("Kitchen", 150));
    }
    
    // When House is destroyed, Rooms are destroyed too
}

// Usage
House house = new House("123 Main St");
house = null;  // House is gone, and all its rooms are gone too!
```

### Summary:

| Aspect | Aggregation | Composition |
|--------|-------------|-------------|
| **Ownership** | Weak | Strong |
| **Lifecycle** | Independent | Dependent |
| **Child exists without parent** | Yes | No |
| **UML Symbol** | Empty diamond (◇) | Filled diamond (◆) |
| **Example** | Team has Players | Car has Engine |

---

## Q11: What is the difference between static and instance members?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    STATIC vs INSTANCE MEMBERS                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   STATIC MEMBERS                      INSTANCE MEMBERS                   │
│   ──────────────                      ────────────────                   │
│   • Belong to CLASS                   • Belong to OBJECT                │
│   • One copy shared by all            • Each object has its own copy    │
│   • Accessed via ClassName            • Accessed via object reference   │
│   • Created when class loads          • Created when object is created  │
│   • Can access only static members    • Can access both static/instance │
│                                                                          │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │  class Counter {                                                 │   │
│   │      static int totalCount = 0;  // Shared by ALL objects       │   │
│   │      int instanceCount = 0;      // Each object has its own     │   │
│   │                                                                   │   │
│   │      public Counter() {                                          │   │
│   │          totalCount++;   // All objects increment same counter  │   │
│   │          instanceCount++;                                        │   │
│   │      }                                                           │   │
│   │  }                                                               │   │
│   │                                                                   │   │
│   │  Counter c1 = new Counter();  // totalCount=1, c1.instanceCount=1│   │
│   │  Counter c2 = new Counter();  // totalCount=2, c2.instanceCount=1│   │
│   │  Counter c3 = new Counter();  // totalCount=3, c3.instanceCount=1│   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
public class Employee {
    // Static - shared across all employees
    private static int totalEmployees = 0;
    private static String companyName = "TechCorp";
    
    // Instance - unique to each employee
    private int employeeId;
    private String name;
    
    public Employee(String name) {
        this.name = name;
        this.employeeId = ++totalEmployees;  // Use and increment static counter
    }
    
    // Static method - can only access static members
    public static int getTotalEmployees() {
        // return name;  // ❌ Cannot access instance variable
        return totalEmployees;  // ✅ OK
    }
    
    // Instance method - can access both
    public void displayInfo() {
        System.out.println(name + " works at " + companyName);  // ✅ Both OK
    }
}

// Usage
Employee.getTotalEmployees();  // Access via class name
Employee emp = new Employee("John");
emp.displayInfo();  // Access via object
```

| Aspect | Static | Instance |
|--------|--------|----------|
| **Belongs to** | Class | Object |
| **Memory** | One copy | Per object |
| **Access** | ClassName.member | object.member |
| **Keyword** | `static` | None |
| **Use case** | Utilities, counters | Object state |

---

## Q12: What is the `final` keyword in Java?

**Answer:**

The `final` keyword restricts modification and applies to variables, methods, and classes.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         FINAL KEYWORD                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   1. FINAL VARIABLE - Cannot be reassigned                               │
│   ──────────────────────────────────────────                             │
│   final int MAX_SIZE = 100;                                             │
│   MAX_SIZE = 200;  // ❌ Compile error!                                  │
│                                                                          │
│   final List<String> list = new ArrayList<>();                          │
│   list.add("item");  // ✅ OK - modifying content                       │
│   list = new ArrayList<>();  // ❌ Cannot reassign reference            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   2. FINAL METHOD - Cannot be overridden                                 │
│   ──────────────────────────────────────                                 │
│   class Parent {                                                         │
│       public final void criticalMethod() { }                            │
│   }                                                                      │
│                                                                          │
│   class Child extends Parent {                                           │
│       public void criticalMethod() { }  // ❌ Cannot override!          │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   3. FINAL CLASS - Cannot be extended                                    │
│   ────────────────────────────────────                                   │
│   final class ImmutableClass { }                                        │
│                                                                          │
│   class Child extends ImmutableClass { }  // ❌ Cannot extend!          │
│                                                                          │
│   Examples: String, Integer, Math are all final classes                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// Practical example: Constants
public class AppConstants {
    public static final String APP_NAME = "MyApp";
    public static final int MAX_CONNECTIONS = 100;
    public static final double PI = 3.14159;
}

// Practical example: Immutable class
public final class ImmutableUser {
    private final String name;
    private final int age;
    
    public ImmutableUser(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    // No setters - cannot modify after creation
}
```

---

## Q13: What are the rules for method overloading?

**Answer:**

Method overloading allows multiple methods with the **same name** but **different parameters**.

```java
public class OverloadingRules {
    
    // ✅ VALID: Different number of parameters
    void print(int a) { }
    void print(int a, int b) { }
    
    // ✅ VALID: Different types of parameters
    void display(int a) { }
    void display(String a) { }
    
    // ✅ VALID: Different order of parameters
    void process(int a, String b) { }
    void process(String a, int b) { }
    
    // ❌ INVALID: Just different return type
    // int calculate() { return 0; }
    // double calculate() { return 0.0; }  // Compile error!
    
    // ❌ INVALID: Just different access modifier
    // public void test() { }
    // private void test() { }  // Compile error!
    
    // ❌ INVALID: Just different parameter name
    // void show(int number) { }
    // void show(int value) { }  // Compile error! Same signature
}
```

### Rules Summary:

| Rule | Valid? | Example |
|------|--------|---------|
| Different parameter count | ✅ | `add(int)` vs `add(int, int)` |
| Different parameter types | ✅ | `add(int)` vs `add(double)` |
| Different parameter order | ✅ | `add(int, String)` vs `add(String, int)` |
| Only different return type | ❌ | `int get()` vs `double get()` |
| Only different access modifier | ❌ | `public` vs `private` |

---

## Q14: What is covariant return type?

**Answer:**

Covariant return type allows an overriding method to return a **subtype** of the return type declared in the parent method.

```java
class Animal {
    Animal reproduce() {
        return new Animal();
    }
}

class Dog extends Animal {
    @Override
    Dog reproduce() {  // ✅ Returns Dog (subtype of Animal) - Covariant!
        return new Dog();
    }
}

class Cat extends Animal {
    @Override
    Cat reproduce() {  // ✅ Returns Cat (subtype of Animal) - Covariant!
        return new Cat();
    }
}

// Usage - no casting needed!
Dog dog = new Dog();
Dog puppy = dog.reproduce();  // Returns Dog, not Animal

// Without covariant return:
Animal animal = new Dog();
Dog d = (Dog) animal.reproduce();  // Would need casting
```

### Benefits:
- **Type safety**: No need for explicit casting
- **Cleaner code**: More specific return types
- **Better API design**: Subclasses can return more specific types

---

## Q15: What is method hiding vs method overriding?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    METHOD HIDING vs OVERRIDING                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   METHOD OVERRIDING (Instance methods):                                  │
│   ────────────────────────────────────                                   │
│   • Runtime polymorphism                                                │
│   • Actual object type determines which method runs                     │
│                                                                          │
│   METHOD HIDING (Static methods):                                        │
│   ────────────────────────────────                                       │
│   • Compile-time binding                                                │
│   • Reference type determines which method runs                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
class Parent {
    // Instance method - can be overridden
    public void instanceMethod() {
        System.out.println("Parent instance method");
    }
    
    // Static method - can only be hidden, not overridden
    public static void staticMethod() {
        System.out.println("Parent static method");
    }
}

class Child extends Parent {
    @Override  // True override
    public void instanceMethod() {
        System.out.println("Child instance method");
    }
    
    // This HIDES parent's static method (not override!)
    public static void staticMethod() {
        System.out.println("Child static method");
    }
}

// Demonstration
Parent p = new Child();

p.instanceMethod();  // "Child instance method" - RUNTIME (actual object type)
p.staticMethod();    // "Parent static method" - COMPILE TIME (reference type)

Child c = new Child();
c.staticMethod();    // "Child static method" - reference is Child
```

| Aspect | Overriding | Hiding |
|--------|------------|--------|
| **Applies to** | Instance methods | Static methods |
| **Binding** | Runtime | Compile-time |
| **Polymorphism** | Yes | No |
| **`@Override`** | Recommended | Not applicable |

---

## Q16: What is the `instanceof` operator?

**Answer:**

The `instanceof` operator checks if an object is an instance of a specific class or interface.

```java
// Syntax
object instanceof Type  // Returns true or false

// Example
class Animal { }
class Dog extends Animal { }
class Cat extends Animal { }

Animal myDog = new Dog();
Animal myCat = new Cat();

// Type checking
System.out.println(myDog instanceof Animal);  // true
System.out.println(myDog instanceof Dog);     // true
System.out.println(myDog instanceof Cat);     // false
System.out.println(myDog instanceof Object);  // true (everything is Object)

// Practical use: Safe casting
public void processAnimal(Animal animal) {
    if (animal instanceof Dog) {
        Dog dog = (Dog) animal;  // Safe to cast
        dog.bark();
    } else if (animal instanceof Cat) {
        Cat cat = (Cat) animal;
        cat.meow();
    }
}

// Java 16+ Pattern matching instanceof
if (animal instanceof Dog dog) {  // Cast and assign in one step!
    dog.bark();  // dog is already cast and available
}
```

### Important Notes:
- Returns `false` if object is `null`
- Checks both class inheritance and interface implementation
- Useful before downcasting to avoid `ClassCastException`

---

## Q17: What is upcasting and downcasting?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    UPCASTING vs DOWNCASTING                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│         Animal (Parent)                                                  │
│            ▲                                                             │
│            │                                                              │
│          ┌─┴─┐                                                           │
│          │Dog│ (Child)                                                   │
│          └───┘                                                           │
│                                                                          │
│   UPCASTING: Child → Parent (Implicit, Safe)                            │
│   ───────────────────────────────────────────                            │
│   Animal animal = new Dog();  // ✅ Automatic                           │
│                                                                          │
│   DOWNCASTING: Parent → Child (Explicit, Risky)                         │
│   ─────────────────────────────────────────────                          │
│   Dog dog = (Dog) animal;  // Requires cast, may throw exception        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
class Animal {
    void eat() { System.out.println("Animal eating"); }
}

class Dog extends Animal {
    void eat() { System.out.println("Dog eating"); }
    void bark() { System.out.println("Woof!"); }
}

// UPCASTING - Child to Parent (always safe)
Dog dog = new Dog();
Animal animal = dog;  // Implicit upcast - no cast operator needed
animal.eat();         // "Dog eating" - polymorphism works!
// animal.bark();     // ❌ Compile error - Animal doesn't have bark()

// DOWNCASTING - Parent to Child (must be explicit)
Animal animal2 = new Dog();  // Actual object is Dog
Dog dog2 = (Dog) animal2;    // ✅ Works - actual object IS a Dog
dog2.bark();                 // Now we can access Dog methods

// DANGEROUS DOWNCAST
Animal animal3 = new Animal();  // Actual object is Animal
Dog dog3 = (Dog) animal3;       // ❌ ClassCastException at runtime!

// SAFE DOWNCASTING
if (animal2 instanceof Dog) {
    Dog safeDog = (Dog) animal2;  // Safe!
    safeDog.bark();
}
```

---

## Q18: What is the difference between `==` and `equals()`?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    == vs equals()                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   == (Reference Comparison)                                              │
│   ─────────────────────────                                              │
│   • Compares memory addresses (references)                              │
│   • Checks if two variables point to SAME object                        │
│                                                                          │
│   equals() (Content Comparison)                                          │
│   ─────────────────────────────                                          │
│   • Compares actual values/content                                      │
│   • Can be overridden to define "logical equality"                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// String example
String s1 = new String("Hello");
String s2 = new String("Hello");
String s3 = s1;

System.out.println(s1 == s2);       // false - different objects in memory
System.out.println(s1.equals(s2));  // true - same content
System.out.println(s1 == s3);       // true - same reference

// String pool special case
String s4 = "Hello";
String s5 = "Hello";
System.out.println(s4 == s5);       // true - same object in string pool!

// Custom class - must override equals()
public class User {
    private Long id;
    private String name;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);  // Compare by ID
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);  // Must override hashCode too!
    }
}
```

| Aspect | `==` | `equals()` |
|--------|------|------------|
| **Compares** | References (memory) | Content (logical) |
| **For primitives** | Value comparison | N/A |
| **For objects** | Same object? | Same content? |
| **Can override** | No | Yes |
| **Null-safe** | Yes | Must check for null |

---

## Q19: Why override `hashCode()` when overriding `equals()`?

**Answer:**

The **contract** between `equals()` and `hashCode()` requires:
- If two objects are **equal** (equals() returns true), they **must** have the same hashCode
- If two objects have the same hashCode, they **may or may not** be equal

```java
// Without proper hashCode - HashMap/HashSet breaks!
public class User {
    private Long id;
    private String name;
    
    // Only equals() overridden - BROKEN!
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return this.id.equals(((User) obj).id);
        }
        return false;
    }
    // hashCode() NOT overridden - uses Object's default (memory address)
}

// Problem demonstration
User u1 = new User(1L, "John");
User u2 = new User(1L, "John");

System.out.println(u1.equals(u2));  // true

Set<User> set = new HashSet<>();
set.add(u1);
set.contains(u2);  // false! u2 has different hashCode, goes to different bucket

Map<User, String> map = new HashMap<>();
map.put(u1, "data");
map.get(u2);  // null! Can't find u2 even though u1.equals(u2)

// CORRECT implementation
@Override
public int hashCode() {
    return Objects.hash(id);  // Same field used in equals()
}
```

### Rules:
1. Always override `hashCode()` when you override `equals()`
2. Use the same fields in both methods
3. `Objects.hash()` is a convenient helper

---

## Q20: What is an immutable class? How do you create one?

**Answer:**

An **immutable class** is a class whose instances cannot be modified after creation.

```java
// Example: Built-in immutable classes
String, Integer, Long, Double, LocalDate, BigDecimal

// Creating an immutable class
public final class ImmutablePerson {  // 1. Make class final
    
    private final String name;         // 2. Make fields private final
    private final int age;
    private final List<String> hobbies;
    
    // 3. Initialize all fields in constructor
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 4. Deep copy mutable objects
        this.hobbies = new ArrayList<>(hobbies);
    }
    
    // 5. Only getters, no setters
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    // 6. Return copies of mutable objects
    public List<String> getHobbies() {
        return new ArrayList<>(hobbies);  // Defensive copy
    }
    
    // 7. To "modify", return a new instance
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(this.name, newAge, this.hobbies);
    }
}

// Usage
ImmutablePerson person = new ImmutablePerson("John", 25, Arrays.asList("Reading"));
ImmutablePerson older = person.withAge(26);  // New object, original unchanged
```

### Rules for Immutability:
1. Declare class as `final`
2. Make all fields `private final`
3. No setter methods
4. Initialize via constructor
5. Deep copy mutable objects in constructor
6. Return defensive copies from getters

### Benefits:
- Thread-safe without synchronization
- Can be freely shared
- Good for hash keys (hashCode never changes)
- Simpler to reason about

---

## Q21: What is the diamond problem and how does Java solve it?

**Answer:**

The **diamond problem** occurs in multiple inheritance when a class inherits from two classes that have a common ancestor.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    DIAMOND PROBLEM                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│           ┌───────────────┐                                              │
│           │   GrandParent │                                              │
│           │   + method()  │                                              │
│           └───────┬───────┘                                              │
│                   │                                                       │
│           ┌───────┴───────┐                                              │
│           │               │                                               │
│     ┌─────▼─────┐   ┌─────▼─────┐                                       │
│     │  Parent1  │   │  Parent2  │   Both override method()               │
│     │ +method() │   │ +method() │                                        │
│     └─────┬─────┘   └─────┬─────┘                                       │
│           │               │                                               │
│           └───────┬───────┘                                              │
│                   │                                                       │
│             ┌─────▼─────┐                                                │
│             │   Child   │   ❓ Which method() to use?                    │
│             └───────────┘                                                │
│                                                                          │
│   Java's Solution: NO multiple inheritance with classes!                 │
│   Instead: Use interfaces with default methods                           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// Java's solution with interfaces
interface Flyable {
    default void travel() {
        System.out.println("Flying...");
    }
}

interface Swimmable {
    default void travel() {
        System.out.println("Swimming...");
    }
}

// Class must resolve conflict explicitly
class Duck implements Flyable, Swimmable {
    
    @Override
    public void travel() {
        // Option 1: Provide own implementation
        System.out.println("Duck waddles...");
        
        // Option 2: Call specific interface method
        Flyable.super.travel();    // "Flying..."
        // OR
        Swimmable.super.travel();  // "Swimming..."
    }
}
```

### Java's Solutions:
1. **No multiple class inheritance** - avoids the problem entirely
2. **Multiple interface inheritance** - allowed, with conflict resolution rules
3. **Explicit resolution** - programmer must resolve conflicting default methods

---

## Q22: What is the difference between interface and abstract class?

**Answer:**

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| **Inheritance** | Single (extends) | Multiple (implements) |
| **Methods** | Abstract + concrete | Abstract + default (Java 8+) |
| **Variables** | Any type | public static final only |
| **Constructor** | Yes | No |
| **Access modifiers** | Any | public only |
| **When to use** | Shared code + "is-a" | Capability/contract + "can-do" |

```java
// Abstract class - for related classes with shared code
abstract class Animal {
    protected String name;  // Instance variable
    
    public Animal(String name) {  // Constructor
        this.name = name;
    }
    
    abstract void makeSound();  // Must implement
    
    void sleep() {  // Shared implementation
        System.out.println(name + " is sleeping");
    }
}

// Interface - for capabilities across unrelated classes
interface Comparable<T> {
    int compareTo(T other);  // Contract method
    
    default boolean isGreaterThan(T other) {  // Default implementation
        return compareTo(other) > 0;
    }
}

// A class can extend one class but implement multiple interfaces
class Dog extends Animal implements Comparable<Dog>, Serializable {
    public Dog(String name) { super(name); }
    
    @Override
    void makeSound() { System.out.println("Woof!"); }
    
    @Override
    public int compareTo(Dog other) { 
        return this.name.compareTo(other.name); 
    }
}
```

---

## Q23: What are marker interfaces?

**Answer:**

A **marker interface** is an interface with no methods, used to indicate a special behavior or capability to the JVM/framework.

```java
// Built-in marker interfaces
public interface Serializable { }  // Can be serialized
public interface Cloneable { }     // Can be cloned
public interface RandomAccess { }  // Supports fast random access

// How they're used
class User implements Serializable {
    private Long id;
    private String name;
    // No methods to implement - just marks the class!
}

// JVM/Framework checks for the marker
if (obj instanceof Serializable) {
    // OK to serialize
    ObjectOutputStream oos = new ObjectOutputStream(stream);
    oos.writeObject(obj);
}

// Without Cloneable, clone() throws CloneNotSupportedException
class MyClass implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Now works!
    }
}
```

### Modern Alternative: Annotations
```java
// Annotations replaced many marker interfaces
@Entity          // Marks as JPA entity (instead of interface)
@Component       // Marks as Spring component
@Deprecated      // Marks as deprecated
```

---

## Q24: What is object cloning?

**Answer:**

**Cloning** creates a copy of an object. Java supports **shallow copy** and **deep copy**.

```java
// Shallow Copy - copies references (not nested objects)
class Address {
    String city;
    Address(String city) { this.city = city; }
}

class Person implements Cloneable {
    String name;
    Address address;  // Reference type
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // SHALLOW COPY
    }
}

Person p1 = new Person("John", new Address("NYC"));
Person p2 = (Person) p1.clone();

p1.address.city = "LA";
System.out.println(p2.address.city);  // "LA" - SAME address object!

// Deep Copy - copies nested objects too
class Person implements Cloneable {
    String name;
    Address address;
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Person cloned = (Person) super.clone();
        cloned.address = new Address(this.address.city);  // New Address
        return cloned;
    }
}

Person p3 = (Person) p1.clone();
p1.address.city = "SF";
System.out.println(p3.address.city);  // "NYC" - Independent copy!
```

### Alternatives to clone():
```java
// Copy constructor (preferred)
public Person(Person other) {
    this.name = other.name;
    this.address = new Address(other.address.city);
}

// Static factory method
public static Person copyOf(Person original) {
    return new Person(original.name, new Address(original.address.city));
}
```

---

## Q25: What is the `Object` class and its important methods?

**Answer:**

`Object` is the **root class** of all Java classes. Every class implicitly extends Object.

```java
public class Object {
    // Most commonly overridden
    public boolean equals(Object obj);     // Compare equality
    public int hashCode();                 // Hash code for collections
    public String toString();              // String representation
    
    // Cloning
    protected Object clone();              // Create copy
    
    // Thread-related
    public void wait();                    // Wait for notification
    public void notify();                  // Notify one waiting thread
    public void notifyAll();               // Notify all waiting threads
    
    // Reflection
    public Class<?> getClass();            // Get runtime class
    
    // Cleanup (deprecated)
    protected void finalize();             // Called before GC (deprecated)
}

// Example: Override important methods
public class User {
    private Long id;
    private String name;
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "'}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

---

## Q26: What is method signature in Java?

**Answer:**

A **method signature** consists of the method **name** and **parameter list** (types and order).

```java
public class SignatureExample {
    
    // Signature: calculate(int, int)
    public int calculate(int a, int b) {
        return a + b;
    }
    
    // Different signature: calculate(double, double)
    public double calculate(double a, double b) {
        return a + b;
    }
    
    // Different signature: calculate(int, int, int)
    public int calculate(int a, int b, int c) {
        return a + b + c;
    }
    
    // SAME signature as first - COMPILE ERROR!
    // Return type is NOT part of signature
    // public double calculate(int a, int b) { return a + b; }
    
    // Parameter names don't matter - COMPILE ERROR!
    // public int calculate(int x, int y) { return x + y; }
}
```

### What's Included in Signature:
- ✅ Method name
- ✅ Parameter types
- ✅ Parameter order
- ❌ Return type
- ❌ Parameter names
- ❌ Access modifiers
- ❌ Exceptions thrown

---

## Q27: What are nested classes in Java?

**Answer:**

Java supports four types of nested classes:

```java
public class OuterClass {
    private int outerField = 10;
    
    // 1. STATIC NESTED CLASS
    // - Cannot access outer instance members directly
    // - Created: OuterClass.StaticNested obj = new OuterClass.StaticNested();
    static class StaticNested {
        void display() {
            // System.out.println(outerField);  // ❌ Cannot access
            System.out.println("Static nested class");
        }
    }
    
    // 2. INNER CLASS (Non-static nested class)
    // - Can access all outer members
    // - Created: outer.new InnerClass();
    class InnerClass {
        void display() {
            System.out.println(outerField);  // ✅ Can access
        }
    }
    
    void method() {
        // 3. LOCAL CLASS (inside a method)
        class LocalClass {
            void display() {
                System.out.println(outerField);  // ✅ Can access
            }
        }
        new LocalClass().display();
    }
}

// 4. ANONYMOUS CLASS (no name, inline)
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        System.out.println("Anonymous class");
    }
};

// Modern alternative: Lambda (for functional interfaces)
Runnable lambdaRunnable = () -> System.out.println("Lambda");
```

| Type | Static? | Access Outer Members | Created |
|------|---------|---------------------|---------|
| Static Nested | Yes | Only static | `Outer.Nested()` |
| Inner Class | No | All | `outer.new Inner()` |
| Local Class | No | All | Inside method |
| Anonymous | No | All | Inline at declaration |

---

## Q28: What is the difference between shallow copy and deep copy?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SHALLOW vs DEEP COPY                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SHALLOW COPY:                                                          │
│   ─────────────                                                          │
│   ┌─────────────┐      ┌─────────────┐                                  │
│   │  Original   │      │    Copy     │                                  │
│   │ name="John" │      │ name="John" │  ← Primitive copied             │
│   │ address ────┼──┐   │ address ────┼──┐                               │
│   └─────────────┘  │   └─────────────┘  │                               │
│                    │                     │                               │
│                    └──────────┬──────────┘                               │
│                               │                                          │
│                    ┌──────────▼──────────┐                               │
│                    │  Address (shared!)   │  ← Reference shared!        │
│                    │  city = "NYC"        │                              │
│                    └─────────────────────┘                               │
│                                                                          │
│   DEEP COPY:                                                             │
│   ──────────                                                             │
│   ┌─────────────┐      ┌─────────────┐                                  │
│   │  Original   │      │    Copy     │                                  │
│   │ name="John" │      │ name="John" │                                  │
│   │ address ────┼──┐   │ address ────┼──┐                               │
│   └─────────────┘  │   └─────────────┘  │                               │
│                    │                     │                               │
│         ┌──────────▼──────────┐   ┌─────▼─────────────┐                 │
│         │     Address 1       │   │     Address 2     │                 │
│         │   city = "NYC"      │   │   city = "NYC"    │                 │
│         └─────────────────────┘   └───────────────────┘                 │
│                                   ↑ Separate copy!                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// Deep copy implementation
public class Person {
    private String name;
    private Address address;
    
    // Deep copy constructor
    public Person(Person other) {
        this.name = other.name;  // String is immutable, OK to share
        this.address = new Address(other.address);  // Create new Address
    }
}

// Using serialization for deep copy (works for any Serializable)
public static <T> T deepCopy(T object) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(object);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    return (T) ois.readObject();
}
```

---

## Q29: What is coupling and cohesion?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    COUPLING and COHESION                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   COUPLING = How dependent classes are on each other                    │
│   ───────────────────────────────────────────────────                    │
│   Goal: LOW coupling (loose coupling)                                   │
│                                                                          │
│   TIGHT COUPLING (Bad):                                                  │
│   class OrderService {                                                   │
│       private MySQLDatabase db;  // Directly depends on MySQL           │
│       void save() { db.executeQuery("INSERT..."); }                     │
│   }                                                                      │
│                                                                          │
│   LOOSE COUPLING (Good):                                                 │
│   class OrderService {                                                   │
│       private Database db;  // Depends on interface                     │
│       void save() { db.save(order); }  // Not tied to implementation   │
│   }                                                                      │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   COHESION = How focused a class is on a single purpose                 │
│   ──────────────────────────────────────────────────────                 │
│   Goal: HIGH cohesion                                                   │
│                                                                          │
│   LOW COHESION (Bad):                                                    │
│   class UserManager {                                                    │
│       void createUser() { }                                              │
│       void sendEmail() { }    // Not related to user management         │
│       void generateReport() { }  // Not related                         │
│       void backupDatabase() { }  // Definitely not related!            │
│   }                                                                      │
│                                                                          │
│   HIGH COHESION (Good):                                                  │
│   class UserService {                                                    │
│       void createUser() { }                                              │
│       void updateUser() { }   // All related to users                   │
│       void deleteUser() { }                                              │
│   }                                                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Recipe for Good Design:
- **Low Coupling + High Cohesion = Maintainable Code**
- Use interfaces to reduce coupling
- Keep classes focused on single responsibility

---

## Q30: What is the Liskov Substitution Principle (LSP)?

**Answer:**

**LSP states**: Objects of a superclass should be replaceable with objects of subclasses without breaking the program.

```java
// VIOLATION of LSP
class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // Square must have equal sides
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;  // Square must have equal sides
        this.height = height;
    }
}

// This breaks code expecting Rectangle behavior!
void resize(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    assert r.getArea() == 50;  // ❌ FAILS for Square (100)!
}

// CORRECT approach - don't inherit if behavior differs
interface Shape {
    int getArea();
}

class Rectangle implements Shape { /* width, height */ }
class Square implements Shape { /* side */ }
```

### Signs of LSP Violation:
- Subclass throws exceptions parent doesn't
- Subclass ignores or overrides parent methods with empty body
- Type checking with `instanceof` everywhere

---

## Q31: What is the difference between association, aggregation, and composition?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│              ASSOCIATION vs AGGREGATION vs COMPOSITION                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   ASSOCIATION (General relationship)                                     │
│   ────────────────────────────────────                                   │
│   • General "uses" or "knows about" relationship                        │
│   • No ownership implied                                                │
│   • Example: Teacher teaches Student                                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   AGGREGATION (Weak "has-a")                                             │
│   ──────────────────────────                                             │
│   • Part can exist without the whole                                    │
│   • Weak ownership                                                       │
│   • Example: Department has Employees (employees can leave)             │
│   • UML: Empty diamond (◇)                                              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   COMPOSITION (Strong "has-a")                                           │
│   ────────────────────────────                                           │
│   • Part cannot exist without the whole                                 │
│   • Strong ownership - whole controls lifecycle                         │
│   • Example: House has Rooms (rooms don't exist without house)          │
│   • UML: Filled diamond (◆)                                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
// Association - uses/knows about
class Teacher {
    void teach(Student student) {  // Just uses Student
        student.learn();
    }
}

// Aggregation - weak "has-a"
class Department {
    private List<Employee> employees;  // Employees passed in
    
    public Department(List<Employee> employees) {
        this.employees = employees;  // Reference, not ownership
    }
}

// Composition - strong "has-a"
class House {
    private final List<Room> rooms;
    
    public House() {
        this.rooms = new ArrayList<>();
        rooms.add(new Room("Living"));  // House creates and owns rooms
        rooms.add(new Room("Bedroom"));
    }
}
```

---

## Q32: What is the Single Responsibility Principle (SRP)?

**Answer:**

**SRP states**: A class should have only **one reason to change** (one responsibility).

```java
// ❌ VIOLATES SRP - Multiple responsibilities
class Employee {
    void calculatePay() { }      // Payroll logic
    void saveToDatabase() { }    // Persistence logic
    void generateReport() { }    // Reporting logic
}
// Problem: Changes to payroll, database, or reporting all affect this class

// ✅ FOLLOWS SRP - Single responsibility each
class Employee {
    private Long id;
    private String name;
    private BigDecimal salary;
    // Just domain data
}

class PayrollService {
    void calculatePay(Employee emp) { }  // Only payroll
}

class EmployeeRepository {
    void save(Employee emp) { }  // Only persistence
}

class EmployeeReportGenerator {
    void generate(Employee emp) { }  // Only reporting
}
```

### Benefits:
- Easier to understand and test
- Changes are isolated
- Lower coupling
- Better reusability

---

## Q33: What is the Open/Closed Principle (OCP)?

**Answer:**

**OCP states**: Software entities should be **open for extension** but **closed for modification**.

```java
// ❌ VIOLATES OCP - Must modify for new types
class AreaCalculator {
    double calculate(Object shape) {
        if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            return r.width * r.height;
        } else if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            return Math.PI * c.radius * c.radius;
        }
        // Must add new if-else for each new shape!
        return 0;
    }
}

// ✅ FOLLOWS OCP - Extend without modifying
interface Shape {
    double calculateArea();
}

class Rectangle implements Shape {
    double width, height;
    
    @Override
    public double calculateArea() {
        return width * height;
    }
}

class Circle implements Shape {
    double radius;
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

// New shape? Just add new class - no changes to existing code!
class Triangle implements Shape {
    double base, height;
    
    @Override
    public double calculateArea() {
        return 0.5 * base * height;
    }
}

class AreaCalculator {
    double calculate(Shape shape) {
        return shape.calculateArea();  // Works for any shape!
    }
}
```

---

## Q34: What is the Interface Segregation Principle (ISP)?

**Answer:**

**ISP states**: Clients should not be forced to depend on interfaces they don't use.

```java
// ❌ VIOLATES ISP - Fat interface
interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
}

class Robot implements Worker {
    void work() { /* works */ }
    void eat() { /* Robots don't eat! */ }      // Forced to implement
    void sleep() { /* Robots don't sleep! */ }  // Forced to implement
    void attendMeeting() { /* OK */ }
}

// ✅ FOLLOWS ISP - Segregated interfaces
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

interface MeetingAttendable {
    void attendMeeting();
}

class Human implements Workable, Eatable, Sleepable, MeetingAttendable {
    // Implements all - makes sense for humans
}

class Robot implements Workable, MeetingAttendable {
    // Only workable and meeting - no forced empty implementations
}
```

---

## Q35: What is the Dependency Inversion Principle (DIP)?

**Answer:**

**DIP states**:
1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
2. Abstractions should not depend on details. Details should depend on abstractions.

```java
// ❌ VIOLATES DIP - Direct dependency on concrete class
class OrderService {
    private MySQLDatabase database = new MySQLDatabase();  // Tight coupling!
    
    void saveOrder(Order order) {
        database.insert(order);  // Depends on MySQL implementation
    }
}

// ✅ FOLLOWS DIP - Depends on abstraction
interface Database {
    void insert(Object entity);
}

class MySQLDatabase implements Database {
    public void insert(Object entity) { /* MySQL logic */ }
}

class MongoDatabase implements Database {
    public void insert(Object entity) { /* MongoDB logic */ }
}

class OrderService {
    private final Database database;  // Depends on interface!
    
    // Dependency injected
    public OrderService(Database database) {
        this.database = database;
    }
    
    void saveOrder(Order order) {
        database.insert(order);  // Works with any database!
    }
}
```

---

## Q36: What is Dependency Injection (DI)?

**Answer:**

**Dependency Injection** is a design pattern where dependencies are provided ("injected") to a class rather than created internally.

```java
// WITHOUT DI - Creates its own dependencies
class OrderService {
    private PaymentGateway gateway = new StripeGateway();  // Hard to test!
    private EmailService email = new SmtpEmailService();    // Hard to change!
}

// WITH DI - Dependencies injected
class OrderService {
    private final PaymentGateway gateway;
    private final EmailService email;
    
    // Constructor Injection (preferred)
    public OrderService(PaymentGateway gateway, EmailService email) {
        this.gateway = gateway;
        this.email = email;
    }
}

// Types of Injection:

// 1. Constructor Injection
public OrderService(PaymentGateway gateway) {
    this.gateway = gateway;
}

// 2. Setter Injection
public void setPaymentGateway(PaymentGateway gateway) {
    this.gateway = gateway;
}

// 3. Field Injection (Spring)
@Autowired
private PaymentGateway gateway;
```

### Benefits:
- Loose coupling
- Easy to test (mock dependencies)
- Easy to swap implementations
- Follows DIP

---

## Q37: What is the difference between early binding and late binding?

**Answer:**

| Aspect | Early Binding | Late Binding |
|--------|--------------|--------------|
| **When** | Compile time | Runtime |
| **Also called** | Static binding | Dynamic binding |
| **Used for** | Overloaded methods, static methods | Overridden methods |
| **Performance** | Faster | Slightly slower |

```java
class Animal {
    // Static method - early binding
    static void breathe() {
        System.out.println("Animal breathing");
    }
    
    // Instance method - late binding
    void move() {
        System.out.println("Animal moving");
    }
}

class Dog extends Animal {
    static void breathe() {
        System.out.println("Dog breathing");
    }
    
    @Override
    void move() {
        System.out.println("Dog running");
    }
}

Animal animal = new Dog();

// EARLY BINDING - decided at compile time (reference type)
animal.breathe();  // "Animal breathing" - static method

// LATE BINDING - decided at runtime (actual object type)
animal.move();     // "Dog running" - overridden method
```

---

## Q38: What is a wrapper class in Java?

**Answer:**

**Wrapper classes** provide object representations of primitive types.

```
Primitive  →  Wrapper
───────────────────────
byte       →  Byte
short      →  Short
int        →  Integer
long       →  Long
float      →  Float
double     →  Double
char       →  Character
boolean    →  Boolean
```

```java
// Boxing - primitive to wrapper
int primitive = 10;
Integer wrapper = Integer.valueOf(primitive);  // Explicit boxing
Integer autoBox = primitive;                    // Autoboxing (Java 5+)

// Unboxing - wrapper to primitive
Integer wrapper2 = 20;
int primitive2 = wrapper2.intValue();  // Explicit unboxing
int autoUnbox = wrapper2;              // Auto-unboxing

// Why use wrappers?
// 1. Collections only work with objects
List<Integer> numbers = new ArrayList<>();
numbers.add(10);  // Works! (autoboxing)
// List<int> nums;  // ❌ Won't compile

// 2. Null handling
Integer canBeNull = null;  // OK
// int cannotBeNull = null;  // ❌ Compile error

// 3. Utility methods
int parsed = Integer.parseInt("123");
String binary = Integer.toBinaryString(10);  // "1010"
int max = Integer.MAX_VALUE;
```

---

## Q39: What is the `transient` keyword?

**Answer:**

The `transient` keyword excludes a field from serialization.

```java
public class User implements Serializable {
    private Long id;
    private String name;
    private transient String password;  // NOT serialized!
    private transient Connection dbConnection;  // NOT serialized!
    
    // Getters/setters
}

// Serialization
User user = new User(1L, "John", "secret123", connection);
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.ser"));
oos.writeObject(user);

// Deserialization
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.ser"));
User loadedUser = (User) ois.readObject();

System.out.println(loadedUser.getPassword());  // null (was transient)
System.out.println(loadedUser.getConnection()); // null (was transient)
```

### Use Cases:
- Sensitive data (passwords, credit cards)
- Derived/calculated fields
- Non-serializable objects (connections, threads)

---

## Q40: What is the `volatile` keyword?

**Answer:**

The `volatile` keyword ensures a variable is read from and written to main memory, not CPU cache.

```java
public class VolatileExample {
    private volatile boolean running = true;  // Visible to all threads
    
    public void stop() {
        running = false;  // Immediately visible to other threads
    }
    
    public void run() {
        while (running) {  // Always reads from main memory
            // Do work
        }
    }
}

// Without volatile:
// Thread 1 might cache 'running' and never see Thread 2's update

// With volatile:
// Thread 1 always reads current value from main memory
```

### Key Points:
- Guarantees visibility across threads
- Does NOT guarantee atomicity (use `synchronized` or `AtomicInteger` for atomic ops)
- Prevents compiler optimizations that could reorder code

---

## Q41: What is the builder pattern?

**Answer:**

The **Builder Pattern** provides a step-by-step way to construct complex objects.

```java
// Complex object with many fields
public class User {
    private final Long id;
    private final String name;
    private final String email;
    private final int age;
    private final String phone;
    private final String address;
    
    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }
    
    // Static Builder class
    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private int age;
        private String phone;
        private String address;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        // ... other setters
        
        public User build() {
            return new User(this);
        }
    }
}

// Usage - fluent, readable
User user = new User.Builder()
    .id(1L)
    .name("John")
    .email("john@email.com")
    .age(25)
    .build();

// Using Lombok
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}

User user = User.builder()
    .id(1L)
    .name("John")
    .build();
```

---

## Q42: What is the factory pattern?

**Answer:**

The **Factory Pattern** creates objects without exposing creation logic.

```java
// Product interface
interface Vehicle {
    void drive();
}

// Concrete products
class Car implements Vehicle {
    public void drive() { System.out.println("Driving car"); }
}

class Truck implements Vehicle {
    public void drive() { System.out.println("Driving truck"); }
}

class Motorcycle implements Vehicle {
    public void drive() { System.out.println("Riding motorcycle"); }
}

// Simple Factory
class VehicleFactory {
    public static Vehicle createVehicle(String type) {
        return switch (type.toLowerCase()) {
            case "car" -> new Car();
            case "truck" -> new Truck();
            case "motorcycle" -> new Motorcycle();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}

// Usage
Vehicle car = VehicleFactory.createVehicle("car");
car.drive();  // "Driving car"

// Benefits:
// - Encapsulates object creation
// - Easy to add new types
// - Client doesn't know concrete class
```

---

## Q43: What is the singleton pattern?

**Answer:**

The **Singleton Pattern** ensures a class has only one instance.

```java
// Thread-safe Singleton (Lazy initialization with double-checked locking)
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() { }  // Private constructor
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Enum Singleton (Best approach - thread-safe, serialization-safe)
public enum DatabaseConnection {
    INSTANCE;
    
    private Connection connection;
    
    DatabaseConnection() {
        // Initialize connection
    }
    
    public Connection getConnection() {
        return connection;
    }
}

// Usage
DatabaseConnection.INSTANCE.getConnection();

// Spring Singleton (via container)
@Component  // Singleton by default in Spring
public class MyService { }
```

---

## Q44: What is method chaining?

**Answer:**

**Method chaining** allows calling multiple methods on the same object in a single statement.

```java
public class StringBuilder {
    public StringBuilder append(String s) {
        // Append string
        return this;  // Return this for chaining!
    }
}

// Chained calls
String result = new StringBuilder()
    .append("Hello")
    .append(" ")
    .append("World")
    .toString();

// Custom class with chaining
public class QueryBuilder {
    private String table;
    private String whereClause;
    private int limit;
    
    public QueryBuilder from(String table) {
        this.table = table;
        return this;
    }
    
    public QueryBuilder where(String condition) {
        this.whereClause = condition;
        return this;
    }
    
    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }
    
    public String build() {
        return String.format("SELECT * FROM %s WHERE %s LIMIT %d",
            table, whereClause, limit);
    }
}

// Usage
String query = new QueryBuilder()
    .from("users")
    .where("age > 18")
    .limit(10)
    .build();
```

---

## Q45: What is tight coupling vs loose coupling with example?

**Answer:**

```java
// ❌ TIGHT COUPLING - Direct dependency on concrete class
class OrderService {
    private MySQLRepository repository = new MySQLRepository();
    private SmtpEmailSender emailSender = new SmtpEmailSender();
    
    void createOrder(Order order) {
        repository.save(order);      // Coupled to MySQL
        emailSender.send(order);     // Coupled to SMTP
    }
}
// Problems:
// - Cannot change database without modifying OrderService
// - Hard to unit test (no way to mock)
// - Cannot substitute implementation

// ✅ LOOSE COUPLING - Depends on interfaces
interface OrderRepository {
    void save(Order order);
}

interface EmailSender {
    void send(Order order);
}

class OrderService {
    private final OrderRepository repository;
    private final EmailSender emailSender;
    
    // Dependencies injected
    public OrderService(OrderRepository repo, EmailSender sender) {
        this.repository = repo;
        this.emailSender = sender;
    }
    
    void createOrder(Order order) {
        repository.save(order);    // Interface call
        emailSender.send(order);   // Interface call
    }
}

// Benefits:
// - Can swap MySQL for PostgreSQL without changing OrderService
// - Easy to test with mocks
// - Follows SOLID principles
```

---

## Q46: What is the difference between `throw` and `throws`?

**Answer:**

| Aspect | `throw` | `throws` |
|--------|---------|----------|
| **Purpose** | Actually throw exception | Declare possible exceptions |
| **Location** | Inside method body | Method signature |
| **Usage** | One exception at a time | Multiple exceptions allowed |
| **Keyword for** | Creating exception | Warning callers |

```java
public class ThrowVsThrows {
    
    // 'throws' - declares what exceptions method might throw
    public void readFile(String path) throws IOException, FileNotFoundException {
        
        if (path == null) {
            // 'throw' - actually throws the exception
            throw new IllegalArgumentException("Path cannot be null");
        }
        
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }
        
        // Read file...
    }
    
    // Caller must handle or propagate
    public void process() {
        try {
            readFile("data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## Q47: What is exception chaining?

**Answer:**

**Exception chaining** wraps one exception inside another to preserve the original cause.

```java
public void processOrder(Order order) throws OrderException {
    try {
        database.save(order);
    } catch (SQLException e) {
        // Chain the original cause
        throw new OrderException("Failed to save order", e);
    }
}

// Custom exception with chaining
public class OrderException extends Exception {
    public OrderException(String message, Throwable cause) {
        super(message, cause);  // Pass cause to parent
    }
}

// Later, you can retrieve the original cause
try {
    processOrder(order);
} catch (OrderException e) {
    System.out.println("Error: " + e.getMessage());
    
    Throwable originalCause = e.getCause();
    System.out.println("Caused by: " + originalCause.getMessage());
}

// Output:
// Error: Failed to save order
// Caused by: Connection timed out
```

---

## Q48: What is a POJO?

**Answer:**

**POJO (Plain Old Java Object)** is a simple Java class with no special restrictions.

```java
// ✅ POJO - Simple, no framework dependencies
public class User {
    private Long id;
    private String name;
    private String email;
    
    // Constructors
    public User() { }
    
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... other getters/setters
}

// ❌ NOT a POJO - extends framework class
class MyServlet extends HttpServlet { }

// ❌ NOT a POJO - implements framework interface  
class MyBean implements EntityBean { }
```

### Related Terms:
- **JavaBean**: POJO + no-arg constructor + getters/setters + serializable
- **DTO (Data Transfer Object)**: POJO used for data transfer
- **Entity**: POJO mapped to database table

---

## Q49: What is the difference between object state and behavior?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    STATE vs BEHAVIOR                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   STATE = What the object IS (data/attributes)                          │
│   ─────────────────────────────────────────────                          │
│   • Instance variables                                                  │
│   • Current values                                                       │
│                                                                          │
│   BEHAVIOR = What the object CAN DO (actions/methods)                   │
│   ─────────────────────────────────────────────────                      │
│   • Methods                                                              │
│   • Operations on state                                                 │
│                                                                          │
│   CLASS: Car                                                            │
│   ────────────────────────────────────────────────────                  │
│   STATE (Fields):                                                       │
│   │ brand = "Toyota"                                                    │
│   │ color = "Red"                                                       │
│   │ speed = 60                                                          │
│   │ engineRunning = true                                                │
│   │                                                                      │
│   BEHAVIOR (Methods):                                                   │
│   │ start() → changes engineRunning to true                            │
│   │ stop() → changes engineRunning to false                            │
│   │ accelerate() → increases speed                                     │
│   │ brake() → decreases speed                                          │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
public class Car {
    // STATE - instance variables
    private String brand;
    private String color;
    private int speed;
    private boolean engineRunning;
    
    // BEHAVIOR - methods
    public void start() {
        engineRunning = true;  // Modifies state
    }
    
    public void accelerate(int amount) {
        if (engineRunning) {
            speed += amount;   // Modifies state
        }
    }
    
    public int getSpeed() {
        return speed;          // Returns state
    }
}
```

---

## Q50: How does garbage collection work with object references?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    GARBAGE COLLECTION & REFERENCES                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   REACHABLE OBJECTS (Not GC'd):                                         │
│   ─────────────────────────────                                          │
│   • Referenced from stack (local variables)                             │
│   • Referenced from static fields                                       │
│   • References reachable from other reachable objects                   │
│                                                                          │
│   UNREACHABLE OBJECTS (Will be GC'd):                                   │
│   ──────────────────────────────────                                     │
│   • No references pointing to them                                      │
│   • Only have circular references among themselves                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

```java
public class GCExample {
    
    public static void main(String[] args) {
        // user1 is reachable
        User user1 = new User("John");
        
        // user2 is reachable
        User user2 = new User("Jane");
        
        // Now user2 is unreachable - eligible for GC
        user2 = null;
        
        // Object referenced by user1 now unreachable
        user1 = new User("Bob");
        // "John" object eligible for GC
        
        // Circular reference - STILL gets garbage collected!
        Node a = new Node();
        Node b = new Node();
        a.next = b;
        b.next = a;
        
        a = null;
        b = null;
        // Both nodes eligible for GC (no external references)
    }
}

// Reference types
// Strong Reference - normal, prevents GC
User user = new User();

// Weak Reference - allows GC when memory needed
WeakReference<User> weakRef = new WeakReference<>(new User());

// Soft Reference - GC'd only when memory is low
SoftReference<User> softRef = new SoftReference<>(new User());
```

### Ways Objects Become Eligible for GC:
1. Nullifying reference: `user = null;`
2. Reassigning reference: `user = new User();`
3. Reference goes out of scope (method ends)
4. Island of isolation (circular refs only among unreachable objects)

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Encapsulation** | Hide data, expose API through methods |
| **Inheritance** | Child class inherits from parent class |
| **Polymorphism** | Same method, different behavior |
| **Abstraction** | Hide complexity, show simple interface |
| **Class** | Blueprint/template for objects |
| **Object** | Instance of a class with state |
| **Constructor** | Initialize object state when created |
| **Access Modifiers** | Control visibility (public/protected/default/private) |
| **this** | Reference to current object |
| **super** | Reference to parent class |
| **Aggregation** | Weak "has-a" (independent lifecycle) |
| **Composition** | Strong "has-a" (dependent lifecycle) |

---

> **🎉 Interview Prep Complete!**

All 11 topic documents have been generated. Good luck with your interviews!
