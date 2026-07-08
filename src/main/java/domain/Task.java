package domain;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;

    Task(String title, String description, TaskStatus status){
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("Título é obrigatório!");
        }
        this.title = title; this.description = description; this.status = status;
    }

    // getters
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public TaskStatus getStatus(){ return status; }

}