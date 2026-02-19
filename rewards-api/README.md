# Rewards API

A Spring Boot REST API for calculating customer reward points based on purchase transactions.

## Overview

This application implements a rewards program where customers earn points based on their purchases:
- **2 points** for every dollar spent over $100
- **1 point** for every dollar spent between $50 and $100
- **0 points** for amounts $50 or below

Example: A $120 purchase = 2×$20 + 1×$50 = 90 points

## Features

- Calculate reward points for individual customers
- Calculate reward points for all customers
- Monthly breakdown of points (dynamic, not hardcoded)
- Total points calculation over a 3-month period
- Create new transactions
- Comprehensive error handling
- Input validation
- Sample data initialization

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Data JPA**
- **MySQL** (production)
- **H2 Database** (testing)
- **Lombok**
- **Maven**
- **JUnit 5 & Mockito** (testing)

## Project Structure

```
src/
├── main/
│   ├── java/com/retail/rewards/
│   │   ├── controller/          # REST controllers
│   │   │   ├── RewardsController.java
│   │   │   └── TransactionController.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── CustomerRewardsDto.java
│   │   │   └── TransactionDto.java
│   │   ├── entity/              # JPA entities
│   │   │   ├── Customer.java
│   │   │   └── Transaction.java
│   │   ├── exception/           # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── repository/          # Data access layer
│   │   │   ├── CustomerRepository.java
│   │   │   └── TransactionRepository.java
│   │   ├── service/             # Business logic
│   │   │   ├── RewardsService.java
│   │   │   └── TransactionService.java
│   │   ├── util/                # Utility classes
│   │   │   └── RewardsCalculator.java
│   │   ├── DataInitializer.java # Sample data loader
│   │   └── RewardsApiApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/retail/rewards/
    │   ├── controller/          # Integration tests
    │   ├── service/             # Unit tests
    │   └── util/                # Unit tests
    └── resources/
        └── application.properties
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (for production)

## Database Setup

### MySQL Configuration

1. Create a database:
```sql
CREATE DATABASE rewards_db;
```

2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rewards_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Running the Application

### Using Maven

```bash
# Clean and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Get Rewards for a Specific Customer

```http
GET /api/rewards/customer/{customerId}
```

**Response:**
```json
{
  "customerId": 1,
  "customerName": "Sample Customer Name",
  "monthlyPoints": {
    "2025-11": 90,
    "2025-12": 250,
    "2026-01": 150
  },
  "totalPoints": 490
}
```

### Get Rewards for All Customers

```http
GET /api/rewards/customers
```

**Response:**
```json
[
  {
    "customerId": 1,
    "customerName": "Sample Customer Name",
    "monthlyPoints": {
      "2025-12": 340
    },
    "totalPoints": 340
  },
  {
    "customerId": 2,
    "customerName": "Sample Customer Name 2",
    "monthlyPoints": {
      "2025-12": 450
    },
    "totalPoints": 450
  }
]
```

### Create a Transaction

```http
POST /api/transactions
Content-Type: application/json

{
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2025-12-15"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 120.00,
  "transactionDate": "2025-12-15"
}
```

## Sample Data

The application automatically loads sample data on startup with:
- 3 customers (Deepanshu, Raj and Gagan)
- 15 transactions spread across the last 3 months
- Various transaction amounts to demonstrate the points calculation

## Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage

The project includes:
- **Unit Tests**: RewardsCalculator, RewardsService
- **Integration Tests**: RewardsController, TransactionController
- **Parameterized Tests**: Multiple test scenarios with different amounts
- **Negative Test Cases**: Invalid inputs, missing data, edge cases
- **Exception Handling Tests**: Resource not found, validation errors

### Test Scenarios Covered

1. **Positive Scenarios**
   - Valid transaction amounts
   - Multiple customers with multiple transactions
   - Monthly breakdown calculations
   - Total points aggregation

2. **Negative Scenarios**
   - Customer not found
   - Invalid transaction amounts (negative, zero, null)
   - Missing required fields
   - Invalid customer ID

3. **Edge Cases**
   - Amounts exactly at thresholds ($50, $100)
   - Amounts just above/below thresholds
   - No transactions for a customer
   - Transactions outside the 3-month window

## Implementation Details

### Rewards Calculation Logic

The `RewardsCalculator` utility class implements the points calculation:

```java
// For $120 purchase:
// - $20 over $100 = 20 × 2 = 40 points
// - $50 between $50-$100 = 50 × 1 = 50 points
// Total = 90 points
```

### Dynamic Month Calculation

The application dynamically calculates the last 3 months from the current date:
```java
LocalDate endDate = LocalDate.now();
LocalDate startDate = endDate.minusMonths(3);
```

### Error Handling

Global exception handler provides consistent error responses:
- `404 Not Found` - Resource doesn't exist
- `400 Bad Request` - Validation errors
- `500 Internal Server Error` - Unexpected errors

## Code Quality

- **Java Coding Standards**: Follows standard naming conventions
- **Package Structure**: Organized by layer (controller, service, repository, etc.)
- **JavaDoc**: Comprehensive documentation at class and method levels
- **Lombok**: Reduces boilerplate code
- **Validation**: Input validation using Bean Validation
- **Exception Handling**: Centralized error handling
- **Code Formatting**: Consistent formatting throughout
