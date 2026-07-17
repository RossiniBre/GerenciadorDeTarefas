package domain.exceptions;

public class TaskNotFoundException extends DomainException {

    public TaskNotFoundException(String taskId) {
        super("Tarefa com id '" + taskId + "' não encontrada.");
    }
}