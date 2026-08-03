package domain.assistant;

public interface TaskAssistant {
    AssistantResponse process(AssistantContext context);
}