package domain.exceptions;

public class UnauthorizedTaskAccessException extends DomainException {

    public UnauthorizedTaskAccessException() {
        super("Você não tem permissão para acessar ou modificar esta tarefa.");
    }
}