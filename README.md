---
# 💰 Finance Dashboard – Backend API (Spring Boot)
A secure and structured **Finance Management Backend API** built using **Spring Boot**.
This service provides role-based access control for managing **users, financial records, and dashboard summaries**, making it suitable for finance tracking systems and dashboards.
---

## 🚀 Features

- **User Management** (Create, update, activate/deactivate users)
- **Role Based Access Control** (ADMIN, ANALYST, VIEWER)
- **JWT Authentication** (Secure login with token)
- **Financial Records Management** (Add, update, soft delete, filter)
- **Dashboard Summary API** (Income, expenses, net balance)
- **Category Wise Totals**
- **SQLite Database Integration** (No setup needed)
- **Layered Architecture** (Controller → Service → Repository)
- **Exception Handling & Validation**
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
| API Format        | REST + JSON           |

---

## 📁 Project Structure

finance-dashboard/
├── src/
│ ├── main/
│ │ ├── java/com/finance/dashboard/
│ │ │ ├── controller/
│ │ │ │ ├── AuthController.java
│ │ │ │ ├── UserController.java
│ │ │ │ ├── FinancialRecordController.java
│ │ │ │ └── DashboardController.java
│ │ │ ├── service/
│ │ │ │ ├── AuthService.java
│ │ │ │ ├── UserService.java
│ │ │ │ └── FinancialRecordService.java
│ │ │ ├── repository/
│ │ │ │ ├── UserRepository.java
│ │ │ │ └── FinancialRecordRepository.java
│ │ │ ├── model/
│ │ │ │ ├── User.java
│ │ │ │ ├── Role.java
│ │ │ │ ├── FinancialRecord.java
│ │ │ │ └── RecordType.java
│ │ │ ├── dto/
│ │ │ │ ├── AuthDTO.java
│ │ │ │ ├── UserDTO.java
│ │ │ │ └── FinancialRecordDTO.java
│ │ │ └── security/
│ │ │ ├── JwtUtil.java
│ │ │ ├── JwtFilter.java
│ │ │ └── SecurityConfig.java
│ │ └── resources/
│ │ └── application.properties
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

Default URL:http://localhost:8080/api
✅ Database file **finance.db** is created automatically on first run!

---

## 🔑 How Authentication Works

1. Create your first admin using `/api/users/setup`
2. Login using `/api/auth/login`
3. Copy the token from the response
4. Add it to every request as:Authorization: Bearer <your-token>

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

| Method | Endpoint                                               | Description          | Auth           |
| ------ | ------------------------------------------------------ | -------------------- | -------------- |
| POST   | `/api/records`                                         | Create a record      | ADMIN, ANALYST |
| GET    | `/api/records`                                         | Get all records      | ALL ROLES      |
| GET    | `/api/records?type=INCOME`                             | Filter by type       | ALL ROLES      |
| GET    | `/api/records?category=Salary`                         | Filter by category   | ALL ROLES      |
| GET    | `/api/records?startDate=2026-01-01&endDate=2026-04-03` | Filter by date range | ALL ROLES      |
| PUT    | `/api/records/{id}`                                    | Update a record      | ADMIN, ANALYST |
| DELETE | `/api/records/{id}`                                    | Soft delete a record | ADMIN          |

### **📊 Dashboard**

| Method | Endpoint                         | Description                        | Auth           |
| ------ | -------------------------------- | ---------------------------------- | -------------- |
| GET    | `/api/dashboard/summary`         | Total income, expenses and balance | ADMIN, ANALYST |
| GET    | `/api/dashboard/category-totals` | Totals grouped by category         | ADMIN, ANALYST |

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

---

## 📈 Future Enhancements

- Swagger API Documentation
- Pagination for large record sets
- Budget Planning Module
- Recurring Transactions
- Export to Excel or PDF
- Monthly and yearly analytics
- AI based spending insights

---

## 🙌 Author

**Anju Bhargavi**  
Backend Developer
