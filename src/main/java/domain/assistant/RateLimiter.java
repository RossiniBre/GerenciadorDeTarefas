package domain.assistant;

public interface RateLimiter {
    boolean tryConsume(String key);
}