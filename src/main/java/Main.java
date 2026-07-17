import application.RegisterUserUseCase;
import application.LoginUseCase;
import application.CreateTaskUseCase;
import domain.*;
import infrastructure.InMemoryTaskRepository;
import infrastructure.InMemoryUserRepository;
import infrastructure.Pbkdf2PasswordHasher;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskRepository repo = new InMemoryTaskRepository();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repo);

        UserRepository userRepository = new InMemoryUserRepository();
        PasswordHasher passwordHasher = new Pbkdf2PasswordHasher();

        RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(userRepository, passwordHasher);
        LoginUseCase loginUseCase = new LoginUseCase(userRepository, passwordHasher);
        
        registerUserUseCase.execute("joao123", "minhaSenha123");
        System.out.println("Usuário registrado com sucesso.");

        User loggedUser = loginUseCase.execute("joao123", "minhaSenha123");
        System.out.println("Login bem-sucedido: " + loggedUser.getUsername());

        // Login com senha errada (caminho de erro)
        try {
            loginUseCase.execute("joao123", "senhaErrada");
        } catch (IllegalArgumentException e) {
            System.out.println("Falha esperada no login: " + e.getMessage());
        }

        Task t = useCase.execute("Estudar Java", "Revisar Fase 4", loggedUser);

        System.out.println(t);
        List<Task> tasksDoUser = repo.findAllByOwner(loggedUser.getId());
        for (Task task : tasksDoUser) {
            System.out.println("- " + task.getTitle());
        }
    }
}