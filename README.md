---
# 💰 Finance Dashboard – Backend API (Spring Boot)
A secure and structured **Finance Management Backend API** built using **Spring Boot**. This service provides role-based access control for managing **users, financial records, and dashboard summaries**, making it suitable for finance tracking systems and dashboards.
---

## 🚀 Features

- **User Management** (Create, update, activate/deactivate users)
- **Role Based Access Control** (ADMIN, ANALYST, VIEWER)
- **JWT Authentication** (Secure login with token)
- **Financial Records Management** (Add, update, soft delete, filter)
- **Dashboard Summary API** (Income, expenses, net balance)
- **Category Wise Totals**
- **Pagination & Sorting** (Page through records with ?page=0&size=10)
- **Search Support** (Keyword search across category and notes)
- **Rate Limiting** (60 requests/min per IP using Bucket4j)
- **Swagger API Documentation** (Interactive UI at /swagger-ui/index.html)
- **SQLite Database Integration** (No setup needed)
- **Layered Architecture** (Controller → Service → Repository)
- **Exception Handling & Validation** (Field-level error messages)
- **Unit & Integration Tests** (17 tests passing)
- **CI/CD Pipeline** (GitHub Actions runs tests on every push)
- **RESTful API Design**

---

## 🏗️ Tech Stack

| Layer             | Technology            |
| ----------------- | --------------------- |
| Backend Framework | Spring Boot 3.3.5     |
| Build Tool        | Maven                 |
| Database          | SQLite                |
| ORM               | Spring Data JPA       |
| Language          | Java 21               |
| Security          | Spring Security + JWT |
| Rate Limiting     | Bucket4j              |
| API Docs          | Springdoc OpenAPI     |
| API Format        | REST + JSON           |

---

## 📁 Project Structure

finance-dashboard/
├── src/
│   ├── main/
│   │   ├── java/com/finance/dashboard/
│   │   │   ├── config/
│   │   │   │   └── SwaggerConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── FinancialRecordController.java
│   │   │   │   └── DashboardController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   └── FinancialRecordService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── FinancialRecordRepository.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── FinancialRecord.java
│   │   │   │   └── RecordType.java
│   │   │   ├── dto/
│   │   │   │   ├── AuthDTO.java
│   │   │   │   ├── UserDTO.java
│   │   │   │   └── FinancialRecordDTO.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── security/
│   │   │       ├── JwtUtil.java
│   │   │       ├── JwtFilter.java
│   │   │       ├── RateLimitFilter.java
│   │   │       └── SecurityConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/finance/dashboard/
│           ├── DashboardApplicationTests.java
│           ├── controller/
│           │   └── FinancialRecordControllerTest.java
│           └── service/
│               └── FinancialRecordServiceTest.java
├── finance.db
├── pom.xml
├── README.md
└── mvnw / mvnw.cmd

---

## 👥 User Roles & Permissions

| Action                   | VIEWER | ANALYST | ADMIN |
| ------------------------ | ------ | ------- | ----- |
| View financial records   | ✅     | ✅      | ✅    |
| Create financial records | ❌     | ✅      | ✅    |
| Update financial records | ❌     | ✅      | ✅    |
| Delete financial records | ❌     | ❌      | ✅    |
| View dashboard summary   | ❌     | ✅      | ✅    |
| Manage users             | ❌     | ❌      | ✅    |

---

## ⚙️ How to Run the Project

### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/Anjubhargavii/finance-dashboard.git
cd finance-dashboard
```

### **2️⃣ Build the Project**
```bash
mvn clean install -DskipTests
```

### **3️⃣ Run the Application**
```bash
mvn spring-boot:run
```

### **4️⃣ Access the API**

Default URL: `http://localhost:8080/api`

✅ Database file **finance.db** is created automatically on first run!

---

## 📖 API Documentation (Swagger UI)

Once the application is running, open your browser and go to:  http://localhost:8080/swagger-ui/index.html

You can:
- Browse all available endpoints grouped by controller
- Test endpoints directly from the browser
- Authorize with your JWT token using the **Authorize** button

### How to use Swagger with JWT:
1. Run the app and open Swagger UI
2. Call `POST /api/auth/login` to get your token
3. Click the **Authorize** button (top right)
4. Enter: `Bearer <your-token>`
5. All protected endpoints are now testable directly from Swagger

---

## 🔑 How Authentication Works

1. Create your first admin using `/api/users/setup`
2. Login using `/api/auth/login`
3. Copy the token from the response
4. Add it to every request as: `Authorization: Bearer <your-token>`

---

## 🔗 API Endpoints

### **🔐 Auth**

| Method | Endpoint          | Description             | Auth |
| ------ | ----------------- | ----------------------- | ---- |
| POST   | `/api/auth/login` | Login and get JWT token | No   |

### **👤 Users**

