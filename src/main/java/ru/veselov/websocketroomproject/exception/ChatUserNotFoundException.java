package ru.veselov.websocketroomproject.exception;

public class ChatUserNotFoundException extends RuntimeException {

    public ChatUserNotFoundException(String message) {
        super(message);
    }

}
