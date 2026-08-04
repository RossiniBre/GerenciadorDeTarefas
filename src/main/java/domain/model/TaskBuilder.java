package domain.model;

import java.time.LocalDateTime;

public class TaskBuilder {
    private final String ownerId;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskCategory category;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;

    public TaskBuilder (String ownerId){
        this.ownerId = ownerId;
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder description(String description){
        this.description = description;
        return this;
    }

    public TaskBuilder priority(TaskPriority priority){
        this.priority = priority;
        return this;
    }

    public TaskBuilder category(TaskCategory category){
        this.category = category;
        return this;
    }

    public TaskBuilder withDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder withReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
        return this;
    }

    public Task build() {
        Task task = Task.newTask(title, description, ownerId, dueDate, reminderDate);

        if (priority != null) {
            task.updatePriority(priority);
        }
        if (category != null) {
            task.updateCategory(category);
        }

        return task;
    }
}