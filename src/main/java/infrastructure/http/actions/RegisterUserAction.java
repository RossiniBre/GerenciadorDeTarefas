package infrastructure.http.actions;

import application.RegisterUserUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.model.User;
import domain.exceptions.DomainException;
import domain.exceptions.DuplicateUsernameException;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.RegisterUserRequest;
import infrastructure.http.dto.RegisterUserResponse;

import java.io.IOException;

public class RegisterUserAction implements HttpHandler {

    private final RegisterUserUseCase registerUserUseCase;
    private final JsonMapper jsonMapper;

    public RegisterUserAction(RegisterUserUseCase registerUserUseCase, JsonMapper jsonMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String body = HttpJson.readBody(exchange.getRequestBody());
            RegisterUserRequest request = jsonMapper.fromJson(body, RegisterUserRequest.class);

            User user = registerUserUseCase.execute(request.username, request.password);

            var response = new RegisterUserResponse(user.getId(), user.getUsername());
            HttpJson.sendResponse(exchange, 201, jsonMapper.toJson(response));

        } catch (DuplicateUsernameException e) {
            HttpJson.sendResponse(exchange, 409, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}