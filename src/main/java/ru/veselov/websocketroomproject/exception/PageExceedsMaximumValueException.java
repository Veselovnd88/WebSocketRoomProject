package ru.veselov.websocketroomproject.exception;

public class PageExceedsMaximumValueException extends RuntimeException {

    public PageExceedsMaximumValueException(String message) {
        super(message);
    }

}
