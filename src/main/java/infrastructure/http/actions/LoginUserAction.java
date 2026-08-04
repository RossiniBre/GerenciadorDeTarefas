package infrastructure.http.actions;

import application.AuthenticateUserUseCase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.exceptions.DomainException;
import domain.exceptions.InvalidCredentialsException;
import domain.exceptions.TooManyAttemptsException;
import infrastructure.http.json.HttpJson;
import infrastructure.http.json.JsonMapper;
import infrastructure.http.dto.LoginUserRequest;
import infrastructure.http.dto.LoginUserResponse;

import java.io.IOException;

public class LoginUserAction implements HttpHandler {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JsonMapper jsonMapper;

    public LoginUserAction(AuthenticateUserUseCase authenticateUserUseCase, JsonMapper jsonMapper) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String body = HttpJson.readBody(exchange.getRequestBody());
            LoginUserRequest request = jsonMapper.fromJson(body, LoginUserRequest.class);

            AuthenticateUserUseCase.Session session = authenticateUserUseCase.execute(request.username, request.password);

            var response = new LoginUserResponse(session.getUser().getId(), session.getUser().getUsername(), session.getToken());
            HttpJson.sendResponse(exchange, 200, jsonMapper.toJson(response));

        } catch (InvalidCredentialsException e) {
            HttpJson.sendResponse(exchange, 401, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"Valor inválido: " + e.getMessage() + "\"}");
        } catch (TooManyAttemptsException e) {
            HttpJson.sendResponse(exchange, 429, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (DomainException e) {
            HttpJson.sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            HttpJson.sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}