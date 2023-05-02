package ru.veselov.websocketroomproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private String error;
    private String message;


}
