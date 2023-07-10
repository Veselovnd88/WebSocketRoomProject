package ru.veselov.websocketroomproject.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse implements Serializable {

    @Schema(description = "Error code starts with ERROR_",example = "ERROR_NOT_FOUND")
    private ErrorCode error;

    @Schema(description = "HttpStatus of error", example = "400")
    private int code;

    @Schema(description = "Detailed message", example = "Error message")
    private String message;

}
