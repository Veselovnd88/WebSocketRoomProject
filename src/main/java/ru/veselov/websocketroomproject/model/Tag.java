package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Tag implements Serializable {
    @Schema(description = "Tag name", example = "Movie")
    @NotEmpty(message = "Tag name cannot be empty")
    @Size(min = 3, max = 10, message = "Name length should be from 3 to 10 symbols")
    @JsonValue
    private String name;

}
