package infrastructure.http.dto;

public record MessageDto(
        String author,
        String content
) {
}