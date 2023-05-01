package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.Room;

import java.util.UUID;

public interface RoomService {

    Room createRoom(Room room);

    Room getRoom(UUID uuid);

    Room changeOwner(UUID uuid, String currentOwner, String newOwnerName);

    Room changeStatus(UUID uuid, String ownerName, boolean isPrivate);

}