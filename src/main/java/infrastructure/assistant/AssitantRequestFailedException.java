package infrastructure.assistant;

public class AssitantRequestFailedException extends RuntimeException {
    public AssitantRequestFailedException(String message) {
        super(message);
    }

    public AssitantRequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}