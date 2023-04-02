package ru.veselov.websocketroomproject.exception;

import lombok.NoArgsConstructor;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(String message) {
        super(message);
    }

}
