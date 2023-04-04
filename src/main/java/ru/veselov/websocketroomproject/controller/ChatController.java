package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import ru.veselov.websocketroomproject.dto.ChatMessage;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    @Value("${socket.chat-topic}")
    private String chatDestination;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/app/chat/{id}")
    public void processMessage(
            @Payload ChatMessage chatMessage, @PathVariable("id") String roomId) {
        log.info("Message received");
    }

    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

}
