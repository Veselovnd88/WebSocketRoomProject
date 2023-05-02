package ru.veselov.websocketroomproject.exception;

public class NotCorrectOwnerException extends RuntimeException {
    public NotCorrectOwnerException(String message) {
        super(message);
    }
}
