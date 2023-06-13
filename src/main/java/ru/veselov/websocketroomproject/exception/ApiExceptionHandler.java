package ru.veselov.websocketroomproject.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.exception.error.ErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ValidationErrorResponse;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({RoomAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleConflictException(RuntimeException exception) {
        return new ErrorResponse(ErrorConstants.ERROR_CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({NotCorrectOwnerException.class, NotCorrectTokenException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleNotAuthorizedException(RuntimeException exception) {
        return new ErrorResponse(ErrorConstants.ERROR_NOT_AUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, SubscriptionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(RuntimeException exception) {
        return new ErrorResponse(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(RuntimeException exception) {
        return new ErrorResponse(ErrorConstants.ERROR_VALIDATION, "No");
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleMessagingException(RuntimeException exception) {
        return new ErrorResponse(ErrorConstants.ERROR_MESSAGING, exception.getMessage());
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public org.springframework.web.ErrorResponse handleValidationException(CustomValidationException exception) {
        return org.springframework.web.ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .titleMessageCode("CODE")
                .build();
        /*return new ValidationErrorResponse(ErrorConstants.ERROR_VALIDATION,
                exception.getValidationMap());*/
    }

}
