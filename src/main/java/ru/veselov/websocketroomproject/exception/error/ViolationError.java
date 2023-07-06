package ru.veselov.websocketroomproject.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ViolationError implements Serializable {

    @Schema(description = "Field name where violation occurred", example = "name")
    private String fieldName;

    @Schema(description = "Detailed message")
    private String message;

    @Schema(description = "Current value of field")
    private String currentValue;

}
