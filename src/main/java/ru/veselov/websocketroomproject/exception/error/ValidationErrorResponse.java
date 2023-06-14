package ru.veselov.websocketroomproject.exception.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ValidationErrorResponse extends CustomErrorResponse {

    private List<ViolationError> violations;

    public ValidationErrorResponse(String error, List<ViolationError> violations) {
        super(error);
        this.violations = violations;
    }

}
