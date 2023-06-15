package ru.veselov.websocketroomproject.exception.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Data
public class ValidationErrorResponse extends ApiErrorResponse {

    private List<ViolationError> violations;

    public ValidationErrorResponse(ErrorCode error,
                                   int code,
                                   String message,
                                   List<ViolationError> violations) {
        super(error, code, message);
        this.violations = violations;
    }

}
