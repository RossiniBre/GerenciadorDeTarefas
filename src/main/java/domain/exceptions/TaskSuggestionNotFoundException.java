package domain.exceptions;

public class TaskSuggestionNotFoundException extends DomainException {
    public TaskSuggestionNotFoundException() {
        super("Task suggestion not found or already resolved");
    }
}