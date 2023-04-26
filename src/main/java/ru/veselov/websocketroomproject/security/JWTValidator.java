package ru.veselov.websocketroomproject.security;

public interface JWTValidator<T extends RuntimeException> {

    void validate(String authHeader, T exception);

}
