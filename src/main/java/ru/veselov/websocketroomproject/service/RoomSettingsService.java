package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;

public interface RoomSettingsService {

    RoomEntity applySettings(RoomEntity roomEntity, RoomSettingsDTO roomSettingsDTO);

}
