# Task Manager

A task management backend built incrementally to learn Clean
Architecture, SOLID, Design Patterns, and Spec-Driven Development in
Java.

## What's implemented so far

**Phase 1 — Foundation**
- `Task` entity with constructor validation and a Static Factory
  Method (`Task.newTask(...)`)
- `TaskStatus` enum
- `TaskRepository` interface (Repository Pattern)
- `InMemoryTaskRepository` — first implementation, no database yet
- `CreateTaskUseCase` — constructor-injected with the repository
  interface (Dependency Inversion)
- `Main` as the composition root

**Phase 2 — Update, delete, testing (in progress)**
- `Task.updateDetails(...)` — mutates title/description with the
  same validation rules as construction
- `UpdateTaskDetailsUseCase` — find, mutate, persist, return the
  updated `Task`
- `DeleteTaskUseCase` — find (to guarantee existence), then remove
- `StartTaskUseCase` / `CompleteTaskUseCase` — status transitions
  guarded by `Task`'s own state-change rules
- `InMemoryTaskRepository.save` now upserts (removes any existing
  entry with the same id before adding) instead of always
  appending — fixes a duplication bug found via testing
- JUnit 5 configured via Maven; unit tests cover
  `InMemoryTaskRepository` (save/find/no-duplication on update)
- **Still missing:** unit tests for the use cases themselves
  (currently only the repository is covered)

## Architecture
src/main/java/
domain/           Business rules. No knowledge of infrastructure.
Task.java
TaskStatus.java
TaskRepository.java (interface — the contract)
application/       Use cases. Orchestrates the domain.
CreateTaskUseCase.java
UpdateTaskDetailsUseCase.java
DeleteTaskUseCase.java
StartTaskUseCase.java
CompleteTaskUseCase.java
infrastructure/     Technical details (persistence, frameworks).
InMemoryTaskRepository.java
Main.java           Composition root.
src/test/java/
infrastructure/
InMemoryTaskRepositoryTest.java

Dependency direction always points inward: infrastructure depends on
domain, never the other way around.

## Roadmap

| Phase | Scope | Pattern |
|---|---|---|
| 1 done | Create tasks, in-memory persistence | Repository, Static Factory |
| 2 in progress | Update/delete done; use-case unit tests still pending | -- |
| 3 | Categories and priorities | Factory or Builder |
| 4 | Users and authentication | Strategy |
| 5 | REST API + MySQL persistence | Adapter |
| 6 | AI-assisted task creation from free text | Strategy |
| 7 | Notifications | Observer |
| 8 | Migrate to Spring Boot | -- |

## Tech stack

Java 21 (Corretto), Maven, JUnit 5. MySQL from Phase 5 onward. No
framework yet, by design.

## Running it

Open the project in IntelliJ IDEA and run `Main.java` directly
(right-click -> Run 'Main.main()'). Maven-based execution isn't
configured yet -- that comes once the project is packaged as a
proper artifact, later in the roadmap.

## Running tests

Via IntelliJ's Maven tool window: `Lifecycle` -> `test`. (The `mvn`
CLI isn't on PATH outside the IDE yet.)