package com.taskmanager.infrastructure.assistant;

import com.taskmanager.domain.assistant.RateLimiter;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRateLimiter implements RateLimiter {

    private final int dailyLimit;
    private final Clock clock;
    private final Map<String, AtomicInteger> countersByKey = new ConcurrentHashMap<>();
    private volatile LocalDate currentDay;

    public InMemoryRateLimiter(int dailyLimit, Clock clock) {
        if (dailyLimit <= 0) {
            throw new IllegalArgumentException("dailyLimit deve ser positivo!");
        }
        this.dailyLimit = dailyLimit;
        this.clock = clock;
        this.currentDay = LocalDate.now(clock);
    }

    @Override
    public synchronized boolean tryConsume(String key) {
        resetIfNewDay();

        AtomicInteger counter = countersByKey.computeIfAbsent(key, k -> new AtomicInteger(0));

        if (counter.get() >= dailyLimit) {
            return false;
        }

        counter.incrementAndGet();
        return true;
    }

    @Override
    public LocalDateTime nextResetAt() {
        return currentDay.plusDays(1).atStartOfDay();
    }

    private void resetIfNewDay() {
        LocalDate today = LocalDate.now(clock);
        if (!today.equals(currentDay)) {
            countersByKey.clear();
            currentDay = today;
        }
    }
}