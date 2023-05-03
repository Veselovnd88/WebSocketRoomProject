package ru.veselov.websocketroomproject.exception;

import jakarta.persistence.EntityNotFoundException;

public class ChatUserNotFoundException extends EntityNotFoundException {

    public ChatUserNotFoundException(String message) {
        super(message);
    }

}
