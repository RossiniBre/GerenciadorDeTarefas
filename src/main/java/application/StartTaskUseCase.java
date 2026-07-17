package application;

import domain.Task;
import domain.TaskRepository;
import domain.exceptions.TaskNotFoundException;

public class StartTaskUseCase {
    private final TaskRepository repo;

    public StartTaskUseCase(TaskRepository repo){ this.repo = repo; }

    public void execute(String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);
        task.startTask();
        repo.save(task);
    }
}