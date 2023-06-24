package ru.veselov.websocketroomproject.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveURLUpdateEvent {

    String roomId;

    String url;

}
