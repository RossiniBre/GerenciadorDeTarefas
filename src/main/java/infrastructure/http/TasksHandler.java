package infrastructure.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class TasksHandler implements HttpHandler {

    private final HttpHandler createAction;
    private final HttpHandler listAction;

    public TasksHandler(HttpHandler createAction, HttpHandler listAction) {
        this.createAction = createAction;
        this.listAction = listAction;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod().toUpperCase()) {
            case "POST" -> createAction.handle(exchange);
            case "GET" -> listAction.handle(exchange);
            default -> HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }
}