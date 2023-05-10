package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.UrlEntity;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.RoomSettingsService;
import ru.veselov.websocketroomproject.service.RoomValidator;

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

    private final RoomSettingsService roomSettingsService;

    private final RoomValidator roomValidator;

    @Override
    @Transactional
    public Room createRoom(Room room) {
        String name = room.getName();
        roomValidator.validateRoomName(name);
        RoomEntity roomEntity = roomMapper.toEntity(room);
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
        if (roomEntity.getIsPrivate()) {
            roomValidator.validateToken(roomEntity, token);
        }
        log.info("Retrieving [room {}] from repo", id);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    public Room getRoomByName(String name) {
        Optional<RoomEntity> foundRoom = roomRepository.findByName(name);
        RoomEntity roomEntity = foundRoom.orElseThrow(
                () -> {
                    log.error("No room found with [name={}]", name);
                    throw new RoomNotFoundException(String.format("No room found with name [%s]", name));
                }
        );
        log.info("Retrieving [room {}] from repo", name);
        return roomMapper.entityToRoom(roomEntity);
    }

    @Override
    @Transactional
    public Room changeSettings(String roomId, RoomSettingsDTO settingsDTO, Principal principal) {
        RoomEntity roomEntity = findRoomById(roomId);
        roomValidator.validateOwner(principal, roomEntity);
        RoomEntity changedRoomEntity = roomSettingsService.applySettings(roomEntity, settingsDTO);
        RoomEntity saved = roomRepository.save(changedRoomEntity);
        log.info("[Room's {}] settings changed", roomId);
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
        UrlEntity urlEntity = new UrlEntity(url, ZonedDateTime.now(ZoneId.of(zoneId)));
        roomEntity.addUrl(urlEntity);
        roomRepository.save(roomEntity);
        log.info("New [url {}] added to [room {}]", url, roomId);
    }

    private RoomEntity findRoomById(String id) {
        UUID uuid = UUID.fromString(id);
        Optional<RoomEntity> foundRoom = roomRepository.findById(uuid);
        return foundRoom.orElseThrow(
                () -> {
                    log.error("No room found with [id={}]", id);
                    throw new RoomNotFoundException(String.format("No room found with id [%s]", id));
                }
        );
    }

}