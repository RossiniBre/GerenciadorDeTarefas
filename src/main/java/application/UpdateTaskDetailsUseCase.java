package application;

import domain.Task;
import domain.TaskCategory;
import domain.TaskPriority;
import domain.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class UpdateTaskDetailsUseCase {
    private final TaskRepository repo;

    public UpdateTaskDetailsUseCase(TaskRepository repo){ this.repo = repo; }

    public Task execute(String title, String description, String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);
        task.updateDetails(title, description);
        return repo.save(task);
    }
}