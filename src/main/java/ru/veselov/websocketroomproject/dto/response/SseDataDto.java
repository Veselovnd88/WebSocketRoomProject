package ru.veselov.websocketroomproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SseDataDto<T> implements Serializable {

    private String message;

    private T payload;

}
