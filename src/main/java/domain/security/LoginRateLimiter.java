package domain.security;

public interface LoginRateLimiter {
    boolean isBlocked(String username);
    void registerFailure(String username);
    void registerSuccess(String username);
}