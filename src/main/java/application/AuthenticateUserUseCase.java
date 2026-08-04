package application;

import domain.security.LoginRateLimiter;
import domain.repositories.SessionRepository;
import domain.security.TokenGenerator;
import domain.model.User;
import domain.exceptions.InvalidCredentialsException;
import domain.exceptions.TooManyAttemptsException;

import java.time.Duration;
import java.time.Instant;

public class AuthenticateUserUseCase {

    private static final Duration SESSION_DURATION = Duration.ofHours(2);

    private final LoginUseCase loginUseCase;
    private final SessionRepository sessionRepository;
    private final TokenGenerator tokenGenerator;
    private final LoginRateLimiter rateLimiter;

    public AuthenticateUserUseCase(LoginUseCase loginUseCase, SessionRepository sessionRepository,
                                   TokenGenerator tokenGenerator, LoginRateLimiter rateLimiter) {
        this.loginUseCase = loginUseCase;
        this.sessionRepository = sessionRepository;
        this.tokenGenerator = tokenGenerator;
        this.rateLimiter = rateLimiter;
    }

    public Session execute(String username, String password) {
        if (rateLimiter.isBlocked(username)) {
            throw new TooManyAttemptsException(username);
        }

        User user;
        try {
            user = loginUseCase.execute(username, password);
        } catch (InvalidCredentialsException e) {
            rateLimiter.registerFailure(username);
            throw e;
        }

        rateLimiter.registerSuccess(username);

        String token = tokenGenerator.generate();
        Instant expiresAt = Instant.now().plus(SESSION_DURATION);
        sessionRepository.save(token, user.getId(), expiresAt);

        return new Session(token, user);
    }

    public static class Session {
        private final String token;
        private final User user;

        public Session(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public User getUser() { return user; }
    }
}