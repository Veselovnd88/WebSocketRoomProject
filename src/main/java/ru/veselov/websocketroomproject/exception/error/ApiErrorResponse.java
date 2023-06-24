package ru.veselov.websocketroomproject.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse implements Serializable {

    private ErrorCode error;

    private int code;

    private String message;

}
