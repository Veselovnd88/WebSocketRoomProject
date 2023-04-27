package ru.veselov.websocketroomproject.security.impl;

import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.security.AuthTokenManager;
import ru.veselov.websocketroomproject.security.JWTConverter;
import ru.veselov.websocketroomproject.security.JWTUtils;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthTokenManagerImpl implements AuthTokenManager {

    private final JWTConverter jwtConverter;

    @Override
    public UsernamePasswordAuthenticationToken createAndAuthenticateToken(String authHeader) {
        String jwt = authHeader.substring(7);
        if (jwt.isBlank()) {
            log.warn("Empty jwt in bearer header");
            throw new JWTDecodeException("Empty JWT in Bearer header");
        }
        UsernamePasswordAuthenticationToken token = jwtConverter.convert(jwt);
        SecurityContextHolder.getContext().setAuthentication(token);
        return token;
    }

}