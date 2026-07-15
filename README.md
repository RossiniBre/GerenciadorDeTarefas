# Task Manager

A task management backend built incrementally to learn Clean
Architecture, SOLID, Design Patterns, and Spec-Driven Development in
Java.

## What's implemented so far

### Phase 1 — Foundation
- `Task` entity with constructor validation and a Static Factory
  Method (`Task.newTask(...)`)
- `TaskStatus` enum
- `TaskRepository` interface (Repository Pattern)
- `InMemoryTaskRepository` — first implementation, no database yet
- `CreateTaskUseCase` — constructor-injected with the repository
  interface (Dependency Inversion)
- `Main` as the composition root

### Phase 2 — Update, delete and testing
- `Task.updateDetails(...)` — mutates title/description with the
  same validation rules as construction
- `UpdateTaskDetailsUseCase` — find, mutate, persist and return the
  updated `Task`
- `DeleteTaskUseCase` — find (to guarantee existence), then remove
- `StartTaskUseCase` / `CompleteTaskUseCase` — status transitions
  guarded by `Task`'s own state-change rules
- `InMemoryTaskRepository.save` now performs an upsert instead of
  always appending, preventing duplicate tasks
- JUnit 5 configured via Maven
- Unit tests covering repository behavior and application use cases

### Phase 3 — Categories, priorities and Builder
- `TaskCategory` enum
- `TaskPriority` enum
- `Task` now supports category and priority
- Fail-fast validation for every field update, keeping entity state
  always valid
- `TaskBuilder` with fluent method chaining
- Builder reuses `Task.newTask(...)` and applies only the desired
  updates instead of duplicating construction logic
- Existing use cases updated to support the new fields
- Existing unit tests updated and all passing

## Architecture

```
src/main/java/
├── domain/
│   ├── Task.java
│   ├── TaskBuilder.java
│   ├── TaskStatus.java
│   ├── TaskCategory.java
│   ├── TaskPriority.java
│   └── TaskRepository.java
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
    └── Main.java

src/test/java/
└── ...
```

Dependency direction always points inward: infrastructure depends on
domain, never the other way around.

## Roadmap

| Phase         | Scope | Pattern |
|---------------|-------|---------|
| 1 done        | Create tasks, in-memory persistence | Repository, Static Factory |
| 2 done        | Update/delete, status transitions and unit tests | — |
| 3 done        | Categories, priorities and fluent TaskBuilder | Builder |
| 4 in progress | Users and authentication | Strategy |
| 5             | REST API + MySQL persistence | Adapter |
| 6             | AI-assisted task creation from free text | Strategy |
| 7             | Notifications | Observer |
| 8             | Migrate to Spring Boot | — |

## Tech stack

- Java 21 (Amazon Corretto)
- Maven
- JUnit 5

MySQL will be introduced in Phase 5. No framework is used yet, by
design, so the project focuses on understanding the architecture
before introducing Spring Boot.

## Running it

Open the project in IntelliJ IDEA and run `Main.java` directly.

Maven-based execution will be added later as the project evolves.

## Running tests

Using IntelliJ:

`Maven → Lifecycle → test`

or, if Maven is installed on your machine:

```bash
mvn test
```