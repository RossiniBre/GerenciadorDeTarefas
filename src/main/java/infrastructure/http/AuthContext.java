package infrastructure.http;

import com.sun.net.httpserver.HttpExchange;
import domain.repositories.SessionRepository;
import domain.model.User;
import domain.repositories.UserRepository;
import domain.exceptions.InvalidCredentialsException;

public final class AuthContext {

    private AuthContext() {}

    public static User requireUser(HttpExchange exchange, SessionRepository sessionRepository, UserRepository userRepository) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidCredentialsException();
        }
        String token = header.substring("Bearer ".length()).trim();

        String userId = sessionRepository.findUserIdByToken(token).orElseThrow(InvalidCredentialsException::new);

        return userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
    }
}