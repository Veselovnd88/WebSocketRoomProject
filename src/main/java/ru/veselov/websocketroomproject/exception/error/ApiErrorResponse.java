package ru.veselov.websocketroomproject.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApiErrorResponse implements Serializable {

    private ErrorCode error;

    private int code;

    private String message;

}
