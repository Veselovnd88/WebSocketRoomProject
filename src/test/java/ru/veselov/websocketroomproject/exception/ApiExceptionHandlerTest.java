package ru.veselov.websocketroomproject.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.veselov.websocketroomproject.exception.error.ErrorConstants;
import ru.veselov.websocketroomproject.exception.error.ErrorResponse;

class ApiExceptionHandlerTest {

    private static final String MESSAGE = "message";

    ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    @Test
    void shouldReturnConflictError() {
        ErrorResponse errorResponse = exceptionHandler.handleConflictException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_CONFLICT);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnUnauthorizedError() {
        ErrorResponse errorResponse = exceptionHandler.handleNotAuthorizedException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_NOT_AUTHORIZED);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnNotFoundError() {
        ErrorResponse errorResponse = exceptionHandler.handleNotFoundException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_NOT_FOUND);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

    @Test
    void shouldReturnInternalServerError() {
        ErrorResponse errorResponse = exceptionHandler.handleMessagingException(new RuntimeException(MESSAGE));
        Assertions.assertThat(errorResponse.getError()).isEqualTo(ErrorConstants.ERROR_MESSAGING);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(MESSAGE);
    }

}
