package infrastructure.http.dto;

import java.util.Map;

public class ListTasksRequest {
    public String status;
    public String priority;
    public String category;

    public static ListTasksRequest fromQuery(Map<String, String> params) {
        ListTasksRequest request = new ListTasksRequest();
        request.status = params.get("status");
        request.priority = params.get("priority");
        request.category = params.get("category");
        return request;
    }
}