package infrastructure.http.actions;

import application.ListNotificationsUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.exceptions.DomainException;
import domain.exceptions.TaskNotFoundException;
import domain.exceptions.UnauthorizedTaskAccessException;
import domain.model.Task;
import domain.model.User;
import domain.notification.Notification;
import domain.repositories.SessionRepository;
import domain.repositories.TaskRepository;
import domain.repositories.UserRepository;
import infrastructure.http.AuthContext;
import infrastructure.http.dto.ListNotificationsResponse;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;

import java.io.IOException;
import java.util.List;

public class ListNotificationsAction implements HttpHandler {

    private static final String PREFIX = "/tasks/";
    private static final String SUFFIX = "/notifications";

    private final ListNotificationsUseCase listNotificationsUseCase;
    private final TaskRepository taskRepository;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public ListNotificationsAction(ListNotificationsUseCase listNotificationsUseCase, TaskRepository taskRepository,
                                   JsonMapper jsonMapper, UserRepository userRepository, SessionRepository sessionRepository) {
        this.listNotificationsUseCase = listNotificationsUseCase;
        this.taskRepository = taskRepository;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            String taskId = extractTaskId(exchange.getRequestURI().getPath());

            Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
            task.verifyOwnership(user.getId());

            List<Notification> notifications = listNotificationsUseCase.execute(taskId);

            List<ListNotificationsResponse.NotificationItem> items = notifications.stream()
                    .map(ListNotificationsResponse.NotificationItem::from)
                    .toList();

            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(new ListNotificationsResponse(items)));

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

    private String extractTaskId(String path) {
        if (!path.startsWith(PREFIX) || !path.endsWith(SUFFIX)) {
            throw new IllegalArgumentException("Caminho inválido");
        }
        return path.substring(PREFIX.length(), path.length() - SUFFIX.length());
    }
}