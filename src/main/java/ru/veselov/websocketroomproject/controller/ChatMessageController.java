package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;
import ru.veselov.websocketroomproject.mapper.ChatMessageMapper;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {

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


    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

    private SendChatMessage createSendChatMessage(ReceivedChatMessage receivedChatMessage, String username) {
        SendChatMessage sendChatMessage = chatMessageMapper.toSendChatMessage(receivedChatMessage);
        sendChatMessage.setSentFrom(username);
        return sendChatMessage;
    }

}