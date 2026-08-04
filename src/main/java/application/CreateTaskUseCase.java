package application;

import domain.model.Task;
import domain.repositories.TaskRepository;
import domain.model.User;
import domain.model.TaskPriority;
import domain.model.TaskCategory;
import domain.exceptions.InvalidFieldException;

import java.time.Clock;
import java.time.LocalDateTime;

public class CreateTaskUseCase {
    private final TaskRepository repo;
    private final Clock clock;

    public CreateTaskUseCase(TaskRepository repo, Clock clock){
        this.repo = repo;
        this.clock = clock;
    }

    public Task execute(String title, String description, User loggedUser, TaskPriority priority, TaskCategory category, LocalDateTime dueDate, LocalDateTime reminderDate){
        LocalDateTime now = LocalDateTime.now(clock);

        if (dueDate != null && dueDate.isBefore(now)) {
            throw new InvalidFieldException("dueDate não pode estar no passado");
        }
        if (reminderDate != null && reminderDate.isBefore(now)) {
            throw new InvalidFieldException("reminderDate não pode estar no passado");
        }

        Task task = Task.newTask(title, description, loggedUser.getId(), dueDate, reminderDate);

        if (priority != null) {
            task.updatePriority(priority);
        } if (category != null) {
            task.updateCategory(category);
        }

        return repo.save(task);
    }
}