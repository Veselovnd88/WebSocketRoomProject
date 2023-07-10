package ru.veselov.websocketroomproject.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.veselov.websocketroomproject.entity.RoomEntity;

@Data
@RequiredArgsConstructor
public class RoomDeleteEvent {

    private final RoomEntity roomEntity;

}
