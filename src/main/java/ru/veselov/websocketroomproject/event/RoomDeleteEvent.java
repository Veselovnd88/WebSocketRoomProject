package ru.veselov.websocketroomproject.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RoomDeleteEvent {

    private final String roomId;

}
