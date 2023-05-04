package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;

public interface RoomSettingsService {

    RoomEntity applySettings(RoomEntity roomEntity, RoomSettingsDTO roomSettingsDTO);

}
