package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.PlayerStateDTO;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class YouTubeVideoController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/youtube/{roomId}")
    public void manageYouTubePlayerState(@DestinationVariable("roomId") String roomId,
                                         @Payload PlayerStateDTO playerStateDTO,
                                         Principal principal) {
        log.info("Received YTPlayer state {} of room {}", playerStateDTO, roomId);
        simpMessagingTemplate.convertAndSend("/topic/youtube/" + roomId, playerStateDTO);
    }

    @GetMapping(value = "/room/{roomId}")
    public void getVideos(@PathVariable String roomId, @RequestParam(value = "scroll", required = false) String scroll) {
        log.info("Paused");
        log.info("scrolled to: {}", scroll);

    }
}