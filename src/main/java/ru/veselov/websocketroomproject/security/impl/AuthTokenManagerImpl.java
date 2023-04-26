package ru.veselov.websocketroomproject.security.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.security.AuthTokenManager;
import ru.veselov.websocketroomproject.security.JWTAuthToken;
import ru.veselov.websocketroomproject.security.JWTUtils;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthTokenManagerImpl implements AuthTokenManager {

    private final JWTUtils jwtUtils;

    @Override
    public JWTAuthToken createToken(String authHeader) {
        String jwt = authHeader.substring(7);
        return new JWTAuthToken(
                jwtUtils.getUsername(jwt),
                Collections.singletonList(new SimpleGrantedAuthority(jwtUtils.getRole(jwt)))
        );
    }

    @Override
    public void setAuthentication(JWTAuthToken jwtAuthToken) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
        }
    }

}