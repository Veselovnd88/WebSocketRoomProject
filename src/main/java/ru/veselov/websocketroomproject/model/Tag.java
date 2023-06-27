package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Tag implements Serializable {
    @JsonValue
    private String name;

}
