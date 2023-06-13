package ru.veselov.websocketroomproject.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private String error;
    private String message;

}
