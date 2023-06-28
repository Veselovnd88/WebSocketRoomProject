package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.model.Room;

import java.security.Principal;

public interface RoomSettingsService {

    Room changeSettings(String roomId, RoomSettingsDTO settings, Principal principal);

    void addUrl(String roomId, String url, Principal principal);

}
