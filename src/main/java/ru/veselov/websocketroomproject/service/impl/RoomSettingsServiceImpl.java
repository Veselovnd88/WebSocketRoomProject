package ru.veselov.websocketroomproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.service.RoomSettingsService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RoomSettingsServiceImpl implements RoomSettingsService {

    private static final Map<String, PlayerType> videoPlayers = new HashMap<>();

    @Value("${server.zoneId}")
    private String zoneId;

    public RoomSettingsServiceImpl() {
        for (PlayerType p : PlayerType.values()) {
            videoPlayers.put(p.name(), p);
        }
    }

    @Override
    public RoomEntity applySettings(RoomEntity roomEntity, RoomSettingsDTO settingsDTO) {
        if (settingsDTO.getOwnerName() != null) {
            roomEntity.setOwnerName(settingsDTO.getOwnerName());
        }
        if (settingsDTO.getRoomName() != null) {
            roomEntity.setName(settingsDTO.getRoomName());
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
            roomEntity.setPlayerType(getPlayer(settingsDTO));
        }
        if (settingsDTO.getChangeToken() != null && settingsDTO.getChangeToken()) {
            roomEntity.setRoomToken(RandomStringUtils.randomAlphanumeric(10));
        }
        roomEntity.setChangedAt(ZonedDateTime.now(ZoneId.of(zoneId)));
        return roomEntity;
    }

    private PlayerType getPlayer(RoomSettingsDTO settingsDTO) {
        return videoPlayers.getOrDefault(settingsDTO.getPlayerType(), PlayerType.YOUTUBE);
    }

}