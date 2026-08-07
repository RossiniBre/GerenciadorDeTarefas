package com.taskmanager.application;

import com.taskmanager.domain.security.CredentialsValidator;
import com.taskmanager.domain.security.PasswordHasher;
import com.taskmanager.domain.model.User;
import com.taskmanager.domain.repositories.UserRepository;
import com.taskmanager.domain.exceptions.DuplicateUsernameException;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher){
        if (userRepository == null){
            throw new IllegalArgumentException("Repositório de usuário é obrigatório!");
        }
        if (passwordHasher == null){
            throw new IllegalArgumentException("Serviço de hash de senha é obrigatório!");
        }
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(String username, String password){
        // 1
        CredentialsValidator.validate(username, password);

        // 2
        if (userRepository.findByUsername(username).isPresent()){
            throw new DuplicateUsernameException(username);
        }

        // 3
        String hashedPassword = passwordHasher.hash(password);

        // 4
        User registeredUser = User.newUser(username, hashedPassword);

        // 5
        return userRepository.save(registeredUser);
    }
}