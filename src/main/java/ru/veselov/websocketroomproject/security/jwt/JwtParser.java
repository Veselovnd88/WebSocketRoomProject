package ru.veselov.websocketroomproject.security.jwt;

public interface JwtParser {

    String getUsername(String token);

    String getRole(String token);

}
