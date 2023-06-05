package ru.veselov.websocketroomproject.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

public class WrongJwtException extends JWTVerificationException {

    public WrongJwtException(String message) {
        super(message);
    }
}
