package infrastructure.http.actions;

import application.UpdateTaskDetailsUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.User;
import domain.exceptions.DomainException;
import domain.exceptions.UnauthorizedTaskAccessException;
import domain.repositories.SessionRepository;
import domain.repositories.UserRepository;
import infrastructure.http.AuthContext;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.UpdateTaskRequest;
import infrastructure.http.dto.UpdateTaskResponse;

import java.io.IOException;

public class UpdateTaskAction implements HttpHandler {

    private final UpdateTaskDetailsUseCase updateTaskUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UpdateTaskAction(UpdateTaskDetailsUseCase updateTaskUseCase, JsonMapper jsonMapper,
                            UserRepository userRepository, SessionRepository sessionRepository) {
        this.updateTaskUseCase = updateTaskUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            String id = HttpJson.extractedId(exchange.getRequestURI().getPath(), "/tasks/");
            String body = HttpJson.readBody(exchange.getRequestBody());
            UpdateTaskRequest request = jsonMapper.fromJson(body, UpdateTaskRequest.class);

            TaskPriority priority = request.priority != null ? TaskPriority.valueOf(request.priority) : null;
            TaskCategory category = request.category != null ? TaskCategory.valueOf(request.category) : null;

            Task task = updateTaskUseCase.execute(request.title, request.description, priority, category, id, user.getId());

            var response = new UpdateTaskResponse(task.getId(), task.getTitle(), task.getStatus().name(), task.getPriority().name(), task.getCategory().name());
            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(response));

        } catch (domain.exceptions.InvalidCredentialsException e) {
            HttpJson.sendResponse(exchange, 401, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (UnauthorizedTaskAccessException e) {
            HttpJson.sendResponse(exchange, 403, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}