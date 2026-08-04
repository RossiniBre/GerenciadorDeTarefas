package application;

import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.repositories.TaskRepository;
import domain.model.TaskStatus;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
                .filter(task -> !appliedFilter.excludedStatuses().contains(task.getStatus()))
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .toList();
    }

    public record TaskFilter(
            TaskStatus status,
            TaskPriority priority,
            TaskCategory category,
            Set<TaskStatus> excludedStatuses
    ) {

        public TaskFilter {
            excludedStatuses = excludedStatuses == null ? Set.of() : excludedStatuses;
        }

        public static TaskFilter none() {
            return new TaskFilter(null, null, null, Set.of());
        }

        public static TaskFilter byStatus(TaskStatus status) {
            return new TaskFilter(status, null, null, Set.of());
        }

        public static TaskFilter byPriority(TaskPriority priority) {
            return new TaskFilter(null, priority, null, Set.of());
        }

        public static TaskFilter byCategory(TaskCategory category) {
            return new TaskFilter(null, null, category, Set.of());
        }

        public static TaskFilter excludingStatus(TaskStatus status) {
            return new TaskFilter(null, null, null, EnumSet.of(status));
        }
    }
}