package domain.assistant;

import domain.model.TaskCategory;
import domain.model.TaskPriority;

public record TaskSuggestion(
        String title,
        String description,
        TaskCategory category,
        TaskPriority priority
) {
}