| Method | Endpoint                 | Description                 | Auth  |
| ------ | ------------------------ | --------------------------- | ----- |
| POST   | `/api/users/setup`       | Create first admin user     | No    |
| POST   | `/api/users`             | Create a new user           | ADMIN |
| GET    | `/api/users`             | Get all users               | ADMIN |
| GET    | `/api/users/{id}`        | Get user by ID              | ADMIN |
| PATCH  | `/api/users/{id}/status` | Activate or deactivate user | ADMIN |
| DELETE | `/api/users/{id}`        | Delete a user               | ADMIN |

### **💸 Financial Records**

| Method | Endpoint | Description | Auth |
| ------ | -------- | ----------- | ---- |
| POST   | `/api/records` | Create a record | ADMIN, ANALYST |
| GET    | `/api/records` | Get all records (paginated) | ALL ROLES |
| GET    | `/api/records?type=INCOME` | Filter by type | ALL ROLES |
| GET    | `/api/records?category=Salary` | Filter by category | ALL ROLES |
| GET    | `/api/records?startDate=2026-01-01&endDate=2026-04-03` | Filter by date range | ALL ROLES |
| GET    | `/api/records?search=rent` | Keyword search | ALL ROLES |
| GET    | `/api/records?page=0&size=10&sort=date,desc` | Pagination & sorting | ALL ROLES |
| PUT    | `/api/records/{id}` | Update a record | ADMIN, ANALYST |
| DELETE | `/api/records/{id}` | Soft delete a record | ADMIN |

### **📊 Dashboard**

| Method | Endpoint                         | Description                        | Auth           |
| ------ | -------------------------------- | ---------------------------------- | -------------- |
| GET    | `/api/dashboard/summary`         | Total income, expenses and balance | ADMIN, ANALYST |
| GET    | `/api/dashboard/category-totals` | Totals grouped by category         | ADMIN, ANALYST |

---

## 🛡️ Rate Limiting

All endpoints are protected by rate limiting using **Bucket4j**:
- **Limit:** 60 requests per minute per IP address
- **Response when exceeded:** HTTP 429 Too Many Requests
```json
{
  "status": 429,
  "error": "Too many requests. Limit is 60 requests per minute."
}
```

---

## ✅ Validation & Error Handling

All input is validated at the DTO level. Invalid requests return structured JSON:
```json
{
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "amount": "Amount is required",
    "category": "Category is required"
  },
  "timestamp": "2026-04-06T10:00:00"
}
```

---

## 🗄️ Database Schema

### **Users Table**

| Field      | Type      | Description            |
| ---------- | --------- | ---------------------- |
| id         | Integer   | Primary key            |
| username   | String    | Unique username        |
| password   | String    | BCrypt encrypted       |
| email      | String    | Unique email           |
| role       | String    | ADMIN, ANALYST, VIEWER |
| active     | Boolean   | Account status         |
| created_at | Timestamp | Account creation time  |

### **Financial Records Table**

| Field      | Type      | Description             |
| ---------- | --------- | ----------------------- |
| id         | Integer   | Primary key             |
| amount     | Decimal   | Transaction amount      |
| type       | String    | INCOME or EXPENSE       |
| category   | String    | Category name           |
| date       | Date      | Transaction date        |
| notes      | String    | Optional notes          |
| created_by | String    | Username who created it |
| created_at | Timestamp | Record creation time    |
| is_deleted | Boolean   | Soft delete flag        |

---

## 🧪 Tests

Tests run: 17, Failures: 0, Errors: 0

| Test Class | Tests | Coverage |
|---|---|---|
| FinancialRecordServiceTest | 8 | Create, update, delete, error cases, dashboard totals |
| FinancialRecordControllerTest | 8 | Role enforcement, validation, auth checks |
| DashboardApplicationTests | 1 | Application context loads |

Run tests with:
```bash
mvn test
```

---

## 🧪 Sample Requests

### **Create Admin User**
```json
POST /api/users/setup
{
  "username": "admin",
  "password": "admin123",
  "email": "admin@finance.com",
  "role": "ADMIN"
}
```

### **Login**
```json
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

### **Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

### **Create Financial Record**
```json
POST /api/records
Authorization: Bearer <token>
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-03",
  "notes": "Monthly salary"
}
```

### **Dashboard Summary Response**
```json
{
  "totalIncome": 5000.0,
  "totalExpenses": 1500.0,
  "netBalance": 3500.0
}
```

---

## 📌 Assumptions Made

- SQLite is used for simplicity — no separate database installation needed
- The `/api/users/setup` endpoint is open to allow creating the first admin user
- Soft delete is used for financial records so data is never permanently lost
- JWT tokens expire after 24 hours
- All financial amounts are stored as decimals for accuracy
- Rate limiting is per IP address, resets every minute

---

## 📈 Future Enhancements

- Budget Planning Module
- Recurring Transactions
- Export to Excel or PDF
- Monthly and yearly analytics
- AI based spending insights

---

## 🙌 Author

**Anju Bhargavi**  
Backend Developer
