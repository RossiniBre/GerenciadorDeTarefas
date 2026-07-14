package domain;

import java.util.UUID;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;
    private String id;

    private Task(String title, String description, TaskStatus status, String id){
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("Título é obrigatório!");
        }
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public static Task newTask(String title, String description){
        return new Task(title, description, TaskStatus.PENDING, UUID.randomUUID().toString());
    }

    public static Task rebuiltedTask(String title, String description, TaskStatus status, String id){
        return new Task(title, description, status, id);
    }

    // getters
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public TaskStatus getStatus(){ return status; }
    public String getId(){ return id; }


    // Iniciar/finalizar/Atualizar
    public void startTask(){
        if (status != TaskStatus.PENDING){
            throw new IllegalStateException("Só é possível iniciar uma task pendente!");
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void completeTask(){
        if (status != TaskStatus.IN_PROGRESS){
            throw new IllegalStateException("Só é possível finalizar uma task já iniciada!");
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

    @Override
    public String toString(){
        return "Task: " + title + " - Status: " + status + " - ID: " + id;
    }
}