package ru.veselov.websocketroomproject.event.handler;

import ru.veselov.websocketroomproject.model.Room;

public interface RoomUpdateHandler {

    void handleRoomSettingUpdateEvent(Room room);

    void handleActiveURLUpdateEvent(String roomId, String  url);

}
