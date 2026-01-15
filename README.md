# E-commerce Food Store Backend


## 🎓 Project Context

### Background

This project was originally developed as a team project (3 members) for a university software engineering course. The codebase has been individually refactored to demonstrate:


- **Clean code principles** - SOLID principles, proper naming, and organization
- **Security best practices** - JWT authentication, input validation, error handling
### Team Contributions

**Original Team Project (2025):**
- Frontend development (React) - Teammate
- Backend development - Individual work
- Testing development - Teammate
- Database design - Individual work
- Kubernetes deployment - Individual work

**Individual Refactoring (2026):**
- Complete backend restructuring
- Security enhancements
- Exception handling
- Testing implementation (in progress)
- Documentation

---

## 📊 Project Status

### ✅ Completed

- **Clean Architecture** - Proper package structure with clear separation of concerns
- **Profile-based Configuration** - Separate dev/prod environments with externalized config
- **Exception Handling** - Custom exception hierarchy with global error handling
- **RESTful API Design** - Properly structured endpoints following REST principles
- **JWT Authentication** - Secure token-based authentication system
- **Database Design** - Normalized schema with proper relationships


### ⏳ In Progress

- **Unit Testing** - Mockito-based unit tests (Target: 80% coverage)
- **Integration Testing** - API endpoint testing with TestContainers
- **CI/CD Pipeline** - GitHub Actions workflow
- **Docker Support** - Containerized application with Docker Compose
- **API Documentation** - Comprehensive endpoint documentation

### ⚠️ Important Note (How to Run)

At the moment, the project does not support running via Docker / Docker Compose / Kubernetes.
To run the project, you must use PostgreSQL installed locally on your machine (local database) only.

The project includes an init.sql file for creating the tables and inserting initial seed data.
---


## 🏗️ Architecture

### Tech Stack

**Backend Framework:**
- Java
- Spring Boot
- Spring Data JPA

**Database:**
- PostgreSQL

**Security:**
- JWT

**Build & Deployment:**
- Maven 
- Docker & Docker Compose
- Kubernetes (deployment configs)

### Project Structure

```
src/main/java/com/example/pizza_backend
├── api/                    
│   ├── controller/
│   └── dto/
|       └──request/
|       └──response/
│   
├── auth/                    
│   ├── Interceptor/         # seperate admin & customer authen
│   └── JWTService.java
|
├── config/                    
│   ├── CorsConfig.java      #CORS
│   ├── MVCConfig.java       #Image directory config
│   └── WebConfig.java       #allowed path for admin/customer
|
├── exception/                   
│   └── GlobalExceptionHandler.java
|
├── mapper/                 # mapping dto -> entity/ entity -> dto
|
├── persistence/
│   ├── entity/
│   └── repository/
|
└── service/

```
---

### Testing the API

Once running, the API will be available at `http://localhost:8080/home/`


### Example Request/Response

**POST /profile/login**

Request:
```json
{
    "username":"bob",
    "password":"bob123"
}
```

Response (200 OK):
```json
{
    "message": "success"
}
```

**Error Response (400 Bad Request):**
```json
{
    "message": "incorrect username or password"
}
```

---

## 🗄️ Database Schema

### Core Tables

**users**
- User authentication and profile information
- Relationships: orders (1:N), cart (1:1), addresses (1:1)

**products**
- Product catalog with pricing and inventory
- Relationships: categories (N:1), cart_items (1:N)

**categories**
- Product categorization
- Relationships: products (1:M)

**carts**
- User shopping carts
- Relationships: users (1:1), cart_items (1:N)

**cart_items**
- Items in user's cart
- Relationships: carts (N:1), products (N:1)

**orders**
- Customer orders
- Relationships: users (N:1), order_items (1:N), addresses (N:1)

**order_items**
- Line items in orders
- Relationships: orders (N:1)

**addresses**
- User shipping/billing addresses
- Relationships: users (1:1), orders (1:N)

---

## 🧪 Testing


### Test Structure

```
src/test/java/com/example/pizza_backend
└── service/
```
---

## 📄 License

This project is for educational and portfolio purposes. Original team project code has been substantially refactored for demonstration of software engineering skills.

---

## 📧 Contact

*If you want the original complete project, you can contact me via below*

**Pattaraporn Tonsomboon**
- Email: ceekay662005@gmail.com
- GitHub: [@lookchub](https://github.com/lookchub)

---

## 🙏 Acknowledgments

- Original team project completed as part of Software Engineering course at Mahidol University
- Refactored individually to demonstrate production-ready development skills
- Inspired by enterprise-level Spring Boot applications and clean architecture principles

---
