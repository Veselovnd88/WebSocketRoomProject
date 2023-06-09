package ru.veselov.websocketroomproject.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ErrorCode;
import ru.veselov.websocketroomproject.exception.error.ValidationErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ViolationError;

import java.util.List;


@RestControllerAdvice
@Slf4j
@ApiResponse(responseCode = "400", description = "Validation of fields failed",
        content = @Content(
                schema = @Schema(implementation = ValidationErrorResponse.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        ))
@ApiResponse(responseCode = "401", description = "Authentication failed",
        content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                        {
                          "error": "ERROR_UNAUTHORIZED",
                          "code": 401,
                          "message": "Something went wrong with authentication"
                        }"""),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        ))
public class ApiExceptionHandler {

    @ExceptionHandler({RoomAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleConflictException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_CONFLICT, HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ExceptionHandler(InvalidRoomTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponse handleInvalidRoomTokenException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_INVALID_ROOM_TOKEN,
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage());
    }


    @ExceptionHandler(NotCorrectOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponse handleNotAuthorizedException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_ROOM_OWNER,
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(RuntimeException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_FOUND, HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler({PageExceedsMaximumValueException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handlePageExceedMaximumValueException(PageExceedsMaximumValueException exception) {
        List<ViolationError> violationErrors = List.of(new ViolationError("Page", "Page number exceed maximum",
                String.valueOf(exception.getCurrentPage())));
        return new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(), violationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        List<ViolationError> violationErrors = exception.getConstraintViolations().stream()
                .map(v -> new ViolationError(
                        fieldNameFromPath(v.getPropertyPath().toString()),
                        v.getMessage(),
                        v.getInvalidValue().toString()))
                .toList();
        return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), violationErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ViolationError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ViolationError(error.getField(), error.getDefaultMessage(),
                        error.getRejectedValue() != null ? (String) error.getRejectedValue() : "null"))
                .toList();
        return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), violations);
    }

    private String fieldNameFromPath(String path) {
        String[] split = path.split("\\.");
        if (split.length > 1) {
            return split[split.length - 1];
        }
        return path;
    }

}
