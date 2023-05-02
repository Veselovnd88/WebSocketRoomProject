package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    Room createRoom(Room room);

    Room getRoomById(UUID id);

    Room getRoomByName(String name);

    Room changeOwner(Room room, String newOwnerName);

    Room changeStatus(Room room, boolean isPrivate);

    List<Room> getAllRooms();

}