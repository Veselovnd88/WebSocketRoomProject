package ru.veselov.websocketroomproject.security;


import ru.veselov.websocketroomproject.security.authentication.JwtAuthenticationToken;

public interface AuthTokenManager {

    JwtAuthenticationToken createAndAuthenticateToken(String authHeader);

}
