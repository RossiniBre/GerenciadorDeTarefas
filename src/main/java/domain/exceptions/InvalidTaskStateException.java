package domain.exceptions;

public class InvalidTaskStateException extends DomainException {

    public InvalidTaskStateException(String message) {
        super(message);
    }
}