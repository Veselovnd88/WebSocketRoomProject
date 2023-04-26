package ru.veselov.websocketroomproject.security;

public interface JWTValidator {

    void validate(String authHeader);

}
