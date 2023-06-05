package ru.veselov.websocketroomproject.exception;

public class NotCorrectTokenException extends RuntimeException {

    public NotCorrectTokenException(String message) {
        super(message);
    }
}
