package domain.assistant;

import java.util.List;
import java.util.UUID;

public record Message(
        MessageAuthor author,
        String content,
        List<UUID> suggestionIds
) {

    public Message(MessageAuthor author, String content) {
        this(author, content, List.of());
    }
}