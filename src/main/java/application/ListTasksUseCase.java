package application;

import domain.Task;
import domain.TaskCategory;
import domain.TaskPriority;
import domain.TaskRepository;
import domain.TaskStatus;
import java.util.Comparator;
import java.util.List;

public class ListTasksUseCase {

    private final TaskRepository taskRepository;

    public ListTasksUseCase(TaskRepository taskRepository) {
        if (taskRepository == null) {
            throw new IllegalArgumentException("Repositório de tarefas é obrigatório!");
        }
        this.taskRepository = taskRepository;
    }

    public List<Task> execute(String ownerId, TaskFilter filter) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Id do usuário é obrigatório!");
        }

        TaskFilter appliedFilter = filter == null ? TaskFilter.none() : filter;

        return taskRepository.findAllByOwner(ownerId).stream()
                .filter(task -> appliedFilter.status() == null || task.getStatus() == appliedFilter.status())
                .filter(task -> appliedFilter.priority() == null || task.getPriority() == appliedFilter.priority())
                .filter(task -> appliedFilter.category() == null || task.getCategory() == appliedFilter.category())
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .toList();
    }

    public record TaskFilter(TaskStatus status, TaskPriority priority, TaskCategory category) {

        public static TaskFilter none() {
            return new TaskFilter(null, null, null);
        }

        public static TaskFilter byStatus(TaskStatus status) {
            return new TaskFilter(status, null, null);
        }

        public static TaskFilter byPriority(TaskPriority priority) {
            return new TaskFilter(null, priority, null);
        }

        public static TaskFilter byCategory(TaskCategory category) {
            return new TaskFilter(null, null, category);
        }
    }
}