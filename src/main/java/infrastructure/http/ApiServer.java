package infrastructure.http;

import application.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import domain.assistant.*;
import domain.repositories.AssistantSessionRepository;
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
import infrastructure.persistence.RedisAssistantSessionRepository;
import infrastructure.persistence.TaskSuggestionAdapter;
import infrastructure.security.Pbkdf2PasswordHasher;
import infrastructure.security.UuidTokenGenerator;
import redis.clients.jedis.JedisPool;

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

    private static String loadInstructions(String resourcePath) {
        try (var input = ApiServer.class.getClassLoader().getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IllegalStateException("Arquivo não encontrado: " + resourcePath);
            }

            return new String(
                    input.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar o prompt: " + resourcePath, e);
        }
    }

    public void start(int port) throws IOException {

        // 1
        JsonMapper jsonMapper = new GsonJsonMapper();
        AssistantConfig assistantConfig = AssistantConfig.load();
        String systemInstructions = loadInstructions("prompts/task-assistant-system-instructions.txt");
        String answerFormatterInstructions = loadInstructions("prompts/task-assistant-answer-formatter-instructions.txt");
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
                        systemInstructions,
                        answerFormatterInstructions,
                        Clock.systemDefaultZone()
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
                new UpdateTaskDetailsUseCase(taskRepository, Clock.systemDefaultZone());

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
        Gson assistantGson = new GsonBuilder()
                .registerTypeAdapter(TaskSuggestion.class, new TaskSuggestionAdapter())
                .create();

        JedisPool jedisPool = new JedisPool("localhost", 6379);
        AssistantSessionRepository assistantSessionRepository =
                new RedisAssistantSessionRepository(jedisPool, assistantGson);

        SendMessageToAssistantUseCase sendMessageToAssistantUseCase =
                new SendMessageToAssistantUseCase(
                        assistantSessionRepository,
                        orchestrator,
                        listTasksUseCase
                );

        StartTaskUseCase startTaskUseCase = new StartTaskUseCase(taskRepository);
        CompleteTaskUseCase completeTaskUseCase = new CompleteTaskUseCase(taskRepository);

        ConfirmTaskSuggestionUseCase confirmTaskSuggestionUseCase =
                new ConfirmTaskSuggestionUseCase(
                        assistantSessionRepository,
                        createTaskUseCase,
                        updateTaskUseCase,
                        deleteTaskUseCase,
                        startTaskUseCase,
                        completeTaskUseCase
                );

        RejectTaskSuggestionUseCase rejectTaskSuggestionUseCase =
                new RejectTaskSuggestionUseCase(assistantSessionRepository);

        SendMessageAssistantAction sendMessageAssistantAction =
                new SendMessageAssistantAction(
                        sendMessageToAssistantUseCase,
                        jsonMapper,
                        userRepository,
                        sessionRepository
                );

        ConfirmSuggestionAction confirmSuggestionAction =
                new ConfirmSuggestionAction(
                        confirmTaskSuggestionUseCase,
                        jsonMapper,
                        userRepository,
                        sessionRepository
                );

        RejectSuggestionAction rejectSuggestionAction =
                new RejectSuggestionAction(
                        rejectTaskSuggestionUseCase,
                        jsonMapper,
                        userRepository,
                        sessionRepository
                );

        AssistantHandler assistantHandler =
                new AssistantHandler(
                        sendMessageAssistantAction,
                        confirmSuggestionAction,
                        rejectSuggestionAction
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