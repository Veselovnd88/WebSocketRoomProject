package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.service.RoomSettingsService;
import ru.veselov.websocketroomproject.validation.RoomValidator;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomSettingsServiceImpl implements RoomSettingsService {

    @Value("${server.zoneId}")
    private String zoneId;

    private final RoomValidator roomValidator;

    @Override
    public RoomEntity applySettings(RoomEntity roomEntity, RoomSettingsDTO settingsDTO) {
        if (settingsDTO.getRoomName() != null) {
            roomValidator.validateRoomName(settingsDTO.getRoomName());
            roomEntity.setName(settingsDTO.getRoomName());
        }
        if (settingsDTO.getOwnerName() != null) {
            roomEntity.setOwnerName(settingsDTO.getOwnerName());
        }
        if (settingsDTO.getIsPrivate() != null) {
            Boolean isPrivate = settingsDTO.getIsPrivate();
            if (isPrivate) {
                roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
            } else {
                roomEntity.setRoomToken(null);
            }
            roomEntity.setIsPrivate(isPrivate);
        }
        if (settingsDTO.getPlayerType() != null) {
            roomEntity.setPlayerType(settingsDTO.getPlayerType());
        }
        if (settingsDTO.getChangeToken() != null && settingsDTO.getChangeToken() && roomEntity.getIsPrivate()) {
            roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
        }
        roomEntity.setChangedAt(ZonedDateTime.now(ZoneId.of(zoneId)));
        return roomEntity;
    }

}
