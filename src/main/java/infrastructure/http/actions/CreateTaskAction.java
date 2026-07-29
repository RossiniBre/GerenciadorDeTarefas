package infrastructure.http.actions;

import application.usecases.CreateTaskUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.User;
import domain.exceptions.DomainException;
import domain.repositories.SessionRepository;
import domain.repositories.UserRepository;
import infrastructure.http.AuthContext;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.CreateTaskRequest;
import infrastructure.http.dto.CreateTaskResponse;

import java.io.IOException;

public class CreateTaskAction implements HttpHandler {

    private final CreateTaskUseCase createTaskUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public CreateTaskAction(CreateTaskUseCase createTaskUseCase, JsonMapper jsonMapper,
                            UserRepository userRepository, SessionRepository sessionRepository) {
        this.createTaskUseCase = createTaskUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            String body = HttpJson.readBody(exchange.getRequestBody());
            CreateTaskRequest request = jsonMapper.fromJson(body, CreateTaskRequest.class);

            TaskPriority priority = request.priority != null ? TaskPriority.valueOf(request.priority) : null;
            TaskCategory category = request.category != null ? TaskCategory.valueOf(request.category) : null;

            Task task = createTaskUseCase.execute(request.title, request.description, user, priority, category);

            var response = new CreateTaskResponse(task.getId(), task.getTitle(), task.getStatus().name(), task.getPriority().name(), task.getCategory().name());
            HttpJson.sendResponse(exchange, 201, jsonMapper.toJson(response));

        } catch (domain.exceptions.InvalidCredentialsException e) {
            HttpJson.sendResponse(exchange, 401, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}