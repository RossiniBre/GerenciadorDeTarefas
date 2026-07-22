package application;
import domain.Task;
import domain.TaskRepository;
import domain.User;
import domain.TaskPriority;
import domain.TaskCategory;
public class CreateTaskUseCase {
    private final TaskRepository repo;

    public CreateTaskUseCase(TaskRepository repo){ this.repo = repo; }
    public Task execute(String title, String description, User loggedUser, TaskPriority priority, TaskCategory category){
        Task task = Task.newTask(title, description, loggedUser.getId());

        if (priority != null) {
            task.updatePriority(priority);
        } if (category != null) {
            task.updateCategory(category);
        }

        return repo.save(task); }
}