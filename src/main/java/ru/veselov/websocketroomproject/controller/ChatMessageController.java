package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import ru.veselov.websocketroomproject.dto.ChatMessage;

import java.time.ZonedDateTime;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {

    @Value("${socket.chat-topic}")
    private String chatDestination;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{id}")
    public void processMessage(@DestinationVariable("id") String roomId,
                               @Payload ChatMessage chatMessage, Authentication authentication) {
        log.info("Message received {}", chatMessage);
        chatMessage.setSent(ZonedDateTime.now());
        String username = authentication.getName();
        if (chatMessage.getSentFrom() == null) {
            chatMessage.setSentFrom(username);
        }
        simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                chatMessage
        );
    }

    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

}