package application;

import domain.Task;
import domain.TaskRepository;

public class StartTaskUseCase {
    private final TaskRepository repo;

    public StartTaskUseCase(TaskRepository repo){ this.repo = repo; }

    public void execute(String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada!"));
        task.verifyOwnership(requesterId);
        task.startTask();
        repo.save(task);
    }
}