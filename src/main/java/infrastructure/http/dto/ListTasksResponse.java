package infrastructure.http.dto;

import java.util.List;

public class ListTasksResponse {
    public List<TaskItem> tasks;
    public int count;

    public ListTasksResponse(List<TaskItem> tasks) {
        this.tasks = tasks;
        this.count = tasks.size();
    }

    public static class TaskItem {
        public String id;
        public String title;
        public String status;
        public String priority;
        public String category;

        public TaskItem(String id, String title, String status, String priority, String category) {
            this.id = id;
            this.title = title;
            this.status = status;
            this.priority = priority;
            this.category = category;
        }
    }
}