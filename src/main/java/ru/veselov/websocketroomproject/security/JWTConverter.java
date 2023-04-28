package ru.veselov.websocketroomproject.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTConverter implements Converter<String, JwtAuthenticationToken> {

    private final JWTUtils jwtUtils;

    @Override
    public JwtAuthenticationToken convert(@NonNull String jwt) {
        String username = jwtUtils.getUsername(jwt);
        String role = jwtUtils.getRole(jwt);
        JwtAuthenticationToken token = new JwtAuthenticationToken(username, jwt, role);
        token.setAuthenticated(true);
        return token;
    }

}