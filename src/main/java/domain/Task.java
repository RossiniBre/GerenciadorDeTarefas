package domain;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;

    private Task(String title, String description, TaskStatus status){
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("Título é obrigatório!");
        }
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public static Task newTask(String title, String description){
        return new Task(title, description, TaskStatus.PENDING);
    }

    public static Task rebuiltedTask(String title, String description, TaskStatus status){
        return new Task(title, description, status);
    }

    // getters
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public TaskStatus getStatus(){ return status; }

}