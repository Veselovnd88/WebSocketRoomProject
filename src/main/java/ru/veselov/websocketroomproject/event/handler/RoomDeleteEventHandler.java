package ru.veselov.websocketroomproject.event.handler;

import ru.veselov.websocketroomproject.entity.RoomEntity;

public interface RoomDeleteEventHandler {

    void handleRoomDeleteEvent(RoomEntity roomEntity);
}
