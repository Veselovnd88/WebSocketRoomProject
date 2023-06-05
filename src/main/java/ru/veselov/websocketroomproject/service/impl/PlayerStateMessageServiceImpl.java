package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.request.PlayerStateDTO;
import ru.veselov.websocketroomproject.service.PlayerStateMessageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerStateMessageServiceImpl implements PlayerStateMessageService {

    @Value("${socket.youtube-topic}")
    private String youtubeTopic;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendToTopic(String roomId, PlayerStateDTO message) {
        simpMessagingTemplate.convertAndSend(toDestination(roomId), message);
    }

    private String toDestination(String roomId) {
        return youtubeTopic + "/" + roomId;
    }
}
