package ru.veselov.websocketroomproject.exception;

import org.springframework.messaging.MessagingException;

public class InvalidStompHeaderException extends MessagingException {
    public InvalidStompHeaderException(String description) {
        super(description);
    }

}
