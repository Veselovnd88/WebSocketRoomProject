package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import ru.veselov.websocketroomproject.dto.PlayerStateDTO;
import ru.veselov.websocketroomproject.service.PlayerStateMessageService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class YouTubePlayerController {

    private final PlayerStateMessageService playerStateMessageService;

    /**
     * Handling YouTubePlayer states, only room owner, or responsible user should send messages here,
     * this state will be broadcasted to all clients and set their players:
     * Play/Paused, PlayingTime, Quality, Rate will be synchronized based on Owner's player
     */

    @MessageMapping("/youtube/{roomId}")
    public void manageYouTubePlayerState(@DestinationVariable("roomId") String roomId,
                                         @Payload PlayerStateDTO message) {
        playerStateMessageService.sendToTopic(roomId, message);
    }

}