package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "local", name = "roomServiceStub", havingValue = "enabled")
@RequiredArgsConstructor
public class RoomServiceImplStub implements RoomService {

    private final HashMap<UUID, RoomEntity> roomRepository;

    private static final String URL = "/api/room/";

    private final RoomMapper roomMapper;


    @Override
    public Room createRoom(Room room) {
        UUID uuid = UUID.randomUUID();
        RoomEntity roomEntity = roomMapper.dtoToRoomEntity(room);
        roomEntity.setId(uuid);
        roomEntity.setSourceUrl(URL + uuid);
        roomEntity.setRoomToken("secretToken");
        roomRepository.put(roomEntity.getId(), roomEntity);
        roomMapper.entityToRoom(roomEntity);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    public Room getRoom(UUID uuid) {
        return roomMapper.entityToRoom(roomRepository.get(uuid));
    }

    @Override
    public Room changeOwner(UUID uuid, String currentOwner, String newOwnerName) {
        RoomEntity room = roomRepository.get(uuid);
        validateOwner(room.getOwnerName(), currentOwner);
        room.setOwnerName(newOwnerName);
        roomRepository.put(uuid, room);
        return roomMapper.entityToRoom(room);
    }

    @Override
    public Room changeStatus(UUID uuid, String ownerName, boolean isPrivate) {
        RoomEntity room = roomRepository.get(uuid);
        validateOwner(room.getOwnerName(), ownerName);
        room.setIsPrivate(isPrivate);
        roomRepository.put(uuid, room);
        return roomMapper.entityToRoom(room);
    }

    private void validateOwner(String ownerName, String username) {
        if (!ownerName.equals(username)) {
            throw new RuntimeException("You are not owner");
        }
    }
}