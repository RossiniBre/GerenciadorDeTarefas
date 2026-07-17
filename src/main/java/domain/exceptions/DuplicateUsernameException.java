package domain.exceptions;

public class DuplicateUsernameException extends DomainException {

    public DuplicateUsernameException(String username) {
        super("O nome de usuário '" + username + "' já está em uso.");
    }
}