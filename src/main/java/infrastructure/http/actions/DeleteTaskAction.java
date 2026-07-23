package infrastructure.http.actions;

import application.DeleteTaskUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.User;
import domain.UserRepository;
import domain.exceptions.DomainException;
import domain.exceptions.InvalidCredentialsException;
import infrastructure.http.json.HttpJson;

import java.io.IOException;
import java.util.Map;

public class DeleteTaskAction implements HttpHandler {

    private final DeleteTaskUseCase deleteTaskUseCase;
    private final UserRepository userRepository;

    public DeleteTaskAction(DeleteTaskUseCase deleteTaskUseCase, UserRepository userRepository) {
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String id = HttpJson.extractedId(exchange.getRequestURI().getPath(), "/tasks/");

            Map<String, String> params = HttpJson.parseQuery(exchange.getRequestURI().getQuery());
            String username = params.get("username");
            if (username == null || username.isBlank()) {
                HttpJson.sendResponse(exchange, 400, "{\"error\":\"Parâmetro 'username' é obrigatório (auth temporária)\"}");
                return;
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(InvalidCredentialsException::new);

            deleteTaskUseCase.execute(id, user.getId());

            HttpJson.sendResponse(exchange, 204, "");

        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}