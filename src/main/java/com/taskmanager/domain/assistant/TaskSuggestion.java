package com.taskmanager.domain.assistant;

import com.taskmanager.domain.model.TaskPriority;
import com.taskmanager.domain.model.TaskCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface TaskSuggestion permits
        TaskSuggestion.Create,
        TaskSuggestion.Update,
        TaskSuggestion.Delete,
        TaskSuggestion.Start,
        TaskSuggestion.Complete {

    UUID id();

    record Create(
            UUID id,
            String title,
            String description,
            TaskPriority priority,
            TaskCategory category,
            LocalDateTime dueDate,
            LocalDateTime reminderDate
    ) implements TaskSuggestion {}

    record Update(
            UUID id,
            String targetTaskId,
            String title,
            String description,
            TaskPriority priority,
            TaskCategory category,
            LocalDateTime dueDate,
            LocalDateTime reminderDate
    ) implements TaskSuggestion {}

    record Delete(
            UUID id,
            String targetTaskId
    ) implements TaskSuggestion {}

    record Start(
            UUID id,
            String targetTaskId
    ) implements TaskSuggestion {}

    record Complete(
            UUID id,
            String targetTaskId
    ) implements TaskSuggestion {}
}