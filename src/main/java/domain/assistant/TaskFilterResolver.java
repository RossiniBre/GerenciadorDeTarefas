package domain.assistant;

import application.ListTasksUseCase.TaskFilter;
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

        TaskStatus status = EnumParser.parse(TaskStatus.class, intent.status());
        TaskPriority priority = EnumParser.parse(TaskPriority.class, intent.priority());
        TaskCategory category = EnumParser.parse(TaskCategory.class, intent.category());
        TaskStatus excluded = EnumParser.parse(TaskStatus.class, intent.excludeStatus());

        Set<TaskStatus> excludedStatuses = excluded == null ? Set.of() : EnumSet.of(excluded);

        return new TaskFilter(status, priority, category, excludedStatuses);
    }
}