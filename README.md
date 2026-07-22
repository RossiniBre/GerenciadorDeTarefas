# Task Manager

A task management backend built incrementally to learn **Clean Architecture**, **SOLID**, **Design Patterns**, **Domain-Driven Design (DDD)**, and **Spec-Driven Development** using plain Java before introducing frameworks.

Instead of relying on Spring Boot from the beginning, every architectural decision is implemented manually first, making it possible to understand how a backend actually works under the hood.

---

# What's implemented so far

## Phase 1 — Foundation

- `Task` entity with constructor validation
- Static Factory Method (`Task.newTask(...)`)
- `TaskStatus` enum
- `TaskRepository` interface (Repository Pattern)
- `InMemoryTaskRepository`
- `CreateTaskUseCase`
- Constructor Dependency Injection
- Composition Root (`Main`)

---

## Phase 2 — Updating, deleting and testing

- `UpdateTaskDetailsUseCase`
- `DeleteTaskUseCase`
- `StartTaskUseCase`
- `CompleteTaskUseCase`
- Safe task status transitions enforced inside the domain
- Repository upsert behavior
- JUnit 5 configured with Maven
- Unit tests covering repositories and use cases

---

## Phase 3 — Categories, priorities and Builder

- `TaskCategory`
- `TaskPriority`
- Builder Pattern (`TaskBuilder`)
- Fluent API
- Fail-fast validation for mutable fields
- Existing use cases updated
- Existing tests updated

---

## Phase 4 — Users, ownership and authentication

### Users

- `User` entity
- `UserRepository`
- `InMemoryUserRepository`

### Task ownership

- Tasks now belong to an owner (`ownerId`)
- Ownership propagated automatically during creation
- `findAllByOwner(...)`
- Authorization enforced across every task use case

### Authentication

- `PasswordHasher` strategy
- `Pbkdf2PasswordHasher`
- `RegisterUserUseCase`
- `LoginUseCase`
- Password hashing with PBKDF2-HMAC-SHA256
- Duplicate username protection
- Authentication flow validated end-to-end

### Domain exceptions

Generic exceptions were replaced with domain-specific exceptions:

- `InvalidFieldException`
- `DuplicateUsernameException`
- `InvalidCredentialsException`
- `TaskNotFoundException`
- `UnauthorizedTaskAccessException`
- `InvalidTaskStateException`

Programming/configuration errors intentionally remain `IllegalArgumentException`.

---

## Phase 5 — MySQL persistence and REST API

### Persistence

- MySQL 8 running in Docker
- JDBC repositories
- `DatabaseConfig`
- Environment-based configuration
- Repository upsert using `ON DUPLICATE KEY UPDATE`
- UNIQUE constraint for usernames
- Secure password comparison using `MessageDigest.isEqual`
- PBKDF2 iteration count increased to 600,000 (OWASP recommendation)

### Querying

- `ListTasksUseCase`
- Optional filtering by:
  - status
  - priority
  - category
- Combined filters
- Results ordered by priority

### HTTP API (without frameworks)

A lightweight HTTP layer was implemented using Java's built-in HTTP server.

Implemented components:

- `ApiServer`
- `TasksHandler`
- `CreateTaskAction`
- `ListTasksAction`
- Request/Response DTOs
- JSON abstraction (`JsonMapper`)
- Gson implementation (`GsonJsonMapper`)
- `HttpJson` helper

Current endpoints:

- `POST /tasks`
- `GET /tasks`

The HTTP layer follows the Adapter pattern, keeping the application and domain layers completely independent of transport concerns.

---

# Architecture

```text
src/main/java
│
├── domain
│   ├── model
│   ├── repository
│   ├── services
│   └── exceptions
│
├── application
│   └── usecases
│
├── infrastructure
│   ├── http
│   │   ├── dto
│   │   ├── ApiServer
│   │   ├── TasksHandler
│   │   ├── CreateTaskAction
│   │   ├── ListTasksAction
│   │   ├── JsonMapper
│   │   ├── GsonJsonMapper
│   │   └── HttpJson
│   │
│   ├── DatabaseConfig
│   ├── MySqlTaskRepository
│   ├── MySqlUserRepository
│   ├── InMemoryTaskRepository
│   ├── InMemoryUserRepository
│   └── Pbkdf2PasswordHasher
│
└── Main
```

Dependency direction always points inward.

```
Infrastructure
        ↓
Application
        ↓
Domain
```

The domain layer has no dependency on frameworks, HTTP, JDBC, or JSON libraries.

---

# Design Patterns Used

| Pattern | Purpose |
|----------|---------|
| Repository | Persistence abstraction |
| Static Factory | Controlled entity creation |
| Builder | Fluent object construction |
| Dependency Injection | Decoupling use cases |
| Strategy | Password hashing |
| Adapter | HTTP layer and JDBC implementations |
| Exception Hierarchy | Explicit domain errors |

---

# Roadmap

| Phase | Status | Scope |
|--------|------|-------|
| 1 | Done | Task creation |
| 2 | Done | Update, delete, tests |
| 3 | Done | Categories, priorities, Builder |
| 4 | Done | Users, authentication, authorization |
| 5 | In Progress | MySQL, REST API, HTTP adapters |
| 6 | | AI-assisted task creation |
| 7 | | Notifications |
| 8 | | Migration to Spring Boot |

---

# Tech Stack

- Java 21
- Maven
- JUnit 5
- MySQL 8
- JDBC
- Gson
- Java Built-in HTTP Server (`com.sun.net.httpserver.HttpServer`)
- Docker
- WSL2

---

# Running the Project

Run:

```
Main.java
```

Requirements:

- MySQL running
- Environment variables:

```
DB_HOST
DB_PORT
DB_USER
DB_PASSWORD
```

The HTTP server starts locally and exposes the REST endpoints.

---

# Running the Tests

Using Maven:

```bash
mvn test
```

Or through IntelliJ:

```
Lifecycle → test
```

---

# Project Goals

The objective is to build a production-style backend while introducing one architectural concept at a time.

The final project will include:

- Clean Architecture
- SOLID
- Design Patterns
- REST API
- Authentication
- Authorization
- MySQL
- AI-assisted task creation
- Spring Boot
- Comprehensive automated tests