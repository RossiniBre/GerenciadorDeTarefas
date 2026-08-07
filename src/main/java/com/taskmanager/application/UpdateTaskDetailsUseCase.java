package com.taskmanager.application;

import com.taskmanager.domain.model.Task;
import com.taskmanager.domain.model.TaskCategory;
import com.taskmanager.domain.model.TaskPriority;
import com.taskmanager.domain.repositories.TaskRepository;
import com.taskmanager.domain.exceptions.TaskNotFoundException;
import com.taskmanager.domain.exceptions.InvalidFieldException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateTaskDetailsUseCase {
    private final TaskRepository repo;
    private final Clock clock;
    private final RescheduleNotificationsUseCase rescheduleNotificationsUseCase;

    public UpdateTaskDetailsUseCase(TaskRepository repo, Clock clock,
                                    RescheduleNotificationsUseCase rescheduleNotificationsUseCase) {
        this.repo = repo;
        this.clock = clock;
        this.rescheduleNotificationsUseCase = rescheduleNotificationsUseCase;
    }

    public Task execute(String title, String description, TaskPriority priority, TaskCategory category, LocalDateTime dueDate, LocalDateTime reminderDate, String id, String requesterId) {
        Task task = repo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.verifyOwnership(requesterId);

        LocalDateTime now = LocalDateTime.now(clock);

        LocalDateTime oldDueDate = task.getDueDate();
        TaskPriority oldPriority = task.getPriority();

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

        Task updatedTask = repo.save(task);

        boolean dueDateChanged = !Objects.equals(oldDueDate, updatedTask.getDueDate());
        boolean priorityChanged = !Objects.equals(oldPriority, updatedTask.getPriority());

        if (dueDateChanged || priorityChanged) {
            rescheduleNotificationsUseCase.execute(updatedTask);
        }

        return updatedTask;
    }
}