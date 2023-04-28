package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "local", name = "roomServiceStub", havingValue = "enabled")
@RequiredArgsConstructor
public class RoomServiceImplStub implements RoomService {

    private final HashMap<UUID, Room> roomRepository;

    private static final String URL = "/api/room/";

    @Override
    public Room createRoom(String roomName, String ownerName, boolean isPrivate) {
        UUID uuid = UUID.randomUUID();
        Room room = new Room(
                uuid,
                roomName,
                isPrivate,
                URL + uuid,
                "sercretToken",
                ownerName
        );
        roomRepository.put(uuid, room);
        return room;
    }

    @Override
    public Room getRoom(UUID uuid) {
        return roomRepository.get(uuid);
    }

    @Override
    public Room changeOwner(UUID uuid, String currentOwner, String newOwnerName) {
        Room room = roomRepository.get(uuid);
        validateOwner(room.getOwnerName(), currentOwner);
        room.setOwnerName(newOwnerName);
        roomRepository.put(uuid, room);
        return room;
    }

    @Override
    public Room changeStatus(UUID uuid, String ownerName, boolean isPrivate) {
        Room room = roomRepository.get(uuid);
        validateOwner(room.getOwnerName(), ownerName);
        room.setPrivate(isPrivate);
        roomRepository.put(uuid, room);
        return room;
    }

    private void validateOwner(String ownerName, String username) {
        if (!ownerName.equals(username)) {
            throw new RuntimeException("You are not owner");
        }
    }
}
