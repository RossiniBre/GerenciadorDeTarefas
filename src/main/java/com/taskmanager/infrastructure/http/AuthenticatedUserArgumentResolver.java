package com.taskmanager.infrastructure.http;

import com.taskmanager.domain.exceptions.InvalidCredentialsException;
import com.taskmanager.domain.model.User;
import com.taskmanager.domain.repositories.SessionRepository;
import com.taskmanager.domain.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public AuthenticatedUserArgumentResolver(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidCredentialsException();
        }

        String token = header.substring("Bearer ".length()).trim();

        String userId = sessionRepository.findUserIdByToken(token)
                .orElseThrow(InvalidCredentialsException::new);

        return userRepository.findById(userId)
                .orElseThrow(InvalidCredentialsException::new);
    }
}