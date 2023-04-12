package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.mapper.ChatMessageMapper;

import java.security.Principal;
import java.time.ZonedDateTime;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {
    @Value("${socket.server-name}")
    private String serverName;

    @Value("${socket.chat-topic}")
    private String chatDestination;

    @Value("${socket.private-message-topic}")
    private String privateMessageDestination;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatMessageMapper chatMessageMapper;

    @MessageMapping("/chat/{id}")
    public void processTextMessage(@DestinationVariable("id") String roomId,
                                   @Payload ReceivedChatMessage receivedChatMessage,
                                   Principal principal) {
        log.info("Message received {}", receivedChatMessage);
        String username = principal.getName();
        simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                createSendChatMessage(receivedChatMessage, username)
        );
    }

    @MessageMapping("/chat-private")
    public void processTextMessageToUser(@Payload ReceivedChatMessage receivedChatMessage,
                                         Principal principal) {
        String sendTo = receivedChatMessage.getSendTo();
        log.info("Received private message to {}", sendTo);
        String username = principal.getName();
        simpMessagingTemplate.convertAndSendToUser(sendTo,
                privateMessageDestination,
                createSendChatMessage(receivedChatMessage, username)
        );
    }

    @MessageExceptionHandler
    public void handleException(Exception exception, Principal principal) {
        log.warn("Caught exception: {}", exception.getMessage());
        String username = principal.getName();
        SendChatMessage sendChatMessage = new SendChatMessage();
        sendChatMessage.setSentFrom(serverName);
        sendChatMessage.setContent("Error occurred, please reconnect: " + exception.getMessage());
        sendChatMessage.setSentTime(ZonedDateTime.now());
        simpMessagingTemplate.convertAndSendToUser(username,
                privateMessageDestination,
                sendChatMessage
        );
    }

    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

    private SendChatMessage createSendChatMessage(ReceivedChatMessage receivedChatMessage, String username) {
        SendChatMessage sendChatMessage = chatMessageMapper.toSendChatMessage(receivedChatMessage);
        sendChatMessage.setSentFrom(username);
        return sendChatMessage;
    }

}