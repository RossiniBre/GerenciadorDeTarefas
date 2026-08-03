package infrastructure.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class AssistantHandler implements HttpHandler {

    private final HttpHandler sendMessageAction;
    private final HttpHandler confirmAction;
    private final HttpHandler rejectAction;

    public AssistantHandler(
            HttpHandler sendMessageAction,
            HttpHandler confirmAction,
            HttpHandler rejectAction
    ) {
        this.sendMessageAction = sendMessageAction;
        this.confirmAction = confirmAction;
        this.rejectAction = rejectAction;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            HttpJson.sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        switch (path) {
            case "/assistant/message" -> sendMessageAction.handle(exchange);
            case "/assistant/confirm" -> confirmAction.handle(exchange);
            case "/assistant/reject" -> rejectAction.handle(exchange);
            default -> HttpJson.sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
        }
    }
}