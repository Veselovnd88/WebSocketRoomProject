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
import ru.veselov.websocketroomproject.event.handler.RoomUpdateHandler;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.mapper.RoomMapper;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.RoomSettingsService;
import ru.veselov.websocketroomproject.validation.RoomValidator;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomSettingsServiceImpl implements RoomSettingsService {

    @Value("${server.zoneId}")
    private String zoneId;

    private final RoomValidator roomValidator;

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    private final RoomUpdateHandler roomUpdateHandler;

    @Override
    @Transactional
    public Room changeSettings(String roomId, RoomSettingsDTO settingsDTO, Principal principal) {
        RoomEntity roomEntity = findRoomById(roomId);
        roomValidator.validateOwner(principal, roomEntity);
        RoomEntity changedRoomEntity = applySettings(roomEntity, settingsDTO);
        RoomEntity saved = roomRepository.save(changedRoomEntity);
        log.info("[Room's {}] settings changed", roomId);
        Room room = roomMapper.entityToRoom(saved);
        roomUpdateHandler.handleRoomSettingUpdateEvent(room);
        return room;
    }

    @Override
    @Transactional
    public void addUrl(String roomId, String url, Principal principal) {
        RoomEntity roomEntity = findRoomById(roomId);
        roomEntity.setActiveUrl(url);
        UrlEntity urlEntity = new UrlEntity(url, ZonedDateTime.now(ZoneId.of(zoneId)));
        roomEntity.addUrl(urlEntity);
        roomRepository.save(roomEntity);
        log.info("New [active url {}] added to [room {}]", url, roomId);
        roomUpdateHandler.handleActiveURLUpdateEvent(roomId, url);
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

    private RoomEntity applySettings(RoomEntity roomEntity, RoomSettingsDTO settingsDTO) {
        if (settingsDTO.getRoomName() != null) {
            roomValidator.validateRoomName(settingsDTO.getRoomName());
            roomEntity.setName(settingsDTO.getRoomName());
        }
        if (settingsDTO.getOwnerName() != null) {
            roomEntity.setOwnerName(settingsDTO.getOwnerName());
        }
        if (settingsDTO.getIsPrivate() != null) {
            Boolean isPrivate = settingsDTO.getIsPrivate();
            if (Boolean.TRUE.equals(isPrivate)) {
                roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
            } else {
                roomEntity.setRoomToken(null);
            }
            roomEntity.setIsPrivate(isPrivate);
        }
        if (settingsDTO.getPlayerType() != null) {
            roomEntity.setPlayerType(settingsDTO.getPlayerType());
        }
        if (settingsDTO.getChangeToken() != null
                && settingsDTO.getChangeToken()
                && Boolean.TRUE.equals(roomEntity.getIsPrivate())) {
            roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
        }
        roomEntity.setChangedAt(ZonedDateTime.now(ZoneId.of(zoneId)));
        return roomEntity;
    }

}
