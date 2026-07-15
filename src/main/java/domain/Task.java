package domain;

import java.util.UUID;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;
    private String id;
    private TaskPriority priority;
    private TaskCategory category;

    private Task(String title, String description, TaskStatus status, String id, TaskPriority priority, TaskCategory category){
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("Título é obrigatório!");
        }
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.priority = priority;
        this.category = category;
    }

    public static Task newTask(String title, String description){
        return new Task(title, description, TaskStatus.PENDING, UUID.randomUUID().toString(), TaskPriority.LOW, TaskCategory.UNCATEGORIZED);
    }

    public static Task rebuiltedTask(String title, String description, TaskStatus status, String id, TaskPriority priority, TaskCategory category){
        return new Task(title, description, status, id, priority, category);
    }

    // getters
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public TaskStatus getStatus(){ return status; }
    public String getId(){ return id; }
    public TaskPriority getPriority(){ return priority; }
    public TaskCategory getCategory(){ return category; }


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

    @Override
    public String toString(){
        return "Task: " + title + " - Status: " + status + " - ID: " + id + " - Priority: " + priority + " - Category: " + category;
    }
}