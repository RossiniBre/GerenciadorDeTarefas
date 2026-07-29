package application.usecases;

import domain.model.Task;
import domain.model.User;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class CompleteTaskUseCase {
    private final TaskRepository repo;

    public CompleteTaskUseCase(TaskRepository repo) { this.repo = repo; }

    public Task execute(String taskId, User loggedUser) {
        Task task = repo.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        task.completeTask();
        return repo.save(task);
    }
}