package ru.veselov.websocketroomproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.websocketroomproject.event.EventType;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EventMessageDTO<T> implements Serializable {
    @Schema(description = "Type of event", example = "init")
    private EventType eventType;

    @Schema(description = "Payload data", example = "data")
    private SseDataDto<T> data;

}
