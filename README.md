# 🎨 Pixel Battle — Real-Time Multiplayer Pixel Art Platform

![Build](https://img.shields.io/github/actions/workflow/status/ZebaroWhite-Alliance/Pixel-Battle-BE/deploy.yaml)
![Coverage](https://img.shields.io/codecov/c/gh/ZebaroWhite-Alliance/Pixel-Battle-BE)
![Docker Pulls](https://img.shields.io/docker/pulls/whitedh/pixel-battle-be)
![License](https://img.shields.io/github/license/ZebaroWhite-Alliance/Pixel-Battle-BE)
![Code Quality](https://img.shields.io/codacy/grade/6d75af562d414b3e90eed24018e16bf4)
![Docker](https://img.shields.io/docker/v/whitedh/pixel-battle-be?label=docker%20image)

![Java](https://img.shields.io/badge/java-21-blue)
![Spring](https://img.shields.io/badge/spring-boot_3.4.4-green)
![Gradle](https://img.shields.io/badge/gradle-8.13-blueviolet)



A full-stack real-time collaborative pixel art platform 
where thousands of users can draw together on a shared canvas.  
This repository contains the **backend server**, 
built with **Java + Spring Boot**, using **WebSockets**,
**JWT authentication**, **rate limiting**, **template management**,
**pixel history**, **Redis** and **PostgreSQL persistence**.

Frontend (React) repository: 👉 *[here](https://github.com/ZebaroWhite-Alliance/Pixel-Battle-FE)*


---
![2025-11-27-16-18-48-_online-video-cutter com_-_1_](https://github.com/user-attachments/assets/bbdf7e5d-8b73-4db2-ae06-e3f1d8e9e1d5)


---

## 📚 Table of Contents
- [🎯 Live Demo](#-live-demo)
- [🚀 Quick Start](#-quick-start)
- [✨ Key Features](#-key-features)
- [🛠️ Tech Stack](#-tech-stack)
- [📂 Project Structure](#-project-structure)
- [🔌 API Overview](#-api-overview)
- [🌐 WebSocket (Real-Time Updates)](#-websocket-real-time-updates)
- [🧪 Testing & Quality](#-testing--quality)
- [🗄️ Database & Storage](#-database--storage)
- [⚡ Example API Responses](#-example-api-responses)
- [🔒 Security](#-security)
- [🤝 Contributing](#-contributing)
- [📝 License](#-license)

---

## 🎯 Live Demo
> ⚠️ Note: The demo depends on my personal electricity supply.  
> to save my portable power station, the server may be temporarily turned off.


| Component | URL                                          |
|----------|----------------------------------------------|
| **Frontend App** | `https://pixel-battle.zebaro.dev/`              |
| **Backend API** | `https://pixel-battle.zebaro.dev/`              |
| **API Documentation** | `https://pixel-battle.zebaro.dev/swagger-ui/index.html` |
| **Health Dashboard** | `https://pixel-battle.zebaro.dev/actuator/health`      |


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


> For full API documentation, including all endpoints, 
> request & response examples, visit 
> [Swagger](https://pixel-battle.zebaro.dev/swagger-ui/index.html)

### 🧰 Postman Collection

For convenient API testing, a **Postman collection** is available containing all endpoints and request examples.

**How to use:**

1. Open Postman.
2. Go to `File → Import` and select the collection file:
   `docs/postman/pixel-battle-api.postman_collection.json`

3. Choose the environment (e.g., `dev`) and set variables:
- `base_url` — your API URL (e.g., `http://localhost:8080/api/v1`)
- `access_token` — will be populated after calling `/auth/login`
4. You can now test all endpoints without manually entering URLs and headers.

> ⚡ Tip: First execute `POST /auth/login` to obtain your `access_token`.

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

### ✅ Code Style

- Project follows Google Java Style
- Checkstyle is integrated with Gradle, run it via:
```bash
./gradlew checkstyleMain
./gradlew checkstyleTest
```

- IDE (IntelliJ/VSCode) can auto-format code according to Google Style
- Ensures consistent code style across contributors
  Unit tests for business logic and internal functionality:

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


## 🤝 Contributing

Contributions, issues and feature requests are welcome!
If you want to contribute to Pixel Battle, follow these **steps**:

#### 1. Fork the repository

#### 2. Create a new branch:

```bash 
git checkout -b feature/my-new-feature
```

#### 3. Make your changes and commit them:

```bash
git commit -am "feat: Add new feature"
```

####  4. Push to your branch:

```bash
git push origin feature/my-new-feature
```

#### 5. Open a Pull Request

Before submitting, please ensure:

- Your code follows project style rules (Spotless)
- All tests pass (./gradlew test)
- You added or updated tests if needed
- Thank you for helping improve the project! 🚀

## 📝 License

This project is licensed under the MIT License.
You are free to use, modify and distribute this software, as long as the license file is included.

MIT © ZebaroWhite Alliance
