package infrastructure.http.actions;

import application.DeleteTaskUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.SessionRepository;
import domain.User;
import domain.UserRepository;
import domain.exceptions.DomainException;
import domain.exceptions.UnauthorizedTaskAccessException;
import infrastructure.http.AuthContext;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class DeleteTaskAction implements HttpHandler {

    private final DeleteTaskUseCase deleteTaskUseCase;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public DeleteTaskAction(DeleteTaskUseCase deleteTaskUseCase, UserRepository userRepository, SessionRepository sessionRepository) {
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            String id = HttpJson.extractedId(exchange.getRequestURI().getPath(), "/tasks/");

            deleteTaskUseCase.execute(id, user.getId());

            HttpJson.sendResponse(exchange, 204, "");

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