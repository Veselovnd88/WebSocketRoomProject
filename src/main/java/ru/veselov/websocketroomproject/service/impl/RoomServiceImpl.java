package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.NotCorrectTokenException;
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
            roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
        }
        RoomEntity saved = roomRepository.save(roomEntity);
        log.info("[Saved room {}]", saved);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public Room getRoomById(String id, String token) {
        RoomEntity roomEntity = findRoomById(id);
        log.info("Retrieving [room {}] from repo", id);
        if (roomEntity.getIsPrivate()) {
            validateToken(roomEntity, token);
        }
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
        RoomEntity roomEntity = findRoomById(roomId);
        validateOwner(principal, roomEntity);
        if (settingsDTO.getOwnerName() != null) {
            roomEntity.setOwnerName(settingsDTO.getOwnerName());
        }
        if (settingsDTO.getIsPrivate() != null) {
            roomEntity.setIsPrivate(settingsDTO.getIsPrivate());
        }
        if (settingsDTO.getPlayerType() != null) {
            //playerService.configurePlayer();
        }
        if (settingsDTO.getToken() != null) {
            roomEntity.setRoomToken(settingsDTO.getToken());
        }
        roomEntity.setChangedAt(ZonedDateTime.now(ZoneId.of(zoneId)));
        RoomEntity saved = roomRepository.save(roomEntity);
        log.info("[Room {}] settings changed", roomId);
        return roomMapper.entityToRoom(saved);
    }

    @Override
    public List<Room> getAllPublicRooms() {
        return roomMapper.entitiesToRooms(roomRepository.findAll());
    }

    @Override
    @Transactional
    public void addUrl(String roomId, String url, Principal principal) {
        RoomEntity roomEntity = findRoomById(roomId);
        roomEntity.setActiveUrl(url);
        roomEntity.addUrl(url);
        roomRepository.save(roomEntity);
    }

    private RoomEntity findRoomById(String id) {
        UUID uuid = UUID.fromString(id);
        Optional<RoomEntity> foundRoom = roomRepository.findById(uuid);
        return foundRoom.orElseThrow(
                () -> {
                    log.warn("No room found with [id={}]", id);
                    throw new RoomNotFoundException(String.format("No room found with id [%s]", id));
                }
        );
    }

    private void validateOwner(Principal principal, RoomEntity roomEntity) {
        if (!StringUtils.equals(principal.getName(), roomEntity.getOwnerName())) {
            log.warn("Only owner can assign new settings or set Url, [{}] is not owner", principal.getName());
            throw new NotCorrectOwnerException("Only owner can assign new settings or set URL");
        }
    }

    private void validateToken(RoomEntity roomEntity, String token) {
        if (!StringUtils.equals(roomEntity.getRoomToken(), token)) {
            log.warn("Not correct [token: {}]", token);
            throw new NotCorrectTokenException(String.format("Not correct token %s for access to private room", token));
        }
    }

}
