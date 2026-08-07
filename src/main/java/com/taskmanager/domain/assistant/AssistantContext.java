package com.taskmanager.domain.assistant;

import java.util.List;

public record AssistantContext(
        List<Message> conversationHistory,
        List<TaskSuggestion> pendingSuggestions,
        String requesterId
) {
}