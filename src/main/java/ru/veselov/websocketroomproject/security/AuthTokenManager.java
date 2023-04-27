package ru.veselov.websocketroomproject.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthTokenManager {

    UsernamePasswordAuthenticationToken createToken(String authHeader);

    void setAuthentication(UsernamePasswordAuthenticationToken token);

}
