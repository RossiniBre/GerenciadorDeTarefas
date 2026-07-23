package infrastructure.http.dto;

public class ListTasksRequest {
    public String username;
    public String status;
    public String priority;
    public String category;

    public ListTasksRequest(String username, String status, String priority, String category) {
        this.username = username;
        this.status = status;
        this.priority = priority;
        this.category = category;
    }

    public static ListTasksRequest fromQuery(java.util.Map<String, String> params) {
        return new ListTasksRequest(
                params.get("username"),
                params.get("status"),
                params.get("priority"),
                params.get("category")
        );
    }
}