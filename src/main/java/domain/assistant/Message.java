package domain.assistant;

public record Message(
        MessageAuthor author,
        String messageText
) {
}
