# Task Manager

A task management backend built incrementally to learn **Clean Architecture**, **SOLID**, **Design Patterns**, **Domain-Driven Design (DDD)**, and **Spec-Driven Development** using plain Java before introducing frameworks.

Instead of relying on Spring Boot from the beginning, every architectural decision is implemented manually first, making it possible to understand how a backend actually works under the hood.

The project evolves from a simple in-memory application into a production-style backend with persistence, authentication, authorization, REST APIs, and automated integration testing.

---

# What's implemented so far

## Phase 1 — Foundation

* `Task` entity with constructor validation
* Static Factory Method (`Task.newTask(...)`)
* `TaskStatus` enum
* `TaskRepository` interface (Repository Pattern)
* `InMemoryTaskRepository`
* `CreateTaskUseCase`
* Constructor Dependency Injection
* Composition Root (`Main`)

---

## Phase 2 — Updating, deleting and testing

* `UpdateTaskDetailsUseCase`
* `DeleteTaskUseCase`
* `StartTaskUseCase`
* `CompleteTaskUseCase`
* Safe task status transitions enforced inside the domain
* Repository upsert behavior
* JUnit 5 configured with Maven
* Unit tests covering repositories and use cases

---

## Phase 3 — Categories, priorities and Builder

* `TaskCategory`
* `TaskPriority`
* Builder Pattern (`TaskBuilder`)
* Fluent API
* Fail-fast validation for mutable fields
* Existing use cases updated
* Existing tests updated

---

# Phase 4 — Users, ownership and authentication

## Users

Implemented a complete user domain:

* `User` entity
* `UserRepository`
* `InMemoryUserRepository`
* `MySqlUserRepository`

## Task ownership

Tasks are now associated with users.

Implemented:

* `ownerId` propagation during task creation
* Owner-based task retrieval
* Ownership validation inside task use cases
* Authorization checks preventing users from accessing tasks they do not own

The flow became:

```
User
 ↓
CreateTaskUseCase
 ↓
TaskBuilder
 ↓
Task(ownerId)
 ↓
Repository
 ↓
Database
```

---

## Authentication

Authentication was implemented without external frameworks.

Features:

* `PasswordHasher` strategy
* `Pbkdf2PasswordHasher`
* `RegisterUserUseCase`
* `LoginUseCase`
* Password hashing using PBKDF2-HMAC-SHA256
* Secure password comparison
* Token-based authentication flow
* Logout and token invalidation
* Generic exceptions were replaced with explicit domain exceptions:

Security improvements:

* Passwords are never stored in plain text
* PBKDF2 iteration count configured with 600,000 iterations
* Username uniqueness enforced
* Authentication failures return domain-specific errors

---

# Phase 5 — MySQL persistence and REST API

## Persistence

Database integration implemented using JDBC:

* JDBC persistence layer
* MySQL repositories
* Database configuration
* Environment-based configuration
* Transaction handling
* Database constraints

---

# REST API (without frameworks)

A lightweight HTTP layer was created using Java's built-in HTTP server:

```
com.sun.net.httpserver.HttpServer
```

The API follows the Adapter pattern, keeping HTTP concerns isolated from the application and domain layers.

Implemented components:

* `ApiServer`
* `TasksHandler`
* User handlers
* Request/Response DTOs
* `JsonMapper`
* `GsonJsonMapper`
* `HttpJson`
* HTTP routing system

---

# Current API Endpoints

## Users

| Method | Endpoint | Description |
|---|---|---|
| POST | `/users/register` | Register a new user |
| POST | `/users/login` | Authenticate and generate token |
| DELETE | `/users/logout` | Invalidate authentication session |

## Tasks

| Method | Endpoint | Description |
|---|---|---|
| POST | `/tasks` | Create a task (authentication required) |
| GET | `/tasks` | List tasks with optional filters |
| PATCH | `/tasks/{id}` | Partially update a task |
| DELETE | `/tasks/{id}` | Delete a task |

Supported filters:

- `status`
- `priority`
- `category`

Examples:

```
GET /tasks?priority=HIGH
GET /tasks?category=WORK
```

# API Testing

A complete Postman Collection was created to validate the API end-to-end.

Current automated coverage:

* User registration
* Authentication
* Token generation
* Protected routes
* Logout
* Token invalidation
* Task creation
* Task listing
* Filtering
* Task update
* Task deletion
* Unauthorized requests
* Invalid credentials handling

Current test suite: 49 automated tests passing

---

# Test User Strategy

The Postman Collection intentionally creates isolated users during executions.

```
Example: usuarioTeste_1785171015409
```

The timestamp suffix is generated to guarantee uniqueness between executions.

This allows:

* Running the collection multiple times without conflicts
* Testing complete registration flows
* Avoiding dependency between test runs
* Validating authentication with fresh users

This behavior is intentional and simulates independent user registrations.

# Phase 6 — AI-assisted task creation

## Assistant workflow foundation

