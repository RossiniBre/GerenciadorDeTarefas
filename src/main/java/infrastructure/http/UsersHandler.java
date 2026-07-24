package infrastructure.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class UsersHandler implements HttpHandler {

    private final HttpHandler registerAction;
    private final HttpHandler loginAction;
    private final HttpHandler logoutAction;

    public UsersHandler(HttpHandler registerAction, HttpHandler loginAction, HttpHandler logoutAction) {
        this.registerAction = registerAction;
        this.loginAction = loginAction;
        this.logoutAction = logoutAction;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.equals("/users/register") && method.equalsIgnoreCase("POST")) {
            registerAction.handle(exchange);
        } else if (path.equals("/users/login") && method.equalsIgnoreCase("POST")) {
            loginAction.handle(exchange);
        } else if (path.equals("/users/logout") && method.equalsIgnoreCase("DELETE")) {
            logoutAction.handle(exchange);
        } else if (path.equals("/users/register") || path.equals("/users/login") || path.equals("/users/logout")) {
            HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        } else {
            HttpJson.sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
        }
    }
}