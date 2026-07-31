package infrastructure.http;

import application.usecases.*;
import com.sun.net.httpserver.HttpServer;
import domain.assistant.*;
import domain.repositories.SessionRepository;
import domain.repositories.TaskRepository;
import domain.repositories.UserRepository;
import domain.security.LoginRateLimiter;
import domain.security.PasswordHasher;
import domain.security.TokenGenerator;
import infrastructure.assistant.*;
import infrastructure.config.AssistantConfig;
import infrastructure.http.actions.*;
import infrastructure.http.json.GsonJsonMapper;
import infrastructure.http.json.JsonMapper;
import infrastructure.persistence.InMemoryLoginRateLimiter;
import infrastructure.persistence.InMemorySessionRepository;
import infrastructure.security.Pbkdf2PasswordHasher;
import infrastructure.security.UuidTokenGenerator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Clock;

public class ApiServer {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ApiServer(
            CreateTaskUseCase createTaskUseCase,
            ListTasksUseCase listTasksUseCase,
            UserRepository userRepository,
            TaskRepository taskRepository
    ) {
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    private static String loadSystemInstructions() {
        try (var input = ApiServer.class.getClassLoader()
                .getResourceAsStream("prompts/task-assistant-system-instructions.txt")) {

            if (input == null) {
                throw new IllegalStateException("Arquivo não encontrado.");
            }

            return new String(
                    input.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar o prompt.", e);
        }
    }

    public void start(int port) throws IOException {

        // 1
        JsonMapper jsonMapper = new GsonJsonMapper();
        AssistantConfig assistantConfig = AssistantConfig.load();
        String systemInstructions = loadSystemInstructions();
        HttpClient httpClient = HttpClient.newHttpClient();

        // 2
        RateLimiter assistantRateLimiter =
                new InMemoryRateLimiter(150, Clock.systemDefaultZone());

        IntentExtractor intentExtractor =
                new RateLimitedIntentExtractor(
                        new AssistantIntentExtractor(
                                httpClient,
                                jsonMapper,
                                assistantConfig.getApiKey()
                        ),
                        assistantRateLimiter
                );

        AnswerFormatter answerFormatter =
                new RateLimitedAnswerFormatter(
                        new AssistantAnswerFormatter(
                                httpClient,
                                jsonMapper,
                                assistantConfig.getApiKey()
                        ),
                        assistantRateLimiter
                );

        TaskFilterResolver taskFilterResolver = new TaskFilterResolver();

        TaskAssistantOrchestrator orchestrator =
                new TaskAssistantOrchestrator(
                        intentExtractor,
                        answerFormatter,
                        taskFilterResolver,
                        listTasksUseCase,
                        jsonMapper,
                        systemInstructions
                );

        // 3
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        PasswordHasher passwordHasher = new Pbkdf2PasswordHasher();
        LoginUseCase loginUseCase = new LoginUseCase(userRepository, passwordHasher);
        RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(userRepository, passwordHasher);

        SessionRepository sessionRepository = new InMemorySessionRepository();
        TokenGenerator tokenGenerator = new UuidTokenGenerator();
        LoginRateLimiter loginRateLimiter = new InMemoryLoginRateLimiter();

        AuthenticateUserUseCase authenticateUserUseCase =
                new AuthenticateUserUseCase(
                        loginUseCase,
                        sessionRepository,
                        tokenGenerator,
                        loginRateLimiter
                );

        // 4
        CreateTaskAction createAction =
                new CreateTaskAction(createTaskUseCase, jsonMapper, userRepository, sessionRepository);

        ListTasksAction listAction =
                new ListTasksAction(listTasksUseCase, jsonMapper, userRepository, sessionRepository);

        UpdateTaskDetailsUseCase updateTaskUseCase =
                new UpdateTaskDetailsUseCase(taskRepository);

        UpdateTaskAction updateAction =
                new UpdateTaskAction(updateTaskUseCase, jsonMapper, userRepository, sessionRepository);

        DeleteTaskUseCase deleteTaskUseCase =
                new DeleteTaskUseCase(taskRepository);

        DeleteTaskAction deleteAction =
                new DeleteTaskAction(deleteTaskUseCase, userRepository, sessionRepository);

        server.createContext(
                "/tasks",
                new TasksHandler(createAction, listAction, updateAction, deleteAction)
        );

        // 5
        RegisterUserAction registerAction =
                new RegisterUserAction(registerUserUseCase, jsonMapper);

        LoginUserAction loginAction =
                new LoginUserAction(authenticateUserUseCase, jsonMapper);

        LogoutUserAction logoutAction =
                new LogoutUserAction(sessionRepository);

        server.createContext(
                "/users",
                new UsersHandler(registerAction, loginAction, logoutAction)
        );

        // 6
        AssistantAction assistantAction =
                new AssistantAction(
                        orchestrator,
                        jsonMapper
                );

        AssistantHandler assistantHandler =
                new AssistantHandler(
                        assistantAction,
                        sessionRepository,
                        userRepository
                );

        server.createContext(
                "/assistant",
                assistantHandler
        );

        // 7
        server.setExecutor(null);
        server.start();

        // 8
        System.out.println("API rodando na porta " + port);
    }
}