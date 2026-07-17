package application;

import domain.PasswordHasher;
import domain.User;
import domain.UserRepository;
import infrastructure.InMemoryUserRepository;
import infrastructure.Pbkdf2PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import domain.exceptions.InvalidFieldException;
import domain.exceptions.DuplicateUsernameException;

class RegisterUserUseCaseTest {

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(repo, hasher);

        // Act
        User registeredUser = useCase.execute("joao123", "minhaSenha123");

        // Assert
        assertNotNull(registeredUser.getId());
        assertEquals("joao123", registeredUser.getUsername());
        assertNotEquals("minhaSenha123", registeredUser.getPasswordHash());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        // Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(repo, hasher);

        // Act & Assert
        assertThrows(InvalidFieldException.class, () ->
                useCase.execute("", "minhaSenha123"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(repo, hasher);

        // Act & Assert
        assertThrows(InvalidFieldException.class, () ->
                useCase.execute("joao123", ""));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(repo, hasher);
        useCase.execute("joao123", "minhaSenha123");

        // Act & Assert
        assertThrows(DuplicateUsernameException.class, () ->
                useCase.execute("joao123", "outraSenha456"));
    }
}