package infrastructure.persistence;

import domain.assistant.AssistantSession;
import domain.repositories.AssistantSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAssistantSessionRepository implements AssistantSessionRepository {

    private final Map<String, AssistantSession> sessions = new HashMap<>();

    @Override
    public Optional<AssistantSession> find(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    @Override
    public void save(String token, AssistantSession session) {
        sessions.put(token, session);
    }

    @Override
    public void delete(String token) {
        sessions.remove(token);
    }
}