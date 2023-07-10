package ru.veselov.websocketroomproject.exception;

import lombok.Getter;

public class PageExceedsMaximumValueException extends RuntimeException {
    @Getter
    private final int currentPage;

    public PageExceedsMaximumValueException(String message, int currentPage) {
        super(message);
        this.currentPage = currentPage;
    }
}
