package domain.assistant;

import application.usecases.ListTasksUseCase.TaskFilter;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.TaskStatus;

import java.util.Set;

public class TaskFilterParser {

    public TaskFilter parse(String text) {
        if (text == null || text.isBlank()) {
            return TaskFilter.none();
        }

        String normalized = normalize(text);

        return new TaskFilter(
                extractStatus(normalized),
                extractPriority(normalized),
                extractCategory(normalized),
                Set.of()
        );
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[áàãâ]", "a")
                .replaceAll("[éê]", "e")
                .replaceAll("[íî]", "i")
                .replaceAll("[óõô]", "o")
                .replaceAll("[úû]", "u")
                .replaceAll("ç", "c");
    }

    private TaskPriority extractPriority(String text) {
        if (text.contains("alta") || text.contains("urgente")) return TaskPriority.HIGH;
        if (text.contains("media")) return TaskPriority.MEDIUM;
        if (text.contains("baixa")) return TaskPriority.LOW;
        return null;
    }

    private TaskStatus extractStatus(String text) {
        if (text.contains("concluida") || text.contains("finalizada")) return TaskStatus.COMPLETED;
        if (text.contains("andamento") || text.contains("progresso")) return TaskStatus.IN_PROGRESS;
        if (text.contains("pendente")) return TaskStatus.PENDING;
        return null;
    }

    private TaskCategory extractCategory(String text) {
        for (TaskCategory category : TaskCategory.values()) {
            if (text.contains(category.name().toLowerCase())) return category;
        }
        return null;
    }
}