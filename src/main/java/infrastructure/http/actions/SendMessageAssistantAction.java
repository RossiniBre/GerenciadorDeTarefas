package infrastructure.http.actions;

import application.usecases.SendMessageToAssistantUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.assistant.AssistantResponse;
import domain.exceptions.DomainException;
import domain.model.User;
import domain.repositories.SessionRepository;
import domain.repositories.UserRepository;
import infrastructure.http.AuthContext;
import infrastructure.http.dto.AssistantRequest;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;

import java.io.IOException;

public class SendMessageAssistantAction implements HttpHandler {

    private final SendMessageToAssistantUseCase sendMessageToAssistantUseCase;
    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public SendMessageAssistantAction(
            SendMessageToAssistantUseCase sendMessageToAssistantUseCase,
            JsonMapper jsonMapper,
            UserRepository userRepository,
            SessionRepository sessionRepository
    ) {
        this.sendMessageToAssistantUseCase = sendMessageToAssistantUseCase;
        this.jsonMapper = jsonMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User user = AuthContext.requireUser(exchange, sessionRepository, userRepository);

            String body = HttpJson.readBody(exchange.getRequestBody());
            AssistantRequest request = jsonMapper.fromJson(body, AssistantRequest.class);

            AssistantResponse response = sendMessageToAssistantUseCase.execute(
                    user.getId(),
                    request.userMessage()
            );

            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(response));

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