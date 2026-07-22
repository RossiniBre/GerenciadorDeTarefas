package infrastructure.http.actions;

import application.ListTasksUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.*;
import domain.exceptions.DomainException;
import domain.exceptions.InvalidCredentialsException;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.CreateTaskResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ListTasksAction implements HttpHandler {

    private final ListTasksUseCase listTasksUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;

    public ListTasksAction(ListTasksUseCase listTasksUseCase, JsonMapper jsonMapper, UserRepository userRepository) {
        this.listTasksUseCase = listTasksUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = HttpJson.parseQuery(exchange.getRequestURI().getQuery());

            String username = params.get("username");
            if (username == null || username.isBlank()) {
                HttpJson.sendResponse(exchange, 400, "{\"error\":\"Parâmetro 'username' é obrigatório (auth temporária)\"}");
                return;
            }
            User user = userRepository.findByUsername(username)
                    .orElseThrow(InvalidCredentialsException::new);

            TaskStatus status = params.containsKey("status") ? TaskStatus.valueOf(params.get("status")) : null;
            TaskPriority priority = params.containsKey("priority") ? TaskPriority.valueOf(params.get("priority")) : null;
            TaskCategory category = params.containsKey("category") ? TaskCategory.valueOf(params.get("category")) : null;

            ListTasksUseCase.TaskFilter filter = (status == null && priority == null && category == null)
                    ? null
                    : new ListTasksUseCase.TaskFilter(status, priority, category);

            List<Task> tasks = listTasksUseCase.execute(user.getId(), filter);

            List<CreateTaskResponse> response = tasks.stream()
                    .map(t -> new CreateTaskResponse(t.getId(), t.getTitle(), t.getStatus().name(), t.getPriority().name(), t.getCategory().name()))
                    .toList();

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