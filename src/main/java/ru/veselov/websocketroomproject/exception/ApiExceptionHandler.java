package ru.veselov.websocketroomproject.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.exception.error.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final String LOG_MESSAGE = "Error [handled: {}]";

    @ExceptionHandler({RoomAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleConflictException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({NotCorrectOwnerException.class, NotCorrectTokenException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleNotAuthorizedException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_NOT_AUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, SubscriptionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_ILLEGAL_ARG, exception.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleMessagingException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_MESSAGING, exception.getMessage());
    }

}