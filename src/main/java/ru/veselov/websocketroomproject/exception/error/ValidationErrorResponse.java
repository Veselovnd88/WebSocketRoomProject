package ru.veselov.websocketroomproject.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ApiErrorResponse {

    @Schema(description = "Error code starts with ERROR_", example = "ERROR_VALIDATION")
    private ErrorCode error=ErrorCode.ERROR_VALIDATION;

    @Schema(description = "List of violations in the field")
    private List<ViolationError> violations;

    public ValidationErrorResponse(int code,
                                   String message,
                                   List<ViolationError> violations) {
        super(ErrorCode.ERROR_VALIDATION, code, message);
        this.violations = violations;
    }

}
