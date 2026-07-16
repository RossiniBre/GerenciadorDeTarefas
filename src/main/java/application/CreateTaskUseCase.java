package application;

import domain.Task;
import domain.TaskRepository;
import domain.User;

public class CreateTaskUseCase {

    private final TaskRepository repo;

    public CreateTaskUseCase(TaskRepository repo){ this.repo = repo; }

    public Task execute(String title, String description, User loggedUser){
        Task task = Task.newTask(title, description, loggedUser.getId());
        return repo.save(task);
    }
}
