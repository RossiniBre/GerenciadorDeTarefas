package com.taskmanager.infrastructure.assistant;

import com.taskmanager.domain.assistant.IntentExtractor;
import com.taskmanager.domain.assistant.RateLimiter;
import com.taskmanager.infrastructure.config.AssistantConfig;

public class RateLimitedIntentExtractor implements IntentExtractor {
    
    private final IntentExtractor delegate;
    private final RateLimiter rateLimiter;

    public RateLimitedIntentExtractor(IntentExtractor delegate, RateLimiter rateLimiter) {
        this.delegate = delegate;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public String extract(String instructions, String userMessage) {
        if (!rateLimiter.tryConsume(AssistantConfig.MODEL)) {
            throw new DailyQuotaExceededException(rateLimiter.nextResetAt());
        }

        return delegate.extract(instructions, userMessage);
    }
}