package ru.veselov.websocketroomproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.websocketroomproject.controller.EventType;

@Data
@AllArgsConstructor
public class EventMessageDTO<T> {
    private EventType eventType;

    private T message;

}
