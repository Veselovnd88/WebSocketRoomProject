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
    @ResponseBody
    public ErrorResponse handleConflictException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_CONFLICT, exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotCorrectOwnerException.class, NotCorrectTokenException.class})
    @ResponseBody
    public ErrorResponse handleNotAuthorizedException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_NOT_AUTHORIZED, exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({EntityNotFoundException.class, SubscriptionNotFoundException.class})
    @ResponseBody
    public ErrorResponse handleNotFoundException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_ILLEGAL_ARG, exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseBody
    public ErrorResponse handleMessagingException(RuntimeException exception) {
        log.error(LOG_MESSAGE, exception.getMessage());
        return new ErrorResponse(
                ErrorConstants.ERROR_MESSAGING,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}