package ru.veselov.websocketroomproject.security;

public interface AuthTokenManager {

    JWTAuthToken createToken(String authHeader);

    void setAuthentication(JWTAuthToken token);

}
