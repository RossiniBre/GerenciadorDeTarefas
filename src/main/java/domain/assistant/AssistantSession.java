package domain.assistant;

import java.util.List;

public record AssistantSession(
        List<Message> conversationHistory,
        List<TaskSuggestion> pendingSuggestions
) {
}