package ru.veselov.websocketroomproject.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.exception.error.ErrorResponse;

class ApiExceptionHandlerTest {

    private static final String MESSAGE = "message";

    ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    @Test
    void shouldReturnConflictError() {
        ErrorResponse errorResponse = exceptionHandler.handleConflictException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_CONFLICT);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnUnauthorizedError() {
        ErrorResponse errorResponse = exceptionHandler.handleNotAuthorizedException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_NOT_AUTHORIZED);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnNotFoundError() {
        ErrorResponse errorResponse = exceptionHandler.handleNotFoundException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_NOT_FOUND);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnBadRequestError() {
        ErrorResponse errorResponse = exceptionHandler.handleIllegalArgumentException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_ILLEGAL_ARG);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnInternalServerError() {
        ErrorResponse errorResponse = exceptionHandler.handleMessagingException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_MESSAGING);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

}