package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;

import java.time.ZonedDateTime;
import java.util.Collections;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {

    @Value("${socket.chat-topic}")
    private String chatDestination;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{id}")
    public void processTextMessage(@DestinationVariable("id") String roomId,
                                   @Payload ReceivedChatMessage receivedChatMessage, Authentication authentication) {
        log.trace("Message received {}", receivedChatMessage);
        String username = authentication.getName();
        simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                createSendChatMessage(receivedChatMessage, username)
        );
        log.trace("Message sent to {}", toDestination(roomId));
    }

    @MessageMapping("/chat/{id}/binary")
    public void processBinaryMessage(@DestinationVariable("id") String roomId, byte[] message) {

        log.info("Received binary content");
        simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                message
        );
        SendChatMessage sendChatMessage = new SendChatMessage();
        sendChatMessage.setContent("Sent file");
        sendChatMessage.setSentFrom("sender");
        sendChatMessage.setSentTime(ZonedDateTime.now());

        /*simpMessagingTemplate.convertAndSend(
                toDestination(roomId),
                sendChatMessage
        );*/

        log.info("Sent binary content");
    }


    private String toDestination(String roomId) {
        return chatDestination + "/" + roomId;
    }

    private SendChatMessage createSendChatMessage(ReceivedChatMessage receivedChatMessage, String username) {
        SendChatMessage sendChatMessage = new SendChatMessage();
        sendChatMessage.setSentTime(ZonedDateTime.now());
        sendChatMessage.setContent(receivedChatMessage.getContent());
        if (receivedChatMessage.getSentFrom() == null) {
            sendChatMessage.setSentFrom(username);
        } else {
            sendChatMessage.setSentFrom(receivedChatMessage.getSentFrom());
        }
        return sendChatMessage;
    }

}