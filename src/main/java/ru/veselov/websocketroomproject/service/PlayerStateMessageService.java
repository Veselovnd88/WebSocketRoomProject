package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.dto.PlayerStateDTO;

public interface PlayerStateMessageService {

    void sendToTopic(String roomId, PlayerStateDTO message);
}
