package infrastructure.http.dto;

public class CreateTaskResponse {
    public String id;
    public String title;
    public String status;
    public String priority;
    public String category;

    public CreateTaskResponse(String id, String title, String status, String priority, String category) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.category = category;
    }
}