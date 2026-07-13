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

## Architecture

```
src/main/java/
  domain/           Business rules. No knowledge of infrastructure.
    Task.java
    TaskStatus.java
    TaskRepository.java      (interface — the contract)

  application/      Use cases. Orchestrates the domain.
    CreateTaskUseCase.java

  infrastructure/   Technical details (persistence, frameworks).
    InMemoryTaskRepository.java

  Main.java         Composition root.
```

Dependency direction always points inward: infrastructure depends on
domain, never the other way around.

## Roadmap

| Phase | Scope | Pattern |
|---|---|---|
| 1 done | Create tasks, in-memory persistence | Repository, Static Factory |
| 2 | Update/delete, unit tests with a fake repository | -- |
| 3 | Categories and priorities | Factory or Builder |
| 4 | Users and authentication | Strategy |
| 5 | REST API + MySQL persistence | Adapter |
| 6 | AI-assisted task creation from free text | Strategy |
| 7 | Notifications | Observer |
| 8 | Migrate to Spring Boot | -- |

## Tech stack

Java 21 (Corretto), Maven. MySQL from Phase 5 onward. No framework
yet, by design.

## Running it

Open the project in IntelliJ IDEA and run `Main.java` directly
(right-click -> Run 'Main.main()'). Maven-based execution isn't
configured yet -- that comes once the project is packaged as a
proper artifact, later in the roadmap.