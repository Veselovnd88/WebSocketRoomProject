package ru.veselov.websocketroomproject.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class CustomErrorResponse implements Serializable {

    private String error;

}
