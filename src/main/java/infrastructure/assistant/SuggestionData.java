package infrastructure.assistant;

public record SuggestionData(
        String action,
        String targetTaskId,
        String title,
        String description,
        String priority,
        String category,
        String dueDate,
        String reminderDate
) {}