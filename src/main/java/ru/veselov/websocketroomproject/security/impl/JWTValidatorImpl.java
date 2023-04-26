package ru.veselov.websocketroomproject.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.security.JWTUtils;
import ru.veselov.websocketroomproject.security.JWTValidator;
@Component
@Slf4j
@RequiredArgsConstructor
public class JWTValidatorImpl<T extends RuntimeException> implements JWTValidator<T> {

    private static final String BEARER = "Bearer ";

    private static final String HEADER = "Authorization";

    private final JWTUtils jwtUtils;

    @Override
    public void validate(String authHeader, T exception) {
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            throw  T("Not correct JWT in auth");
        }
    }
}
