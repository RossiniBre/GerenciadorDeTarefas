package infrastructure.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.model.User;
import domain.repositories.SessionRepository;
import domain.repositories.UserRepository;
import infrastructure.http.actions.AssistantAction;
import infrastructure.http.json.HttpJson;

import java.io.IOException;

public class AssistantHandler implements HttpHandler {

    private final AssistantAction assistantAction;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public AssistantHandler(
            AssistantAction assistantAction,
            SessionRepository sessionRepository,
            UserRepository userRepository
    ) {
        this.assistantAction = assistantAction;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // debug
        System.out.println("ENTROU NO ASSISTANT HANDLER");

        try {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            User user = AuthContext.requireUser(
                    exchange,
                    sessionRepository,
                    userRepository
            );

            String body = HttpJson.readBody(
                    exchange.getRequestBody()
            );

            String response = assistantAction.execute(
                    body,
                    user
            );

            HttpJson.sendResponse(
                    exchange,
                    200,
                    response
            );

        } catch (Exception e) {

            e.printStackTrace();

            HttpJson.sendResponse(
                    exchange,
                    500,
                    "{\"error\":\"" + e.getMessage() + "\"}"
            );
        }
    }
}