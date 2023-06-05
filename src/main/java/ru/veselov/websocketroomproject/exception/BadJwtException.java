package ru.veselov.websocketroomproject.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class BadJwtException extends JWTVerificationException {

    public BadJwtException(String message) {
        super(message);
    }
}
