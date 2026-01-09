# 📚 Digital Bookstore Management System

A comprehensive microservices-based backend system for managing a digital bookstore. Built with **Spring Boot 4.0**, **Spring Cloud 2025.1.0**, and modern cloud-native patterns.

---

## 🏗️ Architecture Overview

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
   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
   │  User   │ │  Auth   │ │Inventory│ │  Book   │ │  Order  │ │ Review  │ │   ...   │
   │ Service │ │ Service │ │ Service │ │ Service │ │ Service │ │ Service │ │         │
   │  9082   │ │  9081   │ │  9083   │ │  9084   │ │  9085   │ │  9086   │ │         │
   └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘
```

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 4.0.0 | Application Framework |
| **Spring Cloud** | 2025.1.0 | Microservices Infrastructure |
| **Spring Cloud Gateway** | WebFlux | API Gateway & Routing |
| **Netflix Eureka** | - | Service Discovery |
| **Spring Cloud Config** | - | Centralized Configuration |
| **Resilience4j** | - | Circuit Breaker & Resilience |
| **Spring Data JPA** | - | Data Persistence |
| **Lombok** | - | Boilerplate Reduction |
| **SpringDoc OpenAPI** | 3.0.1 | API Documentation (Swagger) |
| **JWT** | - | Authentication & Authorization |

---

## 📁 Project Structure

```
services/
├── 📂 eureka-service/          # Service Discovery Server
├── 📂 config-service/          # Centralized Configuration Server
├── 📂 api-gateway/             # API Gateway (Entry Point)
├── 📂 user-service/            # User Management
├── 📂 authentication-service/  # JWT Authentication
├── 📂 inventory-service/       # Stock & Inventory Management
├── 📂 book-service/            # Book Catalog Management
├── 📂 order-service/           # Order Processing
├── 📂 review-service/          # Book Reviews & Ratings
├── 📜 start-services.cmd       # Windows Startup Script
├── 📜 BookManagementAPI.postman_collection.json
└── 📜 README.md
```

---

## 🚀 Services

### 1. Eureka Service (Port: 8761)
**Service Discovery Server** - All microservices register here for dynamic discovery.

- **Dashboard**: http://localhost:8761
- Enables load balancing and service-to-service communication

### 2. Config Service (Port: 8888)
**Centralized Configuration Server** - Manages configuration for all services.

- Stores configurations in `config-repo/`

### 3. API Gateway (Port: 9080)
**Single Entry Point** - Routes all client requests to appropriate services.

- **Swagger UI**: http://localhost:9080/swagger-ui.html
- JWT token validation
- Rate limiting & Circuit breaker patterns
- CORS configuration
- Request/Response logging

### 4. User Service (Port: 9082)
**User Management** - CRUD operations for user accounts.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/users` | GET | Get all users |
| `/api/v1/users/{id}` | GET | Get user by ID |
| `/api/v1/users` | POST | Create user |
| `/api/v1/users/{id}` | PUT | Update user |
| `/api/v1/users/{id}` | DELETE | Delete user |

### 5. Authentication Service (Port: 9081)
**JWT Authentication** - Handles login, registration, and token management.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/register` | POST | Register new user |
| `/api/v1/auth/login` | POST | Login & get tokens |
| `/api/v1/auth/validate` | POST | Validate JWT token |
| `/api/v1/auth/refresh` | POST | Refresh access token |
| `/api/v1/auth/logout` | POST | Logout (blacklist token) |
| `/api/v1/auth/logout-all` | POST | Logout from all devices |

### 6. Book Service (Port: 9084)
**Book Catalog** - Manage book inventory and catalog.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/book/add` | POST | Add new book |
| `/api/v1/book/getAll` | GET | Get all books |
| `/api/v1/book/getById/{id}` | GET | Get book by ID |
| `/api/v1/book/getByAuthor/{authorId}` | GET | Get books by author |
| `/api/v1/book/getByCategory` | GET | Get books by category |
| `/api/v1/book/search` | GET | Search books by title |
| `/api/v1/book/update/{id}` | PATCH | Update book |
| `/api/v1/book/delete/{id}` | DELETE | Delete book |

### 7. Inventory Service (Port: 9083)
**Stock Management** - Track and manage book inventory levels.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/inventory/create` | POST | Create inventory record |
| `/api/v1/inventory` | GET | Get all inventory |
| `/api/v1/inventory/{id}` | GET | Get inventory by ID |
| `/api/v1/inventory/book/{bookId}` | GET | Get inventory by book |
| `/api/v1/inventory/book/{id}/availability` | GET | Check availability |
| `/api/v1/inventory/book/{id}/reduce` | PATCH | Reduce stock |
| `/api/v1/inventory/book/{id}/restock` | PATCH | Restock inventory |
| `/api/v1/inventory/low-stock` | GET | Get low stock alerts |

### 8. Order Service (Port: 9085)
**Order Processing** - Handle customer orders and transactions.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/order/place` | POST | Place new order |
| `/api/v1/order/{id}` | GET | Get order by ID |
| `/api/v1/order/user/{userId}` | GET | Get orders by user |
| `/api/v1/order/{id}/status` | PATCH | Update order status |
| `/api/v1/order/{id}/cancel` | PATCH | Cancel order |

