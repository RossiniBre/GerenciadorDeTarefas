package application;

import domain.PasswordHasher;
import domain.User;
import domain.UserRepository;
import domain.exceptions.InvalidCredentialsException;
import infrastructure.InMemoryUserRepository;
import infrastructure.Pbkdf2PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginUseCaseTest {

    @Test
    void shouldLoginSuccessfully(){
        //Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        LoginUseCase loginUseCase = new LoginUseCase(repo, hasher);
        RegisterUserUseCase registerUseCase = new RegisterUserUseCase(repo, hasher);

        //Act
        User registeredUser = registerUseCase.execute("joao123", "minhaSenha123");
        User loginUser = loginUseCase.execute("joao123", "minhaSenha123");

        // Assert
        assertNotNull(loginUser);
        assertEquals(registeredUser.getUsername(), loginUser.getUsername());
        assertEquals(registeredUser.getPasswordHash(), loginUser.getPasswordHash());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){
        // Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        LoginUseCase loginUseCase = new LoginUseCase(repo, hasher);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () ->
                loginUseCase.execute("naoExiste", "qualquerSenha"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect(){
        // Arrange
        UserRepository repo = new InMemoryUserRepository();
        PasswordHasher hasher = new Pbkdf2PasswordHasher();
        LoginUseCase loginUseCase = new LoginUseCase(repo, hasher);
        RegisterUserUseCase registerUseCase = new RegisterUserUseCase(repo, hasher);
        registerUseCase.execute("joao123", "minhaSenha123");

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () ->
                loginUseCase.execute("joao123", "senhaErrada"));
    }
}