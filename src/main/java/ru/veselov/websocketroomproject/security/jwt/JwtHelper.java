package ru.veselov.websocketroomproject.security.jwt;

public interface JwtHelper {

    String getUsername(String jwt);

    String getRole(String jwt);

}
