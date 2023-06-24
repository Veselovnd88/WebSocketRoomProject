package ru.veselov.websocketroomproject.event.handler;

import ru.veselov.websocketroomproject.model.Room;

public interface RoomUpdateEventHandler {

    void handleRoomSettingUpdateEvent(Room room);

    void handleActiveURLUpdateEvent(String roomId, String  url);

}
