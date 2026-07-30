package domain.assistant;

import application.usecases.ListTasksUseCase.TaskFilter;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.TaskStatus;

import java.util.EnumSet;
import java.util.Set;

public class TaskFilterResolver {

    public TaskFilter resolve(TaskFilterIntent intent) {
        if (intent == null) {
            return TaskFilter.none();
        }

        TaskStatus status = parseEnum(TaskStatus.class, intent.status());
        TaskPriority priority = parseEnum(TaskPriority.class, intent.priority());
        TaskCategory category = parseEnum(TaskCategory.class, intent.category());
        TaskStatus excluded = parseEnum(TaskStatus.class, intent.excludeStatus());

        Set<TaskStatus> excludedStatuses = excluded == null ? Set.of() : EnumSet.of(excluded);

        return new TaskFilter(status, priority, category, excludedStatuses);
    }

    private <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}