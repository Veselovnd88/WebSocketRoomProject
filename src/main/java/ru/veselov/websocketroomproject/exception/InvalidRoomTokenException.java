package ru.veselov.websocketroomproject.exception;

public class InvalidRoomTokenException extends RuntimeException {

    public InvalidRoomTokenException(String message) {
        super(message);
    }
}
