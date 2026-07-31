package infrastructure.assistant;

import domain.assistant.IntentExtractor;
import infrastructure.http.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class AssistantIntentExtractor implements IntentExtractor {

    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final String apiKey;

    public AssistantIntentExtractor(HttpClient httpClient, JsonMapper jsonMapper, String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "ASSISTANT_API_KEY não configurada. Defina a variável de ambiente antes de iniciar a aplicação.");
        }
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
        this.apiKey = apiKey;
    }

    @Override
    public String extract(String instructions, String userMessage) {
        String prompt = instructions + "\n\nMensagem do usuário: " + userMessage;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(body)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AssitantRequestFailedException(
                        "ASSISTANT retornou status " + response.statusCode() + ": " + response.body());
            }

            return extractTextFromResponse(response.body());

        } catch (java.io.IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssitantRequestFailedException("Falha ao chamar ASSISTANT API", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(String rawJson) {
        Map<String, Object> parsed = jsonMapper.fromJson(rawJson, Map.class);
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) parsed.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new AssitantRequestFailedException("ASSISTANT não retornou candidates na resposta");
        }

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        return (String) parts.get(0).get("text");
    }
}