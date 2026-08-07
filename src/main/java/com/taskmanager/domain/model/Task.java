package com.taskmanager.domain.model;

import com.taskmanager.domain.exceptions.InvalidFieldException;
import com.taskmanager.domain.exceptions.InvalidTaskStateException;
import com.taskmanager.domain.exceptions.UnauthorizedTaskAccessException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;
    private String id;
    private TaskPriority priority;
    private TaskCategory category;
    private String ownerId;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;

    private Task(String title, String description, TaskStatus status, String id, TaskPriority priority, TaskCategory category, String ownerId, LocalDateTime dueDate, LocalDateTime reminderDate){
        if (title == null || title.isBlank()){
            throw new InvalidFieldException("Título é obrigatório!");
        }
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.priority = priority;
        this.category = category;
        if (ownerId == null || ownerId.isBlank()){
            throw new InvalidFieldException("Tarefa sem dono!");
        }
        this.ownerId = ownerId;
        if (dueDate != null && reminderDate != null && reminderDate.isAfter(dueDate)) {
            throw new InvalidFieldException("reminderDate não pode ser depois de dueDate");
        }
        this.dueDate = dueDate;
        this.reminderDate = reminderDate;
    }

    public static Task newTask(String title, String description, String ownerId, LocalDateTime dueDate, LocalDateTime reminderDate){
        return new Task(title, description, TaskStatus.PENDING, UUID.randomUUID().toString(), TaskPriority.LOW, TaskCategory.UNCATEGORIZED, ownerId, dueDate, reminderDate);
    }

    public static Task rebuiltTask(String title, String description, TaskStatus status, String id, TaskPriority priority, TaskCategory category, String ownerId, LocalDateTime dueDate, LocalDateTime reminderDate){
        return new Task(title, description, status, id, priority, category, ownerId, dueDate, reminderDate);
    }

    // getters
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public TaskStatus getStatus(){ return status; }
    public String getId(){ return id; }
    public TaskPriority getPriority(){ return priority; }
    public TaskCategory getCategory(){ return category; }
    public String getOwnerId(){ return ownerId; }
    public LocalDateTime getDueDate(){ return dueDate; }
    public LocalDateTime getReminderDate(){ return reminderDate; }


    // Iniciar/finalizar/Atualizar
    public void startTask(){
        if (status != TaskStatus.PENDING){
            throw new InvalidTaskStateException("Só é possível iniciar uma tarefa pendente!");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void completeTask(){
        if (status != TaskStatus.IN_PROGRESS){
            throw new InvalidTaskStateException("Só é possível finalizar uma tarefa já iniciada!");
        }
        this.status = TaskStatus.COMPLETED;
    }

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidFieldException("title");
        }
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePriority(TaskPriority priority) {
        if (priority == null){
            throw new InvalidFieldException("Escolha uma prioridade!");
        }
        this.priority = priority;
    }
    public void updateCategory(TaskCategory category) {
        if (category == null){
            throw new InvalidFieldException("Escolha uma categoria!");
        }
        this.category = category;
    }

    public void updateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && reminderDate != null && reminderDate.isAfter(dueDate)) {
            throw new InvalidFieldException("reminderDate não pode ser depois de dueDate");
        }
        this.dueDate = dueDate;
    }

    public void updateReminderDate(LocalDateTime reminderDate) {
        if (reminderDate != null && dueDate != null && reminderDate.isAfter(dueDate)) {
            throw new InvalidFieldException("reminderDate não pode ser depois de dueDate");
        }
        this.reminderDate = reminderDate;
    }

    public void verifyOwnership(String requesterId) {
        if (!this.ownerId.equals(requesterId)) {
            throw new UnauthorizedTaskAccessException();
        }
    }

    @Override
    public String toString(){
        return "Task: " + title + " - Status: " + status + " - ID: " + id + " - Priority: " + priority + " - Category: " + category + " - OwnerId: " + ownerId;
    }
}