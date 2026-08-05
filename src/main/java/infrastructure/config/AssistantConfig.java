package infrastructure.config;

import domain.exceptions.InvalidFieldException;

public class AssistantConfig {

    public static final String MODEL = "openrouter/free";;
    public static final int DAILY_LIMIT = 50;

    private final String apiKey;

    public AssistantConfig(String apiKey) {
        this.apiKey = apiKey;
    }

    public static AssistantConfig load() {
        String apiKey = System.getenv("ASSISTANT_API_KEY");
        validateNotNull(apiKey);
        return new AssistantConfig(apiKey);
    }

    private static void validateNotNull(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException("ASSISTANT_API_KEY não configurado");
        }
    }

    public String getApiKey() {
        return apiKey;
    }
}