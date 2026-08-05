package infrastructure.http.actions;

import application.ListTasksUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.model.*;
import domain.exceptions.DomainException;
import domain.repositories.SessionRepository;
import domain.repositories.UserRepository;
import infrastructure.http.AuthContext;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.ListTasksRequest;
import infrastructure.http.dto.ListTasksResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListTasksAction implements HttpHandler {

    private final ListTasksUseCase listTasksUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public ListTasksAction(ListTasksUseCase listTasksUseCase, JsonMapper jsonMapper,
                           UserRepository userRepository, SessionRepository sessionRepository) {
        this.listTasksUseCase = listTasksUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            Map<String, String> params = HttpJson.parseQuery(exchange.getRequestURI().getQuery());
            ListTasksRequest request = ListTasksRequest.fromQuery(params);

            TaskStatus status = request.status != null ? TaskStatus.valueOf(request.status) : null;
            TaskPriority priority = request.priority != null ? TaskPriority.valueOf(request.priority) : null;
            TaskCategory category = request.category != null ? TaskCategory.valueOf(request.category) : null;

            ListTasksUseCase.TaskFilter filter = (status == null && priority == null && category == null)
                    ? null
                    : new ListTasksUseCase.TaskFilter(status, priority, category, Set.of(), null, null);

            List<Task> tasks = listTasksUseCase.execute(user.getId(), filter);

            List<ListTasksResponse.TaskItem> items = tasks.stream()
                    .map(t -> new ListTasksResponse.TaskItem(t.getId(), t.getTitle(), t.getStatus().name(), t.getPriority().name(), t.getCategory().name()))
                    .toList();

            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(items));

            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(new ListTasksResponse(items)));

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