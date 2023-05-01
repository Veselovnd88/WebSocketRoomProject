package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.RoomCreationDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final String URL = "/api/room/";

    private final RoomMapper roomMapper;

    private final RoomRepository roomRepository;

    @Override
    public Room createRoom(Room room) {
        RoomEntity roomEntity = roomMapper.dtoToRoomEntity(room);
        roomEntity.setSourceUrl(URL);
        log.warn("ROom [{}]", roomEntity);
        if (roomEntity.getIsPrivate()) {
            roomEntity.setRoomToken(UUID.randomUUID().toString());
        }
        RoomEntity saved = roomRepository.save(roomEntity);
        log.warn("[Saved room {}]", saved);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public Room getRoom(UUID uuid) {
        return null;
    }

    @Override
    public Room changeOwner(UUID uuid, String currentOwner, String newOwnerName) {
        return null;
    }

    @Override
    public Room changeStatus(UUID uuid, String ownerName, boolean isPrivate) {
        return null;
    }
}
