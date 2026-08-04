package application;

import domain.model.Task;
import domain.model.User;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class StartTaskUseCase {
    private final TaskRepository repo;

    public StartTaskUseCase(TaskRepository repo) { this.repo = repo; }

    public Task execute(String taskId, User loggedUser) {
        Task task = repo.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        task.verifyOwnership(loggedUser.getId());
        task.startTask();
        return repo.save(task);
    }
}