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
            "https://openrouter.ai/api/v1/chat/completions";

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
                "model", "openrouter/free",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        // debug
        System.out.println(jsonMapper.toJson(body));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.toJson(body)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AssistantRequestFailedException(
                        "ASSISTANT retornou status " + response.statusCode() + ": " + response.body());
            }

            return extractTextFromResponse(response.body());

        } catch (java.io.IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssistantRequestFailedException("Falha ao chamar ASSISTANT API", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(String rawJson) {

        Map<String, Object> parsed =
                jsonMapper.fromJson(rawJson, Map.class);

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) parsed.get("choices");

        if (choices == null || choices.isEmpty()) {
            throw new AssistantRequestFailedException(
                    "ASSISTANT não retornou choices na resposta"
            );
        }

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }
}