package infrastructure.http.actions;

import application.UpdateTaskDetailsUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.*;
import domain.exceptions.DomainException;
import domain.exceptions.InvalidCredentialsException;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.UpdateTaskRequest;
import infrastructure.http.dto.UpdateTaskResponse;

import java.io.IOException;

public class UpdateTaskAction implements HttpHandler {

    private final UpdateTaskDetailsUseCase updateTaskUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;

    public UpdateTaskAction(UpdateTaskDetailsUseCase updateTaskUseCase, JsonMapper jsonMapper, UserRepository userRepository) {
        this.updateTaskUseCase = updateTaskUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String id = HttpJson.extractedId(exchange.getRequestURI().getPath(), "/tasks/");
            String body = HttpJson.readBody(exchange.getRequestBody());
            UpdateTaskRequest request = jsonMapper.fromJson(body, UpdateTaskRequest.class);

            User user = userRepository.findByUsername(request.username).orElseThrow(InvalidCredentialsException::new);

            TaskPriority priority = request.priority != null ? TaskPriority.valueOf(request.priority) : null;
            TaskCategory category = request.category != null ? TaskCategory.valueOf(request.category) : null;

            Task task = updateTaskUseCase.execute(request.title, request.description, priority, category, id, user.getId());

            var response = new UpdateTaskResponse(task.getId(), task.getTitle(), task.getStatus().name(), task.getPriority().name(), task.getCategory().name());
            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(response));

        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}