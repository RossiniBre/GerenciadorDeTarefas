package domain.assistant;

import application.ListTasksUseCase.TaskFilter;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public class TaskFilterResolver {

    public TaskFilter resolve(TaskFilterIntent intent) {
        if (intent == null) {
            return TaskFilter.none();
        }

        TaskStatus status = EnumParser.parse(TaskStatus.class, intent.status());
        TaskPriority priority = EnumParser.parse(TaskPriority.class, intent.priority());
        TaskCategory category = EnumParser.parse(TaskCategory.class, intent.category());
        TaskStatus excluded = EnumParser.parse(TaskStatus.class, intent.excludeStatus());

        Set<TaskStatus> excludedStatuses = excluded == null ? Set.of() : EnumSet.of(excluded);

        LocalDateTime dueDateFrom = parseDateOrNull(intent.dueDateFrom());
        LocalDateTime dueDateTo = parseDateOrNull(intent.dueDateTo());

        return new TaskFilter(status, priority, category, excludedStatuses, dueDateFrom, dueDateTo);
    }

    private LocalDateTime parseDateOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("Data de filtro inválida retornada pela IA, ignorando: " + value);
            return null;
        }
    }
}