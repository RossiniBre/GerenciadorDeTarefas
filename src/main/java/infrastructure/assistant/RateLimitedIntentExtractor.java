package infrastructure.assistant;

import domain.assistant.IntentExtractor;
import domain.assistant.RateLimiter;

public class RateLimitedIntentExtractor implements IntentExtractor {
    
    private final IntentExtractor delegate;
    private final RateLimiter rateLimiter;

    public RateLimitedIntentExtractor(IntentExtractor delegate, RateLimiter rateLimiter) {
        this.delegate = delegate;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public String extract(String instructions, String userMessage) {
        if (!rateLimiter.tryConsume(RateLimitKeys.GLOBAL)) {
            throw new DailyQuotaExceededException();
        }

        return delegate.extract(instructions, userMessage);
    }
}