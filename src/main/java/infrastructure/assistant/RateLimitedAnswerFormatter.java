package infrastructure.assistant;

import domain.assistant.AnswerFormatter;
import domain.assistant.RateLimiter;
import infrastructure.config.AssistantConfig;

import java.time.LocalDateTime;

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