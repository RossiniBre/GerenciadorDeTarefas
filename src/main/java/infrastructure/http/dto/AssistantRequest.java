package infrastructure.http.dto;

import java.util.List;

public record AssistantRequest(
        String requesterId,
        List<MessageDto> conversationHistory
) {
}