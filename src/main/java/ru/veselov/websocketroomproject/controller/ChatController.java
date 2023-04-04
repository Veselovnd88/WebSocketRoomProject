package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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

    @MessageMapping("/chat/{id}")
    public void processMessage(@DestinationVariable("id") String roomId,
                               @Payload ChatMessage chatMessage) {
        log.info("Message received {}", chatMessage);
        System.out.println(roomId);

        simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                chatMessage
        );
    }

    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

}
