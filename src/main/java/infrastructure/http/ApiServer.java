package infrastructure.http;

import application.usecases.*;
import com.sun.net.httpserver.HttpServer;
import domain.repositories.SessionRepository;
import domain.repositories.TaskRepository;
import domain.repositories.UserRepository;
import domain.security.LoginRateLimiter;
import domain.security.PasswordHasher;
import domain.security.TokenGenerator;
import infrastructure.persistence.InMemoryLoginRateLimiter;
import infrastructure.persistence.InMemorySessionRepository;
import infrastructure.security.Pbkdf2PasswordHasher;
import infrastructure.security.UuidTokenGenerator;
import infrastructure.http.actions.*;
import infrastructure.http.json.GsonJsonMapper;
import infrastructure.http.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {
    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ApiServer(CreateTaskUseCase createTaskUseCase, ListTasksUseCase listTasksUseCase, UserRepository userRepository, TaskRepository taskRepository) {
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public void start(int port) throws IOException {
        // 1
        JsonMapper jsonMapper = new GsonJsonMapper();

        // 2
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // 3
        PasswordHasher passwordHasher = new Pbkdf2PasswordHasher();
        LoginUseCase loginUseCase = new LoginUseCase(userRepository, passwordHasher);
        RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(userRepository, passwordHasher);

        SessionRepository sessionRepository = new InMemorySessionRepository();
        TokenGenerator tokenGenerator = new UuidTokenGenerator();
        LoginRateLimiter rateLimiter = new InMemoryLoginRateLimiter();

        AuthenticateUserUseCase authenticateUserUseCase =
                new AuthenticateUserUseCase(loginUseCase, sessionRepository, tokenGenerator, rateLimiter);

        // 4 - tasks
        CreateTaskAction createAction = new CreateTaskAction(createTaskUseCase, jsonMapper, userRepository, sessionRepository);
        ListTasksAction listAction = new ListTasksAction(listTasksUseCase, jsonMapper, userRepository, sessionRepository);
        UpdateTaskDetailsUseCase updateTaskUseCase = new UpdateTaskDetailsUseCase(taskRepository);
        UpdateTaskAction updateAction = new UpdateTaskAction(updateTaskUseCase, jsonMapper, userRepository, sessionRepository);
        DeleteTaskUseCase deleteTaskUseCase = new DeleteTaskUseCase(taskRepository);
        DeleteTaskAction deleteAction = new DeleteTaskAction(deleteTaskUseCase, userRepository, sessionRepository);
        server.createContext("/tasks", new TasksHandler(createAction, listAction, updateAction, deleteAction));

        // 5 - users
        RegisterUserAction registerAction = new RegisterUserAction(registerUserUseCase, jsonMapper);
        LoginUserAction loginAction = new LoginUserAction(authenticateUserUseCase, jsonMapper);
        LogoutUserAction logoutAction = new LogoutUserAction(sessionRepository);
        server.createContext("/users", new UsersHandler(registerAction, loginAction, logoutAction));

        // 6
        server.setExecutor(null);

        // 7
        server.start();

        // 8
        System.out.println("API rodando na porta " + port);
    }
}