package domain.repositories;

import java.time.Instant;
import java.util.Optional;

public interface SessionRepository {
    void save(String token, String userId, Instant expiresAt);
    Optional<String> findUserIdByToken(String token);
    void delete(String token);
}