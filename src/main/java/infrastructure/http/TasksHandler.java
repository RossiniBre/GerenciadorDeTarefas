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
    private final HttpHandler listNotificationsAction;

    public TasksHandler(HttpHandler createAction, HttpHandler listAction, HttpHandler updateAction,
                        HttpHandler deleteAction, HttpHandler listNotificationsAction) {
        this.createAction = createAction;
        this.listAction = listAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.listNotificationsAction = listNotificationsAction;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();

        if (path.equals("/tasks")) {
            switch (method) {
                case "POST" -> createAction.handle(exchange);
                case "GET" -> listAction.handle(exchange);
                default -> HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else if (path.endsWith("/notifications")) {
            switch (method) {
                case "GET" -> listNotificationsAction.handle(exchange);
                default -> HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else if (path.startsWith("/tasks/")) {
            switch (method) {
                case "PATCH" -> updateAction.handle(exchange);
                case "DELETE" -> deleteAction.handle(exchange);
                default -> HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } else {
            HttpJson.sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
        }
    }
}