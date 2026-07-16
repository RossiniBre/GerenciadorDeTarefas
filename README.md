# Task Manager

A task management backend built incrementally to learn **Clean Architecture**, **SOLID**, **Design Patterns**, **Domain-Driven Design principles**, and **Spec-Driven Development** using plain Java before introducing frameworks.

The project intentionally starts without Spring Boot or a database so every architectural decision can be understood from first principles.

---

# What's implemented so far

## Phase 1 — Foundation

- `Task` entity with constructor validation and a Static Factory Method (`Task.newTask(...)`)
- `TaskStatus` enum
- `TaskRepository` interface (Repository Pattern)
- `InMemoryTaskRepository` implementation
- `CreateTaskUseCase` using constructor injection (Dependency Inversion)
- `Main` as the composition root

---

## Phase 2 — Updating, deleting and testing

- `Task.updateDetails(...)` with the same validation rules used during creation
- `UpdateTaskDetailsUseCase`
- `DeleteTaskUseCase`
- `StartTaskUseCase`
- `CompleteTaskUseCase`
- Safe status transitions enforced inside the domain entity
- `InMemoryTaskRepository.save(...)` performs upsert instead of always inserting
- JUnit 5 configured with Maven
- Unit tests covering repositories and application use cases

---

## Phase 3 — Categories, priorities and Builder

- `TaskCategory` enum
- `TaskPriority` enum
- Category and priority support inside `Task`
- Fail-fast validation for every mutable field
- `TaskBuilder` with fluent method chaining
- Builder delegates object creation to `Task.newTask(...)`, avoiding duplicated construction logic
- Existing use cases updated to support the new fields
- Existing unit tests updated and all passing

---

## Phase 4 — Users and task ownership (In Progress)

- `User` entity
- `UserRepository` interface
- `InMemoryUserRepository`
- `Task` now stores the owner's identifier (`ownerId`)
- `TaskBuilder` requires an `ownerId`
- Ownership propagated automatically during task creation
- `TaskRepository.findAllByOwner(...)`
- End-to-end ownership flow validated:

```
User
      ↓
CreateTaskUseCase
      ↓
Task (ownerId)
      ↓
TaskRepository
      ↓
findAllByOwner(ownerId)
```

- Ownership verification added to every task use case, preventing users from accessing or modifying tasks they do not own
- `PasswordHasher` strategy interface introduced in preparation for authentication
- Existing unit tests updated and all passing

---

# Architecture

```text
src/main/java/
├── domain/
│   ├── Task.java
│   ├── User.java
│   ├── TaskBuilder.java
│   ├── TaskStatus.java
│   ├── TaskCategory.java
│   ├── TaskPriority.java
│   ├── TaskRepository.java
│   ├── UserRepository.java
│   └── PasswordHasher.java
│
├── application/
│   ├── CreateTaskUseCase.java
│   ├── UpdateTaskDetailsUseCase.java
│   ├── DeleteTaskUseCase.java
│   ├── StartTaskUseCase.java
│   └── CompleteTaskUseCase.java
│
└── infrastructure/
    ├── InMemoryTaskRepository.java
    ├── InMemoryUserRepository.java
    └── Main.java

src/test/java/
└── ...
```

Dependency direction always points inward:

```
Infrastructure
        ↓
Application
        ↓
Domain
```

The domain layer has no knowledge of infrastructure or framework-specific code.

---

# Design Patterns Used

| Pattern | Purpose |
|----------|---------|
| Repository | Abstract persistence from the application layer |
| Static Factory | Centralize entity creation and validation |
| Builder | Create complex objects through a fluent API |
| Dependency Injection | Decouple use cases from concrete implementations |
| Strategy (preparation) | Password hashing abstraction |

Additional patterns will be introduced as the project evolves.

---

# Roadmap

| Phase | Status | Scope | Main Pattern |
|-------|-------|-------|--------------|
| 1 | Done | Task creation and in-memory persistence | Repository, Static Factory |
| 2 | Done | Updating, deleting and testing | — |
| 3 | Done | Categories, priorities and Builder | Builder |
| 4 | In Progress | Users, task ownership and authentication | Strategy |
| 5 | | REST API + MySQL persistence | Adapter |
| 6 | | AI-assisted task creation from free text | Strategy |
| 7 | | Notifications | Observer |
| 8 | | Migration to Spring Boot | — |

---

# Tech Stack

- Java 21 (Amazon Corretto)
- Maven
- JUnit 5

Current persistence is entirely in memory.

MySQL will be introduced in Phase 5 after the architecture is fully established.

Spring Boot will only be introduced later so the project focuses on architecture before framework conventions.

---

# Running the Project

Open the project in IntelliJ IDEA and run:

```text
Main.java
```

A Maven entry point may be added in future phases.

---

# Running the Tests

Using IntelliJ:

```text
Maven → Lifecycle → test
```

Or from the command line:

```bash
mvn test
```

---

# Project Goals

This project is being developed as a long-term learning exercise focused on backend engineering.

Each phase introduces one new architectural concept while preserving the previous ones, allowing the codebase to evolve incrementally rather than becoming a complete application from day one.

The final objective is to build a backend featuring:

- Clean Architecture
- SOLID principles
- Design Patterns
- Authentication
- Authorization
- REST API
- MySQL persistence
- AI-assisted task creation
- Spring Boot
- Comprehensive automated tests