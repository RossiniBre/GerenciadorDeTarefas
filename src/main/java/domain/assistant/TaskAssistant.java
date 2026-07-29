package domain.assistant;

import java.util.List;

public interface TaskAssistant {
    AssistantResponse process(List<Message> conversationHistory, String requesterId);
}