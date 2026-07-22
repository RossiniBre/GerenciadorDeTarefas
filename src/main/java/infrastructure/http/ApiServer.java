package infrastructure.http;

import application.CreateTaskUseCase;
import application.ListTasksUseCase;
import com.sun.net.httpserver.HttpServer;
import domain.UserRepository;
import infrastructure.http.actions.CreateTaskAction;
import infrastructure.http.actions.ListTasksAction;
import infrastructure.http.json.GsonJsonMapper;
import infrastructure.http.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {
    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UserRepository userRepository;

    public ApiServer(CreateTaskUseCase createTaskUseCase, ListTasksUseCase listTasksUseCase, UserRepository userRepository) {
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.userRepository = userRepository;
    }

    public void start(int port) throws IOException {
        // 1
        JsonMapper jsonMapper = new GsonJsonMapper();
        // 2
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // 3
        CreateTaskAction createAction = new CreateTaskAction(createTaskUseCase, jsonMapper, userRepository);
        ListTasksAction listAction = new ListTasksAction(listTasksUseCase, jsonMapper, userRepository);
        server.createContext("/tasks", new TasksHandler(createAction, listAction));
        // 4
        server.setExecutor(null);
        // 5
        server.start();
        // 6
        System.out.println("API rodando na porta " + port);
    }
}