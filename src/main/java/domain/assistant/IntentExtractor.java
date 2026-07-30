package domain.assistant;

public interface IntentExtractor {
    String extract(String instructions, String userMessage);
}