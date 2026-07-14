package application;

import domain.Task;
import domain.TaskRepository;

public class StartTaskUseCase {
    private final TaskRepository repo;

    public StartTaskUseCase(TaskRepository repo){ this.repo = repo; }

    public void execute(String id) {
        Task task = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada!"));
        task.startTask();
        repo.save(task);
    }
}