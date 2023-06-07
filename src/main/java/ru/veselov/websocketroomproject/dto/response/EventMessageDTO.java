package ru.veselov.websocketroomproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.websocketroomproject.event.EventType;

@Data
@AllArgsConstructor
public class EventMessageDTO<T> {
    private EventType eventType;

    private T data;

}