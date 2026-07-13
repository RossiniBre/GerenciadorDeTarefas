package application;

import domain.Task;
import domain.TaskRepository;

public class CreateTaskUseCase {

    private TaskRepository repo;

    public CreateTaskUseCase(TaskRepository repo){
        this.repo = repo;
    }

    public Task execute(String title, String description){
        Task task = Task.newTask(title, description);
        return repo.save(task);
    }
}
