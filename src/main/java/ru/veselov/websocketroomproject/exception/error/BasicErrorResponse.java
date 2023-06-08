package ru.veselov.websocketroomproject.exception.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasicErrorResponse extends CustomErrorResponse {

    private String message;

    public BasicErrorResponse(String error, String message) {
        super(error);
        this.message = message;
    }

}