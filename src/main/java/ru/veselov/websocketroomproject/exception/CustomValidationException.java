package ru.veselov.websocketroomproject.exception;

import lombok.Getter;

import java.util.Map;

public class CustomValidationException extends RuntimeException {

    @Getter
    private final Map<String, String> validationMap;

    public CustomValidationException(String message, Map<String, String> validationMap) {
        super(message);
        this.validationMap = validationMap;
    }

}
