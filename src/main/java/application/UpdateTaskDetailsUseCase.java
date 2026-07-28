package application;

import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class UpdateTaskDetailsUseCase {
    private final TaskRepository repo;

    public UpdateTaskDetailsUseCase(TaskRepository repo) { this.repo = repo; }

    public Task execute(String title, String description, TaskPriority priority, TaskCategory category, String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);

        if (title != null) task.updateTitle(title);
        if (description != null) task.updateDescription(description);
        if (priority != null) task.updatePriority(priority);
        if (category != null) task.updateCategory(category);

        return repo.save(task);
    }
}