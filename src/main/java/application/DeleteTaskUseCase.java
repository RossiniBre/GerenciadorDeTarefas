package application;

import domain.Task;
import domain.TaskRepository;

public class DeleteTaskUseCase {
    private final TaskRepository repo;

    public DeleteTaskUseCase(TaskRepository repo){ this.repo = repo; }

    public void execute(String id) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada!"));
        repo.removeTask(id);
    }
}