package ru.veselov.websocketroomproject.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.exception.error.ErrorCode;

class ApiExceptionHandlerTest {

    private static final String MESSAGE = "message";

    ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    @Test
    void shouldReturnConflictError() {
        ApiErrorResponse errorResponse = exceptionHandler.handleConflictException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorCode.ERROR_CONFLICT);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnUnauthorizedError() {
        ApiErrorResponse errorResponse = exceptionHandler.handleNotAuthorizedException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorCode.ERROR_UNAUTHORIZED);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnNotFoundError() {
        ApiErrorResponse errorResponse = exceptionHandler.handleNotFoundException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorCode.ERROR_NOT_FOUND);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnInternalServerError() {
        ApiErrorResponse errorResponse = exceptionHandler.handleMessagingException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorCode.ERROR_MESSAGING);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

}
