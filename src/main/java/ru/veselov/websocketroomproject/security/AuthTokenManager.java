package ru.veselov.websocketroomproject.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthTokenManager {

    JwtAuthenticationToken createAndAuthenticateToken(String authHeader);

}