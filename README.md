# 💧 BeverageTracker — Gamified Hydration & Beverage Consumption API

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Spring_Data_JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI_3-Swagger_UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle&logoColor=white)

**BeverageTracker** is a full-stack web application and RESTful backend engineered in **Java** and **Spring Boot** that applies **gamification mechanics** (points, badges/achievements, daily hydration goals, and real-time leaderboards) to help users build and maintain optimal hydration habits.

Unlike simple water counters, BeverageTracker models **beverage-specific hydration coefficients** (e.g., Water `1.0`, Unsweetened Tea `0.95`, Juice `0.85`, Coffee `0.80`, Sweetened Soda `0.65`) and automatically computes **effective hydration volume** alongside raw intake.

---

## ✨ Key Engineering Highlights

* **Clean Layered Architecture:** Strict separation of concerns across `Controller` → `DTO / Mapper` → `Service` → `Repository` → `Entity` layers, preventing domain model leakage and circular serialization issues.
* **Domain-Driven Hydration Calculation:** Automatically resolves beverage types and calculates effective hydration (`effectiveHydrationMl = round(amountMl × hydrationFactor)`) within transactional service boundaries.
* **Gamification & Leaderboard Engine:** Tracks user consumption milestones, unlocks achievements automatically upon reaching thresholds, and computes aggregated rankings via custom **JPQL constructor expressions** (`SELECT new ... UserStatsDto(...)`).
* **Declarative Validation & Centralized Error Handling:** Uses **Jakarta Bean Validation** (`@Valid`, `@NotBlank`, `@Email`, `@Min`, `@Max`, `@DecimalMin`) paired with a `@ControllerAdvice` global exception handler returning consistent HTTP status codes (`400 Bad Request`, `404 Not Found`, `201 Created`).
* **Zero-Config Startup Seeding:** Implements a `CommandLineRunner` (`DataInitializer`) that idempotently seeds default beverage catalog items and hydration coefficients on application startup.
* **Multi-Layer Automated Testing:**
  * **Unit Tests (`@ExtendWith(MockitoExtension.class)`):** Isolated business-logic verification for services (`UserServiceTest`, `ConsumptionServiceTest`).
  * **Web Slice Tests (`@WebMvcTest`):** Controller routing, JSON serialization, and validation testing with `MockMvc` (`UserControllerTest`).
  * **End-to-End Integration Tests (`@SpringBootTest`):** Full lifecycle testing with `TestRestTemplate` against a live embedded web server (`FullSystemTest`).

---

## 📸 Screenshots & API Documentation

### Web Interface & Real-Time Leaderboard
![Application GUI](docs/images/gui.png)
![Leaderboard View](docs/images/leaderboard.png)

### Interactive OpenAPI 3 / Swagger UI
![Swagger UI Documentation](docs/images/swaggerUI.png)

---

## 🏗️ System Architecture & Project Structure

```text
src/main/java/cz/vsfs/danilalisitskii/beveragetracker/
├── config/
│   └── DataInitializer.java          # Idempotent startup seeding for default beverage types
├── controller/
│   ├── AchievementController.java    # REST endpoints for achievements management
│   ├── BeverageTypeController.java   # REST endpoints for beverage catalog & hydration factors
│   ├── ConsumptionController.java    # REST endpoints for logging intake & leaderboards
│   ├── UserController.java           # REST endpoints for user profiles & daily hydration goals
│   └── dto/
│       ├── *Dto.java / *CreateDto.java
│       └── mappers/                  # Dedicated Entity <-> DTO mappers
├── entity/
│   ├── Achievement.java              # Gamification badge entity
│   ├── BeverageType.java             # Beverage catalog item with hydration coefficient
│   ├── Consumption.java              # Recorded intake linked to User and BeverageType
│   └── User.java                     # User profile, daily water goal, and M:N achievements
├── exception/
│   ├── GlobalExceptionHandler.java   # Centralized @ControllerAdvice error responses
│   └── ResourceNotFoundException.java
├── repository/
│   ├── AchievementRepository.java
│   ├── BeverageTypeRepository.java
│   ├── ConsumptionRepository.java    # Includes custom JPQL aggregation for leaderboards
│   └── UserRepository.java
└── service/
    ├── AchievementService.java
    ├── BeverageTypeService.java
    ├── ConsumptionService.java       # Transactional intake logging, hydration math & achievement checks
    └── UserService.java
```

---