The project started introducing an AI-assisted workflow for creating tasks through natural language.

The objective is to allow users to describe tasks conversationally while maintaining the same architectural principles used throughout the project: separation of responsibilities, domain isolation, and controlled data flow.

Implemented so far:

* Assistant workflow foundation
* User intent classification before task generation
* Structured assistant response flow
* Validation pipeline before task creation
* Task suggestion generation contract

---

## Assistant response handling

The assistant communication was designed with explicit response states instead of generic responses.

Every interaction is classified into one of three possible scenarios:

* Valid suggestions — when the user's request contains enough information to generate task suggestions
* Missing information — when the request is related to task creation but additional details are required
* Out of scope — when the message does not belong to the task management context

This approach prevents uncontrolled assistant behavior and keeps the interaction predictable for the application layer.

---

## Assistant task operations

he assistant workflow was expanded from task suggestion generation into task management operations.

Implemented capabilities:

* Natural language task creation
* Task listing through natural language requests
* Task update requests
* Task deletion requests
* Task lifecycle actions (start and complete)
* Structured assistant responses

The assistant does not directly modify the domain.

All operations still pass through the existing application use cases, preserving validations, ownership rules, and business constraints.

---

## Assistant session management

The assistant workflow introduced session-based context management.

Implemented:

* Assistant session abstraction
* Session repository contract
* Redis-based session persistence
* In-memory session implementation for testing
* User-scoped assistant interactions

The current implementation maintains assistant-related session data, preparing the architecture for future conversational context handling.

Implemented:

* `AssistantSession`
* `AssistantSessionRepository`
* Redis-based session persistence
* Docker Redis container integration
* In-memory session implementation for testing

---

## Validation pipeline

Before a task can be created, the assistant workflow performs multiple verification steps.

The assistant does not directly create tasks or bypass domain rules.

After generating a valid suggestion, the existing application flow is reused.

This keeps task ownership, validations, and persistence responsibilities inside the existing backend architecture.

---
# Engineering Principles

Throughout the project, every feature follows these principles:

* Business rules remain inside the domain layer
* External technologies depend on application contracts
* Use cases coordinate application behavior
* Infrastructure details can be replaced without changing business logic
* Tests validate behavior instead of implementation details

---

# Architecture

---
```
src/main/java
│
├── domain
│   ├── model
│   ├── repository
│   ├── assistant
│   ├── exceptions
│   └── security
│
├── application
│   └── usecases
│
├── infrastructure
│   │
│   ├── http
│   │   ├── dto
│   │   ├── ApiServer
│   │   ├── TasksHandler
│   │   ├── UserHandlers
│   │   ├── Actions
│   │   ├── JsonMapper
│   │   ├── GsonJsonMapper
│   │   └── HttpJson
│   │
│   ├── config
│   │   └── DatabaseConfig
│   │   
│   ├── security
│   │   ├── Pbkdf2PasswordHasher
│   │   └── UuidTokenGenerator
│   │   
│   └── persistence
│   
└── Main
```
Dependency direction:

```
Infrastructure
        ↓
Application
        ↓
Domain
```

The domain layer has no dependency on:

* HTTP
* JDBC
* JSON libraries
* Frameworks

---

# Design Patterns Used

| Pattern              | Purpose                           |
| -------------------- | --------------------------------- |
| Repository           | Persistence abstraction           |
| Static Factory       | Controlled entity creation        |
| Builder              | Fluent object construction        |
| Dependency Injection | Use case decoupling               |
| Strategy             | Password hashing abstraction      |
| Adapter              | HTTP and database implementations |
| Exception Hierarchy  | Explicit domain errors            |

---

# Roadmap

| Phase | Status  | Scope                                |
| ----- | ------- | ------------------------------------ |
| 1     | Done    | Task creation                        |
| 2     | Done    | Update, delete, tests                |
| 3     | Done    | Categories, priorities, Builder      |
| 4     | Done    | Users, authentication, authorization |
| 5     | Done    | MySQL, REST API, HTTP adapters       |
| 6 | In Progress | AI assistant workflow, conversational memory and task operations |
| 7     | | Notifications                                |
| 8     | | Migration to Spring Boot                     |
----------------------------------------------------------


# Tech Stack

* Java 21
* Maven
* JUnit 5
* MySQL 8
* JDBC
* Gson
* Java Built-in HTTP Server
* Docker
* WSL2
* Postman
* Redis

---

# Running the Project

Run:

```
Main.java
```

Requirements:

* Java 21+
* MySQL running
* Docker (recommended)

Environment variables:

```
DB_HOST
DB_PORT
DB_USER
DB_PASSWORD
```

The HTTP server starts locally and exposes the REST endpoints.

---

# Running Tests

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

Final goals:

* Clean Architecture
* SOLID principles
* Design Patterns
* REST API
* Authentication
* Authorization
* MySQL persistence
* Automated API testing
* AI-assisted task creation
* Spring Boot migration
