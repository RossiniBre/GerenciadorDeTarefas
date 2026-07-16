package domain;

import java.util.UUID;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;
    private String id;
    private TaskPriority priority;
    private TaskCategory category;
    private String ownerId;

    private Task(String title, String description, TaskStatus status, String id, TaskPriority priority, TaskCategory category, String ownerId){
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("Título é obrigatório!");
        }
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.priority = priority;
        this.category = category;
        if (ownerId == null || ownerId.isBlank()){
            throw new IllegalArgumentException("Tarefa sem dono!");
        }
        this.ownerId = ownerId;
    }

    public static Task newTask(String title, String description, String ownerId){
        return new Task(title, description, TaskStatus.PENDING, UUID.randomUUID().toString(), TaskPriority.LOW, TaskCategory.UNCATEGORIZED, ownerId);
    }

    public static Task rebuiltedTask(String title, String description, TaskStatus status, String id, TaskPriority priority, TaskCategory category, String ownerId){
        return new Task(title, description, status, id, priority, category, ownerId);
    }

    // getters
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public TaskStatus getStatus(){ return status; }
    public String getId(){ return id; }
    public TaskPriority getPriority(){ return priority; }
    public TaskCategory getCategory(){ return category; }
    public String getOwnerId(){ return ownerId; }


    // Iniciar/finalizar/Atualizar
    public void startTask(){
        if (status != TaskStatus.PENDING){
            throw new IllegalStateException("Só é possível iniciar uma tarefa pendente!");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void completeTask(){
        if (status != TaskStatus.IN_PROGRESS){
            throw new IllegalStateException("Só é possível finalizar uma tarefa já iniciada!");
        }
        this.status = TaskStatus.COMPLETED;
    }

    public void updateDetails(String title, String description){
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("Título é obrigatório!");
        }
        this.title = title;
        this.description = description;
    }

    public void updatePriority(TaskPriority priority) {
        if (priority == null){
            throw new IllegalArgumentException("Escolha uma prioridade!");
        }
        this.priority = priority;
    }
    public void updateCategory(TaskCategory category) {
        if (category == null){
            throw new IllegalArgumentException("Escolha uma categoria!");
        }
        this.category = category;
    }

    public void verifyOwnership(String requesterId) {
        if (!this.ownerId.equals(requesterId)) {
            throw new IllegalStateException("Você não tem permissão para modificar esta tarefa!");
        }
    }

    @Override
    public String toString(){
        return "Task: " + title + " - Status: " + status + " - ID: " + id + " - Priority: " + priority + " - Category: " + category + " - OwnerId: " + ownerId;
    }
}