### 9. Review Service (Port: 9086)
**Reviews & Ratings** - Manage book reviews and ratings.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/reviews` | POST | Create review |
| `/api/v1/reviews/{id}` | GET | Get review by ID |
| `/api/v1/reviews/book/{bookId}` | GET | Get reviews for book |
| `/api/v1/reviews/user/{userId}` | GET | Get reviews by user |
| `/api/v1/reviews/{id}` | PUT | Update review |
| `/api/v1/reviews/{id}` | DELETE | Delete review |

---

## ⚡ Quick Start

### Prerequisites
- **Java 21** or higher
- **Maven 3.8+**
- **MySQL 8.0+** (required for all services except Eureka and Config)

### Database Configuration

⚠️ **Important**: Before starting the services, configure your MySQL credentials in the config-service property files.

1. Navigate to `config-service/src/main/resources/config-repo/`
2. Update the database credentials in the following files:
   - `book-service.properties`
   - `order-service.properties`
   - `user-service.properties`
   - `review-service.properties`
   - `inventory-service.properties`

3. In each file, modify these properties with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/<database_name>?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=<your_mysql_username>
spring.datasource.password=<your_mysql_password>
```

| Service | Default Database Name |
|---------|----------------------|
| book-service | `bookstore_books` |
| order-service | `bookstore_order` |
| user-service | `bookstore_user` |
| review-service | `bookstore_review` |
| inventory-service | `bookstore_inventory` |

> **Note**: The databases will be created automatically if they don't exist (`createDatabaseIfNotExist=true`).

### Option 1: Using Start Script (Windows)

```cmd
cd services
start-services.cmd
```

This script automatically starts all services in the correct order and waits for each to be ready before starting the next.

### Option 2: Manual Start

Start services in this order:

```bash
# 1. Eureka Service (Service Discovery)
cd eureka-service
mvnw spring-boot:run

# 2. Config Service (Configuration Server)
cd config-service
mvnw spring-boot:run

# 3. API Gateway
cd api-gateway
mvnw spring-boot:run

# 4-9. Business Services (can be started in parallel)
cd user-service && mvnw spring-boot:run
cd authentication-service && mvnw spring-boot:run
cd inventory-service && mvnw spring-boot:run
cd book-service && mvnw spring-boot:run
cd order-service && mvnw spring-boot:run
cd review-service && mvnw spring-boot:run
```

### Verify Services

| Service | Health Check URL |
|---------|-----------------|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:9080/actuator/health |
| Config Service | http://localhost:8888/actuator/health |

---

## 🔐 Authentication

This system uses **JWT (JSON Web Tokens)** for authentication.

### Getting Started with Authentication

1. **Register a new user**:
```bash
curl -X POST http://localhost:9080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }'
```

2. **Login to get tokens**:
```bash
curl -X POST http://localhost:9080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

3. **Use the access token** for authenticated requests:
```bash
curl -X GET http://localhost:9080/api/v1/book/getAll \
  -H "Authorization: Bearer <your-access-token>"
```

### User Roles
- `CUSTOMER` - Can browse books, place orders, write reviews
- `ADMIN` - Full access to all resources

---

## 📖 API Documentation

### Swagger UI
Access the interactive API documentation at:
- **API Gateway Swagger**: http://localhost:9080/swagger-ui.html

### Postman Collection
Import the included Postman collection for testing:
```
BookManagementAPI.postman_collection.json
```

The collection includes:
- Pre-configured environment variables
- Auto-token extraction on login
- All endpoints organized by service

---

## ⚙️ Configuration

### MySQL Credentials

Update your MySQL credentials in the property files located at:

```text
config-service/src/main/resources/config-repo/
```

Each service property file contains:
```properties
spring.datasource.username=root           # Change to your MySQL username
spring.datasource.password=admin          # Change to your MySQL password
```

### Application Settings

Key configuration values in `application.properties` (shared config):

| Property | Description | Default |
|----------|-------------|---------|
| `eureka.client.service-url.defaultZone` | Eureka server URL | `http://localhost:8761/eureka/` |
| `jwt.secret` | JWT signing key | (development default) |
| `jwt.expiration` | Token expiration (ms) | `86400000` (24 hours) |
| `gateway.security.expected-token` | Gateway security token | (development default) |

### Centralized Configuration

All service configurations are managed in:
```
config-service/src/main/resources/config-repo/
├── application.properties          # Shared settings
├── api-gateway.properties
├── authentication-service.properties
├── book-service.properties
├── inventory-service.properties
├── order-service.properties
├── review-service.properties
└── user-service.properties
```

---

## 🛡️ Resilience Patterns

### Circuit Breaker (Resilience4j)
- **Sliding Window Size**: 10 calls
- **Failure Rate Threshold**: 50%
- **Wait Duration in Open State**: 10 seconds

### Rate Limiting
- **Limit**: 100 requests per second (configurable)

### Retry Pattern
- **Max Attempts**: 3
- **Exponential Backoff**: Enabled

---

## 🔧 Development

### Running Tests

```bash
# Run all tests for a service
cd <service-name>
mvnw test

# Run with coverage
mvnw verify
```

### Building for Production

```bash
# Build all services
cd <service-name>
mvnw clean package -DskipTests

# Run JAR
java -jar target/<service-name>-0.0.1-SNAPSHOT.jar
```

---

## 📊 Monitoring

### Actuator Endpoints

All services expose the following actuator endpoints:

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health status |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus metrics |

### Logs

Logs are stored in the `logs/` directory with the following configuration:
- **Max File Size**: 10MB
- **Max History**: 30 days
- **Total Size Cap**: 100MB

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is for educational purposes as part of the Cognizant Digital Bookstore Management System case study.

---

## 📞 Support

For questions or issues, please refer to the learning guides available in each service directory or open an issue in the repository.

---

<p align="center">
  Made By Shelfie Team 😎 
</p>
