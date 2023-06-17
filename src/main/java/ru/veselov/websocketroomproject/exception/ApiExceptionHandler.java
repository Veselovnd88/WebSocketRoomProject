package ru.veselov.websocketroomproject.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ErrorCode;
import ru.veselov.websocketroomproject.exception.error.ValidationErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ViolationError;

import java.util.List;


@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({RoomAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiErrorResponse handleConflictException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_CONFLICT, HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ExceptionHandler({NotCorrectOwnerException.class, NotCorrectTokenException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiErrorResponse handleNotAuthorizedException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiErrorResponse handleNotFoundException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_FOUND, HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        List<ViolationError> violationErrors = exception.getConstraintViolations().stream()
                .map(v -> new ViolationError(
                        fieldNameFromPath(v.getPropertyPath().toString()),
                        v.getMessage(),
                        v.getInvalidValue().toString()))
                .toList();
        return new ValidationErrorResponse(ErrorCode.ERROR_VALIDATION,
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(), violationErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ViolationError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ViolationError(error.getField(), error.getDefaultMessage(),
                        error.getRejectedValue() != null ? (String) error.getRejectedValue() : "null"))
                .toList();
        return new ValidationErrorResponse(ErrorCode.ERROR_VALIDATION,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                violations);
    }

    private String fieldNameFromPath(String path) {
        String[] split = path.split("\\.");
        if (split.length > 1) {
            return split[split.length - 1];
        }
        return path;
    }

}
