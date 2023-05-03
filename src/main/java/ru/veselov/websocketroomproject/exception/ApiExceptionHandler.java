package ru.veselov.websocketroomproject.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.exception.error.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({RoomAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleConflictException(RuntimeException exception) {
        log.error("Error [handled: {}]", exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({NotCorrectOwnerException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleNotAuthorizedException(RuntimeException exception) {
        log.error("Error [handled: {}]", exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_NOT_AUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, SubscriptionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoFoundException(RuntimeException exception) {
        log.error("Error [handled: {}]", exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(RuntimeException exception) {
        log.error("Error [handled: {}]", exception.getMessage());
        return new ErrorResponse(ErrorConstants.ERROR_ILLEGAL_ARG, exception.getMessage());
    }

}
