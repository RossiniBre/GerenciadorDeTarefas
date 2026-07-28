package application;

import domain.model.Task;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class CompleteTaskUseCase {
    private final TaskRepository repo;

    public CompleteTaskUseCase(TaskRepository repo){
        this.repo = repo;
    }

    public void execute(String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);
        task.completeTask();
        repo.save(task);
    }
}