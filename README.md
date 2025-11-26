# 🎨 Pixel Battle — Real-Time Multiplayer Pixel Art Platform

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/97d8d6e1eaa04778b03bc514a9a2a68e)](https://app.codacy.com/gh/ZebaroWhite-Alliance/Pixel-Battle-BE?utm_source=github.com&utm_medium=referral&utm_content=ZebaroWhite-Alliance/Pixel-Battle-BE&utm_campaign=Badge_Grade)

![Build](https://img.shields.io/github/actions/workflow/status/ZebaroWhite-Alliance/Pixel-Battle-BE/deploy.yaml)
![Coverage](https://img.shields.io/codecov/c/gh/ZebaroWhite-Alliance/Pixel-Battle-BE)
![Docker Pulls](https://img.shields.io/docker/pulls/zebarowhite/pixel-battle-be)
![License](https://img.shields.io/github/license/ZebaroWhite-Alliance/Pixel-Battle-BE)

A full-stack real-time collaborative pixel art platform 
where thousands of users can draw together on a shared canvas.  
This repository contains the **backend server**, 
built with **Java + Spring Boot**, using **WebSockets**,
**JWT authentication**, **rate limiting**, **template management**,
**pixel history**, **Redis** and **PostgreSQL persistence**.

Frontend (React) repository: 👉 *[here](https://github.com/ZebaroWhite-Alliance/Pixel-Battle-FE)*

---

📚 Table of Contents
🚀 Quick Start

🎯 Live Demo

✨ Features

🏗️ Architecture

🛠️ Tech Stack

📁 Project Structure

🔌 API Reference

🌐 WebSocket Events

🧪 Testing & Quality

🗄️ Database & Storage

🛠️ Development Guide

🤝 Contributing

❓ FAQ

📄 License

---

## 🚀 Live Demo

| Component | URL                                          |
|----------|----------------------------------------------|
| **Frontend App** | `http://localhost:3000` (local)              |
| **Backend API** | `http://localhost:8080` (local)              |
| **API Documentation** | `http://localhost:8080/swagger-ui/index.html` |
| **Health Dashboard** | `http://localhost:8080/actuator/health`      |
---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local development)
- Gradle 7.6+

### 1️⃣Clone & Setup:

```bash
git clone https://github.com/ZebaroWhite-Alliance/Pixel-Battle-BE.git
cd Pixel-Battle-BE
cp .env.example .env
```

### 2️⃣ Configure environment variables
Below is a table describing required environment variables.

| Variable                   | Description                                | Example                                               | Required |
| -------------------------- | ------------------------------------------ | ----------------------------------------------------- | -------- |
| **POSTGRES_HOST**          | PostgreSQL host                            | `pixel-battle-db`                                     | ✔️       |
| **POSTGRES_PORT**          | PostgreSQL port                            | `5432`                                                | ✔️       |
| **POSTGRES_DB**            | Database name                              | `pixel-battle`                                        | ✔️       |
| **POSTGRES_USER**          | DB username                                | `postgres`                                            | ✔️       |
| **POSTGRES_PASSWORD**      | DB password                                | `postgres`                                            | ✔️       |
| **SPRING_DATASOURCE_URL**  | Full JDBC URL (overrides host/port if set) | `jdbc:postgresql://pixel-battle-db:5432/pixel-battle` | ✔️       |
| **REDIS_HOST**             | Redis host                                 | `pixel-battle-redis`                                  | ✔️       |
| **REDIS_PORT**             | Redis port                                 | `6379`                                                | ✔️       |
| **JWT_SECRET**             | Secret used to sign JWT tokens             | `super-secret`                                        | ✔️       |
| **SPRING_PROFILES_ACTIVE** | Active Spring profile (`dev` / `prod`)     | `dev`                                                 | ✔️       |

Spring Boot will automatically read these variables if
you use spring-boot-dotenv or configure them in application-dev.yaml
/ application-prod.yaml.

### 3️⃣ Run with Docker (recommended)
```bash
# Start backend along with PostgreSQL and Redis
docker-compose up --build
```

This will start:
- 🐘 PostgreSQL (port 5432) - Primary database
- 🧠 Redis (port 6379) - Real-time cache & sessions
- 🚀 Backend API (port 8080) - Spring Boot application

### 4️⃣ Verify Installation

```bash
# Check if services are running
docker-compose ps

# Test API health
curl http://localhost:8080/actuator/health
```


### 5️⃣ Quick API Test

**Register a new user**

```
curl -X POST "http://localhost:8080/api/v1/auth/register" \
-H "Content-Type: application/json" \
-d '{"username":"username", "password":"Pass123*"}'
```

**login and get token**
```
curl -X POST "http://localhost:8080/api/v1/auth/login" \
-H "Content-Type: application/json" \
-d '{"username":"username","password":"Pass123*"}'
```
**place pixel (use returned Bearer token)**

```
curl -X POST "http://localhost:8080/api/v1/pixels" \
-H "Authorization: Bearer <ACCESS_TOKEN>" \
-H "Content-Type: application/json" \
-d '{"x":12,"y":44,"color":"#FF00AA"}'
```

---

## ✨ Key Features

### 🔥 Real-Time Collaboration
- Instant pixel updates across all connected clients
- Broadcast and message routing system
- Automatic synchronization of canvas state

### 🛡️ Authentication & Security
- JWT-based user authentication with **Access** and **Refresh tokens**
- Token expiration & refresh logic
- Secure endpoints and WebSocket handshake validation

### 🖼️ Canvas Logic
- Pixel placement with cooldowns (rate limiting)
- Coordinate & color validation
- All canvas state stored in Redis for fast retrieval
- Full pixel change history with timestamps

### 📁 Templates System
- Users can save private templates to simplify drawing
- Templates are visible only to the creator
- Apply template to the board via REST endpoint

### 🧾 History Tracking
- Accurate, timestamped change log
- Efficient pagination for large datasets

### 📦 Production-Ready
- Docker support (backend + database)
- Proper exception handling (custom error API)
- Logging, validation, and environment profiles
- CI-friendly build structure

---


## 🛠️ Tech Stack


### Backend & Framework
| Layer      | Technology          | Purpose                  |
| ---------- | ------------------- | ------------------------ |
| Language   | Java 21             | High-performance runtime |
| Framework  | Spring Boot 3       | Application framework    |
| Build Tool | Gradle 7.6+         | Dependency management    |
| API Docs   | SpringDoc OpenAPI 3 | API documentation        |


### Data & Persistence
| Storage    | Technology                  | Usage                     |
| ---------- | --------------------------- | ------------------------- |
| Database   | PostgreSQL 15               | Primary data store        |
| Cache      | Redis 7                     | Real-time data & sessions |
| ORM        | Spring Data JPA + Hibernate | Database operations       |
| Migrations | Flyway                      | Database versioning       |


### Infrastructure
| Component        | Technology                       | Role                      |
| ---------------- | -------------------------------- | ------------------------- |
| Containerization | Docker + Docker Compose          | Deployment                |
| Testing          | JUnit 5, Mockito, Testcontainers | Quality assurance         |
| Code Quality     | Spotless, JaCoCo                 | Code standards & coverage |
| CI/CD            | GitHub Actions                   | Automated pipelines       |



---

## 📂 Project Structure

```text
pixel-battle-be/
├── src/main/java/ua/cn/stu/pixelbattle/
│   ├── config/       # Security, WebSocket, CORS, Swagger, app configuration
│   ├── controller/   # REST controllers
│   ├── dto/          # Request / response DTOs
│   ├── exception/    # Global exception handling
│   ├── model/        # Entity classes
│   ├── repository/   # JPA repositories
│   ├── security/     # JWT filters, authentication, authorization
│   └── service/      # Business logic layer
├── src/main/resources/
│   ├── db/migration/ # Flyway migrations
│   ├── application.yaml        # default config
│   ├── application-dev.yaml    # development config
│   └── application-prod.yaml   # production config
├── .env.example             # Environment template
├── docker-compose.yml       # Local development stack
└── build.gradle             # Build configuration
```
---

## 🔌 API Overview

### Authentication

| Method | Endpoint                | Description                           |
| ------ |-------------------------| ------------------------------------- |
| POST   | `/api/v1/auth/register` | Register a new user                   |
| POST   | `/api/v1/auth/login`       | Login and get Access & Refresh tokens |
| POST   | `/api/v1/auth/refresh`     | Refresh Access token                  |

### Canvas and pixels

| Method | Endpoint          | Description                        |
|--------|-------------------| ---------------------------------- |
| GET    | `/api/v1/pixels`  | Retrieve current canvas state      |
| POST   | `/api/v1/pixels`  | Place a new pixel on the board     |
| GET    | `/api/v1/history` | Retrieve pixel history after given ID (incremental sync). Default limit = 10000|

### Templates

| Method | Endpoint            | Description                   |
| ------ |---------------------| ----------------------------- |
| GET    | `/api/v1/templates` | Get user's saved templates    |
| POST   | `/api/v1/templates` | Save a new template (private) |

---

## 🌐 WebSocket (Real-Time Updates)

Pixel Battle uses **one-way STOMP WebSocket** for real-time pixel updates.  
All pixel placements are done via **REST API**, then broadcasted to connected clients via WebSocket.

### Connection Details

- Endpoint: `ws://localhost:8080/ws`
- Protocol: STOMP over WebSocket
- SockJS Fallback: Enabled

### **Subscriptions**
| Destination       | Description                          |
|------------------|--------------------------------------|
| `/topic/pixels`  | Broadcast of pixel changes in real-time |

### **Event Format (Server → Client)**

```json
{
  "x": 12,
  "y": 44,
  "color": "#FF00AA"
}
```

Every time a pixel is changed via REST API (POST /api/v1/pixels), the new pixel data is sent to all clients subscribed to /topic/pixels.

Clients do not send WebSocket messages.

---

## 🧪 Testing & Quality
Run all tests:
```bash
./gradlew test
```

#### ✅ Controller Tests

Tests covering REST endpoints, validation, and security:

- AuthControllerTest
- GameInfoControllerTest
- PixelControllerTest
- PixelHistoryControllerTest
- SesionControllerTest
- TemplateControllerTest
- UserControllerTest

#### ✅ Service Tests

Unit tests for business logic and internal functionality:

- AuthServiceTest
- JwtTokenServiceTest
- PixelHistoryServiceTest
- PixelServiceTest
- RefreshTokenServiceTest
- SessionServiceTest
- TemplateServiceTest
- UserServiceTest

### 📊 Coverage Reports
Generated automatically via JaCoCo:
```
build/reports/tests/test
build/reports/jacoco/test/html
```

## 🗄️ Database & Storage
Pixel Battle combines Redis for real-time operations 
and PostgreSQL for persistent storage.

### 🔹 Redis — Real-Time Data
- Pixel board: stores current pixels with coordinates,
color, and username
- Rate limiting: tracks user cooldowns for pixel placement
- Refresh tokens: manages JWT refresh tokens with expiration

### 🔹 PostgreSQL — Persistent Storage

- Users: account info, roles, and statistics
- Pixel history: logs all pixel changes with timestamps
- Templates: user-created drawing templates
Redis ensures instant updates for fast gameplay, while 
PostgreSQL provides reliable persistence for user data 
and history.

### 🔹 Database Migrations
- Uses **Flyway** for database versioning
- Migrations automatically run on application startup
- Location: `src/main/resources/db/migration/`

## ⚡ Example API Responses

**Successful pixel placement**
```
{
"x": 12,
"y": 44,
"color": "#FF00AA",
"username": "joe",
"timestamp": "2025-11-20T19:00:00Z"
}
```

**Error response**

```
{
"timestamp": "2025-11-20T19:00:00Z",
"status": 400,
"error": "Bad Request",
"message": "Color is invalid",
"path": "/api/v1/pixels"
}
```



## 🔒 Security

- JWT & Credentials: Keep JWT_SECRET and all sensitive credentials secure; never commit them to the repository.
- Password Hashing: All user passwords are securely hashed using bcrypt.
- Web Security: CSRF protection and CORS are properly configured for safe API and WebSocket access.
- Tokens: Access and Refresh JWT tokens are validated for each request and during WebSocket handshake.

