package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.RoomAlreadyExistsException;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Value("${server.zoneId}")
    private String zoneId;

    private static final String URL = "/api/room/";

    private final RoomMapper roomMapper;

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public Room createRoom(Room room) {
        String name = room.getName();
        Optional<RoomEntity> byName = roomRepository.findByName(name);
        if (byName.isPresent()) {
            log.warn("Room with [name {}] already exists", name);
            throw new RoomAlreadyExistsException(String.format("Room with such name [%s] already exists", name));
        }
        RoomEntity roomEntity = roomMapper.dtoToRoomEntity(room);
        roomEntity.setCreatedAt(ZonedDateTime.now(ZoneId.of(zoneId)));
        if (roomEntity.getIsPrivate()) {
            roomEntity.setRoomToken(UUID.randomUUID().toString());
        }
        RoomEntity saved = roomRepository.save(roomEntity);
        log.info("[Saved room {}]", saved);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public Room getRoomById(String id) {
        UUID uuid = UUID.fromString(id);
        RoomEntity roomEntity = findRoomById(uuid);
        log.info("Retrieving [room {}] from repo", id);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    public Room getRoomByName(String name) {
        Optional<RoomEntity> foundRoom = roomRepository.findByName(name);
        log.info("Retrieving [room {}] from repo", name);
        RoomEntity roomEntity = foundRoom.orElseThrow(
                () -> {
                    log.warn("No room found with [name={}]", name);
                    throw new RoomNotFoundException(String.format("No room found with name [%s]", name));
                }
        );
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    @Transactional
    public Room changeSettings(String roomId, RoomSettingsDTO settingsDTO, Principal principal) {
        UUID uuid = UUID.fromString(roomId);
        RoomEntity roomEntity = findRoomById(uuid);
        if (roomEntity.getOwnerName().equals(principal.getName())) {
            roomEntity.setOwnerName(settingsDTO.getOwnerName());
            roomEntity.setIsPrivate(settingsDTO.getIsPrivate());
            roomEntity.setChangedAt(ZonedDateTime.now(ZoneId.of(zoneId)));
            RoomEntity saved = roomRepository.save(roomEntity);
            log.info("[Room {}] settings changed", roomId);
            return roomMapper.entityToRoom(saved);
        } else {
            log.warn("Only owner can assign new settings, [{}] is not owner", settingsDTO.getOwnerName());
            throw new NotCorrectOwnerException("Only owner can assign new settings");
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return null;
    }

    private RoomEntity findRoomById(UUID id) {
        Optional<RoomEntity> foundRoom = roomRepository.findById(id);
        return foundRoom.orElseThrow(
                () -> {
                    log.warn("No room found with [id={}]", id);
                    throw new RoomNotFoundException(String.format("No room found with id [%s]", id));
                }
        );
    }

}
