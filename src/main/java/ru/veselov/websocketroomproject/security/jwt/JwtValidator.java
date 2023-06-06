package ru.veselov.websocketroomproject.security.jwt;

public interface JwtValidator {

    boolean isValidJwt(String jwt);
}
