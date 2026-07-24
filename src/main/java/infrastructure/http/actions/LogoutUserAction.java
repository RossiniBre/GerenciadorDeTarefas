package infrastructure.http.actions;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.SessionRepository;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class LogoutUserAction implements HttpHandler {

    private final SessionRepository sessionRepository;

    public LogoutUserAction(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String header = exchange.getRequestHeaders().getFirst("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length()).trim();
            sessionRepository.delete(token);
        }
        
        HttpJson.sendResponse(exchange, 204, "");
    }
}