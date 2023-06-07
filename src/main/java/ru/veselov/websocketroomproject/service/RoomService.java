package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.model.Room;

import java.security.Principal;
import java.util.List;

public interface RoomService {

    Room createRoom(Room room, Principal principal);

    Room getRoomById(String id, String token);

    Room getRoomByName(String name);

    Room changeSettings(String roomId, RoomSettingsDTO settings, Principal principal);

    void addUrl(String roomId, String url, Principal principal);

    List<Room> findAll(int page, String sort);

}