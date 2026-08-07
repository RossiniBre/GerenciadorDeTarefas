package com.taskmanager.infrastructure.assistant;

import com.taskmanager.domain.assistant.AnswerFormatter;
import com.taskmanager.domain.assistant.RateLimiter;
import com.taskmanager.infrastructure.config.AssistantConfig;

public class RateLimitedAnswerFormatter implements AnswerFormatter {

    private final AnswerFormatter delegate;
    private final RateLimiter rateLimiter;

    public RateLimitedAnswerFormatter(AnswerFormatter delegate, RateLimiter rateLimiter) {
        this.delegate = delegate;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public String format(String instructions, String data) {
        if (!rateLimiter.tryConsume(AssistantConfig.MODEL)) {
            throw new DailyQuotaExceededException(rateLimiter.nextResetAt());
        }

        return delegate.format(instructions, data);
    }
}