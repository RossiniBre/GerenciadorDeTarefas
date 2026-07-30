package domain.assistant;

public record TaskFilterIntent(
        String status,
        String priority,
        String category,
        String excludeStatus
) {}