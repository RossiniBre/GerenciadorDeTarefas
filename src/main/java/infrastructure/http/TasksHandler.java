package infrastructure.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class TasksHandler implements HttpHandler {

    private final HttpHandler createAction;
    private final HttpHandler listAction;
    private final HttpHandler updateAction;
    private final HttpHandler deleteAction;

    public TasksHandler(HttpHandler createAction, HttpHandler listAction, HttpHandler updateAction, HttpHandler deleteAction) {
        this.createAction = createAction;
        this.listAction = listAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/tasks")) {
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "POST" -> createAction.handle(exchange);
                case "GET" -> listAction.handle(exchange);
                default -> HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else if (path.startsWith("/tasks/")) {
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "PATCH" -> updateAction.handle(exchange);
                case "DELETE" -> deleteAction.handle(exchange);
                default -> HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else {
            HttpJson.sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
        }
    }
}