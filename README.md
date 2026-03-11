# Retailer Rewards Calculator API

REST API for calculating customer reward points based on purchase transactions.

## Points Calculation Rules

- **$0 - $50**: 0 points
- **$50.01 - $100**: 1 point per dollar over $50
- **Over $100**: 2 points per dollar over $100 + 50 points for the $50-$100 tier

**Example:** A $120 purchase earns (20 × 2) + 50 = 90 points

## Package Structure

```
com.charter.reward_api
├── config/              # Configuration classes (OpenAPI)
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
├── exception/           # Custom exceptions and global exception handler
├── model/               # JPA entities (Customer, Transaction)
├── repository/          # Spring Data JPA repositories
└── service/             # Business logic layer
```

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8.0+ (or use H2 for in-memory database)

### Database Setup

#### Option 1: MySQL (Production)

1. **Install MySQL** (if not already installed)

2. **Create Database**
   ```sql
   CREATE DATABASE reward_db;
   ```

3. **Create Tables** (run in MySQL)
   ```sql
   CREATE TABLE customer (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL UNIQUE
   );

   CREATE TABLE transaction (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       customer_id BIGINT NOT NULL,
       amount DECIMAL(10,2) NOT NULL,
       transaction_date DATE NOT NULL,
       FOREIGN KEY (customer_id) REFERENCES customer(id)
   );
   ```

4. **Configure Database Connection**
   
   Set environment variables:
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/reward_db
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   ```
   
   Or update `src/main/resources/application.yaml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/reward_db
       username: root
       password: your_password
   ```

5. **Load Sample Data**
   
   The application automatically executes `data.sql` on startup to populate sample data. To manually load:
   ```bash
   mysql -u root -p reward_db < src/main/resources/data.sql
   ```

#### Option 2: H2 In-Memory Database (Development/Testing)

1. **Update `application.yaml`**
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:reward_db
       username: sa
       password:
       driver-class-name: org.h2.Driver
     jpa:
       hibernate:
         ddl-auto: create-drop
       properties:
         hibernate:
           dialect: org.hibernate.dialect.H2Dialect
     h2:
       console:
         enabled: true
   ```

2. **Tables are auto-created** by Hibernate and `data.sql` loads sample data automatically

### Steps

1. **Clone the repository**

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or with custom database settings:
   ```bash
   DB_URL=jdbc:mysql://localhost:3306/reward_db DB_USERNAME=root DB_PASSWORD=yourpass mvn spring-boot:run
   ```

4. **Verify database initialization**: Check logs for "Executed SQL script from class path resource [data.sql]"

### Access Points
- **API Base URL**: `http://localhost:8081/api/rewards`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **H2 Console** (if using H2): `http://localhost:8081/h2-console`
  - JDBC URL: `jdbc:h2:mem:reward_db`
  - Username: `sa`
  - Password: (leave empty)

## API Endpoints

### 1. Get All Customer Rewards (Paginated)

**Endpoint:** `GET /api/rewards`

**Query Parameters:**
- `page` (optional, default: 0) - Page number (>= 0)
- `size` (optional, default: 10) - Page size (>= 1)
- `from` (optional) - Start date (ISO-8601: yyyy-MM-dd)
- `to` (optional) - End date (ISO-8601: yyyy-MM-dd)

**Request Example:**
```http
GET /api/rewards?page=0&size=10&from=2024-01-01&to=2024-03-31
```

**Response Example:**
```json
{
  "content": [
    {
      "customerId": 1,
      "customerName": "Rajesh Kumar",
      "monthlyRewards": [
        {
          "year": 2024,
          "month": "JANUARY",
          "points": 90
        },
        {
          "year": 2024,
          "month": "FEBRUARY",
          "points": 275
        }
      ],
      "totalPoints": 365
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 3,
  "totalPages": 1,
  "last": true
}
```

### 2. Get Customer Rewards by ID

**Endpoint:** `GET /api/rewards/{customerId}`

**Path Parameters:**
- `customerId` (required) - Customer ID (>= 1)

**Query Parameters:**
- `from` (optional) - Start date (ISO-8601: yyyy-MM-dd)
- `to` (optional) - End date (ISO-8601: yyyy-MM-dd)

**Request Example:**
```http
GET /api/rewards/1?from=2024-01-01&to=2024-03-31
```

**Response Example:**
```json
{
  "customerId": 1,
  "customerName": "Rajesh Kumar",
  "monthlyRewards": [
    {
      "year": 2024,
      "month": "JANUARY",
      "points": 90
    },
    {
      "year": 2024,
      "month": "FEBRUARY",
      "points": 275
    },
    {
      "year": 2024,
      "month": "MARCH",
      "points": 90
    }
  ],
  "totalPoints": 455
}
```

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-03-04T20:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "'from' date (2024-03-01) must not be after 'to' date (2024-01-01)"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-03-04T20:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999"
}
```

## Database Schema

### Customer Table
```sql
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

### Transaction Table
```sql
CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);
```

## Running Tests

```bash
mvn test
```

