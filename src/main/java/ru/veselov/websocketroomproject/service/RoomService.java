package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.model.Room;

import java.security.Principal;
import java.util.List;

public interface RoomService {

    Room createRoom(Room room);

    Room getRoomById(String id);

    Room getRoomByName(String name);

    Room changeSettings(String roomId, RoomSettingsDTO settings, Principal principal);

    List<Room> getAllRooms();

    void addUrl(String roomId, String url);

}
