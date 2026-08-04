package infrastructure.http.actions;

import application.RejectTaskSuggestionUseCase;
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

public class RejectSuggestionAction implements HttpHandler {

    private final RejectTaskSuggestionUseCase rejectTaskSuggestionUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public RejectSuggestionAction(
            RejectTaskSuggestionUseCase rejectTaskSuggestionUseCase,
            JsonMapper jsonMapper,
            UserRepository userRepository,
            SessionRepository sessionRepository
    ) {
        this.rejectTaskSuggestionUseCase = rejectTaskSuggestionUseCase;
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

            rejectTaskSuggestionUseCase.execute(user.getId(), suggestionId);

            HttpJson.sendResponse(exchange, 200, "{\"status\":\"rejected\"}");

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