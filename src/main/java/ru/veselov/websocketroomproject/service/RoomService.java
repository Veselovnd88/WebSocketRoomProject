package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.Room;

import java.security.Principal;
import java.util.List;

public interface RoomService {

    Room createRoom(Room room, Principal principal);

    Room getRoomById(String id, String token);

    Room getRoomByName(String name);

    List<Room> findAll(int page, String sort, String order);

    List<Room> findAllByTag(String tag, int page, String sort, String order);

    void deleteRoom(String roomId);

    void deleteRoomByOwner(String roomId, Principal principal);

    void addUserCount(String roomId, String username);

    void decreaseUserCount(String roomId, String username);

}