package domain.assistant;

import java.util.List;

public interface TaskCreationAssistant {
    AssistantResponse process(List<Message> conversationHistory);
}
