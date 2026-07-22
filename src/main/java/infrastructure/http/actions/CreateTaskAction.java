package infrastructure.http.actions;

import application.CreateTaskUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.*;
import domain.exceptions.DomainException;
import domain.exceptions.InvalidCredentialsException;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.CreateTaskRequest;
import infrastructure.http.dto.CreateTaskResponse;

import java.io.IOException;

public class CreateTaskAction implements HttpHandler {

    private final CreateTaskUseCase createTaskUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;

    public CreateTaskAction(CreateTaskUseCase createTaskUseCase, JsonMapper jsonMapper, UserRepository userRepository) {
        this.createTaskUseCase = createTaskUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String body = HttpJson.readBody(exchange.getRequestBody());
            CreateTaskRequest request = jsonMapper.fromJson(body, CreateTaskRequest.class);

            TaskPriority priority = request.priority != null ? TaskPriority.valueOf(request.priority) : null;
            TaskCategory category = request.category != null ? TaskCategory.valueOf(request.category) : null;

            User user = userRepository.findByUsername(request.username)
                    .orElseThrow(InvalidCredentialsException::new);

            Task task = createTaskUseCase.execute(request.title, request.description, user, priority, category);

            var response = new CreateTaskResponse(task.getId(), task.getTitle(), task.getStatus().name(), task.getPriority().name(), task.getCategory().name());
            HttpJson.sendResponse(exchange, 201, jsonMapper.toJson(response));

        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}