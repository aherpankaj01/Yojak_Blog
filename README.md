# 🚀 Yojak — Spring Boot Backend

Backend REST API for **Yojak**, a full-stack blogging platform built using **Java, Spring Boot, Spring Security, PostgreSQL, JPA/Hibernate, JWT, Flyway, and Cloudinary**.

The backend provides secure APIs for authentication, users, blog posts, comments, likes, profiles, and image management.

## 🌐 Application

🚀 **Live Frontend:**
https://yojak-front-end.vercel.app/

🔗 **Frontend Repository:**
Add your frontend GitHub repository URL here.

## ✨ Features

### 🔐 Authentication & Security

* User registration
* User login
* JWT-based authentication
* Spring Security integration
* Protected REST endpoints
* Ownership-based authorization
* JWT request filtering
* Password security

### 📝 Blog Management

* Create posts
* Read posts
* Update posts
* Delete posts
* Author-based access control
* Post filtering
* Most recent posts
* Most liked posts
* Most commented posts

### 💬 Comments

* Add comments
* Retrieve comments
* Update comments
* Delete comments
* Comment ownership validation

### ❤️ Likes

* Like posts
* Unlike posts
* Retrieve like information
* User-specific like handling

### 👤 User Profiles

* Retrieve user profiles
* Update profile information
* Profile image support

### 🖼️ File Management

* Image upload
* Cloudinary integration
* Profile image management
* Post image management

### 🗄️ Database

* PostgreSQL
* JPA / Hibernate
* Relational entity mapping
* Foreign key relationships
* Database validation
* Flyway migrations

### ⚠️ Error Handling

* Global exception handling
* Structured API responses
* Validation handling
* Application-level error management

## 🛠️ Tech Stack

| Category              | Technology             |
| --------------------- | ---------------------- |
| Language              | Java 21                |
| Framework             | Spring Boot            |
| Web                   | Spring MVC / REST APIs |
| Security              | Spring Security        |
| Authentication        | JWT                    |
| ORM                   | JPA / Hibernate        |
| Database              | PostgreSQL             |
| Migration             | Flyway                 |
| File Storage          | Cloudinary             |
| Build Tool            | Maven                  |
| Validation            | Spring Validation      |
| Boilerplate Reduction | Lombok                 |
| Testing               | Spring Boot Test       |
| API Testing           | Postman                |
| Version Control       | Git / GitHub           |
| Deployment            | Render                 |

## 🏗️ Backend Architecture

```text
                    Client
                      │
                      ▼
                REST Controller
                      │
                      ▼
                   Service
                      │
                      ▼
                 Repository
                      │
                      ▼
              JPA / Hibernate
                      │
                      ▼
                 PostgreSQL
```

## 🔐 Authentication Architecture

```text
Client
  │
  ▼
Login Request
  │
  ▼
AuthController
  │
  ▼
AuthService
  │
  ▼
Spring Security
  │
  ▼
JWT Generation
  │
  ▼
Client Receives JWT
  │
  ▼
Protected API Request
  │
  ▼
JwtFilter
  │
  ▼
JWT Validation
  │
  ▼
Controller
  │
  ▼
Service
  │
  ▼
Repository
```

## 📂 Project Structure

