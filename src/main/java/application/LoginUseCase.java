package application;

import domain.CredentialsValidator;
import domain.PasswordHasher;
import domain.User;
import domain.UserRepository;
import domain.exceptions.InvalidCredentialsException;
import java.util.Optional;

public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUseCase(UserRepository userRepository, PasswordHasher passwordHasher){
        if (userRepository == null){
            throw new IllegalArgumentException("Repositório de usuário é obrigatório!");
        }
        if (passwordHasher == null){
            throw new IllegalArgumentException("Serviço de hash de senha é obrigatório!");
        }
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(String username, String password) {
        // 1
        CredentialsValidator.validate(username, password);

        // 2
        Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = foundUser.get();

        // 3
        boolean correctPassword = passwordHasher.verify(password, user.getPasswordHash());

        // 4
        if (!correctPassword){
            throw new InvalidCredentialsException();
        }

        return user;
    }
}