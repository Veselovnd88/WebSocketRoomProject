package ru.veselov.websocketroomproject.exception.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorConstants {

    public static final String ERROR_NOT_FOUND = "error.not.found";

    public static final String ERROR_NOT_AUTHORIZED = "error.not.authorized";

    public static final String ERROR_CONFLICT = "error.conflict";

    public static final String ERROR_ILLEGAL_ARG = "error.illegal.argument";

    public static final String ERROR_MESSAGING = "error.messaging";

    public static final String ERROR_VALIDATION = "error.validation";

}
