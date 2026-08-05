package domain.assistant;

import java.time.LocalDateTime;

public interface RateLimiter {
    boolean tryConsume(String key);
    LocalDateTime nextResetAt();
}