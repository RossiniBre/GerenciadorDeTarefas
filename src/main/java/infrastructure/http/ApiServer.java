package infrastructure.http;

import application.CreateTaskUseCase;
import application.DeleteTaskUseCase;
import application.ListTasksUseCase;
import application.UpdateTaskDetailsUseCase;
import com.sun.net.httpserver.HttpServer;
import domain.TaskRepository;
import domain.UserRepository;
import infrastructure.http.actions.CreateTaskAction;
import infrastructure.http.actions.DeleteTaskAction;
import infrastructure.http.actions.ListTasksAction;
import infrastructure.http.actions.UpdateTaskAction;
import infrastructure.http.json.GsonJsonMapper;
import infrastructure.http.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {
    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ApiServer(CreateTaskUseCase createTaskUseCase, ListTasksUseCase listTasksUseCase,
                     UserRepository userRepository, TaskRepository taskRepository) {
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
        CreateTaskAction createAction = new CreateTaskAction(createTaskUseCase, jsonMapper, userRepository);
        ListTasksAction listAction = new ListTasksAction(listTasksUseCase, jsonMapper, userRepository);
        UpdateTaskDetailsUseCase updateTaskUseCase = new UpdateTaskDetailsUseCase(taskRepository);
        UpdateTaskAction updateAction = new UpdateTaskAction(updateTaskUseCase, jsonMapper, userRepository);
        DeleteTaskUseCase deleteTaskUseCase = new DeleteTaskUseCase(taskRepository);
        DeleteTaskAction deleteAction = new DeleteTaskAction(deleteTaskUseCase, userRepository);
        server.createContext("/tasks", new TasksHandler(createAction, listAction, updateAction, deleteAction));
        // 4
        server.setExecutor(null);
        // 5
        server.start();
        // 6
        System.out.println("API rodando na porta " + port);
    }
}