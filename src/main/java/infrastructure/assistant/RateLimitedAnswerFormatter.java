package infrastructure.assistant;

import domain.assistant.AnswerFormatter;
import domain.assistant.RateLimiter;

public class RateLimitedAnswerFormatter implements AnswerFormatter {

    private final AnswerFormatter delegate;
    private final RateLimiter rateLimiter;

    public RateLimitedAnswerFormatter(AnswerFormatter delegate, RateLimiter rateLimiter) {
        this.delegate = delegate;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public String format(String instructions, String data) {
        if (!rateLimiter.tryConsume(RateLimitKeys.GLOBAL)) {
            throw new DailyQuotaExceededException();
        }

        return delegate.format(instructions, data);
    }
}