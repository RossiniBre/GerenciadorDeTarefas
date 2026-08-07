package com.taskmanager.infrastructure.persistence;

import com.taskmanager.domain.security.LoginRateLimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLoginRateLimiter implements LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private static class AttemptInfo {
        int failureCount = 0;
        Instant blockedUntil = null;
    }

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    @Override
    public boolean isBlocked(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null || info.blockedUntil == null) {
            return false;
        }
        if (Instant.now().isAfter(info.blockedUntil)) {
            attempts.remove(username); // bloqueio expirou, libera de novo
            return false;
        }
        return true;
    }

    @Override
    public void registerFailure(String username) {
        AttemptInfo info = attempts.computeIfAbsent(username, k -> new AttemptInfo());
        info.failureCount++;
        if (info.failureCount >= MAX_ATTEMPTS) {
            info.blockedUntil = Instant.now().plus(BLOCK_DURATION);
        }
    }

    @Override
    public void registerSuccess(String username) {
        attempts.remove(username);
    }
}