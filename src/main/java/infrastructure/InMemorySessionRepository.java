package infrastructure;

import domain.SessionRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository implements SessionRepository {

    private record SessionData(String userId, Instant expiresAt) {}

    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    @Override
    public void save(String token, String userId, Instant expiresAt) {
        sessions.put(token, new SessionData(userId, expiresAt));
    }

    @Override
    public Optional<String> findUserIdByToken(String token) {
        SessionData data = sessions.get(token);
        if (data == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(data.expiresAt())) {
            sessions.remove(token); // sessão vencida: limpa no primeiro acesso pós-expiração
            return Optional.empty();
        }
        return Optional.of(data.userId());
    }

    @Override
    public void delete(String token) {
        sessions.remove(token);
    }
}