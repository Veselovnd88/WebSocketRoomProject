package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.Room;

import java.security.Principal;
import java.util.List;

public interface RoomService {

    Room createRoom(Room room);

    Room getRoomById(String id);

    Room getRoomByName(String name);

    Room changeOwner(Room room, Principal principal);

    Room changeStatus(Room room, Principal principal);

    List<Room> getAllRooms();

}