## 🔌 REST API Overview

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| **Users** | | | |
| `GET` | `/users` | List all registered users and their unlocked achievements | `200 OK` |
| `GET` | `/users/{id}` | Get user profile, daily water goal (`dailyWaterGoalMl`), and badges | `200 OK`, `404` |
| `POST` | `/users` | Register a new user (defaults to `2000 ml` daily goal if omitted) | `201 Created`, `400` |
| `PUT` | `/users/{id}` | Update user profile and daily hydration goal (`500–10000 ml`) | `200 OK`, `400`, `404` |
| `DELETE` | `/users/{id}` | Delete user and cascade-remove consumption history | `204 No Content`, `404` |
| `POST` | `/users/{userId}/achievements/{achievementId}` | Manually assign an achievement to a user | `200 OK`, `404` |
| **Beverage Types** | | | |
| `GET` | `/beverage-types` | List all beverage types with hydration factors and icons | `200 OK` |
| `GET` | `/beverage-types/{id}` | Get details of a specific beverage type | `200 OK`, `404` |
| `POST` | `/beverage-types` | Create a custom beverage type (`hydrationFactor` `0.1–1.5`) | `201 Created`, `400` |
| **Consumptions & Analytics** | | | |
| `POST` | `/consumptions` | Log beverage intake (`10–3000 ml`), compute effective hydration & evaluate badges | `201 Created`, `400`, `404` |
| `GET` | `/consumptions` | Retrieve full consumption history with calculated hydration values | `200 OK` |
| `DELETE` | `/consumptions/{id}` | Delete a specific consumption entry | `204 No Content`, `404` |
| `GET` | `/consumptions/stats/leaderboard` | Get global leaderboard ranked by total volume consumed | `200 OK` |
| **Achievements** | | | |
| `GET` | `/achievements` | List all available gamification achievements | `200 OK` |
| `GET` | `/achievements/{id}` | Get a specific achievement by ID | `200 OK`, `404` |
| `POST` | `/achievements` | Create a new achievement definition | `201 Created`, `400` |
| `PUT` | `/achievements/{id}` | Update achievement parameters | `200 OK`, `404` |
| `DELETE` | `/achievements/{id}` | Delete an achievement | `204 No Content`, `404` |

### Example: Logging Coffee Intake (`POST /consumptions`)

**Request:**
```json
{
  "userId": 1,
  "beverageTypeId": 4,
  "amountMl": 250
}
```

**Response (`201 Created`):**
```json
{
  "id": 12,
  "userId": 1,
  "username": "danila",
  "beverageTypeId": 4,
  "beverageName": "Coffee",
  "hydrationFactor": 0.8,
  "amountMl": 250,
  "effectiveHydrationMl": 200,
  "createdAt": "2026-11-08T13:45:10.412"
}
```

---

## 🚀 Getting Started

### Prerequisites
* **JDK 25** (configured via Gradle Java Toolchain)
* **PostgreSQL 16+** running on `localhost:5432`

### 1. Start PostgreSQL (via Docker or local service)
If you don't have a local PostgreSQL instance running, spin one up in seconds with Docker:
```bash
docker run --name beverage-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 \
  -d postgres:16-alpine
```

### 2. Build & Run the Application
```bash
git clone <your-repository-url>
cd beverageTracker
./gradlew bootRun
```

* **REST API Base URL:** `http://localhost:8080`
* **Swagger UI (Interactive Docs):** `http://localhost:8080/swagger-ui/index.html`

### 3. Run Automated Tests
```bash
./gradlew test
```

---

## 🗺️ Engineering Roadmap

- [x] **Phase 1 — Core Domain & Hydration Engine:** Multi-beverage catalog (`BeverageType`), effective hydration math, user daily hydration goals, OpenAPI documentation, and test suite.
- [ ] **Phase 2 — Authentication & RBAC Security:** Stateless JWT authentication (`Spring Security`), `BCrypt` password hashing, and role separation (`ROLE_USER`, `ROLE_ADMIN`).
- [ ] **Phase 3 — Dynamic Rule-Based Gamification Engine:** Configurable achievement rules (`TOTAL_DRINKS`, `TOTAL_VOLUME_ML`, `DAILY_STREAK`, `WATER_PERCENTAGE`) and streak tracking.
- [ ] **Phase 4 — DevOps & CI/CD Pipeline:** Multi-stage `Dockerfile`, `docker-compose.yml` orchestration, and automated GitHub Actions CI/CD workflow with JaCoCo test coverage reporting.

---

## 👤 Author

**Danila Lisitskii**  
*Bachelor's Thesis Project — Applied Informatics (VŠFS Prague)*
