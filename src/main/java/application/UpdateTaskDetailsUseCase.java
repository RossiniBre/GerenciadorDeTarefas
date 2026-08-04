package application;

import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.repositories.TaskRepository;
import domain.exceptions.TaskNotFoundException;
import domain.exceptions.InvalidFieldException;

import java.time.Clock;
import java.time.LocalDateTime;

public class UpdateTaskDetailsUseCase {
    private final TaskRepository repo;
    private final Clock clock;

    public UpdateTaskDetailsUseCase(TaskRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    public Task execute(String title, String description, TaskPriority priority, TaskCategory category, LocalDateTime dueDate, LocalDateTime reminderDate, String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);

        LocalDateTime now = LocalDateTime.now(clock);

        if (title != null) task.updateTitle(title);
        if (description != null) task.updateDescription(description);
        if (priority != null) task.updatePriority(priority);
        if (category != null) task.updateCategory(category);
        if (dueDate != null) {
            if (dueDate.isBefore(now)) throw new InvalidFieldException("dueDate não pode estar no passado");
            task.updateDueDate(dueDate);
        }
        if (reminderDate != null) {
            if (reminderDate.isBefore(now)) throw new InvalidFieldException("reminderDate não pode estar no passado");
            task.updateReminderDate(reminderDate);
        }

        return repo.save(task);
    }
}