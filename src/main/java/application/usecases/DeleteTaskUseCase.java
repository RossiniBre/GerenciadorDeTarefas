package application.usecases;

import domain.model.Task;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class DeleteTaskUseCase {
    private final TaskRepository repo;

    public DeleteTaskUseCase(TaskRepository repo){ this.repo = repo; }

    public void execute(String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);
        repo.removeTask(id);
    }
}