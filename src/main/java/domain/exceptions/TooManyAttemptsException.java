package domain.exceptions;

public class TooManyAttemptsException extends DomainException {
    public TooManyAttemptsException(String username) {
        super("Muitas tentativas de login para o usuário '" + username + "'. Tente novamente mais tarde.");
    }
}