```text
src/
│
├── main/
│   ├── java/com/yojak/backend/
│   │
│   ├── config/
│   │   ├── CloudinaryConfig.java
│   │   ├── CorsConfig.java
│   │   ├── FileStorageConfig.java
│   │   └── SecurityConfig.java
│   │
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── CommentController.java
│   │   ├── FileController.java
│   │   ├── LikeController.java
│   │   ├── PostController.java
│   │   └── UserController.java
│   │
│   ├── dto/
│   │   ├── AuthResponse.java
│   │   ├── CommentRequest.java
│   │   ├── CommentResponse.java
│   │   ├── LikeResponse.java
│   │   ├── LoginRequest.java
│   │   ├── PostRequest.java
│   │   ├── PostResponse.java
│   │   ├── SignupRequest.java
│   │   ├── UpdateProfileRequest.java
│   │   └── UserProfileResponse.java
│   │
│   ├── entity/
│   │   ├── Comment.java
│   │   ├── Like.java
│   │   ├── Post.java
│   │   └── User.java
│   │
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── repository/
│   │   ├── CommentRepository.java
│   │   ├── LikeRepository.java
│   │   ├── PostRepository.java
│   │   └── UserRepository.java
│   │
│   ├── security/
│   │   ├── JwtFilter.java
│   │   ├── JwtUtil.java
│   │   └── UserDetailsServiceImpl.java
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── CommentService.java
│   │   ├── FileStorageService.java
│   │   ├── LikeService.java
│   │   ├── PostService.java
│   │   └── UserService.java
│   │
│   └── BackendApplication.java
│
├── resources/
│   ├── db/
│   │   └── migration/
│   │       ├── V1__init_schema.sql
│   │       ├── V2__add_comments.sql
│   │       ├── V3__add_likes.sql
│   │       └── V4__add_user_profile_fields.sql
│   │
│   └── application.properties
│
└── test/
    └── java/
```

## 🗄️ Database Design

The application uses PostgreSQL with the following primary entities:

```text
User
 │
 ├──────────► Post
 │              │
 │              ├────────► Comment
 │              │
 │              └────────► Like
 │
 └──────────► Like
```

### Main Entities

* `User`
* `Post`
* `Comment`
* `Like`

Relationships are managed using JPA entity mappings and relational database constraints.

## 🔄 Database Migration

Flyway is used to version and manage database schema changes.

```text
V1__init_schema.sql
        ↓
V2__add_comments.sql
        ↓
V3__add_likes.sql
        ↓
V4__add_user_profile_fields.sql
```

This allows database changes to be tracked and applied in a controlled manner.

## 🔑 Environment Variables

The application uses environment variables for sensitive configuration.

```env
SPRING_DATASOURCE_URL=your_postgresql_url
SPRING_DATASOURCE_USERNAME=your_database_username
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

SERVER_PORT=8082
```

Never commit real database credentials, JWT secrets, or Cloudinary credentials to GitHub.

## 🚀 Getting Started

### Prerequisites

Make sure you have:

* Java 21
* Maven
* PostgreSQL
* Git

### 1. Clone the Repository

```bash
git clone https://github.com/aherpankaj01/Yojak_Backend.git
```

```bash
cd Yojak_Backend
```

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE yojak_db;
```

Configure the database credentials using environment variables.

### 3. Configure Environment Variables

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/yojak_db
SPRING_DATASOURCE_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_secret

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

SERVER_PORT=8082
```

### 4. Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Or run the application directly from IntelliJ IDEA.

The backend runs on:

```text
http://localhost:8082
```

## 🧪 Testing

The project includes Spring Boot test configuration.

API endpoints can be tested using **Postman**.

Typical API flow:

```text
Authentication
     ↓
Get JWT Token
     ↓
Send JWT with Authorization Header
     ↓
Access Protected API
```

Authorization header:

```text
Authorization: Bearer <JWT_TOKEN>
```

## 🚀 Deployment

The backend is designed to run as a Spring Boot application on platforms such as Render.

```text
GitHub
   ↓
Render
   ↓
Spring Boot Application
   ↓
Neon PostgreSQL
   +
Cloudinary
```

## 🔗 Frontend Integration

The React frontend communicates with this backend through REST APIs.

```text
React.js
   │
   │ Axios
   ▼
Spring Boot REST API
   │
   ├── Authentication
   ├── Users
   ├── Posts
   ├── Comments
   ├── Likes
   └── Files
```

## 🎯 Key Highlights

* Layered Spring Boot architecture
* RESTful API design
* JWT authentication
* Spring Security
* Ownership-based authorization
* JPA / Hibernate
* PostgreSQL relational database
* Flyway database migrations
* Cloudinary image storage
* DTO-based API communication
* Global exception handling
* CORS configuration
* Environment-based configuration
* Maven dependency management

## 👨‍💻 Author

**Pankaj Aher**

Computer Engineering Graduate
Java Full-Stack Developer

GitHub: https://github.com/aherpankaj01

Portfolio: https://aher-pankaj.vercel.app/
