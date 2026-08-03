package infrastructure.http.actions;

import application.usecases.ConfirmTaskSuggestionUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.exceptions.DomainException;
import domain.exceptions.TaskSuggestionNotFoundException;
import domain.model.User;
import domain.repositories.SessionRepository;
import domain.repositories.UserRepository;
import infrastructure.http.AuthContext;
import infrastructure.http.dto.SuggestionIdRequest;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;

import java.io.IOException;
import java.util.UUID;

public class ConfirmSuggestionAction implements HttpHandler {

    private final ConfirmTaskSuggestionUseCase confirmTaskSuggestionUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public ConfirmSuggestionAction(
            ConfirmTaskSuggestionUseCase confirmTaskSuggestionUseCase,
            JsonMapper jsonMapper,
            UserRepository userRepository,
            SessionRepository sessionRepository
    ) {
        this.confirmTaskSuggestionUseCase = confirmTaskSuggestionUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            String body = HttpJson.readBody(exchange.getRequestBody());
            SuggestionIdRequest request = jsonMapper.fromJson(body, SuggestionIdRequest.class);
            UUID suggestionId = UUID.fromString(request.suggestionId());

            confirmTaskSuggestionUseCase.execute(user.getId(), user, suggestionId);

            HttpJson.sendResponse(exchange, 200, "{\"status\":\"confirmed\"}");

        } catch (TaskSuggestionNotFoundException e) {
            HttpJson.sendResponse(exchange, 404, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
        e.printStackTrace();
        HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
    }
    }
}