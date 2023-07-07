package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Tag implements Serializable {
    @Schema(description = "Tag name", example = "Movie")
    @JsonValue
    private String name;

}
